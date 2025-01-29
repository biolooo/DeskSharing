<template>
  <div>
    <div class="floor-selection">

      <button class="user-bookings-button" @click="toggleUserBookingsSidebar">
        📋 Meine Buchungen
      </button>

      <div class="dividivi">
        <!-- Gebäude auswählen -->
        <label>
          <select v-model="selectedBuildingId" @change="onBuildingChange">
            <option value="">-- Gebäude wählen --</option>
            <option v-for="b in buildings" :key="b.id" :value="b.id">
              {{ b.name }}
            </option>
          </select>
        </label>

        <!-- Stockwerk auswählen (abhängig vom Gebäude) -->
        <label>
          <select v-model="selectedFloorId">
            <option value="">-- Stockwerk wählen --</option>
            <option v-for="f in floors" :key="f.id" :value="f.id">
              {{ f.name }}
            </option>
          </select>
        </label>

        <label class="date-picker">
          <input type="date" v-model="selectedDateFilter" />
        </label>


        <!-- Button zum Laden/Wechseln -->
        <button class="confirm-button" @click="applySelection">
          Anwenden
        </button>
      </div>

      <div class="dividivi">
        <!-- Button zum Hinzufügen eines Tisches -->
        <div
            class="add-desk-button"
            :class="{ 'sidebar-open': selectedDesk }"
            @click="startCreateMode"
        >
          <div class="add-desk-icon">+</div>
        </div>

        <!-- NEU: Edit-Button anstelle des alten Logout-Buttons -->
        <button class="edit-button" @click="goToEditPage">Gebäude/Stockwerk Verwaltung</button>

        <!-- Profil-Menü mit Dropdown -->
        <div class="profile-menu">
          <button class="profile-button" @click="toggleDropdown">
            <img src="../assets/ProfileIcon.webp" alt="Profile Icon" class="profile-icon" />
          </button>
          <!-- Dropdown-Menü -->
          <ul v-if="dropdownOpen" class="dropdown">
            <li @click="goToProfile">Profil</li>
            <!-- NEU: Logout ins Dropdown verschoben -->
            <li @click="logout">Logout</li>
          </ul>
        </div>
      </div>



    </div>
    <!-- Sidebar für Benutzerbuchungen -->
    <div v-if="userBookingsSidebarOpen" class="user-bookings-sidebar">



      <h2>Meine Buchungen</h2>

      <ul>
        <!-- Iteriere über die Buchungen -->
        <li v-for="booking in bookingsWithWeekday" :key="booking.bookingId">
          <!-- Wochentag als Überschrift -->
          <h3>{{ booking.weekday }}</h3>
          <p>
            <strong>{{ booking.desk.name }}</strong> am {{ booking.bookedDate }}
          </p>
          <div class="actions">
            <button class="cancel-button" @click="cancelUserBooking(booking.bookingId)">
              Stornieren
            </button>
            <button class="confirm-button" @click="jumpToDesk(booking.desk.id)">
              Springen
            </button>
          </div>
        </li>
      </ul>


      <!-- Button zum Schließen der linken Sidebar -->
      <button class="close-user-bookings" @click="toggleUserBookingsSidebar">
        &triangleleft;
      </button>
    </div>
    <!-- Sidebar für Tischdetails -->
    <div v-if="selectedDesk" class="sidebar">
      <button class="close-sidebar-button" @click="closeSidebar">
        &triangleright; <!-- das Zeichen '>' -->
      </button>
      <button class="button-delete" @click="deleteDesk(selectedDesk.id)">
        <svg viewBox="0 0 448 512" class="svgIcon">
          <path d="M135.2 17.7L128 32H32C14.3 32 0 46.3 0 64S14.3 96 32 96H416c17.7 0 32-14.3 32-32s-14.3-32-32-32H320l-7.2-14.3C307.4 6.8 296.3 0 284.2 0H163.8c-12.1 0-23.2 6.8-28.6 17.7zM416 128H32L53.2 467c1.6 25.3 22.6 45 47.9 45H346.9c25.3 0 46.3-19.7 47.9-45L416 128z"></path>
        </svg>
      </button>
      <div class="sidebar-content">
        <!-- Name des Tisches + Pencil-Icon -->
        <div class="inline-field">
          <div class="row">
            <!-- Überschrift (nur anzeigen wenn !editingDesk) -->
            <h2 v-if="!editingDesk">{{ selectedDesk.name }}</h2>

            <!-- Falls editingDesk => <input> -->
            <input
                v-else
                type="text"
                v-model="editedDeskData.name"
                @keyup.enter="saveDeskChanges"
                class="desk-edit-input"
            />

            <!-- Icon-Button, der Bearbeiten-Mode toggelt -->
            <button class="edit-icon-button" @click="toggleEditMode">
              &#9998;  <!-- oder <img src="...stift.png"> -->
            </button>
          </div>
        </div>

        <!-- Beschreibung des Tisches + Pencil-Icon -->
        <div class="inline-field">
          <div class="row">
            <!-- Wenn NICHT edit => <p> -->
            <p v-if="!editingDesk">{{ selectedDesk.description }}</p>

            <!-- Wenn edit => <textarea> -->
            <textarea
                v-else
                v-model="editedDeskData.description"
                @keyup.enter="saveDeskChanges"
                class="desk-edit-textarea"
            ></textarea>

            <!-- Derselbe Edit-Button, kann man wiederverwenden -->
            <button class="edit-icon-button" @click="toggleEditMode">
              &#9998;
            </button>
          </div>
        </div>

        <!-- Buttons zum Speichern oder Abbrechen (nur im Edit-Mode) -->
        <div v-if="editingDesk" class="inline-actions">
          <button class="confirm-button" @click="saveDeskChanges">Speichern</button>
          <button class="cancel-button" @click="cancelEditingDesk">Abbrechen</button>
        </div>

        <!-- Kalender für Buchungen -->
        <v-calendar
            v-model="selectedDate"
            :min-date="minDate"
            :inline="true"
            :disabled-dates="disabledDates"
            :day-class="getDayClass"
            :language="locale"
            @dayclick="onDayClick"
            :input-props="{ placeholder: 'Datum auswählen' }"
        />


        <div v-if="selectedDate" class="booking-controls">
          <p>Ausgewähltes Datum: {{ formattedSelectedDate }}</p>

          <!-- Nur anzeigen, wenn bereits gebucht -->
          <p v-if="selectedBooking">
            Dieser Tisch ist bereits gebucht von {{ selectedBooking.username }}.
          </p>

          <div class="buttons">
            <button class="cancel-button" @click="cancelBooking" :disabled="!selectedBooking">
              Stornieren
            </button>
            <button
                class="confirm-button"
                @click="bookSelectedDate"
                :disabled="isBookedDay(selectedDate) || selectedBooking"
            >
              Buchen
            </button>
          </div>
        </div>
      </div>


    </div>


    <div class="canvas-container">
      <!-- Canvas für den Grundriss -->
      <canvas
          ref="canvas"
          @mousedown="startDrag"
          @mouseup="stopDrag"
          @mousemove="onDrag"
          @click="handleCanvasClick"
          @wheel="onZoom"
      ></canvas>
    </div>

    <div v-if="createMode" class="create-overlay">
      <p>Wähle einen Ort für den neuen Tisch</p>
    </div>
    <!-- Buttons unten rechts im Create-Modus -->
    <div v-if="createMode && deskMarker" class="action-buttons">
      <button class="cancel-button" @click="cancelAddingDesk">Abbrechen</button>
      <button class="confirm-button" @click="openAddDeskForm">Auswählen</button>
    </div>

    <!-- Formular für Tisch hinzufügen -->
    <div v-if="addingDesk" class="form-overlay">
      <form @submit.prevent="createDesk">
        <h2>Neuen Tisch hinzufügen</h2>
        <label>
          Name:
          <input type="text" v-model="newDesk.name" required />
        </label>
        <label>
          Beschreibung:
          <textarea v-model="newDesk.description" required></textarea>
        </label>
        <div class="form-buttons">
          <button
              type="button"
              class="cancel-button"
              @click="cancelAddingDesk"
          >
            Abbrechen
          </button>
          <button type="submit" class="confirm-button">Tisch erstellen</button>
        </div>
      </form>
    </div>

  </div>
</template>

<script>
import axios from "axios";
import { format } from "date-fns";

export default {
  name: "DeskList",
  data() {
    return {
      buildings: [],
      floors: [],
      selectedBuildingId: "",
      selectedFloorId: "",
      desks: [],
      floorPlanImage: null,
      selectedDesk: null,
      editingDesk: false, // Ob wir gerade im Bearbeitungsmodus sind
      editedDeskData: {    // Lokale Kopie der Desk-Daten
        name: "",
        description: "",
      },
      bookings: [],
      selectedBooking: null, // <-- Neu: hier speichern wir die Buchung, falls Tag belegt
      userBookings: [],
      userBookingsSidebarOpen: false,
      selectedDateFilter: null,
      canvasContext: null,
      floorPlan: null,
      dropdownOpen: false,
      scale: 1,
      offsetX: 0,
      offsetY: 0,
      isDragging: false,
      dragStartX: 0,
      dragStartY: 0,
      addingDesk: false,
      newDesk: { name: "", description: "", x: 0, y: 0 },
      createMode: false,
      deskMarker: null,
      // Daten für den Kalender
      selectedDate: null,
      minDate: new Date(),
      bookedAttributes: [],
      disabledDates: [],
      isTodayAvailable: false,
      locale: {
        // Einstellungen für die Lokalisierung
        id: "de", // Deutsch
        firstDayOfWeek: 2, // Woche beginnt am Montag
        masks: {
          L: "DD.MM.YYYY",
        },
        dayNames: [
          "Sonntag",
          "Montag",
          "Dienstag",
          "Mittwoch",
          "Donnerstag",
          "Freitag",
          "Samstag",
        ],
        dayNamesShort: ["So", "Mo", "Di", "Mi", "Do", "Fr", "Sa"],
        monthNames: [
          "Januar",
          "Februar",
          "März",
          "April",
          "Mai",
          "Juni",
          "Juli",
          "August",
          "September",
          "Oktober",
          "November",
          "Dezember",
        ],
        monthNamesShort: [
          "Jan",
          "Feb",
          "Mär",
          "Apr",
          "Mai",
          "Jun",
          "Jul",
          "Aug",
          "Sep",
          "Okt",
          "Nov",
          "Dez",
        ],
      },
    };
  },
  computed: {
    formattedSelectedDate() {
      return this.selectedDate ? format(this.selectedDate, "dd.MM.yyyy") : "";
    },
    bookingsWithWeekday() {
      const weekdays = new Intl.DateTimeFormat("de-DE", { weekday: "long" });

      // Sortiere die Buchungen nach Datum
      const sortedBookings = [...this.userBookings].sort(
          (a, b) => new Date(a.bookedDate) - new Date(b.bookedDate)
      );

      // Füge jedem Eintrag den Wochentag hinzu
      return sortedBookings.map((booking) => ({
        ...booking,
        weekday: weekdays.format(new Date(booking.bookedDate)),
      }));
    },
  },
  methods: {
    getDayClass({ date }) {
      // Wenn der Tag in disabledDates enthalten ist, bekommt er eine Klasse
      if (this.isBookedDay(date)) {
        return "booked-day";
      }
      return "";
    },
    isBookedDay(date) {
      if (!date || !Array.isArray(this.disabledDates)) return false;
      return this.disabledDates.some(
          (bookedDate) => bookedDate.toDateString() === date.toDateString()
      );
    },
    startCreateMode() {
      this.createMode = true; // Aktiviert den Create-Modus
      this.deskMarker = null; // Entfernt vorherige Marker
    },
    handleCanvasClick(event) {
      const canvas = this.$refs.canvas;
      const rect = canvas.getBoundingClientRect();
      const clickX = (event.clientX - rect.left - this.offsetX) / this.scale;
      const clickY = (event.clientY - rect.top - this.offsetY) / this.scale;

      if (this.createMode) {
        this.deskMarker = { x: clickX, y: clickY }; // Setzt den Marker
        this.drawCanvas(); // Zeichnet den Marker neu
      } else {
        const clickedDesk = this.desks.find(
            (desk) => Math.hypot(desk.x - clickX, desk.y - clickY) <= 18
        );
        if (clickedDesk) {
          this.selectedDesk = clickedDesk;
          this.fetchDeskAvailability(clickedDesk.id).then(() => {
            // 2) Datum setzen
            // Falls selectedDateFilter existiert => verwende es, sonst "heute"
            const dateToSelect = this.selectedDateFilter
                ? new Date(this.selectedDateFilter)
                : new Date(); // Default: heute

            this.selectedDate = dateToSelect;

            // 3) Prüfen, ob es für dieses Datum eine Buchung gibt
            const booking = this.bookings.find((b) => {
              return new Date(b.bookedDate).toDateString() === dateToSelect.toDateString();
            });
            this.selectedBooking = booking || null;

            // 4) Neu zeichnen (falls gewünscht)
            this.drawCanvas();
          })
              .catch((err) => {
                console.error(err);
              });
        }
      }
    },
    async cancelBooking() {
      if(!this.selectedBooking) return;

      try {
        await axios.delete(`/api/user/${this.selectedBooking.bookingId}`, {
          headers: {Authorization: `Bearer ${localStorage.getItem("token")}`},
        });

        alert("Buchung storniert!");
        this.selectedBooking = null;
        this.selectedDate = null;
        await this.fetchDesks();
        await this.fetchDeskAvailability(this.selectedDesk.id);
        this.drawCanvas();
      } catch (error) {
        console.error("Fehler beim Stornieren der Buchung:", error);
        alert(error.message || "Stornierung fehlgeschlagen.");
      }

    },
    toggleUserBookingsSidebar() {
      this.userBookingsSidebarOpen = !this.userBookingsSidebarOpen;
      if (this.userBookingsSidebarOpen) {
        // Beim Öffnen Buchungen laden
        this.fetchUserBookings();
      }
    },

    // Buchungen des angemeldeten Nutzers abrufen
    async fetchUserBookings() {
      try {
        const response = await axios.get("/api/user/bookings", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        // response.data = z.B. ein Array aus BookingDTO
        // bookingId, deskId, deskName, bookedDate, ...
        this.userBookings = response.data;
        this.userBookings.sort((a, b) => new Date(a.bookedDate) - new Date(b.bookedDate));
      } catch (error) {
        console.error("Fehler beim Laden der User-Buchungen:", error);
      }
    },

    // Buchung stornieren
    async cancelUserBooking(bookingId) {
      try {
        await axios.delete(`/api/user/${bookingId}`, {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        alert("Buchung erfolgreich storniert!");
        // Liste aktualisieren
        await this.fetchUserBookings();
        // Tische/Canvas updaten
        await this.fetchDesks();
        await this.drawCanvas();
      } catch (error) {
        console.error("Fehler beim Stornieren:", error);
      }
    },

    // Zu einem Tisch springen
    async jumpToDesk(deskId) {
      const desk = this.desks.find((d) => d.id === deskId);
      if (!desk) {
        alert("Tisch nicht gefunden - ggf. anderes Stockwerk?");
        return;
      }

      const startScale = this.scale;
      const endScale = this.scale * 2;  // 200% mehr

      // Start offset
      const startOffsetX = this.offsetX;
      const startOffsetY = this.offsetY;

      // Wir ermitteln den offset, den wir bei endScale bräuchten
      // um den Desk in die Bildschirmmitte zu platzieren
      // Hier der Trick: Wir tun erst so, als wären wir bei endScale,
      // um die *theoretische* offset zu berechnen.
      const tempOffsetX = -desk.x * endScale + window.innerWidth / 2;
      const tempOffsetY = -desk.y * endScale + window.innerHeight / 2;

      // Animation: ~30 Frames in 300ms (10ms pro Frame)
      const frames = 50;
      let currentFrame = 0;

      const animate = () => {
        currentFrame++;
        // interpolierter Fortschritt 0..1
        const progress = currentFrame / frames;

        // Zoom interpolation
        this.scale = startScale + (endScale - startScale) * progress;

        // Offset interpolation
        this.offsetX = startOffsetX + (tempOffsetX - startOffsetX) * progress;
        this.offsetY = startOffsetY + (tempOffsetY - startOffsetY) * progress;

        // neu zeichnen
        this.drawCanvas();

        if (currentFrame < frames) {
          requestAnimationFrame(animate);
        }
      };

      requestAnimationFrame(animate);
    },
    drawCanvas() {
      const context = this.canvasContext;

      context.clearRect(0, 0, context.canvas.width, context.canvas.height);
      context.save();
      context.translate(this.offsetX, this.offsetY);
      context.scale(this.scale, this.scale);
      context.drawImage(this.floorPlan, 0, 0);

      this.desks.forEach((desk) => {
        context.beginPath();
        context.arc(desk.x, desk.y, 18, 0, 2 * Math.PI);

        if (!desk.available) {
          if (desk.profilePicture) {
            const img = new Image();
            img.src = `data:image/jpeg;base64,${desk.profilePicture}`;

            // Synchronisiertes Zeichnen
            img.onload = () => {
              // Um sicherzustellen, dass das Bild korrekt gezeichnet wird, speichern und restaurieren wir den Kontext hier.
              this.canvasContext.save();
              this.canvasContext.translate(this.offsetX, this.offsetY);
              this.canvasContext.scale(this.scale, this.scale);

              context.beginPath();
              context.arc(desk.x, desk.y, 18, 0, 2 * Math.PI);
              context.clip(); // Clip für den Kreis
              context.drawImage(img, desk.x - 18, desk.y - 18, 36, 36); // Profilbild zeichnen
              context.restore(); // Kontext zurücksetzen
            };
          } else if (desk.username) {
            // Initialen des Benutzers
            const initials = desk.username
                .split(" ")
                .map((name) => name[0])
                .join("")
                .toUpperCase();

            context.fillStyle = "#f05f5f"; // Hintergrundfarbe für Initialen
            context.beginPath();
            context.arc(desk.x, desk.y, 18, 0, 2 * Math.PI);
            context.fill();

            context.fillStyle = "#000000"; // Textfarbe
            context.font = "14px Arial";
            context.textAlign = "center";
            context.textBaseline = "middle";
            context.fillText(initials, desk.x, desk.y); // Initialen zeichnen
          } else {
            context.fillStyle = "#f02f2f";
          }
        } else {
          // Verfügbarer Tisch (türkis)
          context.fillStyle = "#40E0D0"; // Türkis
          context.fill();
        }
        context.stroke();
      });

      if (this.deskMarker) {
        context.beginPath();
        context.arc(this.deskMarker.x, this.deskMarker.y, 10, 0, 2 * Math.PI);
        context.fillStyle = "blue";
        context.fill();
        context.stroke();
      }

      context.restore();
    },
    // *** Neuer Endpunkt, der Booking-Infos liefert (mit username, bookedDate etc.)
    async fetchDeskAvailability(deskId) {
      try {
        const response = await axios.get(`/api/desks/${deskId}/bookings`, {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });

        this.bookings = response.data;

        this.bookedAttributes = this.bookings.map((booking) => ({
          key: "booked-date",
          dates: new Date(booking.bookedDate),
          highlight: {
            backgroundColor: "#dc3545",
            borderColor: "#dc3545",
          },
          popover: {
            label: `Bereits gebucht von ${booking.username}`,
          },
        }));

        this.disabledDates = this.bookings.map(
            (booking) => new Date(booking.bookedDate)
        );
      } catch (error) {
        console.error("Fehler beim Abrufen der Verfügbarkeit:", error);
      }
    },
    async deleteDesk(deskId) {
      try {
        if (confirm("Möchten Sie diesen Tisch wirklich löschen?")) {
          await axios.delete(`/api/desks/${deskId}`, {
            headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
          });
          alert("Tisch erfolgreich gelöscht!");
          await this.fetchDesks();
          this.closeSidebar();
          this.drawCanvas();

        }
      } catch (error) {
        alert("Fehler beim Löschen des Tisches!");
        console.error(error);
      }
    },
    async bookSelectedDate() {
      try {
        if (!this.selectedDate) {
          throw new Error("Kein Datum ausgewählt!");
        }

        const payload = {
          deskId: this.selectedDesk.id,
          bookedDate: format(this.selectedDate, "yyyy-MM-dd"),
        };

        await axios.post(`/api/user/BookDesk`, payload, {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });

        alert("Buchung erfolgreich!");
        // Verfügbarkeit neu laden, um den Kalender zu aktualisieren
        await this.fetchDeskAvailability(this.selectedDesk.id);
        this.selectedDate = null;
        // Tische neu laden, damit die Canvas-Farbe aktualisiert wird
        await this.fetchDesks();
        this.drawCanvas();
      } catch (error) {
        console.error("Fehler beim Buchen:", error.message);
        alert(error.message || "Ein unerwarteter Fehler ist aufgetreten.");
      }
    },
    logout() {
      // Token aus dem localStorage entfernen
      localStorage.removeItem("token");

      // Zur Login-Seite weiterleiten
      window.location.href = "http://localhost:8081/login";
    },
    toggleDropdown() {
      this.dropdownOpen = !this.dropdownOpen;
    },
    goToProfile() {
      this.$router.push({ path: "/profile" }); // Profil-Seite
    },
    goToEditPage() {
      this.$router.push({ path: "/edit" }); // Edit-Seite
    },
    // *** Anstelle von alert => selectedBooking wird gesetzt
    onDayClick(day) {
      // Speichere das ausgewählte Datum
      this.selectedDate = day.date;

      // Prüfen, ob es für diesen Tag bereits eine Buchung gibt
      const booking = this.bookings.find((b) => {
        return (
            new Date(b.bookedDate).toDateString() === day.date.toDateString()
        );
      });

      if (booking) {
        // Tag ist bereits gebucht => Merken wir in selectedBooking
        this.selectedBooking = booking;
      } else {
        // Tag ist frei => setze selectedBooking auf null
        this.selectedBooking = null;
      }
    },
    openAddDeskForm() {
      this.addingDesk = true;
      this.createMode = false;
      this.newDesk.x = this.deskMarker.x;
      this.newDesk.y = this.deskMarker.y;
    },
    cancelAddingDesk() {
      this.createMode = false;
      this.addingDesk = false;
      this.deskMarker = null;
      this.newDesk = { name: "", description: "", x: 0, y: 0 };
      this.drawCanvas();
    },
    closeSidebar() {
      this.selectedDesk = null;
      this.selectedDate = null;
      this.selectedBooking = null; // Sidebar schließen => reset
      this.editingDesk = false;
      this.editedDeskData = {
        name: "",
        description: "",
      };
      this.drawCanvas();
    },
    startEditingDesk() {
      this.editingDesk = true;
      if (this.selectedDesk) {
        this.editedDeskData.name = this.selectedDesk.name;
        this.editedDeskData.description = this.selectedDesk.description;
      }
    },
    toggleEditMode() {
      if (!this.editingDesk) {
        // Bearbeiten starten => Kopie anlegen
        this.editingDesk = true;
        this.editedDeskData.name = this.selectedDesk.name;
        this.editedDeskData.description = this.selectedDesk.description;
      } else {
        // Already editing => evtl. beenden?
        // Oder du lässt es einfach als "nichts tun"
      }
    },
    // 2) Abbrechen: Setze editingDesk auf false, ohne zu speichern
    cancelEditingDesk() {
      this.editingDesk = false;
    },

    // 3) Speichern: PUT-Request an dein Backend
    async saveDeskChanges() {
      if (!this.selectedDesk) return;
      const deskId = this.selectedDesk.id;

      try {
        // Beispiel-Endpunkt: PUT /api/desks/{deskId}
        const payload = {
          name: this.editedDeskData.name,
          description: this.editedDeskData.description
          // ggf. mehr Felder ...
        };

        const response = await axios.put(
            `/api/desks/${deskId}`,
            payload,
            {
              headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
            }
        );

        // Aktualisiere selectedDesk
        this.selectedDesk.name = response.data.name;
        this.selectedDesk.description = response.data.description;

        // Bearbeitungsmodus verlassen
        this.editingDesk = false;

        // Tische neu laden + Canvas aktualisieren (falls gewünscht)
        await this.fetchDesks();
        this.drawCanvas();

      } catch (error) {
        console.error("Fehler beim Speichern:", error);
        alert("Speichern fehlgeschlagen!");
      }
    },
    async createDesk() {
      try {
        const response = await axios.post(
            "http://localhost:8080/api/desks",
            {
              ...this.newDesk,
              floor: { id: this.$route.params.floorId },
            },
            {
              headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`,
              },
            }
        );
        console.log(response.data);
        this.desks.push(response.data);
        this.cancelAddingDesk();
        this.drawCanvas();
      } catch (error) {
        console.error("Fehler beim Erstellen des Tisches:", error);
      }
    },
    async fetchBuildings() {
      try {
        const response = await axios.get("/api/buildings", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        this.buildings = response.data; // [{id, name, ...}, ...]
      } catch (error) {
        console.error("Fehler beim Laden der Gebäude:", error);
      }
    },
    async fetchFloorsByBuilding(buildingId) {
      try {
        const response = await axios.get("/api/floors", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
          params: { buildingId }, // => /api/floors?buildingId=...
        });
        this.floors = response.data; // [{id, name, ...}, ...]
      } catch (error) {
        console.error("Fehler beim Laden der Stockwerke:", error);
      }
    },
    onBuildingChange() {
      // Reset selectedFloorId, laden neue Floors
      if (!this.selectedBuildingId) {
        this.floors = [];
        this.selectedFloorId = "";
        return;
      }
      this.fetchFloorsByBuilding(this.selectedBuildingId).then(() => {
        // Falls localStorage-Wert nicht mehr existiert, bleiben wir leer
        if (!this.floors.find((f) => f.id == this.selectedFloorId)) {
          this.selectedFloorId = "";
        }
      });
    },
    applySelection() {
      if (!this.selectedBuildingId || !this.selectedFloorId) {
        alert("Bitte Gebäude und Stockwerk auswählen!");
        return;
      }
      // Im localStorage speichern
      localStorage.setItem("lastBuildingId", this.selectedBuildingId);
      localStorage.setItem("lastFloorId", this.selectedFloorId);
      // Rufe fetchFloorDetails für dieses Stockwerk
      // oder wechsle die Route, damit floorId -> fetchDesks() und fetchFloorDetails()
      this.$router.push({
        name: "DeskList",
        params: { floorId: this.selectedFloorId }
      }).then(() => {
        // Jetzt ist die Route aktualisiert
        console.log("Route aktualisiert:", this.$route.params.floorId);
        this.fetchFloorDetails();
        this.fetchDesks();
      });
    },
    async fetchFloorDetails() {
      const floorId = this.$route.params.floorId;
      try {
        const response = await axios.get(`/api/floors/${floorId}`, {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        this.floorPlanImage = response.data.floorPlanImage;
        this.loadCanvas();
      } catch (error) {
        console.error("Fehler beim Laden des Grundrisses:", error);
      }
    },
    async fetchDesks() {
      console.log("12341414132412412")
      const floorId = this.$route.params.floorId;
      const date = this.selectedDateFilter
          ? new Date(this.selectedDateFilter).toISOString().split("T")[0]
          : new Date().toISOString().split("T")[0];
      try {
        const response = await axios.get(
            `http://localhost:8080/api/desks?floorId=${floorId}&date=${date}`,
            {
              headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
            }
        );
        this.desks = response.data;
      } catch (error) {
        console.error("Fehler beim Laden der Tische:", error);
      }
    },
    loadCanvas() {
      const canvas = this.$refs.canvas;
      const context = canvas.getContext("2d");
      this.canvasContext = context;

      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;

      const image = new Image();
      image.src = this.floorPlanImage;
      image.onload = () => {
        this.floorPlan = image;
        this.drawCanvas();
      };
    },
    onZoom(event) {
      event.preventDefault();
      const zoomFactor = 0.1;       // Schrittweite beim Zoomen
      const prevScale = this.scale;

      // Neuen Scale berechnen
      let newScale = this.scale + (event.deltaY < 0 ? zoomFactor : -zoomFactor);

      // Clamping: Grenzen setzen, damit nicht zu stark gezoomt wird
      if (newScale < 0.5) newScale = 0.5;
      if (newScale > 2) newScale = 2;

      // Canvas- und Mausposition ermitteln (für den "Zoom-Anchor")
      const canvas = this.$refs.canvas;
      const rect = canvas.getBoundingClientRect();
      const mouseX = event.clientX - rect.left;
      const mouseY = event.clientY - rect.top;

      // Zoom-Ratio (neue Skala / alte Skala)
      const ratio = newScale / prevScale;

      // Offset so anpassen, dass zum Mauszeiger hin gezoomt wird
      this.offsetX = mouseX - ratio * (mouseX - this.offsetX);
      this.offsetY = mouseY - ratio * (mouseY - this.offsetY);

      // Scale übernehmen
      this.scale = newScale;

      // -------------------------------------------
      // 1) Bild ggf. zentrieren, wenn es jetzt kleiner als die Canvas ist
      // -------------------------------------------
      const scaledWidth = this.floorPlan.width * this.scale;
      const scaledHeight = this.floorPlan.height * this.scale;

      // Falls die Breite des Bildes kleiner als die Canvas-Breite ist -> horizontal zentrieren
      if (scaledWidth < canvas.width) {
        this.offsetX = (canvas.width - scaledWidth) / 2;
      }

      // Falls die Höhe des Bildes kleiner als die Canvas-Höhe ist -> vertikal zentrieren
      if (scaledHeight < canvas.height) {
        this.offsetY = (canvas.height - scaledHeight) / 2;
      }

      if (scaledWidth >= canvas.width) {
        // Linken Rand und rechten Rand "klemmen"
        const maxOffsetX = 0; // oder wie du es definieren möchtest
        const minOffsetX = canvas.width - scaledWidth;
        this.offsetX = Math.min(maxOffsetX, Math.max(minOffsetX, this.offsetX));
      }
      if (scaledHeight >= canvas.height) {
        const maxOffsetY = 0;
        const minOffsetY = canvas.height - scaledHeight;
        this.offsetY = Math.min(maxOffsetY, Math.max(minOffsetY, this.offsetY));
      }


      // Neu zeichnen
      this.drawCanvas();
    },
    startDrag(event) {
      this.isDragging = true;
      this.dragStartX = event.clientX;
      this.dragStartY = event.clientY;
    },
    stopDrag() {
      this.isDragging = false;
    },
    onDrag(event) {
      if (this.isDragging) {
        this.offsetX += event.clientX - this.dragStartX;
        this.offsetY += event.clientY - this.dragStartY;
        this.dragStartX = event.clientX;
        this.dragStartY = event.clientY;

        this.drawCanvas();
      }
    },
  },
  created() {
    this.fetchFloorDetails();
    this.fetchDesks();
    this.fetchBuildings();
  },
};
</script>

<style scoped>
/*
=========================================
  CANVAS & ALLGEMEINE STYLES
=========================================
*/
.canvas-container {
  position: relative;
  background-color: #f8f9fa; /* Helles Grau */
  border: 1px solid #dee2e6; /* Einheitliches Hellgrau */
  border-radius: 8px;
  overflow: hidden;
  max-height: 87vh;
}

/* Create-Overlay */
.create-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 20px 30px;
  border-radius: 8px;
  font-size: 1.2rem;
  text-align: center;
  z-index: 1000;
}

/* Button für Buchungsübersicht (oben links) */
.user-bookings-button {
  background-color: #6c757d;  /* neutral-grau */
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 10px 20px;
  font-size: 1rem;
  cursor: pointer;
  box-shadow: 0 2px 5px rgba(0,0,0,0.15);
  transition: background-color 0.3s ease, transform 0.3s ease;
}
.user-bookings-button:hover {
  background-color: #5a6268; /* dunkleres Grau */
  transform: scale(1.05);
}

/*
=========================================
  RECHTE SIDEBAR (Tischdetails)
=========================================
*/
.sidebar {
  position: fixed;
  right: 0;
  top: 0;
  width: 350px;
  height: 100%;
  background-color: #fff;
  border-left: 1px solid #dee2e6;
  box-shadow: -2px 0 5px rgba(0, 0, 0, 0.1);
  padding: 20px 15px;
  overflow-y: auto;
  z-index: 1000;
  font-family: Arial, sans-serif;
  display: flex;
  flex-direction: column; /* Elemente vertikal anordnen */
  align-items: center;    /* Horizontal zentrieren */
}

.sidebar h2 {
  font-size: 1.5rem;
  margin-bottom: 10px;
  color: #343a40;
  margin-left: 5px;
}

.sidebar p {
  font-size: 1rem;
  color: #6c757d;
  margin-bottom: 15px;
  margin-left: 5px;
}

/* Schließen-Button (rechte Sidebar) */
.sidebar .close-sidebar-button {
  position: absolute;
  top: 0px;
  left: 0px;
  width: 35px;
  height: 35px;
  background-color: #6c757d; /* neutral-Grau */
  border: none;
  border-radius: 4px;
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}
.sidebar .close-sidebar-button:hover {
  background-color: #5a6268;
}

/* Kalender-Styling */
.vc-day.today .vc-day-content {
  border: 1px solid #007bff;
}
.vc-day.selected .vc-day-content {
  background-color: #007bff !important;
  color: #fff !important;
}

/* Sidebar-Buttons (z.B. Buchen/Stornieren in der Sidebar) */
.sidebar .sidebar-btn {
  width: 100%;
  padding: 10px;
  background-color: #6c757d;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  margin-bottom: 10px;
  transition: background-color 0.3s ease;
}
.sidebar .sidebar-btn:hover {
  background-color: #5a6268;
}

/*
=========================================
  RUNDER + BUTTONS (z.B. zum Tisch hinzufügen)
=========================================
*/
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
}
.add-desk-button:hover {
  background-color: #0056b3;
  transform: scale(1.1);
}
.add-desk-icon {
  font-size: 2rem;
  color: #fff;
  font-weight: bold;
}

/* Buttons unten rechts bei Create-Modus */
.action-buttons {
  position: absolute;
  bottom: 20px;
  right: 20px;
  display: flex;
  gap: 10px;
}
.action-buttons button {
  padding: 10px 20px;
  font-size: 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

/*
=========================================
  FORM OVERLAY (Neuen Tisch hinzufügen)
=========================================
*/
.form-overlay {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: #fff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  border-radius: 8px;
  padding: 30px;
  z-index: 1100;
  width: 400px;
  max-width: 90%;
}
.form-overlay form h2 {
  margin-bottom: 20px;
  font-size: 1.5rem;
  color: #343a40;
}
.form-overlay label {
  display: block;
  margin-bottom: 15px;
  font-size: 1rem;
  color: #343a40;
}
.form-overlay input,
.form-overlay textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 1rem;
  margin-top: 5px;
}
/* Buttons im Overlay */
.form-overlay .form-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
  gap: 10px;
}
.form-overlay .form-buttons button {
  flex: 1;
  padding: 10px 20px;
  font-size: 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

/*
=========================================
  BOOKING CONTROLS (Datum etc.)
=========================================
*/
.booking-controls,
.today-booking {
  margin-top: 20px;
}
.booking-controls p {
  font-size: 1rem;
  margin-bottom: 10px;
}
.booking-controls .buttons {
  display: flex;
  gap: 10px;
}
.booking-controls .buttons button {
  flex: 1;
  padding: 10px;
  font-size: 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

/* Primary / Danger-Button */
.confirm-button {
  background-color: #007bff;
  color: #fff;
}
.confirm-button:hover {
  background-color: #0056b3;
}
.cancel-button {
  background-color: #dc3545;
  color: #fff;
}
.cancel-button:hover {
  background-color: #c82333;
}

/*
=========================================
  RESPONSIVE (mobil)
=========================================
*/
@media (max-width: 768px) {
  .sidebar {
    width: 100%;
    position: static;
    height: auto;
  }
  .vc-day {
    font-size: 0.8rem;
  }
  .vc-day .content {
    min-height: 40px;
  }
}

/*
=========================================
  LINKER SIDEBAR (Meine Buchungen)
=========================================
*/
.user-bookings-sidebar {
  position: fixed;
  left: 0;
  top: 0;
  width: 400px;
  height: 100%;
  background-color: #ffffff;
  border-right: 1px solid #dee2e6;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
  padding: 20px 15px;
  overflow-y: auto;
  z-index: 1100;
  font-family: Arial, sans-serif;
}
.user-bookings-sidebar h2 {
  font-size: 1.5rem;
  margin-bottom: 20px;
  color: #343a40;
}
.user-bookings-sidebar ul {
  list-style: none;
  padding: 0;
  margin: 0;
}
.user-bookings-sidebar li {
  margin-bottom: 15px;
  padding: 10px;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  background-color: #f8f9fa;
}
.user-bookings-sidebar li p {
  margin: 0 0 8px 0;
  color: #495057;
  font-size: 1rem;
}
.user-bookings-sidebar li strong {
  color: #343a40;
}

/* Aktionen (Stornieren/Springen) nebeneinander */
.user-bookings-sidebar .actions {
  display: flex;
  gap: 8px;
}

/* Schließen-Button in der linken Sidebar (oben rechts innerhalb der Sidebar) */
.close-user-bookings {
  position: absolute;
  top: 0px;
  right: 0px;
  width: 35px;
  height: 35px;
  background-color: #6c757d;
  border: none;
  border-radius: 4px;
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.3s ease;
}
.close-user-bookings:hover {
  background-color: #5a6268;
}
.user-bookings-sidebar .actions {
  display: flex;
  gap: 8px;
}

/* Allgemeiner Button-Stil in der linken Sidebar:
   pill-förmig, kleiner Font, leichte Hover-Animation */
.user-bookings-sidebar .actions button {
  padding: 8px 16px;
  border: none;
  border-radius: 20px; /* pill-shape */
  font-size: 0.9rem;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.3s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

/* Leichter Hover-Effekt: minimal vergrößern */
.user-bookings-sidebar .actions button:hover {
  transform: scale(1.03);
}

/* Spezifische Farben für die Cancel- und Confirm-Buttons */
.user-bookings-sidebar .actions .cancel-button {
  background-color: #dc3545; /* Danger-Rot */
  color: #fff;
}
.user-bookings-sidebar .actions .cancel-button:hover {
  background-color: #c82333;
}

.user-bookings-sidebar .actions .confirm-button {
  background-color: #007bff; /* Blau */
  color: #fff;
}
.user-bookings-sidebar .actions .confirm-button:hover {
  background-color: #0056b3;
}

/*
=========================================
  INLINE-EDITING (Name & Beschreibung)
=========================================
*/
.inline-field {
  margin-bottom: 15px;
}
.inline-field h2,
.inline-field p {
  margin: 0;
  cursor: pointer;
  color: #343a40;
}

/* Input/Textarea für Inline-Editing */
.desk-edit-input,
.desk-edit-textarea {
  width: 100%;
  border: 1px solid #dee2e6;
  border-radius: 4px;
  padding: 8px;
  margin-bottom: 10px;
  font-size: 1rem;
  font-family: inherit;
  box-sizing: border-box;
}

/* Bearbeiten-Button (Stift-Symbol oder Unicode) */
.edit-icon-button {
  background-color: transparent;
  border: none;
  color: #007bff;
  cursor: pointer;
  padding: 5px 10px;
  font-size: 1rem;
  width: auto;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.edit-icon-button:hover {
  color: #0056b3;
}

/* Row-Container für Text + Button nebeneinander */
.row {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* Aktionsbereich (Speichern/Abbrechen) */
.inline-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

/* Styles für die Gebäudewahl und Stockwerksauswahl */
.floor-selection {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 10px; /* Abstand nach oben und unten */
  padding: 18px;
}

.floor-selection label {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  font-size: 1rem;
  color: #495057; /* Dezentes Grau */
}

.floor-selection select {
  padding: 8px 12px;
  font-size: 1rem;
  color: #495057;
  border: 1px solid #ced4da; /* Rahmenfarbe */
  border-radius: 4px;
  background-color: #fff; /* Weißer Hintergrund */
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05); /* Leichter Schatten */
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.floor-selection select:focus {
  outline: none;
  border-color: #007bff; /* Blau bei Fokus */
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.5); /* Leichter blauer Schein */
}

.floor-selection button {
  padding: 10px 20px;
  font-size: 1rem;
  color: #fff;
  background-color: #007bff; /* Blau */
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.3s ease;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); /* Leichter Schatten */
}
.floor-selection button:hover {
  background-color: #0056b3; /* Dunkleres Blau bei Hover */
  transform: scale(1.05); /* Leichte Vergrößerung bei Hover */
}
.floor-selection button:active {
  background-color: #004085; /* Noch dunkleres Blau bei Klick */
  transform: scale(0.95); /* Leichte Verkleinerung bei Klick */
}
@media (max-width: 768px) {
  .floor-selection {
    flex-direction: column; /* Vertikale Anordnung auf kleinen Bildschirmen */
    gap: 10px;
  }
  .floor-selection label {
    width: 100%;
  }
  .floor-selection select {
    width: 100%; /* Breite anpassen für Mobilgeräte */
  }
  .floor-selection button {
    width: 100%; /* Breite anpassen für Mobilgeräte */
  }
}
.sidebar .sidebar-content {
  margin-top: 20px;
}

.profile-button {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.profile-icon {
  width: 41px; /* Icon passt in den Kreis */
  height: 41px;
  object-fit: contain;
  filter: brightness(0) invert(1);
}

.dropdown {
  position: absolute;
  top: 100%; /* Direkt unter der Schaltfläche */
  right: 0; /* Am rechten Rand der Profil-Schaltfläche */
  background-color: #fff;
  border: 1px solid #dee2e6;
  border-radius: 4px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  list-style: none;
  padding: 10px 0;
  margin: 0;
  z-index: 1000;
}

.dropdown li {
  padding: 8px 16px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.dropdown li:hover {
  background-color: #f8f9fa;
}

.profile-menu {
  position: relative; /* Wichtig für die absolute Positionierung des Dropdowns */
  display: inline-block;
}

.profile-button {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: none;
  border: none;
  cursor: pointer;
  border-radius: 50%;
}

.profile-button:hover {
  background-color: #f8f9fa; /* Optionaler Hover-Effekt */
}

/*der neue Style Yeah */

/* From Uiverse.io by vinodjangid07 */
.button-delete {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: rgb(20, 20, 20);
  border: none;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0px 0px 20px rgba(0, 0, 0, 0.164);
  cursor: pointer;
  transition-duration: .3s;
  overflow: hidden;
  position: absolute;
  bottom: 380px;
  right: 15px;
}

.svgIcon {
  width: 12px;
  transition-duration: .3s;
}

.svgIcon path {
  fill: white;
}

.button-delete:hover {
  width: 140px;
  border-radius: 50px;
  transition-duration: .3s;
  background-color: rgb(255, 69, 69);
  align-items: center;
}

.button-delete:hover .svgIcon {
  width: 50px;
  transition-duration: .3s;
  transform: translateY(60%);
}

.button-delete::before {
  position: absolute;
  top: -20px;
  content: "Delete";
  color: white;
  transition-duration: .3s;
  font-size: 2px;
}

.button-delete:hover::before {
  font-size: 13px;
  opacity: 1;
  transform: translateY(30px);
  transition-duration: .3s;
}

.dividivi{
  display: flex;
  align-items: center;
  gap: 12px;
}

/* Container für den Date Picker */
.date-picker {
  display: inline-block;
  position: relative;
  font-size: 1rem;
  color: #495057; /* Dezentes Grau */
}

/* Styling für das Date Input */
.date-picker input[type="date"] {
  padding: 10px 12px;
  font-size: 1rem;
  color: #495057; /* Textfarbe */
  border: 1px solid #ced4da; /* Dezenter Rand */
  border-radius: 4px; /* Runde Ecken */
  background-color: #fff; /* Hintergrund */
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); /* Leichter Schatten */
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  outline: none; /* Entfernt den Standardfokus-Rahmen */
  width: 100%; /* Optional: Kann angepasst werden */
  box-sizing: border-box; /* Padding in die Breite einbeziehen */
}

/* Hover-Effekt */
.date-picker input[type="date"]:hover {
  border-color: #007bff; /* Blau bei Hover */
}

/* Fokus-Effekt */
.date-picker input[type="date"]:focus {
  border-color: #007bff; /* Blau bei Fokus */
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.5); /* Leichter blauer Schein */
}

/* Optional: Icon hinzufügen */
.date-picker::before {
  content: "\1F4C5"; /* Kalender-Emoji als Icon */
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 1.2rem;
  color: #495057; /* Dezentes Grau */
  pointer-events: none; /* Klicks auf das Icon ignorieren */
}

.date-picker input[type="date"] {
  padding-left: 40px; /* Platz für das Icon schaffen */
}

input[type="date"],
select {
  padding: 10px 12px;
  font-size: 1rem; /* Gleiche Schriftgröße */
  color: #495057;
  border: 1px solid #ced4da; /* Gleicher Rand */
  border-radius: 4px;
  background-color: #fff;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  outline: none;
  height: auto; /* Stellt sicher, dass die Höhe dynamisch bleibt */
  box-sizing: border-box; /* Padding in Breite/Höhe einberechnen */
}

/* Hover-Effekte für beide */
input[type="date"]:hover,
select:hover {
  border-color: #007bff;
}

/* Fokus-Effekt */
input[type="date"]:focus,
select:focus {
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.5);
}

/* Einheitliche Höhe */
input[type="date"],
select {
  height: 42px; /* Angleichung der Höhe */
}

</style>