<template>
  <ContendField>对局列表</ContendField>
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
</style>