import axios from 'axios';

const BASE_URL = process.env.REACT_APP_BASE_URL;

export const GuestApiClient = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,
});

export const UserApiClient = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,
});
