import { AcGameObject } from "./AcGameObject";
import { Cell } from "./Cell";


export class Snake extends AcGameObject {
    constructor(info, gamemap) {
        super();

        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap;

        this.cells = [new Cell(info.r, info.c)]; // 存放蛇的身体, cells[0]存放蛇头
        this.next_cell = null; // 下一步的目的地

        this.speed = 5; // 蛇每秒走5个格子
        this.direction = -1; // -1 表示没有指令, 0, 1, 2, 3 分别表示 上、右、下、左
        this.status = "idle"; // idle 表示静止, move 表示正在移动, die 表示死亡

        this.dr = [-1, 0, 1, 0]; // 四个方向行的偏移量
        this.dc = [0, 1, 0, -1]; // 四个方向列的偏移量

        this.step = 0;
        this.eps = 1e-2; //距离误差，距离小于这个值即可认为两点重合
    }

    start() {

    }
    set_direction(d) {
        this.direction = d;
    }

    check_tail_increasing() { // 检测当前回合蛇的长度是否要增加 => 蛇尾要变长,则蛇尾不动即可。如果不变长，需要跟着蛇头动
        if (this.step <= 10) return true;
        if (this.step % 3 === 1) return true;
        return false;
    }

    next_step() { // 将蛇的状态变为走下一步
        const d = this.direction;
        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);
        this.direction = -1; // 清空方向
        this.status = "move";
        this.step++;


        const k = this.cells.length;
        for (let i = k; i > 0; i--) {
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1])); // 必须深层复制
        }
    }

    update_move() {
        //this.cells[0].x += this.speed * this.timedelta / 1000;
        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        const distance = Math.sqrt(dx * dx + dy * dy);
        if (distance < this.eps) {
            this.cells[0] = this.next_cell; // 添加一个新蛇头
            this.next_cell = null;
            this.status = "idle"; // 走完了

            // 判断一下蛇没有超过10的长度就砍掉蛇尾
            if (!this.check_tail_increasing()) {
                this.cells.pop();
            }
        } else {
            const move_distance = this.speed * this.timedelta / 1000; // 每帧走过的距离
            this.cells[0].x += move_distance * dx / distance;
            this.cells[0].y += move_distance * dy / distance;
            // 蛇尾移动
            if (!this.check_tail_increasing()) {
                const k = this.cells.length;
                const tail = this.cells[k - 1],
                    tail_target = this.cells[k - 2];
                const tail_dx = tail_target.x - tail.x;
                const tail_dy = tail_target.y - tail.y;
                tail.x += move_distance * tail_dx / distance;
                tail.y += move_distance * tail_dy / distance;
            }
        }
    }

    update() { // 每一帧执行一次
        if (this.status === "move")
            this.update_move();
        this.render();
    }

    render() {
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;
        for (const cell of this.cells) {
            ctx.beginPath(); // 画圆
            ctx.arc(cell.x * L, cell.y * L, L / 2 * 0.8, 0, Math.PI * 2); // (x, y)坐标，半径，起始角度和终止角度
            ctx.fill();
        }

        for (let i = 1; i < this.cells.length; i++) { // 优化蛇的形状, 相邻两个球之间画一个矩形
            const a = this.cells[i - 1],
                b = this.cells[i];
            if (Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps) continue;
            if (Math.abs(a.x - b.x) < this.eps) { // 竖方向相邻的情况
                ctx.fillRect((a.x - 0.4) * L, Math.min(a.y, b.y) * L, L * 0.8, Math.abs(a.y - b.y) * L);
            } else {
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.4) * L, Math.abs(a.x - b.x) * L, L * 0.8);
            }
        }
    }
}