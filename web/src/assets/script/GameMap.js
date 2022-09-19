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

    }

    render() {
        this.ctx.fillStyle = 'green';
        this.ctx.fillRect(0, 0, this.ctx.canvas.width, this.ctx.canvas.height); //(0, 0): 起点坐标
    }
}