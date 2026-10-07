<template>
    <el-upload class="avatar-uploader" action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
        :show-file-list="false" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
        <el-avatar v-if="imageUrl || avatarUrl" :size="75" :src="imageUrl || avatarUrl" fit="cover" />

        <el-icon v-else class="avatar-uploader-icon">
            <Plus />
        </el-icon>
    </el-upload>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import useUserStore from '@/stores/userStore'

const imageUrl = ref()
const avatar = defineModel('avatar')
const avatarUrl = computed(() => {

    if (!avatar.value) return ''

    if (avatar.value.startsWith('http')) {
        return avatar.value
    }

    return import.meta.env.VITE_APP_BASE_URL + avatar.value
})
const userStore = useUserStore()
const handleAvatarSuccess = (response, uploadFile) => {
    imageUrl.value = URL.createObjectURL(uploadFile.raw)
}

const beforeAvatarUpload = (rawFile) => {
    if (rawFile.type !== 'image/jpeg') {
        ElMessage.error('Avatar picture must be JPG format!')
        return false
    } else if (rawFile.size / 1024 / 1024 > 2) {
        ElMessage.error('Avatar picture size can not exceed 2MB!')
        return false
    }
    return true
}
onMounted(() => {
})
</script>

<style lang="scss" scoped></style>