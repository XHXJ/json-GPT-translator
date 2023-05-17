import {http} from "@/utils/http";
import {baseUrlApi} from "./utils";

// url拼接传参
export const translationDataPage = (params?: object) => {
  return http.request("get", baseUrlApi("translation-data/page"), {params});
};
