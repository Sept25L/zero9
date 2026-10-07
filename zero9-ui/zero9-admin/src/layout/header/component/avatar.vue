<template>
    <div class="flex-between">
        <el-text class="mx-1">您好：{{ userStore.nickName }}</el-text>
        <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
                <el-avatar :src="userStore.getAvatar" />
            </span>
            <template #dropdown>
                <el-dropdown-menu>
                    <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                    <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
            </template>
        </el-dropdown>
    </div>
</template>

<script setup>
import { ArrowRight } from '@element-plus/icons-vue'
import useUserStore from '@/stores/userStore'
import { ElMessageBox, ElMessage } from 'element-plus'
import router from '@/router'


// 实例化用户状态管理
const userStore = useUserStore()

// 菜单项选择函数
const handleCommand = (command) => {
    if (command === 'logout') {
        ElMessageBox.confirm('确定要退出登录吗？', '提示', {
            type: 'warning'
        })
            .then(() => {
                userStore.logout().then(() => {
                    router.replace('/login')
                    ElMessage.success('已退出登录')
                })
            }).catch(() => { })
    } else if (command === 'profile') {
        router.push("/userinfo")
    }
}
</script>

<style lang="scss" scoped>
.el-dropdown-link {
    cursor: pointer;
    color: var(--el-color-primary);
    outline: none !important;
    display: flex;
    align-items: center;
}
</style>