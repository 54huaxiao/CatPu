<template>
	<div id='app'>
		<transition>
      <router-view :url = 'url'></router-view>
    </transition>
   </div>  
</template>



<script>
export default {
	name: 'app',
  data() {
    return {
    	url: 'http://localhost:8080/api'
    }
  },
  mounted() {
  	this.$router.replace('/login_and_register_page')
  }
}
</script>


<style>
	#app {
		position: fixed;
		width: 100%;
		height: 100%;
	}
</style>