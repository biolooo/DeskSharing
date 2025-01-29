import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import VCalendar from 'v-calendar';
import 'v-calendar/style.css';


const app = createApp(App);

// VCalendar für Vue 3 registrieren
app.use(VCalendar, {componentPrefix: 'v',})
app.use(router);
app.mount("#app");
