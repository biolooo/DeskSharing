<template>
  <div class="container">
    <h2>Gebäude</h2>
    <div class="list-container">
      <!-- Linke Spalte: Gebäude-Liste -->
      <div class="list">
        <ul>
          <li
              v-for="building in buildings"
              :key="building.id"
              :class="{ selected: building.id === selectedBuildingId }"
          >
            <span>{{ building.name }}</span>
            <div class="actions">
              <button @click="editBuilding(building)">✏️ Bearbeiten</button>
              <button @click="deleteBuilding(building.id)">🗑️ Löschen</button>
            </div>
          </li>
        </ul>
        <div
            class="add-desk-button"
            @click="createNewBuilding"
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
  name: "BuildingsList",
  data() {
    return {
      selectedBuildingId: null, // ID des aktuell ausgewählten Gebäudes
    };
  },
  props: {
    buildings: {
      type: Array,
    }
  },
  methods: {
    async deleteBuilding(buildingId) {
      try {
        if (confirm("Möchten Sie dieses Gebäude wirklich löschen?")) {
          await axios.delete(`/api/buildings/${buildingId}`, {
            headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
          });
          alert("Gebäude erfolgreich gelöscht!");
        }
      } catch (error) {
        alert("Fehler beim Löschen des Gebäudes!");
        console.error(error);
      }
    },
    editBuilding(building) {
      this.selectedBuildingId = building.id; // Speichere die ID des ausgewählten Gebäudes
      this.$emit("edit-building", building); // Sende das Gebäude an die übergeordnete Komponente
    },
    createNewBuilding() {
      this.selectedBuildingId = null; // Kein Gebäude ausgewählt
      this.$emit("create-new-building"); // Informiere die übergeordnete Komponente
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
