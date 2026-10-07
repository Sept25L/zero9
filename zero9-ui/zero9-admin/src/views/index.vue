<template>
    <div class="common-layout wh">
        <!-- <el-container style="height: 100%;">
            <el-aside style="height: 100%; background-color: #545c64" width="200px"> -->
        <el-container>
            <el-aside width="200px">
                <Menu />
            </el-aside>
            <el-container>
                <el-header>
                    <Header />
                </el-header>
                <el-container>
                    <el-main>
                        <Tabs />
                        <RouterView />
                    </el-main>
                    <el-footer class="footer-bar">
                        <Footer />
                    </el-footer>
                </el-container>
            </el-container>
        </el-container>
    </div>
</template>

<script setup>
import Header from '@/layout/header'
import Menu from '@/layout/menu'
import Tabs from '@/layout/tabs'
import Footer from '@/layout/footer'
</script>

<style scoped>
.el-container>.el-container {
    display: flex;
    flex-direction: column;
    height: 100%;
}

.el-main {
    flex: 1;
    /* 填满剩余空间 */
    overflow: auto;
    padding: 10px;
    padding-bottom: 60px;
}

.footer-bar {
    position: fixed;
    bottom: 0;
    left: 200px;
    /* 让出 aside */
    right: 0;
    z-index: 999;
}
</style>