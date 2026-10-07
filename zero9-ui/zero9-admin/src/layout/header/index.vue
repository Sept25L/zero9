<template>
    <div class="flex-between wh wrap">
        <BreadCrumb />
        <Avatar />
    </div>
</template>

<script setup>
import BreadCrumb from './component/breadcrumb'
import Avatar from './component/avatar'

</script>

<style lang="scss" scoped>
.wrap {
    // padding: 0 20px;
    // background-color: #f5f5f5;
    // box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
    box-sizing: border-box;
}
</style>
