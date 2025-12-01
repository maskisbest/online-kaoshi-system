import axios from "axios";
import { Message } from "element-ui";

const http = axios.create({
  baseURL: "",
  timeout: 10000,
});

http.interceptors.request.use(
  (config) => {
    if (config.url && !config.url.startsWith("http")) {
      const normalized = config.url.startsWith("/api")
        ? config.url
        : `/api${config.url.startsWith("/") ? config.url : `/${config.url}`}`;
      config.url = normalized;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

http.interceptors.response.use(
  (response) => response,
  (error) => {
    const resp = error && error.response;
    const status = resp && resp.status;
    const msg =
      (resp && resp.data && resp.data.message) ||
      (status ? `请求失败 (${status})` : "网络异常，请稍后重试");
    Message.error(msg);
    return Promise.reject(error);
  }
);

export default http;
