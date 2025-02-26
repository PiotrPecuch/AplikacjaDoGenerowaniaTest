<template>
  <v-dialog v-model="isVisible" persistent max-width="500px" >
    <v-card :theme="themeType">
      <v-card-title>
        <span class="headline">Regulamin Egzaminu</span>
      </v-card-title>
      <v-card-text>
        1. Po rozpoczęciu egzaminu nie ma możliwości jego przerwania ani wznowienia w późniejszym terminie. <br>
        2. Jeśli opuścisz stronę podczas egzaminu, egzamin zostanie automatycznie przerwany i unieważniony. <br>
        3. Czas na rozwiązanie egzaminu jest ograniczony i wskazany przed jego rozpoczęciem. Po upływie czasu egzamin zakończy się automatycznie. <br>
        4. Użytkownik jest zobowiązany do samodzielnego rozwiązywania zadań, bez korzystania z pomocy osób trzecich. <br>
        5. Stabilne połączenie internetowe jest wymagane do prawidłowego przeprowadzenia egzaminu. <br>
        6. Odpowiedź nie może zostać zmieniona po przejściu dalej. <br>
      </v-card-text>
        <v-divider></v-divider>
        <v-card-title>
          Informacje<br>
        </v-card-title>
      <v-card-text>
        1. Opis egzaminu: {{examData.examDescription}}. <br>
        2. Czas na rozwiązanie to {{examData.time}} minut. <br>
        3. Pytań w egzaminie jest: {{examData.numberOfQuestion}}.
      </v-card-text>
      <v-card-actions>
        <v-btn @click="close">Odrzuć</v-btn>
        <v-btn @click="accept">Akceptuję</v-btn>

      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  props: {
    examData: Object,
  },
  data() {
    return {
      themeType: this.$themeType,
      isVisible: false,
    };
  },
  methods: {
    accept() {
      this.$emit('accepted');
      this.isVisible = false;
    },
    close() {
      this.$emit('close');
      this.isVisible = false;
    },
  },
  mounted() {
    this.isVisible = true;
  }
};
</script>
