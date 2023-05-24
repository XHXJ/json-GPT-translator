
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
      <h4 class="table-name">项目查询:</h4>
      <el-table :data="table.data" style="width: 100%"
                border @expandChange="expandChange">
        <!--获取文件信息-->
        <el-table-column type="expand">
          <template #default="props">
              <div style="padding: 10px 30px 10px 30px">
                <h3>文件列表:</h3>
                <el-table :data="childrenTable.get(props.row.projectId)" :border="false">
                  <el-table-column label="文件名" prop="fileName"/>
                  <el-table-column label="操作" width="200px">
                    <template #default="scope">
                      <el-button size="small" @click="queryFile(scope.row.projectId, scope.row.fileId)">
                        查看翻译
                      </el-button>
                      <el-popconfirm @confirm="onDeleteFile(scope.row.fileId)"
                                     :title="'是否删除 ' + scope.row.fileName + '?'">
                        <template #reference>
                          <el-button size="small" type="danger">删除</el-button>
                        </template>
                      </el-popconfirm>
                    </template>
                  </el-table-column>
                  <el-table-column width="200" label="翻译进度">
                    <template #default="scope">
                      <el-progress :percentage="calcPercentage(scope.row.notCompleted, scope.row.completed)"/>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
          </template>
        </el-table-column>
        <el-table-column prop="projectId" label="id" width="50px"/>
        <el-table-column prop="projectName" label="项目名称"/>
        <el-table-column label="操作" width="75px">
          <template #default="scope">
            <el-popconfirm @confirm="onDeleteProject(scope.row.projectId)"
                           :title="'是否删除 ' + scope.row.projectName + '?'">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
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
import {deleteFile, deleteProjects, fileDataList, projectsDataPage} from "@/api/query";
import {useRouter} from "vue-router";
import {message} from "@/utils/message";

defineOptions({
  // name 作为一种规范最好必须写上并且和路由的name保持一致
  name: "project"
});

const currentPage = ref(1)
const pageSize = ref(10)
const small = ref(false)
const background = ref(false)
const disabled = ref(false)

const router = useRouter()

const table = reactive({
  data: [],
  total: 0
})

const form = reactive({
  projectName: ''
})
const getData = (pageNum, pageSize, form) =>
  projectsDataPage({pageNum, pageSize, ...form}).then(res => {
    table.data = res.data.records
    table.total = res.data.total
  })

const childrenTable = ref(new Map())


const expandChange = async (row, expandedRows) => {
  if (expandedRows.length > 0) {
    await fileDataList({projectId: row.projectId}).then(res => {
      if (res.code === 0) {
        childrenTable.value.set(row.projectId, res.data)
      }
    })
  }
}
const onSearch = () => {
  getData(currentPage.value, pageSize.value, form)
}

const handleSizeChange = (val: number) => {
  getData(currentPage.value, val, form)

}
const handleCurrentChange = (val: number) => {
  getData(val, pageSize.value, form)
}
//查看翻译跳转查询翻译页面
const queryFile = (projectId: number, fileId: number) => {
  // 获取路由器实例
  router.push({
    name: "translate",
    query: {
      projectId: projectId,
      fileId: fileId
    }
  });

}
//计算百分比
const calcPercentage = (notCompleted: number, completed: number) => {
  const total = notCompleted + completed;
  if (total === 0) return 0;
  return (completed / total * 100).toFixed(2);
};

//删除项目
const onDeleteProject = (projectId: number) => {
  deleteProjects({id: projectId}).then(res => {
    if (res.code === 0) {
      message('删除项目成功', {type: 'success'})
      getData(currentPage.value, pageSize.value, form)
    } else {
      message(res.msg, {type: 'error'})
    }
  })
}
//删除文件
const onDeleteFile = (fileId: number) => {
  deleteFile({id: fileId}).then(res => {
    if (res.code === 0) {
      message('删除文件成功', {type: 'success'})
      getData(currentPage.value, pageSize.value, form)
    } else {
      message(res.msg, {type: 'error'})
    }
  })
}

onMounted(() => {
//初始化
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
