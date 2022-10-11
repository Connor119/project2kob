<template>
    <ContentField>
        <table class="table table-hover" style="text-align: center">
            <thead>
                <tr>
                    <th>A</th>
                    <th>B</th>
                    <th>PK Result</th>
                    <th>PK Time</th>
                    <th>Operation</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="record in records" :key="record.record.id">
                    <td>
                        <img :src="record.a_photo" alt="" class="record-user-photo">
                        &nbsp;
                        <span class="record-user-username">{{ record.a_username }}</span>
                    </td>
                    <td>
                        <img :src="record.b_photo" alt="" class="record-user-photo">
                        &nbsp;
                        <span class="record-user-username">{{ record.b_username }}</span>
                    </td>
                    <td>
                        {{ record.result }}
                    </td>
                    <td>
                        {{ record.record.createtime }}
                    </td>
                    <td>
                        <button @click="open_record_content(record.record.id)" type="button"
                            class="btn btn-secondary">View</button>
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
import router from '../../router/index'
export default {
    components: {
        ContentField,
    },
    setup() {
        const store = useStore();
        let records = ref([]);
        let total_records = 0;
        let current_page = 1;
        let pages = ref([]);
        const click_page = (page_num) => {
            console.log("page num is: " +page_num)
            if (page_num === -2) page_num = current_page - 1;
            else if (page_num === -1) page_num = current_page + 1;
            let max_pages = parseInt(Math.ceil(total_records / 10));
            if (page_num >= 1 && page_num <= max_pages) {
                pull_page(page_num);
            }
        }
        console.log(total_records);
        const update_pages = () => {
            let max_pages = parseInt(Math.ceil(total_records / 10));
            let new_pages = [];
            for (let i = current_page - 2; i <= current_page + 2; i++) { // 一共显示五页
                if (i >= 1 && i <= max_pages) {
                    new_pages.push({
                        number: i,
                        is_active: i === current_page ? "active" : "",
                    });
                }
            }
            // pull_page(current_page);
            pages.value = new_pages;
        }
        const pull_page = (page_num) => {
            current_page = page_num;
            $.ajax({
                url: "http://127.0.0.1:3000/record/getlist/",
                data: {
                    page_num,
                },
                type: "GET",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    // console.log(resp);
                    records.value = resp.records;
                    total_records = resp.records_count;
                    update_pages();
                },
                error(resp) {
                    console.log(resp);
                }
            });
        }
        pull_page(current_page);
        const stringTo2D = (map) => {
            let g = [];
            for (let i = 0, k = 0; i < 13; i++) {
                let line = [];
                for (let j = 0; j < 14; j++, k++) {
                    if (map[k] === '0') line.push(0);
                    else line.push(1);
                }
                g.push(line);
            }
            return g;
        }
        const open_record_content = (recordId) => {
            for (const record of records.value) {
                if (record.record.id === recordId) {
                    store.commit("updateIsRecord", true);
                    store.commit("updateGame", {
                        map: stringTo2D(record.record.map),
                        a_id: record.record.aid,
                        a_sx: record.record.asx,
                        a_sy: record.record.asy,
                        b_id: record.record.bid,
                        b_sx: record.record.bsx,
                        b_sy: record.record.bsy,
                    });
                    store.commit("updateSteps", {
                        a_steps: record.record.asteps,
                        b_steps: record.record.bsteps,
                    });
                    store.commit("updateRecordLoser", record.record.loser);
                    router.push({
                        name: "record_content",
                        params: {
                            recordId
                        }
                    });
                    break;
                }
            }
        }
        return {
            records,
            open_record_content,
            pages,
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