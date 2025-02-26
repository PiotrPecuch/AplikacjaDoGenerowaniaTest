<template>
  <v-card
      class="loginPanel"
      title="Egzamin"
      style="position: absolute; top:50%; left:50%; transform: translate(-50%,-50%); "
      :theme="themeType"
  >
    <v-container >
      <v-text-field
          v-model="user.ssoLogin"
          :color="primaryColor"
          label="Login"
          placeholder="Podaj login"
          variant="underlined"
          @keyup.enter="submitLogin"
      ></v-text-field>

      <v-text-field
          v-model="user.ssoPassword"
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
      </v-label>

    </v-container>

    <v-divider></v-divider>

    <v-card-actions>
      <v-spacer></v-spacer>
      <v-btn :color="secondaryColor" @click="submitLogin" >
        Zaloguj
        <v-icon icon="mdi-chevron-right"></v-icon>
      </v-btn>
    </v-card-actions>
  </v-card>

  <ExamRules
      v-model="showExamRulesModal"
      :examData="examData"
      @accepted="handleExamRulesAccepted"
      @close="handleExamRulesClose"
  />
</template>

<script>
import {useToast} from "vue-toastification";
import ExamServices from "@/services/ExamServices.js";
import ExamRules from "@/components/ExamRules.vue";
import notificationService from "@/services/NotificationService.js";

export default {
  components: {
    ExamRules
  },
  data() {
    return {
      user: {
        ssoLogin: '',
        ssoPassword: '',
      },
      show1: false,
      themeType: this.$themeType,
      primaryColor: 'var(--primary-color)',
      secondaryColor: 'var(--secondary-color)',
      showExamRulesModal: false,
      examData: {},
    };
  },
  mounted() {
    this.$toast = useToast();
  },
  methods: {
    submitLogin() {
      if (!this.user.ssoLogin.trim() || !this.user.ssoPassword.trim()) {
        notificationService.notification("info","Puste pole logowania")
      } else {
        ExamServices.startExam(this.user)
            .then(response => {
              this.examData = response;
              this.showExamRulesModal = true;
            })
            .catch(error => {
              console.error('Błąd podczas pobierania egzaminu:', error);
            });
      }
    },

    handleExamRulesAccepted() {
      this.showExamRulesModal = false;
      this.$router.push({
        path: '/exam/solve',
        state: { countdown: this.examData.time,
        numberOfQuestions: this.examData.numberOfQuestion,
        ssoLogin: this.user.ssoLogin,
        ssoPassword: this.user.ssoPassword}
      });
    },

    handleExamRulesClose() {
      this.showExamRulesModal = false;
    }
  }
};
</script>
<style scoped>
@media (max-width: 1024px) {
  .loginPanel{
    width: 75%;
  }
}
@media (min-width: 1024px) {
  .loginPanel{
    width: 25%;
  }
}

</style>