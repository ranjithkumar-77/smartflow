const apiBase = "http://localhost:8080";

const state = {
  token: localStorage.getItem("smartflow_token") || "",
  user: JSON.parse(localStorage.getItem("smartflow_user") || "null"),
  requests: [],
  offers: []
};

let locationDetectionAttempted = false;
let offersRefreshTimer = null;

const ui = {
  authSection: document.getElementById("authSection"),
  appSection: document.getElementById("appSection"),
  userChip: document.getElementById("userChip"),
  logoutBtn: document.getElementById("logoutBtn"),
  welcomeTitle: document.getElementById("welcomeTitle"),
  requestList: document.getElementById("requestList"),
  offersList: document.getElementById("offersList"),
  statRequests: document.getElementById("statRequests"),
  statOffers: document.getElementById("statOffers"),
  statActive: document.getElementById("statActive"),
  statCompleted: document.getElementById("statCompleted"),
  statRating: document.getElementById("statRating"),
  requestCategory: document.getElementById("requestCategory"),
  newRequestBtn: document.getElementById("newRequestBtn"),
  requestLocation: document.getElementById("requestLocation"),
  requestLatitude: document.getElementById("requestLatitude"),
  requestLongitude: document.getElementById("requestLongitude"),
  locationStatus: document.getElementById("locationStatus"),
  useCurrentLocationBtn: document.getElementById("useCurrentLocationBtn"),
  registerRole: document.getElementById("registerRole"),
  registerTechnicianSkill: document.getElementById("registerTechnicianSkill"),
  technicianSkillField: document.getElementById("technicianSkillField"),
  technicianLocationFields: document.getElementById("technicianLocationFields"),
  registerTechnicianLocation: document.getElementById("registerTechnicianLocation"),
  registerTechnicianLatitude: document.getElementById("registerTechnicianLatitude"),
  registerTechnicianLongitude: document.getElementById("registerTechnicianLongitude"),
  registerUseCurrentLocationBtn: document.getElementById("registerUseCurrentLocationBtn"),
  registerLocationStatus: document.getElementById("registerLocationStatus"),
  toast: document.getElementById("toast")
};

const navButtons = document.querySelectorAll(".nav-btn");
const authTabs = document.querySelectorAll(".tab-btn");

const renderView = (viewName) => {
  const isCustomer = state.user?.role === "CUSTOMER";
  if (viewName === "requests" && !isCustomer) {
    viewName = "dashboard";
  }

  document.querySelectorAll(".view").forEach((view) => {
    view.classList.toggle("active-view", view.id === `${viewName}View`);
  });

  navButtons.forEach((btn) => {
    btn.classList.toggle("active", btn.dataset.view === viewName);
    btn.classList.toggle("hidden", btn.dataset.view === "requests" && !isCustomer);
  });

  if (viewName === "requests" && navigator.geolocation && !locationDetectionAttempted) {
    useCurrentLocation();
    locationDetectionAttempted = true;
  }
};

const showToast = (message) => {
  ui.toast.textContent = message;
  ui.toast.classList.add("show");
  clearTimeout(showToast.timeoutId);
  showToast.timeoutId = setTimeout(() => ui.toast.classList.remove("show"), 2200);
};

const setAuthMode = (mode) => {
  const loginForm = document.getElementById("loginForm");
  const registerForm = document.getElementById("registerForm");
  loginForm.classList.toggle("active-form", mode === "login");
  registerForm.classList.toggle("active-form", mode === "register");

  authTabs.forEach((btn) => {
    btn.classList.toggle("active", btn.dataset.auth === mode);
  });
};

const setLoggedInUI = () => {
  const user = state.user;
  ui.userChip.textContent = user ? user.name : "Guest";
  ui.userChip.classList.toggle("hidden", !user);
  ui.logoutBtn.classList.toggle("hidden", !user);
  ui.authSection.classList.toggle("hidden", !!user);
  ui.appSection.classList.toggle("hidden", !user);

  if (user) {
    const isCustomer = String(user.role || "").toUpperCase() === "CUSTOMER";
    const isTechnician = !isCustomer;
    ui.welcomeTitle.textContent = `${isCustomer ? "Customer" : "Technician"} dashboard`;
    ui.newRequestBtn.classList.toggle("hidden", !isCustomer);
    ui.requestList.closest(".section-card")?.classList.remove("hidden");
    const recentRequestsTitle = ui.requestList.closest(".section-card")?.querySelector("h3");
    if (recentRequestsTitle) {
      recentRequestsTitle.textContent = isTechnician ? "Requests available for you" : "Recent requests";
    }
    document.querySelector('[data-view="requests"]')?.classList.toggle("hidden", !isCustomer);
    document.querySelector('[data-view="offers"]')?.classList.toggle("hidden", !isTechnician);

    const requestsButton = document.querySelector('[data-view="requests"]');
    if (requestsButton) {
      requestsButton.classList.toggle("hidden", !isCustomer);
    }

    if (isTechnician && document.querySelector(".view.active-view")?.id === "requestsView") {
      renderView("dashboard");
    }

    loadCustomerData();
    if (isTechnician) {
      clearInterval(offersRefreshTimer);
      offersRefreshTimer = setInterval(loadOffers, 5000);
    } else {
      clearInterval(offersRefreshTimer);
      offersRefreshTimer = null;
    }
  }
};

const toggleTechnicianSkillField = () => {
  const isTechnician = (ui.registerRole?.value || "CUSTOMER") === "TECHNICIAN";
  ui.technicianSkillField?.classList.toggle("hidden", !isTechnician);
  ui.technicianLocationFields?.classList.toggle("hidden", !isTechnician);
  if (!isTechnician) {
    ui.registerTechnicianSkill.value = "";
    if (ui.registerTechnicianLocation) ui.registerTechnicianLocation.value = "";
    if (ui.registerTechnicianLatitude) ui.registerTechnicianLatitude.value = "";
    if (ui.registerTechnicianLongitude) ui.registerTechnicianLongitude.value = "";
  }
};

const normalizeErrorMessage = (data) => {
  if (!data) return "Request failed";

  if (typeof data === "string") {
    const lower = data.toLowerCase();
    if (lower.includes("email already exists") || lower.includes("duplicate key") || lower.includes("unique index") || lower.includes("already exists")) {
      return "Email already exists. Please use a different email.";
    }
    if (lower.includes("phone number already exists") || lower.includes("phone already exists")) {
      return "Phone number already exists. Please use a different phone number.";
    }
    if (lower.includes("invalid email") || lower.includes("email format")) {
      return "Invalid email format";
    }
    if (lower.includes("password") && lower.includes("required")) {
      return "Password is required";
    }
    if (lower.includes("invalid email or password") || lower.includes("user not found")) {
      return "Invalid email or password";
    }
    if (lower.includes("could not execute statement") || lower.includes("sql") || lower.includes("constraint")) {
      return "This account already exists. Please use different details.";
    }
    return data;
  }

  if (typeof data === "object") {
    if (typeof data.message === "string" && data.message.trim()) {
      return data.message;
    }
    if (typeof data.error === "string" && data.error.trim()) {
      return data.error;
    }
    if (typeof data.detail === "string" && data.detail.trim()) {
      return data.detail;
    }
  }

  return "Request failed";
};

const api = async (url, method = "GET", body = null, customHeaders = {}) => {
  const headers = { ...customHeaders };
  if (body && !(body instanceof FormData)) {
    headers["Content-Type"] = "application/json";
  }
  if (state.token) {
    headers.Authorization = `Bearer ${state.token}`;
  }

  const response = await fetch(`${apiBase}${url}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : null
  });

  const contentType = response.headers.get("content-type") || "";
  let data = null;

  try {
    data = contentType.includes("application/json") ? await response.json() : await response.text();
  } catch (error) {
    data = null;
  }

  if (!response.ok) {
    throw new Error(normalizeErrorMessage(data) || `Request failed with status ${response.status}`);
  }

  return data;
};

const login = async (event) => {
  event.preventDefault();
  const email = document.getElementById("loginEmail").value.trim();
  const password = document.getElementById("loginPassword").value.trim();

  try {
    const response = await api("/api/login", "POST", { email, password });
    state.token = response.token;
    state.user = {
      id: response.id,
      name: response.name,
      email: response.email,
      phone: response.phone,
      role: response.role
    };
    localStorage.setItem("smartflow_token", state.token);
    localStorage.setItem("smartflow_user", JSON.stringify(state.user));
    setLoggedInUI();
    renderView("dashboard");
    showToast("Logged in successfully");
  } catch (error) {
    showToast(error.message);
  }
};

const createTechnicianProfile = async (userId, selectedSkillName) => {
  if (!selectedSkillName) {
    throw new Error("Please choose your technician type");
  }

  const location = (ui.registerTechnicianLocation?.value || "").trim();
  const latitudeRaw = ui.registerTechnicianLatitude?.value ?? "";
  const longitudeRaw = ui.registerTechnicianLongitude?.value ?? "";

  if (!location) {
    throw new Error("Please enter your technician location");
  }

  if (!latitudeRaw || !longitudeRaw) {
    throw new Error("Please use your current location to fill the technician coordinates");
  }

  const latitude = Number(latitudeRaw);
  const longitude = Number(longitudeRaw);

  if (!Number.isFinite(latitude) || !Number.isFinite(longitude)) {
    throw new Error("Please enter valid technician latitude and longitude");
  }

  const skillList = await api("/api/skills", "GET");
  const selectedSkill = skillList.find((skill) => skill.name.toLowerCase() === selectedSkillName.toLowerCase());

  if (!selectedSkill) {
    throw new Error("Selected technician type is not available yet");
  }

  const technicianResponse = await api("/api/technicians", "POST", {
    user: { id: userId },
    availabilityStatus: "ONLINE_AVAILABLE",
    location,
    latitude,
    longitude,
    rating: 0,
    currentJobs: 0
  });

  await api(`/api/technician-skills/${technicianResponse.id}/${selectedSkill.id}`, "POST");
  return technicianResponse;
};

const completeExistingTechnicianRegistration = async (payload, technicianSkillName) => {
  const loginResponse = await api("/api/login", "POST", {
    email: payload.email,
    password: payload.password
  });

  const previousToken = state.token;
  const previousUser = state.user;
  state.token = loginResponse.token;
  try {
    try {
      await api("/api/technicians/me", "GET");
      throw new Error("Email already exists. Please use a different email.");
    } catch (error) {
      if (error.message !== "Technician profile not found") {
        throw error;
      }
    }

    await createTechnicianProfile(loginResponse.id, technicianSkillName);
    return true;
  } finally {
    state.token = previousToken;
    state.user = previousUser;
  }
};

const register = async (event) => {
  event.preventDefault();

  try {
    const payload = {
      name: document.getElementById("registerName").value.trim(),
      email: document.getElementById("registerEmail").value.trim(),
      phone: document.getElementById("registerPhone").value.trim(),
      role: document.getElementById("registerRole").value,
      password: document.getElementById("registerPassword").value.trim()
    };

    const technicianSkillName = ui.registerTechnicianSkill?.value;
    if (payload.role === "TECHNICIAN" && !technicianSkillName) {
      throw new Error("Please choose your technician type");
    }

    let recoveredExistingTechnician = false;
    let createdUser;
    try {
      createdUser = await api("/api/users", "POST", payload);
    } catch (error) {
      if (payload.role !== "TECHNICIAN" || error.message !== "Email already exists. Please use a different email.") {
        throw error;
      }

      recoveredExistingTechnician = await completeExistingTechnicianRegistration(
        payload,
        technicianSkillName
      );
    }

    if (payload.role === "TECHNICIAN" && !recoveredExistingTechnician) {
      // Profile and skill setup requires an authenticated technician request.
      // Sign in temporarily so registration cannot leave an incomplete technician.
      const loginResponse = await api("/api/login", "POST", {
        email: payload.email,
        password: payload.password
      });
      const previousToken = state.token;
      const previousUser = state.user;
      state.token = loginResponse.token;
      try {
        await createTechnicianProfile(createdUser.id, technicianSkillName);
      } finally {
        state.token = previousToken;
        state.user = previousUser;
      }
    }

    setAuthMode("login");
    showToast(recoveredExistingTechnician
      ? "Technician profile completed. Please log in."
      : "Account created. Please log in.");
    document.getElementById("registerForm").reset();
    toggleTechnicianSkillField();
  } catch (error) {
    showToast(error.message);
  }
};

const loadSkills = async () => {
  try {
    const skills = await api("/api/skills", "GET");

    const requestOptions = ['<option value="ANY">Any technician</option>']
      .concat(skills.map((skill) => `<option value="${skill.name}">${skill.name}</option>`))
      .join("");

    if (ui.requestCategory) {
      const currentValue = ui.requestCategory.value || "ANY";
      ui.requestCategory.innerHTML = requestOptions;
      ui.requestCategory.value = skills.some((skill) => skill.name === currentValue) ? currentValue : "ANY";
    }

    if (ui.registerTechnicianSkill) {
      ui.registerTechnicianSkill.innerHTML = '<option value="">Select type</option>' +
        skills.map((skill) => `<option value="${skill.name}">${skill.name}</option>`).join("");
      ui.registerTechnicianSkill.value = "";
    }
  } catch (error) {
    if (ui.requestCategory) {
      ui.requestCategory.innerHTML = '<option value="ANY">Any technician</option>';
    }
    if (ui.registerTechnicianSkill) {
      ui.registerTechnicianSkill.innerHTML = '<option value="">Select type</option>';
    }
  }
};

const createRequest = async (event) => {
  event.preventDefault();

  try {
    const requestLatitudeValue = document.getElementById("requestLatitude").value;
    const requestLongitudeValue = document.getElementById("requestLongitude").value;

    if (!requestLatitudeValue || !requestLongitudeValue) {
      throw new Error("Please use your current location before creating the request");
    }

    const payload = {
      title: document.getElementById("requestTitle").value.trim(),
      description: document.getElementById("requestDescription").value.trim(),
      category: document.getElementById("requestCategory").value.trim(),
      location: document.getElementById("requestLocation").value.trim(),
      latitude: Number(requestLatitudeValue),
      longitude: Number(requestLongitudeValue),
      priority: document.getElementById("requestPriority").value,
      status: "OPEN"
    };

    await api("/api/service-requests", "POST", payload);
    document.getElementById("requestForm").reset();
    if (ui.requestCategory) {
      ui.requestCategory.value = "ANY";
    }
    showToast("Request created");
    renderView("dashboard");
    loadCustomerData();
  } catch (error) {
    showToast(error.message);
  }
};

const loadCustomerData = async () => {
  if (!state.user) return;

  try {
    if (state.user.role === "TECHNICIAN") {
      await loadOffers();
      return;
    }

    const requests = await api("/api/service-requests/my", "GET");
    state.requests = requests;
    ui.statRequests.textContent = String(requests.length);
    ui.statOffers.textContent = String(requests.filter((r) => ["OPEN", "ASSIGNED"].includes(r.status)).length);
    ui.statActive.textContent = String(requests.filter((r) => ["ASSIGNED", "IN_PROGRESS", "RESOLVED"].includes(r.status)).length);
    ui.statCompleted.textContent = String(requests.filter((r) => r.status === "CLOSED").length);
    ui.statRating.textContent = "0.0";
    ui.requestList.innerHTML = requests.length
      ? requests.slice(0, 4).map((r) => `
        <article class="request-item">
          <div class="request-top">
            <strong>${r.title}</strong>
            <span class="badge ${statusClass(r.status)}">${r.status}</span>
          </div>
          <div class="meta">
            <span>${r.category}</span>
            <span>•</span>
            <span>${r.location}</span>
            <span>•</span>
            <span>${r.priority}</span>
          </div>
          <div class="meta">
            <span>${r.description}</span>
          </div>
          ${r.assignedTechnicianName ? `<div class="meta"><span>Technician: ${r.assignedTechnicianName}</span></div>` : ""}
          <div class="meta">
            ${r.status === "ASSIGNED"
              ? `<button class="primary-btn small" data-request-action="start" data-id="${r.id}">Start job</button>`
              : ""}
            ${r.status === "RESOLVED"
              ? `<button class="primary-btn small" data-request-action="close" data-id="${r.id}">Close request</button>
                 `
              : ""}
            ${r.status === "CLOSED"
              ? `<button class="ghost-btn small" data-request-action="review" data-id="${r.id}">Give review</button>`
              : ""}
            ${r.status === "OPEN"
              ? `<button class="ghost-btn small" data-request-action="delete" data-id="${r.id}">Delete request</button>`
              : ""}
          </div>
        </article>
      `).join("")
      : `<div class="request-item">No requests yet.</div>`;

    document.querySelectorAll("[data-request-action]").forEach((button) => {
      button.addEventListener("click", handleRequestDelete);
    });
  } catch (error) {
    showToast(error.message);
  }
};

const handleRequestDelete = async (event) => {
  const action = event.currentTarget.dataset.requestAction;
  const requestId = event.currentTarget.dataset.id;
  if (action === "review") {
    const rating = Number(window.prompt("Enter a rating from 1 to 5"));
    if (!Number.isInteger(rating) || rating < 1 || rating > 5) {
      showToast("Rating must be between 1 and 5");
      return;
    }
    const comment = window.prompt("Add a review comment (optional)") || "";
    try {
      await api("/api/reviews", "POST", { requestId: Number(requestId), rating, comment });
      showToast("Review submitted");
      await loadCustomerData();
    } catch (error) {
      showToast(error.message);
    }
    return;
  }

  const endpoint = action === "start"
    ? `/api/service-requests/${requestId}/start`
    : action === "close"
      ? `/api/service-requests/${requestId}/close`
      : `/api/service-requests/${requestId}`;
  if (action === "delete" && !window.confirm("Delete this open request?")) return;

  try {
    await api(endpoint, action === "delete" ? "DELETE" : "PUT");
    showToast(action === "delete" ? "Request deleted" : action === "close" ? "Request closed" : "Job started");
    await loadCustomerData();
  } catch (error) {
    showToast(error.message);
  }
};

const loadOffers = async () => {
  try {
    const [offers, technician] = await Promise.all([
      api("/api/request-offers/my", "GET"),
      api("/api/technicians/me", "GET")
    ]);
    state.offers = offers;
    ui.statRating.textContent = Number(technician.rating || 0).toFixed(1);
    const pendingOffers = offers.filter((offer) => String(offer.status).toUpperCase() === "OFFERED");
    ui.statOffers.textContent = String(pendingOffers.length);
    const offerMarkup = offers.length
      ? offers.map((offer) => `
        <article class="offer-item">
          <div class="offer-top">
            <strong>${offer.requestTitle}</strong>
            <span class="badge ${statusClass(offer.status)}">${offer.status}</span>
          </div>
          <div class="meta">
            <span>${offer.technicianName}</span>
            <span>•</span>
            <span>${offer.requestId}</span>
          </div>
          <div class="meta">
            <span>${offer.requestLocation || "Location not set"}</span>
            <span>•</span>
            <span>${Number.isFinite(offer.requestLatitude) && Number.isFinite(offer.requestLongitude)
              ? `${offer.requestLatitude}, ${offer.requestLongitude}`
              : "Coordinates unavailable"}</span>
          </div>
          <div class="meta"><span>${offer.requestCategory || ""} • ${offer.requestPriority || ""}</span></div>
          <div class="meta"><span>Customer: ${offer.customerName || "Customer"}</span></div>
          <div class="meta offer-actions">
            <button class="ghost-btn small" data-offer-action="open" data-id="${offer.id}">Open</button>
            ${String(offer.status).toUpperCase() === "OFFERED"
              ? `<button class="primary-btn small" data-offer-action="accept" data-id="${offer.id}">Accept request</button>
                 <button class="ghost-btn small" data-offer-action="reject" data-id="${offer.id}">Reject</button>`
              : String(offer.status).toUpperCase() === "ACCEPTED" && offer.requestStatus === "ASSIGNED"
                ? `<button class="primary-btn small" data-offer-action="start" data-id="${offer.requestId}">Start job</button>`
                : String(offer.status).toUpperCase() === "ACCEPTED" && offer.requestStatus === "IN_PROGRESS"
                  ? `<button class="primary-btn small" data-offer-action="resolve" data-id="${offer.requestId}">Resolve job</button>`
              : ""}
          </div>
        </article>
      `).join("")
      : `<div class="offer-item">No matching requests are available yet. New matching requests will appear here.</div>`;

    ui.offersList.innerHTML = offerMarkup;
    ui.requestList.innerHTML = offerMarkup;
    ui.statRequests.textContent = String(pendingOffers.length);

    document.querySelectorAll("[data-offer-action]").forEach((button) => {
      button.addEventListener("click", handleOfferAction);
    });
  } catch (error) {
    ui.statOffers.textContent = "0";
    const message = error.message === "Technician profile not found"
      ? "This technician account has no profile yet. Please register again with technician type and current location."
      : (error.message || "Unable to load offers.");
    const errorMarkup = `<div class="offer-item">${message}</div>`;
    ui.offersList.innerHTML = errorMarkup;
    ui.requestList.innerHTML = errorMarkup;
    showToast(message);
  }
};

const handleOfferAction = async (event) => {
  const { offerAction, id } = event.currentTarget.dataset;
  const selectedOffer = state.offers.find((offer) => String(offer.id) === String(id));

  if (offerAction === "open") {
    if (selectedOffer?.requestLatitude != null && selectedOffer?.requestLongitude != null) {
      window.open(
        `https://www.google.com/maps/search/?api=1&query=${selectedOffer.requestLatitude},${selectedOffer.requestLongitude}`,
        "_blank",
        "noopener"
      );
    } else {
      showToast(selectedOffer?.requestLocation || "Request location is unavailable");
    }

    return;
  }

  if (offerAction === "start" || offerAction === "resolve") {
    try {
      await api(`/api/service-requests/${id}/${offerAction}`, "PUT");
      showToast(offerAction === "start" ? "Job started" : "Job resolved");
      await loadOffers();
    } catch (error) {
      showToast(error.message);
    }
    return;
  }

  const action = offerAction === "accept" ? "accept" : "reject";

  try {
    await api(`/api/request-offers/${id}/${action}`, "PUT");
    showToast(`${action === "accept" ? "Accepted" : "Rejected"} offer`);
    await loadOffers();
  } catch (error) {
    showToast(error.message);
  }
};

const setLocationStatus = (message, isError = false) => {
  if (!ui.locationStatus) return;
  ui.locationStatus.textContent = message;
  ui.locationStatus.style.color = isError ? "#b52d2d" : "#5c6d83";
};

const setRegisterLocationStatus = (message, isError = false) => {
  if (!ui.registerLocationStatus) return;
  ui.registerLocationStatus.textContent = message;
  ui.registerLocationStatus.style.color = isError ? "#b52d2d" : "#5c6d83";
};

const formatLocationFromReverseGeocode = (locationData) => {
  const address = locationData?.address || {};
  const parts = [
    address.village,
    address.town,
    address.city,
    address.city_district,
    address.state_district,
    address.state,
    address.country
  ].filter(Boolean);

  const formatted = parts.join(", ");
  return formatted || locationData?.display_name || "Current location";
};

const populateCurrentLocation = async (latitude, longitude, targetLocationInput, targetLatitudeInput, targetLongitudeInput, statusCallback) => {
  if (targetLatitudeInput) targetLatitudeInput.value = latitude.toFixed(6);
  if (targetLongitudeInput) targetLongitudeInput.value = longitude.toFixed(6);

  try {
    const reverseResponse = await fetch(`https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${latitude}&lon=${longitude}`);
    const locationData = await reverseResponse.json();
    const placeName = formatLocationFromReverseGeocode(locationData);
    if (targetLocationInput) targetLocationInput.value = placeName;
    if (statusCallback) statusCallback("Using your current location");
  } catch (error) {
    if (targetLocationInput) targetLocationInput.value = "Current location";
    if (statusCallback) statusCallback("Coordinates captured. Add a custom location if needed.");
  }
};

const useCurrentLocation = () => {
  if (!navigator.geolocation) {
    setLocationStatus("Geolocation is not supported in this browser.", true);
    return;
  }

  setLocationStatus("Requesting your current location...");
  navigator.geolocation.getCurrentPosition(
    async (position) => {
      const latitude = position.coords.latitude;
      const longitude = position.coords.longitude;
      await populateCurrentLocation(
        latitude,
        longitude,
        ui.requestLocation,
        ui.requestLatitude,
        ui.requestLongitude,
        setLocationStatus
      );
    },
    (error) => {
      const message = error.code === error.PERMISSION_DENIED
        ? "Location access denied. Please enter your location manually."
        : "Unable to detect your location right now. Please enter it manually.";
      setLocationStatus(message, true);
    },
    { enableHighAccuracy: true, timeout: 15000, maximumAge: 60000 }
  );
};

const useCurrentLocationForTechnician = () => {
  if (!navigator.geolocation) {
    setRegisterLocationStatus("Geolocation is not supported in this browser.", true);
    return;
  }

  setRegisterLocationStatus("Requesting your current location...");
  navigator.geolocation.getCurrentPosition(
    async (position) => {
      const latitude = position.coords.latitude;
      const longitude = position.coords.longitude;
      await populateCurrentLocation(
        latitude,
        longitude,
        ui.registerTechnicianLocation,
        ui.registerTechnicianLatitude,
        ui.registerTechnicianLongitude,
        setRegisterLocationStatus
      );
    },
    (error) => {
      const message = error.code === error.PERMISSION_DENIED
        ? "Location access denied. Please enter your location manually."
        : "Unable to detect your location right now. Please enter it manually.";
      setRegisterLocationStatus(message, true);
    },
    { enableHighAccuracy: true, timeout: 15000, maximumAge: 60000 }
  );
};

const statusClass = (status) => {
  if (!status) return "warning";
  const normalized = status.toUpperCase();
  if (["ACCEPTED", "CLOSED", "ASSIGNED", "IN_PROGRESS"].includes(normalized)) return "success";
  if (["OFFERED", "RESOLVED"].includes(normalized)) return "warning";
  return "danger";
};

navButtons.forEach((button) => {
  button.addEventListener("click", () => {
    renderView(button.dataset.view);
  });
});

authTabs.forEach((button) => {
  button.addEventListener("click", () => setAuthMode(button.dataset.auth));
});

document.getElementById("logoutBtn").addEventListener("click", () => {
  localStorage.removeItem("smartflow_token");
  localStorage.removeItem("smartflow_user");
  state.token = "";
  state.user = null;
  setLoggedInUI();
  showToast("Logged out");
});

document.getElementById("loginForm").addEventListener("submit", login);
document.getElementById("registerForm").addEventListener("submit", register);
document.getElementById("requestForm").addEventListener("submit", createRequest);
document.getElementById("newRequestBtn").addEventListener("click", () => renderView("requests"));
ui.registerRole.addEventListener("change", toggleTechnicianSkillField);
ui.useCurrentLocationBtn.addEventListener("click", useCurrentLocation);
if (ui.registerUseCurrentLocationBtn) {
  ui.registerUseCurrentLocationBtn.addEventListener("click", useCurrentLocationForTechnician);
}

setLocationStatus("Location will be detected automatically when you click Use my current location.");
if (ui.registerLocationStatus) {
  setRegisterLocationStatus("Location will auto-fill here.");
}
loadSkills();
toggleTechnicianSkillField();
setAuthMode("login");
renderView("dashboard");
setLoggedInUI();

if (state.user) {
  loadCustomerData();
}
