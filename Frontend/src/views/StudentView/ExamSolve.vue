<template>
  <!--  @mouseleave="handleMouseLeave"-->
  <v-container style="height:100vh;min-width: 100vw" @mouseleave="handleMouseLeave" :theme="themeType">


    <v-label style="color: white; overflow-x: hidden" class="d-flex justify-center align-center">
      <span>Pozostały czas: {{ hideTimer ? '--:-- ' : formattedTime }} </span>
      <span style="margin-left: 4px"> Pytanie {{ actualQuestionNumber }}/{{ numberOfQuestions }}</span>

      <span>
    <v-btn
        :text="hideTimer ? 'Pokaż zegar' : 'Ukryj zegar'"
        :type="hideTimer ? 'text' : 'outlined'"
        @click="hideTimer = !hideTimer"
        class="ml-2 mb-2">
    </v-btn>

  </span>
    </v-label>


      <v-container class="centered-container" fluid>
        <v-row align="center" justify="center">
          <!-- lewy panel -->
          <v-container v-if="questionAndAnswersData !== null" align="center" justify="center">
          <v-col cols="12" md="8" class="columns-container">
            <v-col cols="12" md="6" class="column left" style="overflow-x: hidden;">
              <v-container v-if="questionAndAnswersData && questionAndAnswersData.question" style="color:white;">
                {{ questionAndAnswersData.question.questionContent }}
              </v-container>


              <v-container
                  v-if="questionAndAnswersData.question.questionFile && isImage(questionAndAnswersData.question.questionFile.contentType)"
                  class="answer-image-container" style="display: flex; justify-content: center; align-items: center;">
                <img
                    @click="openImageModal(formattedBase64(questionAndAnswersData.question.questionFile.imageData, questionAndAnswersData.question.questionFile.contentType))"
                    :src="formattedBase64(questionAndAnswersData.question.questionFile.imageData, questionAndAnswersData.question.questionFile.contentType)"
                    alt="Image description"
                    style="max-width: 100%; height: auto; cursor: pointer;"
                    v-if="formattedBase64(questionAndAnswersData.question.questionFile.imageData, questionAndAnswersData.question.questionFile.contentType)"
                />
              </v-container>


              <v-container
                  v-else-if="questionAndAnswersData.question.questionFile && isPdf(questionAndAnswersData.question.questionFile.contentType)">
                <v-container v-if="isMobile">
                  Podgląd treści na urządzeniach mobilnych jest niedostępny.
                </v-container>
                <v-container v-else>
                  <div @mouseenter="handleIFrameBlurIn" @mouseleave="handleIFrameBlurOut"
                       style="resize: vertical; overflow: auto; width: 100%; height: 27vw; min-height: 400px; max-height: 800px; border: 1px solid #ccc;">
                    <embed
                        @click="handleIFrameBlur"
                        :src="formattedBase64(questionAndAnswersData.question.questionFile.imageData, questionAndAnswersData.question.questionFile.contentType)"
                        type="application/pdf"
                        width="100%"
                        height="100%"
                    />
                  </div>
                </v-container>
              </v-container>


              <v-container
                  v-else-if="questionAndAnswersData.question.questionFile  && questionAndAnswersData.question.questionFile.contentType && isText(questionAndAnswersData.question.questionFile.contentType)">
            <textarea
                :value="decodeBase64(questionAndAnswersData.question.questionFile.imageData)"
                readonly
                style=" resize:vertical; width: 100%; height: 29vw; color:white; overflow-y: auto; border: 1px solid #ccc;"
            />
              </v-container>
            </v-col>

            <v-divider v-if="$vuetify.display.mdAndUp" vertical class="divider"></v-divider>
            <v-divider v-else style="margin-top: 10px; margin-bottom: 10px; height: 3px;"></v-divider>


            <v-col cols="12" md="6" class="column right" style="overflow-x: hidden;">
              <v-container v-if="questionAndAnswersData.answer.length > 1">
                <v-card
                    :theme="themeType"
                    class="questionCard"
                    v-for="(item, i) in questionAndAnswersData.answer"
                    :key="i"
                    style=" margin-bottom: 10px"
                    @click="toggleSelection(item.answerId)"
                    :style="{ cursor: 'pointer', backgroundColor: isSelected(item.answerId) ? primaryColor : '#282828' }"
                >
                  <v-container class="w-100" style="justify-content: space-between;">
                    <v-container v-if="item.answerContent">
                      <p>{{ item.answerContent }}</p>
                    </v-container>

                    <v-container v-if="item.answerFiles && isImage(item.answerFiles.contentType)"
                                 class="answer-image-container"
                                 style="display: flex; justify-content: center; align-items: center;">
                      <img
                          @click.stop="openImageModal(formattedBase64(item.answerFiles.data, item.answerFiles.type))"
                          :src="formattedBase64(item.answerFiles.data, item.answerFiles.type)"
                          alt="Image description"
                          style="max-width: 100%; height: auto; cursor: pointer;"
                          v-if="formattedBase64(item.answerFiles.data, item.answerFiles.type)"
                      />
                    </v-container>
                  </v-container>
                </v-card>

              </v-container>
              <v-container v-else>
                <v-textarea
                    maxlength="3000"
                    counter
                    v-model="textAnswer"
                    label="Wprowadź odpowiedź"
                    outlined
                    auto-grow
                    :rows="1"
                    style="color: white"
                    class="white-counter"
                />
              </v-container>

            </v-col>
          </v-col>
          </v-container>
          <v-container v-else>
            Ładowanie
          </v-container>

        </v-row>

        <div v-if="imageDialog" class="image-modal" @click="closeImageModal">
          <img :src="dialogImage" class="modal-image" @click.stop/>
        </div>
      </v-container>
      <v-container class="d-flex justify-center align-center">
        <v-btn @click="getNextQuestion" :color="primaryColor" :theme="themeType" text="Następne pytanie"/>
      </v-container>


  </v-container>


  <v-container @mouseenter="handleMouseEnter" v-if="alertTimeInformation"
               style="position: absolute; top: 0; left: 0; width: 100vw; height: 100vh; background-color: RGBA(255,0,0,0.4); display: flex; justify-content: center; align-items: center;">
    <v-container
        readonly
        outlined
        style="font-size: 2rem;  -webkit-text-stroke-width: 2px;
  -webkit-text-stroke-color: black; color: white; text-align: center;"
    ><h1>MASZ<br> {{ emergencyCountdown }}s.<br> NA POWRÓT</h1></v-container>
  </v-container>


</template>


<script>
import ExamServices from "@/services/ExamServices.js";
import router from "@/router/index.js";

export default {
  data() {
    return {
      countdown: null,
      timer: null,
      emergencyTimer: null,
      emergencyCountdown: 240,
      selectedAnswers: [],
      numberOfQuestions: 0,
      user: {
        ssoLogin: null,
        ssoPassword: null,
      },
      questionAndAnswersData: null,
      themeType: this.$themeType,
      dialogImage: "",
      imageDialog: null,
      primaryColor: 'var(--secondary-color)',
      textAnswer: '',
      isMobile: false,
      userAlert: false,
      isPageActive: true,
      isAlertSent: false,
      visibilityChangeListener: null,
      iframeBlur: false,
      hideTimer: false,
      actualQuestionNumber: 1,
      alertTimeInformation: false,
    };
  },
  computed: {
    formattedTime() {
      const minutes = Math.floor(this.countdown / 60);
      const seconds = this.countdown % 60;
      return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
    }
  },
  mounted() {
    this.countdown = window.history.state?.countdown * 60;
    this.startTimer();
    this.visibilityChangeListener = this.handleVisibilityChange.bind(this);
    document.addEventListener("visibilitychange", this.visibilityChangeListener);
    window.addEventListener("blur", this.onUserInactive);
    this.getFirstQuestion();
  },
  methods: {
    handleIFrameBlurIn() {

      this.iframeBlur = true
    },
    handleIFrameBlurOut() {

      this.iframeBlur = false
    },
    startTimer() {
      if (!this.timer) {
        this.timer = setInterval(() => {
          if (this.countdown > 0) {
            this.countdown -= 1;
          } else {
            this.stopTimer();
            this.timeUp();
          }
        }, 1000);
      }
    },
    startEmergencyTimer(time) {
      this.emergencyCountdown = time;
      this.alertTimeInformation = true;
      if (!this.emergencyTimer) {
        this.emergencyTimer = setInterval(() => {
          if (this.emergencyCountdown > 0) {

            this.emergencyCountdown -= 1;
          } else {
            this.stopEmergencyTimer();
            this.userAlert = true;

            this.endExam();
          }
        }, 1000);
      }
    },
    stopEmergencyTimer() {

      if (this.emergencyTimer) {
        this.alertTimeInformation = false;
        clearInterval(this.emergencyTimer);
        this.emergencyTimer = null;
      }
    },
    onUserInactive() {
      if (!this.iframeBlur) {
        this.isPageActive = false;
        this.userAlert = true;
        this.endExam()
      }
    },
    stopTimer() {
      if (this.timer) {
        clearInterval(this.timer);
        this.timer = null;
      }
    },

    timeUp() {
      alert("Czas na rozwiązywanie dobiegł końca!");
      this.endExam()
    },
    getFirstQuestion() {
      ExamServices.getFirstQuestion(this.user)
          .then(items => {
            if (items) {
              this.questionAndAnswersData = items;

            } else {
              console.error('Nie udało się pobrać danych z endpointu.');
            }
          })
          .catch(error => {
            this.endExam()
            console.error('Błąd podczas pobierania danych:', error);
          });
    },

    toggleSelection(itemId) {
      const index = this.selectedAnswers.indexOf(itemId);
      if (index === -1) {
        this.selectedAnswers.push(itemId);
      } else {
        this.selectedAnswers.splice(index, 1);
      }
    },


    isSelected(i) {
      return this.selectedAnswers.includes(i);
    },


    isImage(type) {
      return type.substring(0, type.indexOf("/")) === 'image';
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
    openImageModal(image) {
      this.dialogImage = image;
      this.imageDialog = true;
    },

    closeImageModal() {
      this.imageDialog = false;
      this.dialogImage = "";
    },
    getNextQuestion() {


      ExamServices.getNextQuestion(this.user, this.selectedAnswers, this.textAnswer)
          .then(items => {
            if (items) {
              this.actualQuestionNumber+=1;
              this.selectedAnswers = [];
              this.textAnswer = "";
              this.questionAndAnswersData = items;
            } else {
              this.endExam()
            }
          })
    },
    checkIfMobile() {
      this.isMobile = window.innerWidth <= 1024;
    },
    handleMouseLeave() {
      this.startEmergencyTimer(3);
    },
    handleMouseEnter() {
      this.stopEmergencyTimer();
    },
    handleVisibilityChange() {
      this.userAlert = true;
      this.endExam()
    },
    async endExam() {
      if (!this.isAlertSent) {
        this.selectedAnswers = [];
        this.textAnswer = "";
        this.stopTimer();
        this.stopEmergencyTimer();
        this.isAlertSent = true;
        ExamServices.endExam(this.user, this.userAlert);

      }
    }
  },
  created() {
    this.numberOfQuestions = window.history.state?.numberOfQuestions;
    this.user.ssoLogin = window.history.state?.ssoLogin;
    this.user.ssoPassword = window.history.state?.ssoPassword;
    this.checkIfMobile();
  },
  beforeDestroy() {
    this.stopTimer();
    this.stopEmergencyTimer();
    this.questionAndAnswersData = [];
    this.userAlert = false;
    this.isAlertSent = false;
    this.user = "";
    document.removeEventListener("visibilitychange", this.handleVisibilityChange);
  }
};
</script>
<style>
.v-container {
  max-width: none;
}

.white-counter .v-counter {
  color: white;
}

.centered-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 80vh;
}

.columns-container {
  display: flex;
  align-items: stretch;
  position: relative;
  min-width: 85vw;
  width: 100%;
  height: 80vh;
  max-width: 800px;
  flex-direction: column;
  padding: 20px;
  border: 1px solid white;
  box-sizing: border-box;
  border-radius: 25px;
}


@media (min-width: 960px) {
  .columns-container {
    flex-direction: row;
  }
}

.column {
  padding: 16px;
  flex: 1;
  overflow: scroll;
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
</style>
