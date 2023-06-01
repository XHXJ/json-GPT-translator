<template>
  <div>
    <div class="form">
      <el-card>
        <el-form
          :inline="true"
          class="bg-bg_color w-[99/100] pl-9 pt-4">
          <el-form-item label="项目:">
            <el-select
              v-model="form.projectId"
              filterable
              remote
              reserve-keyword
              placeholder="请输入项目名"
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
              placeholder="请选输入文件名"
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
          class="bg-bg_color w-[99/100] pl-10 pt-4"
        >

          <el-form-item label="id：" prop="id">
            <el-input
              v-model="form.id"
              placeholder="输入查询id"
              clearable
              class="!w-[200px]"
            />
          </el-form-item>
          <el-form-item label="原文正则表达式：" prop="originalRegular">
            <el-input
              v-model="form.originalRegular"
              placeholder="输入正则表达式"
              clearable
              class="!w-[200px]"
            />
          </el-form-item>
          <el-form-item label="译文正则表达式：" prop="translationRegular">
            <el-input
              v-model="form.translationRegular"
              placeholder="输入正则表达式"
              clearable
              class="!w-[200px]"
            />
          </el-form-item>
        </el-form>
        <el-form
          :inline="true"
          class="bg-bg_color w-[99/100] pl-6 pt-4"
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
          <el-form-item>
            <el-button
              type="success"
              @click="translateOne"
            >
              批量单条翻译
            </el-button>
          </el-form-item>
          <el-form-item>
            <el-popconfirm @confirm="onDeleteSelectionChangeForm"
                           :title="'删除 ' + selectionChangeForm.value.length + ' 条数据?'">
              <template #reference>
                <el-button type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <el-card>
      <h4 class="table-name">翻译查询:</h4>
      <el-table :data="table.data" style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55"/>
        <el-table-column prop="id" label="id" width="100">
          <template #default="scope">
            <div v-if="scope.row.id  == form.id" style="color: red">
              {{ scope.row.id }}
            </div>
            <div v-else>
              {{ scope.row.id }}
            </div>
          </template>
        </el-table-column>
        >
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
import {
  postTranslateOne,
  translationDataPage,
  translationDelete,
  translationUpdate,
  vueFileSelect,
  vueProjectsSelect
} from '@/api/query'
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
  originalRegular: '',
  translationRegular: '',
  id: '',
  regular: '',
  originalText: '',
  translationText: '',
  isTranslation: '',
  projectId: route.query.projectId,
  fileId: route.query.fileId
})

const onReset = () => {
  form.originalRegular = ''
  form.translationRegular = ''
  form.originalText = ''
  form.translationText = ''
  form.isTranslation = ''
  form.projectId = ''
  form.fileId = ''
  form.id = ''
  value1.value = []
  value2.value = []
}
const selectionChangeForm = reactive({
  value: []
})

const translateOne = () => {
  if (selectionChangeForm.value.length === 0) {
    message('请选择要翻译的数据', {type: 'warning'})
    return
  }
  const ids = selectionChangeForm.value.map(item => item.id)
  postTranslateOne(ids).then(res => {
    if (res.code === 0) {
      message(res.data, {type: 'success'})
      onSearch()
    }
  })
}

//多选
const handleSelectionChange = (val: object[]) => {
  selectionChangeForm.value = val
}

const onDeleteSelectionChangeForm = () => {
  console.log(selectionChangeForm.value)
  if (selectionChangeForm.value.length === 0) {
    message('请选择要删除的数据', {type: 'warning'})
    return
  }
  const ids = selectionChangeForm.value.map(item => item.id)
  translationDelete(ids).then(res => {
    if (res.code === 0) {
      message('删除成功', {type: 'success'})
      onSearch()
    }
  })
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
