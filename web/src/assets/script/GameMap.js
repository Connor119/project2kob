//引入基类
import { AcGameObject } from "./AcGameObject";
import { Wall } from "./Wall";

export class GameMap extends AcGameObject {
    constructor(ctx, parent) { //ctx: 画布， parent: 画布的父元素, 用来动态修改画布的长宽
        super();
        this.ctx = ctx;
        this.parent = parent;
        this.L = 0;

        this.rows = 13;
        this.cols = 13;
        this.walls = [];
    }

    start() {
        this.create_walls();

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

        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if (g[r][c]) {
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }
    }
    update_size() {
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows))

        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    update() {
        this.update_size();
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