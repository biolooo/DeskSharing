<template>

  <div class="login-container">
    <div class="login-box">
      <div>
        <img src="../assets/Logo(2).png" alt="Logo" class="logo" />
      </div>
      <h2>Anmelden</h2>
      <form @submit.prevent="handleLogin" class="login-form">
        <input
            v-model="username"
            type="text"
            placeholder="Nutzername"
            required
            class="input-field"
        />
        <input
            v-model="password"
            type="password"
            placeholder="Passwort"
            required
            class="input-field"
        />
        <button type="submit" class="login-button">Anmelden</button>
      </form>
      <p v-if="error" class="error-message">{{ error }}</p>
      <p class="register-text">
        Du hast noch kein Konto?
        <router-link to="/register" class="register-link">Konto erstellen</router-link>
      </p>
    </div>
  </div>
</template>

<script>
import { login } from "../services/authService";
import { useRouter } from "vue-router";
import { ref } from "vue";

export default {
  name: "UserLogin",
  setup() {
    const router = useRouter();
    const username = ref("");
    const password = ref("");
    const error = ref("");

    async function handleLogin() {
      try {
        // Authentifizieren und Token speichern
        await login(username.value, password.value);

        // Überprüfen, ob eine letzte Stockwerk-ID im localStorage gespeichert ist
        const lastFloorId = localStorage.getItem("lastFloorId") || "1";

        // Weiterleitung zur Seite mit dem Grundriss des letzten Stockwerks
        router.push(`/desks/${lastFloorId}`);
      } catch (err) {
        error.value = "Login failed. Please check your credentials.";
      }
    }

    return { username, password, handleLogin, error };
  },
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f4f8;
}

.login-box {
  background: #fff;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
  text-align: center;
}

h2 {
  margin-bottom: 1.5rem;
  color: #333;
  font-size: 1.8rem;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.input-field {
  padding: 0.8rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  transition: border-color 0.3s;
}

.input-field:focus {
  border-color: #007bff;
  outline: none;
}

.login-button {
  padding: 0.8rem;
  font-size: 1rem;
  color: #fff;
  background-color: #007bff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.login-button:hover {
  background-color: #0056b3;
}

.error-message {
  color: #d9534f;
  font-size: 0.9rem;
  margin-top: 1rem;
}

.register-text {
  margin-top: 1.5rem;
  font-size: 0.9rem;
  color: #666;
}

.register-link {
  color: #007bff;
  text-decoration: none;
  font-weight: bold;
}

.register-link:hover {
  text-decoration: underline;
}
.logo {
  width: 100%; /* Breite des Logos */
  height: auto; /* Automatische Höhe entsprechend der Breite */
}
</style>
