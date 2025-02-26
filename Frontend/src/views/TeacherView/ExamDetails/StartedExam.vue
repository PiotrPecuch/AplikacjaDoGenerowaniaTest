
<template>
  <MenuBar></MenuBar>


  <div v-if="originalExam.length > 0">
    <v-row class = "rowElements" align="center" justify="center" style="margin-top : 0;height: 20px">


      <v-container style="height: 100vh;">
      <SearchBar :items="originalExam" @update:filtered="updateExamList" />

      <v-container class="elements">
        <v-col v-for="item in updatedExam" cols="auto" class="contentPanel" >

          <v-card :theme="themeType" class="examCard   mx-auto">
            <v-card-item class="d-flex align-center" style="height:200px">
              <v-card-item class="text-h6 mb-1">{{ item.examDescription }}</v-card-item>
              <v-card-item class="text-caption">
                Egzamin: {{ item.exam.examName }}
              </v-card-item>
              <v-card-item class="text-caption">
                Data rozpoczęcia {{ formatDate(item.startDate)  }}
              </v-card-item>
              <v-card-item class="text-caption">
                Data zakończenia {{ formatDate(item.endDate)  }}
              </v-card-item>

            </v-card-item>


            <v-card-actions class="navigationButtons" style=" display:flex; flex-wrap: wrap;justify-content: flex-end">
              <v-btn @click="archiveExam(item)">
                Archiwizuj
              </v-btn>
              <v-divider vertical/>
              <v-btn @click="sendNotification(item)">
                Powiadom o ocenach
              </v-btn>
              <v-divider vertical/>
              <v-btn :to="{ path: '/main/startedExams/details/' +  item.id  }">
                Sprawdź szczegóły
              </v-btn>

            </v-card-actions>
          </v-card>
        </v-col>
      </v-container>
      </v-container>
    </v-row>
  </div>
</template>

<style scoped>

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
  .navigationButtons{
    justify-content: space-between;
    flex-wrap: wrap;
    flex-direction: column;
    align-content: space-around;
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
import MenuBar from "@/components/MenuBar.vue";
import ExamServices from "@/services/ExamServices.js";
import SearchBar from "@/components/SearchBarStartedAndFinishedExams.vue";
export default {
  components:{
    SearchBar,
    MenuBar,
  },
  data(){
    return {
      themeType: this.$themeType,
      originalExam: [],
      updatedExam: [],
    }
  },
  created() {
    this.fetchStartedExams();
  },
  methods:{
    archiveExam(item){
      ExamServices.archiveExam(item);
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleString("pl-PL", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
      });
    },
    updateExamList(filtered) {
      this.updatedExam = filtered;
    },
    sendNotification(item){
      ExamServices.sendNotification(item);
    },
    fetchStartedExams(){
      ExamServices.getStartedExams()
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
  },
};
</script>