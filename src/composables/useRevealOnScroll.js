import { onMounted, onUnmounted, ref } from 'vue'

/** Marca `revealed = true` na primeira vez que o elemento entra na viewport. */
export function useRevealOnScroll({ threshold = 0.15 } = {}) {
  const targetRef = ref(null)
  const revealed = ref(false)
  let observer = null

  onMounted(() => {
    if (!targetRef.value) return
    observer = new IntersectionObserver(
      (entries) => {
        if (entries[0]?.isIntersecting) {
          revealed.value = true
          observer.disconnect()
        }
      },
      { threshold },
    )
    observer.observe(targetRef.value)
  })

  onUnmounted(() => observer?.disconnect())

  return { targetRef, revealed }
}
