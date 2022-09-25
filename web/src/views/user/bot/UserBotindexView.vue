<template>
  <ContendField>我的bot</ContendField>
</template>

<script>
import ContendField from "../../../components/ContendField.vue"
// 测试接口/user/bot/add/
import $ from 'jquery'
// 由于我们的token存入了全局变量中所以需要引入vueX
import { useStore } from 'vuex'



export default {
    components:{
        ContendField,
    },
    setup() {
        const store = useStore();
      //   $.ajax({
      //       url: "http://127.0.0.1:3000/user/bot/add/",
      //       type: "POST",
      //       data: {
      //           title: "My Bot3",
      //           description: null,
      //           content: "Bot Code",
      //       },
      //       headers: {
      //           Authorization: "Bearer " + store.state.user.token,
      //       },
      //       success(resp) {
      //           console.log(resp);
      //       },
      //       error(resp) {
      //           console.log(resp);
      //       }
      //   });
      // }

      $.ajax({
            // url: "http://127.0.0.1:3000/user/bot/add/",
            url: "http://127.0.0.1:3000/user/bot/remove/",
            type: "POST",
            data: {
                // title: "My Bot1",
                // description: null,
                // content: "Bot Code",
                bot_id: 3,
            },
            headers: {
                Authorization: "Bearer " + store.state.user.token,
            },
            success(resp) {
                console.log(resp);
            },
            error(resp) {
                console.log(resp);
            }
        });
    }
}
</script>

<style>

</style>