import api from "@/services/api.js";
import TokenServices from "@/services/TokenServices.js";
import tokenServices from "@/services/TokenServices.js";
import PDFService from "@/services/PDFService.js";
import notificationService from "@/services/NotificationService.js";
import router from "@/router/index.js";

class AdminServices{
    getAllUsers() {
        return new Promise((resolve, reject) => {
            TokenServices.isTokenValid().then(async () => {
                return api.get("/admin/getAllUsers", {
                    headers: {
                        'Authorization': `Bearer ${tokenServices.getLocalAccessToken()}`,
                        'Content-Type': 'application/json'
                    }
                })
                    .then(response => {
                        resolve(response.data);
                    })
                    .catch(error => {
                        notificationService.notification("error", `${error.response.data}`);
                        reject(error);
                    });
            })
        })
    }


    getAllInformation() {
        return new Promise((resolve, reject) => {
            TokenServices.isTokenValid().then(async () => {
                return api.get("/admin/getAllInformations", {
                    headers: {
                        'Authorization': `Bearer ${tokenServices.getLocalAccessToken()}`,
                        'Content-Type': 'application/json'
                    }
                })
                    .then(response => {
                        resolve(response.data);
                    })
                    .catch(error => {
                        notificationService.notification("error", `${error.response.data}`);
                        console.error('Błąd podczas pobierania użytkowników:', error.response);
                        reject(error);
                    });
            })
        })
    }


    deleteUser(userEmail) {
        return new Promise((resolve, reject) => {
            TokenServices.isTokenValid().then(async () => {
                return api.delete("/admin/deleteUser", {
                    params:{
                        userEmail: userEmail
                    },
                    headers: {
                        'Authorization': `Bearer ${tokenServices.getLocalAccessToken()}`,
                        'Content-Type': 'application/json'
                    }
                })
                    .then(response => {
                        resolve(response.data);
                        notificationService.notification("success", `${response.data}`);
                    })
                    .catch(error => {
                        notificationService.notification("error", `${error.response.data}`);
                        console.error('Błąd:', error.response);
                        reject(error);
                    });
            })
        })
    }

    sendResetPasswordRequest(userEmail) {
        return new Promise((resolve, reject) => {
            TokenServices.isTokenValid().then(async () => {
                return api.post("/admin/resetPasswordRequest", null,{
                    params:{
                        userEmail: userEmail
                    },
                    headers: {
                        'Authorization': `Bearer ${tokenServices.getLocalAccessToken()}`,
                        'Content-Type': 'application/json'
                    }
                })
                    .then(response => {
                        resolve(response.data);
                        notificationService.notification("success", `${response.data}`);
                    })
                    .catch(error => {
                        notificationService.notification("error", `${error.response.data}`);
                        console.error('Błąd:', error.response);
                        reject(error);
                    });
            })
        })
    }

    resetPassword(token, password) {
        return new Promise((resolve, reject) => {
            TokenServices.isTokenValid().then(async () => {
                return api.post("/auth/resetPassword", null,{
                    params:{
                        token: token,
                        password: password
                    },
                })
                    .then(response => {
                        resolve(response.data);
                        notificationService.notification("success", `${response.data}`);
                        router.push("/login")
                    })
                    .catch(error => {
                        notificationService.notification("error", `${error.response.data}`);
                        console.error('Błąd:', error.response);
                        reject(error);
                    });
            })
        })
    }




}
export default new AdminServices();