import { onUnmounted, ref, watch } from 'vue'

const REDUCE_MOTION = window.matchMedia('(prefers-reduced-motion: reduce)').matches

function easeOutCubic(t) {
  return 1 - Math.pow(1 - t, 3)
}

/**
 * Anima um número de seu valor anterior até o novo valor sempre que `target` muda.
 * Na primeira renderização, anima de 0 até o valor inicial.
 */
export function useCountUp(target, { duration = 1500 } = {}) {
  const displayValue = ref(0)
  let previous = 0
  let frameId = null

  function animateTo(next) {
    if (REDUCE_MOTION) {
      displayValue.value = next
      previous = next
      return
    }

    const from = previous
    const delta = next - from
    const start = performance.now()

    cancelAnimationFrame(frameId)

    function step(now) {
      const elapsed = now - start
      const progress = Math.min(1, elapsed / duration)
      displayValue.value = Math.round(from + delta * easeOutCubic(progress))
      if (progress < 1) {
        frameId = requestAnimationFrame(step)
      } else {
        previous = next
      }
    }

    frameId = requestAnimationFrame(step)
  }

  watch(
    () => target.value,
    (next) => animateTo(next ?? 0),
    { immediate: true },
  )

  onUnmounted(() => cancelAnimationFrame(frameId))

  return { displayValue }
}
