import { getDoctors, filterDoctors } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";
import { bookAppointment } from "./services/appointmentRecordService.js";

document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();
  document.getElementById("searchBar")?.addEventListener("input", filterDoctorsOnChange);
  document.getElementById("filterTime")?.addEventListener("change", filterDoctorsOnChange);
  document.getElementById("filterSpecialty")?.addEventListener("change", filterDoctorsOnChange);
});

function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";
  doctors.forEach((doctor) => contentDiv.appendChild(createDoctorCard(doctor)));
}

async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Failed to load doctors:", error);
  }
}

async function filterDoctorsOnChange() {
  const name = document.getElementById("searchBar")?.value?.trim() || null;
  const time = document.getElementById("filterTime")?.value || null;
  const specialty = document.getElementById("filterSpecialty")?.value || null;

  try {
    const response = await filterDoctors(name, time, specialty);
    const doctors = response.doctors || [];
    if (!doctors.length) {
      document.getElementById("content").innerHTML = "<p>No doctors found with the given filters.</p>";
      return;
    }
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Failed to filter doctors:", error);
    alert("An error occurred while filtering doctors.");
  }
}

export function showBookingOverlay(e, doctor, patient) {
  const ripple = document.createElement("div");
  ripple.classList.add("ripple-overlay");
  ripple.style.left = `${e.clientX}px`;
  ripple.style.top = `${e.clientY}px`;
  document.body.appendChild(ripple);
  setTimeout(() => ripple.classList.add("active"), 50);

  const modalApp = document.createElement("div");
  modalApp.classList.add("modalApp");
  modalApp.innerHTML = `
    <h2>Book Appointment</h2>
    <input class="input-field" type="text" value="${patient.name}" disabled />
    <input class="input-field" type="text" value="${doctor.name}" disabled />
    <input class="input-field" type="text" value="${doctor.specialty}" disabled />
    <input class="input-field" type="email" value="${doctor.email}" disabled />
    <input class="input-field" type="date" id="appointment-date" />
    <select class="input-field" id="appointment-time">
      <option value="">Select time</option>
      ${(doctor.availableTimes || []).map((t) => `<option value="${t}">${t}</option>`).join("")}
    </select>
    <button class="confirm-booking">Confirm Booking</button>
  `;
  document.body.appendChild(modalApp);
  setTimeout(() => modalApp.classList.add("active"), 300);

  modalApp.querySelector(".confirm-booking").addEventListener("click", async () => {
    const date = modalApp.querySelector("#appointment-date").value;
    const slot = modalApp.querySelector("#appointment-time").value;
    if (!date || !slot) {
      alert("Please choose date and time.");
      return;
    }

    const startTime = slot.split("-")[0];
    const token = localStorage.getItem("token");
    const appointment = {
      doctor: { id: doctor.id },
      patient: { id: patient.id },
      appointmentTime: `${date}T${startTime}:00`,
      status: 0
    };

    const { success, message } = await bookAppointment(appointment, token);
    if (success) {
      alert("Appointment booked successfully.");
      ripple.remove();
      modalApp.remove();
      return;
    }
    alert(`Failed to book appointment: ${message}`);
  });
}
