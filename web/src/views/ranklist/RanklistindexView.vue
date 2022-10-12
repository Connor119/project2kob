<template>
  <ContentField>
        <table class="table table-hover" style="text-align: center">
            <thead>
                <tr>
                    <th>Player</th>
                    <th>Rating</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="user in users" :key="user.id">
                    <td>
                        <img :src="user.photo" alt="" class="record-user-photo">
                        &nbsp;
                        <span class="record-user-username">{{ user.username }}</span>
                    </td>
                    <td>
                        {{ user.rating }}
                    </td>
                </tr>
            </tbody>
        </table>

        <nav aria-label="...">
            <ul class="pagination" style="float:right">
                <li class="page-item" @click="click_page(-2)">
                    <a class="page-link">Previous</a>
                </li>
                <li :class="'page-item ' + page.is_active" v-for="page in pages" :key="page.number"
                    @click="click_page(page.number)">
                    <a class="page-link">{{ page.number }}</a>
                </li>
                <li :class="page-item" @click="click_page(-1)">
                    <a class="page-link">Next</a>
                </li>
            </ul>
        </nav>
    </ContentField>
</template>


<script>
import ContentField from '../../components/ContendField.vue'
import { useStore } from 'vuex'
import { ref } from 'vue'
import $ from 'jquery'
export default {
    components: {
        ContentField,
    },
    setup() {
        const store = useStore();
        let users = ref([]);
        let total_users = 0;
        let current_page = 1;
        let pages = ref([]);
        const click_page = (page_num) => {
            if (page_num === -2) page_num = current_page - 1;
            else if (page_num === -1) page_num = current_page + 1;
            let max_pages = parseInt(Math.ceil(total_users / 3));
            if (page_num >= 1 && page_num <= max_pages) {
                pull_page(page_num);
            }
        }
        const update_pages = () => {
            let max_pages = parseInt(Math.ceil(total_users / 3));
            let new_pages = [];
            for (let i = current_page - 2; i <= current_page + 2; i++) { // 一共显示五页
                if (i >= 1 && i <= max_pages) {
                    new_pages.push({
                        number: i,
                        is_active: i === current_page ? "active" : "",
                    });
                }
            }
            pages.value = new_pages;
        }
        const pull_page = (page_num) => {
            current_page = page_num;
            $.ajax({
                url: "http://127.0.0.1:3000/ranklist/getlist/",
                data: {
                    page_num,
                },
                type: "GET",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    // console.log(resp);
                    users.value = resp.users;
                    total_users = resp.users_count;
                    update_pages();
                },
                error(resp) {
                    console.log(resp);
                }
            });
        }
        pull_page(current_page);
        return {
            users,
            pages,
            total_users,
            click_page,
        }
    }
}
</script>


<style scoped>
.record-user-photo {
    width: 4vh;
    border-radius: 50%;
}
</style>