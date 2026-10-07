<template>
    <el-container>
        <el-form :model="queryParams" ref="queryForm" style="width: 100%;" :inline="true" label-width="68px"
            @submit.prevent>
            <el-form-item label="用户名称" prop="username">
                <el-input v-model="queryParams.username" placeholder="请输入用户名称" clearable @keyup.enter="initUserList"
                    style="width: 160px" />
            </el-form-item>
            <el-form-item label="手机号码" prop="phonenumber">
                <el-input v-model="queryParams.phonenumber" placeholder="请输入手机号码" clearable @keyup.enter="initUserList"
                    style="width: 160px" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="initUserList">搜索</el-button>
                <el-button @click="resetInitUserList">重置</el-button>
            </el-form-item>
        </el-form>
    </el-container>
    <el-container style="padding-bottom: 20px;">
        <el-button type="primary" plain @click="handleSubmitForm('新增用户', 'add')" v-hasPermi="['system:user:add']">
            <SvgIcon name="add" size="16" margin="0 5px 0 0" />新增
        </el-button>
        <el-button type="success" plain :disabled="single" @click="handleSubmitForm('编辑用户', 'edit', ids)"
            v-hasPermi="['system:user:edit']">修改</el-button>
        <el-button type="danger" plain v-hasPermi="['system:user:delete']" @click="handleDelete"
            :disabled="remove">删除</el-button>
        <el-button type="warning" plain>导出</el-button>
    </el-container>
    <el-container>
        <el-table :data="tableData" style="width: 100%; height: 100%;" fit="true" show-overflow-tooltip="true"
            @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column prop="username" label="用户名" min-width="120" />
            <el-table-column prop="nickName" label="昵称" min-width="120" />
            <el-table-column prop="sex" label="性别" min-width="120">
                <template #default="{ row }">
                    <span v-if="row.sex === '0' || row.sex === 0">男</span>
                    <span v-else-if="row.sex === '1' || row.sex === 1">女</span>
                    <span v-else>未知</span>
                </template>
            </el-table-column>
            <el-table-column prop="avatar" label="头像" min-width="120">
                <template #default="{ row }">
                    <el-image style="width: 50px; height: 50px" :src="getAvatar(row.avatar)"
                        :preview-src-list="preview(row.avatar)" :initial-index="0" :preview-teleported="true"
                        scale="0.7" :z-index="3000" fit="cover" />
                </template>
            </el-table-column>
            <el-table-column prop="phonenumber" label="手机" min-width="120" />
            <el-table-column prop="email" label="邮箱" min-width="120" />
            <el-table-column prop="status" label="状态" min-width="150">
                <template #default="{ row }">
                    <el-switch v-model="row.status" active-value="1" active-text="禁用" inactive-value="0"
                        inactive-text="正常" @change="handleStatus(row)" />
                </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" min-width="120">
                <template #default="{ row }">
                    <el-button link type="primary" size="small" @click="handleSubmitForm('编辑用户', 'edit', row.id)"
                        v-if="row.id !== 1" v-hasPermi="['system:user:edit']">
                        修改
                    </el-button>
                    <el-button link type="danger" size="small" v-if="row.id !== 1" v-hasPermi="['system:user:delete']"
                        @click="handleDelete(row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <FormData ref="refEl" @success="initUserList" />
    </el-container>
    <el-pagination v-model:current-page="queryParams.pageNo" v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 30, 40]" :size="size" :disabled="disabled" :background="background"
        layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" />
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUsers, updateUserStatus, removeUser } from '@/api/system/user'
import FormData from './UserForm.vue'

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
    username: undefined,
    phonenumber: undefined
})
const total = ref(0)

// 函数

// 根据类型打开表单弹窗
const handleSubmitForm = (title, type, id) => {
    refEl.value.open(title, type, id)
}

// 初始化数据
const initUserList = async () => {
    const resp = await getUsers(queryParams.value)
    tableData.value = resp.data.records
    total.value = resp.data.total
}

// 重置表单参数
const resetForm = () => {
    queryParams.value.pageNo = 1
    queryParams.value.pageSize = 10
    queryParams.value.username = undefined
    queryParams.value.phonenumber = undefined

}

// 重新初始化数据
const resetInitUserList = async () => {
    resetForm()
    const resp = await getUsers(queryParams.value)
    tableData.value = resp.data.records
    total.value = resp.data.total
}

// 每页显示多少条数据
const handleSizeChange = (pageSize) => {
    queryParams.value.pageNo = 1
    queryParams.value.pageSize = pageSize
    initUserList()
}

// 改变当前页
const handleCurrentChange = (pageNo) => {
    queryParams.value.pageNo = pageNo
    initUserList()
}

// 勾选列表
const handleSelectionChange = (selection) => {
    ids.value = selection.map(item => item.id)
    single.value = (selection.length != 1 || selection.some(item => item.id === 1))
    remove.value = (selection.length != 1 || selection.some(item => item.id === 1))
}

// 修改用户状态
const handleStatus = (row) => {
    let text = row.status === "0" ? "启用" : "停用"
    ElMessageBox.confirm(
        '确认要"' + text + '""' + row.username + '"用户吗？',
        '系统提示'
    )
        .then(async () => {
            const resp = await updateUserStatus(row)
            ElMessage.success(resp.msg)
            initUserList()
        })
        .catch(() => {
            ElMessage.info('已取消操作')
            initUserList()
        })
}

// 删除用户
const handleDelete = (row) => {
    const userIds = row.id ? [row.id] : ids.value
    ElMessageBox.confirm(
        `是否删除编号为 ${userIds.join(',')} 的用户？`,
        '系统提示'
    )
        .then(async () => {
            // 调接口
            const resp = await removeUser(userIds)
            ElMessage.success(resp.msg)
            initUserList()
        })
        .catch(() => {
            ElMessage.info('已取消操作')
        })
}

// 处理图片地址
const getAvatar = (avatar) => {
    if (!avatar) return ''

    if (avatar.startsWith('http')) {
        return avatar
    }

    return import.meta.env.VITE_APP_BASE_URL + avatar
}

// 预览图片
const preview = (avatar) => {
    const url = getAvatar(avatar)
    return url ? [url] : []
}

// 组件挂载完成后处理
onMounted(() => {
    initUserList()
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