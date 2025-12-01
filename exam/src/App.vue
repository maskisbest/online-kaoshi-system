<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
ul {
  list-style: none;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  color: var(--text-main);
}
</style>
