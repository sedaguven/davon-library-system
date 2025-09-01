import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api', // Backend API base URL
  headers: {
    'Content-Type': 'application/json',
  },
  // Disable withCredentials for now to avoid CORS issues
  withCredentials: false,
  timeout: 50000, // 50 second timeout
});

// Add request interceptor to handle auth tokens
apiClient.interceptors.request.use((config) => {
  // Add auth token if available
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

// Add response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = axios.isAxiosError(error) ? error.response?.status : undefined;
    const isExpectedClientOutcome = status === 404 || status === 409;
    const message = axios.isAxiosError(error)
      ? (error.response?.data?.message ?? error.response?.data ?? error.message)
      : (error instanceof Error ? error.message : String(error));
    if (!isExpectedClientOutcome) {
      console.error('API Error:', message);
    }
    return Promise.reject(error);
  }
);

export default apiClient; 