<template>
  <div class="register-container">
    <div class="register-box">
      <div>
        <img src="../assets/Logo(2).png" alt="Logo" class="logo" />
      </div>
      <h2>Konto erstellen</h2>
      <form @submit.prevent="handleRegister" class="register-form">
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
        <small class="password-hint">mindestens 8 Zeichen, 1 Zahl, 1 Großbuchstaben, 1 Sonderzeichen</small>
        <input
            v-model="email"
            type="email"
            placeholder="Email"
            required
            class="input-field"
        />
        <button type="submit" class="register-button">Konto erstellen</button>
      </form>
      <p v-if="error" class="error-message">{{ error }}</p>
      <p> <router-link to="/login" class="register-link">Anmelden</router-link> </p>
    </div>
  </div>
</template>

<script>
import { register } from "../services/authService";
import { useRouter } from "vue-router";
import { ref } from "vue";

export default {
  name: "UserRegister",
  setup() {
    const router = useRouter();
    const username = ref("");
    const password = ref("");
    const email = ref("");
    const error = ref("");

    async function handleRegister() {
      try {
        await register(username.value, password.value, email.value);
        router.push("/login");
      } catch (err) {
        error.value = "Registration failed.";
      }
    }

    return { username, password, email, handleRegister, error };
  },
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f4f8;
}

.register-box {
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

.register-form {
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

.register-button {
  padding: 0.8rem;
  font-size: 1rem;
  color: #fff;
  background-color: #007bff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.register-button:hover {
  background-color: #0056b3;
}

.error-message {
  color: #d9534f;
  font-size: 0.9rem;
  margin-top: 1rem;
}
.logo {
  width: 100%; /* Breite des Logos */
  height: auto; /* Automatische Höhe entsprechend der Breite */
}
.password-hint {
  font-size: 0.8rem;
  color: #555;
  margin-top: -0.5rem;
}
</style>
