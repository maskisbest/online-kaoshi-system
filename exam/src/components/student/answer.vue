<!--考生答题界面-->
<template>
  <div id="answer">
    <!--顶部信息栏-->
    <div class="top">
      <ul class="item">
        <li><i class="iconfont icon-menufold icon20" ref="toggle" @click="slider_flag = !slider_flag"></i></li>
        <li>{{ examData.type }}-{{ examData.source }}</li>
        <li @click="flag = !flag">
          <i class="iconfont icon-user icon20"></i>
          <div class="msg" v-if="flag" @click="flag = !flag">
            <p>姓名：{{ userInfo.name }}</p>
            <p>准考证号: {{ userInfo.id }}</p>
          </div>
        </li>
        <li><i class="iconfont icon-arrLeft icon20"></i></li>
      </ul>
    </div>
    <div class="flexarea">
      <!--左边题目编号区-->
      <transition name="slider-fade">
        <div class="left" v-if="slider_flag">
          <ul class="l-top">
            <li>
              <a href="javascript:;"></a>
              <span>当前</span>
            </li>
            <li>
              <a href="javascript:;"></a>
              <span>未答</span>
            </li>
            <li>
              <a href="javascript:;"></a>
              <span>已答</span>
            </li>
            <li>
              <a href="javascript:;"></a>
              <span>标记</span>
            </li>
          </ul>
          <div class="l-bottom">
            <div class="item">
              <p>选择题部分</p>
              <ul>
                <li v-for="(list, index1) in topic[1]" :key="index1">
                  <a href="javascript:;"
                     @click="change(index1)"
                     :class="{'border': index == index1 && currentType == 1,'bg': bg_flag && topic[1][index1].isClick == true}">
                    <span :class="{'mark': topic[1][index1].isMark == true}"></span>
                    {{ index1 + 1 }}
                  </a>
                </li>
              </ul>
            </div>
            <div class="item">
              <p>填空题部分</p>
              <ul>
                <li v-for="(list, index2) in topic[2]" :key="index2">
                  <a href="javascript:;" @click="fill(index2)"
                     :class="{'border': index == index2 && currentType == 2,'bg': fillStatus[index2] === true}"><span
                    :class="{'mark': topic[2][index2].isMark == true}"></span>{{ topicCount[0] + index2 + 1 }}</a>
                </li>
              </ul>
            </div>
            <div class="item">
              <p>判断题部分</p>
              <ul>
                <li v-for="(list, index3) in topic[3]" :key="index3">
                  <a href="javascript:;" @click="judge(index3)"
                     :class="{'border': index === index3 && currentType === 3,'bg': bg_flag && topic[3][index3].isClick === true}"><span
                    :class="{'mark': topic[3][index3].isMark === true}"></span>{{ topicCount[0] + topicCount[1] + index3 + 1 }}</a>
                </li>
              </ul>
            </div>
            <div class="actions">
              <div class="save" @click="saveProgress()">暂存进度</div>
              <div class="final" @click="commit(examData.examCode)">结束考试</div>
            </div>
          </div>
        </div>
      </transition>
      <!--右边选择答题区-->
      <transition name="slider-fade">
        <div class="right">
          <div class="title">
            <p>{{ title }}</p>
            <i class="iconfont icon-right auto-right"></i>
            <span>全卷共{{ topicCount[0] + topicCount[1] + topicCount[2] }}题  <i class="iconfont icon-time"></i>倒计时：<b>{{ timeText }}</b></span>
          </div>
          <div class="content">
            <p class="topic"><span class="number">{{ number }}</span>{{ showQuestion }}</p>
            <div v-if="currentType == 1">
              <el-radio-group v-model="radio[index]" @change="getChangeLabel">
                <el-radio :label="1">{{ showAnswer.answerA }}</el-radio>
                <el-radio :label="2">{{ showAnswer.answerB }}</el-radio>
                <el-radio :label="3">{{ showAnswer.answerC }}</el-radio>
                <el-radio :label="4">{{ showAnswer.answerD }}</el-radio>
              </el-radio-group>
              <div class="analysis" v-if="isPractice">
                <ul>
                  <li>
                    <el-tag type="success">正确姿势：</el-tag>
                    <span class="right">{{ reduceAnswer.rightAnswer }}</span></li>
                  <li>
                    <el-tag>题目解析：</el-tag>
                  </li>
                  <li>{{ reduceAnswer.analysis == null ? '暂无解析' : reduceAnswer.analysis }}</li>
                </ul>
              </div>
            </div>
            <div class="fill" v-if="currentType == 2">
              <div v-for="(item,currentIndex) in part" :key="currentIndex">
                <el-input placeholder="请填在此处"
                          v-model="fillAnswer[index][currentIndex]"
                          clearable
                          @blur="fillBG">
                </el-input>
              </div>
              <div class="analysis" v-if="isPractice">
                <ul>
                  <li>
                    <el-tag type="success">正确姿势：</el-tag>
                    <span class="right">{{ topic[2][index].answer }}</span></li>
                  <li>
                    <el-tag>题目解析：</el-tag>
                  </li>
                  <li>{{ topic[2][index].analysis == null ? '暂无解析' : topic[2][index].analysis }}</li>
                </ul>
              </div>
            </div>
            <div class="judge" v-if="currentType == 3">
              <el-radio-group v-model="judgeAnswer[index]" @change="getJudgeLabel" v-if="currentType == 3">
                <el-radio :label="1">正确</el-radio>
                <el-radio :label="2">错误</el-radio>
              </el-radio-group>
              <div class="analysis" v-if="isPractice">
                <ul>
                  <li>
                    <el-tag type="success">正确姿势：</el-tag>
                    <span class="right">{{ topic[3][index].answer }}</span></li>
                  <li>
                    <el-tag>题目解析：</el-tag>
                  </li>
                  <li>{{ topic[3][index].analysis == null ? '暂无解析' : topic[3][index].analysis }}</li>
                </ul>
              </div>
            </div>
          </div>
          <div class="operation">
            <ul class="end">
              <li @click="previous()"><i class="iconfont icon-previous"></i><span>上一题</span></li>
              <li @click="mark()"><i class="iconfont icon-mark-o"></i><span>标记</span></li>
              <li @click="next()"><span>下一题</span><i class="iconfont icon-next"></i></li>
            </ul>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  data() {
    return {
      startTime: null, //考试开始时间
      endTime: null, //考试结束时间
      remainingSeconds: 0, //倒计时，秒
      timer: null,
      reduceAnswer: [],  //vue官方不支持3层以上数据嵌套,如嵌套则会数据渲染出现问题,此变量直接接收3层嵌套时的数据。
      answerScore: 0, //答题总分数
      bg_flag: false, //已答标识符,已答改变背景色
      isFillClick: false, //选择题是否点击标识符
      slider_flag: true, //左侧显示隐藏标识符
      flag: false, //个人信息显示隐藏标识符
      currentType: 1, //当前题型类型  1--选择题  2--填空题  3--判断题
      radio: [], //保存考生所有选择题的选项
      title: "请选择正确的选项",
      index: 0, //全局index
      userInfo: { //用户信息
        name: null,
        id: null
      },
      topicCount: [0, 0, 0],//每种类型题目的总数
      score: [0, 0, 0],  //每种类型分数的总数
      examData: { //考试信息
      },
      topic: {  //试卷信息
        1: [],
        2: [],
        3: []
      },
      showQuestion: [], //当前显示题目信息
      showAnswer: {}, //当前题目对应的答案选项
      number: 1, //题号
      part: 1, //填空题的空格数量
      fillAnswer: [], //二维数组保存所有填空题答案
      fillStatus: [], //填空题作答状态
      judgeAnswer: [], //保存所有判断题答案
      topic1Answer: [],  //学生选择题作答编号,
      rightAnswer: '',
      progressKey: '',
      submitting: false,
      status: 'pending'
    }
  },
  created() {
    this.getCookies()
    this.getExamData()
    window.addEventListener('beforeunload', this.handleLeave)
  },
  beforeDestroy() {
    this.clearTimer()
    window.removeEventListener('beforeunload', this.handleLeave)
  },
  methods: {
    handleLeave() {
      this.saveProgress(false)
    },
    getTime(date) { //日期格式化
      let year = date.getFullYear()
      let month = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
      let day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
      let hours = date.getHours() < 10 ? "0" + date.getHours() : date.getHours();
      let minutes = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes();
      let seconds = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
      // 拼接
      return year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds;
    },
    getCookies() {  //获取cookie
      this.userInfo.name = this.$cookies.get("cname")
      this.userInfo.id = this.$cookies.get("cid")
    },
    buildProgressKey(examCode) {
      return `exam-progress-${examCode}-${this.userInfo.id || 'anonymous'}`
    },
    getExamStatus(examDate, totalTime) {
      if (!examDate) return 'pending'
      const start = new Date(`${examDate.replace(/-/g, '/')} 00:00:00`)
      const duration = Number(totalTime) || 60
      const end = new Date(start.getTime() + duration * 60000)
      const now = new Date()
      if (now < start) return 'notStarted'
      if (now > end) return 'ended'
      return 'ongoing'
    },
    normalizeText(str) {
      return (str || '').toString().replace(/\s+/g, '').toLowerCase()
    },
    normalizeFillAnswers(raw) {
      if (!raw) return []
      const cleaned = raw.replace(/；/g, ';').replace(/，/g, ',')
      const parts = cleaned.split(/[,;、|/]/).map((item) => this.normalizeText(item)).filter(Boolean)
      if (parts.length > 0) {
        return parts
      }
      return [this.normalizeText(raw)]
    },
    questionBlankCount(question) {
      if (!question) return 1
      const blanks = question.match(/\(\)/g)
      return blanks ? blanks.length : 1
    },
    clearTimer() {
      if (this.timer) {
        clearInterval(this.timer)
        this.timer = null
      }
    },
    startTimer() {
      this.clearTimer()
      if (!this.remainingSeconds || this.remainingSeconds <= 0) {
        this.remainingSeconds = 0
        if (this.examData && this.examData.examCode) {
          this.commit(this.examData.examCode, true)
        }
        return
      }
      this.timer = setInterval(() => {
        this.remainingSeconds -= 1
        if (this.remainingSeconds === 600) {
          this.$message({
            showClose: true,
            type: 'warning',
            message: '考生注意，考试时间还剩10分钟'
          })
        }
        if (this.remainingSeconds <= 0) {
          this.clearTimer()
          this.$message.warning('考试时间到，系统已自动交卷')
          this.commit(this.examData.examCode, true)
        }
      }, 1000)
    },
    getExamData() { //获取当前试卷所有信息
      let date = new Date()
      this.startTime = this.getTime(date)
      let examCode = this.$route.query.examCode //获取路由传递过来的试卷编号
      this.progressKey = this.buildProgressKey(examCode)
      this.$axios(`/api/exam/${examCode}`).then(res => {  //通过examCode请求试卷详细信息
        this.examData = { ...res.data.data } //获取考试详情
        this.status = this.getExamStatus(this.examData.examDate, this.examData.totalTime)
        if (this.status !== 'ongoing') {
          this.$message.warning('当前不在考试时间，无法作答')
          this.$router.replace('/student')
          return
        }
        this.index = 0
        const totalMinutes = Number(this.examData.totalTime || 0)
        this.remainingSeconds = (totalMinutes > 0 ? totalMinutes : 60) * 60
        let paperId = this.examData.paperId
        this.$axios(`/api/paper/${paperId}`).then(res => {  //通过paperId获取试题题目信息
          this.topic = { ...res.data }
          this.topicCount = []
          this.score = []
          this.fillAnswer = []
          this.fillStatus = []
          this.radio = []
          this.topic1Answer = []
          this.judgeAnswer = []
          let reduceAnswer = (this.topic[1] && this.topic[1][this.index]) || {}
          this.reduceAnswer = reduceAnswer
          let keys = Object.keys(this.topic) //对象转数组
          keys.forEach(e => {
            let data = this.topic[e]
            this.topicCount.push(data.length)
            let currentScore = 0
            for (let i = 0; i < data.length; i++) { //循环每种题型,计算出总分
              currentScore += Number(data[i].score || 0)
            }
            this.score.push(currentScore) //把每种题型总分存入score
          })
          const fillQuestions = this.topic[2] || []
          this.fillAnswer = fillQuestions.map((q) => {
            const blanks = Math.max(this.questionBlankCount(q.question), this.normalizeFillAnswers(q.answer).length, 1)
            return Array(blanks).fill(null)
          })
          this.fillStatus = this.fillAnswer.map(() => false)
          let dataInit = this.topic[1] || []
          if (dataInit.length > 0) {
            this.number = 1
            this.showQuestion = dataInit[0].question
            this.showAnswer = dataInit[0]
          } else {
            this.number = 0
            this.showQuestion = ''
            this.showAnswer = {}
          }
          this.part = this.questionBlankCount(this.showQuestion)
          this.restoreProgress()
          this.startTimer()
        })
      })
    },
    change(index) { //选择题
      const choiceList = this.topic[1] || []
      const len = choiceList.length
      if (len === 0) {
        return
      }
      this.index = index
      this.reduceAnswer = choiceList[this.index] || {}
      this.isFillClick = true
      this.currentType = 1
      if (this.index < len) {
        if (this.index <= 0) {
          this.index = 0
        }
        this.title = "请选择正确的选项"
        let Data = this.topic[1]
        this.showQuestion = Data[this.index].question //获取题目信息
        this.showAnswer = Data[this.index]
        this.number = this.index + 1
      } else if (this.index >= len) {
        this.index = 0
        this.fill(this.index)
      }
    },
    fillBG() { //填空题已答题目
      if (Array.isArray(this.fillAnswer[this.index])) {
        const answered = this.fillAnswer[this.index].some(item => item !== null && item !== '')
        this.$set(this.fillStatus, this.index, answered)
      }
    },
    fill(index) { //填空题
      let list = this.topic[2] || []
      let len = list.length
      this.currentType = 2
      this.index = index
      if (index < len) {
        if (index < 0) {
          index = (this.topic[1] ? this.topic[1].length : 1) - 1
          this.change(index)
        } else {
          this.title = "请在横线处填写答案"
          let Data = this.topic[2]
          this.showQuestion = Data[index].question //获取题目信息
          this.part = this.questionBlankCount(this.showQuestion)
          this.number = (this.topicCount[0] || 0) + index + 1
        }
      } else if (index >= len) {
        this.index = 0
        this.judge(this.index)
      }
    },
    judge(index) { //判断题
      let len = (this.topic[3] || []).length
      this.currentType = 3
      this.index = index
      if (this.index < len) {
        if (this.index < 0) {
          this.index = (this.topic[2] ? this.topic[2].length : 1) - 1
          this.fill(this.index)
        } else {
          this.title = "请作出正确判断"
          let Data = this.topic[3]
          this.showQuestion = Data[index].question //获取题目信息
          this.number = (this.topicCount[0] || 0) + (this.topicCount[1] || 0) + index + 1
        }
      } else if (this.index >= len) {
        this.index = 0
        this.change(this.index)
      }
    },
    getChangeLabel(val) { //获取选择题作答选项
      this.$set(this.radio, this.index, val) //当前选择的序号
      if (val) {
        let data = this.topic[1]
        this.bg_flag = true
        if (data && data[this.index]) {
          data[this.index]["isClick"] = true
        }
      }
      /* 保存学生答题选项 */
      this.$set(this.topic1Answer, this.index, val)
      this.saveProgress(false)
    },
    getJudgeLabel(val) {  //获取判断题作答选项
      this.$set(this.judgeAnswer, this.index, val)
      if (val) {
        let data = this.topic[3]
        this.bg_flag = true
        if (data && data[this.index]) {
          data[this.index]["isClick"] = true
        }
      }
      this.saveProgress(false)
    },
    previous() { //上一题
      this.index--
      switch (this.currentType) {
        case 1:
          this.change(this.index)
          break
        case 2:
          this.fill(this.index)
          break
        case 3:
          this.judge(this.index)
          break
      }
    },
    next() { //下一题
      this.index++
      switch (this.currentType) {
        case 1:
          this.change(this.index)
          break
        case 2:
          this.fill(this.index)
          break
        case 3:
          this.judge(this.index)
          break
      }
    },
    mark() { //标记功能
      switch (this.currentType) {
        case 1:
          this.topic[1][this.index]["isMark"] = true //选择题标记
          break
        case 2:
          this.topic[2][this.index]["isMark"] = true //填空题标记
          break
        case 3:
          this.topic[3][this.index]["isMark"] = true //判断题标记
      }
    },
    saveProgress(showToast = true) { //暂存答题进度
      if (!this.progressKey) {
        return
      }
      const payload = {
        radio: this.radio,
        fillAnswer: this.fillAnswer,
        judgeAnswer: this.judgeAnswer,
        topic1Answer: this.topic1Answer,
        currentType: this.currentType,
        index: this.index,
        remainingSeconds: this.remainingSeconds
      }
      localStorage.setItem(this.progressKey, JSON.stringify(payload))
      if (showToast) {
        this.$message.success('已暂存当前答题进度')
      }
    },
    restoreProgress() {
      if (!this.progressKey) {
        this.updateCurrentQuestion()
        return
      }
      const raw = localStorage.getItem(this.progressKey)
      if (!raw) {
        this.updateCurrentQuestion()
        return
      }
      try {
        const parsed = JSON.parse(raw)
        this.radio = parsed.radio || this.radio
        this.fillAnswer = parsed.fillAnswer || this.fillAnswer
        this.judgeAnswer = parsed.judgeAnswer || this.judgeAnswer
        this.topic1Answer = parsed.topic1Answer || this.topic1Answer
        if (parsed.currentType) this.currentType = parsed.currentType
        if (parsed.index !== undefined) this.index = parsed.index
        if (parsed.remainingSeconds) {
          this.remainingSeconds = Math.min(this.remainingSeconds || parsed.remainingSeconds, parsed.remainingSeconds)
        }
        this.fillStatus = this.fillAnswer.map(item => Array.isArray(item) && item.some(v => v !== null && v !== ''))
        this.updateCurrentQuestion()
      } catch (e) {
        this.updateCurrentQuestion()
      }
    },
    updateCurrentQuestion() {
      switch (this.currentType) {
        case 1:
          this.change(this.index)
          break
        case 2:
          this.fill(this.index)
          break
        case 3:
          this.judge(this.index)
          break
        default:
          this.change(0)
      }
    },
    calculateScore() { //统一计算分数
      let finalScore = 0
      const choiceList = this.topic[1] || []
      choiceList.forEach((q, idx) => {
        const selected = this.topic1Answer[idx]
        if (!selected && selected !== 0) {
          return
        }
        const mapping = { 1: "A", 2: "B", 3: "C", 4: "D" }
        const right = mapping[selected]
        if (right && right === q.rightAnswer) {
          finalScore += Number(q.score || 0)
        }
      })

      const fillList = this.topic[2] || []
      fillList.forEach((q, idx) => {
        const expectAnswers = this.normalizeFillAnswers(q.answer)
        const blanks = Math.max(expectAnswers.length, this.questionBlankCount(q.question))
        const studentAnswers = (this.fillAnswer[idx] || []).slice(0, blanks).map(ans => this.normalizeText(ans))
        const expected = expectAnswers.length ? expectAnswers : [this.normalizeText(q.answer)]
        let correctCount = 0
        for (let i = 0; i < blanks; i++) {
          const expect = expected[i] || expected[expected.length - 1]
          const actual = studentAnswers[i]
          if (expect && actual && expect === actual) {
            correctCount++
          }
        }
        if (blanks > 0 && correctCount > 0) {
          const scorePerBlank = Number(q.score || 0) / blanks
          finalScore += scorePerBlank * correctCount
        }
      })

      const judgeList = this.topic[3] || []
      judgeList.forEach((q, idx) => {
        const val = this.judgeAnswer[idx]
        const right = val === 1 ? "T" : val === 2 ? "F" : ""
        if (right && right === q.answer) {
          finalScore += Number(q.score || 0)
        }
      })
      return Math.round(finalScore)
    },
    clearProgress() {
      if (this.progressKey) {
        localStorage.removeItem(this.progressKey)
      }
    },
    commit(id, forceSubmit = false) { //答案提交计算分数
      if (this.submitting) {
        return
      }
      const finalScore = this.calculateScore()
      const submit = () => {
        this.submitting = true
        let date = new Date()
        this.endTime = this.getTime(date)
        let answerDate = this.endTime.substr(0, 10)
        //提交成绩信息
        this.$axios({
          url: '/api/score',
          method: 'post',
          data: {
            examCode: this.examData.examCode, //考试编号
            studentId: this.userInfo.id, //学号
            subject: this.examData.source, //课程名称
            etScore: finalScore, //答题成绩
            answerDate: answerDate, //答题日期
          }
        }).then(res => {
          if (res.data.code == 200) {
            this.clearProgress()
            this.$router.push({
              path: '/studentScore', query: {
                examCode: id,
                score: finalScore,
                startTime: this.startTime,
                endTime: this.endTime
              }
            })
          } else {
            this.$message.error(res.data.message || '成绩提交失败，请稍后再试')
          }
        }).finally(() => {
          this.submitting = false
          this.clearTimer()
        })
      }
      if (!forceSubmit && this.remainingSeconds > 0) {
        this.$confirm("考试结束时间未到,是否提前交卷", "友情提示", {
          confirmButtonText: '立即交卷',
          cancelButtonText: '再检查一下',
          type: 'warning'
        }).then(() => {
          submit()
        }).catch(() => {
          // 继续答题
        })
      } else {
        submit()
      }
    }
  },
  computed: {
    ...mapState(["isPractice"]),
    timeText() {
      const total = Math.max(this.remainingSeconds, 0)
      const m = String(Math.floor(total / 60)).padStart(2, '0')
      const s = String(total % 60).padStart(2, '0')
      return `${m}:${s}`
    }
  }
}
</script>

<style lang="less">
.iconfont.icon-time {
  color: #2776df;
  margin: 0px 6px 0px 20px;
}

.analysis {
  margin-top: 20px;

  .right {
    color: #2776df;
    font-size: 18px;
    border: 1px solid #2776df;
    padding: 0px 6px;
    border-radius: 4px;
    margin-left: 20px;
  }

  ul li:nth-child(2) {
    margin: 20px 0px;
  }

  ul li:nth-child(3) {
    padding: 10px;
    background-color: #d3c6c9;
    border-radius: 4px;
  }
}

.analysis span:nth-child(1) {
  font-size: 18px;
}

.mark {
  position: absolute;
  width: 4px;
  height: 4px;
  content: "";
  background-color: red;
  border-radius: 50%;
  top: 0px;
  left: 22px;
}

.border {
  position: relative;
  border: 1px solid #FF90AA !important;
}

.bg {
  background-color: #5188b8 !important;
}

.fill .el-input {
  display: inline-flex;
  width: 150px;
  margin-left: 20px;

  .el-input__inner {
    border: 1px solid transparent;
    border-bottom: 1px solid #eee;
    padding-left: 20px;
  }
}

/* slider过渡效果 */
.slider-fade-enter-active {
  transition: all .3s ease;
}

.slider-fade-leave-active {
  transition: all .3s cubic-bezier(1.0, 0.5, 0.8, 1.0);
}

.slider-fade-enter, .slider-fade-leave-to {
  transform: translateX(-100px);
  opacity: 0;
}

.operation .end li:nth-child(2) {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: rgb(39, 118, 223);
  border-radius: 50%;
  width: 50px;
  height: 50px;
  color: #fff;
}

.operation .end li {
  cursor: pointer;
  margin: 0 100px;
}

.operation {
  background-color: #fff;
  border-radius: 4px;
  padding: 10px 0px;
  margin-right: 10px;
}

.operation .end {
  display: flex;
  justify-content: center;
  align-items: center;
  color: rgb(39, 118, 223);
}

.content .number {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  width: 20px;
  height: 20px;
  background-color: rgb(39, 118, 223);
  border-radius: 4px;
  margin-right: 4px;
}

.content {
  padding: 0px 20px;
}

.content .topic {
  padding: 20px 0px;
  padding-top: 30px;
}

.right .content {
  background-color: #fff;
  margin: 10px;
  margin-left: 0px;
  height: 470px;
}

.content .el-radio-group label {
  color: #000;
  margin: 5px 0px;
}

.content .el-radio-group {
  display: flex;
  flex-direction: column;
}

.right .title p {
  margin-left: 20px;
}

.flexarea {
  display: flex;
}

.flexarea .right {
  flex: 1;
}

.auto-right {
  margin-left: auto;
  color: #2776df;
  margin-right: 10px;
}

.right .title {
  margin-right: 10px;
  padding-right: 30px;
  display: flex;
  margin-top: 10px;
  background-color: #fff;
  height: 50px;
  line-height: 50px;
}

.clearfix {
  clear: both;
}

.l-bottom .final {
  cursor: pointer;
  display: inline-block;
  text-align: center;
  background-color: rgb(39, 118, 223);
  width: 240px;
  margin: 20px 0px 20px 10px;
  border-radius: 4px;
  height: 30px;
  line-height: 30px;
  color: #fff;
  margin-top: 22px;
}

.actions {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 10px 10px 10px;
}

.l-bottom .save {
  cursor: pointer;
  display: inline-block;
  text-align: center;
  background-color: #ecf5ff;
  width: 120px;
  border-radius: 4px;
  height: 30px;
  line-height: 30px;
  color: #2776df;
  border: 1px solid #c6e2ff;
}

#answer .left .item {
  padding: 0px;
  font-size: 16px;
}

.l-bottom {
  border-radius: 4px;
  background-color: #fff;
}

.l-bottom .item p {
  margin-bottom: 15px;
  margin-top: 10px;
  color: #000;
  margin-left: 10px;
  letter-spacing: 2px;
}

.l-bottom .item li {
  width: 15%;
  margin-left: 5px;
  margin-bottom: 10px;
}

.l-bottom .item {
  display: flex;
  flex-direction: column;
}

.l-bottom .item ul {
  width: 100%;
  margin-bottom: -8px;
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
}

.l-bottom .item ul li a {
  position: relative;
  justify-content: center;
  display: inline-flex;
  align-items: center;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background-color: #fff;
  border: 1px solid #eee;
  text-align: center;
  color: #000;
  font-size: 16px;
}

.left .l-top {
  display: flex;
  justify-content: space-around;
  padding: 16px 0px;
  border: 1px solid #eee;
  border-radius: 4px;
  margin-bottom: 10px;
  background-color: #fff;
}

.left {
  width: 260px;
  height: 100%;
  margin: 10px 10px 0px 10px;
}

.left .l-top li:nth-child(2) a {
  border: 1px solid #eee;
}

.left .l-top li:nth-child(3) a {
  background-color: #5188b8;
  border: none;
}

.left .l-top li:nth-child(4) a {
  position: relative;
  border: 1px solid #eee;
}

.left .l-top li:nth-child(4) a::before {
  width: 4px;
  height: 4px;
  content: " ";
  position: absolute;
  background-color: red;
  border-radius: 50%;
  top: 0px;
  left: 16px;
}

.left .l-top li {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}

.left .l-top li a {
  display: inline-block;
  padding: 10px;
  border-radius: 50%;
  background-color: #fff;
  border: 1px solid #FF90AA;
}

#answer .top {
  background-color: rgb(39, 118, 223);
}

#answer .item {
  color: #fff;
  display: flex;
  padding: 20px;
  font-size: 20px;
}

#answer .top .item li:nth-child(1) {
  margin-right: 10px;
}

#answer .top .item li:nth-child(3) {
  position: relative;
  margin-left: auto;
}

#answer {
  padding-bottom: 30px;
}

.icon20 {
  font-size: 20px;
  font-weight: bold;
}

.item .msg {
  padding: 10px 15px;
  border-radius: 4px;
  top: 47px;
  right: -30px;
  color: #6c757d;
  position: absolute;
  border: 1px solid rgba(0, 0, 0, .15);
  background-color: #fff;
}

.item .msg p {
  font-size: 16px;
  width: 200px;
  text-align: left;
}
</style>
