import { createRouter, createWebHashHistory } from 'vue-router'

import PKIndexView from '../views/pk/PKindexView.vue'
import RecordIndexView from '../views/record/RecordindexView.vue'
import RanklistIndexView from '../views/ranklist/RanklistindexView.vue'
import UserBotIndexView from '../views/user/bot/UserBotindexView.vue'
import NotFound from '../views/error/NotFound.vue'
import UserAccountLoginView from '../views/user/account/UserAccountLoginView'
import UserAccountRegisterView from '../views/user/account/UserAccountRegisterView'
import store from '../store/index'
import RecordContentView from '../views/record/RecordContentView'

const routes = [{
        path: '/',
        name: 'home',
        redirect: '/pk/',
        meta: {
            requestAuth: true,
        }

    }, {
        path: "/pk/",
        name: "pk_index",
        component: PKIndexView,
        meta: {
            requestAuth: true,
        }
    }, {
        path: "/record/",
        name: "record_index",
        component: RecordIndexView,
        meta: {
            requestAuth: true,
        }
    }, {
        path: "/ranklist/",
        name: "ranklist_index",
        component: RanklistIndexView,
        meta: {
            requestAuth: true,
        }
    }, {
        path: "/user/bot/",
        name: "user_bot_index",
        component: UserBotIndexView,
        meta: {
            requestAuth: true,
        }
    }, {
        path: "/user/account/login/",
        name: "user_account_login",
        component: UserAccountLoginView,
        meta: {
            requestAuth: false,
        }
    }, {
        path: "/record/:recordId/",
        name: "record_content",
        component: RecordContentView,
        meta: {
            requestAuth: true,
        }
    },
    {
        path: "/user/account/register/",
        name: "user_account_register",
        component: UserAccountRegisterView,
        meta: {
            requestAuth: false,
        }
    }, {
        path: "/404/",
        name: "not_found_index",
        component: NotFound,
        meta: {
            requestAuth: false,
        }
    }, {
        path: "/:catchAll(.*)",
        redirect: "/404/"
    }
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})


router.beforeEach((to, from, next) => {
    if (to.meta.requestAuth && !store.state.user.is_login) {
        next({ name: "user_account_login" });
    } else {
        next();
    }
})


export default router