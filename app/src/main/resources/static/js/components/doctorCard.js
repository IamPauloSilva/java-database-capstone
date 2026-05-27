import { showBookingOverlay } from "../loggedPatient.js";
import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";

export function createDoctorCard(doctor) {
  const role = localStorage.getItem("userRole");
  const card = document.createElement("div");
  card.className = "doctor-card";

  const times = (doctor.availableTimes || []).map((time) => `<li>${time}</li>`).join("");
  card.innerHTML = `
    <div class="doctor-info">
      <h3>${doctor.name}</h3>
      <p><strong>Specialty:</strong> ${doctor.specialty}</p>
      <p><strong>Email:</strong> ${doctor.email}</p>
      <ul>${times}</ul>
    </div>
    <div class="doctor-actions"></div>
  `;

  const actions = card.querySelector(".doctor-actions");

  if (role === "admin") {
    const deleteBtn = document.createElement("button");
    deleteBtn.className = "dashboard-btn";
    deleteBtn.textContent = "Delete";
    deleteBtn.addEventListener("click", async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("Session expired.");
        return;
      }
      const { success, message } = await deleteDoctor(doctor.id, token);
      if (!success) {
        alert(message || "Failed to delete doctor.");
        return;
      }
      card.remove();
    });
    actions.appendChild(deleteBtn);
  } else if (role === "patient") {
    const bookBtn = document.createElement("button");
    bookBtn.className = "dashboard-btn";
    bookBtn.textContent = "Book Now";
    bookBtn.addEventListener("click", () => alert("Please login first to book an appointment."));
    actions.appendChild(bookBtn);
  } else if (role === "loggedPatient") {
    const bookBtn = document.createElement("button");
    bookBtn.className = "dashboard-btn";
    bookBtn.textContent = "Book Now";
    bookBtn.addEventListener("click", async (e) => {
      const token = localStorage.getItem("token");
      if (!token) {
        window.location.href = "/pages/patientDashboard.html";
        return;
      }
      const patient = await getPatientData(token);
      if (!patient) {
        alert("Unable to load patient profile.");
        return;
      }
      showBookingOverlay(e, doctor, patient);
    });
    actions.appendChild(bookBtn);
  }

  return card;
}
