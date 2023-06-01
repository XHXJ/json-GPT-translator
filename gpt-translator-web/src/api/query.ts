import {http} from "@/utils/http";

// url拼接传参
export const translationDataPage = (params?: object) => {
  return http.request("get", "/api/translation-data/page", {params});
};

export const translationUpdate = (data?: object) => {
  return http.request("put", "/api/translation-data/update", {data});
};
export const projectsDataPage = (params?: object) => {
  return http.request("get", "/api/projects/page", {params});
}

export const fileDataList = (params?: object) => {
  return http.request("get", "/api/projects/file-list", {params});
}

export const vueProjectsSelect = (params?: object) => {
  return http.request("get", "/api/projects/vue-projects-select", {params});
}

export const vueFileSelect = (params?: object) => {
  return http.request("get", "/api/projects/vue-file-select", {params});
}

export const deleteProjects = (params?: object) => {
  return http.request("delete", "/api/projects/delete-projects", {params});
}
export const deleteFile = (params?: object) => {
  return http.request("delete", "/api/projects/delete-file", {params});
}

export const translationDelete = (data?: object) => {
  return http.request("delete", "/api/translation-data/delete",{data});
}

export const postTranslateOne = (data?: object) => {
  return http.request("post", "/api/translation-data/one-translate",{data});
}
