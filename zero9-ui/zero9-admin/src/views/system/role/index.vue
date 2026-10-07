<template>
    <el-container>
        <el-form :model="queryParams" ref="queryForm" style="width: 100%;" :inline="true" label-width="68px"
            @submit.prevent>
            <el-form-item label="角色名称" prop="roleName">
                <el-input v-model="queryParams.roleName" placeholder="请输入角色名称" clearable @keyup.enter="initRoleList"
                    style="width: 160px" />
            </el-form-item>
            <el-form-item label="角色标识" prop="roleKey">
                <el-input v-model="queryParams.roleKey" placeholder="请输入角色标识" clearable @keyup.enter="initRoleList"
                    style="width: 160px" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="initRoleList">搜索</el-button>
                <el-button @click="resetinitRoleList">重置</el-button>
            </el-form-item>
        </el-form>
    </el-container>
    <el-container style="padding-bottom: 20px;">
        <el-button type="primary" plain @click="handleSubmitForm('新增角色', 'add')" v-hasPermi="['system:role:add']">
            <SvgIcon name="add" size="16" margin="0 5px 0 0" />新增
        </el-button>
        <el-button type="success" plain :disabled="single" @click="handleSubmitForm('编辑角色', 'edit', ids)"
            v-hasPermi="['system:role:edit']">修改</el-button>
        <el-button type="danger" plain v-hasPermi="['system:role:delete']" @click="handleDelete"
            :disabled="remove">删除</el-button>
        <el-button type="warning" plain>导出</el-button>
    </el-container>
    <el-container>
        <el-table :data="tableData" style="width: 100%; height: 100%;" fit="true" show-overflow-tooltip="true"
            @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column prop="id" label="编号" min-width="100" />
            <el-table-column prop="roleName" label="角色名称" min-width="120" />
            <el-table-column prop="roleKey" label="权限标识" min-width="120" />
            <el-table-column prop="status" label="状态" min-width="150">
                <template #default="{ row }">
                    <el-switch v-model="row.status" active-value="1" active-text="禁用" inactive-value="0"
                        inactive-text="正常" @change="handleStatus(row)" />
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" min-width="150" />
            <el-table-column fixed="right" label="操作" min-width="120">
                <template #default="{ row }">
                    <el-button link type="primary" size="small" @click="handleSubmitForm('编辑角色', 'edit', row.id)"
                        v-if="row.id !== 1" v-hasPermi="['system:role:edit']">
                        修改
                    </el-button>
                    <el-button link type="danger" size="small" v-if="row.id !== 1" v-hasPermi="['system:role:delete']"
                        @click="handleDelete(row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <RoleForm ref="refEl" @success="initRoleList" />
    </el-container>
    <el-pagination v-model:current-page="queryParams.pageNo" v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 30, 40]" :size="size" :disabled="disabled" :background="background"
        layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" />
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoles, removeRole } from '@/api/system/role'
import RoleForm from './RoleForm.vue'

// 变量
const tableData = ref([])
const background = ref(false)
const disabled = ref(false)
const refEl = ref()
const ids = ref([])
const single = ref(true)
const remove = ref(true)
const queryParams = ref({
    pageNo: 1,
    pageSize: 10,
    roleName: undefined,
    roleKey: undefined
})
const total = ref(0)

// 函数

// 根据类型打开表单弹窗
const handleSubmitForm = (title, type, id) => {
    refEl.value.open(title, type, id)
}

// 初始化数据
const initRoleList = async () => {
    const resp = await getRoles(queryParams.value)
    tableData.value = resp.data.records
    total.value = resp.data.total
}

// 重置表单参数
const resetForm = () => {
    queryParams.value.pageNo = 1
    queryParams.value.pageSize = 10
    queryParams.value.roleName = undefined
    queryParams.value.roleKey = undefined

}

// 重新初始化数据
const resetinitRoleList = async () => {
    resetForm()
    const resp = await getRoles(queryParams.value)
    tableData.value = resp.data.records
    total.value = resp.data.total
}

// 每页显示多少条数据
const handleSizeChange = (pageSize) => {
    queryParams.value.pageNo = 1
    queryParams.value.pageSize = pageSize
    initRoleList()
}

// 改变当前页
const handleCurrentChange = (pageNo) => {
    queryParams.value.pageNo = pageNo
    initRoleList()
}

// 勾选列表
const handleSelectionChange = (selection) => {
    ids.value = selection.map(item => item.id)
    single.value = (selection.length != 1 || selection.some(item => item.id === 1))
    remove.value = (selection.length != 1 || selection.some(item => item.id === 1))
}

// 修改角色状态
const handleStatus = (row) => {
    const newStatus = row.status
    const oldStatus = newStatus === 0 ? 1 : 0
    ElMessageBox.confirm(
        `是否${newStatus === 1 ? '禁用' : '启用'} ${row.roleName} 用户？`,
        '系统提示'
    )
        .then(() => {
            // 调接口
            ElMessage.success('操作成功')
        })
        .catch(() => {
            row.status = oldStatus
            ElMessage.info('已取消操作')
        })
}

// 删除角色
const handleDelete = (row) => {
    const roleIds = row.id ? [row.id] : ids.value
    ElMessageBox.confirm(
        `是否删除编号为 ${roleIds.join(',')} 的角色？`,
        '系统提示'
    )
        .then(async () => {
            // 调接口
            const resp = await removeRole(roleIds)
            ElMessage.success(resp.msg)
            initRoleList()
        })
        .catch(() => {
            ElMessage.info('已取消操作')
        })
}


// 组件挂载完成后处理
onMounted(() => {
    initRoleList()
})
</script>

<style lang="scss" scoped>
.el-pagination {
    justify-self: end;
    margin: 20px;
}

.demo-pagination-block+.demo-pagination-block {
    margin-top: 10px;
}

.demo-pagination-block .demonstration {
    margin-bottom: 16px;
}
</style>