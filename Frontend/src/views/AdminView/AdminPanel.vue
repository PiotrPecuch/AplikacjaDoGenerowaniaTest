<template >
  <v-container style="width: 100vw; background-color: #282828; display: flex; justify-content: space-between; align-items: center;">
    <span style="color: white">Panel Admina</span>
    <v-btn :theme="themeType" @click="handleLogout()"> Wyloguj</v-btn>
  </v-container>

  <v-container class="list-container" :theme="themeType"  >
    <v-container class="list-item">
      <p>{{ databaseInformationDescription[0] }}</p>
      <v-container>{{ databaseInformation.databaseSize }}</v-container>
    </v-container>

    <v-container class="list-item">
      <p>{{ databaseInformationDescription[1] }}</p>
      <v-container>{{ databaseInformation.userCount }}</v-container>
    </v-container>

    <v-container class="list-item">
      <p>{{ databaseInformationDescription[2] }}</p>
      <v-container>{{ databaseInformation.questionCount }}</v-container>
    </v-container>


    <v-container class="list-item">
      <p>{{ databaseInformationDescription[3] }}</p>
      <v-container>{{ databaseInformation.examCount }}</v-container>
    </v-container>


  </v-container>

  <v-table fixed-header :theme="themeType" style="height: 76vh">
    <thead>
    <tr >
      <th style = "width: 70%"><v-text-field style="min-width: 100px;padding-top: 20px" placeholder="Email" v-model="inputText" @input="filterList"></v-text-field></th>
      <th class="text-center" >Zresetuj hasło</th>
      <th class="text-center" >Usuń konto</th>
    </tr>
    </thead>
    <tbody>
    <tr v-for="item in filteredEmails" :key="item.name">
      <td>{{ item.userEmail }}</td>
      <td style="text-align: center;">
        <v-btn @click="resetPassword(item.userEmail)">Resetuj</v-btn>
      </td>
      <td style="text-align: center;">
        <v-btn @click="deleteUser(item.userEmail)">Usuń</v-btn>
      </td>
    </tr>

    </tbody>
  </v-table>
</template>

<script>
import AdminServices from "@/services/AdminServices.js";
import AuthServices from "@/services/auth.services.js";

export default {
  name: "AdminPanel",
  data() {
    return {
      inputText: '',
      themeType: this.$themeType,
      usersEmail: [],
      filteredEmails: [],
      databaseInformation: [],
      databaseInformationDescription: ["Rozmiar (MB)","Ilość użytkowników","Ilość pytań","Ilość egzaminów"],
    }
  },
  mounted() {
    this.fetchUsers();
    this.usersEmail = AdminServices.getAllUsers();
    AdminServices.getAllInformation()
    this.fetchDatabaseInformation();

  },
  methods: {
    deleteUser(userEmail){
      AdminServices.deleteUser(userEmail).then(() => {
        this.fetchUsers();
        this.fetchDatabaseInformation();
      })
    },
    resetPassword(userEmail){
      AdminServices.sendResetPasswordRequest(userEmail);
    },
    filterList() {
      let filtered =   this.usersEmail;

      if (this.inputText !== '') {
        filtered = filtered.filter((item) =>
            item.userEmail.toLowerCase().includes(this.inputText.toLowerCase())
        );
      }
      this.filteredEmails = filtered
    },
    handleLogout() {
      AuthServices.logout();
    },
    fetchUsers() {
      AdminServices.getAllUsers()
          .then(items => {
            if (items) {
              this.usersEmail = items;
              this.filteredEmails = items;
            } else {
              console.error('Nie udało się pobrać uzytkowników z endpointu.');
            }
          })
          .catch(error => {
            console.error('Błąd podczas pobierania danych:', error);
          });
    },
    fetchDatabaseInformation() {
      AdminServices.getAllInformation()
          .then(items => {
            if (items) {
              this.databaseInformation = items;
            } else {
              console.error('Nie udało się pobrać uzytkowników z endpointu.');
            }
          })
          .catch(error => {
            console.error('Błąd podczas pobierania danych:', error);
          });
    }
  },
}
</script>

<style scoped>
.v-container{
  max-width: none;
}

.list-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
  padding: 1rem;
}

.list-item {
  background-color: #282828;
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 1rem;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

table td {
  padding: 0.5rem;
  border: 1px solid gray;
}
table th {
  padding: 0.5rem;
  border: 1px solid gray;
}
</style>