import {createRouter, createWebHistory} from 'vue-router'
import LoginView from "@/views/StudentView/LoginView.vue";
import TokenServices from "@/services/TokenServices.js";

const router = createRouter({
        history: createWebHistory(import.meta.env.BASE_URL),
        routes: [
            {
                path: '/',
                name: 'home',
                component: LoginView,
                meta: {
                    title: 'Logowanie',
                    requiresAuth: false,
                },
            },
            {
                path: '/login',
                name: 'login',
                component: LoginView,
                meta: {
                    title: 'Logowanie',
                    requiresAuth: false,
                },
            },
            {
                path: '/register',
                name: 'register',
                component: () => import('../views/StudentView/RegisterView.vue'),
                meta: {
                    title: 'Rejestracja',
                    requiresAuth: false,
                },
            },{
                path: '/exam',
                name: 'ExamLogin',
                props: true,
                component: () => import('../views/StudentView/ExamLogin.vue'),
                meta: {
                    title: 'Rozpocznij egzamin',
                    requiresAuth: false,
                }
            },
            {
                path: '/main',
                name: 'main',
                component: () => import('../views/TeacherView/Main.vue'),
                meta: {
                    title: 'Strona główna',
                    requiresAuth: true,
                }
            }, {
                path: '/main/startedExams',
                name: 'Started exams',
                component: () => import('../views/TeacherView/ExamDetails/StartedExam.vue'),
                meta: {
                    title: 'Rozpoczęte egzaminy',
                    requiresAuth: true,
                }
            },{
                path: '/main/finishedExams',
                name: 'Finished exams',
                component: () => import('../views/TeacherView/ExamDetails/FinishedExams.vue'),
                meta: {
                    title: 'Zakończone egzaminy',
                    requiresAuth: true,
                }
            },
            {
                path: '/admin',
                name: 'AdminPanel',
                component: () => import('../views/AdminView/AdminPanel.vue'),
                meta: {
                    title: 'AdminPanel',
                    requiresAuth: true,
                }
            },{
                path: '/resetPassword',
                name: 'Resetowanie',
                component: () => import('../views/StudentView/ResetPassword.vue'),
                meta: {
                    title: 'Resetowanie hasła',
                    requiresAuth: false,
                }
            },{
                path: '/exam/solve',
                name: 'Solving exam',
                component: () => import('../views/StudentView/ExamSolve.vue'),
                meta: {
                    title: 'Rozwiązywanie',
                    requiresAuth: false,
                }
            },{
                path: '/main/startedExams/details/:id',
                name: 'Started exam details',
                props: true,
                component: () => import('../views/TeacherView/ExamDetails/StartedExamDetails.vue'),
                meta: {
                    title: 'Detale ucznia',
                    requiresAuth: true,
                }
            },{
                path: '/exam/results',
                name: 'EndExam',
                props: true,
                component: () => import('../views/StudentView/ExamResults.vue'),
                meta: {
                    title: 'Wynik',
                    requiresAuth: false,
                }
            },
            {
                path: '/main/startedExams/details/:id/answers/:ssoLogin/:ssoPassword',
                name: 'ExamDetails',
                props: true,
                component: () => import('../views/TeacherView/ExamDetails/StartedExamDetailsUserAnswers.vue'),
                meta: {
                    title: 'Detale',
                    requiresAuth: true,
                }
            },{
                    path: '/main/edit/:id',
                    name: 'Edit exam',
                    component: () => import('../views/TeacherView/EditExam.vue'),
                    meta: {
                        title: 'Edytowanie',
                        requiresAuth: true,
                    }
                },

            {path: '/:notFound', component: () => import('../views/PageNotFound.vue')}
        ]
    }

)

router.beforeEach(async (to, from, next) => {
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
    if (requiresAuth && !TokenServices.getLocalAccessToken()) {
        next('/login');
    }  else if (requiresAuth && TokenServices.getLocalAccessToken()) {
        const requiredRole = TokenServices.getUserRole();
        const isValid = await TokenServices.isTokenValid();

        if (isValid && requiredRole.includes("ROLE_ADMIN")) {
            if (to.path !== '/admin') {
                next('/admin');
            } else {
                next();
            }
        } else if (isValid) {
            next();
        } else {
            next('/login');
        }
    } else {
        next();
    }
});

router.afterEach((to, from) => {
    document.title = to.meta.title
})


export default router
