// 我的试卷页面
<template>
  <div id="myExam" class="student-section">
    <div class="title">我的试卷</div>
    <div class="wrapper">
      <ul class="top">
        <li class="order">试卷列表</li>
        <li class="search-li"><div class="icon"><input type="text" placeholder="试卷名称" class="search" v-model="key"><i class="el-icon-search"></i></div></li>
        <li><el-button type="primary" @click="search()">搜索试卷</el-button></li>
      </ul>
      <ul class="paper" v-loading="loading">
        <li class="item" v-for="(item,index) in pagination.records" :key="index">
          <h4 @click="toExamMsg(item.examCode)">{{item.source}}</h4>
          <p class="name">{{item.source}}-{{item.description}}</p>
          <div class="info">
            <i class="el-icon-loading"></i><span>{{item.examDate.substr(0,10)}}</span>
            <i class="iconfont icon-icon-time"></i><span v-if="item.totalTime != null">限时{{item.totalTime}}分钟</span>
            <i class="iconfont icon-fenshu"></i><span>满分{{item.totalScore}}分</span>
          </div>
        </li>
      </ul>
      <div class="pagination">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.current"
          :page-sizes="[6, 10, 20, 40]"
          :page-size="pagination.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total">
        </el-pagination>
      </div>
    </div>
  </div>
</template>

<script >
export default {
  // name: 'myExam'
  data() {
    return {
      loading: false,
      key: null, //搜索关键字
      allExam: null, //所有考试信息
      pagination: { //分页后的考试信息
        current: 1, //当前页
        total: null, //记录条数
        size: 6 //每页条数
      }
    }
  },
  created() {
    this.getExamInfo()
    this.loading = true
  },
  // watch: {
    
  // },
  methods: {
    //获取当前所有考试信息
    getExamInfo() {
      this.$axios(`/api/exams/${this.pagination.current}/${this.pagination.size}`).then(res => {
        this.pagination = res.data.data
        this.loading = false
        console.log(this.pagination)
      }).catch(error => {
        console.log(error)
      })
    },
    //改变当前记录条数
    handleSizeChange(val) {
      this.pagination.size = val
      this.getExamInfo()
    },
    //改变当前页码，重新发送请求
    handleCurrentChange(val) {
      this.pagination.current = val
      this.getExamInfo()
    },
    //搜索试卷
    search() {
      this.$axios('/api/exams').then(res => {
        if(res.data.code == 200) {
          let allExam = res.data.data
          let newPage = allExam.filter(item => {
            return item.source.includes(this.key)
          })
          this.pagination.records = newPage
        }
      })
    },
    //跳转到试卷详情页
    toExamMsg(examCode) {
      this.$router.push({path: '/examMsg', query: {examCode: examCode}})
      console.log(examCode)
    }
  }
}
</script>

<style lang="less" scoped>
#myExam {
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
  color: var(--text-main);
}

#myExam .title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
}

.wrapper {
  background: transparent;
}

.top {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  list-style: none;
  gap: 12px;
  margin: 0 0 20px;
  padding: 20px;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--panel-strong);
}

.wrapper .top .order {
  font-weight: 600;
  cursor: pointer;
  padding-bottom: 4px;
  border-bottom: 2px solid transparent;
  transition: var(--transition);
}

.wrapper .top .order:hover {
  color: var(--brand);
  border-color: var(--brand);
}

.top .search-li {
  margin-left: auto;
  flex: 1;
  min-width: 220px;
}

.top .icon {
  position: relative;
}

.top .search {
  width: 100%;
  padding: 10px 36px 10px 14px;
  border-radius: 999px;
  border: 1px solid var(--border);
  background: rgba(255, 255, 255, 0.04);
  color: var(--text-main);
  transition: var(--transition);
}

.top .search::placeholder {
  color: var(--text-muted);
}

.top .search:focus,
.top .search:hover {
  border-color: rgba(14, 165, 233, 0.7);
  outline: none;
}

.top .el-icon-search {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-muted);
}

.top li {
  display: flex;
  align-items: center;
}

.top ::v-deep .el-button--primary {
  background: var(--brand);
  border-color: var(--brand);
  color: #0b1221;
  transition: var(--transition);
}

.top ::v-deep .el-button--primary:hover {
  background: var(--brand-strong);
  border-color: var(--brand-strong);
}

.wrapper .paper {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.paper .item {
  border-radius: var(--radius);
  padding: 20px;
  border: 1px solid var(--border);
  background: var(--panel);
  box-shadow: var(--shadow);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.paper .item:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 30px rgba(0, 0, 0, 0.35);
}

.paper .item a,
.paper .item h4 {
  color: var(--text-main);
  margin: 0 0 10px;
}

.paper .item .info,
.paper .item .name {
  font-size: 14px;
  color: var(--text-muted);
}

.item .info i {
  margin-right: 5px;
  color: var(--brand);
}

.item .info span {
  margin-right: 12px;
}

.pagination {
  padding: 28px 0 10px;
}

.pagination ::v-deep .el-pagination {
  display: flex;
  justify-content: center;
}

#myExam .wrapper .paper,
#myExam .wrapper .top {
  width: 100%;
}

@media (max-width: 640px) {
  .top {
    padding: 16px;
  }

  .top .search-li {
    flex: 1 1 100%;
  }
}
</style>
