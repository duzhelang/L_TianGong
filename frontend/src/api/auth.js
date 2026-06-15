import http from "./http";

export function login(credentials) {
  return http.post("/api/v1/auth/login", credentials);
}

export function register(userData) {
  return http.post("/api/v1/auth/register", userData);
}

export function getUserInfo() {
  return http.get("/api/v1/auth/userinfo");
}

export function updateProfile(profileData) {
  return http.put("/api/v1/auth/profile", profileData);
}

export function changePassword(passwordData) {
  return http.put("/api/v1/auth/password", passwordData);
}