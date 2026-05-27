import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";
import { getDoctors } from "./doctorServices.js";

const ADMIN_API = `${API_BASE_URL}/admin/login`;
const DOCTOR_API = `${API_BASE_URL}/doctor/login`;

window.addEventListener("load", () => {
  const adminBtn = document.getElementById("adminLogin");
  const doctorBtn = document.getElementById("doctorLogin");

  if (adminBtn) {
    adminBtn.addEventListener("click", () => openModal("adminLogin"));
  }
  if (doctorBtn) {
    doctorBtn.addEventListener("click", () => openModal("doctorLogin"));
  }
});

window.adminLoginHandler = async function adminLoginHandler() {
  try {
    const username = document.getElementById("username")?.value?.trim();
    const password = document.getElementById("password")?.value;
    const response = await fetch(ADMIN_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password })
    });
    const data = await response.json();
    if (!response.ok) {
      alert(data.message || "Invalid credentials.");
      return;
    }
    localStorage.setItem("token", data.token);
    selectRole("admin");
  } catch (error) {
    alert("Unable to login now.");
  }
};

window.doctorLoginHandler = async function doctorLoginHandler() {
  try {
    const email = document.getElementById("email")?.value?.trim();
    const password = document.getElementById("password")?.value;
    const response = await fetch(DOCTOR_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password })
    });
    const data = await response.json();
    if (!response.ok) {
      alert(data.message || "Invalid credentials.");
      return;
    }
    localStorage.setItem("token", data.token);
    const doctors = await getDoctors();
    const doctor = doctors.find((d) => d.email?.toLowerCase() === email.toLowerCase());
    if (doctor?.id) {
      localStorage.setItem("doctorId", doctor.id);
    }
    selectRole("doctor");
  } catch (error) {
    console.error(error);
    alert("Unable to login now.");
  }
};
