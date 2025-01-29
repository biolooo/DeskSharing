<template>
  <div class="container">
    <h2>Stockwerke</h2>
    <div class="list-container">
      <div class="list">
        <ul>
          <li
              v-for="floor in floors"
              :key="floor.id"
              :class="{ selected: floor.id === selectedFloorId }"
          >
            <span>{{ floor.name }}</span>
            <div class="actions">
              <button @click="editFloor(floor)">✏️ Bearbeiten</button>
              <button @click="deleteFloor(floor.id)">🗑️ Löschen</button>
            </div>
          </li>
        </ul>
        <div
            class="add-desk-button"
            @click="createNewFloor"
        >
          <div class="add-desk-icon">+</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "FloorsList",
  props: {
    buildingId: {
      type: Number,
    },
  },
  data() {
    return {
      floors: [], // Liste der Stockwerke
      selectedFloorId: null, // ID des aktuell ausgewählten Stockwerks
    };
  },
  methods: {
    async fetchFloors() {
      try {
        const response = await axios.get(`/api/floors/all`, {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        this.floors = response.data;
        console.log(this.floors);
      } catch (error) {
        alert("Fehler beim Laden der Stockwerke!");
        console.error(error);
      }
    },
    async deleteFloor(floorId) {
      try {
        if (confirm("Möchten Sie dieses Stockwerk wirklich löschen?")) {
          await axios.delete(`/api/floors/${floorId}`, {
            headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
          });
          alert("Stockwerk erfolgreich gelöscht!");
          this.fetchFloors(); // Aktualisiere die Liste
        }
      } catch (error) {
        alert("Fehler beim Löschen des Stockwerks!");
        console.error(error);
      }
    },
    editFloor(floor) {
      this.selectedFloorId = floor.id; // Speichere die ID des ausgewählten Stockwerks
      this.$emit("edit-floor", floor); // Sende das Stockwerk an die übergeordnete Komponente
    },
    createNewFloor() {
      this.selectedFloorId = null; // Kein Stockwerk ausgewählt
      this.$emit("create-new-floor"); // Informiere die übergeordnete Komponente
    },
  },
  watch: {
    buildingId: {
      immediate: true,
      handler() {
        this.fetchFloors(); // Lade Stockwerke, wenn ein neues Gebäude ausgewählt wird
      },
    },
  },
};
</script>

<style scoped>
.add-desk-button {
  width: 40px;
  height: 40px;
  background-color: #007bff;
  border-radius: 50%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: transform 0.3s ease, background-color 0.3s ease;
  margin: 10px auto 0 auto; /* zentriert unter der Liste */
}
.add-desk-button:hover {
  background-color: #0056b3;
  transform: scale(1.1);
}
/* Icon im Button */
.add-desk-icon {
  font-size: 24px;
  color: #fff;
  line-height: 1; /* Damit das Pluszeichen nicht verschoben wird */
}
.container {
  padding: 20px;
}
h2 {
  text-align: center;
  margin-bottom: 10px;
}
.list-container {
  display: flex;
  flex-direction: column;
}
.list {
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 10px;
  background-color: #f9f9f9;
}
ul {
  list-style: none;
  padding: 0;
}
li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
  border-bottom: 1px solid #ddd;
}
li.selected {
  background-color: #dceeff;
}
li:last-child {
  border-bottom: none;
}
.actions button {
  margin-left: 5px;
  cursor: pointer;
}
.add-button {
  text-align: center;
  margin-top: 10px;
}
.add-button button {
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  font-size: 24px;
  cursor: pointer;
}
.add-button button:hover {
  background-color: #0056b3;
}
</style>
