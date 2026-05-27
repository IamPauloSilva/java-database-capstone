import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

const tableBody = document.getElementById("patientTableBody");
const token = localStorage.getItem("token");
const datePicker = document.getElementById("datePicker");
const todayButton = document.getElementById("todayButton");
const searchBar = document.getElementById("searchBar");

let selectedDate = new Date().toISOString().split("T")[0];
let patientName = null;

if (datePicker) {
  datePicker.value = selectedDate;
}

searchBar?.addEventListener("input", () => {
  const value = searchBar.value.trim();
  patientName = value || null;
  loadAppointments();
});

todayButton?.addEventListener("click", () => {
  selectedDate = new Date().toISOString().split("T")[0];
  if (datePicker) datePicker.value = selectedDate;
  loadAppointments();
});

datePicker?.addEventListener("change", (e) => {
  selectedDate = e.target.value;
  loadAppointments();
});

async function loadAppointments() {
  try {
    const appointments = await getAllAppointments(selectedDate, patientName, token);
    tableBody.innerHTML = "";
    if (!appointments.length) {
      tableBody.innerHTML = "<tr><td colspan='5' class='noPatientRecord'>No Appointments found for selected date.</td></tr>";
      return;
    }

    appointments.forEach((appointment) => {
      const patient = {
        id: appointment.patientId,
        name: appointment.patientName,
        phone: appointment.patientPhone,
        email: appointment.patientEmail
      };
      tableBody.appendChild(createPatientRow(patient, appointment.id, appointment.doctorId));
    });
  } catch (error) {
    tableBody.innerHTML = "<tr><td colspan='5' class='noPatientRecord'>Error loading appointments. Try again later.</td></tr>";
  }
}

document.addEventListener("DOMContentLoaded", loadAppointments);
