<template>
    <el-menu active-text-color="#ffd04b" background-color="#545c64" text-color="#fff" router
        :default-active="activeMenu">
        <el-menu-item index="/index">首页</el-menu-item>
        <MenuItem v-for="item in menus" :key="item.id" :item="item" />
    </el-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import useUserStore from '@/stores/userStore'
import MenuItem from './component/MenuItem'

const userStore = useUserStore()
const route = useRoute()

const menus = computed(() => userStore.menus)

// ✅ 动态高亮当前路由
const activeMenu = computed(() => route.path)
</script>