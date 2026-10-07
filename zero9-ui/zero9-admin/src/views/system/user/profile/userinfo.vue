<template>
    <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="10" :lg="8" :xl="4">
            <el-card style="max-width: 100%">
                <template #header>
                    <div class="card-header">
                        <span>基本资料</span>
                    </div>
                </template>
                <UserAvatar v-model:avatar="user.avatar" />
                <template #footer>
                    <el-descriptions column="1" label-width="30%" border>
                        <el-descriptions-item label="用户名">{{ user.username }}</el-descriptions-item>
                        <el-descriptions-item label="昵称">{{ user.nickName }}</el-descriptions-item>
                        <el-descriptions-item label="性别">{{ user.sex ? '男' : '女' }}</el-descriptions-item>
                        <el-descriptions-item label="手机">{{ user.phonenumber }}</el-descriptions-item>
                        <el-descriptions-item label="邮箱">{{ user.email }}</el-descriptions-item>
                    </el-descriptions>
                </template>
            </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="14" :lg="16" :xl="20">
            <el-card style="max-width: 100%">
                <template #header>
                    <div class="card-header">
                        <span>基本资料</span>
                    </div>
                </template>
                <el-tabs type="border-card">
                    <el-tab-pane label="基本资料">
                        <el-form ref="infoEl" :model="infoForm" :rules="infoRules" label-width="auto"
                            style="max-width: 600px">
                            <el-form-item label="用户名" prop="username">
                                <el-input v-model="infoForm.username" />
                            </el-form-item>
                            <el-form-item label="昵称" prop="nickName">
                                <el-input v-model="infoForm.nickName" />
                            </el-form-item>
                            <el-form-item label="手机号" prop="phonenumber">
                                <el-input v-model="infoForm.phonenumber" />
                            </el-form-item>
                            <el-form-item label="邮箱" prop="email">
                                <el-input v-model="infoForm.email" />
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="updateUser(infoEl)">保存</el-button>
                            </el-form-item>
                        </el-form>
                    </el-tab-pane>
                    <el-tab-pane label="修改密码">
                        <el-form ref="pwdEl" :model="pwdForm" :rules="pwdRules" label-width="auto"
                            style="max-width: 600px">
                            <el-form-item label="旧密码" prop="oldPassWord">
                                <el-input v-model="pwdForm.oldPassWord" />
                            </el-form-item>
                            <el-form-item label="新密码" prop="newPassWord">
                                <el-input v-model="pwdForm.newPassWord" />
                            </el-form-item>
                            <el-form-item label="确认新密码" prop="confirmPassWord">
                                <el-input v-model="pwdForm.confirmPassWord" />
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="updatePwd(pwdEl)">保存</el-button>
                            </el-form-item>
                        </el-form>
                    </el-tab-pane>
                </el-tabs>
            </el-card>
        </el-col>
    </el-row>
</template>

<script setup>
import { onMounted, ref, reactive } from 'vue'
import UserAvatar from './UserAvatar.vue'
import useUserStore from '@/stores/userStore'
import * as userApi from '@/api/system/user'
import { updateInfo, resetPwd } from '@/api/system/user'
import { ElMessage } from 'element-plus'


const userStore = useUserStore()
const user = ref({})
const infoForm = reactive({
    id: undefined,
    username: undefined,
    nickName: undefined,
    phonenumber: undefined,
    email: undefined
})
const pwdForm = reactive({
    id: undefined,
    oldPassWord: undefined,
    newPassWord: undefined,
    confirmPassWord: undefined
})

const infoEl = ref()
const pwdEl = ref()

const validatePhone = (rule, value, callback) => {
    if (!value) {
        callback(new Error('请输入手机号'))
    } else if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('手机号格式不正确'))
    } else {
        callback()
    }
}

const validateConfirmPassword = (rule, value, callback) => {
    if (!value) {
        callback(new Error('请确认新密码'))
    } else if (value !== pwdForm.newPassWord) {
        callback(new Error('两次输入的新密码不一致'))
    } else {
        callback()
    }
}

const infoRules = reactive({
    username: [
        { required: true, message: '请输入用户名!', trigger: 'blur' },
    ],
    nickName: [
        { required: true, message: '请输入昵称!', trigger: 'blur', },
    ],
    phonenumber: [
        { validator: validatePhone, required: true, trigger: 'blur' }
    ],
    email: [
        { type: 'email', required: true, message: '请输入正确的邮箱地址', trigger: 'blur' }
    ]
})

const pwdRules = reactive({
    oldPassWord: [
        { required: true, message: '请输入旧密码', trigger: 'blur' }
    ],

    newPassWord: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, message: '新密码长度不能少于 6 位', trigger: 'blur' }
    ],

    confirmPassWord: [
        { required: true, message: '请确认新密码', trigger: 'blur' },
        { validator: validateConfirmPassword, trigger: 'blur' }
    ]
})



const initUserInfo = async () => {
    const resp = await userApi.getUser(userStore.id)
    user.value = resp.data
    Object.keys(infoForm).forEach((key) => {
        infoForm[key] = user.value[key]
    })
}

const updateUser = async (formEl) => {
    if (!formEl) return

    try {
        await formEl.validate()
        console.log(infoForm)
        const resp = await updateInfo(infoForm)
        initUserInfo()
        ElMessage.success(resp.msg)
    } catch (error) {
        console.error(error)
    }
}

const updatePwd = async (formEl) => {
    if (!formEl) return

    try {

        await formEl.validate()
        pwdForm.id = user.value.id
        console.log(pwdForm)
        const resp = await resetPwd(pwdForm)

        ElMessage.success(resp.msg)
    } catch (error) {
        console.error(error)
    }
}


onMounted(() => {
    initUserInfo()
})
</script>

<style lang="scss" scoped>
.avatar-uploader {
    display: flex;
    justify-content: center;
    align-items: center;

    .el-upload {
        border: 1px dashed var(--el-border-color);
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        transition: var(--el-transition-duration-fast);
    }
}

.avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 75px;
    height: 75px;
    text-align: center;
}

@media screen {}
</style>