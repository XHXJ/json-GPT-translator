import {http} from "@/utils/http";

export const openaiPropertiesPage = (params?: object) => {
  return http.request("get", "/api/openai-properties/page", {params});
};

export const openaiPropertiesCreateKey = (data?: object) => {
  return http.request("post", "/api/openai-properties/key", {data});
};

export const openaiPropertiesDelete = (params?: object) => {
  return http.request("delete", "/api/openai-properties/delete", {params});
};
