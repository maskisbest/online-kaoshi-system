<!--左边下拉导航栏-->
<template>
  <div id="left" class="sidebar-shell">
    <el-menu
      active-text-color="#f97316"
      text-color="#e2e8f0"
      :default-active="this.$route.path"
      class="el-menu-vertical-demo modern-menu"
      @open="handleOpen"
      @close="handleClose"
      :collapse="flag"
      background-color="transparent"
      menu-trigger="click" router>
      <el-submenu v-for="(item,index) in menu" :index='item.index' :key="index">
        <template slot="title">
          <div class="left-width">
            <i class="iconfont" :class="item.icon"></i>
            <span slot="title" class="title">{{item.title}}</span>
          </div>
        </template>
        <el-menu-item-group   v-for="(list,index1) in item.content" :key="index1" >
          <el-menu-item  class="LiColor"  @click="handleTitle(item.index)" :index="list.path" v-if="list.item1 != null" >{{list.item1}}</el-menu-item>
          <el-menu-item  class="LiColor"  @click="handleTitle(item.index)" :index="list.path" v-if="list.item2 != null"  >{{list.item2}}</el-menu-item>
          <el-menu-item class="LiColor"  @click="handleTitle(item.index)" :index="list.path" v-if="list.item3 != null" >{{list.item3}}</el-menu-item>
        </el-menu-item-group>
      </el-submenu>
    </el-menu>
  </div>
</template>

<script>
import {mapState} from 'vuex'
export default {
  name: "mainLeft",
  data() {
    return {}
  },
  computed: mapState(["flag","menu"]),
  created() {
    this.addData()
  },
  methods: {
    handleOpen(key, keyPath) {
      // console.log(key, keyPath);
    },
    handleClose(key, keyPath) {
      // console.log(key, keyPath);
    },
    //点击标题传递参数给navigator组件
    handleTitle(index) {
      this.bus.$emit('sendIndex',index)
    },
    addData() {
      let role = this.$cookies.get("role")
      if(role === 0) {
        this.menu.push({
          index: '5',
          title: '教师管理',
          icon: 'icon-Userselect',
          content:[{item1:'教师管理',path:'/teacherManage'},{item2: '添加教师',path: '/addTeacher'}],
        })
      }
    }
  },
}
</script>

<style>
.sidebar-shell {
  height: 100%;
}

.modern-menu {
  background-color: transparent !important;
  border-right: none;
  width: 100%;
}

.el-menu-vertical-demo .el-submenu__title {
  overflow: hidden;
  color: var(--text-main);
  font-weight: 600;
}

.left-width {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
}

.left-width .iconfont {
  font-size: 18px;
  color: var(--accent);
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  min-height: calc(100vh - 160px);
}

#left .el-menu-vertical-demo .title {
  color: var(--text-main);
  font-size: 15px;
  font-weight: 600;
}

.el-submenu__title:hover {
  background-color: rgba(255, 255, 255, 0.04) !important;
}

.el-menu-item {
  border-radius: 10px !important;
  margin: 4px 12px;
  color: var(--text-muted) !important;
}

.el-menu-item.is-active {
  background: rgba(249, 115, 22, 0.15) !important;
  color: #fff !important;
}

.LiColor {
  color: var(--text-muted) !important;
}

.el-submenu {
  border-bottom: 1px solid rgba(255, 255, 255, 0.03) !important;
  margin-bottom: 4px;
}
</style>
