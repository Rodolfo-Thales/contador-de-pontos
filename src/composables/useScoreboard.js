import { ref } from 'vue'
import { api } from '../api/client'

export function useScoreboard() {
  const groups = ref([])
  const history = ref([])
  const loading = ref(true)
  const error = ref(null)

  async function refresh() {
    try {
      const data = await api.get('/api/scoreboard')
      groups.value = data.groups
      history.value = data.history
      error.value = null
    } catch (e) {
      error.value = e.message
    } finally {
      loading.value = false
    }
  }

  async function addPoints(groupId, amount) {
    await api.post(`/api/groups/${groupId}/points`, { amount })
    await refresh()
  }

  async function resetAll() {
    await api.post('/api/scoreboard/reset')
    await refresh()
  }

  return { groups, history, loading, error, refresh, addPoints, resetAll }
}
