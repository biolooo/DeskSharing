<template>
  <div class="form-container" v-if="isVisible">
    <h2>{{ editing ? "Gebäude bearbeiten" : "Neues Gebäude erstellen" }}</h2>
    <form @submit.prevent="handleSubmit">
      <!-- Eingabefeld: Name -->
      <label for="name">Name:</label>
      <input id="name" type="text" v-model="buildingData.name" required />

      <!-- Eingabefeld: Adresse -->
      <label for="address">Adresse:</label>
      <input id="address" type="text" v-model="buildingData.address" required />

      <!-- Buttons -->
      <div class="form-buttons">
        <button type="button" class="cancel-button" @click="cancel">
          Abbrechen
        </button>
        <button type="submit" class="save-button">
          {{ editing ? "Speichern" : "Erstellen" }}
        </button>
      </div>
    </form>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "BuildingForm",
  props: {
    building: {
      type: Object,
      default: () => null,
    },
  },
  data() {
    return {
      buildingData: {
        name: "",
        address: "",
      },
      editing: false,
      isVisible: false, // Standardmäßig versteckt
    };
  },
  watch: {
    building: {
      immediate: true,
      handler(newBuilding) {
        if (newBuilding) {
          // Daten des zu bearbeitenden Gebäudes ins Formular laden
          this.buildingData = { ...newBuilding };
          this.editing = true;
          this.isVisible = true;
        } else {
          // Formular zurücksetzen
          this.resetForm();
        }
      },
    },
  },
  methods: {
    async handleSubmit() {
      try {
        if (this.buildingData.id !== undefined) {
          // Gebäude bearbeiten
          await axios.put(`/api/buildings/${this.buildingData.id}`, this.buildingData, {
            headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
          });
          alert("Gebäude erfolgreich bearbeitet!");
        } else {
          // Neues Gebäude erstellen
          await axios.post("/api/buildings", this.buildingData, {
            headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
          });
          alert("Gebäude erfolgreich erstellt!");
        }
        console.log("form-submitted");
        this.$emit("form-submitted");
        //*window.location.reload();// Ereignis auslösen
        this.resetForm();
      } catch (error) {
        alert("Fehler beim Speichern des Gebäudes!");
        console.error(error);
      }
    },
    cancel() {
      this.resetForm(); // Nur hier soll das Formular ausgeblendet werden
      this.$emit("form-cancelled");
    },
    resetForm() {
      this.buildingData = { name: "", address: "" };
      this.editing = false;
      this.isVisible = false; // Standardmäßig ausgeblendet
    },
  },
};
</script>

<style scoped>
.form-container {
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 20px;
  background-color: #f9f9f9;
}
h2 {
  text-align: center;
  margin-bottom: 20px;
}
label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}
input {
  width: 100%;
  padding: 8px;
  margin-bottom: 15px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.form-buttons {
  display: flex;
  justify-content: space-between;
}
.save-button {
  background-color: #007bff;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.save-button:hover {
  background-color: #0056b3;
}
.cancel-button {
  background-color: #dc3545;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.cancel-button:hover {
  background-color: #c82333;
}
</style>
