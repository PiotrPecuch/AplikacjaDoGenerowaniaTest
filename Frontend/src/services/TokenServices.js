import authServices from "@/services/auth.services.js";

class TokenService {

    getLocalRefreshToken() {
        const user = JSON.parse(localStorage.getItem("user"));
        return user.refreshToken;
    }

    getUserRole(){
        const user = JSON.parse(localStorage.getItem("user"));
        return user.roles;
    }

    getLocalAccessToken() {
        const user = JSON.parse(localStorage.getItem("user"));
        return user.accessToken;
    }

    updateLocalAccessToken(token) {
        let user = JSON.parse(localStorage.getItem("user"));
        user.accessToken = token;
        localStorage.setItem("user", JSON.stringify(user));
    }
    updateRefreshToken(token) {
        let user = JSON.parse(localStorage.getItem("user"));
        user.refreshToken = token;
        localStorage.setItem("user", JSON.stringify(user));
    }

    getUser() {
        return JSON.parse(localStorage.getItem("user"));
    }

    setUser(user) {
        localStorage.setItem("user", JSON.stringify(user));
    }

    removeUser() {
        localStorage.removeItem("user");
    }


    async isTokenValid() {
        const user = this.getUser();

        if (!user || !user.accessToken) {
            return false;
        }

        const accessToken = user.accessToken;
        const payload = this.parseJwtPayload(accessToken);

        if (!payload || !payload.exp) {
            return false;
        }

        const expirationTime = payload.exp * 1000;

        if (expirationTime > Date.now()) {
            return true;
        }

        try {
            await authServices.refreshToken();
            return true;
        } catch (error) {
            return false; 
        }
    }



    parseJwtPayload(token) {
        try {
            const payloadBase64 = token.split('.')[1];
            return JSON.parse(atob(payloadBase64));
        } catch (error) {
            return null;
        }
    }
}

export default new TokenService();