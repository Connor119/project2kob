<template>
  <div>
    hello,World
    <div>从后端搞来的数据: {{ data1 }}</div>
  </div>
  <router-view/>
</template>


<script>
import $ from 'jquery';
import { ref } from 'vue';

export default {
  name: "APP",
  setup: ()=>{
      let data1 = ref("");

      $.ajax({
        url: "http://127.0.0.1:3000/pk/getBotInfoList",
        type: "get",
        success: resp => {
          console.log("1111");
          console.log(resp);
          data1.value = resp[0];
        }
      });


      return {
        data1
      }
  }
}
</script>


<style>
body {
  background-image: url("@/assets/20.png");
  background-size: cover;
}
</style>
