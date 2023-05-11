<template>
    <el-menu
        :default-active="activeIndex"
        mode="horizontal"
        @select="handleSelect"
    >

        <el-text class="title" size="large">
            <router-link to="/">GPT-translator</router-link>
            <el-text tag="sub" class="title_subtitle" size="small">
                V0.1.0
            </el-text>
        </el-text>
        <el-menu-item index="1">
            <router-link to="/upload-file-start">上传文件</router-link>
        </el-menu-item>
        <el-sub-menu index="2">
            <template #title>翻译配置</template>
            <router-link to="/configure/key">
                <el-menu-item index="2-1">

                    <el-text size="small">
                        openai-key设置
                    </el-text>
                </el-menu-item>
            </router-link>

            <router-link to="/configure/prompt">
                <el-menu-item index="2-2">

                    <el-text size="small">
                        prompt设置
                    </el-text>
                </el-menu-item>
            </router-link>


        </el-sub-menu>
        <el-menu-item index="3">
            <router-link to="/query">查询翻译</router-link>
        </el-menu-item>
        <el-menu-item index="4">
            <router-link to="/export">导出</router-link>
        </el-menu-item>
    </el-menu>


</template>

<script lang="ts" setup>
import {ref} from 'vue'

const activeIndex = ref('1')
const handleSelect = (key: string, keyPath: string[]) => {
    console.log(key, keyPath)
}
</script>

<style scoped lang="css">
.title {
    margin-right: 30px;
    margin-left: 30px;

    .title_subtitle {
        color: #909399;
    }
}

a {
    text-decoration: none;
    color: #909399
}

</style>
