<script setup>
import { ref } from 'vue'
import { useAuthStore } from '../store/auth'
import { useRouter } from 'vue-router'
import { LogIn, User, Lock, Loader2 } from 'lucide-vue-next'

const authStore = useAuthStore()
const router = useRouter()

const username = ref('')
const password = ref('')
const isLoading = ref(false)
const error = ref('')

const handleLogin = async () => {
  if (!username.value || !password.value) return
  isLoading.value = true
  error.value = ''
  
  const success = await authStore.login(username.value, password.value)
  if (success) {
    router.push('/')
  } else {
    error.value = '登录失败，请检查用户名和密码'
  }
  isLoading.value = false
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-emerald-50 to-teal-100 p-4">
    <div class="w-full max-w-md bg-white/80 backdrop-blur-xl rounded-[2rem] shadow-2xl border border-white/20 p-8">
      <div class="text-center mb-8">
        <div class="inline-flex p-4 bg-emerald-500 rounded-2xl shadow-lg mb-4">
          <LogIn class="text-white" :size="32" />
        </div>
        <h1 class="text-2xl font-black text-emerald-900">欢迎回来</h1>
        <p class="text-emerald-600/60 text-sm mt-2">请登录您的医疗服务账号</p>
      </div>

      <form @submit.prevent="handleLogin" class="space-y-6">
        <div class="space-y-2">
          <label class="text-xs font-bold text-emerald-800/50 uppercase tracking-widest ml-1">用户名</label>
          <div class="relative group">
            <User class="absolute left-4 top-1/2 -translate-y-1/2 text-emerald-300 group-focus-within:text-emerald-500 transition-colors" :size="18" />
            <input 
              v-model="username"
              type="text" 
              placeholder="请输入用户名"
              class="w-full bg-white border border-emerald-100 rounded-xl py-3.5 pl-12 pr-4 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 transition-all"
            />
          </div>
        </div>

        <div class="space-y-2">
          <label class="text-xs font-bold text-emerald-800/50 uppercase tracking-widest ml-1">密码</label>
          <div class="relative group">
            <Lock class="absolute left-4 top-1/2 -translate-y-1/2 text-emerald-300 group-focus-within:text-emerald-500 transition-colors" :size="18" />
            <input 
              v-model="password"
              type="password" 
              placeholder="请输入密码"
              class="w-full bg-white border border-emerald-100 rounded-xl py-3.5 pl-12 pr-4 focus:outline-none focus:ring-2 focus:ring-emerald-500/20 transition-all"
            />
          </div>
        </div>

        <div v-if="error" class="text-red-500 text-xs text-center font-bold bg-red-50 py-2 rounded-lg border border-red-100">
          {{ error }}
        </div>

        <button 
          type="submit"
          :disabled="isLoading"
          class="w-full bg-emerald-600 hover:bg-emerald-500 text-white font-bold py-4 rounded-xl shadow-lg shadow-emerald-200 transition-all flex items-center justify-center gap-2"
        >
          <Loader2 v-if="isLoading" class="animate-spin" :size="20" />
          <span v-else>立即登录</span>
        </button>
      </form>

      <div class="mt-8 text-center">
        <p class="text-xs text-emerald-800/30 font-medium">
          测试账号: patient / 123456 或 admin / 123456
        </p>
      </div>
    </div>
  </div>
</template>
