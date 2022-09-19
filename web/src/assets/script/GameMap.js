//引入基类
import { AcGameObject } from "./AcGameObject";


export class GameMap extends AcGameObject {
    constructor(ctx, parent) { //ctx: 画布， parent: 画布的父元素, 用来动态修改画布的长宽
        super();
        this.ctx = ctx;
        this.parent = parent;
        this.L = 0;

        this.rows = 13;
        this.cols = 13;
    }

    start() {


    }
    update_size() {
        this.L = Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows);
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