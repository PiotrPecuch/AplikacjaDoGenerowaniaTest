<template>
  <MenuBar />
  <v-container class="questionMainContainer">
    <v-container class="stickyQuestionHeader">
      <v-container class="question-information">
        Edytowanie egzaminu: {{ examName }}
      </v-container>
    </v-container>

    <v-container v-if="questions.length > 0">
      <v-container class="questionContent">
        <v-card
            class="questionCard"
            v-for="(item, i) in questions"
            :key="i"
            style="margin-bottom: 10px"
            :theme="themeType"
        >
          <v-container>
            <v-container class="d-flex w-100" style="justify-content: space-between;">
              <v-icon @click="toggleAnswers(item, i)" style="cursor: pointer;">
                {{ item.showAnswers ? 'mdi-chevron-up' : 'mdi-chevron-down' }}
              </v-icon>

              <div class="text-h10 mb-1 pl-5" style="flex-grow: 1;">{{ item.questionContent }}</div>

              <div class="d-flex" style="align-items: center; min-width: 150px; justify-content: flex-end;">
                <v-divider :thickness="2" style="margin-right: 15px;" vertical></v-divider>
                <span style="min-width: 50px; margin-right:5px; text-align: right; white-space: nowrap;">Pkt. {{ item.points }}</span>
                <v-btn :color="primaryColor" text="Usuń" variant="tonal" @click="handleDeleteQuestion(item)"></v-btn>
              </div>
            </v-container>

            <v-container v-if="item.showAnswers" class="answers-container" style="width: 100%;">
              <v-container>
                <v-row v-if="item.questionFile">
                  Materiał do pytania:
                  <v-col cols="12">
                    <template v-if="item.questionFile && item.questionFileType && isImage(item.questionFileType)">
                      <img
                          @click="openImageModal(formattedBase64(item.questionFile, item.questionFileType))"
                          :src="formattedBase64(item.questionFile, item.questionFileType)"
                          alt="Image description"
                          style="max-width: 100%; height: auto; max-height: 400px; cursor: pointer;"
                      />
                    </template>

                    <template v-else-if="item.questionFile && item.questionFileType && isPdf(item.questionFileType)">
                      <div v-if="isMobile">
                        Podgląd pdf niemożliwy
                      </div>
                      <div v-else style="resize: vertical; overflow: auto; width: 100%; height: 100%; min-height: 400px; max-height: 800px; border: 1px solid #ccc;">
                        <embed
                            :src="formattedBase64(item.questionFile, item.questionFileType)"
                            type="application/pdf"
                            width="100%"
                            height="100%"
                        />
                      </div>
                    </template>

                    <template v-else-if="item.questionFile && item.questionFileType && isText(item.questionFileType)">
                      <textarea
                          :value="decodeBase64(item.questionFile)"
                          readonly
                          style="width: 100%; height: 200px; resize: vertical; overflow-y: auto; border: 1px solid #ccc;"
                      />
                    </template>

                    <template v-else>
                      <div>Nieobsługiwany typ pliku: {{ item.questionFileType }}</div>
                    </template>
                  </v-col>
                  <v-divider></v-divider>
                </v-row>
              </v-container>
              <v-container v-if="item.answers.length === 0">
                Wymagana odpowiedź pisemna.
              </v-container>
              <v-row  v-for="(answer, answerIndex) in item.answers" :key="answerIndex" style="display: flex; overflow:auto; flex-grow: 1; width:100%">
                <v-col cols="12" style="padding: 0;">
                  <v-container
                      :style="{
                        backgroundColor: answer.correct ? 'rgba(74, 240, 99, 0.1)' : 'rgba(240, 74, 74, 0.1)',
                        padding: '5px',
                        borderRadius: '4px',
                        width: '100%'
                      }"
                  >
                    <span style="display: block; width: 100%;">{{ answer.answerContent }}</span>
                    <v-divider v-if="answer.answerContent" />

                    <v-container v-if="answer.answerFiles" class="image-label">
                      {{ String.fromCharCode(65 + answerIndex) }}
                    </v-container>

                    <v-container v-if="answer.answerFiles && isImage(answer.answerFiles.contentType)" class="answer-image-container" style=" width: 25%;display: flex; justify-content: center; align-items: center;">
                      <img
                          @click="openImageModal(formattedBase64(answer.answerFiles.data, answer.answerFiles.contentType))"
                          :src="formattedBase64(answer.answerFiles.data, answer.answerFiles.contentType)"
                          alt="Image description"
                          style="max-width: 100%; height: auto; cursor: pointer;"
                      />
                    </v-container>

                    <v-container v-else-if="answer.answerFiles">
                      <div>Nieobsługiwany typ pliku: {{ answer.answerFiles.contentType }}</div>
                    </v-container>
                  </v-container>

                </v-col>
              </v-row>

            </v-container>
          </v-container>
        </v-card>
      </v-container>
    </v-container>

    <div v-else>
      <br />
      <v-container class="questionContent">
        <v-col cols="auto">
          <v-card :theme="themeType" class="questionCard">
            <v-card-item class="d-flex align-center justify-center">
              <v-container style="cursor: pointer" class="text-h10 mb-1 text-center" @click="importDialog = true">
                Brak pytań, kliknij <u>tutaj</u> aby je importować.
              </v-container>
            </v-card-item>
          </v-card>
        </v-col>
      </v-container>
    </div>

    <!-- Modal for Image Enlargement -->
    <div v-if="imageDialog" class="image-modal" @click="closeImageModal">
      <img :src="dialogImage" class="modal-image" @click.stop />
    </div>

    <!-- Modal for PDF Display -->
    <v-dialog v-model="pdfDialog" max-width="800px">
      <v-card>
        <v-toolbar flat dark color="primary">
          <v-toolbar-title>PDF Viewer</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon @click="closePdfModal">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-toolbar>
        <v-card-text>
          <iframe :src="pdfSource" width="100%" height="600px" style="border: none;"></iframe>
        </v-card-text>
      </v-card>
    </v-dialog>


    <!-- Import Questions Dialog -->
    <v-dialog  v-model="importDialog" max-width="500px">
      <v-card :theme="themeType">
        <v-card-title class="headline">Importuj pytania</v-card-title>
        <v-card-text>
          <v-file-input v-model="importFile" label="Wybierz plik"/>
        </v-card-text>
        <v-card-actions>
          <v-btn text @click="importDialog = false">Zamknij</v-btn>
          <v-btn variant="tonal" :color="primaryColor" @click="handleImport" :readonly="isLoading">
            <template v-if="isLoading">
              <v-progress-circular indeterminate color="white" size="24" class="mr-2"></v-progress-circular>
              Importowanie...
            </template>
            <template v-else>
              Importuj
            </template>
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>


  </v-container>
  <QuestionAndAnswersDialog :dialog="dialog" @questions-saved="fetchQuestions" @click="dialog = true" />
</template>

<script>
import MenuBar from "@/components/MenuBar.vue";
import QuestionServices from "@/services/QuestionServices.js";
import QuestionAndAnswersDialog from "@/components/AddingQuestionAndAnswersDialog.vue";
import NotificationService from "@/services/NotificationService.js";

export default {
  components: {
    MenuBar,
    QuestionAndAnswersDialog,
  },
  data() {
    return {
      questions: [],
      dialog: false,
      themeType: this.$themeType,
      examId: this.$route.params.id,
      examName: this.$route.query.name,
      imageDialog: false,
      dialogImage: "",
      pdfDialog: false,
      pdfSource: "",
      isMobile: false,
      primaryColor: 'var(--secondary-color)',
      importDialog: false,
      importFile: null,
      isLoading: false,

    };
  },
  created() {
    this.fetchQuestions();
    this.checkIfMobile();
  },
  mounted() {
    window.addEventListener("resize", this.checkIfMobile);
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.checkIfMobile);
  },
  methods: {

    fetchQuestions() {
      QuestionServices.getAllQuestions(this.examId)
          .then((questions) => {
            this.questions = questions.map((question) => ({
              ...question,
              showAnswers: false,
              answers: [],
            }));
          })
          .catch((error) => {
            console.error("Error fetching questions:", error);
          });
    },
    handleImport() {
      if (!this.importFile) {
        NotificationService.notification("error", "Proszę wybrać plik do importu.");
      } else {
        this.isLoading = true;
        QuestionServices.answersImport(this.examName, this.importFile)
            .then(() => {
              this.importFile = null;
              this.importDialog = false;
              this.fetchQuestions();
            })
            .catch(error => {
              console.error("Import error:", error);
              NotificationService.notification("error", "Wystąpił błąd podczas importowania.");
            })
            .finally(() => {
              this.isLoading = false;
            });
      }
    },
    async toggleAnswers(question, index) {
      try {
        if (question.answers.length === 0) {
          const response = await QuestionServices.getAnswers(this.examName, question.questionContent, question.questionId);

          if (response && response.answers) {
            this.questions[index].answers = response.answers;


            if (response.questionFile && response.questionFileType) {
              this.questions[index].questionFile = response.questionFile;
              this.questions[index].questionFileType = response.questionFileType;
            } else {
              this.questions[index].questionFile = null;
              this.questions[index].questionFileType = null;
            }
          } else {
            console.error("Unexpected response structure:", response);
            NotificationService.notification("error", "Unexpected response structure.");
          }
        }

        this.questions[index].showAnswers = !this.questions[index].showAnswers;


      } catch (error) {
        console.error("Error fetching answers:", error);
        NotificationService.notification("error", "Error fetching answers.");
      }
    },
    openImageModal(image) {
      this.dialogImage = image;
      this.imageDialog = true;
    },

    closeImageModal() {
      this.imageDialog = false;
      this.dialogImage = "";
    },

    closePdfModal() {
      this.pdfDialog = false;
      this.pdfSource = "";
    },

    isImage(type) {
      return type.startsWith('image/');
    },

    isPdf(type) {
      return type === 'application/pdf';
    },

    isText(type) {
      return type === 'text/plain';
    },

    formattedBase64(data, type) {

      return `data:${type};base64,${data}`;
    },

    decodeBase64(data) {
      return atob(data);
    },

    checkIfMobile() {
      this.isMobile = window.innerWidth <= 768;
    },

    async handleDeleteQuestion(question) {
      try {
        await QuestionServices.deleteAnswer(this.examName, question.questionContent,question.questionId);
        this.fetchQuestions();
      } catch (error) {
        console.error("Delete error!!", error);
      }
    },
  },
};
</script>


<style scoped>
.stickyQuestionHeader {
  background-color: #181818;
  width: 100%;
  text-align: center;
  font-weight: bold;
  font-size: 1em;
  flex-wrap: wrap;
  position: sticky;
  margin-top: -17px;
  top: 0;
  z-index: 1;
  padding-left: 12px;
}


.answers-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: flex-end;
  background-color: rgba(0, 0, 0, 0.05);
  padding: 10px;
  border-radius: 5px;
}


.answer-image-container {
  width: 150px;
  height: 150px;
  overflow: hidden;
  border-radius: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}


.image-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
}


.modal-image {
  max-width: 90%;
  max-height: 90%;
}


.questionMainContainer {
  padding: 20px;
}


.stickyQuestionHeader {
  position: sticky;
  top: 0;
  z-index: 10;
  border-bottom: 1px solid #ccc;
}






@media (max-width: 1024px) {
  * {
    margin: auto;
  }


  .stickyQuestionHeader {
    justify-content: center;
  }


  .questionContent{
    position: absolute;
    left: 0;


  }
  .questionMainContainer {
    width: 100%;
    margin-top: -4em;
  }


  .question-information {
    color: white;
    margin-left: -15px;
    text-align: center;
  }
  .addDialog{
    max-width: 100%;
  }
}


@media (min-width: 1024px) {
  .questionMainContainer {
    margin-left: 300px;
    width: calc(100% - 300px);
    margin-bottom: 2em;
  }


  .stickyQuestionHeader {
    justify-content: left;
  }


  .question-information {
    color: white;
    margin-left: -15px;
    width: 100%;
  }


  @media (min-width: 1280px) {
    .v-container {
      max-width: none;
    }
  }


  @media (min-width: 960px) {
    .v-container {
      max-width: none;
    }
  }




  .image-modal {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.9);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 999;
  }


  .modal-image {
    max-width: 90%;
    max-height: 90%;
    border: 5px solid white;
  }
}
</style>







