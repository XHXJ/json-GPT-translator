<template>
  <div>
    <div class="form">
      <el-form
        ref="formRef"
        :inline="true"
        :model="form"
        class="bg-bg_color w-[99/100] pl-8 pt-4"
      >
        <el-form-item label="项目名称：" prop="projectName">
          <el-input
            v-model="form.projectName"
            placeholder="输入查询项目名称"
            clearable
            class="!w-[200px]"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            @click="onSearch"
          >
            搜索
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-card>
      <el-table :data="table.data" style="width: 100%"
                border>
        <el-table-column prop="projectId" label="id" width="50"/>
        <el-table-column prop="projectName" label="项目名称"/>
        <el-table-column prop="projectName" label="导出文件" width="200px">
          <template #default="scope">
            <el-button size="small" @click="exportJsonFile(scope.row.projectId)">导出json</el-button>
            <el-button size="small" @click="exportExcelFile(scope.row.projectId)">导出Excel</el-button>
          </template>
        </el-table-column>
        <el-table-column width="200" label="翻译进度">
          <template #default="scope">
            <el-progress :percentage="calcPercentage(scope.row.notCompleted, scope.row.completed)"/>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :small="small"
          :disabled="disabled"
          :background="background"
          layout="total, sizes, prev, pager, next, jumper"
          :total="table.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import {onMounted, reactive, ref} from "vue";
import {projectsDataPage} from "@/api/query";
import axios from "axios";
import {downloadByData} from "@pureadmin/utils";

defineOptions({
  name: "export"
});

const currentPage = ref(1)
const pageSize = ref(10)
const small = ref(false)
const background = ref(false)
const disabled = ref(false)

const form = reactive({
  projectName: ''
})

const table = reactive({
  data: [],
  total: 0
})

//导出文件
const exportJsonFile = (projectId: number) => {
  axios
    .get(`/api/translation-data/export-json?projects=${projectId}`, {
      responseType: "blob"
    })
    .then((response) => {
      let disposition = response.headers['content-disposition'];
      let filename = 'translate.json'; // 默认文件名

      if (disposition) {
        let dispositionFilename = disposition.split('filename=')[1]?.trim();
        if (dispositionFilename) {
          filename = dispositionFilename;
        }
      }
      downloadByData(response.data, filename);
    });
}
const exportExcelFile = (projectId: number) => {
  axios
    .get(`/api/translation-data/export-excel?projects=${projectId}`, {
      responseType: "blob"
    })
    .then((response) => {
      let disposition = response.headers['content-disposition'];
      let filename = 'translate.json'; // 默认文件名

      if (disposition) {
        let dispositionFilename = disposition.split('filename=')[1]?.trim();
        if (dispositionFilename) {
          filename = dispositionFilename;
        }
      }
      downloadByData(response.data, filename);
    });
}

//计算百分比
const calcPercentage = (notCompleted: number, completed: number) => {
  const total = notCompleted + completed;
  if (total === 0) return 0;
  return (completed / total * 100).toFixed(2);
};


const onSearch = () => {
  getData(currentPage.value, pageSize.value, form)
}

const handleSizeChange = (val: number) => {
  getData(currentPage.value, val, form)

}
const handleCurrentChange = (val: number) => {
  getData(val, pageSize.value, form)
}

const getData = (pageNum, pageSize, form) =>
  projectsDataPage({pageNum, pageSize, ...form}).then(res => {
    table.data = res.data.records
    table.total = res.data.total
  })

onMounted(() => {
  getData(currentPage.value, pageSize.value, form)
})

</script>

<style scoped lang="scss">
.pagination {
  display: flex;
  justify-content: flex-start;
  margin-top: 20px;
}

.form {
  margin-bottom: 20px;
}

.table-name {
  margin-bottom: 20px;
}
</style>
