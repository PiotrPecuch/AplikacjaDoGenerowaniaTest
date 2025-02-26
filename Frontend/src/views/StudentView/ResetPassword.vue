  <template>
    <v-container class="center-container">
      <v-card :theme="themeType" class="center-card">
        <v-card-title>
          <span class="headline">Resetowanie hasła</span>
        </v-card-title>
        <v-card-text>
        <span>
          <v-text-field
              v-model="newPassword"
              :append-icon="show1 ? 'mdi-eye':'mdi-eye-off'"
              :type="show1 ? 'text' : 'password'"
              @click:append="show1 = !show1"
              :color="primaryColor"
              placeholder="Nowe hasło"
              variant="underlined"
              @keyup.enter="resetPassword"
          ></v-text-field>
        </span>
        </v-card-text>
        <v-card-actions style="flex-direction: row-reverse">
          <v-btn @click="resetPassword()">Zmień</v-btn>
        </v-card-actions>
      </v-card>
    </v-container>
  </template>

  <script>
  import AdminServices from "@/services/AdminServices.js";
  import {useRoute} from "vue-router";

  export default {
    name: "ResetPassword",
    data() {
      return{
        themeType: this.$themeType,
        newPassword: '',
        show1: false,
      }
    },
    setup() {
      const route = useRoute();
      const queryParams = route.query;

      return { queryParams };
    },
    methods: {
      resetPassword() {
        AdminServices.resetPassword(this.queryParams.token, this.newPassword);
      },
    },
  }
  </script>

  <style scoped>
    .center-container {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
    }

    .center-card {
      max-width: 500px;
      width: 100%;
    }
  </style>
