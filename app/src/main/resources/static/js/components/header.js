function logout() {
  localStorage.removeItem("userRole");
  localStorage.removeItem("token");
  window.location.href = "/";
}

function logoutPatient() {
  localStorage.removeItem("token");
  localStorage.setItem("userRole", "patient");
  window.location.href = "/pages/patientDashboard.html";
}

function renderHeader() {
  const headerDiv = document.getElementById("header");
  if (!headerDiv) return;

  if (window.location.pathname === "/") {
    localStorage.removeItem("userRole");
  }

  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  if ((role === "admin" || role === "doctor" || role === "loggedPatient") && !token) {
    localStorage.removeItem("userRole");
    window.location.href = "/";
    return;
  }

  let nav = "";
  if (role === "admin") {
    nav = `<button id="addDocBtn" class="adminBtn">Add Doctor</button><a href="#" id="logoutLink">Logout</a>`;
  } else if (role === "doctor") {
    nav = `<button class="adminBtn" id="homeDoctor">Home</button><a href="#" id="logoutLink">Logout</a>`;
  } else if (role === "patient") {
    nav = `<button id="patientLogin" class="adminBtn">Login</button><button id="patientSignup" class="adminBtn">Sign Up</button>`;
  } else if (role === "loggedPatient") {
    nav = `<button id="home" class="adminBtn">Home</button><button id="patientAppointments" class="adminBtn">Appointments</button><a href="#" id="logoutPatientLink">Logout</a>`;
  }

  headerDiv.innerHTML = `
    <header class="header">
      <div class="logo-section">
        <img src="/assets/images/logo/logo.png" alt="Hospital CMS Logo" class="logo-img">
        <span class="logo-title">Hospital CMS</span>
      </div>
      <nav>${nav}</nav>
    </header>
  `;

  document.getElementById("logoutLink")?.addEventListener("click", (e) => {
    e.preventDefault();
    logout();
  });
  document.getElementById("logoutPatientLink")?.addEventListener("click", (e) => {
    e.preventDefault();
    logoutPatient();
  });
  document.getElementById("homeDoctor")?.addEventListener("click", () => selectRole("doctor"));
  document.getElementById("home")?.addEventListener("click", () => { window.location.href = "/pages/loggedPatientDashboard.html"; });
  document.getElementById("patientAppointments")?.addEventListener("click", () => { window.location.href = "/pages/patientAppointments.html"; });
  document.getElementById("patientLogin")?.addEventListener("click", () => window.openModal?.("patientLogin"));
  document.getElementById("patientSignup")?.addEventListener("click", () => window.openModal?.("patientSignup"));
}

window.logout = logout;
window.logoutPatient = logoutPatient;
window.renderHeader = renderHeader;
renderHeader();
