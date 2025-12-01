<!-- 用户登录界面 -->
<template>
  <div id="login">
    <div class="main-container">
      <div class="hero">
        <div class="brand">EXAM · ONLINE</div>
        <div class="title">在线考试系统</div>
        <p class="text-muted">统一考试与题库管理，安全稳定，开箱即用。</p>
      </div>
      <div class="panel">
        <div class="card-head">
          <i class="iconfont icon-kaoshi"></i>
          <div>
            <div class="card-title">账号登录</div>
            <div class="text-muted">输入工号/学号与密码进入系统</div>
          </div>
        </div>
        <el-form :label-position="labelPosition" label-width="80px" :model="formLabelAlign">
          <el-form-item label="用户名">
            <el-input v-model.trim="formLabelAlign.username" placeholder="请输入用户名"></el-input>
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="formLabelAlign.password" placeholder="请输入密码" show-password></el-input>
          </el-form-item>
          <div class="submit actions">
            <el-button type="primary" class="row-login" :loading="loading" @click="login">登录</el-button>
            <el-button type="text" class="link-btn" @click="registerVisible = true">没有账号？立即注册</el-button>
          </div>
        </el-form>
      </div>
    </div>
    <el-dialog title="实名注册" :visible.sync="registerVisible" width="520px" :close-on-click-modal="false">
      <el-form :model="registerForm" label-width="100px">
        <el-form-item label="角色">
          <el-radio-group v-model="registerForm.role">
            <el-radio label="2">学生</el-radio>
            <el-radio label="1">教师</el-radio>
            <el-radio label="0">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model.trim="registerForm.name" placeholder="请输入真实姓名"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="registerForm.sex" placeholder="请选择">
            <el-option label="男" value="男"></el-option>
            <el-option label="女" value="女"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model.trim="registerForm.cardId" placeholder="用于实名验证"></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model.trim="registerForm.tel" placeholder="用于找回与通知"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model.trim="registerForm.email" placeholder="接收通知"></el-input>
        </el-form-item>
        <el-form-item label="所属学院" v-if="isStudent || isTeacher">
          <el-input v-model.trim="registerForm.institute" placeholder="请输入学院/部门"></el-input>
        </el-form-item>
        <el-form-item label="年级" v-if="isStudent">
          <el-input v-model.trim="registerForm.grade" placeholder="例：2025"></el-input>
        </el-form-item>
        <el-form-item label="专业" v-if="isStudent">
          <el-input v-model.trim="registerForm.major" placeholder="例：软件工程"></el-input>
        </el-form-item>
        <el-form-item label="班级" v-if="isStudent">
          <el-input v-model.trim="registerForm.clazz" placeholder="例：1班"></el-input>
        </el-form-item>
        <el-form-item label="授课类型" v-if="isTeacher">
          <el-input v-model.trim="registerForm.type" placeholder="例：数据库/网络"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="registerForm.pwd" show-password placeholder="至少6位"></el-input>
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="registerForm.confirmPwd" show-password placeholder="再次输入密码"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeRegister">取 消</el-button>
        <el-button type="primary" :loading="registerLoading" @click="submitRegister">提交注册</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {mapState} from 'vuex'
export default {
  name: "login",
  data() {
    return {
      loading: false,
      role: 2,
      labelPosition: 'left',
      formLabelAlign: {
        username: '',
        password: ''
      },
      registerVisible: false,
      registerLoading: false,
      registerForm: {
        role: '2',
        name: '',
        sex: '',
        cardId: '',
        tel: '',
        email: '',
        institute: '',
        grade: '',
        major: '',
        clazz: '',
        type: '',
        pwd: '',
        confirmPwd: ''
      }
    }
  },
  methods: {
    //用户登录请求后台处理
    login() {
      if (!this.formLabelAlign.username || !this.formLabelAlign.password) {
        this.$message.warning('请输入用户名和密码')
        return
      }
      this.loading = true
      this.$axios({
        url: `/login`,
        method: 'post',
        data: {
          ...this.formLabelAlign
        }
      }).then(res => {
        const resData = res.data.data
        if (resData) {
          const role = resData.role
          const roleToPath = { "0": '/index', "1": '/index', "2": '/student' }
          const idKey = role === "2" ? 'studentId' : role === "1" ? 'teacherId' : 'adminId'
          const nameKey = role === "2" ? 'studentName' : role === "1" ? 'teacherName' : 'adminName'
          this.$cookies.set("cname", resData[nameKey])
          this.$cookies.set("cid", resData[idKey])
          this.$cookies.set("role", Number(role))
          this.$router.push({ path: roleToPath[role] })
          this.$message.success('登录成功')
        } else {
          this.$message.error('用户名或者密码错误')
        }
      }).catch(() => {
        this.$message.error('登录失败，请稍后重试')
      }).finally(() => {
        this.loading = false
      })
    },
    clickTag(key) {
      this.role = key
    },
    closeRegister() {
      this.registerVisible = false
      this.registerLoading = false
    },
    submitRegister() {
      if (!this.registerForm.name || !this.registerForm.cardId || !this.registerForm.tel || !this.registerForm.email) {
        this.$message.warning('请填写完整的实名信息')
        return
      }
      if (!this.registerForm.pwd || this.registerForm.pwd.length < 6) {
        this.$message.warning('密码至少6位')
        return
      }
      if (this.registerForm.pwd !== this.registerForm.confirmPwd) {
        this.$message.warning('两次输入密码不一致')
        return
      }
      this.registerLoading = true
      const payload = { ...this.registerForm }
      delete payload.confirmPwd
      this.$axios({
        url: '/auth/register',
        method: 'post',
        data: payload
      }).then(res => {
        if (res.data.code === 200) {
          const data = res.data.data || {}
          const userId = data.userId ? `，分配ID：${data.userId}` : ''
          this.$message.success(`注册成功${userId}，请使用账号登录`)
          this.registerVisible = false
        } else {
          this.$message.error(res.data.message || '注册失败')
        }
      }).catch(() => {
        this.$message.error('注册失败，请稍后重试')
      }).finally(() => {
        this.registerLoading = false
      })
    }
  },
  computed: {
    ...mapState(["userInfo"]),
    isStudent() {
      return this.registerForm.role === '2'
    },
    isTeacher() {
      return this.registerForm.role === '1'
    }
  },
  mounted() {

  }
}
</script>

<style lang="less" scoped>
.remind {
  border-radius: 10px;
  padding: 12px 18px;
  display: flex;
  position: fixed;
  right: 20px;
  bottom: 50%;
  flex-direction: column;
  color: var(--text-main);
  background: var(--panel);
  border-left: 4px solid var(--brand);
  box-shadow: var(--shadow);
}

#login {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 16px;
}

.main-container {
  width: 100%;
  max-width: 960px;
  display: grid;
  grid-template-columns: 1fr;
  justify-content: center;
}

.hero {
  text-align: center;
  margin-bottom: 28px;
}

.hero .brand {
  font-size: 18px;
  letter-spacing: 0.2rem;
  color: var(--accent);
}

.hero .title {
  margin-top: 12px;
  font-size: 34px;
  font-weight: 700;
}

.panel {
  background: var(--panel);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  padding: 28px;
  backdrop-filter: blur(12px);
}

.card-head {
  display: flex;
  align-items: center;
  margin-bottom: 18px;
  gap: 10px;
}

.card-head .icon-kaoshi {
  font-size: 42px;
  color: var(--brand);
}

.card-head .card-title {
  font-size: 20px;
  font-weight: 700;
}

.text-muted {
  color: var(--text-muted);
}

.submit {
  margin-top: 12px;
}

.row-login {
  width: 100%;
  background: linear-gradient(120deg, var(--brand), var(--brand-strong));
  border: none;
  border-radius: 10px;
  padding: 14px 18px;
}

.row-login:hover {
  filter: brightness(1.05);
}
.actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
.link-btn {
  padding: 0 8px;
}

@media (min-width: 960px) {
  .main-container {
    grid-template-columns: 1.2fr 1fr;
    gap: 32px;
    align-items: center;
  }

  .hero {
    text-align: left;
    margin: 0;
  }
}
</style>
