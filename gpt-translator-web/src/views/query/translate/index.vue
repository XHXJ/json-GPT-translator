<template>
  <div>
    <div class="form">
      <el-form
        ref="formRef"
        :inline="true"
        :model="table"
        class="bg-bg_color w-[99/100] pl-8 pt-4"
      >
        <el-form-item label="原文：" prop="name">
          <el-input
            v-model="form.originalText"
            placeholder="输入查询原文"
            clearable
            class="!w-[200px]"
          />
        </el-form-item>
        <el-form-item label="译文：" prop="code">
          <el-input
            v-model="form.translationText"
            placeholder="输入查询译文"
            clearable
            class="!w-[200px]"
          />
        </el-form-item>
        <el-form-item label="状态：" prop="status">
          <el-select
            v-model="form.isTranslation"
            placeholder="请选择状态"
            clearable
            class="!w-[180px]"
          >
            <el-option label="已翻译" value="true"/>
            <el-option label="未翻译" value="false"/>
          </el-select>
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
      <h4 class="table-name">翻译查询:</h4>
      <el-table :data="table.data" style="width: 100%">
        <el-table-column prop="id" label="id" width="100"/>
        <el-table-column prop="originalText" label="原文"/>
        <el-table-column prop="translationText" label="译文"/>
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

<script lang="ts" setup>
defineOptions({
  // name 作为一种规范最好必须写上并且和路由的name保持一致
  name: "translate"
});
import {translationDataPage} from '@/api/query'
import {reactive, ref} from "vue";


const currentPage = ref(1)
const pageSize = ref(10)
const small = ref(false)
const background = ref(false)
const disabled = ref(false)

const table = reactive({
  data: [],
  total: 0
})
const form = reactive({
  originalText: '',
  translationText: '',
  isTranslation: ''
})
//调用api
const getData = (pageNum, pageSize, form) =>
  translationDataPage({pageNum, pageSize, ...form}).then(res => {
    table.data = res.data.records
    table.total = res.data.total
  })

const handleSizeChange = (val: number) => {
  getData(currentPage.value, val, form)

}
const handleCurrentChange = (val: number) => {
  getData(val, pageSize.value, form)
}

const onSearch = () => {
  getData(currentPage.value, pageSize.value, form)
}
//初始化
getData(currentPage.value, pageSize.value, form)
</script>

<style>
.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.form {
  margin-bottom: 20px;
}

.table-name {
  margin-bottom: 20px;
}

</style>
