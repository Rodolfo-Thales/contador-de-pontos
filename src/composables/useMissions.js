import { ref } from 'vue'
import { api } from '../api/client'

export const MISSIONS = [
  {
    id: 'recrutar-participantes',
    number: '01',
    title: 'Recrutar Participantes',
    description: 'Recrute 37 participantes: 30 jovens + 7 adolescentes',
  },
  {
    id: 'criar-identidade',
    number: '02',
    title: 'Criar Identidade',
    description: 'Criar o grupo, escolher o nome e fazer uma logo',
  },
  {
    id: 'video-apresentacao',
    number: '03',
    title: 'Vídeo de Apresentação',
    description: 'Gravar e apresentar um vídeo no próximo Connect',
  },
  {
    id: 'completar-metas',
    number: '04',
    title: 'Completar Todas as Metas',
    description: 'Complete todas as missões acima primeiro e ganhe pontos extras!',
  },
]

export function useMissions() {
  const completions = ref([])
  const loading = ref(true)
  const error = ref(null)

  async function refresh() {
    try {
      completions.value = await api.get('/api/missions')
      error.value = null
    } catch (e) {
      error.value = e.message
    } finally {
      loading.value = false
    }
  }

  function isCompleted(missionId, groupId) {
    return completions.value.some((c) => c.missionId === missionId && c.groupId === groupId && c.completed)
  }

  async function setCompletion(missionId, groupId, completed) {
    await api.post(`/api/missions/${missionId}/${groupId}`, { completed })
    await refresh()
  }

  return { completions, loading, error, refresh, isCompleted, setCompletion }
}
