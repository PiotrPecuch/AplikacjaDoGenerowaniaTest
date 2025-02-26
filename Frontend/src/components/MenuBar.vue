<template>

  <div class="desktopMenu">
  <MenuDesktop :theme="themeType"/>
  </div>


  <div class="mobileMenu">
  <MenuMobile :theme="themeType"/>
  </div>

</template>

<style>


@media (max-width: 1023px) {
.desktopMenu{
  display: none;
}
  .mobileMenu{
    display: contents;
  }
}

@media (min-width: 1024px) {
  .mobileMenu{
    display: none;
  }
}


</style>
<script>
import MenuDesktop from "@/components/Menu/MenuDesktop.vue";
import MenuMobile from "@/components/Menu/MenuMobile.vue";

export default {
  data() {
    return{
      themeType: this.$themeType
    }
  },
  components: {
    MenuDesktop,
    MenuMobile
  }
}
</script>
