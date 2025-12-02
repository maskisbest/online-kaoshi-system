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
      <div class="collapse-shell">
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
      if (!examCode || examCode === "undefined") {
        this.$message.warning("请先从试卷列表选择一份试卷");
        this.$router.replace("/student");
        return;
      }
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
#msg {
  min-height: 100vh;
  padding: 32px 0 64px;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: radial-gradient(circle at 20% 20%, rgba(83, 139, 255, 0.2), transparent 35%),
    radial-gradient(circle at 80% 0%, rgba(255, 255, 255, 0.08), transparent 30%),
    linear-gradient(135deg, #0c1320 0%, #101827 35%, #0c111c 100%);
  color: #e8edf8;
}

#msg .title {
  width: 100%;
  max-width: 1180px;
  margin: 0 20px 16px;
  font-size: 22px;
  font-weight: 700;
  color: #f5f7ff;
  letter-spacing: 1px;
}

#msg .wrapper,
.content {
  width: 100%;
  max-width: 1180px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.12);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.45);
  border-radius: 18px;
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
}

#msg .wrapper {
  padding: 18px 22px 12px;
}

.wrapper .top {
  display: flex;
  margin: 8px 10px 18px;
  align-items: center;
  color: #eef3ff;
  gap: 10px;
}

.wrapper .top .example {
  color: #f8fbff;
  font-size: 22px;
  font-weight: 700;
  text-shadow: 0 6px 14px rgba(0, 0, 0, 0.35);
}

.wrapper .top li i {
  margin-left: 18px;
  color: #93b4ff;
}

.wrapper .top .right {
  margin-left: auto;
}

.wrapper .right div {
  position: relative;
}

.wrapper .right .count {
  margin-right: 60px;
  color: #0d1830;
  padding: 6px 12px;
  background: linear-gradient(135deg, #9ac5ff, #d7e7ff);
  border-top-left-radius: 8px;
  border-bottom-left-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.45);
  font-weight: 700;
}

.wrapper .right .score {
  position: absolute;
  left: 60px;
  top: -6px;
  padding: 4px 16px;
  font-size: 20px;
  color: #dce7ff;
  border: 1px dashed rgba(255, 255, 255, 0.35);
  border-left: none;
  border-top-right-radius: 8px;
  border-bottom-right-radius: 8px;
  font-weight: bold;
  background: rgba(255, 255, 255, 0.06);
}

.wrapper .bottom {
  display: flex;
  margin: 6px 16px 0;
  color: #d5def2;
  font-size: 15px;
  align-items: center;
  gap: 14px;
}

.wrapper .bottom .btn {
  cursor: pointer;
  padding: 6px 12px;
  border: 1px solid rgba(255, 255, 255, 0.22);
  border-radius: 6px;
  color: #c6d9ff;
  background: rgba(255, 255, 255, 0.05);
}

.wrapper .bottom .status {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: auto;
}

.wrapper .info {
  margin: 18px 0 0 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding: 18px 0 8px 0;
}

.wrapper .info a {
  color: #d9e7ff;
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
}

.wrapper .info a:hover {
  color: #7dc4ff;
}

.wrapper .bottom .status .el-tag {
  background: rgba(255, 98, 98, 0.15);
  border: 1px solid rgba(255, 98, 98, 0.35);
  color: #ffb3b3;
}

.wrapper .bottom .status .el-button {
  background: linear-gradient(120deg, #4da1ff, #7c8bff);
  border: none;
  color: #f8fbff;
  box-shadow: 0 10px 24px rgba(67, 137, 255, 0.35);
}

.wrapper .bottom .status .el-button.is-disabled {
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.45);
  box-shadow: none;
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.content {
  margin-top: 18px;
  padding: 8px 16px;
  background: rgba(10, 14, 22, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
}

.collapse-shell {
  background: rgba(8, 12, 20, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  padding: 10px 12px;
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.45);
}

#msg .content .title {
  font-size: 20px;
  font-weight: 700;
  color: #f3f6ff;
  margin: 0;
  display: flex;
  align-items: center;
  letter-spacing: 0.5px;
}

.content .title span {
  margin-right: 10px;
}

.content .title .time {
  font-size: 15px;
  margin-left: auto;
  color: #c9d8f5;
  font-weight: 500;
}

.content .stitle {
  background: linear-gradient(125deg, rgba(109, 178, 255, 0.4), rgba(129, 140, 248, 0.4));
}

.content .header {
  padding: 12px 16px 6px;
}

.content .inner .titlei {
  margin-left: 20px;
  font-size: 16px;
  color: #f0f4ff;
  font-weight: 700;
}

.inner .contenti .question {
  margin-left: 40px;
  color: #e3ebff;
  font-size: 15px;
  line-height: 1.7;
}

.content .inner {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.06);
  padding: 6px 8px;
}

.content .inner .contenti {
  padding: 6px 10px 12px;
  background: rgba(18, 27, 45, 0.7);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 10px;
  margin: 0 6px 8px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.45), inset 0 1px 0 rgba(255, 255, 255, 0.04);
}

.content .inner .question {
  background: transparent;
}

.content .inner .contenti .question li {
  padding: 10px 12px;
  margin: 6px 0;
  background: linear-gradient(135deg, rgba(40, 56, 84, 0.9), rgba(30, 44, 68, 0.9));
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  box-shadow: 0 12px 26px rgba(0, 0, 0, 0.35);
  color: #e6edff;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.content .title .time,
.wrapper .bottom,
.inner .contenti .question {
  text-shadow: 0 4px 12px rgba(0, 0, 0, 0.35);
}

::v-deep .el-collapse-item__header {
  background: linear-gradient(135deg, rgba(22, 32, 52, 0.9), rgba(16, 24, 40, 0.9)) !important;
  color: #f3f6ff;
  border: 1px solid rgba(255, 255, 255, 0.06) !important;
  border-radius: 10px;
  padding-left: 10px;
  margin-bottom: 8px;
  box-shadow: inset 0 -1px 0 rgba(255, 255, 255, 0.04), 0 8px 18px rgba(0, 0, 0, 0.25);
}

::v-deep .el-collapse-item__header.is-active {
  background: linear-gradient(135deg, rgba(30, 44, 68, 0.95), rgba(18, 26, 42, 0.95)) !important;
}

:global(.collapse-shell .el-collapse) {
  background: transparent !important;
  border: none !important;
}

:global(.collapse-shell .el-collapse-item__header) {
  background: linear-gradient(135deg, rgba(22, 32, 52, 0.9), rgba(16, 24, 40, 0.9)) !important;
  color: #f3f6ff;
  border: 1px solid rgba(255, 255, 255, 0.06) !important;
  border-radius: 10px;
  padding-left: 10px;
  margin-bottom: 8px;
  box-shadow: inset 0 -1px 0 rgba(255, 255, 255, 0.04), 0 8px 18px rgba(0, 0, 0, 0.25);
}

:global(.collapse-shell .el-collapse-item__header.is-active) {
  background: linear-gradient(135deg, rgba(30, 44, 68, 0.95), rgba(18, 26, 42, 0.95)) !important;
}

:global(.collapse-shell .el-collapse-item__wrap) {
  background: rgba(12, 18, 30, 0.85) !important;
  border: 1px solid rgba(255, 255, 255, 0.06) !important;
  border-radius: 12px;
  overflow: hidden;
}

:global(.collapse-shell .el-collapse-item__content) {
  background: transparent !important;
  color: #d7e0f3;
  padding: 8px 10px 12px !important;
}

::v-deep .el-collapse {
  background: transparent !important;
  border: none !important;
}

::v-deep .el-collapse-item {
  background: transparent !important;
  border: none !important;
}

::v-deep .el-collapse-item__wrap {
  background: rgba(12, 18, 30, 0.85) !important;
  border: 1px solid rgba(255, 255, 255, 0.06) !important;
  border-radius: 12px;
  overflow: hidden;
}

::v-deep .el-collapse-item__header,
::v-deep .el-collapse-item__wrap,
::v-deep .el-collapse,
::v-deep .el-collapse-item__content {
  box-shadow: none !important;
}

::v-deep .el-collapse-item__content {
  background: transparent !important;
  color: #d7e0f3;
  padding: 8px 10px 12px !important;
}

::v-deep .el-collapse-item__arrow {
  color: #c8d8ff;
}

::v-deep .el-button {
  background: linear-gradient(120deg, #4da1ff, #7c8bff);
  border: none;
  color: #f8fbff;
  box-shadow: 0 10px 24px rgba(67, 137, 255, 0.35);
}

::v-deep .el-button.is-disabled {
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.45);
  box-shadow: none;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

::v-deep .el-tag {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.18);
  color: #dbeafe;
}

.right {
  margin-left: auto;
}
</style>
