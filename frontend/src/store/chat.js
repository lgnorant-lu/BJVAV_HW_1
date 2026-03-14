import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'
import { APP_CONFIG } from '../config'

export const useChatStore = defineStore('chat', () => {
  const userId = ref(APP_CONFIG.user.defaultId)
  const isTyping = ref(false)
  
  // 初始化欢迎语
  const messages = ref([
    { 
      role: 'assistant', 
      text: APP_CONFIG.welcomeMessage,
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    }
  ])

  // 配置 Axios 实例
  const apiClient = axios.create({
    baseURL: APP_CONFIG.api.baseURL,
    timeout: APP_CONFIG.api.timeout,
    headers: { 'Content-Type': 'text/plain' }
  })

  const sendMessage = async (text) => {
    if (!text.trim() || isTyping.value) return
    
    const authStore = import.meta.glob('./auth.js')['./auth.js']().then(m => m.useAuthStore())
    // 实际上在 Pinia 中可以直接 import
    // 为了简单，我们直接在顶部 import 或者在这里动态获取
    
    // 乐观更新 UI：先将用户消息推入数组
    messages.value.push({
      role: 'user',
      text,
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    })
    isTyping.value = true

    try {
      // 动态获取 authStore 避免循环依赖（如果存在）
      const { useAuthStore } = await import('./auth')
      const auth = useAuthStore()
      
      const response = await apiClient.post(`/chat?userId=${auth.user?.userId || userId.value}`, text, {
        headers: {
          'Authorization': `Bearer ${auth.token}`
        }
      })
      messages.value.push({
        role: 'assistant',
        text: response.data,
        time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      })
    } catch (error) {
      console.error("API Error:", error)
      messages.value.push({
        role: 'system',
        text: '网络请求异常或后端服务未启动，请检查连接。',
        time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      })
    } finally {
      isTyping.value = false
    }
  }

  const clearMessages = () => {
    messages.value = [
      {
        role: 'assistant',
        text: APP_CONFIG.welcomeMessage,
        time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      }
    ]
  }

  return { userId, messages, isTyping, sendMessage, clearMessages }
})
