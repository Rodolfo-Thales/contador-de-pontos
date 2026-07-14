import { computed, onMounted, onUnmounted, ref } from 'vue'

const DAY_MS = 24 * 60 * 60 * 1000
const HOUR_MS = 60 * 60 * 1000
const MINUTE_MS = 60 * 1000

export function useCountdown(targetDate) {
  const remainingMs = ref(Math.max(0, targetDate.getTime() - Date.now()))
  let timer = null

  function tick() {
    remainingMs.value = Math.max(0, targetDate.getTime() - Date.now())
  }

  onMounted(() => {
    tick()
    timer = setInterval(tick, 1000)
  })
  onUnmounted(() => clearInterval(timer))

  const days = computed(() => Math.floor(remainingMs.value / DAY_MS))
  const hours = computed(() => Math.floor((remainingMs.value % DAY_MS) / HOUR_MS))
  const minutes = computed(() => Math.floor((remainingMs.value % HOUR_MS) / MINUTE_MS))
  const seconds = computed(() => Math.floor((remainingMs.value % MINUTE_MS) / 1000))

  const isUrgent = computed(() => remainingMs.value <= 7 * DAY_MS)
  const isCritical = computed(() => remainingMs.value <= DAY_MS)
  const isOver = computed(() => remainingMs.value <= 0)

  return { days, hours, minutes, seconds, isUrgent, isCritical, isOver }
}
