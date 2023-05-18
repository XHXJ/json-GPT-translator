<template>
  <div>
    <el-card>
      <el-form
        ref="formRef"
        :inline="true"
        :model="form"
        class="bg-bg_color w-[99/100] pl-8 pt-4"
      >
        <el-form-item label="项目名称：" prop="projectName">
          <el-input
            v-model="form.projectName"
            placeholder="输入项目名称"
            clearable
            class="!w-[200px]"
          />
          <el-text style="margin-left: 20px" >(输入项目名称,便于后续管理)</el-text>
        </el-form-item>
      </el-form>
      <el-upload
        v-model:file-list="fileList"
        class="upload-demo"
        :action="uploadAction"
        :data="form"
        drag
        :on-success="handleSuccess"
        :limit="1"
        :on-exceed="handleExceed"
      >
        <el-button type="primary">{{ props.title }}</el-button>
      </el-upload>
    </el-card>
  </div>

</template>

<script setup lang="ts">
import {computed, reactive, ref} from 'vue'
import type {FormRules, UploadProps, UploadUserFile} from 'element-plus'
import {message} from "@/utils/message";

const props = defineProps<{
  title: string
}>()

const form = reactive({
  projectName: '默认项目名称'
})



const fileList = ref<UploadUserFile[]>([
  // {
  //     name: 'element-plus-logo.svg',
  //     url: 'https://element-plus.org/images/element-plus-logo.svg',
  // },
  // {
  //     name: 'element-plus-logo2.svg',
  //     url: 'https://element-plus.org/images/element-plus-logo.svg',
  // },
])

const onSearch = () => {

}

const uploadAction = computed(() => {
  if (props.title === '点击上传JSON') {
    return '/api/translation-data/upload'
  } else if (props.title === '点击上传Excel(zip压缩包)') {
    return '/api/translation-data/upload-excel'
  } else {
    // 默认的 action
    return '/api/translation-data/upload'
  }
})


const handleExceed: UploadProps['onExceed'] = (files, uploadFiles) => {
  message(
    `The limit is 1, you selected ${files.length} files this time, add up to ${
      files.length + uploadFiles.length
    } totally`
    , {type: 'warning'})
}
const handleSuccess: UploadProps['onSuccess'] = (response: any) => {
  console.log(response)
  if (response.code === 0) {
    message(`新增待翻译数据 :${response.data}条`, {type: 'success'})
  } else {
    message('上传失败', {type: 'error'})
  }
  fileList.value = []
}

</script>

<style scoped lang="scss">

</style>
