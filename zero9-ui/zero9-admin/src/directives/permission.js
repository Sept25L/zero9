// src/directives/permission.js
import useUserStore from '@/stores/userStore'

export default {
  mounted(el, binding) {
    const userStore = useUserStore()
    const value = binding.value; // ['system:user:edit']
    if (value && value instanceof Array && value.length > 0) {
      const hasPermission = value.some(v => userStore.perms.includes(v));
      if (!hasPermission) {
        el.parentNode && el.parentNode.removeChild(el); // 没权限就移除元素
      }
    } else {
      console.error(`v-hasPermi 指令需要一个数组值，例如 ['system:user:edit']`);
    }
  }
};