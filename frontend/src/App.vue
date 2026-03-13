<script setup>
import { ref, nextTick, watch, onMounted } from 'vue'
import { useChatStore } from './store/chat'
import { APP_CONFIG } from './config'
import { Send, User, Cpu, Loader2, Sun, Zap, Leaf, Sparkles, Wind, Trash2, Moon } from 'lucide-vue-next'

const chatStore = useChatStore()
const inputMessage = ref('')
const chatContainer = ref(null)
const isDark = ref(false)

// 主题切换
const toggleTheme = () => {
  isDark.value = !isDark.value
  if (isDark.value) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
  localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
}

onMounted(() => {
  const savedTheme = localStorage.getItem('theme')
  if (savedTheme === 'dark' || (!savedTheme && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
    isDark.value = true
    document.documentElement.classList.add('dark')
  }
})

// 自动滚动到最新消息
watch(() => chatStore.messages.length, async () => {
  await nextTick()
  if (chatContainer.value) {
    chatContainer.value.scrollTo({
      top: chatContainer.value.scrollHeight,
      behavior: 'smooth'
    })
  }
})

const handleSend = () => {
  if (!inputMessage.value.trim() || chatStore.isTyping) return
  chatStore.sendMessage(inputMessage.value)
  inputMessage.value = ''
}
</script>

<template>
  <div class="flex items-center justify-center min-h-screen p-4 md:p-6 lg:p-8 transition-colors duration-500">
    <!-- Main Container with Solarpunk Glassmorphism -->
    <div class="w-full max-w-5xl solar-glass rounded-[2.5rem] flex flex-col h-[92vh] overflow-hidden relative">
      
      <!-- Decorative Background Elements -->
      <div class="absolute top-[-10%] right-[-5%] w-80 h-80 bg-yellow-100/10 rounded-full blur-[80px] pointer-events-none animate-pulse"></div>
      <div class="absolute bottom-[-10%] left-[-5%] w-80 h-80 bg-green-100/10 rounded-full blur-[80px] pointer-events-none animate-pulse" style="animation-delay: 2s"></div>

      <!-- Header -->
      <header class="px-8 py-6 flex items-center justify-between relative z-10 border-b border-white/10 bg-white/5 backdrop-blur-xl">
        <div class="flex items-center gap-5">
          <div class="p-3.5 bg-gradient-to-br from-yellow-300 via-green-400 to-emerald-500 rounded-2xl shadow-sm solar-float">
            <Leaf :size="28" class="text-white" />
          </div>
          <div>
            <div class="flex items-center gap-2.5">
              <h1 class="text-xl font-black tracking-tight text-emerald-900 dark:text-emerald-50">
                {{ APP_CONFIG.title }}
              </h1>
              <div class="flex gap-1">
                <Sun :size="14" class="text-yellow-500 animate-spin-slow" />
                <Wind :size="14" class="text-blue-400 animate-bounce" />
              </div>
            </div>
            <p class="text-[9px] font-bold text-emerald-700/50 dark:text-emerald-300/40 uppercase tracking-[0.3em] mt-0.5">{{ APP_CONFIG.subtitle }}</p>
          </div>
        </div>
        
        <div class="flex items-center gap-3">
          <!-- Theme Toggle -->
          <button 
            @click="toggleTheme"
            class="p-2.5 text-emerald-600/60 hover:text-emerald-500 hover:bg-white/10 rounded-xl transition-all duration-300"
            :title="isDark ? '切换到日间模式' : '切换到夜间模式'"
          >
            <Sun v-if="isDark" :size="20" />
            <Moon v-else :size="20" />
          </button>

          <button 
            @click="chatStore.clearMessages"
            class="p-2.5 text-emerald-600/60 hover:text-red-400 hover:bg-red-50/10 rounded-xl transition-all duration-300"
            title="清空对话"
          >
            <Trash2 :size="20" />
          </button>

          <div class="hidden md:block text-[9px] font-black bg-emerald-600/90 text-white px-3.5 py-2 rounded-full tracking-widest">
            ID: {{ chatStore.userId }}
          </div>
        </div>
      </header>

      <!-- Chat Area -->
      <main ref="chatContainer" class="flex-1 overflow-y-auto p-6 md:p-10 space-y-8 scroll-smooth">
        <TransitionGroup name="message">
          <div 
            v-for="(msg, index) in chatStore.messages" 
            :key="index"
            class="flex w-full"
            :class="msg.role === 'user' ? 'justify-end' : 'justify-start'"
          >
            <div class="flex max-w-[85%] md:max-w-[75%] gap-4" :class="msg.role === 'user' ? 'flex-row-reverse' : 'flex-row'">
              
              <!-- Avatar -->
              <div class="flex-shrink-0 mt-1">
                <div v-if="msg.role === 'user'" class="w-10 h-10 bg-white dark:bg-slate-800 rounded-xl flex items-center justify-center text-blue-500 shadow-sm border border-blue-50 dark:border-slate-700 solar-card">
                  <User :size="20" />
                </div>
                <div v-else-if="msg.role === 'assistant'" class="w-10 h-10 bg-gradient-to-br from-green-50 to-emerald-50 dark:from-emerald-900/20 dark:to-green-900/20 rounded-xl flex items-center justify-center text-emerald-600 dark:text-emerald-400 shadow-sm border border-emerald-100 dark:border-emerald-800/50 solar-card">
                  <Cpu :size="20" />
                </div>
              </div>

              <!-- Message Bubble -->
              <div class="flex flex-col" :class="msg.role === 'user' ? 'items-end' : 'items-start'">
                <div 
                  class="px-6 py-4 rounded-[1.5rem] text-sm md:text-base leading-relaxed whitespace-pre-wrap solar-card"
                  :class="[
                    msg.role === 'user' ? 'bg-gradient-to-br from-blue-500 to-blue-600 text-white rounded-tr-none shadow-md shadow-blue-100/20' : 
                    msg.role === 'assistant' ? 'bg-white/90 dark:bg-slate-800/90 text-emerald-900 dark:text-emerald-50 border border-white/20 rounded-tl-none shadow-sm' :
                    'bg-red-50/80 dark:bg-red-900/20 text-red-600 dark:text-red-400 border border-red-100/20 text-center w-full italic'
                  ]"
                >
                  {{ msg.text }}
                </div>
                <span class="text-[9px] font-bold text-emerald-800/20 dark:text-emerald-100/10 mt-2 px-2 uppercase tracking-widest">{{ msg.time }}</span>
              </div>
            </div>
          </div>
        </TransitionGroup>

        <!-- Typing Indicator -->
        <div v-if="chatStore.isTyping" class="flex items-center gap-4 text-emerald-600/40 dark:text-emerald-400/30">
          <div class="w-10 h-10 flex items-center justify-center bg-white/50 dark:bg-slate-800/50 rounded-xl border border-white/20">
            <Loader2 class="animate-spin" :size="20" />
          </div>
          <div class="flex flex-col gap-0.5">
            <span class="text-[10px] font-bold tracking-widest uppercase">{{ APP_CONFIG.typingMessage }}</span>
            <div class="flex gap-1">
              <span class="w-1 h-1 bg-green-300 rounded-full animate-bounce"></span>
              <span class="w-1 h-1 bg-green-300 rounded-full animate-bounce" style="animation-delay: 0.2s"></span>
              <span class="w-1 h-1 bg-green-300 rounded-full animate-bounce" style="animation-delay: 0.4s"></span>
            </div>
          </div>
        </div>
      </main>

      <!-- Footer / Input -->
      <footer class="p-6 md:p-8 bg-white/10 border-t border-white/10 backdrop-blur-2xl">
        <form @submit.prevent="handleSend" class="flex items-center gap-4 relative max-w-4xl mx-auto">
          <div class="flex-1 relative group">
            <div class="absolute left-5 top-1/2 -translate-y-1/2 text-emerald-300 dark:text-emerald-600 group-focus-within:text-emerald-500 transition-colors">
              <Sparkles :size="18" />
            </div>
            <input 
              v-model="inputMessage"
              type="text" 
              :placeholder="APP_CONFIG.placeholder"
              class="w-full bg-white/80 dark:bg-slate-800/80 text-emerald-900 dark:text-emerald-50 placeholder-emerald-800/20 dark:placeholder-emerald-100/10 pl-12 pr-6 py-4 rounded-[1.25rem] border border-white/20 focus:outline-none focus:ring-2 focus:ring-green-400/20 focus:bg-white dark:focus:bg-slate-800 transition-all duration-300 shadow-inner"
              :disabled="chatStore.isTyping"
            />
          </div>
          <button 
            type="submit" 
            :disabled="!inputMessage.trim() || chatStore.isTyping"
            class="energy-gradient text-white w-14 h-14 rounded-[1.25rem] solar-btn flex items-center justify-center shadow-lg shadow-green-100/20"
          >
            <Send :size="24" />
          </button>
        </form>
        <div class="flex flex-col items-center mt-6 gap-2">
          <div class="h-0.5 w-8 bg-gradient-to-r from-transparent via-green-100/20 to-transparent rounded-full"></div>
          <p class="text-[8px] text-emerald-800/20 dark:text-emerald-100/10 font-bold uppercase tracking-[0.4em]">
            {{ APP_CONFIG.footerText }}
          </p>
        </div>
      </footer>
    </div>
  </div>
</template>

<style scoped>
.animate-spin-slow {
  animation: spin 10s linear infinite;
}
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
