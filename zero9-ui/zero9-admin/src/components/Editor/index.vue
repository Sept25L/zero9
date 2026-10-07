<template>
    <div class="editor" :class="{
        'is-disabled': disabled,
        'is-readonly': readonly,
        'is-fullscreen': isFullscreen
    }">
        <!-- 工具栏 -->
        <Toolbar :editor="editorRef" :defaultConfig="toolbarConfig" :mode="mode" />

        <!-- 编辑器 -->
        <Editor v-model="innerValue" :defaultConfig="editorConfig" :mode="mode" @onCreated="handleCreated"
            @onChange="handleChange" @onFocus="handleFocus" @onBlur="handleBlur" />
    </div>
</template>

<script setup>
import {
    computed,
    onBeforeUnmount,
    ref,
    shallowRef
} from 'vue'

import {
    Editor,
    Toolbar
} from '@wangeditor/editor-for-vue'

import '@wangeditor/editor/dist/css/style.css'
import { upload } from '@/api/system/upload'

/**
 * =========================
 * Props
 * =========================
 */
const props = defineProps({
    /**
     * 编辑器内容
     */
    modelValue: {
        type: String,
        default: ''
    },

    /**
     * 编辑器高度
     *
     * 支持：
     *
     * height="300px"
     *
     * 或：
     *
     * :height="300"
     */
    height: {
        type: [Number, String],
        default: '100%'
    },

    /**
     * 禁用
     */
    disabled: {
        type: Boolean,
        default: false
    },

    /**
     * 只读
     */
    readonly: {
        type: Boolean,
        default: false
    },

    /**
     * Placeholder
     */
    placeholder: {
        type: String,
        default: '请输入内容...'
    },

    /**
     * 图片上传地址
     */
    uploadUrl: {
        type: String,
        default: '/api/upload/file'
    },

    /**
     * 图片最大大小 MB
     */
    maxImageSize: {
        type: Number,
        default: 5
    }
})

/**
 * =========================
 * Emits
 * =========================
 */
const emit = defineEmits([
    'update:modelValue',
    'change',
    'focus',
    'blur'
])

/**
 * =========================
 * 编辑器实例
 * =========================
 */
const editorRef = shallowRef()

/**
 * wangEditor 模式
 */
const mode = 'default'

/**
 * =========================
 * 全屏状态
 *
 * 不自己实现全屏，
 * 直接使用 wangEditor 的状态。
 * =========================
 */
const isFullscreen = ref(false)

/**
 * =========================
 * 待上传图片
 *
 * key：
 *   Blob URL
 *
 * value：
 *   File
 * =========================
 */
const pendingImages = new Map()

/**
 * =========================
 * 编辑器高度
 * =========================
 */
const editorHeight = computed(() => {
    return typeof props.height === 'number'
        ? `${props.height}px`
        : props.height
})

/**
 * =========================
 * v-model
 * =========================
 */
const innerValue = computed({
    get() {
        return props.modelValue
    },

    set(value) {
        emit(
            'update:modelValue',
            value
        )
    }
})

/**
 * =========================
 * 工具栏配置
 * =========================
 */
const toolbarConfig = {
    excludeKeys: [
        'group-video',
        'insertTable'
    ]
}

/**
 * =========================
 * 创建 Blob URL
 * =========================
 */
const createBlobUrl = (file) => {
    const blobUrl =
        URL.createObjectURL(file)

    pendingImages.set(
        blobUrl,
        file
    )

    return blobUrl
}

/**
 * =========================
 * 上传单个文件
 * =========================
 */
const uploadFile = async (file) => {
    const formData = new FormData()

    formData.append('file', file)

    const response = await upload(formData)

    // 根据你的后端返回结构获取 fileUrl
    const fileUrl = response?.fileUrl

    if (!fileUrl) {
        throw new Error('图片上传成功，但没有返回图片地址')
    }

    // 如果后端返回的是完整 URL，就直接使用
    if (fileUrl.startsWith('http')) {
        return fileUrl
    }

    // 相对路径拼接项目后端地址
    return import.meta.env.VITE_APP_BASE_URL + fileUrl
}

/**
 * =========================
 * 上传编辑器中所有图片
 *
 * 父组件：
 *
 * const html =
 *     await editorRef.value.uploadImages()
 * =========================
 */
const uploadImages = async () => {
    let html =
        props.modelValue

    /**
     * 当前 HTML 中存在的 Blob URL
     */
    const blobUrls = []

    pendingImages.forEach(
        (file, blobUrl) => {
            if (
                html.includes(
                    blobUrl
                )
            ) {
                blobUrls.push(
                    blobUrl
                )
            }
        }
    )

    /**
     * 没有图片需要上传
     */
    if (
        blobUrls.length === 0
    ) {
        return html
    }

    /**
     * 逐个上传
     */
    for (
        const blobUrl
        of blobUrls
    ) {
        const file =
            pendingImages.get(
                blobUrl
            )

        if (!file) {
            continue
        }

        try {
            /**
             * 上传
             */
            const url =
                await uploadFile(
                    file
                )

            /**
             * 替换 Blob URL
             */
            html =
                html
                    .split(blobUrl)
                    .join(url)

            /**
             * 释放 Blob URL
             */
            URL.revokeObjectURL(
                blobUrl
            )

            /**
             * 删除待上传图片
             */
            pendingImages.delete(
                blobUrl
            )
        } catch (error) {
            console.error(
                '图片上传失败：',
                error
            )

            throw error
        }
    }

    /**
     * 更新 v-model
     */
    emit(
        'update:modelValue',
        html
    )

    emit(
        'change',
        html
    )

    return html
}

/**
 * =========================
 * 全屏事件
 *
 * wangEditor 官方提供：
 *
 * fullScreen
 * unFullScreen
 *
 * 不需要 MutationObserver。
 * =========================
 */
const handleFullScreen = () => {
    isFullscreen.value = true
}

const handleUnFullScreen = () => {
    isFullscreen.value = false
}

/**
 * =========================
 * 编辑器配置
 * =========================
 */
const editorConfig = {
    /**
     * Placeholder
     */
    placeholder:
        props.placeholder,

    /**
     * 只读
     */
    readOnly:
        props.readonly ||
        props.disabled,

    /**
     * 菜单配置
     */
    MENU_CONF: {
        uploadImage: {
            /**
             * 最大文件大小
             */
            maxFileSize:
                props.maxImageSize *
                1024 *
                1024,

            /**
             * 一次最多选择 1 张
             */
            maxNumberOfFiles: 1,

            /**
             * 图片类型
             */
            allowedFileTypes: [
                'image/jpeg',
                'image/png',
                'image/gif',
                'image/webp'
            ],

            /**
             * 自定义上传
             *
             * 这里只创建 Blob URL，
             * 不立即上传服务器。
             */
            customUpload(
                file,
                insertFn
            ) {
                try {
                    /**
                     * 文件大小
                     */
                    const maxSize =
                        props.maxImageSize *
                        1024 *
                        1024

                    if (
                        file.size >
                        maxSize
                    ) {
                        console.error(
                            `图片大小不能超过 ${props.maxImageSize}MB`
                        )

                        return
                    }

                    /**
                     * 文件类型
                     */
                    const allowedTypes = [
                        'image/jpeg',
                        'image/png',
                        'image/gif',
                        'image/webp'
                    ]

                    if (
                        !allowedTypes.includes(
                            file.type
                        )
                    ) {
                        console.error(
                            '不支持的图片格式'
                        )

                        return
                    }

                    /**
                     * 创建 Blob URL
                     */
                    const blobUrl =
                        createBlobUrl(
                            file
                        )

                    /**
                     * 插入编辑器
                     */
                    insertFn(
                        blobUrl,
                        file.name,
                        blobUrl
                    )
                } catch (error) {
                    console.error(
                        '图片处理失败：',
                        error
                    )
                }
            },

            /**
             * 上传错误
             */
            onError(
                file,
                err
            ) {
                console.error(
                    '图片处理失败',
                    file,
                    err
                )
            }
        }
    }
}

/**
 * =========================
 * 创建编辑器
 * =========================
 */
const handleCreated = (
    editor
) => {
    editorRef.value =
        editor

    /**
     * 监听 wangEditor 全屏事件
     *
     * 官方事件：
     *
     * editor.on('fullScreen')
     * editor.on('unFullScreen')
     */
    editor.on(
        'fullScreen',
        handleFullScreen
    )

    editor.on(
        'unFullScreen',
        handleUnFullScreen
    )

    /**
     * 初始化当前状态
     */
    isFullscreen.value =
        !!editor.isFullScreen
}

/**
 * =========================
 * 内容变化
 * =========================
 */
const handleChange = (
    editor
) => {
    const html =
        editor.getHtml()

    emit(
        'update:modelValue',
        html
    )

    emit(
        'change',
        html
    )
}

/**
 * =========================
 * 获取焦点
 * =========================
 */
const handleFocus = (
    editor
) => {
    emit(
        'focus',
        editor.getHtml()
    )
}

/**
 * =========================
 * 失去焦点
 * =========================
 */
const handleBlur = (
    editor
) => {
    emit(
        'blur',
        editor.getHtml()
    )
}

/**
 * =========================
 * 暴露给父组件
 * =========================
 */
defineExpose({
    /**
     * 上传所有图片
     */
    uploadImages,

    /**
     * 获取编辑器实例
     */
    getEditor: () => {
        return editorRef.value
    },

    /**
     * 全屏状态
     */
    isFullscreen
})

/**
 * =========================
 * 销毁
 * =========================
 */
onBeforeUnmount(() => {
    const editor =
        editorRef.value

    if (editor) {
        /**
         * 移除全屏事件
         */
        editor.off(
            'fullScreen',
            handleFullScreen
        )

        editor.off(
            'unFullScreen',
            handleUnFullScreen
        )

        /**
         * 销毁编辑器
         */
        editor.destroy()
    }

    /**
     * 释放所有 Blob URL
     */
    pendingImages.forEach(
        (file, blobUrl) => {
            URL.revokeObjectURL(
                blobUrl
            )
        }
    )

    pendingImages.clear()

    editorRef.value = null
})
</script>

<style scoped>
/**
 * =========================
 * 编辑器
 * =========================
 */
.editor {
    width: 100%;

    border: 1px solid #dcdfe6;
    border-radius: 4px;

    overflow: hidden;

    background: #fff;
}

/**
 * 获取焦点
 */
/* .editor:focus-within {
    border-color: #18a058;
} */

/**
 * =========================
 * 工具栏
 * =========================
 */
:deep(.w-e-toolbar) {
    border-bottom: 1px solid #dcdfe6 !important;
}

/**
 * =========================
 * 普通状态
 *
 * 这里就是父组件传进来的高度：
 *
 * height="300px"
 *
 * => 300px
 * =========================
 */
:deep(.w-e-text-container) {
    height: v-bind(editorHeight) !important;
}

/**
 * =========================
 * wangEditor 全屏状态
 *
 * 重点：
 *
 * wangEditor 自己负责把编辑器
 * 放到全屏容器中。
 *
 * 我们只负责取消 300px 限制。
 * =========================
 */
:global(.w-e-full-screen-container) {
    z-index: 99999 !important;
}

/**
 * 全屏状态下的编辑区域
 *
 * 不再使用：
 *
 * height: 300px
 *
 * 而是占满屏幕剩余空间。
 */
:global(.w-e-full-screen-container .w-e-text-container) {
    height: calc(100vh - 40px) !important;
}

/**
 * 全屏滚动区域
 */
:global(.w-e-full-screen-container .w-e-scroll) {
    height: 100% !important;
}

/**
 * =========================
 * 兼容某些 wangEditor 版本
 *
 * 如果全屏 class 直接加在
 * editor 外层，也可以正常工作。
 * =========================
 */
.editor.is-fullscreen {
    position: fixed !important;

    top: 0 !important;
    right: 0 !important;
    bottom: 0 !important;
    left: 0 !important;

    width: 100vw !important;
    height: 100vh !important;

    margin: 0 !important;

    border: none !important;
    border-radius: 0 !important;

    z-index: 99999 !important;

    background: #fff !important;

    overflow: hidden !important;
}

/**
 * 自己的全屏状态下，
 * 同样取消 300px 限制。
 */
.editor.is-fullscreen :deep(.w-e-text-container) {
    height: calc(100vh - 40px) !important;
}

.editor.is-fullscreen :deep(.w-e-scroll) {
    height: 100% !important;
}

/**
 * =========================
 * 禁用
 * =========================
 */
.is-disabled {
    cursor: not-allowed;

    opacity: 0.6;
}

/**
 * =========================
 * 只读
 * =========================
 */
.is-readonly {
    border-color: transparent;
}
</style>