import { getDoctors, filterDoctors } from "./services/doctorServices.js";
import { openModal } from "./components/modals.js";
import { createDoctorCard } from "./components/doctorCard.js";
import { patientSignup, patientLogin } from "./services/patientServices.js";

document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();

  document.getElementById("patientSignup")?.addEventListener("click", () => openModal("patientSignup"));
  document.getElementById("patientLogin")?.addEventListener("click", () => openModal("patientLogin"));

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

window.signupPatient = async function signupPatient() {
  try {
    const data = {
      name: document.getElementById("name").value,
      email: document.getElementById("email").value,
      password: document.getElementById("password").value,
      phone: document.getElementById("phone").value,
      address: document.getElementById("address").value
    };

    const { success, message } = await patientSignup(data);
    alert(message);
    if (success) {
      document.getElementById("modal").style.display = "none";
    }
  } catch (error) {
    console.error("Signup failed:", error);
    alert("An error occurred while signing up.");
  }
};

window.loginPatient = async function loginPatient() {
  try {
    const response = await patientLogin({
      email: document.getElementById("email").value,
      password: document.getElementById("password").value
    });

    if (!response.ok) {
      alert("Invalid credentials.");
      return;
    }

    const result = await response.json();
    localStorage.setItem("token", result.token);
    selectRole("loggedPatient");
  } catch (error) {
    console.error("Patient login failed:", error);
    alert("Failed to login.");
  }
};
