<template>
  <div class="form-container" v-if="isVisible">
    <h2>{{ editing ? "Stockwerk bearbeiten" : "Neues Stockwerk erstellen" }}</h2>
    <form @submit.prevent="handleSubmit">
      <!-- Eingabefeld: Name -->
      <label for="name">Name:</label>
      <input id="name" type="text" v-model="floorData.name" required />

      <!-- Datei-Upload: Floor Plan Image -->
      <label for="floorPlanImage">Floor Plan:</label>
      <input id="floorPlanImage" type="file" @change="handleFileUpload" />

      <!-- Auswahlfeld: Gebäude -->
      <label for="building">Gebäude:</label>
      <select id="building" v-model="buildingId" required>
        <option v-for="building in buildings" :value="building.id" :key="building.id">
          {{ building.name }}
        </option>
      </select>

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
  name: "FloorForm",
  props: {
    floor: {
      type: Object,
      default: () => null,
    },
    buildings: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      floorData: {
        name: "",
        building: null,
      },
      file: null, // Für Datei-Upload
      editing: false,
      isVisible: false,
      buildingId: null,
    };
  },
  watch: {
    floor: {
      immediate: true,
      handler(newFloor) {
        if (newFloor) {
          this.floorData = { ...newFloor }; // Daten initialisieren
          this.editing = true;
          this.isVisible = true;
        } else {
          this.resetForm();
        }
      },
    },
  },
  methods: {
    handleFileUpload(event) {
      this.file = event.target.files[0]; // Datei speichern
    },
    async handleSubmit() {
      const buildingResponse = await this.getBuildingById();
      this.floorData.building = buildingResponse.data;
      console.log(this.floorData);
      try {
        if (this.floorData.id !== undefined) {
          console.log(this.floorData);
          await axios.put(`/api/floors/${this.floorData.id}`, this.floorData, {
            headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
          });
          alert("Stockwerk erfolgreich bearbeitet!");
        } else {
          console.log(this.floorData);
          await axios.post("/api/floors", this.floorData, {
            headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
          });
          alert("Stockwerk erfolgreich erstellt!");
        }
        this.$emit("form-submitted"); // Eltern benachrichtigen
        this.resetForm();
        window.location.reload();
      } catch (error) {
        alert("Fehler beim Speichern des Stockwerks!");
        console.error(error);
      }
    },
    cancel() {
      this.resetForm();
      this.$emit("form-cancelled"); // Eltern benachrichtigen
    },
    resetForm() {
      this.floorData = { name: "", buildingId: null };
      this.file = null;
      this.editing = false;
      this.isVisible = false;
    },
    getBuildingById() {
      try {
        return axios.get(`/api/buildings/${this.buildingId}`, {
          headers: {Authorization: `Bearer ${localStorage.getItem("token")}`},
        });
      } catch (error) {
        alert("Fehler beim Laden des Gebäudes!");
        console.error(error);
      }
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
input,
select {
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
