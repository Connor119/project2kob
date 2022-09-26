<template>
  <div class="container">
        <div class="row" style="margin-top: 20px">
            <div class="col-3">
                <div class="card">
                    <div class="card-body">
                        <img :src="$store.state.user.photo" style="width: 100%">
                    </div>
                </div>
            </div>
            <div class="col-9">
                <div class="card">
                    <div class="card-header">
                        <span style="font-size: 130%">My Bot</span>
                        <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal"
                            data-bs-target="#add-bot-button">Create Bot</button>
                        <!-- Modal -->
                        <div class="modal fade" id="add-bot-button" tabindex="-1">
                            <div class="modal-dialog modal-xl">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title" id="exampleModalLabel">Create Bot</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                            aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body">
                                        <div class="mb-3">
                                            <label for="add-bot-title" class="form-label">Title</label>
                                            <input v-model="botadd.title" type="text" class="form-control"
                                                id="add-bot-title" placeholder="Please enter the bot title">
                                        </div>
                                        <div class="mb-3">
                                            <label for="add-bot-description" class="form-label">Description</label>
                                            <textarea v-model="botadd.description" class="form-control"
                                                id="add-bot-description" rows="3"
                                                placeholder="Please enter the bot description"></textarea>
                                        </div>
                                        <div class="mb-3">
                                            <label for="add-bot-code" class="form-label">Code</label>
                                            <textarea v-model="botadd.content" class="form-control" id="add-bot-code"
                                                rows="7" placeholder="Please write the bot code"></textarea>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <div class="error-message">{{ botadd.error_message }}</div>
                                        <button type="button" class="btn btn-primary">Create</button>
                                        <button type="button" class="btn btn-secondary"
                                            data-bs-dismiss="modal">Cancel</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Create Time</th>
                                    <th>Operation</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="bot in bots" :key="bot.id">
                                    <td>{{ bot.title }}</td>
                                    <td>{{ bot.createtime }}</td>
                                    <td>
                                        <button type="button" class="btn btn-secondary"
                                            style="margin-right: 10px">Modify</button>
                                        <button type="button" class="btn btn-danger">Delete</button>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>

import { ref, reactive } from 'vue'
import { useStore } from 'vuex'
import $ from 'jquery'



export default {
    setup() {
        const store = useStore();
        let bots = ref([]);
        const botadd = reactive({
            title: "",
            description: "",
            content: "",
            error_message: "",
        });
        const refresh_bots = () => {
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/getlist/",
                type: "GET",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    bots.value = resp;
                }
            });
        }
        refresh_bots();
        return {
            bots,
            botadd,
            refresh_bots,
        }
    }
}
</script>

<style>

</style>