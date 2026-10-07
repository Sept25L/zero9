<template>
    <!-- 子菜单 -->
    <el-sub-menu v-if="hasChildren && item.menuType !== 'F'" :index="item.path || item.id">
        <template #title>
            <span>{{ item.menuName }}</span>
        </template>

        <MenuItem v-for="child in visibleChildren" :key="child.id" :item="child" />
    </el-sub-menu>

    <!-- 叶子菜单 -->
    <el-menu-item v-else-if="item.menuType !== 'F'" :index="item.path">
        {{ item.menuName }}
    </el-menu-item>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
    item: Object
})

const visibleChildren = computed(() => {
    return (props.item.children || []).filter(
        child => child.menuType !== 'F'
    )
})

const hasChildren = computed(() => {
    return visibleChildren.value.length > 0
})
</script>