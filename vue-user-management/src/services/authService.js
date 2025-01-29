import axios from "axios";

export async function login(username, password) {
  const response = await axios.post("/api/auth/login", { username, password });
  localStorage.setItem("token", response.data.token);
  return response.data;
}

export async function register(username, password, email) {
  return axios.post("/api/auth/register", { username, password, email });
}

export function logout() {
  localStorage.removeItem("token");
}

export function getToken() {
  return localStorage.getItem("token");
}
