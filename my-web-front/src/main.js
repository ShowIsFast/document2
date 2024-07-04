import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import './assets/css/all.css'
import './assets/css/pages-list.css'
import './assets/css/pages-JD-index.css'
import 'normalize.css'
import './http'

createApp(App).use(store).use(router).mount('#app')
