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
