//查询所有考试
<template>
<div class="exam">
    <div class="wrapper">
      <ul class="top">
        <li class="order">试卷列表</li>
        <li class="search-li">
          <div class="icon">
            <input
              type="text"
              placeholder="试卷名称"
              class="search"
              v-model="key"
            /><i class="el-icon-search"></i>
          </div>
        </li>
        <li>
          <el-button type="primary" @click="search()">搜索试卷</el-button>
        </li>
      </ul>
    </div>

    <div class="table-shell">
  <el-table :data="pagination.records" border class="glass-table">
      <el-table-column
        fixed="left"
        prop="source"
        label="试卷名称"
        width="180"
      ></el-table-column>
      <el-table-column
        prop="description"
        label="介绍"
        width="200"
      ></el-table-column>
      <el-table-column
        prop="institute"
        label="所属学院"
        width="120"
      ></el-table-column>
      <el-table-column
        prop="major"
        label="所属专业"
        width="200"
      ></el-table-column>
      <el-table-column prop="grade" label="年级" width="100"></el-table-column>
      <el-table-column
        prop="examDate"
        label="考试日期"
        width="120"
      ></el-table-column>
      <el-table-column
        prop="totalTime"
        label="持续时间"
        width="120"
      ></el-table-column>
      <el-table-column
        prop="totalScore"
        label="总分"
        width="120"
      ></el-table-column>
      <el-table-column
        prop="type"
        label="试卷类型"
        width="120"
      ></el-table-column>
      <el-table-column
        prop="tips"
        label="考生提示"
        width="400"
      ></el-table-column>
      <el-table-column fixed="right" label="操作" width="230">
        <template slot-scope="scope">
          <el-button
            @click="edit(scope.row.examCode)"
            type="primary"
            size="small"
            >编辑</el-button
          >
          <el-button @click="publish(scope.row)" type="success" size="small"
            >发布</el-button
          >
          <el-button
            @click="deleteRecord(scope.row.examCode)"
            type="danger"
            size="small"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
  <el-pagination
    @size-change="handleSizeChange"
    @current-change="handleCurrentChange"
    :current-page="pagination.current"
    :page-sizes="[4, 8, 10, 20]"
    :page-size="pagination.size"
    layout="total, sizes, prev, pager, next, jumper"
    :total="pagination.total"
    class="page"
  >
  </el-pagination>
</div>
    <!-- 编辑对话框-->
    <el-dialog
      title="编辑试卷信息"
      :visible.sync="dialogVisible"
      width="30%"
      :before-close="handleClose"
    >
      <section class="update">
        <el-form ref="form" :model="form" label-width="80px">
          <el-form-item label="试卷名称">
            <el-input v-model="form.source"></el-input>
          </el-form-item>
          <el-form-item label="介绍">
            <el-input v-model="form.description"></el-input>
          </el-form-item>
          <el-form-item label="所属学院">
            <el-input v-model="form.institute"></el-input>
          </el-form-item>
          <el-form-item label="所属专业">
            <el-input v-model="form.major"></el-input>
          </el-form-item>
          <el-form-item label="年级">
            <el-input v-model="form.grade"></el-input>
          </el-form-item>
          <el-form-item label="考试日期">
            <el-col :span="11">
              <el-date-picker
                type="date"
                placeholder="选择日期"
                v-model="form.examDate"
                style="width: 100%"
              ></el-date-picker>
            </el-col>
          </el-form-item>
          <el-form-item label="持续时间">
            <el-input v-model="form.totalTime"></el-input>
          </el-form-item>
          <el-form-item label="总分">
            <el-input v-model="form.totalScore"></el-input>
          </el-form-item>
          <el-form-item label="试卷类型">
            <el-input v-model="form.type"></el-input>
          </el-form-item>
          <el-form-item label="考生提示">
            <el-input type="textarea" v-model="form.tips"></el-input>
          </el-form-item>
        </el-form>
      </section>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submit()">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      form: {}, //保存点击以后当前试卷的信息
      pagination: {
        //分页后的考试信息
        current: 1, //当前页
        total: null, //记录条数
        size: 4, //每页条数
      },
      dialogVisible: false,
    };
  },
  created() {
    this.getExamInfo();
  },
  methods: {
    //搜索试卷
    search() {
      this.$axios("/api/exams").then((res) => {
        if (res.data.code == 200) {
          let allExam = res.data.data;
          let newPage = allExam.filter((item) => {
            return item.source.includes(this.key);
          });
          this.pagination.records = newPage;
        }
      });
    },
    edit(examCode) {
      //编辑试卷
      this.dialogVisible = true;
      this.$axios(`/api/exam/${examCode}`).then((res) => {
        //根据试卷id请求后台
        if (res.data.code == 200) {
          this.form = res.data.data;
        }
      });
    },
    handleClose(done) {
      //关闭提醒
      this.$confirm("确认关闭？")
        .then((_) => {
          done();
        })
        .catch((_) => {});
    },
    submit() {
      //提交修改后的试卷信息
      this.dialogVisible = false;
      this.$axios({
        url: "/api/exam",
        method: "put",
        data: {
          ...this.form,
        },
      }).then((res) => {
        if (res.data.code == 200) {
          this.$message({
            //成功修改提示
            message: "更新成功",
            type: "success",
          });
        }
        this.getExamInfo();
      });
    },
    deleteRecord(examCode) {
      this.$confirm("确定删除该记录吗,该操作不可逆！！！", "删除提示", {
        confirmButtonText: "确定删除",
        cancelButtonText: "算了,留着",
        type: "danger",
      })
        .then(() => {
          //确认删除
          this.$axios({
            url: `/api/exam/${examCode}`,
            method: "delete",
          }).then((res) => {
            this.getExamInfo();
          });
        })
        .catch(() => {});
    },
    publish(row) {
      const today = new Date();
      const y = today.getFullYear();
      const m = String(today.getMonth() + 1).padStart(2, "0");
      const d = String(today.getDate()).padStart(2, "0");
      const examDate = `${y}-${m}-${d}`;
      const payload = {
        ...row,
        examDate,
      };
      this.$axios({
        url: "/api/exam",
        method: "put",
        data: payload,
      }).then((res) => {
        if (res.data.code === 200) {
          this.$message.success("已发布到学生端（考试日期已更新为今天）");
          this.getExamInfo();
        } else {
          this.$message.error(res.data.message || "发布失败");
        }
      });
    },
    getExamInfo() {
      //分页查询所有试卷信息
      this.$axios(
        `/api/exams/${this.pagination.current}/${this.pagination.size}`
      )
        .then((res) => {
          this.pagination = res.data.data;
        })
        .catch((error) => {});
    },
    //改变当前记录条数
    handleSizeChange(val) {
      this.pagination.size = val;
      this.getExamInfo();
    },
    //改变当前页码，重新发送请求
    handleCurrentChange(val) {
      this.pagination.current = val;
      this.getExamInfo();
    },
  },
};
</script>
<style lang="less" scoped>
.exam {
  padding: 24px 40px;
  color: #e8edf8;
}

.wrapper {
  background: rgba(10, 14, 22, 0.82);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 16px 18px;
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.45);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
}

.top {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  list-style: none;
  gap: 12px;
  margin: 0 0 14px;
  padding: 0;
  color: #f5f7ff;
}

.top li {
  display: flex;
  align-items: center;
  margin-right: 12px;
  font-weight: 600;
  font-size: 15px;
  letter-spacing: 1px;
}

.top .search-li {
  margin-left: auto;
  flex: 1;
  min-width: 240px;
}

.top .icon {
  position: relative;
}

.top .search {
  width: 100%;
  padding: 10px 36px 10px 14px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  background: rgba(255, 255, 255, 0.06);
  color: #f3f6ff;
  transition: 0.2s ease;
}

.top .search::placeholder {
  color: rgba(255, 255, 255, 0.55);
}

.top .search:focus,
.top .search:hover {
  border-color: rgba(77, 161, 255, 0.8);
  outline: none;
}

.top .el-icon-search {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: rgba(255, 255, 255, 0.55);
}

.top ::v-deep .el-button--primary {
  background: linear-gradient(120deg, #4da1ff, #7c8bff);
  border: none;
  color: #f8fbff;
  box-shadow: 0 10px 24px rgba(67, 137, 255, 0.35);
}

.table-shell {
  background: rgba(5, 9, 16, 0.92);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 16px;
  padding: 16px;
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.55);
}

.glass-table {
  margin-top: 8px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 14px;
  overflow: hidden;
  background: transparent;
}

:global(.glass-table .el-table),
:global(.glass-table .el-table__header-wrapper),
:global(.glass-table .el-table__body-wrapper),
:global(.glass-table .el-table__inner-wrapper),
:global(.glass-table .el-table__empty-block),
:global(.glass-table .el-table__fixed) {
  background: transparent !important;
  color: #dfe7ff;
}

:global(.glass-table .el-table__header-wrapper thead tr th),
:global(.glass-table .el-table th) {
  background: linear-gradient(120deg, rgba(20, 28, 44, 0.98), rgba(10, 14, 22, 0.98)) !important;
  color: #f7f9ff !important;
  border-color: rgba(255, 255, 255, 0.1) !important;
}

:global(.glass-table .el-table td),
:global(.glass-table .el-table tr) {
  background: rgba(8, 11, 19, 0.97) !important;
  color: #e4e9f8;
}

:global(.glass-table .el-table__body tr:nth-child(odd) > td) {
  background: rgba(8, 11, 19, 0.97) !important;
}

:global(.glass-table .el-table__body tr:hover > td) {
  background: rgba(8, 11, 19, 0.97) !important;
}

:global(.glass-table .el-table td .cell),
:global(.glass-table .el-table th .cell) {
  background: transparent !important;
}

:global(.glass-table .el-table--border::after),
:global(.glass-table .el-table--group::after),
:global(.glass-table .el-table::before),
:global(.glass-table .el-table--border::before),
:global(.glass-table .el-table--group::before) {
  background-color: rgba(255, 255, 255, 0.04);
}

:global(.glass-table .el-table td),
:global(.glass-table .el-table th.is-leaf) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

:global(.glass-table .el-table__empty-text) {
  color: #d8e4ff;
}

:global(.glass-table .el-button--primary) {
  background: linear-gradient(120deg, #4da1ff, #7c8bff);
  border: none;
  color: #f8fbff;
}

.pagination {
  padding: 18px 0 10px;
  color: #dfe7ff;
}

.pagination ::v-deep .el-pagination {
  display: flex;
  justify-content: center;
}

.pagination ::v-deep .el-pagination__total,
.pagination ::v-deep .el-pagination__sizes {
  color: #dfe7ff;
}

.pagination ::v-deep .el-pagination button {
  background: transparent;
  color: #dfe7ff;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.pagination ::v-deep .el-pagination button.is-disabled {
  color: rgba(255, 255, 255, 0.4);
  border-color: rgba(255, 255, 255, 0.08);
}

.pagination ::v-deep .el-pager li {
  background: transparent;
  color: #dfe7ff;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.pagination ::v-deep .el-pager li.active {
  background: linear-gradient(120deg, #4da1ff, #7c8bff);
  color: #f8fbff;
  border: none;
  box-shadow: 0 10px 20px rgba(67, 137, 255, 0.35);
}

.update ::v-deep .el-form-item__label {
  color: #dfe7ff;
}

.update .el-input__inner,
.update .el-textarea__inner {
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #f3f6ff;
}

.update .el-input__inner::placeholder,
.update .el-textarea__inner::placeholder {
  color: rgba(255, 255, 255, 0.55);
}

:deep(.el-dialog) {
  background: rgba(10, 14, 22, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.12);
}

:deep(.el-dialog__title) {
  color: #f5f7ff;
}

:deep(.el-dialog__body) {
  color: #d8e4ff;
}

:deep(.el-dialog__headerbtn .el-dialog__close) {
  color: rgba(255, 255, 255, 0.6);
}

@media (max-width: 768px) {
  .top {
    padding: 0;
  }

  .top .search-li {
    flex-basis: 100%;
    order: 3;
  }
}
</style>
