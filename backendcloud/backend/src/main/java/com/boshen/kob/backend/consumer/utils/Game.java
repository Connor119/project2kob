package com.boshen.kob.backend.consumer.utils;

import com.alibaba.fastjson2.JSONObject;
import com.boshen.kob.backend.consumer.WebSocketServer;
import com.boshen.kob.backend.mapper.RecordMapper;
import com.boshen.kob.backend.pojo.Record;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

//由于现在地图的功能是在前端实现的，所以当用户开始匹配的时候地图并不是同步的
//所以我们需要将地图放入云端（创建一个）
//根据刚才的分析，我们的game不能是单线程的而是需要多线程所以我们需要将这个Game（所谓的资源）变成多线程，可以直接继承一个Thread类
//继承一个Thread类之后我们需要写其中的Run方法来定义这个类运行起来之后需要如何
public class Game extends Thread{
    final private Integer rows;
    final private Integer cols;
    final private Integer inner_walls_count;
    final private int[][] g;
    final private static int[] dx = {-1, 0, 1, 0};
    final private static int[] dy = {0, 1, 0, -1};
//    存储每名玩家
    final private  Player playerA,playerB;
//    定义成员变量来控制两个玩家的下一步操作(A和B分别代表着两名玩家的下一步操作)
//    如果是Null的话就是没有获得下一步操作，0123分别代表上下左右（获取操作通过set来进行）
    private Integer nextStepA = null;
    private Integer nextStepB = null;
//    我们需要等待两个客户端分别去修改stepA和B，两个用户都需要去读我们的两个操作，这时候就会涉及到读写一致性问题，这里我们需要加锁
    private ReentrantLock lock = new ReentrantLock();
//    找一个状态变量来存我们的游戏状态playing 表示正在进行中
    private String status = "playing";
    private String loser = "";
    public static RecordMapper recordMapper;

//创建地图的时候我们需要的信息有，一个二维数组的空白地图和玩家信息
    public Game(Integer rows, Integer cols, Integer inner_walls_count, Integer idA, Integer idB) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
        this.playerA = new Player(idA,rows-2,1,new ArrayList<>());
        this.playerB = new Player(idB,1,cols-2,new ArrayList<>());
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper)  { WebSocketServer.recordMapper = recordMapper; }

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public int[][] getG() {
        return g;
    }

    public void setNextStepA(Integer nextStepA) {
//        由于这里有读写可能不一致的问题，所以只要需要对操作这两个变量的函数进行加锁
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }
    }

    private boolean check_connectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1;
        for (int i = 0; i < 4; i ++ ) {
            int x = sx + dx[i], y = sy + dy[i];
            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0) {
                if (check_connectivity(x, y, tx, ty)) {
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }
        g[sx][sy] = 0;
        return false;
    }


    private boolean draw() {// 画地图
        // 初始化Map
        for(int i = 0; i < this.rows; i ++) {
            for(int j = 0; j < this.cols; j ++) {
                g[i][j] = 0;// 0 表示空地， 1 表示墙
            }
        }

        // 四周添加墙
        for (int r = 0; r < this.rows; r ++ ) {
            g[r][0] = g[r][this.cols - 1] = 1;
        }
        for (int c = 0; c < this.cols; c ++ ) {
            g[0][c] = g[this.rows - 1][c] = 1;
        }


        // 随机生成障碍物
        Random random = new Random();
        for(int i = 0; i < this.inner_walls_count / 2; i ++) {
            for(int j = 0; j < 1000; j ++) {
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);
                if(g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1)
                    continue;
                if(r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2)
                    continue;
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }

        return check_connectivity(this.rows - 2, 1, 1,this.cols - 2);
    }

    public void createMap() {
        for(int i = 0; i < 1000; i ++) {
            if(draw()) {
                break;
            }
        }
    }


    private void judge(){
        /*
        * 判断两个玩家的操作是不是合法
        * */
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        boolean validA = check_valid(cellsA, cellsB);
        boolean validB = check_valid(cellsB, cellsA);
        if(!validA || !validB) {
            status = "finished";

            if(!validA && !validB) {
                loser = "all";
            } else if(!validA) {
                loser = "A";
            } else {
                loser = "B";
            }
        }


    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB) {
        int n = cellsA.size();
        Cell cell = cellsA.get(n - 1);
        if(g[cell.x][cell.y] == 1) return false;

        for(int i = 0; i < n - 1; i ++) {
            if(cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y) {
                return false;
            }
        }

        for(int i = 0; i < n - 1; i ++) {
            if(cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y) {
                return false;
            }
        }

        return true;
    }

    private void sendAllMessage(String message) {
        if(WebSocketServer.users.get(playerA.getId()) != null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        if(WebSocketServer.users.get(playerB.getId()) != null)
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }

    private void sendResult(){
        /*
         * 这个函数是用来向两个client公布结果的
         *
         * */
        JSONObject resp = new JSONObject();
//        event表示当前传播的信息类型是一个事件是一个动作
        resp.put("event", "result");
        resp.put("loser", loser);
        saveToDatabse();
        sendAllMessage(resp.toJSONString());
    }

    private void sendMove() {// 向两个 Client 传递移动信息
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            sendAllMessage(resp.toJSONString());
            nextStepA = nextStepB = null;// 还原
        } finally {
            lock.unlock();
        }
    }



//    当我们启动一个线程之后需要考虑这个线程接下来需要进行什么样的操作：等待下一步的操作
    @Override
    public void run() {
//一个线程也就是一个游戏，这个游戏不会超过1000步就结束，所以我们在这里写一个循环获取1000次下一步操作
        for (int i = 0; i < 1000; i++) {
            if(nextStep()){
                judge();
                if(status.equals("playing")){
//                    这说明我们经过超时校验和步骤合法性校验后玩家都符合要求游戏需要继续
//                    这时候需要将两名玩家的输入分别广播给两个人，这是因为对战的时候c1知道自己的操作而不知道对手的操作，这就需要服务器将对手的操作返回给c1，之后同步页面
                    sendMove();
                }else{
                    sendResult();
//                    结束游戏记得加入break
                }
            }else{
//                如果没有获取到下一步动作，那么游戏就结束了，要更改游戏的状态
                status = "finished";
//                在这里也需要加锁（如果我们使用最普通的课重入锁的话那么我们需要对有并发问题的变量的读写操作都加锁）
                lock.lock();
                try {
                    if(nextStepA == null && nextStepB == null){
    //                    如果两个蛇都死了，都没有获取到下一步操作，这个时候就是平局
                        loser = "all";
                    }else if(nextStepA == null){
                        loser = "A";
                    }else{
                        loser = "B";
                    }
                } finally {
                    lock.unlock();
                }
//                当上面的逻辑都跑完了，我们的游戏就也就结束了，结束之前需要给两个玩家发消息
                sendResult();
                break;
            }
        }


    }

    private String getMapString() {
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < rows; i ++) {
            for(int j = 0; j < cols; j ++) {
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }

    private void saveToDatabse() {
        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
//                这里我们的step在表中是一个String,所以需要在player类中写一个String的转换类
                playerA.getStepsString(),
                playerB.getStepsString(),
//                我们也需要将当前的地图也转化为一个String
                getMapString(),
                loser,
                new Date()
        );
        WebSocketServer.recordMapper.insert(record);
    }

    private boolean nextStep(){
        try {
//            这里是因为前端每秒走5个格子，如果两个人都输入比较快，那么之后的动作就会覆盖掉之前的动作，所以进来的时候先要停200ms
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        等待两名玩家的下一步操作
//        我们需要等待两名玩家都发出命令，如果哪个玩家5s还没有发出命令，需要对这个玩家返回输了的信息
        for (int i = 0; i < 500; i++) {
//            在这个循环里我们让玩家有5秒钟的输入时间，这里可以让线程每5s开启一次，读以西nextStepA和B，看是否有输入
            try {
                Thread.sleep(100);
//                等待5s之后我们需要将其加锁（由于我们需要对并发资源进行操作所以这里要加锁）
                lock.lock();
                try {
                    if(nextStepA !=null && nextStepB !=null){
//                        记录A玩家和B玩家
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
//                        说明我们已经读到了两名玩家的输入，直接返回读到了就可以了
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;

    }
}
