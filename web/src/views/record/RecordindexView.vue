<template>
  <ContendField>
    <table class="table table-hover">
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
                        <button type="button" class="btn btn-secondary">View</button>
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
      return {
          records,
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