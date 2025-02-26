<template>
  <MenuBar/>
  <v-container class="questionMainContainer">

    <div v-if="userQuestionAndAnswer.length > 0">

      <v-row align="center" justify="center" style="margin-top : 0;height: 20px">

        <v-container class="elements">

          <v-col v-for= "(item, index1) in userQuestionAndAnswer" :key="index1" cols="auto" class="contentPanel">


            <v-card :theme="themeType" class="examCard   mx-auto" style="margin-bottom:50px">
              <v-card-item class="d-flex align-center" style="height:200px;">

                <v-card-item class="text-h6 mb-1">{{ item.examDescription }}</v-card-item>
                <v-card-item class="text-caption">
                  Pytanie: {{ item.question.questionContent }}
                </v-card-item>
                <v-card-item class="text-caption">
                  Uzyskane punkty: {{ item.points }} / {{ item.question.points }}
                </v-card-item>
                <v-card-actions class="text-caption">
                  Odpowiedzi:
                  <v-btn  @click="toggleExpanded(item.id)" icon>
                    <v-icon>{{ isExpanded(item.id) ? 'mdi-chevron-up' : 'mdi-chevron-down' }}</v-icon>
                  </v-btn>
                </v-card-actions>
              </v-card-item>




              <v-expand-transition class="answers" style="padding-bottom: 50px; ">
                <div v-if="isExpanded(item.id)" class="additional-info" >
                  <v-card-item v-if=item.question.questionFile>
                    Materiały do pytania:
                    <v-container v-if="item.question.questionFile && isImage(item.question.questionFile.contentType)"
                                 class="answer-image-container"
                                 style=" margin-left: 10px; height: 70%; justify-content: center; align-items: center;">
                      <img
                          @click="openImageModal(formattedBase64(item.question.questionFile.imageData, item.question.questionFile.contentType))"
                          :src="formattedBase64(item.question.questionFile.imageData, item.question.questionFile.contentType)"
                          alt="Błąd ze zdjęciem"
                          style="max-width: 100%; height: auto; cursor: pointer;"
                      />
                    </v-container>

                    <template v-else-if="item.question.questionFile && isPdf(item.question.questionFile.contentType)">
                      <div v-if="isMobile">
                        Podgląd pdf niemożliwy
                      </div>
                      <div v-else style="resize: vertical; overflow: auto; width: 100%; height: 100%; max-height: 800px; border: 1px solid #ccc;">
                        <embed
                            :src="formattedBase64(item.question.questionFile.imageData,item.question.questionFile.contentType)"
                            type="application/pdf"
                            width="100%"
                            height="100%"
                        />
                      </div>
                    </template>

                    <template v-else-if="item.question.questionFile && isText(item.question.questionFile.contentType)">
                      <textarea
                          :value="decodeBase64(item.question.questionFile.imageData,item.question.questionFile.contentType)"
                          readonly
                          style="width: 100%; height: 200px; resize: vertical; overflow-y: auto; border: 1px solid #ccc;"
                      />
                    </template>

                  </v-card-item>

                  <v-card-item v-if="item.userAnswers != null">
                    Odpowiedzi:
                    <v-card-item v-if="item.userTextAnswer != null">
                      {{item.userTextAnswer}}
                    </v-card-item>
                    <row  style="flex-grow: 1; display:flex;">
                      <v-col v-for="(answerItem, index) in item.userAnswers" :key="index" style="height: 70%;  flex-wrap: wrap">
                      <v-container v-if="!answerItem.answerFiles" class="image-label">
                        {{answerItem.answerContent}}<v-divider horizontal></v-divider>
                      </v-container>

                      <v-container v-if="answerItem.answerFiles && isImage(answerItem.answerFiles.contentType)"
                                   class="answer-image-container"
                                   style=" margin-left: 10px; height: 70%; justify-content: center; align-items: center;">
                        {{ answerItem.answerContent }}
                        <img
                            @click="openImageModal(formattedBase64(answerItem.answerFiles.data, answerItem.answerFiles.type))"
                            :src="formattedBase64(answerItem.answerFiles.data, answerItem.answerFiles.type)"
                            alt="Błąd ze zdjęciem"
                            style="max-width: 100%; height: auto; cursor: pointer;"
                        />
                      </v-container>

                </v-col>

                    </row>
                  </v-card-item>
                  <v-divider></v-divider>
                  <v-card-item v-if="item.correctAnswers != null">
                    Poprawne odpowiedzi:
                    <v-row style="flex-grow: 1; display:flex;" >
                      <v-col v-for="(questionItem, index) in item.correctAnswers" :key="index" style="flex-direction: column; height: 70%;">
                      <v-container v-if="!questionItem.answerFiles" class="image-label">
                        {{ questionItem.answerContent }}<v-divider horizontal></v-divider>

                      </v-container>
                        <v-container v-if="questionItem.answerFiles && isImage(questionItem.answerFiles.contentType)"
                                     class="answer-image-container"
                                     style=" margin-left: 10px; height: 70%; justify-content: center; align-items: center;">
                          {{ questionItem.answerContent }}
                          <img
                              @click="openImageModal(formattedBase64(questionItem.answerFiles.data, questionItem.answerFiles.type))"
                              :src="formattedBase64(questionItem.answerFiles.data, questionItem.answerFiles.type)"
                              alt="Błąd ze zdjęciem"
                              style="max-width: 100%; height: auto; cursor: pointer;"
                          />
                        </v-container>
                      </v-col>
                    </v-row>
                  </v-card-item>

                </div>
              </v-expand-transition>




              <v-card-actions style="justify-content: flex-end">
                <v-btn @click="openChangePointsDialog(item)">
                  Zmien ilość punktów
                </v-btn>
              </v-card-actions>



            </v-card>
          </v-col>
        </v-container>
      </v-row>
    </div>
    <div v-else>
      <v-card :theme="themeType" class="examCard   mx-auto" style="margin-bottom:50px;width:100%; text-align:center">
        <v-card-item  style="height:200px; ">
            Ładowanie...
        </v-card-item>
      </v-card>
    </div>
    <div v-if="imageDialog" class="image-modal" @click="closeImageModal">
      <img :src="dialogImage" class="modal-image" @click.stop/>
    </div>
  </v-container>



  <v-dialog  :theme="themeType"  :item="selectedItem"  v-model="changePointsDialog" class="changePointsDialog">
    <v-card style="width: 100%" prepend-icon="mdi-note" title="Zmien ilość punktów">
      <v-card-text>
        <v-row dense>
          <v-col md="16" sm="16">
            <!--            @keyup.enter="checkAndAddExam()"-->
            <v-number-input v-model="updatedPoints" label="Punkty" :min="0" :max="selectedItem?.question?.points" required></v-number-input>
          </v-col>
        </v-row>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text="Anuluj" variant="plain" @click="closeChangePointsDialog()"></v-btn>
        <v-btn :color="primaryColor" text="Zmień" variant="tonal" @click="handleChangePoints(selectedItem.question.questionId)"></v-btn>
      </v-card-actions>
      <v-divider></v-divider>
    </v-card>
  </v-dialog>


</template>


<script>
import MenuBar from "@/components/MenuBar.vue";
import ExamServices from "@/services/ExamServices.js";
import { VNumberInput } from 'vuetify/labs/VNumberInput'
import questionServices from "@/services/QuestionServices.js";

export default {
  components: {
    MenuBar,
    VNumberInput
  },
  props: ['id', 'ssoLogin', 'ssoPassword'],
  data() {
    return {
      themeType: this.$themeType,
      userQuestionAndAnswer: {},
      primaryColor: 'var(--secondary-color)',
      expandedIndexes: [],
      imageDialog: false,
      changePointsDialog: false,
      updatedPoints: 0,
      selectedItem: null,
    }
  },
  created() {
    this.fetchAllUsersAnswers();
  },
  methods: {
    openChangePointsDialog(item){
      this.selectedItem=item;
      this.changePointsDialog = true;

    },
    closeChangePointsDialog(){
      this.selectedItem=null;
      this.changePointsDialog = false;

    },
    toggleExpanded(index) {
      const idx = this.expandedIndexes.indexOf(index);
      if (idx === -1) {
        this.expandedIndexes.push(index);
      } else {
        this.expandedIndexes.splice(idx, 1);
      }
    },
    isExpanded(index) {
      return this.expandedIndexes.includes(index);
    },
    decodeBase64(data) {
      return atob(data);
    },
    isImage(type) {
      return type.substring(0, type.indexOf("/")) === 'image';
    },formattedBase64(data, type) {
      return `data:${type};base64,${data}`;
    },
    isPdf(type) {
      return type === 'application/pdf';
    },

    isText(type) {
      return type === 'text/plain';
    },
    openImageModal(image) {
      this.dialogImage = image;
      this.imageDialog = true;
    },

    closeImageModal() {
      this.imageDialog = false;
      this.dialogImage = "";
    },
    handleChangePoints(questionId){

      questionServices.changePoints(questionId, this.ssoLogin, this.ssoPassword, this.updatedPoints).then(()=>{
        this.fetchAllUsersAnswers();
        this.changePointsDialog = false;
        this.updatedPoints = 0;
      });
    },
    fetchAllUsersAnswers() {

      ExamServices.getAllUserAnswers(this.id, this.ssoLogin, this.ssoPassword)

          .then(items => {
            if (items) {
              this.userQuestionAndAnswer = items;

            } else {
              console.error('Nie udało się pobrać danych z endpointu.');
            }
          })
          .catch(error => {
            console.error('Błąd podczas pobierania danych:', error);
          });
    },
  }
};
</script>

<style scoped>
.changePointsDialog >>> .v-overlay__scrim {
  background-color: black;
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




@media (max-width: 1024px) {

  .v-container{
    padding:0
  }

  .changePointsDialog {
    max-width: 100%;
  }



}

@media (min-width: 1024px) {
  .questionMainContainer {
    margin-left: 300px;
    width: calc(100% - 300px);
    margin-bottom: 2em;
  }
  .changePointsDialog {
    max-width: 500px;
  }

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
</style>








