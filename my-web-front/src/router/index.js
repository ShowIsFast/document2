import { createRouter, createWebHashHistory } from 'vue-router'
import Home from '../views/Home'
import Search from '../views/Search'
const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/Search',
    name: 'Search',
    component: Search
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
