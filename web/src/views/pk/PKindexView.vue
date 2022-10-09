<template>
    <playGround v-if="$store.state.pk.status === 'playing'"></playGround>
    <MatchGround v-if="$store.state.pk.status === 'matching'"></MatchGround>
    <ResultBoard v-if="$store.state.pk.loser != 'none'"></ResultBoard>
</template>


<script>
import playGround from "../../components/PlayGround.vue"
import { onMounted, onUnmounted } from 'vue';// 挂载: 打开该组件
import MatchGround from '@/components/MatchGround.vue'
import { useStore } from 'vuex'
import ResultBoard from '@/components/ResultBoard.vue'

export default {
    components:{
        playGround,
        MatchGround,
        ResultBoard,
    },
    setup() {
        const store = useStore();
        const socketUrl = `ws://127.0.0.1:3000/websocket/${store.state.user.token}`;

        store.commit("updateLoser", "none");
        
        let socket = null;
        onMounted(() => {
            store.commit("updateOpponent", {
                opponent_username: "My Opponent",
                opponent_photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
            });
            socket = new WebSocket(socketUrl);
            socket.onopen = () => {
                console.log("connected");
                store.commit("updateSocket", socket);
            }
            socket.onmessage = (msg) => {
                // Spring定义的传递回来的格式
                const data = JSON.parse(msg.data);
                if (data.event === "start-matching") { // 表示匹配成功
                    store.commit("updateOpponent", {
                        opponent_username: data.opponent_username,
                        opponent_photo: data.opponent_photo,
                    });
                    setTimeout(() => {// 延迟，以看见对手信息
                        store.commit("updateStatus", "playing");
                    }, 200);
                    store.commit("updateGame", data.game);
                } else if (data.event === "move") {
                    console.log(data);
                    const game = store.state.pk.gameObject;
                    const [snake0, snake1] = game.snakes;
                    snake0.set_direction(data.a_direction);
                    snake1.set_direction(data.b_direction);
                } else if (data.event === "result") {
                    console.log(data);
                    const game = store.state.pk.gameObject;
                    const [snake0, snake1] = game.snakes;
                    if (data.loser === "all" || data.loser === "A") {
                        snake0.status = "die";
                    }
                    if (data.loser === "all" || data.loser === "B") {
                        snake1.status = "die";
                    }
                    store.commit("updateLoser", data.loser);
                }
            }
            socket.onclose = () => {
                console.log("disconnected");
            }
        });
        onUnmounted(() => {
            socket.close();
            store.commit("updateStatus","matching");
        })
    }
}
</script>

<style scoped>

</style>