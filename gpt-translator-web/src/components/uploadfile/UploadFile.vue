<template>
    <el-upload
        v-model:file-list="fileList"
        class="upload-demo"
        action="https://run.mocky.io/v3/6db66b93-1638-4137-a5e4-932606b41686"
        drag
        :on-preview="handlePreview"
        :on-remove="handleRemove"
        :before-remove="beforeRemove"
        :limit="1"
        :on-exceed="handleExceed"
    >
        <el-button type="primary">{{ title }}</el-button>
    </el-upload>
</template>

<script setup lang="ts">
import {ref} from 'vue'
import type {UploadProps, UploadUserFile} from 'element-plus'
import {ElMessage, ElMessageBox} from 'element-plus'

defineProps<{
    title: string,
}>()

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

const handleRemove: UploadProps['onRemove'] = (file, uploadFiles) => {
    console.log(file, uploadFiles)
}

const handlePreview: UploadProps['onPreview'] = (uploadFile) => {
    console.log(uploadFile)
}

const handleExceed: UploadProps['onExceed'] = (files, uploadFiles) => {
    ElMessage.warning(
        `The limit is 1, you selected ${files.length} files this time, add up to ${
            files.length + uploadFiles.length
        } totally`
    )
}

const beforeRemove: UploadProps['beforeRemove'] = (uploadFile, uploadFiles) => {
    return ElMessageBox.confirm(
        `Cancel the transfer of ${uploadFile.name} ?`
    ).then(
        () => true,
        () => false
    )
}
</script>

<style scoped lang="scss">

</style>
