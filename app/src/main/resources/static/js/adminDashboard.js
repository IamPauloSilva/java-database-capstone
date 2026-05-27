import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";
import { openModal } from "./components/modals.js";

const contentDiv = document.getElementById("content");

document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();
  document.getElementById("addDocBtn")?.addEventListener("click", () => openModal("addDoctor"));
});

document.getElementById("searchBar")?.addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime")?.addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpecialty")?.addEventListener("change", filterDoctorsOnChange);

async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error(error);
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
      contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
      return;
    }
    renderDoctorCards(doctors);
  } catch (error) {
    alert("Error filtering doctors.");
  }
}

function renderDoctorCards(doctors) {
  contentDiv.innerHTML = "";
  doctors.forEach((doctor) => contentDiv.appendChild(createDoctorCard(doctor)));
}

window.adminAddDoctor = async function adminAddDoctor() {
  const doctor = {
    name: document.getElementById("doctorName")?.value?.trim(),
    specialty: document.getElementById("specialization")?.value?.trim(),
    email: document.getElementById("doctorEmail")?.value?.trim(),
    password: document.getElementById("doctorPassword")?.value,
    phone: document.getElementById("doctorPhone")?.value?.trim(),
    availableTimes: Array.from(document.querySelectorAll('input[name="availability"]:checked')).map((x) => x.value)
  };

  const token = localStorage.getItem("token");
  if (!token) {
    alert("Session expired. Please login again.");
    return;
  }

  const { success, message } = await saveDoctor(doctor, token);
  if (!success) {
    alert(message || "Failed to save doctor.");
    return;
  }

  alert("Doctor saved successfully.");
  document.getElementById("modal").style.display = "none";
  loadDoctorCards();
};
