<!-- 在playGround区域搞一个地图出来 -->
<template>
    <div ref="parent" class="gamemap">
        <canvas ref="canvas"></canvas>
    </div>
</template>


<script>
    import { GameMap } from "@/assets/script/GameMap";
    import { ref, onMounted } from 'vue';//onMounted: 组件挂载完需要执行的操作
    export default {
        setup() {
            let parent = ref(null);
            let canvas = ref(null);
            onMounted(() => {
                new GameMap(canvas.value.getContext('2d'), parent.value);// mdn => canvas
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
        /* 水平居中 */
        justify-content: center;
        /* 竖直居中 */
        align-items: center;
    }
</style>