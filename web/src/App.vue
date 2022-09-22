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
import $ from 'jquery'

export default {
  name: "APP",
  components: {
    NavBar,
  },
  setup(){
    $.ajax({
      url: "http://localhost:3000/user/account/token/",
      type: "post",
      data: {
        username:'fbs',
        password:'119119'
      },
      success(resp){
        console.log(resp);
      },
      error(resp){
        console.log(resp);
      }
    })

    $.ajax({
      url: "http://127.0.0.1:3000/user/account/info/",
      type: "get",
      headers: {
        Authorization: "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJkZjliMTJmNzI2MTM0YmZiOTc1MGQxODM1ZTY4Yjc1YiIsInN1YiI6IjIiLCJpc3MiOiJzZyIsImlhdCI6MTY2Mzg0MzUyMiwiZXhwIjoxNjY1MDUzMTIyfQ.9-d6yHzhrCCYVSAjEzv-D3IkHbk6QdDWj8R7GGV5Bo8",
      },
      success(resp) {
        console.log(resp);
      },
      error(resp) {
        console.log(resp);
      }
    });

    $.ajax({
      url: "http://localhost:3000/user/account/info/",
      type: "get",
      headers: {
        Authorization:"Bearer "+"eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI1YjI3OWY2NjRkMWE0YzA5YjEyMWMzMGM3N2M4YzU3MyIsInN1YiI6IjIiLCJpc3MiOiJzZyIsImlhdCI6MTY2Mzg0MjM5MSwiZXhwIjoxNjY1MDUxOTkxfQ.IzgG2azZWnpy0lMX_bz6sZGKOWXVpnOhTewDtSOGZpY",
      },
      success(resp){
        console.log(resp);
      },
      error(resp){
        console.log(resp);
      }
    })

    $.ajax({
      url: "http://localhost:3000/user/account/register/",
      type: "post",
      data: {
        username:'fbs1',
        password:'119119',
        confirmedPassword:'119118',
      },
      success(resp){
        console.log(resp);
      },
      error(resp){
        console.log(resp);
      }
    })


  }
}
</script>


<style >
body {
  background-image: url("@/assets/images/20.png");
  background-size: cover;
}
</style>
