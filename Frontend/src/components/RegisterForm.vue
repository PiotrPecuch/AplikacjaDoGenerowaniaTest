<template>
  <v-card
      class="mx-auto extended-card"
      title="Rejestracja"
  >
    <v-container>
      <v-form v-model="valid">
      <v-text-field
          v-model="user.email"
          :color="primaryColor"
          :rules="emailRules"
          label="Email"
          variant="underlined"
      ></v-text-field>

      <v-text-field
          v-model="user.password"
          :color="primaryColor"
          :append-icon="show1 ? 'mdi-eye':'mdi-eye-off'"
          :type="show1 ? 'text' : 'password'"
          :rules="passwordRules"
          @click:append="show1 = !show1"
          label="Hasło"
          placeholder="Wprowadź hasło"
          variant="underlined"
      ></v-text-field>
      <v-text-field
          v-model="user.repeatPassword"
          :color="primaryColor"
          label="Powtórz hasło"
          :append-icon="show2 ? 'mdi-eye':'mdi-eye-off'"
          :type="show2 ? 'text' : 'password'"
          @click:append="show2 = !show2"
          placeholder="Wprowadź hasło ponownie"
          variant="underlined"
          @keyup.enter="submitRegister"
      ></v-text-field>
      <v-checkbox
          v-model="terms"
          :color="secondaryColor"
          @keyup.enter="submitRegister"
          label="Zgadzam się z regulaminem strony"
      ></v-checkbox>
      </v-form>
    </v-container>

    <v-divider></v-divider>

    <v-card-actions>
      <nav>
        <RouterLink to="/">
          <v-btn :color="secondaryColor">
            <v-icon icon="mdi-chevron-left"></v-icon>
            Masz konto? <br> Zaloguj się!

          </v-btn>
        </RouterLink>
      </nav>
      <v-spacer></v-spacer>
      <v-btn @keyup.enter="enterClicked()" @click="submitRegister" :color="secondaryColor">Zarejestruj
        <v-icon icon="mdi-chevron-right"></v-icon>
      </v-btn>

    </v-card-actions>
  </v-card>
</template>

<style scoped>
@import '@/assets/base.css';

.extended-card {
  width: 100%;
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
<script>

import {useToast} from "vue-toastification";
import NotificationService from "@/services/NotificationService.js";

export default {
  data() {
    return {
      valid: false,
      passwordRules: [
        v => v.length >= 8 || 'Hasło musi mieć co najmniej 8 znaków.',
        v => /[!@#$%^&*(),.?":{}|<>]/.test(v) || 'Hasło musi zawierać co najmniej jeden znak specjalny.',
      ],
      emailRules: [
        value => {
          if (value) return true

          return 'Adres e-mail jest wymagany.'
        },
        value => {
          if (/.+@.+\..+/.test(value)) return true

          return 'Podany adres jest błędny.'
        },
      ],
      user: {
        email: '',
        password: '',
        repeatPassword: ''
      },
      terms: false,
      show1: false,
      show2: false,
      primaryColor: 'var(--primary-color)',
      secondaryColor: 'var(--secondary-color)'
    }
  }, mounted() {
    this.$toast = useToast();
  },
  methods: {
    submitRegister() {
      if (this.user.repeatPassword !== this.user.password) {
        NotificationService.notification("warning", "Hasła nie pasują do siebie")
      } else if (!this.user.email.trim() || !this.user.password.trim() || !this.user.repeatPassword.trim() || this.terms.valueOf() !== true) {
        NotificationService.notification("warning", "Puste pola rejestracji")
      } else {
        this.$emit('register', this.user);
      }
    }
  }
}
</script>