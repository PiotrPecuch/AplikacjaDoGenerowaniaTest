import TokenServices from "@/services/TokenServices.js";
import router from "@/router/index.js";
import api from "@/services/api.js";
import NotificationService from "@/services/NotificationService.js";
import notificationService from "@/services/NotificationService.js";

class AuthServices {


    async refreshToken() {
        return api.post('/auth/refreshtoken', {refreshToken: TokenServices.getLocalRefreshToken()})
            .then((response) => {
                TokenServices.updateLocalAccessToken(response.data.accessToken);
                TokenServices.updateRefreshToken(response.data.refreshToken);
                return response.data.accessToken;
            })
            .catch((error) => {
                notificationService.notification("error","Sesja wygasła")
                this.logout();
                throw error;
            });
    }

    async login(credentials) {
        await api.post('/auth/signin', credentials)
            .then(async (response) => {
                await TokenServices.setUser(response.data);
                await router.replace("/main")
            })
            .catch((error) => {
                NotificationService.notification("error", "Błąd podczas logowania, sprawdź wprowadzone dane.")
            });
    }

    async logout() {
        try {
            await TokenServices.removeUser();
            await router.push("/");
        } catch (error) {
            console.error("Błąd podczas logowania użytkownika:", error);
        }
    }

    register(credentials) {
        api.post('/auth/signup', credentials)
            .then((response) => {
                TokenServices.setUser(response.data);
                NotificationService.notification("success","Zarejestrowano pomyślnie")
                router.replace("/login");
            })
            .catch((error) => {
                console.log(error)
                NotificationService.notification("error",error.response.data.message)
            });
    }
}

export default new AuthServices();