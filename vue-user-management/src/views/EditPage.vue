<template>
  <div class="edit-wrapper">
    <!-- Zurück-Button -->
    <button class="back-button" @click="goBack">&#8592;</button>

    <!-- Zentraler Container mit weißem Hintergrund -->
    <div class="main-content">
      <!-- Erste „Box“ (Gebäude) -->
      <div class="edit-block">
        <BuildingsList
            :buildings="buildings"
            @edit-building="handleEditBuilding"
            @create-new-building="handleCreateBuilding"
        />
        <BuildingForm
            :building="selectedBuilding"
            @form-submitted="refreshBuildings"
            @form-cancelled="clearBuildingSelection"
        />
      </div>

      <!-- Zweite „Box“ (Stockwerke) -->
      <div class="edit-block">
        <FloorsList
            ref="floorsList"
            @edit-floor="handleEditFloor"
            @create-new-floor="handleCreateFloor"
        />
        <FloorForm
            :floor="selectedFloor"
            :buildings="buildings"
            @form-submitted="refreshFloors"
            @form-cancelled="clearFloorSelection"
        />
      </div>
    </div>
  </div>
</template>

<script>
import BuildingsList from "../views/EditPage/BuildingList.vue";
import BuildingForm from "../views/EditPage/BuildingForm.vue";
import FloorsList from "../views/EditPage/FloorsList.vue";
import FloorForm from "../views/EditPage/FloorForm.vue";
import axios from "axios";

export default {
  name: "BuildingsPage",
  components: { BuildingsList, BuildingForm, FloorsList, FloorForm },
  data() {
    return {
      buildings: [], // Liste aller Gebäude
      selectedBuilding: null, // Ausgewähltes Gebäude
      selectedFloor: null,    // Ausgewähltes Stockwerk
    };
  },
  created() {
    this.fetchBuildings();
  },
  methods: {
    goBack() {
      // Beispiel: Zurück zur DeskList (oder einfach history.back())
      // this.$router.go(-1);
      // Oder zu einer bestimmten Route:
      this.$router.push({
        name: "DeskList",
        params: { floorId: localStorage.getItem("lastFloorId") },
      });
    },
    async fetchBuildings() {
      try {
        const response = await axios.get("/api/buildings", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        this.buildings = response.data;
      } catch (error) {
        alert("Fehler beim Laden der Gebäude!");
        console.error(error);
      }
    },
    refreshBuildings() {
      this.fetchBuildings();
      this.selectedBuilding = null;
    },
    handleEditBuilding(building) {
      this.selectedBuilding = building;
    },
    handleCreateBuilding() {
      this.selectedBuilding = {};
    },
    clearBuildingSelection() {
      this.selectedBuilding = null;
    },
    handleEditFloor(floor) {
      this.selectedFloor = floor;
    },
    handleCreateFloor() {
      this.selectedFloor = {};
    },
    refreshFloors() {
      const floorsList = this.$refs.floorsList;
      floorsList?.fetchFloors();
      this.selectedFloor = null;
    },
    clearFloorSelection() {
      this.selectedFloor = null;
    },
  },
};
</script>

<style scoped>
/* Ähnlich wie in deinem Profil-Code */
.edit-wrapper {
  /* Heller Hintergrund, damit sich die weiße Box abhebt */
  background-color: #f5f6fa;
  min-height: 100vh;    /* Volle Bildschirmhöhe */
  padding: 2rem;
  box-sizing: border-box;
  position: relative;
  /* Damit wir Inhalt mittig ausrichten können (horizontal) */
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* Zurück-Button oben links (wie im Profil) */
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

/* Haupt-Container */
.main-content {
  /* Weiße Box in der Mitte mit Schatten */
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);

  /* Innenabstand, Rand, Größe */
  padding: 2rem;
  margin-top: 1rem; /* etwas Abstand vom oberen Rand */
  width: 100%;
  max-width: 1100px; /* maximale Breite für große Bildschirme */
}

/* Wrapper für Building-List + Building-Form und FloorsList + FloorForm */
.edit-block {
  display: grid;
  grid-template-columns: 1fr 1fr; /* Zwei Spalten, jeweils 1fr */
  gap: 20px;
  margin-bottom: 2rem; /* Abstand zwischen Building-Block und Floor-Block */
}

@media (max-width: 768px) {
  .edit-block {
    grid-template-columns: 1fr;
  }
}
</style>
