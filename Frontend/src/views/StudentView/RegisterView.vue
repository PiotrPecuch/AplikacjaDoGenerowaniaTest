<script xmlns="http://www.w3.org/1999/html">
import LoginForm from "@/components/LoginForm.vue";
import RegisterView from "@/components/RegisterForm.vue";
import AuthServices from "@/services/auth.services.js";

export default {
  data() {
    return {
      primaryColor: 'var(--primary-color)',
      secondaryColor: 'var(--secondary-color)'
    };
  },
  components: {
    LoginForm,
    RegisterView,
  },
  methods : {
    handleRegister(credentials){
      AuthServices.register(credentials);
    }
  }
};
</script>

<template>

  <div class="wrapper">
    <div>
      <h1>Twórz testy</h1>
      <h3>
        I sprawdzaj wiedzę innych.
      </h3>
      <br>

    </div>
    <RegisterView @register="handleRegister"/>
  </div>


</template>

<style scoped>

.wrapper {
  margin: auto;
  font-weight: normal;
  display: grid;
  padding: 0  2rem;
  place-items: center;
  vertical-align: middle;
}

.wrapper h1{
  position: relative;
  color: var(--secondary-color);
}
.wrapper h3{
  text-align: center;
}


a{
  text-decoration: none;
  color: var(--primary-color);
  transition: 0.4s;
  padding: 3px;
}

@media (min-width: 1024px) {
  .wrapper {
    grid-template-columns: 1fr 1fr;
    width:75vw;
    max-width: 1500px;
    position: absolute;
    top: 50%;
    left: 50%;
    -ms-transform: translate(-50%, -50%);
    transform: translate(-50%, -50%);
  }
  .wrapper h1{
    font-size: 4rem;
  }

}
@media (max-width: 1024px) {
  .wrapper{
    width: 100vw;
    position: absolute;
    top: 50%;
    left: 50%;
    -ms-transform: translate(-50%, -50%);
    transform: translate(-50%, -50%);
  }
  .wrapper h1{
    font-size: 3rem;
  }
}



</style>
