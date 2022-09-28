<template>

    <playGround></playGround>


</template>

<script>
import playGround from "../../components/PlayGround.vue"
import { onMounted, onUnmounted } from 'vue';// 挂载: 打开该组件
import { useStore } from 'vuex'

export default {
    components:{
        playGround,
    },
    setup() {
        const store = useStore();
        const socketUrl = `ws://127.0.0.1:3000/websocket/${store.state.user.token}`;
        let socket = null;
        onMounted(() => {
            socket = new WebSocket(socketUrl);
            socket.onopen = () => {
                console.log("connected");
            }
            socket.onmessage = (msg) => {
                // Spring定义的传递回来的格式
                const data = JSON.parse(msg.data);
                console.log(data);
            }
            socket.onclose = () => {
                console.log("disconnected");
            }
        });
        onUnmounted(() => {
            socket.close();
        })
    }
}
</script>

<style scoped>

</style>