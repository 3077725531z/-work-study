import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import Vant from 'vant'
import 'vant/lib/index.css'
import '../../shared/tokens.css'
import './theme.css'

createApp(App).use(router).use(Vant).mount('#app')
