<template>
  <!--  Menu bar-->
  <MenuBar></MenuBar>


  <!--        Exam list and searchbar-->
  <div v-if="originalExam.length > 0">



    <v-row class="rowElements" align="center" justify="center" style="margin-top : 0; ">
      <v-container style="height: 100vh;">
      <SearchBar :items="originalExam" @update:filtered="updateExamList" />

      <v-container class="elements" :theme="themeType">
        <v-col v-for="item in updatedExam" cols="auto" class="contentPanel">

          <v-card :theme="themeType" class="examCard   mx-auto">
            <v-card-item class="d-flex align-center" style="height:200px">
              <v-card-item class="text-h6 mb-1">{{ item.examName }} <v-icon @click="openExamChangeDialog(item)" class="hover-icon" style="scale: 0.6;">mdi-pencil</v-icon></v-card-item>
              <v-card-item class="text-caption">
                Data utworzenia: {{ item.creationDate }}, {{ item.creationTime }}
              </v-card-item>
              <v-card-item class="text-caption" >
                Data modyfikacji: {{ item.modificationDate!==null ? item.modificationDate + ', ' + item.modificationTime : "Brak modyfikacji" }}
              </v-card-item>
              <v-card-item class="text-caption">
                Ilość pytań: {{ item.numberOfQuestions }}
              </v-card-item>
            </v-card-item>


            <v-card-actions style="justify-content: flex-end; flex-wrap: wrap; text-align: center">
              <v-btn @click="showGeneratePDFDialog(item)">
                Generuj PDF
              </v-btn>
              <v-divider vertical/>
              <v-btn @click="showGenerationDialog(item)">
                Zaplanuj
              </v-btn>
              <v-divider vertical/>
              <v-btn :to="{ path: '/main/edit/' + item.examId, query: { name: item.examName }}"> Edytuj</v-btn>
              <v-divider vertical/>
              <v-btn @click="showDeleteDialog(item)">Usuń
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-container>
      </v-container>
    </v-row>

  </div>

<!--  No exam information-->
  <div v-else>
    <v-row class="rowElements" justify="center" style="margin-top : 0;height: 20px; text-align:center">

      <v-container class="elements">
        <v-col cols="auto" class="contentPanel">

          <v-card :theme="themeType" class="examCard  mx-auto">
            <v-card-item class="align-center" style="height:200px; width: calc(100vw - 300px);">
              <div class="text-h6 mb-1">Brak przygotowanych egzaminów.</div>
            </v-card-item>

          </v-card>
        </v-col>
      </v-container>
    </v-row>
  </div>

<!--  Add exam icon-->
  <div style="position: relative;">
    <v-btn @click="addDialog = true" class="floatingButton">
      <v-icon>mdi-plus</v-icon>
      <v-tooltip
          activator="parent"
          location="start"
      >Stwórz egzamin</v-tooltip>
    </v-btn>
  </div>


<!--  Add exam dialog-->
  <v-dialog v-model="addDialog" class="addDialog">
    <v-card :theme="themeType" style="width: 100%" prepend-icon="mdi-note" title="Stwórz egzamin">
      <v-card-text>
        <v-row dense>
          <v-col md="16" sm="16">
            <v-text-field label="Nazwa egzaminu*" maxlength="50" counter @keyup.enter="checkAndAddExam()"
                          v-model="examName" required></v-text-field>
          </v-col>
        </v-row>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text="Anuluj" variant="plain" @click="addDialog = false"></v-btn>
        <v-btn :color="primaryColor" text="Utwórz" variant="tonal" @click="checkAndAddExam()"></v-btn>
      </v-card-actions>
      <v-divider></v-divider>
    </v-card>
  </v-dialog>


<!--  Change exam name dialog-->
  <v-dialog v-model="changeExamNameDialog" class="addDialog">
    <v-card :theme="themeType" style="width: 100%" prepend-icon="mdi-note" title="Zmień nazwę">
      <v-card-text>
        <v-row dense>
          <v-col md="16" sm="16">
            <v-text-field label="Nowa nazwa egzaminu*" maxlength="50" counter @keyup.enter="changeExamName(examName)"
                          v-model="examName" required></v-text-field>
          </v-col>
        </v-row>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text="Anuluj" variant="plain" @click="closeExamChangeDialog"></v-btn>
        <v-btn :color="primaryColor" text="Zmień" variant="tonal" @click="changeExamName(examName)"></v-btn>
      </v-card-actions>
      <v-divider></v-divider>
    </v-card>
  </v-dialog>


<!--  Delete exam dialog-->
  <v-dialog v-model="deleteDialog" class="deleteDialog">
    <v-card :theme="themeType" style="width: 100%" prepend-icon="mdi-delete-empty" title="Usuń egzamin">
      <v-card-text>
        <v-row dense>
          <v-col md="12" sm="12">
            <v-card-text>Czy na pewno chcesz usunąć egzamin <b>{{ examToDelete.examName }}</b>, spowoduje to również
              usunięcie wszystkich przeprowadzonych egzaminów z historii.
            </v-card-text>
          </v-col>
        </v-row>
      </v-card-text>
      <v-card-actions style="margin: unset">
        <v-spacer></v-spacer>
        <v-btn text="Anuluj" variant="plain" @click="deleteDialog = false"></v-btn>
        <v-btn :color="primaryColor" text="Usuń" variant="tonal" @click="handleDeleteExam()"></v-btn>
      </v-card-actions>
      <v-divider></v-divider>
    </v-card>
  </v-dialog>


<!--  PDF exam genration dialog-->
  <v-dialog v-model="examToPDFDialog" class="deleteDialog">
    <v-card :theme="themeType" style="width: 100%" prepend-icon="mdi-table-arrow-down" title="Generuj PDF">
      <v-card-text>
        Wygenerowany egzamin nie posiada plików (IMG, PNG, PDF), jeżeli liczba pytań bez plików nie będzie wystarczająca, ich ilość może ulec zmianie.
      </v-card-text>
      <v-card-text>
        <v-row dense>
          <v-col md="12" sm="12">
              <v-number-input v-model=questionAmmount :min="0"  :max="examToPDF.numberOfQuestions"></v-number-input>
          </v-col>
        </v-row>
      </v-card-text>
      <v-card-actions style="margin: unset">
        <v-spacer></v-spacer>
        <v-btn text="Anuluj" variant="plain" @click="examToPDFDialog = false"></v-btn>
        <v-btn :color="primaryColor" text="Generuj" variant="tonal" @click="handleGeneratePDFExam()"></v-btn>
      </v-card-actions>
      <v-divider></v-divider>
    </v-card>
  </v-dialog>

<!--  Exam generation dialog-->
  <v-dialog v-model="examGenerationDialog" class="generationDialog">
    <v-card :theme="themeType" style="width: 100%" prepend-icon="mdi-test-tube " title="Generuj egzamin">
      <v-card-text>
        <v-row dense>
          <v-col md="12" sm="12">
            <v-text-field
                label="Nazwa egzaminu"
                v-model="examToGenerate.examName"
                readonly
            ></v-text-field>
            <v-text-field
                label="Opis"
                v-model="examDescription"
            ></v-text-field>
            <v-file-input v-model="studentsList" label="Lista studentów"/>
            Data rozpoczęcia dołączania
            <v-divider/>
            <br>


            <v-row justify="space-around">
              <v-col cols="12" sm="6">
                <v-text-field
                    v-model="pickedTime1"
                    placeholder="Godzina"
                    prepend-icon="mdi-clock-time-four-outline"
                    readonly
                >
                  <v-menu
                      v-model="time1"
                      :close-on-content-click="false"
                      activator="parent"
                      transition="scale-transition"
                  >
                    <v-time-picker
                        :format="'24hr'"
                        v-model="pickedTime1"
                        full-width
                        :max="pickedTime2"
                        :color="primaryColor"
                        @click:save="time1 = false"
                    ></v-time-picker>
                  </v-menu>
                </v-text-field>


              </v-col>

              <v-col cols="12" sm="6">
                <v-text-field
                    :value="formatPickedDate(pickedDate1)"
                    prepend-icon="mdi-calendar"
                    placeholder="Data"
                    readonly
                >
                  <v-menu
                      v-model="date1"
                      :close-on-content-click="false"
                      activator="parent"
                      transition="scale-transition"
                  >
                    <v-date-picker
                        :color="primaryColor"
                        type="date"
                        :min="minDate"
                        :max="pickedDate2"
                        v-model="pickedDate1"
                        @click:save="date1 = false"
                    ></v-date-picker>
                  </v-menu>
                </v-text-field>
              </v-col>
            </v-row>

            Data zakończenia dołączania
            <v-divider/>
            <br>


            <v-row justify="space-around">
              <v-col cols="12" sm="6">
                <v-text-field
                    v-model="pickedTime2"
                    placeholder="Godzina"
                    prepend-icon="mdi-clock-time-four-outline"
                    readonly
                >
                  <v-menu
                      v-model="time2"
                      :close-on-content-click="false"
                      activator="parent"
                      transition="scale-transition"
                  >
                    <v-time-picker
                        :format="'24hr'"
                        :color="primaryColor"
                        v-model="pickedTime2"
                        full-width
                        :min="pickedTime1"
                        @click:save="time2 = false"
                    ></v-time-picker>
                  </v-menu>
                </v-text-field>


              </v-col>


              <v-col cols="12" sm="6">
                <v-text-field
                    :value="formatPickedDate(pickedDate2)"
                    prepend-icon="mdi-calendar"
                    placeholder="Data"
                    readonly
                >
                  <v-menu
                      v-model="date2"
                      :close-on-content-click="false"
                      activator="parent"
                      transition="scale-transition"
                  >
                    <v-date-picker
                        :color="primaryColor"
                        type="date"
                        locale="pl"
                        :min="pickedDate1"
                        v-model="pickedDate2"
                        @click:save="date2 = false"
                    ></v-date-picker>
                  </v-menu>
                </v-text-field>
              </v-col>
              <v-col cols="12">
                Czas na rozwiązanie (minuty)
                <v-divider/>
                <v-number-input
                    :reverse="false"
                    controlVariant="stacked"
                    label=""
                    :min=1
                    :hideInput="false"
                    :inset="false"
                    v-model="solutionTime"
                    variant="solo-filled"

                ></v-number-input>
              </v-col>
              <v-col cols="12">

                Ilość pytań
                <v-divider/>
                <v-number-input
                    :reverse="false"
                    controlVariant="stacked"
                    label=""
                    :min=1
                    :max="examToGenerate.numberOfQuestions"
                    :hideInput="false"
                    :inset="false"
                    v-model="numberOfQuestions"
                    variant="solo-filled"
                ></v-number-input>
              </v-col>
            </v-row>
          </v-col>

        </v-row>


      </v-card-text>
      <v-card-actions style="margin: unset">
        <v-spacer></v-spacer>
        <v-btn text="Anuluj" variant="plain" @click="examGenerationDialog = false"></v-btn>
        <v-btn :color="primaryColor" text="Zaplanuj" variant="tonal" @click="generateExam()"></v-btn>
      </v-card-actions>
      <v-divider></v-divider>
    </v-card>
  </v-dialog>

</template>
<style scoped>
.hover-icon:hover{
  cursor:pointer;
}


.v-container{
  max-width: none;
}


@media (max-width: 1024px) {
  .v-container{
    padding:0 }
  .v-col{
    padding-left: 10px;
  }
  * {
    margin: auto;


  }


  .addDialog {
    max-width: 100%;
  }


  .deleteDialog {
    max-width: 100%;
  }


  .generationDialog {
    max-width: 100%;
  }


  .elements {
    padding-bottom: 50px;


  }




  .examCard {
    margin-left: auto;
    margin-right: auto;
    min-width: 20em;
  }


  .contentPanel {
    margin: auto;


  }


  .floatingButton {
    bottom: 5rem;
  }
}


@media (min-width: 1024px) {
  .v-card-actions .v-btn ~ .v-btn:not(.v-btn-toggle .v-btn) {
    margin-inline-start:0;
  }
  .rowElements {
    margin-left: 20rem;
    margin-right: 1em;
  }


  .addDialog {
    max-width: 500px;
  }


  .deleteDialog {
    max-width: 500px;
  }


  .generationDialog {
    max-width: 500px;
  }


  .elements {
    display: contents;
  }




  .floatingButton {
    bottom: 2rem;
  }
}


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




.v-dialog > .v-overlay__content > .v-card > .v-card-item + .v-card-text {
  width: 100%;
}


</style>


<script>
import ExamServices from "@/services/ExamServices.js";
import NotificationService from "@/services/NotificationService.js";
import MenuBar from "@/components/MenuBar.vue";
import SearchBar from "@/components/SearchBarMain.vue";
import {VTimePicker} from 'vuetify/labs/VTimePicker'
import {VNumberInput} from 'vuetify/labs/VNumberInput'


export default {
  components: {
    MenuBar,
    VTimePicker,
    SearchBar,
    VNumberInput,
  },
  data() {
    return {
      content: '',
      originalExam: [],
      updatedExam: [],
      menu: false,
      addDialog: false,
      deleteDialog: false,
      examToDelete: {},
      primaryColor: 'var(--secondary-color)',
      examName: '',
      valueToPass: '',
      themeType: this.$themeType,
      examGenerationDialog: false,
      examToGenerate: null,
      examDescription: null,
      studentsList: null,
      time1: null,
      date1: false,
      pickedDate1: null,
      pickedTime1: null,
      time2: null,
      date2: false,
      pickedDate2: null,
      pickedTime2: null,
      changeExamNameDialog: false,
      examToUpdate: null,
      minDate: new Date().toISOString().substring(0, 10),
      solutionTime: 0,
      numberOfQuestions: 0,
      examToPDF: {},
      examToPDFDialog: false,
      questionAmmount: 0
    };
  },
  created() {
    this.fetchExams();
  },
  methods: {
    fetchExams() {
      ExamServices.getAllExams()
          .then(items => {
            if (items) {
              this.originalExam = items;
              this.updatedExam = items;

            } else {
              console.error('Nie udało się pobrać danych z endpointu.');
            }
          })
          .catch(error => {
            console.error('Błąd podczas pobierania danych:', error);
          });
    },
    updateExamList(filtered) {
      this.updatedExam = filtered;
    },
    generateExam() {
      if(this.examToGenerate.examName === null || this.studentsList === null || this.examDescription===null || this.formatPickedDate(this.pickedDate1) === null || this.formatPickedDate(this.pickedDate2) === null){
        NotificationService.notification("error","Uzupełnij puste pola")
        return;
      }
      const formData = new FormData();
      formData.append('examName', this.examToGenerate.examName);
      formData.append('studentList', this.studentsList);
      formData.append('description', this.examDescription);

      const formattedDate1 = this.formatPickedDate(this.pickedDate1).split('.').reverse().join('-');
      const formattedDate2 = this.formatPickedDate(this.pickedDate2).split('.').reverse().join('-');

      formData.append('startDate', `${formattedDate1}T${this.pickedTime1}`);
      formData.append('endDate', `${formattedDate2}T${this.pickedTime2}`);
      formData.append('solutionTime', this.solutionTime);
      formData.append('numberOfQuestions', this.numberOfQuestions);
      ExamServices.generateExam(formData).then(r => this.examGenerationDialog = false)

    },
    showDeleteDialog(item) {
      this.examToDelete = item;
      this.deleteDialog = true;

    },
    showGenerationDialog(item) {
      if(item.numberOfQuestions === 0){
        NotificationService.notification("error","Pusty egzamin")
      }
      else{
        this.examToGenerate = item;
        this.examGenerationDialog = true;
      }

    },
    showGeneratePDFDialog(item) {
      this.examToPDF = item;
      this.examToPDFDialog = true;

    },


    handleGeneratePDFExam() {
      ExamServices.generatePDFExam(this.examToPDF, this.questionAmmount)
          .catch(error => {
            console.error('Błąd podczas generowania PDF egzaminu:', error);
          });
    },
    async handleDeleteExam() {
      ExamServices.deleteExam(this.examToDelete.examId).then(() => {
        this.fetchExams();
        this.deleteDialog = false;
      })

    },
    openExamChangeDialog(item){
      this.changeExamNameDialog = true;
      this.examToUpdate = item;
    },
    closeExamChangeDialog(){
      this.changeExamNameDialog = false;
      this.examName="";
      this.examToUpdate = null;
    },
    changeExamName(){
      if(this.examName===""){
        NotificationService.notification("warning","Uzupełnij puste pola")
      }
      else{
        ExamServices.updateExamName(this.examToUpdate.examId, this.examName).then(()=>{
          this.fetchExams();
          this.examName="";
          this.changeExamNameDialog=false;
        })
      }
    },
    checkAndAddExam() {
      if (this.examName.trim() !== '') {
        this.handleAddExam();
      } else {
        NotificationService.notification("info", "Uzupełnij wszystkie pola")
      }
    },
    async handleAddExam() {
      await ExamServices.addExam(this.examName).then(() => {
            this.examName = "";
            this.addDialog = false;
            this.fetchExams();
          }
      )
    },
    formatPickedDate(date) {
      if (!date) return '';
      const options = {year: 'numeric', month: '2-digit', day: '2-digit'};
      return new Date(date).toLocaleDateString('pl-PL', options);
    },
    resetExamGenerationDialog() {
      this.pickedDate1 = null;
      this.pickedTime1 = null;
      this.pickedDate2 = null;
      this.pickedTime2 = null;
      this.studentsList = null;
      this.examDescription = null;
      this.examToGenerate = {};
      this.solutionTime = 1;
      this.numberOfQuestions = 1;
    },
  },
  watch: {
    examGenerationDialog(value) {
      if (!value) {
        this.resetExamGenerationDialog();
      }
    }
  }

};
</script>
<script setup lang="ts">
</script>