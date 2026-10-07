<template>
    <el-breadcrumb :separator-icon="ArrowRight">
        <el-breadcrumb-item v-for="(item, index) in breadcrumbList" :to="item.path">
            {{ item.meta.title }}
        </el-breadcrumb-item>
    </el-breadcrumb>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { onMounted, ref, watch } from 'vue'

const route = useRoute()

const breadcrumbList = ref([])

const isDashboard = (route) => {
    const name = route && route.name
    if (name) {
        return name.trim() === 'Index'
    }
    return false
}


const initBreadcrumbList = () => {
    const matched = route.matched.filter(item => item.meta && item.meta.title)
    if (!isDashboard(matched[0])) {
        matched.unshift({
            path: '/index',
            meta: { title: '首页' }
        })
    }
    breadcrumbList.value = matched
}

onMounted(() => {
    initBreadcrumbList()
})

watch(
    () => route.path,
    () => {
        initBreadcrumbList()
    }
)
</script>

<style lang="scss" scoped></style>