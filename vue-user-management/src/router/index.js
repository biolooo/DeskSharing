import { createRouter, createWebHistory } from "vue-router";
import UserLogin from "../views/UserLogin.vue";
import UserRegister from "../views/UserRegister.vue";
import UserProfile from "../views/UserProfile.vue";
import BuildingList from "../views/EditPage/BuildingList.vue";
import FloorList from "../views/EditPage/FloorsList.vue";
import DeskList from "../components/DeskList.vue";
import EditPage from "../views/EditPage.vue";

const routes = [
  // Authentication routes
  { path: "/login", name: "UserLogin", component: UserLogin },
  { path: "/register", name: "UserRegister", component: UserRegister },

  // Protected routes
  {
    path: "/building",
    name: "BuildingList",
    component: BuildingList,
    meta: { requiresAuth: true }, // Requires authentication
  },
  {
    path: "/edit",
    name: "EditPage",
    component: EditPage,
  },
  {
    path: "/profile",
    name: "UserProfile",
    component: UserProfile,
    meta: { requiresAuth: true }, // Requires authentication
  },
  {
    path: "/floors/:buildingId",
    name: "FloorList",
    component: FloorList,
    props: true,
    meta: { requiresAuth: true }, // Requires authentication
  },
  {
    path: "/desks/:floorId",
    name: "DeskList",
    component: DeskList,
    props: true,
    meta: { requiresAuth: true }, // Requires authentication
  },

  // Catch-All Route
  { path: "/:catchAll(.*)", redirect: "/login" },
];


const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token"); // Retrieve the token
  const isAuthenticated = token && token.trim().length > 0; // Check if the token is valid

  if (to.meta.requiresAuth && !isAuthenticated) {
    next({ path: "/login", query: { redirect: to.fullPath } }); // Redirect to login
  } else {
    next(); // Allow navigation
  }
});
// Navigation Guard für geschützte Routen



export default router;
