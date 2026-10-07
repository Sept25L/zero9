<template>
    <el-dialog v-model="dialogVisible" :title="dialogTitle" style="width: 600px" :before-close="handleClose">
        <el-form ref="formEl" :model="form" :rules="rules" label-width="auto">
            <el-form-item label="上级菜单" prop="parentId">
                <el-tree-select v-model="form.parentId" :data="menus" :props="{
                    value: 'id',
                    label: 'menuName',
                    children: 'children'
                }" filterable clearable check-strictly :render-after-expand="false" placeholder="请选择上级菜单" />
            </el-form-item>
            <el-form-item label="菜单类型" prop="menuType">
                <el-radio-group v-model="form.menuType" :options="types" @change="handleMenuType" />
            </el-form-item>
            <el-row :gutter="20">
                <el-col :span="12" v-if="form.menuType != 'F'">
                    <el-form-item label="菜单图标" prop="icon">
                        <el-input v-model="form.icon" placeholder="请选择菜单图标" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="显示排序" prop="sort">
                        <el-input-number v-model="form.sort" min="0" controls-position="right" />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="菜单名称" prop="menuName">
                        <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
                    </el-form-item>
                </el-col>
                <el-col :span="12" v-if="form.menuType == 'C'">
                    <el-form-item label="路由名称">
                        <el-input v-model="form.routerName" placeholder="请输入路由名称" />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="12" v-if="form.menuType != 'F'">
                    <el-form-item label="路由地址" prop="path">
                        <el-input v-model="form.path" placeholder="请输入路由地址" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="菜单状态">
                        <el-radio-group v-model="form.status" :options="statusis" @change="handleMenuType" />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="12" v-if="form.menuType == 'C'">
                    <el-form-item label="组件路径">
                        <el-input v-model="form.component" placeholder="请输入组件路径" />
                    </el-form-item>
                </el-col>
                <el-col :span="12" v-if="form.menuType != 'M'">
                    <el-form-item label="权限标识">
                        <el-input v-model="form.perms" placeholder="请输入权限标识" />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="12" v-if="form.menuType == 'C'">
                    <el-form-item label="路由参数">
                        <el-input v-model="form.query" placeholder="请输入路由参数" />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item :label-width="0" class="dialog-footer">
                <el-button type="primary" @click="submitForm(formEl)">
                    确定
                </el-button>

                <el-button @click="handleClose">
                    取消
                </el-button>
            </el-form-item>
        </el-form>
    </el-dialog>
</template>

<script setup>
import { reactive, ref, defineExpose, defineEmits } from 'vue'
import { getMenu, getMenus, addMenu, editMenu } from '@/api/system/menu'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['success'])
const dialogVisible = ref(false)
const dialogTitle = ref()
const formType = ref()
// 表单数据
const form = reactive({
    id: undefined,
    parentId: 0,
    menuName: undefined,
    menuType: 'M',
    icon: undefined,
    routerName: undefined,
    status: '0',
    path: undefined,
    component: undefined,
    perms: undefined,
    query: undefined,
})

const menus = ref([])

const types = [
    {
        value: 'M',
        label: '目录',
    },
    {
        value: 'C',
        label: '菜单',
    },
    {
        value: 'F',
        label: '按钮',
    }
]

const statusis = [
    {
        value: '0',
        label: '正常'
    },
    {
        value: '1',
        label: '禁用'
    },
]

const formEl = ref()

const rules = reactive({
    parentId: [
        { required: true, message: '请选择上级菜单!', trigger: 'blur' },
    ],
    menuName: [
        { required: true, message: '请输入菜单名称!', trigger: 'blur' },
    ],
    menuType: [
        { required: true, message: '请选择菜单类型!', trigger: 'blur' },
    ],
})

const initMenuList = async () => {
    const resp = await getMenus()
    menus.value = [
        {
            id: 0,
            menuName: '主类目',
            children: resp.data
        }
    ]
}

const handleMenuType = (value) => {
    console.log(value)
}

const submitForm = async (formEl) => {
    if (!formEl) return

    try {
        await formEl.validate()

        let resp

        if (formType.value === 'add') {
            console.log(form)
            resp = await addMenu(form)
        } else {
            resp = await editMenu(form)
        }

        ElMessage.success(resp.msg)
        handleClose()
        emit('success')
    } catch (error) {
        console.error(error)
    }
}

/**
 * 打开表单
 * @param type 对话框类型-edit | add
 * @param id 用户id
 */
const open = async (title, type, id) => {
    dialogVisible.value = true
    dialogTitle.value = title || '未知操作'
    formType.value = type
    resetForm()
    await initMenuList()
    if (id) {
        const resp = await getMenu(id)
        Object.assign(form, resp.data)
    }
}

// 重置表单
const resetForm = () => {
    form.id = undefined
    form.parentId = 0
    form.menuName = undefined
    form.menuType = 'M'
    form.icon = undefined
    form.routerName = undefined
    form.status = '0'
    form.path = undefined
    form.component = undefined
    form.perms = undefined
    form.query = undefined

    formEl.value?.resetFields()
}

// 关闭表单
const handleClose = () => {
    dialogVisible.value = false
    resetForm()
}

defineExpose({
    open
})
</script>
<style lang="scss" scoped>
.el-input {
    width: 240px;
}

.dialog-footer {
    :deep(.el-form-item__content) {
        width: 100%;
        justify-content: flex-end;
    }
}
</style>
