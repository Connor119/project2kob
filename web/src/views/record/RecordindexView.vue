<template>
  <ContendField>
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
  </ContendField>
</template>

<script>
import ContendField from "../../components/ContendField.vue"
import { useStore } from 'vuex'
import { ref } from 'vue'
import $ from 'jquery'
import router from '../../router/index'
export default {
  components: {
    ContendField,
  },
  setup() {
      const store = useStore();
      let records = ref([]);
      let total_records = 0;
      let current_page = 1;
      console.log(total_records);
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
                // console.log(resp)
                records.value = resp.records;
                total_records = resp.records_count;
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