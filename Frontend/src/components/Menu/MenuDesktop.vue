<script>
import AuthServices from "@/services/auth.services.js";

export default {
  name: 'User',
  data() {
    return {
      value: 0,
      primaryColor: 'var(--primary-color)',
      secondaryColor: 'var(--secondary-color)',
    }
  }, methods: {
    handleLogout() {
      AuthServices.logout();
    },
  }
}
</script>

<template>
  <v-card>
    <v-layout >
      <v-navigation-drawer
          floating
          permanent
          style="min-width:300px; max-width:300px;"
      >

        <v-list
            density="compact"
            nav
        >
          <v-list-item prepend-icon="mdi-list-box-outline" :to="{ path: '/main' }" title="Przygotowane egzaminy"
                       value="moje-testy"></v-list-item>
          <v-list-item prepend-icon="mdi-calendar-clock-outline" :to="{ path: '/main/startedExams' }" title="Zaplanowane egzaminy"
                       value="rozpoczęte"></v-list-item>
          <v-list-item prepend-icon="mdi-calendar-lock" :to="{ path: '/main/finishedExams' }" title="Zakończone egzaminy"
                       value="zakończone"></v-list-item>

          <v-btn prepend-icon="mdi-account"  style="position: fixed; right:0;bottom: 0; width: 100%; border:none;">
            <v-menu activator="parent" style = "right:-50px;">
              <v-list style="left:-11px; text-align: center">
                <v-list-item-title style="cursor: pointer;" class="hovered-item" @click="handleLogout">Wyloguj
                </v-list-item-title>
              </v-list>
            </v-menu>
          </v-btn>
        </v-list>

      </v-navigation-drawer>

    </v-layout>
  </v-card>
</template>

<style scoped>
.dividerStyle {
  color: black;
  --v-border-opacity: 100;
}

.v-btn--variant-elevated {
  width: 100%;
  box-shadow: none;
  margin-left: -8px;
}
</style>