<template>
    <el-dialog v-model="dialogVisible" :title="dialogTitle" style="width: 600px" :before-close="handleClose">
        <el-form ref="ruleFormRef" :model="form" :rules="rules" label-width="auto">
            <el-form-item label="角色名称" prop="roleName">
                <el-input v-model="form.roleName" placeholder="请输入角色名称" />
            </el-form-item>
            <el-form-item label="权限标识" prop="roleKey">
                <el-input v-model="form.roleKey" placeholder="请输入权限标识" />
            </el-form-item>
            <el-form-item label="状态" prop="status">
                <el-radio-group v-model="form.status">
                    <el-radio value="0">启用</el-radio>
                    <el-radio value="1">禁用</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="菜单权限">
                <el-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand">
                    展开/折叠
                </el-checkbox>

                <el-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll">
                    全选/全不选
                </el-checkbox>

                <el-checkbox v-model="menuCheckStrictly" @change="handleCheckedTreeConnect">
                    父子联动
                </el-checkbox>

                <el-tree ref="menuRef" class="tree-border" :data="menuList" node-key="id" show-checkbox
                    default-expand-all :check-strictly="!menuCheckStrictly" :props="{
                        label: 'menuName',
                        children: 'children'
                    }" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="submitForm(ruleFormRef)">确定</el-button>
            </el-form-item>
        </el-form>
    </el-dialog>
</template>

<script setup>
import { reactive, ref, defineExpose, defineEmits, nextTick } from 'vue'
import { getRole, getRoles, addRole, editRole } from '@/api/system/role'
import { getCurrentMenus } from '@/api/system/menu'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['success'])
const dialogVisible = ref(false)
const dialogTitle = ref()
const formType = ref()
const menuList = ref([])
const menuRef = ref()

const menuExpand = ref(true)

const menuNodeAll = ref(false)

const menuCheckStrictly = ref(true)
// 表单数据
const form = reactive({
    id: undefined,
    roleName: undefined,
    roleKey: undefined,
    status: '0',
    menus: []
})

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

const handleCheckedTreeExpand = (value) => {
    const nodes = menuRef.value.store.nodesMap

    Object.keys(nodes).forEach(key => {
        nodes[key].expanded = value
    })
}

const handleCheckedTreeNodeAll = (value) => {
    if (value) {
        menuRef.value.setCheckedNodes(menuList.value)
    } else {
        menuRef.value.setCheckedKeys([])
    }
}

const getMenuIds = () => {

    const checkedKeys = menuRef.value.getCheckedKeys()

    const halfCheckedKeys = menuRef.value.getHalfCheckedKeys()

    return [
        ...checkedKeys,
        ...halfCheckedKeys
    ]
}

const submitForm = async (formEl) => {
    if (!formEl) return
    try {
        await formEl.validate()
        let resp
        form.menus = getMenuIds()
        if (formType.value === 'add') {
            resp = await addRole(form)
        } else {
            resp = await editRole(form)
        }

        ElMessage.success(resp.msg)
        handleClose()
        emit('success')
    } catch (error) {
        console.error(error)
    }
}

const initMenuList = async () => {
    const resp = await getCurrentMenus()
    menuList.value = resp.data
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
    initMenuList()
    if (id) {
        const resp = await getRole(id)
        Object.assign(form, resp.data)
        menuCheckStrictly.value = false
        await nextTick()
        menuRef.value.setCheckedKeys(resp.data.menus)
    }
}

// 重置表单
const resetForm = () => {
    form.id = undefined
    form.roleName = undefined
    form.rolekey = undefined
    form.status = '0'
    form.menus = []
    menuList.value = []
    menuRef.value?.setCheckedKeys([])
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
/*.el-input {
    width: 240px;
}*/
.tree-border {
    border: 1px solid #e5e6e7;
    background: #fff;
    border-radius: 4px;
    width: 100%;
}
</style>
