<template>
  <div>
    <el-card>
      <el-tabs v-model="activeName" >
        <el-tab-pane label="JSON文件上传" name="0">
          <UploadFile :title="jsonTitle"/>
        </el-tab-pane>
        <el-tab-pane label="Excel文件上传" name="1">
          <UploadFile :title="excelTitle"/>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import {ref} from 'vue'
import UploadFile from "./UploadFile.vue";

const activeName = ref('0')

const jsonTitle = '点击上传JSON';
const excelTitle = '点击上传Excel(zip压缩包)';

</script>

<style scoped lang="scss">

</style>
