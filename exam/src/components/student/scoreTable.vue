//显示学生成绩
<template>
  <div class="table">
    <p class="title">我的分数</p>
    <section class="content-el">
      <el-table ref="filterTable" :data="score" v-loading="loading">
        <el-table-column
          prop="answerDate"
          label="考试日期"
          sortable
          width="300"
          column-key="answerDate"
          :filters="filter"
          :filter-method="filterHandler">
        </el-table-column>
        <el-table-column
          prop="subject"
          label="课程名称"
          width="300"
          filter-placement="bottom-end">
          <template slot-scope="scope">
            <el-tag>{{scope.row.subject}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="etScore" label="考试分数" width="200"></el-table-column>
        <el-table-column label="是否及格" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.etScore>= 60 ? 'success' : 'danger'">{{scope.row.etScore >= 60 ? "及格" : "不及格"}}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-row type="flex" justify="center" align="middle" class="pagination">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.current"
          :page-sizes="[4,6,8,10]"
          :page-size="pagination.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total">
        </el-pagination>
      </el-row>
    </section>
  </div>
</template>

<script>
export default {
  data() {
    return {
      pagination: { //分页后的留言列表
        current: 1, //当前页
        total: null, //记录条数
        size: 10 //每页条数
      },
      loading: false, //加载标识符
      score: [], //学生成绩
      filter: null //过滤参数
    }
  },
  created() {
    this.getScore()
    this.loading = true //数据加载则遮罩表格
  },
  methods: {
    getScore() {
      let studentId = this.$cookies.get("cid")
      this.$axios(`/api/score/${this.pagination.current}/${this.pagination.size}/${studentId}`).then(res => {
        if(res.data.code == 200) {
          this.loading = false //数据加载完成去掉遮罩
          this.score = res.data.data.records
          this.pagination = {...res.data.data}
          let mapVal = this.score.map((element,index) => { //通过map得到 filter:[{text,value}]形式的数组对象
            let newVal = {}
            newVal.text = element.answerDate
            newVal.value = element.answerDate
            return newVal
          })
          let hash = []
          const newArr = mapVal.reduce((item, next) => { //对新对象进行去重操作
            hash[next.text] ? '' : hash[next.text] = true && item.push(next);
            return item
          }, []);
          this.filter = newArr
        }
      })
    },
    //改变当前记录条数
    handleSizeChange(val) {
      this.pagination.size = val
      this.getScore()
    },
    //改变当前页码，重新发送请求
    handleCurrentChange(val) {
      this.pagination.current = val
      this.getScore()
    },
    formatter(row, column) {
      return row.address;
    },
    filterTag(value, row) {
      return row.tag === value;
    },
    filterHandler(value, row, column) {
      const property = column["property"];
      return row[property] === value;
    }
  }
};
</script>

<style lang="less" scoped>
.pagination {
  padding-top: 20px;
}
.table {
  width: 1100px;
  margin: 0 auto;
  padding: 24px 0 48px;
  .title {
    margin: 20px;
    color: #f5f7ff;
    font-size: 22px;
    font-weight: 700;
  }
  .content-el {
    background: rgba(10, 14, 22, 0.82);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 16px;
    padding: 24px 20px;
    box-shadow: 0 20px 50px rgba(0, 0, 0, 0.5);
    backdrop-filter: blur(14px);
    -webkit-backdrop-filter: blur(14px);
  }
}

// 表格主题适配暗色玻璃
:global(.content-el .el-table) {
  background: transparent;
  color: #e8edf8;
}

:global(.content-el .el-table__header-wrapper),
:global(.content-el .el-table__body-wrapper),
:global(.content-el .el-table__fixed-body-wrapper),
:global(.content-el .el-table__footer-wrapper) {
  background: transparent !important;
}

:global(.content-el .el-table th) {
  background: rgba(255, 255, 255, 0.05) !important;
  color: #dfe7ff;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

:global(.content-el .el-table td) {
  background: transparent !important;
  color: #d8e4ff;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

:global(.content-el .el-table tr) {
  background: transparent !important;
}

:global(.content-el .el-table--striped .el-table__body tr.el-table__row--striped td) {
  background: rgba(255, 255, 255, 0.02);
}

:global(.content-el .el-table__body tr:hover > td) {
  background: rgba(255, 255, 255, 0.05) !important;
}

:global(.content-el .el-tag) {
  background: rgba(255, 255, 255, 0.08);
  color: #f0f4ff;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

:global(.content-el .el-tag--success) {
  background: rgba(63, 191, 127, 0.15);
  color: #9ef2c6;
  border-color: rgba(63, 191, 127, 0.45);
}

:global(.content-el .el-tag--danger) {
  background: rgba(255, 98, 98, 0.15);
  color: #ffb3b3;
  border-color: rgba(255, 98, 98, 0.35);
}

:global(.content-el .el-table__empty-text) {
  color: #d8e4ff;
}

// 分页样式
:global(.content-el .el-pagination) {
  color: #dfe7ff;
}

:global(.content-el .el-pager li) {
  background: transparent;
  color: #dfe7ff;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

:global(.content-el .el-pager li.active) {
  background: linear-gradient(120deg, #4da1ff, #7c8bff);
  color: #f8fbff;
  border: none;
  box-shadow: 0 10px 20px rgba(67, 137, 255, 0.35);
}

:global(.content-el .el-pagination button) {
  background: transparent;
  color: #dfe7ff;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

:global(.content-el .el-pagination button.is-disabled) {
  color: rgba(255, 255, 255, 0.4);
  border-color: rgba(255, 255, 255, 0.08);
}
</style>
