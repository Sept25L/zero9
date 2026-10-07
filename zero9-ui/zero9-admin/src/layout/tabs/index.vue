<template>
    <el-tabs v-model="editableTabsValue" type="card" closable @tab-remove="removeTab" @tab-click="handleClick">
        <el-tab-pane v-for="item in editableTabs" :key="item.path" :label="item.title" :name="item.path"
            :closable="item.path !== '/index'" />
    </el-tabs>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { ref, watch, onUnmounted } from 'vue'

const route = useRoute()
const router = useRouter()

const editableTabs = ref(
    JSON.parse(localStorage.getItem('tabs') || '[]')
)

const editableTabsValue = ref('')

const addTab = (route) => {
    const exist = editableTabs.value.find(tab => tab.path === route.path)
    if (!exist) {
        editableTabs.value.push({
            title: route.meta.title,
            name: route.path,
            path: route.path
        })
    }

    editableTabsValue.value = route.path
}

watch(
    () => route.path,
    () => {
        addTab(route)
    },
    { immediate: true }
)

watch(
    editableTabs,
    (val) => {
        localStorage.setItem('tabs', JSON.stringify(val))
    },
    { deep: true }
)

const handleClick = (tab) => {
    router.push(tab.props.name)
}

const removeTab = (targetName) => {
    const tabs = editableTabs.value
    let active = editableTabsValue.value

    if (active === targetName) {
        const index = tabs.findIndex(t => t.path === targetName)
        const next = tabs[index + 1] || tabs[index - 1]

        if (next) {
            active = next.path
            router.push(active)
        }
    }

    editableTabsValue.value = active
    editableTabs.value = tabs.filter(tab => tab.path !== targetName)
}
onUnmounted(() => {
    localStorage.removeItem('tabs')
})
</script>

<style lang="scss" scoped>
.demo-tabs>.el-tabs__content {
    padding: 20px;
    color: #6b778c;
    font-size: 20px;
    font-weight: 600;
}
</style>
