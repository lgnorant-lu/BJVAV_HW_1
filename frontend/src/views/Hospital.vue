<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../store/auth'
import axios from 'axios'
import { 
  Stethoscope, 
  User as UserIcon, 
  Calendar, 
  ChevronRight, 
  ArrowLeft,
  CheckCircle2,
  Clock,
  Plus
} from 'lucide-vue-next'

const authStore = useAuthStore()
const departments = ref([])
const doctors = ref([])
const appointments = ref([])
const selectedDept = ref(null)
const selectedDoctor = ref(null)
const view = ref('depts') // depts, doctors, confirm, my

const fetchDepts = async () => {
  const res = await axios.get('/api/hospital/departments', {
    headers: { Authorization: `Bearer ${authStore.token}` }
  })
  departments.value = res.data
}

const fetchDoctors = async (deptId) => {
  const res = await axios.get(`/api/hospital/doctors?departmentId=${deptId}`, {
    headers: { Authorization: `Bearer ${authStore.token}` }
  })
  doctors.value = res.data
}

const fetchMyAppointments = async () => {
  const res = await axios.get('/api/hospital/appointments/my', {
    headers: { Authorization: `Bearer ${authStore.token}` }
  })
  appointments.value = res.data
}

const selectDept = (dept) => {
  selectedDept.value = dept
  fetchDoctors(dept.id)
  view.value = 'doctors'
}

const selectDoctor = (doc) => {
  selectedDoctor.value = doc
  view.value = 'confirm'
}

const confirmAppointment = async () => {
  try {
    await axios.post('/api/hospital/appointments', {
      doctorId: selectedDoctor.value.id,
      appointmentTime: new Date().toISOString()
    }, {
      headers: { Authorization: `Bearer ${authStore.token}` }
    })
    alert('预约成功！')
    view.value = 'my'
    fetchMyAppointments()
  } catch (e) {
    alert('预约失败')
  }
}

onMounted(() => {
  fetchDepts()
})
</script>

<template>
  <div class="min-h-screen bg-emerald-50/30 p-4 md:p-8">
    <div class="max-w-4xl mx-auto">
      <!-- Header -->
      <header class="flex items-center justify-between mb-8">
        <div class="flex items-center gap-4">
          <button v-if="view !== 'depts'" @click="view = 'depts'" class="p-2 hover:bg-white rounded-full transition-colors">
            <ArrowLeft :size="20" class="text-emerald-600" />
          </button>
          <h1 class="text-2xl font-black text-emerald-900">医疗导诊服务</h1>
        </div>
        <button @click="view = 'my'; fetchMyAppointments()" class="flex items-center gap-2 px-4 py-2 bg-white border border-emerald-100 rounded-xl text-emerald-700 font-bold text-sm shadow-sm hover:shadow-md transition-all">
          <Calendar :size="16" />
          我的预约
        </button>
      </header>

      <!-- View: Departments -->
      <div v-if="view === 'depts'" class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div 
          v-for="dept in departments" 
          :key="dept.id"
          @click="selectDept(dept)"
          class="bg-white p-6 rounded-2xl border border-emerald-100 shadow-sm hover:shadow-md hover:border-emerald-300 transition-all cursor-pointer group"
        >
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-4">
              <div class="p-3 bg-emerald-50 text-emerald-600 rounded-xl group-hover:bg-emerald-500 group-hover:text-white transition-colors">
                <Stethoscope :size="24" />
              </div>
              <div>
                <h3 class="font-bold text-emerald-900">{{ dept.name }}</h3>
                <p class="text-xs text-emerald-600/60 mt-1">{{ dept.description }}</p>
              </div>
            </div>
            <ChevronRight :size="20" class="text-emerald-200 group-hover:text-emerald-500" />
          </div>
        </div>
        <!-- Admin Add Dept Placeholder -->
        <div v-if="authStore.isAdmin" class="border-2 border-dashed border-emerald-200 p-6 rounded-2xl flex items-center justify-center text-emerald-400 hover:border-emerald-400 hover:text-emerald-600 cursor-pointer transition-all">
          <Plus :size="24" />
          <span class="ml-2 font-bold">添加科室 (管理员)</span>
        </div>
      </div>

      <!-- View: Doctors -->
      <div v-if="view === 'doctors'" class="space-y-4">
        <div class="bg-emerald-100/50 p-4 rounded-xl mb-6">
          <p class="text-sm font-bold text-emerald-800">当前科室：{{ selectedDept?.name }}</p>
        </div>
        <div 
          v-for="doc in doctors" 
          :key="doc.id"
          @click="selectDoctor(doc)"
          class="bg-white p-6 rounded-2xl border border-emerald-100 shadow-sm hover:shadow-md transition-all cursor-pointer flex items-center justify-between"
        >
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-blue-50 text-blue-500 rounded-full flex items-center justify-center">
              <UserIcon :size="24" />
            </div>
            <div>
              <div class="flex items-center gap-2">
                <h3 class="font-bold text-emerald-900">{{ doc.name }}</h3>
                <span class="text-[10px] px-2 py-0.5 bg-emerald-100 text-emerald-700 rounded-full font-bold">{{ doc.title }}</span>
              </div>
              <p class="text-xs text-emerald-600/60 mt-1">擅长：{{ doc.specialty }}</p>
            </div>
          </div>
          <div class="text-right">
            <p class="text-emerald-600 font-black">¥{{ doc.fee }}</p>
            <p class="text-[10px] text-emerald-400 font-bold uppercase tracking-tighter">挂号费</p>
          </div>
        </div>
      </div>

      <!-- View: Confirm -->
      <div v-if="view === 'confirm'" class="bg-white p-8 rounded-[2rem] border border-emerald-100 shadow-xl text-center">
        <div class="w-20 h-20 bg-emerald-50 text-emerald-500 rounded-full flex items-center justify-center mx-auto mb-6">
          <Calendar :size="40" />
        </div>
        <h2 class="text-xl font-black text-emerald-900 mb-2">确认预约信息</h2>
        <div class="bg-emerald-50/50 p-6 rounded-2xl mb-8 text-left space-y-3">
          <div class="flex justify-between">
            <span class="text-emerald-600/60 text-sm">就诊科室</span>
            <span class="font-bold text-emerald-900">{{ selectedDept?.name }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-emerald-600/60 text-sm">主治医生</span>
            <span class="font-bold text-emerald-900">{{ selectedDoctor?.name }} ({{ selectedDoctor?.title }})</span>
          </div>
          <div class="flex justify-between">
            <span class="text-emerald-600/60 text-sm">挂号费用</span>
            <span class="font-bold text-emerald-500">¥{{ selectedDoctor?.fee }}</span>
          </div>
        </div>
        <button @click="confirmAppointment" class="w-full bg-emerald-600 text-white font-black py-4 rounded-xl shadow-lg shadow-emerald-100 hover:bg-emerald-500 transition-all">
          确认预约
        </button>
      </div>

      <!-- View: My Appointments -->
      <div v-if="view === 'my'" class="space-y-4">
        <div v-if="appointments.length === 0" class="text-center py-20">
          <Clock :size="48" class="text-emerald-100 mx-auto mb-4" />
          <p class="text-emerald-900/40 font-bold">暂无预约记录</p>
        </div>
        <div 
          v-for="app in appointments" 
          :key="app.id"
          class="bg-white p-6 rounded-2xl border border-emerald-100 shadow-sm flex items-center justify-between"
        >
          <div class="flex items-center gap-4">
            <div class="p-3 bg-emerald-50 text-emerald-500 rounded-xl">
              <CheckCircle2 :size="24" />
            </div>
            <div>
              <h3 class="font-bold text-emerald-900">预约单 #{{ app.id.slice(-6) }}</h3>
              <p class="text-xs text-emerald-600/60 mt-1">时间：{{ new Date(app.appointmentTime).toLocaleString() }}</p>
            </div>
          </div>
          <span class="px-3 py-1 bg-emerald-100 text-emerald-700 text-[10px] font-black rounded-full uppercase">{{ app.status }}</span>
        </div>
      </div>
    </div>
  </div>
</template>
