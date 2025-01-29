<template>
  <div class="profile-wrapper">
    <!-- Zurück-Button -->
    <button class="back-button" @click="goToDesks">&#8592;</button>

    <!-- Profil-Karte -->
    <div class="profile-card">

      <!-- Linker Bereich: Profilbild -->
      <div class="profile-left">
        <div class="profile-picture-section">
          <!-- Profilbild oder Platzhalter -->
          <img
              v-if="user.profilePicture"
              :src="'data:image/jpeg;base64,' + user.profilePicture"
              alt="Profilbild"
              class="profile-picture"
          />
          <div v-else class="profile-picture-placeholder">
            Kein Bild
          </div>

          <!-- Stift-Icon (wird beim Hover sichtbar) -->
          <div class="icon-edit" @click="triggerUpload">
            &#9998;
          </div>

          <!-- Verstecktes Dateiupload-Feld -->
          <input
              ref="fileInput"
              type="file"
              class="upload-input"
              @change="uploadProfilePicture"
          />
        </div>
      </div>

      <!-- Rechter Bereich: Überschrift + Felder -->
      <div class="profile-right">
        <h2>Profil</h2>

        <div class="profile-field">
          <label>Benutzername:</label>
          <div v-if="!editing">
            <p>{{ user.username }}</p>
          </div>
          <input v-else type="text" v-model="editedUser.username" />
        </div>

        <div class="profile-field">
          <label>E-Mail:</label>
          <div v-if="!editing">
            <p>{{ user.email }}</p>
          </div>
          <input
              v-else
              type="email"
              v-model="editedUser.email"
              placeholder="E-Mail eingeben"
          />
        </div>

        <div class="profile-field" v-if="editing">
          <label>Passwort:</label>
          <input
              type="password"
              v-model="editedUser.password"
              placeholder="Neues Passwort eingeben"
          />
        </div>

        <!-- Aktionsbuttons -->
        <div class="profile-actions">
          <button v-if="!editing" @click="startEditing" class="edit-button">
            &#9998; Bearbeiten
          </button>
          <div v-if="editing">
            <button @click="cancelEditing" class="cancel-button">Abbrechen</button>
            <button @click="saveChanges" class="save-button">Speichern</button>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "UserProfile",
  data() {
    return {
      user: {},       // Originaldaten des Benutzers
      editedUser: {}, // Bearbeitbare Kopie der Benutzerdaten
      editing: false, // Bearbeitungsmodus
    };
  },
  async mounted() {
    try {
      const response = await axios.get("/api/user/profile", {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      });
      this.user = response.data;
      this.editedUser = { ...response.data };
    } catch (err) {
      console.error("Fehler beim Laden des Profils:", err);
    }
  },
  methods: {
    goToDesks() {
      this.$router.push({
        name: "DeskList",
        params: { floorId: localStorage.getItem("lastFloorId") },
      });
    },
    startEditing() {
      this.editing = true;
    },
    cancelEditing() {
      this.editedUser = { ...this.user };
      this.editing = false;
    },
    async saveChanges() {
      try {
        const response = await axios.put("/api/user/profile", this.editedUser, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        });
        this.user = response.data;
        this.editing = false;
        alert("Profil erfolgreich aktualisiert!");
      } catch (err) {
        console.error("Fehler beim Speichern der Änderungen:", err);
        alert("Profil konnte nicht aktualisiert werden.");
      }
    },
    // Klick auf das Stift-Icon -> Datei-Upload-Dialog öffnen
    triggerUpload() {
      this.$refs.fileInput.click();
    },
    async uploadProfilePicture(event) {
      const file = event.target.files[0];
      if (!file) return;

      const formData = new FormData();
      formData.append("file", file);

      try {
        await axios.post("/api/user/profile-picture", formData, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
            "Content-Type": "multipart/form-data",
          },
        });
        this.$router.go(0);
      } catch (err) {
        console.error("Fehler beim Hochladen des Profilbilds:", err);
        alert("Profilbild konnte nicht hochgeladen werden.");
      }
    },
  },
};
</script>

<style scoped>
/* Gesamter Hintergrundbereich */
.profile-wrapper {
  background-color: #f5f6fa; /* Leichter Grauton */
  min-height: 100vh;
  padding: 2rem;
  box-sizing: border-box;
  position: relative;
}

/* Card-Container */
.profile-card {
  display: flex;
  flex-direction: row;
  max-width: 900px;
  margin: 0 auto;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

/* Linke Seite (fixe Breite, 15px Rand) */
.profile-left {
  /* Passe die Breite ggf. an deinen Bedarf an: 320px = 300px + 2×15px Rand */
  width: 320px;
  background-color: #f7f9fc;
  padding: 15px;  /* Die ~15 Pixel Abstand zu allen Rändern */
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Rechter Bereich */
.profile-right {
  flex: 1;
  padding: 1.5rem 2rem;
}

/* Profilbild-Bereich */
.profile-picture-section {
  /* Bild so groß wie möglich innerhalb der linke Spalte zeigen */
  width: 100%;
  height: 100%;
  position: relative;
}

/* Tatsächliches Bild */
.profile-picture {
  display: block;
  width: 100%;
  height: 100%;
  /* Füllt das übergeordnete Element aus, behält das Seitenverhältnis */
  object-fit: cover;
  border-radius: 10px;
}

/* Fallback-Platzhalter, falls kein Bild vorhanden */
.profile-picture-placeholder {
  width: 100%;
  height: 100%;
  background-color: #cbd3da;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: #fff;
}

/* Stift-Icon (absolut positioniert, beim Hover eingeblendet) */
.icon-edit {
  position: absolute;
  top: 15px;       /* ebenfalls ~15px vom oberen Rand entfernt */
  right: 15px;     /* ebenfalls ~15px vom rechten Rand entfernt */
  background-color: rgba(0, 0, 0, 0.6);
  color: #fff;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  display: none;  /* unsichtbar, bis Hover passiert */
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.2s;
}

/* Sichtbar beim Hover */
.profile-picture-section:hover .icon-edit {
  display: flex;
}

/* Hover-Effekt aufs Icon */
.icon-edit:hover {
  background-color: rgba(0, 0, 0, 0.8);
}

/* Verstecktes Dateiupload-Feld */
.upload-input {
  display: none;
}

/* Überschrift */
.profile-right h2 {
  margin-top: 0;
  margin-bottom: 1rem;
}

/* Eingabefelder & Labels */
.profile-field {
  margin-bottom: 1rem;
}

.profile-field label {
  display: block;
  font-weight: 600;
  margin-bottom: 0.3rem;
  color: #333;
}

.profile-field p {
  margin: 0;
  padding: 0.8rem;
  background-color: #e9edf1;
  border-radius: 4px;
  color: #333;
}

.profile-field input {
  width: 100%;
  padding: 0.8rem;
  font-size: 1rem;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  box-sizing: border-box;
}

/* Aktions-Buttons (Bearbeiten, Speichern, Abbrechen) */
.profile-actions {
  margin-top: 1.5rem;
  display: flex;
  gap: 0.5rem;
}

.edit-button,
.save-button,
.cancel-button {
  border: none;
  border-radius: 6px;
  padding: 0.8rem 1.2rem;
  cursor: pointer;
  transition: background 0.2s, transform 0.2s;
  font-size: 1rem;
}

.edit-button {
  background-color: #007bff;
  color: #fff;
}

.save-button {
  background-color: #007bff;
  color: #fff;
  margin-right: 10px;
}

.cancel-button {
  background-color: #e74c3c;
  color: #fff;
  margin-right: 10px;
}

.edit-button:hover {
  background-color: #0056b3;
}

.save-button:hover {
  background-color: #0056b3;
}

.cancel-button:hover {
  background-color: #c0392b;
}

.edit-button:active,
.save-button:active,
.cancel-button:active {
  transform: scale(0.98);
}

/* Zurück-Button */
.back-button {
  position: absolute;
  top: 2rem;
  left: 2rem;
  background-color: #868e96;
  color: #fff;
  border: none;
  border-radius: 50%;
  font-size: 1.3rem;
  width: 45px;
  height: 45px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.2s, transform 0.2s;
}

.back-button:hover {
  background-color: #6c757d;
}

.back-button:active {
  transform: scale(0.95);
}
</style>
