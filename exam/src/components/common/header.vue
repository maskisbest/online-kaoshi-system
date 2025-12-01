<!-- 顶部信息栏 -->
<template>
  <header id="topbar" class="glass-panel">
    <div class="brand" @click="index()">
      <i class="iconfont icon-kaoshi"></i>
      <div class="brand-text">
        <span class="title">EXAM-SYSTEM</span>
        <span class="subtitle">{{ subtitle }}</span>
      </div>
    </div>
    <div class="actions">
      <button v-if="showMenuButton" class="ghost-btn" @click="toggle()" title="展开/折叠菜单">
        <i class="el-icon-menu"></i>
      </button>
      <div class="user">
        <span class="user-name">{{user.userName}}</span>
        <img src="@/assets/img/userimg.png" class="user-img" ref="img" @click="showSetting()" />
        <transition name="fade">
          <div class="out" ref="out" v-show="login_flag">
            <ul>
              <li><a href="javascript:;">用户信息</a></li>
              <li><a href="javascript:;">设置</a></li>
              <li class="exit" @click="exit()"><a href="javascript:;">退出登录</a></li>
            </ul>
          </div>
        </transition>
      </div>
    </div>
  </header>
</template>

<script>
import {mapState,mapMutations} from 'vuex'
export default {
  props: {
    showMenuButton: {
      type: Boolean,
      default: true
    },
    subtitle: {
      type: String,
      default: 'Unified Assessment Console'
    }
  },
  data() {
    return {
      login_flag: false,
      user: { //用户信息
        userName: null,
        userId: null
      }
    }
  },
  created() {
    this.getUserInfo()
  },
  computed: mapState(["flag","menu"]),
  methods: {
    //显示、隐藏退出按钮
    showSetting() {
      this.login_flag = !this.login_flag
    },
    //左侧栏放大缩小
    ...mapMutations(["toggle"]),
    getUserInfo() { //获取用户信息
      let userName = this.$cookies.get("cname")
      let userId = this.$cookies.get("cid")
      this.user.userName = userName
      this.user.userId = userId
    },
    index() {
      this.$router.push({path: '/index'})
    },
    exit() {
      let role = this.$cookies.get("role")
      this.$axios({ url: '/auth/logout', method: 'post' }).finally(() => {
        this.$router.push({path:"/"}) //跳转到登录页面
        this.$cookies.remove("cname") //清除cookie
        this.$cookies.remove("cid")
        this.$cookies.remove("role")
        if(role == 0) {
          this.menu.pop()
        }
      })
    }
  },
}
</script>

<style scoped>
.fade-enter-active, .fade-leave-active {
  transition: opacity .5s;
}
.fade-enter, .fade-leave-to /* .fade-leave-active below version 2.1.8 */ {
  opacity: 0;
}
#topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 24px;
  color: var(--text-main);
  position: sticky;
  top: 24px;
  z-index: 5;
}

.brand {
  display: flex;
  align-items: center;
  gap: 18px;
  cursor: pointer;
}

.brand .icon-kaoshi {
  font-size: 44px;
  color: var(--accent);
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.brand-text .title {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 0.04em;
}

.brand-text .subtitle {
  font-size: 13px;
  color: var(--text-muted);
}

.actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.ghost-btn {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid var(--border);
  border-radius: 999px;
  color: var(--text-main);
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: var(--transition);
}

.ghost-btn:hover {
  background: rgba(14, 165, 233, 0.15);
  border-color: rgba(14, 165, 233, 0.5);
}

.ghost-btn i {
  font-size: 20px;
}

.user {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-name {
  font-weight: 600;
}

.user-img {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  cursor: pointer;
  border: 2px solid rgba(255, 255, 255, 0.2);
}

.user .out {
  font-size: 14px;
  position: absolute;
  top: 60px;
  right: 0px;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: calc(var(--radius) - 4px);
  padding: 12px;
  min-width: 160px;
  box-shadow: var(--shadow);
  text-align: left;
}

.user .out ul {
  list-style: none;
  margin: 0;
  padding: 0;
}

.user .out ul > li {
  height: 32px;
  line-height: 32px;
  white-space: nowrap;
}

.out a {
  text-decoration: none;
  color: var(--text-main);
  display: block;
  padding: 0 4px;
}

.out a:hover {
  color: var(--accent);
}

.out .exit {
  margin-top: 6px;
  padding-top: 6px;
  border-top: 1px solid var(--border);
}

@media (max-width: 768px) {
  #topbar {
    flex-direction: column;
    gap: 12px;
  }

  .actions {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
