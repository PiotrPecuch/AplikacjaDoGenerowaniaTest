<template >

  <MenuBar />
  <v-container class="questionMainContainer">
    <v-container :theme="themeType"  class="stickyQuestionHeader">
        <v-btn :color="primaryColor" style="color: white" @click="generatePdf()"> Generuj loginy (PDF)</v-btn>
        <v-text-field v-model="inputText" style="margin-top: 10px; color:white" placeholder="Wyszukaj" @input="filterList"></v-text-field>
    </v-container>
    <div v-if="updatedUserDetails.length > 0">

        <v-row class = "rowElements" align="center" justify="center" style="margin-top : 0;height: 20px">

        <v-container class="elements" style="padding-bottom: 50px">

          <v-col v-for="item in updatedUserDetails" cols="auto" class="contentPanel" >

            <v-card :theme="themeType" class="examCard   mx-auto"    :style="{
                  backgroundColor: item.cheated ? 'rgba(255, 3, 3, 0.1)' : 'rgba(33, 33, 33, 1)'}">
              <v-card-item class="d-flex align-center" style="heigh:200px">

                <v-card-item class="text-h6 mb-1">{{ item.examDescription }}</v-card-item>
                <v-card-item class="text-caption">
                  Imię i nazwisko:  {{ item.studentName }} {{ item.studentLastName }}
                </v-card-item>
                <v-card-item class="text-caption">
                  Numer indeksu:  {{ item.indexNumber }}
                </v-card-item>
                <v-card-item class="text-caption">
                  Login:  {{ item.ssoLogin }}
                </v-card-item>
                <v-card-item class="text-caption">
                  Hasło:  {{ item.ssoPassword }}
                </v-card-item>
                <v-card-item class="text-caption">
                  Status: {{ item.cheated ? "Opuszczenie strony" : (item.started ? "Oddano" : "Nie oddano") }}
                </v-card-item>
                <v-card-item class="text-caption">
                  Punkty: {{ item.pointsFromExam }} / {{item.percentageOfPoints}}%
                </v-card-item>
              </v-card-item>


              <v-card-actions style="justify-content: flex-end">
                <v-btn :to="{ name: 'ExamDetails', params: { id: this.id, ssoLogin: item.ssoLogin, ssoPassword: item.ssoPassword } }">
                  Sprawdź odpowiedzi
                </v-btn>



              </v-card-actions>
            </v-card>
          </v-col>
        </v-container>
      </v-row>
    </div>

  </v-container>
</template>


<script>
import MenuBar from "@/components/MenuBar.vue";
import ExamServices from "@/services/ExamServices.js";
import PDFService from "@/services/PDFService.js";
import SearchBar from "@/components/SearchBarStartedAndFinishedExams.vue";
export default {
  components:{
    SearchBar,
    MenuBar,
  },
  props: ['id'],
  data(){
    return {
      inputText: "",
      themeType: this.$themeType,
      userDetails : {},
      updatedUserDetails: {},
      primaryColor: 'var(--secondary-color)',
    }
  },
  created() {

    this.fetchStartedExamsDetailsAnswers();
  },
  methods: {
    fetchStartedExamsDetailsAnswers() {

      ExamServices.getStartedExamsDetails(this.id)
          .then(items => {
            if (items) {
              this.userDetails = items;
              this.updatedUserDetails = items;
              console.log("Uzytkownicy" + this.userDetails);
            } else {
              console.error('Nie udało się pobrać danych z endpointu.');
            }
          })
          .catch(error => {
            console.error('Błąd podczas pobierania danych:', error);
          });
    },
    generatePdf() {
      PDFService.generatePdf(this.userDetails)
    },
    filterList() {
      if (this.inputText !== '') {
        const lowerInput = this.inputText.toLowerCase();
        this.updatedUserDetails = this.userDetails.filter((item) => {
          const fullName = `${item.studentName} ${item.studentLastName}`.toLowerCase();
          return (
              item.studentName.toLowerCase().includes(lowerInput) ||
              item.studentLastName.toLowerCase().includes(lowerInput) ||
              item.indexNumber.toString().includes(this.inputText) ||
              fullName.includes(lowerInput)
          );
        });
      } else {
        this.updatedUserDetails = [...this.userDetails];
      }
    }
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



.questionMainContainer {
  padding: 20px;
}

.stickyQuestionHeader {
  position: sticky;
  top: 0;
  z-index: 10;
  border-bottom: 1px solid #ccc;
}


.elements{
  display: grid;
  grid-gap: 1rem;
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

  .v-col-auto {
    width: 100vw;

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

</style>






