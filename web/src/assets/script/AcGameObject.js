//将所有的游戏对象都存起来
const AC_GAME_OBJECTS = []
    // 这个基类需要export出去
export class AcGameObject {
    // 类的构造函数
    constructor() {
            AC_GAME_OBJECTS.push(this);
            // 调用的时间间隔
            this.timedelta = 0;
            // 看是否有调用过
            this.has_called_start = false;

        }
        // 只执行1次
    start() {

        }
        // 每一帧执行一次，除了第一次之外
    update() {

        }
        // 删除之前执行的函数
    on_destroy() {

        }
        // 将当前对象从类数组中删除
    destroy() {
        this.on_destroy();
        for (let i in AC_GAME_OBJECTS) {
            const obj = AC_GAME_OBJECTS[i];
            if (obj === this) {
                AC_GAME_OBJECTS.splice(i);
                break;
            }
        }
    }
}
//实现每秒钟刷新60次


// 上一次执行的时刻
let last_timestamp;
// 递归调用自己来每秒钟执行n次
const step = timestamp => {
    for (let obj of AC_GAME_OBJECTS) {
        if (!obj.has_called_start) {
            obj.has_called_start = true;
            obj.start();
        } else {
            obj.timedelta = timestamp - last_timestamp;
            obj.update();
        }
    }
    last_timestamp = timestamp;
    requestAnimationFrame(step)
}

requestAnimationFrame(step)