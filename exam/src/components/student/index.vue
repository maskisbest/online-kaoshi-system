<!--学生考试首页-->
<template>
  <div id="student" class="app-layout">
    <header1 class="app-panel" :show-menu-button="false" subtitle="Student Workspace"></header1>
    <section class="app-body student-shell">
      <div class="app-main">
        <nav class="app-panel student-nav">
          <div class="nav-intro">
            <i class="iconfont icon-kaoshi"></i>
            <div>
              <p class="app-title">Exam-Online</p>
              <p class="app-subtitle">快速进入考试、练习与成绩</p>
            </div>
          </div>
          <ul class="nav-links">
            <li><button class="nav-pill" @click="exam()">我的试卷</button></li>
            <li><button class="nav-pill" @click="practice()">我的练习</button></li>
            <li><router-link class="nav-pill" to="/scoreTable">我的分数</router-link></li>
            <li><router-link class="nav-pill" to="/message">给我留言</router-link></li>
            <li><router-link class="nav-pill" to="/examMsg">考试通知</router-link></li>
          </ul>
          <div class="nav-user" @mouseenter="flag = true" @mouseleave="flag = false">
            <span class="menu-chip"><i class="iconfont icon-Userselect"></i>{{user.userName}}</span>
            <div class="msg" v-if="flag">
              <p @click="manage()">管理中心</p>
              <p class="exit" @click="exit()">退出</p>
            </div>
          </div>
        </nav>
        <div class="app-panel app-content-card student-content">
          <router-view></router-view>
        </div>
        <v-footer class="app-panel"></v-footer>
      </div>
    </section>
  </div>
</template>

<script>
import myFooter from "@/components/student/myFooter"
import header from "@/components/common/header"
import {mapState} from 'vuex'
export default {
  components: {
    "v-footer": myFooter,
    header1: header
  },
  data() {
    return {
      flag: false,
      user: {}
    }
  },
  created() {
    this.userInfo()
  },
  methods: {
    exit() {  //退出登录
      this.$router.push({path:"/"}) //跳转到登录页面
      this.$cookies.remove("cname") //清除cookie
      this.$cookies.remove("cid")
    },
    manage() {  //跳转到修改密码页面
      this.$router.push({path: '/manager'})
    },
    userInfo() {
      let studentName = this.$cookies.get("cname")
      let studentId = this.$cookies.get("cid")
      console.log(`studentId${studentId}`)
      console.log(`studentName ${studentName}`)
      this.user.userName = studentName
      this.user.studentId = studentId
    },
    practice() { //跳转练习模式
      let isPractice = true
      this.$store.commit("practice", isPractice)
      this.$router.push({path:'/startExam'})
    },
    exam() { //跳转考试模式
     let isPractice = false
      this.$store.commit("practice", isPractice)
      this.$router.push({path:'/student'})
    }
  },
  computed:mapState(["isPractice"])
}
</script>

<style scoped>
#student {
  color: var(--text-main);
  font-family: var(--font-sans);
}

.student-shell {
  padding-top: 0;
}

.student-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.nav-intro {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-intro .iconfont {
  font-size: 40px;
  color: var(--brand);
}

.nav-links {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  padding: 0;
  margin: 0;
}

.nav-links li {
  list-style: none;
}

.nav-pill {
  border: 1px solid var(--border);
  background: rgba(255, 255, 255, 0.04);
  color: var(--text-main);
  padding: 10px 18px;
  border-radius: 999px;
  font-size: 14px;
  cursor: pointer;
  transition: var(--transition);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
}

.nav-pill:hover,
.nav-pill.router-link-active {
  background: rgba(14, 165, 233, 0.18);
  border-color: rgba(14, 165, 233, 0.6);
  color: #fff;
}

.nav-pill.router-link-active {
  font-weight: 600;
}

.nav-user {
  position: relative;
}

.nav-user .menu-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
}

.nav-user .iconfont {
  font-size: 18px;
}

.nav-user .msg {
  position: absolute;
  right: 0;
  top: 42px;
  background: var(--surface);
  border-radius: var(--radius);
  border: 1px solid var(--border);
  box-shadow: var(--shadow);
  width: 140px;
  text-align: center;
  overflow: hidden;
}

.nav-user .msg p {
  margin: 0;
  height: 40px;
  line-height: 40px;
  color: var(--text-main);
}

.nav-user .msg p:hover {
  background: rgba(249, 115, 22, 0.2);
}

.nav-user .msg .exit {
  border-top: 1px solid var(--border);
}

.student-content {
  min-height: 70vh;
}

@media (max-width: 768px) {
  .nav-links {
    width: 100%;
    justify-content: center;
  }

  .nav-intro {
    width: 100%;
    justify-content: center;
  }

  .nav-user {
    width: 100%;
    display: flex;
    justify-content: center;
  }
}
</style>
