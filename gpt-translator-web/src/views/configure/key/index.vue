<template>
  <div>
    <div class="form">
      <el-form
        ref="formRef"
        :inline="true"
        :model="addform"
        class="bg-bg_color w-[99/100] pl-8 pt-4"
      >
        <el-form-item label="新增key：" prop="projectName">
          <el-input
            v-model="addform.key"
            placeholder="输入新增的key"
            clearable
            class="!w-[200px]"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="success"
            @click="onAddKey"
          >
            新增
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-card>
      <h4 class="table-name">key 管理:</h4>
      <el-table :data="table.data" style="width: 100%">
        <el-table-column prop="id" label="id" width="50"/>
        <el-table-column prop="openaiKey" label="key"/>
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button type="danger" size="small" @click="onDelete(scope.row.id) "
            >删除
            </el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 20, 50]"
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
import {onMounted, reactive, ref} from "vue";
import {openaiPropertiesCreateKey, openaiPropertiesDelete, openaiPropertiesPage} from "@/api/configure";
import {message} from "@/utils/message";

const table = reactive({
  data: [],
  total: 0
})

const form = reactive({})
const addform = reactive({
  key: ''
})

const currentPage = ref(1)
const pageSize = ref(10)
const small = ref(false)
const background = ref(false)
const disabled = ref(false)

const onDelete = (id) => {
  openaiPropertiesDelete({ids: id}).then(res => {
    if (res.code === 0) {
      message('删除key成功', {type: 'success'})
    }
    getData(currentPage.value, pageSize.value, form)
  })
}

const onAddKey = () => {
  openaiPropertiesCreateKey({key: addform.key}).then(res => {
    if (res.code === 0) {
      message('新增key成功', {type: 'success'})
    }
    getData(currentPage.value, pageSize.value, form)
  })
}

const getData = (pageNum, pageSize, form) =>
  openaiPropertiesPage({pageNum, pageSize, ...form}).then(res => {
    table.data = res.data.records
    table.total = res.data.total
  })

const onSearch = () => {
  getData(currentPage.value, pageSize.value, form)
}

const handleSizeChange = (val: number) => {
  getData(currentPage.value, val, form)

}
const handleCurrentChange = (val: number) => {
  getData(val, pageSize.value, form)
}
onMounted(() => {
  getData(currentPage.value, pageSize.value, form)
})
</script>


<style scoped lang="scss">
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
