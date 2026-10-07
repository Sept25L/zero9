<template>
    <svg class="svg-icon" :style="{
        width: size + 'px',
        height: size + 'px',
        color: color,
        margin: margin
    }">
        <use :xlink:href="symbolId" />
    </svg>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
    name: { type: String, required: true },
    color: { type: String, default: '' },
    size: { type: [Number, String], default: 25 },
    prefix: { type: String, default: 'icon' },
    margin: { type: String, default: 'auto' }
})

const symbolId = computed(() => `#${props.prefix}-${props.name}`)
</script>

<style scoped>
.svg-icon {
    fill: currentColor;
}
</style>