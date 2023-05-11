<template>
    <el-row class="title" justify="space-between">
        <el-col :span="4" class="text">
            <el-text size="large">openai key 管理 :</el-text>
        </el-col>
        <el-col :span="2">
            <el-button class="operation_button" @click="visible = true">新增</el-button>
        </el-col>
    </el-row>

    <el-row>
        <el-table :data="tableData" style="width: 100%">
            <el-table-column prop="key" label="key"/>
            <el-table-column fixed="right" width="120">
                <template #default="scope">
                    <el-button link type="primary" size="small" @click="deletes(scope.row.key)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-row>

    <el-dialog v-model="visible" :show-close="false">
        <template #header="{ close, titleId, titleClass }">
            <h2 class="pop_notification" :id="titleId" :class="titleClass">新增key :</h2>
            <el-input v-model="input" placeholder="输入您的 openai key"/>
        </template>
        <el-row justify="end">
            <el-col :span="5">
                <el-button class="operation_button" type="success" @click="add">
                    <el-icon class="el-icon--left">
                        <CircleCloseFilled/>
                    </el-icon>
                    新增
                </el-button>
            </el-col>
            <el-col :span="5">
                <el-button class="operation_button" type="danger" @click="visible = false">
                    <el-icon class="el-icon--left">
                        <CircleCloseFilled/>
                    </el-icon>
                    关闭
                </el-button>
            </el-col>
        </el-row>

    </el-dialog>

</template>

<script lang="ts" setup>
import {CircleCloseFilled} from '@element-plus/icons-vue'

import {ref} from "vue";

const input = ref('')
const visible = ref(false)
const handleClick = () => {
    console.log('click')
}

const tableData = [
    {
        id: 1,
        key: 'CA 90036',
    },
    {
        id: 2,
        key: 'Office',
    },
    {
        id: 3,
        key: 'Office',
    },
    {
        id: 4,
        key: 'Office',
    },
]

const add = () => {
    visible.value = false
    console.log(input.value)
    input.value = ''
}
const deletes = (row) => {
    console.log('key:', row)
}
</script>


<style scoped lang="scss">
.title {
    .text {
        padding-top: 13px;
    }

    margin-bottom: 10px;
}

.pop_notification {
    margin-bottom: 30px;
}

.operation_button {
    margin-top: 10px;
    margin-bottom: 10px;
}

</style>
