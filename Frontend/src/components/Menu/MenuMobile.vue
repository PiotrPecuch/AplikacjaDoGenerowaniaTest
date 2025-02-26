<script>
import AuthServices from "@/services/auth.services.js";

export default {
  name: 'User',
  data() {
    return {
      themeType: this.$themeType,
      value: 0,
      primaryColor: 'var(--primary-color)',
      secondaryColor: 'var(--secondary-color)',
    }
  }, methods: {
    handleLogout() {
      AuthServices.logout();
    }
  }
}
</script>

<template >
  <v-layout>
    <v-bottom-navigation
        v-model="value"
        color=var(--secondary-color)
        class="barMenu"
        style=" border-top: 1px solid #222222; position: fixed; background: #212121; color:white"
    >
      <v-btn class="menu-button" small :to="{ path: '/main' }">
        <v-icon>mdi-table-column-width</v-icon>

        Moje testy
      </v-btn>

      <v-btn class="menu-button" :to="{ path: '/main/startedExams' }">
        <v-icon>mdi-note-multiple-outline</v-icon>

        Nadchodzące testy
      </v-btn>

      <v-btn class="menu-button" :to="{ path: '/main/finishedExams' }">
        <v-icon>mdi-note-text</v-icon>

        Zakończone testy
      </v-btn>

      <v-btn class="menu-button">
        <v-icon>mdi-account-settings</v-icon>

        Ustawienia
        <v-menu activator="parent" :theme="themeType">
          <v-list  style="border: 1px solid black;  text-align: center">
            <v-list-item-title style="cursor: pointer;" class="hovered-item">{{ name }}
            </v-list-item-title>
            <v-list-item-title style="cursor: pointer;" class="hovered-item" @click="handleLogout">Wyloguj
            </v-list-item-title>
          </v-list>
        </v-menu>
      </v-btn>
    </v-bottom-navigation>
  </v-layout>
</template>




<style scoped>
.dividerStyle{
  color:black;
  --v-border-opacity:100;
}


.barMenu {
  overflow: scroll;
  text-align: center;
}

.menu-button {
  font-size: 5px;
  flex-shrink: 1;
}

</style>