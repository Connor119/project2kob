
<!-- import PlayGround from '@/components/PlayGround.vue'; -->
<template>
    <!-- <div class="playground"></div> -->
    <div class="playground">
        <GameMap />
    </div>
</template>

<script>
import GameMap from "./GameMap.vue";
export default {
    components: {
        GameMap,
    }
}
</script>

<style scoped>
.playground {
    /* vw: 浏览器宽度，vh: 浏览器高度 */
    width: 60vw;
    height: 70vh;
    background: rgb(154, 217, 236);
    margin: 40px auto;
}
</style>