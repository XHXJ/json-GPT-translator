<template>
  <div>
    <el-card>
      <el-button @click="onStart">开始翻译</el-button>
      <el-button @click="onStop">停止翻译</el-button>
    </el-card>
  </div>
</template>
<script setup lang="ts">

import {translationStart, translationStop} from "@/api/translation";

const onStart = () => {
  translationStart().then()
}

const onStop = () => {
  translationStop().then()
}

</script>
<style scoped lang="scss">

</style>
