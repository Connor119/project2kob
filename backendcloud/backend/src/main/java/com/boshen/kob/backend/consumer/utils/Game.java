package com.boshen.kob.backend.consumer.utils;

import com.alibaba.fastjson2.JSONObject;
import com.boshen.kob.backend.consumer.WebSocketServer;
import com.boshen.kob.backend.mapper.RecordMapper;
import com.boshen.kob.backend.pojo.Bot;
import com.boshen.kob.backend.pojo.Record;
import com.boshen.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
    private final static String addBotUrl = "http://127.0.0.1:3007/bot/add/";

//创建地图的时候我们需要的信息有，一个二维数组的空白地图和玩家信息
    public Game(
            Integer rows,
            Integer cols,
            Integer inner_walls_count,
            Integer idA,
            Bot botA,
            Integer idB,
            Bot botB
    ) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];

        Integer botIdA = -1, botIdB = -1;
        String botCodeA = "", botCodeB = "";
        if(botA != null) {
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if(botB != null) {
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }

        this.playerA = new Player(idA, botIdA, botCodeA, rows - 2, 1, new ArrayList<>());
        this.playerB = new Player(idB, botIdB, botCodeB, 1, cols - 2, new ArrayList<>());
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
        for(int i = 0; i < 1000; i ++) {
            if(nextStep()) { // 是否获取两条蛇的下一步操作
                judge();
                if(status.equals("playing")) {
                    sendMove();
                } else {
                    sendResult();
                    break;
                }
            }
            else {
                status = "finished";
                lock.lock();
                try {
                    if(nextStepA == null && nextStepB == null) {
                        loser = "all";
                    } else if(nextStepA == null) {
                        loser = "A";
                    } else {
                        loser = "B";
                    }
                } finally {
                    lock.unlock();
                }
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

//    更新玩家的积分
    private void updateUserRating(Player player, Integer rating) {
        User user = WebSocketServer.userMapper.selectById(player.getId());
        user.setRating(rating);
        WebSocketServer.userMapper.updateById(user);
    }

    private void saveToDatabse() {
        /*
        * 更新操作包含了记录当前对局的情况以及更新用户的积分
        * */
        Integer ratingA = WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
        Integer ratingB = WebSocketServer.userMapper.selectById(playerB.getId()).getRating();

        if("A".equals(loser)) {
            ratingA += 5;
            ratingB -= 2;
        } else if("B".equals(loser)) {
            ratingA -= 2;
            ratingB += 5;
        }

        updateUserRating(playerA, ratingA);
        updateUserRating(playerB, ratingB);


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

    private String getInput(Player player) { // 将当前的局面信息编码成字符串
        Player me, you;
        if(playerA.getId().equals(player.getId())) {
            me = playerA;
            you = playerB;
        } else {
            me = playerB;
            you = playerA;
        }
        // 信息以 # 隔断
        return getMapString() + "#" +
                me.getSx() + "#" +
                me.getSy() + "#(" +
                me.getStepsString() + ")#" +
                you.getSx() + "#" +
                you.getSy() + "#(" +
                you.getStepsString() + ")";
    }

    private void sendBotCode(Player player) {
        if(player.getBotId().equals(-1)) return; // 不需要执行代码
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", player.getId().toString());
        data.add("bot_code", player.getBotCode());
        data.add("input", getInput(player));
        WebSocketServer.restTemplate.postForObject(addBotUrl, data, String.class);
    }

    private boolean nextStep(){
        try {
//            这里是因为前端每秒走5个格子，如果两个人都输入比较快，那么之后的动作就会覆盖掉之前的动作，所以进来的时候先要停200ms
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sendBotCode(playerA);
        sendBotCode(playerB);
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
