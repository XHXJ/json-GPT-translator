<script setup>
import NavigationBar from "./components/navigationbar/NavigationBar.vue";
</script>

<template>
    <div class="common-layout">
        <el-container>
            <el-header>
                <NavigationBar></NavigationBar>
            </el-header>
            <el-main>
                <el-card class="card">
                    <router-view></router-view>
                </el-card>
            </el-main>
        </el-container>
    </div>
</template>

<style scoped lang="scss">
.card{
    margin: 10px 4rem;
}
.breadcrumb{
    margin-bottom: 35px;
}
</style>
