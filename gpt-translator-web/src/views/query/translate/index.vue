<template>
  <div>
    <div class="form">
      <el-card>
        <el-form
          :inline="true"
          class="bg-bg_color w-[99/100] pl-8 pt-4">
          <el-form-item label="项目:">
            <el-select
              v-model="form.projectId"
              filterable
              remote
              reserve-keyword
              placeholder="请选择项目"
              :remote-method="projectsRemoteMethod"
              :loading="loading1"
            >
              <el-option
                v-for="item in options1"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="文件:">
            <el-select
              v-model="form.fileId"
              filterable
              remote
              reserve-keyword
              placeholder="请选择文件"
              :remote-method="fileRemoteMethod"
              :loading="loading2"
            >
              <el-option
                v-for="item in options2"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-form>
        <el-form
          :inline="true"
          class="bg-bg_color w-[99/100] pl-8 pt-4"
        >
          <el-form-item label="原文：" prop="originalText">
            <el-input
              v-model="form.originalText"
              placeholder="输入查询原文"
              clearable
              class="!w-[200px]"
            />
          </el-form-item>
          <el-form-item label="译文：" prop="translationText">
            <el-input
              v-model="form.translationText"
              placeholder="输入查询译文"
              clearable
              class="!w-[200px]"
            />
          </el-form-item>
          <el-form-item label="翻译状态：" prop="isTranslation">
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
          <el-form-item>
            <el-button
              type="warning"
              @click="onReset"
            >
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <el-card>
      <h4 class="table-name">翻译查询:</h4>
      <el-table :data="table.data" style="width: 100%">
        <el-table-column prop="id" label="id" width="100"/>
        <el-table-column prop="originalText" label="原文"/>
        <el-table-column prop="translationText" label="原始译文"/>
        <el-table-column prop="translationText" label="编辑译文">
          <template #default="scope">
            <el-input
              :autosize="{ minRows: 2, maxRows: 4 }"
              v-model="scope.row.translationText"
              type="textarea"
              placeholder="手动编辑译文"
              @change="inputBoxEvent(scope.row)"
            />
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

<script lang="ts" setup>
import {message} from "@/utils/message";
import {translationDataPage, translationUpdate, vueFileSelect, vueProjectsSelect} from '@/api/query'
import {onMounted, reactive, ref} from "vue";
import {useRoute} from "vue-router";

const route = useRoute();

defineOptions({
  // name 作为一种规范最好必须写上并且和路由的name保持一致
  name: "translate"
});

//项目选择器相关
interface ListItem {
  value: string
  label: string
}

// 项目选择器
const options1 = ref<ListItem[]>([])
const value1 = ref<string[]>([])
const loading1 = ref(false)
//文件选择器
const options2 = ref<ListItem[]>([])
const value2 = ref<string[]>([])
const loading2 = ref(false)

//页面列表相关

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
  isTranslation: '',
  projectId: route.query.projectId,
  fileId: route.query.fileId
})

const  onReset = () => {
  form.originalText = ''
  form.translationText = ''
  form.isTranslation = ''
  form.projectId = ''
  form.fileId = ''
  value1.value = []
  value2.value = []
}


const projectsRemoteMethod = (query: string) => {
  if (query) {
    //寻找项目
    vueProjectsSelect({
      projectName: query
    }).then(res => {
      if (res.code === 0) {
        const data = res.data
        options1.value = data.map(item => {
          return {
            value: item.projectId,
            label: item.projectName
          }
        })
      }
    })
  } else {
    options1.value = []
  }
}
const fileRemoteMethod = (query: string) => {
  if (query) {
    //寻找项目
    vueFileSelect({
      projectId: form.projectId,
      fileName: query
    }).then(res => {
      if (res.code === 0) {
        const data = res.data
        options2.value = data.map(item => {
          return {
            value: item.fileId,
            label: item.fileName
          }
        })
      }
    })
  } else {
    options2.value = []
  }
}


const inputBoxEvent = async (ref) => {
  //更新翻译数据
  translationUpdate(ref).then(res => {
    if (res.code === 0) {
      message('修改翻译数据成功', {type: 'success'})
    } else {
      message('修改翻译数据失败', {type: 'error'})
    }
  })
  await getData(currentPage.value, pageSize.value, form)
}

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

onMounted(() => {
  //初始化
  getData(currentPage.value, pageSize.value, form)
})
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
