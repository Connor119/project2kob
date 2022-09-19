<template>
  <NavBar></NavBar>
  <router-view></router-view>
</template>


<script>
// import $ from 'jquery';
// import { ref } from 'vue';
import NavBar from './components/NavBar.vue'
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"

export default {
  name: "APP",
  components: {
    NavBar,
  }
}
</script>


<style >
body {
  background-image: url("@/assets/images/20.png");
  background-size: cover;
}
</style>
