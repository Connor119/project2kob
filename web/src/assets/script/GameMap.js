//引入基类
import { AcGameObject } from "./AcGameObject";
import { Wall } from "./Wall";
import { Snake } from "./snake";

export class GameMap extends AcGameObject {
    constructor(ctx, parent) { //ctx: 画布， parent: 画布的父元素, 用来动态修改画布的长宽
        super();
        this.ctx = ctx;
        this.parent = parent;
        this.L = 0;
        // 初始化地图的大小
        this.rows = 13;
        this.cols = 14;
        // 初始化墙信息
        this.walls = [];
        this.inner_walls_count = 30;
        // 初始化蛇信息
        this.snakes = [
            new Snake({ id: 0, color: "#4876EC", r: this.rows - 2, c: 1 }, this),
            new Snake({ id: 1, color: "#F94848", r: 1, c: this.cols - 2 }, this),
        ];

    }

    start() {
        for (let i = 0; i < 1000; i++) {
            if (this.create_walls()) {
                break;
            }
        }
        this.add_listening_events();
    }

    check_valid(cell) { // 检测目标位置逻辑是否合法: 没有撞到蛇身或者障碍物
        for (const wall of this.walls) {
            if (wall.c === cell.c && wall.r === cell.r)
                return false;
        }
        for (const snake of this.snakes) { // 需要判断蛇尾是不是会缩，如果缩的话，蛇可以继续走
            let k = snake.cells.length;
            if (snake.check_tail_increasing()) { // 蛇尾会前进的时候，不判断蛇尾
                k--;
            }
            for (let i = 0; i < k; i++) { // 判定是否完全重合(撞上的话)
                if (snake.cells[i].r === cell.r && snake.cells[i].c === cell.c)
                    return false;
            }
        }
        return true;
    }

    add_listening_events() {
        this.ctx.canvas.focus(); //聚焦

        const [snake0, snake1] = this.snakes;
        this.ctx.canvas.addEventListener("keydown", e => {
            if (e.key === "w") snake0.set_direction(0);
            else if (e.key === "d") snake0.set_direction(1);
            else if (e.key === "s") snake0.set_direction(2);
            else if (e.key === "a") snake0.set_direction(3);
            else if (e.key === "ArrowUp") snake1.set_direction(0);
            else if (e.key === "ArrowRight") snake1.set_direction(1);
            else if (e.key === "ArrowDown") snake1.set_direction(2);
            else if (e.key === "ArrowLeft") snake1.set_direction(3);
        });
    }


    check_connectivity(g, sx, sy, tx, ty) { // source, target
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = true;

        let dx = [-1, 0, 1, 0],
            dy = [0, 1, 0, -1];
        for (let i = 0; i < 4; i++) {
            let x = sx + dx[i],
                y = sy + dy[i];
            if (!g[x][y] && this.check_connectivity(g, x, y, tx, ty))
                return true;
        }
        return false;
    }

    create_walls() {
        const g = []; //初始化二维数组
        for (let r = 0; r < this.rows; r++) {
            g[r] = [];
            for (let c = 0; c < this.cols; c++) {
                g[r][c] = false;
            }
        }

        //给四周加上墙
        for (let r = 0; r < this.rows; r++) { //左右两条竖边
            g[r][0] = g[r][this.cols - 1] = true;
        }
        for (let c = 0; c < this.cols; c++) { //上下两条横边
            g[0][c] = g[this.rows - 1][c] = true;
        }
        // 加随机障碍物
        for (let i = 0; i < this.inner_walls_count / 2; i++) {
            for (let j = 0; j < 1000; j++) { //随机1000次
                let r = parseInt(Math.random() * this.rows);
                let c = parseInt(Math.random() * this.cols);
                // if (g[r][c] || g[c][r]) continue;
                if (g[r][c] || g[this.rows - 1 - r][this.cols - 1 - c]) continue;
                if (r == this.rows - 2 && c == 1) continue;
                if (r == 1 && c == this.cols - 2) continue;
                // g[r][c] = g[c][r] = true;
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = true; //之后地图逻辑会被移动到后端，为了保证公平
                break;
            }
        }

        const copy_g = JSON.parse(JSON.stringify(g));
        if (!this.check_connectivity(copy_g, this.rows - 2, 1, 1, this.cols - 2)) {
            return false;
        }
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if (g[r][c]) {
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }


        return true;
    }
    update_size() {
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows))

        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }


    check_ready() { // 判断两条蛇是否准备好下一回合
        //条件: 两条蛇都处于静止, 且两条蛇都获取了下一步操作
        for (const snake of this.snakes) {
            if (snake.status !== "idle" || snake.direction === -1) {
                return false;
            }
            if (snake.direction === -1) {
                return false;
            }
        }
        return true;
    }

    next_step() {
        for (const snake of this.snakes) {
            snake.next_step();
        }
    }

    update() {
        this.update_size();
        if (this.check_ready()) {
            this.next_step();
        }
        this.render();

    }

    render() {
        const color_even = "#AAD751",
            color_odd = "#A2D149";
        for (let r = 0; r < this.rows; r++) { // canvas坐标系: 横着为 x, 竖着的为 y 
            for (let c = 0; c < this.cols; c++) {
                if ((r + c) % 2 == 0) {
                    this.ctx.fillStyle = color_even
                } else {
                    this.ctx.fillStyle = color_odd
                }
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L); // 前两个为起点坐标，后两个为边长
            }
        }
    }
}