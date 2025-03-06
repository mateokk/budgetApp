import axios from "axios";

export const api = axios.create({
  baseURL: "http://localhost:8088"
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if(token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  }, (error) => Promise.reject(error)
);

export const login = async(credentials) => {
  if(localStorage.getItem("token")) {
    localStorage.removeItem("token");
  }
  const response = await api.post("/auth/login", credentials);
  localStorage.setItem("token", response.data);
  return response.data;
};
export const signup = (userData) => api.post("/auth/register", userData);
export const getCurrentUser = () => api.get("/auth/me");
export const getTransactions = (categoryId ="") => {
  const url = categoryId ? `?categoryId=${categoryId}` : "";
  return api.get(`/transactions${url}`);
};
export const addTransaction = (transaction) => api.post("/transactions", transaction);
export const getCategories = () => api.get("/categories");
export const updateTransaction = (transaction, id) => api.put(`/transactions/${id}`, transaction);
export const deleteTransaction = (id) => api.delete(`/transactions/${id}`);
export const getUserDetails = () => api.get("auth/me");



