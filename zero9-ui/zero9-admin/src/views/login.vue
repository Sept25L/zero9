<!-- /views/login.vue -->
<template>
    <div class="login-wrap flex-center">
        <div class="container flex-center flex-col">
            <el-form ref="ruleFormRef" :rules="rules" :model="form" style="min-width: 300px">
                <el-form-item>
                    <el-input v-model.trim="form.username" placeholder="请输入账号" />
                </el-form-item>
                <el-form-item prop="password">
                    <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" style="width: 100%" @click="onSubmit(ruleFormRef)">登录</el-button>
                </el-form-item>
            </el-form>
            <div class="link-register">
                <span>还没有账号?</span>
                <RouterLink to="/register" style="color: #1677ff;">立即注册</RouterLink>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { login } from '@/api/system/auth'
import useUserStore from '@/stores/userStore'
import router from '@/router/index'

// 表单实例
const ruleFormRef = ref()

// 登录表单参数
const form = reactive({
    username: '',
    password: ''
})

// 用户状态管理实例
const userStore = useUserStore()

// 表单校验规则
const rules = reactive({
    username: [
        { required: true, message: '请输入账号!', trigger: 'blur' },
    ],
    password: [
        { required: true, message: '请输入密码!', trigger: 'blur' },
    ]
})

// 登录按钮函数
const onSubmit = async (formRef) => {
    if (!formRef) return

    const valid = await formRef.validate()

    if (!valid) return

    try {
        await userStore.login(form)

        // await userStore.getInfo()

        router.replace('/')

    } catch (error) {
        console.error(error)
    }
}
</script>

<style scoped>
.login-wrap {
    height: 100vh;
    background: url("@/assets/images/bg.png") no-repeat center;
}

.container {
    align-items: center;
    padding: 20px;
    background: rgba(255, 255, 255, 0.6);
    border-radius: 8px;
}

.link-register {
    font-size: x-small;
    color: #999999;
}
</style>