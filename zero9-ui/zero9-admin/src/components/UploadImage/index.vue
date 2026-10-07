<template>
    <div class="upload-image">
        <el-upload :show-file-list="false" :auto-upload="false" accept="image/jpeg,image/png,image/gif,image/webp"
            :disabled="disabled" :on-change="handleChange">
            <div v-if="previewUrl" class="preview-wrapper">
                <el-image :src="previewUrl" fit="cover" class="preview-image" :preview-src-list="[previewUrl]"
                    preview-teleported />

                <div class="preview-mask">
                    <el-icon>
                        <Edit />
                    </el-icon>
                    <span>重新选择</span>
                </div>
            </div>

            <div v-else class="upload-box">
                <el-icon class="upload-icon">
                    <Plus />
                </el-icon>
                <span>选择图片</span>
            </div>
        </el-upload>
    </div>
</template>

<script setup>
import { ref, watch, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Edit } from '@element-plus/icons-vue'
import { upload } from '@/api/system/upload'

const props = defineProps({
    // 后端已经保存的 fileUrl
    modelValue: {
        type: String,
        default: ''
    },

    // 最大文件大小 MB
    maxSize: {
        type: Number,
        default: 5
    },

    disabled: {
        type: Boolean,
        default: false
    }
})

const emit = defineEmits([
    'update:modelValue',
    'change'
])

// 当前选择的 File
const file = ref(null)

// 当前预览地址
const previewUrl = ref('')

// 是否是 blob URL
const isBlobUrl = ref(false)

// 是否正在上传
const uploading = ref(false)


// ====================
// 释放 blob URL
// ====================
const revokePreview = () => {
    if (
        isBlobUrl.value &&
        previewUrl.value &&
        previewUrl.value.startsWith('blob:')
    ) {
        URL.revokeObjectURL(previewUrl.value)
    }
}


// ====================
// 初始化 / 编辑时显示已有图片
// ====================
watch(
    () => props.modelValue,
    (value) => {
        if (!value) {
            revokePreview()

            previewUrl.value = ''
            isBlobUrl.value = false

            return
        }

        // 当前是自己生成的 blob，不处理
        if (
            isBlobUrl.value &&
            previewUrl.value === value
        ) {
            return
        }

        revokePreview()

        if (
            value.startsWith('blob:') ||
            value.startsWith('http')
        ) {
            previewUrl.value = value
        } else {
            previewUrl.value =
                import.meta.env.VITE_APP_BASE_URL + value
        }

        isBlobUrl.value = false
    },
    {
        immediate: true
    }
)


// ====================
// 文件校验
// ====================
const validateFile = (rawFile) => {
    const isImage = [
        'image/jpeg',
        'image/png',
        'image/gif',
        'image/webp'
    ].includes(rawFile.type)

    if (!isImage) {
        ElMessage.error(
            '只能上传 JPG、PNG、GIF、WEBP 格式的图片'
        )

        return false
    }

    const isLtSize =
        rawFile.size / 1024 / 1024 < props.maxSize

    if (!isLtSize) {
        ElMessage.error(
            `图片大小不能超过 ${props.maxSize}MB`
        )

        return false
    }

    return true
}


// ====================
// 选择文件
// ====================
const handleChange = (uploadFile) => {
    const rawFile = uploadFile.raw

    if (!rawFile) {
        return
    }

    // 校验
    if (!validateFile(rawFile)) {
        return
    }

    // 释放旧 blob
    revokePreview()

    // 保存 File
    file.value = rawFile

    // 创建预览
    previewUrl.value =
        URL.createObjectURL(rawFile)

    isBlobUrl.value = true

    emit('change', rawFile)
}


// ====================
// 上传
// ====================
const uploadFile = async () => {

    // 没有新文件
    if (!file.value) {
        return props.modelValue || ''
    }

    uploading.value = true

    try {
        const formData = new FormData()

        formData.append('file', file.value)

        const resp = await upload(formData)

        // 根据你的后端实际返回结构修改
        const fileUrl = resp.fileUrl

        if (!fileUrl) {
            throw new Error('上传成功，但没有返回 fileUrl')
        }

        // 上传成功后
        // modelValue 保存真正的 fileUrl
        emit('update:modelValue', fileUrl)

        // 上传之后不再需要 File
        file.value = null

        // blob 释放
        revokePreview()

        // 重新使用服务器地址作为预览
        previewUrl.value =
            fileUrl.startsWith('http')
                ? fileUrl
                : import.meta.env.VITE_APP_BASE_URL + fileUrl

        isBlobUrl.value = false

        return fileUrl

    } catch (error) {
        ElMessage.error(
            error?.message || '图片上传失败'
        )

        throw error

    } finally {
        uploading.value = false
    }
}


// ====================
// 清空
// ====================
const clear = () => {
    revokePreview()

    file.value = null
    previewUrl.value = ''
    isBlobUrl.value = false

    emit('update:modelValue', '')
    emit('change', null)
}


// ====================
// 暴露给父组件
// ====================
defineExpose({
    upload: uploadFile,
    clear
})


onBeforeUnmount(() => {
    revokePreview()
})
</script>

<style scoped lang="scss">
.upload-image {
    display: inline-block;
}

.upload-box {
    width: 120px;
    height: 120px;

    border: 1px dashed #dcdfe6;
    border-radius: 6px;

    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    cursor: pointer;
    transition: border-color 0.2s;

    &:hover {
        border-color: #409eff;
    }
}

.upload-icon {
    font-size: 28px;
    color: #8c939d;
    margin-bottom: 8px;
}

.preview-wrapper {
    position: relative;

    width: 120px;
    height: 120px;

    overflow: hidden;
    border-radius: 6px;

    cursor: pointer;

    &:hover {
        .preview-mask {
            opacity: 1;
        }
    }
}

.preview-image {
    width: 100%;
    height: 100%;
    display: block;
}

.preview-mask {
    position: absolute;
    inset: 0;

    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    background: rgba(0, 0, 0, 0.5);
    color: #fff;

    opacity: 0;
    transition: opacity 0.2s;
}
</style>