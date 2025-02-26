<script>
import router from "@/router/index.js";

export default {
  name: "ExamResults",
  methods: {
    router() {
      return router
    }
  },
  data() {
    return {
      themeType: this.$themeType,
      examDetails: null,
    };
  },
  mounted() {
    const response = sessionStorage.getItem('endExamResponse');
    if (response) {
      this.examDetails = response ? JSON.parse(response) : null;
      sessionStorage.clear();

    } else {

    }
    sessionStorage.clear();
  }
}
</script>
<template>
  <v-container class="center-container">
    <v-card :theme="themeType" class="center-card">
      <v-card-title>
        <span class="headline">Podsumowanie</span>
      </v-card-title>
      <v-card-text>
        <span v-if="examDetails">
          Nr.indexu: <span>{{ examDetails.indexNumber }}</span><br>
          Ilość punktów z egzaminu: <span>{{ examDetails.pointsFromExam }}</span> / {{ examDetails.percentageOfPoints }}%
        </span>
        <span v-else>Błąd podczas ładowania danych, zaloguj się ponownie.</span>
      </v-card-text>
      <v-card-actions>
        <v-btn @click="$router.push('/exam')">Powrót</v-btn>
      </v-card-actions>
    </v-card>
  </v-container>
</template>

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
