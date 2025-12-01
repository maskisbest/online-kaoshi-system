// 点击试卷后的缩略信息
<template>
  <div id="msg">
    <div class="title">
      <span>试卷列表</span>
      <span>/ {{ examData.source }}</span>
    </div>
    <div class="wrapper">
      <ul class="top">
        <li class="example">{{ examData.source }}</li>
        <li><i class="iconfont icon-pen-"></i></li>
        <li><i class="iconfont icon-share"></i></li>
        <li class="right">
          <div>
            <span class="count">总分</span>
            <span class="score">{{ totalScore }}</span>
          </div>
        </li>
      </ul>
      <ul class="bottom">
        <li>考试日期：{{ examData.examDate }}</li>
        <li>来自 {{ examData.institute }}</li>
        <li class="btn">{{ examData.type }}</li>
        <li class="right status">
          <el-tag :type="statusInfo.tag" size="small">{{
            statusInfo.label
          }}</el-tag>
          <el-button
            :disabled="statusInfo.value !== 'ongoing'"
            @click="toAnswer(examData.examCode)"
            >开始答题</el-button
          >
        </li>
      </ul>
      <ul class="info">
        <li @click="dialogVisible = true">
          <a href="javascript:;"><i class="iconfont icon-info"></i>考生须知</a>
        </li>
      </ul>
    </div>
    <div class="content">
      <el-collapse v-model="activeName">
        <el-collapse-item class="header" name="0">
          <template slot="title" class="stitle">
            <div class="title">
              <span>{{ examData.source }}</span
              ><i class="header-icon el-icon-info"></i>
              <span class="time"
                >{{ examData.totalScore }}分 /
                {{ examData.totalTime }}分钟</span
              >
              <el-button type="primary" size="small"
                >点击查看试题详情</el-button
              >
            </div>
          </template>
          <el-collapse class="inner">
            <el-collapse-item>
              <template slot="title" name="1">
                <div class="titlei">
                  选择题 (共{{ topicCount[0] }}题 共计{{ score[0] }}分)
                </div>
              </template>
              <div class="contenti">
                <ul
                  class="question"
                  v-for="(list, index) in topic[1]"
                  :key="index"
                >
                  <li>
                    {{ index + 1 }}. {{ list.question }} {{ list.score }}分
                  </li>
                </ul>
              </div>
            </el-collapse-item>
            <el-collapse-item>
              <template slot="title" name="2">
                <div class="titlei">
                  填空题 (共{{ topicCount[1] }}题 共计{{ score[1] }}分)
                </div>
              </template>
              <div class="contenti">
                <ul
                  class="question"
                  v-for="(list, index) in topic[2]"
                  :key="index"
                >
                  <li>
                    {{ topicCount[0] + index + 1 }}.{{ list.question }}
                    {{ list.score }}分
                  </li>
                </ul>
              </div>
            </el-collapse-item>
            <el-collapse-item>
              <template slot="title" name="3">
                <div class="titlei">
                  判断题 (共{{ topicCount[2] }}题 共计{{ score[2] }}分)
                </div>
              </template>
              <div class="contenti">
                <ul
                  class="question"
                  v-for="(list, index) in topic[3]"
                  :key="index"
                >
                  <li>
                    {{ topicCount[0] + topicCount[1] + index + 1 }}.
                    {{ list.question }} {{ list.score }}分
                  </li>
                </ul>
              </div>
            </el-collapse-item>
            <el-collapse-item>
              <template slot="title" name="4">
                <div class="titlei">
                  主观题 (共{{ topicCount[3] }}题 共计{{ score[3] }}分)
                </div>
              </template>
              <div class="contenti">
                <ul
                  class="question"
                  v-for="(list, index) in topic[4]"
                  :key="index"
                >
                  <li>
                    {{
                      topicCount[0] + topicCount[1] + topicCount[2] + index + 1
                    }}. {{ list.question }} {{ list.score }}分
                  </li>
                </ul>
              </div>
            </el-collapse-item>
          </el-collapse>
        </el-collapse-item>
      </el-collapse>
    </div>
    <!--考生须知对话框-->
    <el-dialog title="考生须知" :visible.sync="dialogVisible" width="30%">
      <span>{{ examData.tips }}</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">知道了</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      dialogVisible: false, //对话框属性
      activeName: "0", //默认打开序号
      topicCount: [0, 0, 0, 0], //每种类型题目的总数
      score: [0, 0, 0, 0], //每种类型分数的总数
      examData: {
        //考试信息
        // source: null,
        // totalScore: null,
      },
      topic: {
        //试卷信息
        1: [],
        2: [],
        3: [],
        4: [],
      },
      status: "pending",
    };
  },
  mounted() {
    this.init();
  },
  methods: {
    //初始化页面数据
    init() {
      let examCode = this.$route.query.examCode; //获取路由传递过来的试卷编号
      this.$axios(`/api/exam/${examCode}`).then((res) => {
        //通过examCode请求试卷详细信息
        res.data.data.examDate = res.data.data.examDate.substr(0, 10);
        this.examData = { ...res.data.data };
        this.status = this.getExamStatus(
          this.examData.examDate,
          this.examData.totalTime
        );
        let paperId = this.examData.paperId;
        this.$axios(`/api/paper/${paperId}`).then((res) => {
          //通过paperId获取试题题目信息
          this.topic = {
            1: res.data[1] || [],
            2: res.data[2] || [],
            3: res.data[3] || [],
            4: res.data[4] || [],
          };
          const order = [1, 2, 3, 4];
          this.topicCount = order.map(
            (type) => (this.topic[type] || []).length
          );
          this.score = order.map((type) => {
            const list = this.topic[type] || [];
            return list.reduce((sum, q) => sum + Number(q.score || 0), 0);
          });
        });
      });
    },
    toAnswer(id) {
      if (this.status !== "ongoing") {
        this.$message.warning("当前不在考试开放时间，无法开始答题");
        return;
      }
      this.$router.push({ path: "/answer", query: { examCode: id } });
    },
    getExamStatus(examDate, totalTime) {
      if (!examDate) return "pending";
      const todayStr = new Date().toISOString().slice(0, 10);
      if (examDate === todayStr) return "ongoing";
      const start = new Date(`${examDate.replace(/-/g, "/")} 00:00:00`);
      const now = new Date();
      if (now < start) return "notStarted";
      return "ended";
    },
  },
  computed: {
    totalScore() {
      return this.score.reduce((sum, val) => sum + Number(val || 0), 0);
    },
    statusInfo() {
      const map = {
        notStarted: { label: "未开始", tag: "info", value: "notStarted" },
        ongoing: { label: "考试中", tag: "success", value: "ongoing" },
        ended: { label: "已结束", tag: "danger", value: "ended" },
        pending: { label: "未获取", tag: "warning", value: "pending" },
      };
      return map[this.status] || map.pending;
    },
  },
};
</script>

<style lang="less" scoped>
.bottom {
  .right {
    .el-button {
      color: #409eff;
      border-color: #c6e2ff;
      background-color: #ecf5ff;
    }
  }
}
.right {
  margin-left: auto;
}
.inner .contenti .question {
  margin-left: 40px;
  color: #1f2937;
  font-size: 15px;
  line-height: 1.7;
}
.content .inner .titlei {
  margin-left: 20px;
  font-size: 16px;
  color: #0f172a;
  font-weight: bold;
}
.content .title .time {
  font-size: 16px;
  margin-left: 420px;
  color: #475569;
  font-weight: 500;
}
.content .stitle {
  background-color: #0195ff;
}
.content .title span {
  margin-right: 10px;
}
#msg .content .title {
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
  margin: 0px;
  display: flex;
  align-items: center;
}
.content {
  margin-top: 20px;
  background-color: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.06);
}
.content .header {
  padding: 10px 30px;
}
.wrapper .info {
  margin: 20px 0px 0px 20px;
  border-top: 1px solid #eee;
  padding: 20px 0px 10px 0px;
}
.wrapper .info a {
  color: #334155;
  font-size: 14px;
  font-weight: 600;
}
.wrapper .info a:hover {
  color: #0195ff;
}
.wrapper .bottom .btn {
  cursor: pointer;
  padding: 5px 10px;
  border: 1px solid #88949b;
  border-radius: 4px;
}
.wrapper .bottom {
  display: flex;
  margin-left: 20px;
  color: #334155;
  font-size: 15px;
  align-items: center;
}
.wrapper .bottom li {
  margin-right: 14px;
}
.wrapper .bottom .status {
  display: flex;
  align-items: center;
  gap: 8px;
}
#msg {
  background-color: #f6f8fb;
  width: 980px;
  margin: 0 auto;
}
#msg .title {
  margin: 20px;
  font-size: 22px;
  font-weight: 700;
  color: #0f172a;
}
#msg .wrapper {
  background-color: #fff;
  padding: 10px;
}
.wrapper .top {
  display: flex;
  margin: 20px;
  align-items: center;
}
.wrapper .top .right {
  margin-left: auto;
}
.wrapper .top .example {
  color: #0f172a;
  font-size: 22px;
  font-weight: 700;
}
.wrapper .top li i {
  margin-left: 20px;
  color: #88949b;
}
.wrapper .right .count {
  margin-right: 60px;
  color: #fff;
  padding: 4px 10px;
  background-color: #88949b;
  border-top-left-radius: 4px;
  border-bottom-left-radius: 4px;
  border: 1px solid #88949b;
}
.wrapper .right .score {
  position: absolute;
  left: 53px;
  top: -5px;
  padding: 1px 12px;
  font-size: 20px;
  color: #88949b;
  border: 1px dashed #88949b;
  border-left: none;
  border-top-right-radius: 4px;
  border-bottom-right-radius: 4px;
  font-weight: bold;
}
.wrapper .right div {
  position: relative;
}
</style>
