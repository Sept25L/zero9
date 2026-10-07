<template>
    <el-dialog v-model="dialogVisible" :title="dialogTitle" style="width: 600px" :before-close="handleClose">
        <el-form ref="ruleFormRef" :model="form" :rules="rules" label-width="auto">
            <el-row :gutter="20" v-if="form.id == undefined">
                <el-col :span="12">
                    <el-form-item label="用户名" prop="username">
                        <el-input v-model="form.username" placeholder="请输入用户名" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="密码" prop="password">
                        <el-input type="password" v-model="form.password" placeholder="请输入密码" show-password />
                    </el-form-item>
                </el-col>
            </el-row>

            <el-row :gutter="10">
                <el-col :span="12">
                    <el-form-item label="昵称" prop="nickName">
                        <el-input v-model="form.nickName" placeholder="请输入昵称" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="性别">
                        <el-select v-model="form.sex" placeholder="请选择性别">
                            <el-option v-for="(item, index) in sexs" :key="item.index" :label="item.label"
                                :value="item.value" />
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>

            <el-row :gutter="10">
                <el-col :span="12">
                    <el-form-item label="手机号" prop="phonenumber">
                        <el-input v-model="form.phonenumber" placeholder="请输入手机号" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="邮箱">
                        <el-input v-model="form.email" placeholder="请输入邮箱" />
                    </el-form-item>
                </el-col>
            </el-row>

            <el-row :gutter="10">
                <el-col :span="12">
                    <el-form-item label="状态" prop="status">
                        <el-radio-group v-model="form.status">
                            <el-radio value="0">启用</el-radio>
                            <el-radio value="1">禁用</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="角色">
                        <el-select v-model="form.roles" multiple collapse-tags collapse-tags-tooltip
                            :max-collapse-tags="3" placeholder="请选择角色">
                            <el-option v-for="(item, index) in roles" :key="item.index" :label="item.roleName"
                                :value="item.id" />
                        </el-select>
                        <!-- <el-select v-model="form.roles" placeholder="请选择角色" clearable multiple>
                            <template #label="{ label, value }">
                                <span>{{ label }}</span>
                            </template>
<el-option v-for="(item, index) in roles" :key="item.index" :label="item.roleName" :value="item.id" />
</el-select> -->
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item>
                <el-button type="primary" @click="submitForm(ruleFormRef)">确定</el-button>
            </el-form-item>
        </el-form>
    </el-dialog>
</template>

<script setup>
import { reactive, ref, defineExpose, defineEmits } from 'vue'
import { getUser, addUser, editUser } from '@/api/system/user'
import { getCurrentUserRoles } from '@/api/system/role'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['success'])
const dialogVisible = ref(false)
const dialogTitle = ref()
const formType = ref()
// 表单数据
const form = reactive({
    id: undefined,
    username: undefined,
    password: undefined,
    nickName: undefined,
    phonenumber: undefined,
    email: undefined,
    sex: undefined,
    status: '0',
    roles: []
})

const sexs = [
    {
        value: '0',
        label: '男',
    },
    {
        value: '1',
        label: '女',
    },
    {
        value: '2',
        label: '未知',
    }
]

const roles = ref([])

const ruleFormRef = ref()

const validatePhone = (rule, value, callback) => {
    if (!value) {
        callback()
    } else if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('手机号格式不正确'))
    } else {
        callback()
    }
}
const rules = reactive({
    username: [
        { required: true, message: '请输入用户名!', trigger: 'blur' },
    ],
    password: [
        { required: true, message: '请输入密码!', trigger: 'blur' },
    ],
    nickName: [
        { required: true, message: '请输入昵称!', trigger: 'blur', },
    ],
    phonenumber: [
        { validator: validatePhone, trigger: 'blur' }
    ]
})

const initRoleList = async () => {
    const resp = await getCurrentUserRoles()
    roles.value = resp.data
}


const submitForm = async (formEl) => {
    if (!formEl) return

    try {
        await formEl.validate()

        let resp

        if (formType.value === 'add') {
            resp = await addUser(form)
        } else {
            resp = await editUser(form)
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
    initRoleList()
    if (id) {
        const resp = await getUser(id)
        Object.assign(form, resp.data)
    }
}

// 重置表单
const resetForm = () => {
    form.id = undefined
    form.username = undefined
    form.password = undefined
    form.nickName = undefined
    form.phonenumber = undefined
    form.email = undefined
    form.sex = undefined
    form.status = '0'
    form.roles = []
    roles.value = []
    ruleFormRef.value?.resetFields()
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
</style>
