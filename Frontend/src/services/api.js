import axios from "axios";

const instance = axios.create({
    baseURL: "http:///100.124.184.2:8080/api",
    headers: {
        "Content-Type": "application/json",
    },
});

export default instance;