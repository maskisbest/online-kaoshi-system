// 展示组件页面
<template>
  <div id="index" class="app-layout">
    <header1 class="app-panel topbar"></header1>
    <section class="app-body">
      <div :class="['app-sidebar', { collapsed: isCollapsed }]">
        <mainLeft class="app-panel sidebar-panel"></mainLeft>
      </div>
      <div class="app-main">
        <navigator class="app-panel nav-panel"></navigator>
        <div class="app-panel app-content-card">
          <router-view></router-view>
        </div>
      </div>
    </section>
  </div>
</template>

<script >
import header from '@/components/common/header'
import mainLeft from '@/components/common/mainLeft'
import navigator from '@/components/common/navigator'
import { mapState } from 'vuex'
export default {
  components:{
    header1: header,
    mainLeft: mainLeft,
    navigator:navigator
  },
  data() {
    return {
      username: '布咕咕'
    }
  },
  computed: {
    ...mapState({
      isCollapsed: state => state.flag
    })
  },
  methods: {

  }
}
</script>

<style lang="less" scoped>
.topbar {
  padding: 0;
}

.nav-panel {
  padding: 8px 24px;
}

.app-content-card {
  min-height: 70vh;
  overflow: hidden;
}

.sidebar-panel {
  padding: 0;
}

.app-sidebar {
  transition: width 0.2s ease;
}

.app-sidebar.collapsed {
  width: 96px;
}

@media (max-width: 1280px) {
  .sidebar-panel {
    max-height: 420px;
    overflow: hidden;
  }
}
</style>
