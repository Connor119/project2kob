import { createRouter, createWebHashHistory } from 'vue-router'

import PKIndexView from '../views/pk/PKindexView.vue'
import RecordIndexView from '../views/record/RecordindexView.vue'
import RanklistIndexView from '../views/ranklist/RanklistindexView.vue'
import UserBotIndexView from '../views/user/bot/UserBotindexView.vue'
import NotFound from '../views/error/NotFound.vue'

const routes = [{
    path: '/',
    name: 'home',
    redirect: '/pk/'

}, {
    path: "/pk/",
    name: "pk_index",
    component: PKIndexView,
}, {
    path: "/record/",
    name: "record_index",
    component: RecordIndexView,
}, {
    path: "/ranklist/",
    name: "ranklist_index",
    component: RanklistIndexView,
}, {
    path: "/user/bot/",
    name: "user_bot_index",
    component: UserBotIndexView,
}, {
    path: "/404/",
    name: "not_found_index",
    component: NotFound,
}, {
    path: "/:catchAll(.*)",
    redirect: "/404/"
}]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

export default router