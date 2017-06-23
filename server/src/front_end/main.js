import Vue from 'vue'
import MuseUI from 'muse-ui'
import 'muse-ui/dist/muse-ui.css'
import VueResource from 'vue-resource'
import AutoFocus from 'vue-auto-focus'
import VueRouter from 'vue-router'
import App from './app.vue'
import router from './router'


Vue.config.devtools = process.env.NODE_ENV === 'production';

Vue.config.debug = true

Vue.use(VueResource)
Vue.use(MuseUI)
Vue.use(AutoFocus)

new Vue({
  el: '#app',
  router,
  render: h => h(App)
});