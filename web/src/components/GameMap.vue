<!-- 在playGround区域搞一个地图出来 -->
<template>
    <div ref="parent" class="gamemap">
        <canvas ref="canvas" tabindex="0"></canvas>
    </div>
</template>


<script>
    import { GameMap } from "@/assets/script/GameMap";
    import { ref, onMounted } from 'vue';//onMounted: 组件挂载完需要执行的操作
    import { useStore } from 'vuex'
    export default {
        setup() {
            let parent = ref(null);
            let canvas = ref(null);
            const store = useStore();
            onMounted(() => {// 匿名函数的优点: this 不会被重新绑定
            // new GameMap(canvas.value.getContext('2d'), parent.value);// mdn => canvas
            store.commit(
                "updateGameObject",
                new GameMap(canvas.value.getContext('2d'), parent.value, store)// mdn => canvas)
            );
        });
            return {
                parent,
                canvas,
            }
        },
    }
</script>
    

<style scoped>
    .gamemap {
        width: 100%;
        height: 100%;
        display: flex;
        /* background-color: #fff; */
        /* 水平居中 */
        justify-content: center;
        /* 竖直居中 */
        align-items: center;
    }
</style>