<template>
    <el-container>
        <el-form :model="queryParams" ref="queryForm" style="width: 100%;" :inline="true" label-width="68px"
            @submit.prevent>
            <el-form-item label="菜单名称" prop="menuName">
                <el-input v-model="queryParams.menuName" placeholder="请输入菜单名称" clearable @keyup.enter="initMenuList"
                    style="width: 160px" />
            </el-form-item>
            <el-form-item label="菜单状态" prop="status">
                <el-select v-model="queryParams.status" placeholder="菜单状态" style="width: 160px">
                    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="initMenuList">搜索</el-button>
                <el-button @click="resetInitMenuList">重置</el-button>
            </el-form-item>
        </el-form>
    </el-container>
    <el-container style="padding-bottom: 20px;">
        <el-button type="primary" plain @click="handleSubmitForm('新增菜单', 'add')" v-hasPermi="['system:menu:add']">
            <SvgIcon name="add" size="16" margin="0 5px 0 0" />新增
        </el-button>
    </el-container>
    <el-container>
        <el-table :data="tableData" style="width: 100%; height: 100%;" fit="true" show-overflow-tooltip="true"
            row-key="id">
            <!-- <el-table-column prop="id" label="编号" min-width="100" /> -->
            <el-table-column prop="menuName" label="菜单名称" min-width="180" />
            <el-table-column prop="menuType" label="类型" min-width="90">
                <template #default="{ row }">
                    <el-tag v-if="row.menuType === 'M'" type="primary"> 目录 </el-tag>
                    <el-tag v-else-if="row.menuType === 'C'" type="success"> 菜单 </el-tag>
                    <el-tag v-else-if="row.menuType === 'F'" type="warning"> 按钮 </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="sort" label="排序" min-width="60" />
            <el-table-column prop="perms" label="权限标识" min-width="180" />
            <el-table-column prop="component" label="组件路径" min-width="180" />
            <el-table-column prop="status" label="状态" min-width="90">
                <template #default="{ row }">
                    <el-text v-if="row.status === '0'" type="success"> 正常 </el-text>
                    <el-text v-else-if="row.status === '1'" type="danger"> 禁用 </el-text>
                </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" min-width="120">
                <template #default="{ row }">
                    <el-button link type="primary" size="small" @click="handleSubmitForm('编辑菜单', 'edit', row.id)"
                        v-if="row.id !== 1" v-hasPermi="['system:menu:edit']">
                        修改
                    </el-button>
                    <el-button link type="danger" size="small" v-if="row.id !== 1" v-hasPermi="['system:menu:delete']"
                        @click="handleDelete(row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <MenuForm ref="refEl" @success="initMenuList" />
    </el-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMenus, removeMenu } from '@/api/system/menu'
import MenuForm from './MenuForm.vue'

// 变量
const tableData = ref([])
const refEl = ref()
const single = ref(true)
const remove = ref(true)
const queryParams = ref({
    menuName: undefined,
    status: undefined
})
const total = ref(0)
const options = [
    {
        value: '0',
        label: '正常',
    },
    {
        value: '1',
        label: '禁用',
    },
]
// 函数

// 根据类型打开表单弹窗
const handleSubmitForm = (title, type, id) => {
    refEl.value.open(title, type, id)
}

// 初始化数据
const initMenuList = async () => {
    const resp = await getMenus(queryParams.value)
    tableData.value = resp.data
}

// 重置表单参数
const resetForm = () => {
    queryParams.value.menuName = undefined
    queryParams.value.status = undefined

}

// 重新初始化数据
const resetInitMenuList = () => {
    resetForm()
    initMenuList()
}

// 删除菜单
const handleDelete = (row) => {
    ElMessageBox.confirm(
        `是否删除编号为 ${row.id} 的菜单？`,
        '系统提示'
    )
        .then(async () => {
            // 调接口
            const resp = await removeMenu(row.id)
            ElMessage.success(resp.msg)
            initMenuList()
        })
        .catch(() => {
            ElMessage.info('已取消操作')
        })
}


// 组件挂载完成后处理
onMounted(() => {
    initMenuList()
})
</script>

<style lang="scss" scoped></style>