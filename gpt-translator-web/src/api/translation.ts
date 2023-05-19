import {http} from "@/utils/http";

export const translationStart = () => {
  return http.request("get", "/api/chatgpt/start");
};

export const translationStop = () => {
  return http.request("get", "/api/chatgpt/stop");
}

