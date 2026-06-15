import http from "./http";

export function getProjects(params) {
  return http.get("/api/v1/projects", { params });
}

export function getProjectById(projectId) {
  return http.get(`/api/v1/projects/${projectId}`);
}

export function createProject(projectData) {
  return http.post("/api/v1/projects", projectData);
}

export function updateProject(projectId, projectData) {
  return http.put(`/api/v1/projects/${projectId}`, projectData);
}

export function deleteProject(projectId) {
  return http.delete(`/api/v1/projects/${projectId}`);
}

export function getProjectMembers(projectId) {
  return http.get(`/api/v1/projects/${projectId}/members`);
}

export function addProjectMember(projectId, memberData) {
  return http.post(`/api/v1/projects/${projectId}/members`, memberData);
}