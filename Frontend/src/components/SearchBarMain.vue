<template >

  <v-card :theme="themeType" style="display: flex; position: sticky; top: 20px; z-index: 20; padding-top: 20px">

    <v-text-field v-model="inputText" placeholder="Wyszukaj" style="padding-left: 10px;width: 40%"  @input="filterList"/>
<!--    datepicker-->
    <v-text-field
        :value="formatPickedDate(pickedDate)"
        prepend-icon="null"
        placeholder="Data od"
        readonly
        clearable
    >
      <v-menu
          v-model="date"
          prepend-icon="null"
          :close-on-content-click="false"
          activator="parent"
          transition="scale-transition"
      >
        <v-date-picker
            :color="primaryColor"
            type="date"
            locale="pl"
            v-model="pickedDate"
            :max="pickedDate2"
            @click="filterList"
        ></v-date-picker>
      </v-menu>
    </v-text-field>



    <v-text-field
        :value="formatPickedDate(pickedDate2)"
        prepend-icon="null"
        placeholder="Data do"
        readonly
        style="padding-right: 20px"
        clearable
    >
      <v-menu
          v-model="date2"
          prepend-icon="null"
          :close-on-content-click="false"
          activator="parent"
          transition="scale-transition"
      >
        <v-date-picker
            :color="primaryColor"
            type="date"
            locale="pl"
            v-model="pickedDate2"
            :min="pickedDate"
            @click="filterList"
        ></v-date-picker>
      </v-menu>
    </v-text-field>
    <v-btn @click="clearSearchBar" icon="mdi-backspace" style="margin-right: 10px; box-shadow: none !important;"/>
  </v-card>

</template>

<style>


</style>
<script>

export default {
  props:{
    items:{
      type: Array,
      required:true,
    },
  },
  data() {
    return{
      themeType: this.$themeType,
      primaryColor: 'var(--secondary-color)',
      pickedDate: null,
      date: null,
      pickedDate2: null,
      date2: null,
      inputText: '',
    }
  },
  methods: {
    formatPickedDate(date) {
      if (!date) return '';
      const options = {year: 'numeric', month: '2-digit', day: '2-digit'};
      return new Date(date).toLocaleDateString('en-CA', options);
    },

    filterList() {
      let filtered = this.items;

      if (this.inputText !== '') {
        filtered = filtered.filter((item) =>
            item.examName.toLowerCase().includes(this.inputText.toLowerCase())
        );
      }


      if (this.pickedDate && this.pickedDate2===null) {
        filtered = filtered.filter((item) =>
            this.formatPickedDate(item.creationDate) >= this.formatPickedDate(this.pickedDate)
        );
      }

      if (this.pickedDate2 && this.pickedDate===null) {
        filtered = filtered.filter((item) =>
            this.formatPickedDate(item.creationDate) <= this.formatPickedDate(this.pickedDate2)
        );
      }

      if (this.pickedDate !== null && this.pickedDate2!==null) {
        filtered = filtered.filter((item) =>
            this.formatPickedDate(item.creationDate) >= this.formatPickedDate(this.pickedDate) && this.formatPickedDate(item.creationDate) <= this.formatPickedDate(this.pickedDate2)
        );
      }




      this.$emit('update:filtered', filtered);
    },
    clearSearchBar(){
      this.pickedDate2=null;
      this.pickedDate=null;
      this.inputText="";
      this.filterList()
    },
  },
  components: {

  }
}
</script>
