<template>
    <div class="result-board">
        <div class="result-board-text" v-if="$store.state.pk.loser === 'all'">
            Draw
        </div>
        <div class="result-board-text"
            v-else-if="$store.state.pk.loser === 'A' && $store.state.pk.a_id == $store.state.user.id">
            <!-- 用==, 因为类型不一样, 或者用 parseInt -->
            Lose
        </div>
        <div class="result-board-text"
            v-else-if="$store.state.pk.loser === 'B' && $store.state.pk.b_id === parseInt($store.state.user.id)">
            Lose
        </div>
        <div class="result-board-text" v-else>
            Win
        </div>
        <div class="result-board-btn">
            <button @click="restart" type="button" class="btn btn-warning btn-lg">
                Rematch!
            </button>
        </div>
    </div>
</template>

<script>
import { useStore } from 'vuex'
export default {
    setup() {
        const store = useStore();
        const restart = () => {
            store.commit("updateStatus", "matching");
            store.commit("updateLoser", "none");
            store.commit("updateOpponent", {
                opponent_username: "My Opponent",
                opponent_photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
            });
        }
        return {
            restart,
        }
    }
}
</script>

<style scoped>
.result-board {
    height: 30vh;
    width: 30vw;
    background-color: rgba(50, 50, 50, 0.5);
    position: absolute;
    top: 35vh;
    left: 35vw;
}
.result-board-text {
    text-align: center;
    color: white;
    font-size: 40px;
    font-weight: 600;
    font-style: italic;
    padding-top: 4vh;
}
.result-board-btn {
    text-align: center;
    padding-top: 5vh;
}
</style>