<template>
  <RouterView :theme="themeType"/>
</template>

<script>
import TokenServices from "@/services/TokenServices.js";
import router from "@/router/index.js";

export default {
  data(){
    return{
      themeType: this.$themeType
    };
  },
created() {
  if (TokenServices.isTokenValid()) {
  return true;
  } else {
    router.push("/login")
  }
}
}
</script>
