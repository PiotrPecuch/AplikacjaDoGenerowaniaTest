import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'

//Toastification
import Toast from "vue-toastification";
import "vue-toastification/dist/index.css";
import './assets/main.css'
import AuthServices from "@/services/auth.services.js";
import {aliases} from "vuetify/iconsets/fa";
import {mdi} from "vuetify/iconsets/mdi";

import { pl } from 'vuetify/locale'



const app = createApp(App)

const vuetify = createVuetify({
    components,
    directives,
    locale: {
        locale: 'pl',
        messages: { pl },
    }
})

app.use(router)
app.use(vuetify)
app.use(Toast)

app.config.globalProperties.$themeType = 'dark';


app.mount('#app')

export default createVuetify({
    icons: {
        defaultSet: 'mdi',
        aliases,
        sets: {
            mdi,
        },
    },
})