<template>
  <v-card
      class="mx-auto extended-card"
      title="Logowanie"
  >
    <v-container>
      <v-text-field
          v-model="user.email"
          :color="primaryColor"
          label="Email"
          variant="underlined"
          placeholder="Podaj email"
          @keyup.enter="submitLogin"
      ></v-text-field>

      <v-text-field
          v-model="user.password"
          :append-icon="show1 ? 'mdi-eye':'mdi-eye-off'"
          :type="show1 ? 'text' : 'password'"
          @click:append="show1 = !show1"
          :color="primaryColor"
          label="Hasło"
          placeholder="Podaj hasło"
          variant="underlined"
          @keyup.enter="submitLogin"
      ></v-text-field>
      <v-label >
        <RouterLink to="/exam" style=" text-decoration: none; color: inherit; font-size: 0.8rem; text-decoration: underline 2px var(--secondary-color)">
        Zaloguj się za pomocą jednorazowego loginu!
        </RouterLink>
      </v-label>

    </v-container>

    <v-divider></v-divider>

    <v-card-actions>
      <nav>
        <RouterLink to="/register">
          <v-btn :color="secondaryColor">
            <v-icon icon="mdi-chevron-left"></v-icon>
            Zarejesruj się

          </v-btn>
        </RouterLink>
      </nav>
      <v-spacer></v-spacer>

      <v-btn :color="secondaryColor" @click="submitLogin" >
        Zaloguj
        <v-icon icon="mdi-chevron-right"></v-icon>
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
import {useToast} from "vue-toastification";

export default {
  data() {
    return {
      user : {
        email: '',
        password: '',
      },
      show1: false,
      primaryColor: 'var(--primary-color)',
      secondaryColor: 'var(--secondary-color)'
    }
  },mounted() {
    this.$toast = useToast();
  },
  methods: {
    submitLogin(){
      if (!this.user.email.trim() || !this.user.password.trim()) {
        this.$toast.warning("Puste pola logownaia", {
          position: "top-center",
          timeout: 1805,
          closeOnClick: true,
          pauseOnFocusLoss: true,
          pauseOnHover: false,
          draggable: true,
          draggablePercent: 0.6,
          showCloseButtonOnHover: false,
          hideProgressBar: true,
          closeButton: false,
          icon: true,
          rtl: false
        });
      }
      else {
      this.$emit('login', this.user);
    }}
  }
}
</script>

<style scoped>
.extended-card {
  width: 90%;
  background-color: transparent;
  color: white;
  border: 1px solid rgba(235, 235, 235, 0.64);
}

@media (max-width: 1000px) {
  .extended-card {
    width: 100%;
  }
}
</style>
