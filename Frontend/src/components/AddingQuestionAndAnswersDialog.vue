<template>
  <div class="pa-4 text-center">
    <v-dialog v-model="dialog" max-width="600" max-height="750">
      <template v-slot:activator="{ props: activatorProps }">
        <div style="position: relative;">
          <v-btn class="floatingButton" v-bind="activatorProps">
            <v-icon>mdi-playlist-plus</v-icon>
            <v-tooltip
                activator="parent"
                location="start"
            >Dodaj pytanie</v-tooltip>

          </v-btn>
        </div>
      </template>

      <v-card prepend-icon="mdi-pencil" title="Kreator pytania" :theme="themeType">
        <v-card-text>
          <v-row dense>
            <v-container style="font-size: 0.875rem; margin:0">Treść
              <v-col cols="24" md="12" sm="12">
                <v-text-field label="Pytanie" v-model="question" required></v-text-field>
              </v-col>
            </v-container>

            <v-divider></v-divider>

            <v-container style="font-size: 0.875rem; margin:0">Załącznik (PDF/IMG/TXT)
              <v-col cols="24" md="12" sm="12">
                <v-file-input
                    accept="image/*,.pdf,.txt"
                    v-model="questionAttachment"
                    prepend-icon=""
                    label="Wstaw załącznik (Maksymalny rozmiar 16MB)"
                ></v-file-input>
              </v-col>
             Ilość punktów za poprawną odpowiedź (Maks. 50)
              <v-col cols="24">
                <v-text-field
                    :label="`Pkt`"
                    type="number"
                    required
                    v-model="questionPoints"
                    :min="0"
                    :max="maxPoints"
                    @input="validatePoints()"
                ></v-text-field>
              </v-col>
            </v-container>

            <v-divider></v-divider>

            <v-container style="font-size: 0.875rem; margin:0">
              Odpowiedzi, zaznacz pole, przy tych, które są poprawne.
              <br />Jeżeli pozostawisz puste, będzie wymagana odpowiedź pisemna.
            </v-container>

            <v-col cols="24" md="12" sm="12" v-for="(answer, index) in answers" :key="index">
              <v-row>
                <v-container style="display: flex; flex-wrap: wrap">
                  <v-col cols="1">
                    <v-checkbox v-model="answer.correct"></v-checkbox>
                  </v-col>
                  <v-col cols="10">
                    <v-text-field
                        :label="`Odpowiedź ${index + 1}`"
                        type="text"
                        required
                        v-model="answer.text"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="1">
                    <v-icon style="margin-top:1em;" @click="deleteAnswer(index)">mdi-delete</v-icon>
                  </v-col>
                  <v-col cols="24" md="12" sm="12">
                    <v-file-input
                        accept="image/*"
                        v-model="answer.attachment"
                        clearable
                        label="Wstaw zdjęcie (Maksymalny rozmiar 16MB)"
                        variant="underlined"
                    ></v-file-input>
                  </v-col>
                </v-container>
              </v-row>
              <v-divider></v-divider>
            </v-col>

            <v-container style="position: relative">
              <v-btn
                  @click="addAnswer"
                  style="position: absolute; right: 0;"
                  :color="primaryColor"
                  text="Dodaj odpowiedź"
                  variant="tonal"
              ></v-btn>
              <br />
            </v-container>
          </v-row>
        </v-card-text>

        <v-divider></v-divider>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn text="Zamknij" variant="plain" @click="dialog = false"></v-btn>
          <v-btn :color="primaryColor" @click="saveQuestion(this.$route.params.id)" text="Zapisz" variant="tonal"></v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import QuestionServices from "@/services/QuestionServices.js";
import notificationService from "@/services/NotificationService.js";

export default {
  data() {
    return {
      themeType: this.$themeType,
      dialog: false,
      primaryColor: "var(--secondary-color)",
      answers: [{ text: "", attachment: null, correct: false}],
      question: "",
      questionPoints: null,
      questionAttachment: null,
      maxPoints: 50,
    };
  },
  methods: {
    validatePoints() {
      if (this.questionPoints > this.maxPoints) {
        this.questionPoints = this.maxPoints;
      }
      if (this.questionPoints < 0) {
        this.questionPoints = 0;
      }
    },
    addAnswer() {
      this.answers.push({ text: "", attachment: null, correct: false});
    },
    deleteAnswer(index) {
      this.answers.splice(index, 1);
    },
    async saveQuestion(examId) {
      if (!this.question.trim()) {
        notificationService.notification("error",'Pytanie jest wymagane.');
        return;
      }
      if(!this.questionPoints){
        notificationService.notification("error",'Dodaj ilość punktów.');
        return;
      }

      const questionsArray = [{
        questionContent: this.question,
        questionAttachment: this.questionAttachment,
        questionPoints: this.questionPoints,
        answerList: this.answers.map(answer => ({
          answerContent: answer.text,
          correct: answer.correct,
          attachment: answer.attachment,
        })),
      }];

      try {
        await QuestionServices.addQuestions(examId, questionsArray);
        this.dialog = false;
        this.$emit('questions-saved');
      } catch (error) {
        console.error("Błąd podczas zapisywania pytania:", error);
      }
    },
    resetForm() {
      this.question = "";
      this.questionAttachment = null;
      this.answers = [{ text: "", attachment: null, correct: false}];
      this.questionPoints = null;
    }
  },
  watch: {
    dialog(value) {
      if (!value) {
        this.resetForm();
      }
    }
  }
};
</script>

<style scoped>
.floatingButton {
  background-color: var(--secondary-color);
  position: fixed;
  color: white;
  min-width: 20px;
  width: 3rem;
  height: 3rem !important;
  z-index: 1000;
  right: 1rem;
  border-radius: 45%;
  transition: background-color 0.3s;
}

.floatingButton:hover {
  background-color: #a65809;
}

@media (max-width: 1024px) {
  .floatingButton {
    bottom: 5rem;
  }
}

@media (min-width: 1024px) {
  .floatingButton {
    bottom: 2rem;
  }
}
</style>
