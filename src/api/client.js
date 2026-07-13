const API_BASE = import.meta.env.VITE_API_URL ?? ''

function getCookie(name) {
  const match = document.cookie.match(new RegExp('(?:^|; )' + name + '=([^;]*)'))
  return match ? decodeURIComponent(match[1]) : null
}

async function request(path, options = {}) {
  const headers = {}

  if (options.body !== undefined) {
    headers['Content-Type'] = 'application/json'
  }

  if (options.method && options.method !== 'GET') {
    let token = getCookie('XSRF-TOKEN')
    if (!token) {
      await fetch(`${API_BASE}/api/auth/csrf`, { credentials: 'include' })
      token = getCookie('XSRF-TOKEN')
    }
    if (token) headers['X-XSRF-TOKEN'] = token
  }

  const response = await fetch(`${API_BASE}${path}`, {
    method: options.method ?? 'GET',
    headers,
    credentials: 'include',
    body: options.body !== undefined ? JSON.stringify(options.body) : undefined,
  })

  if (!response.ok) {
    let message = 'Erro inesperado. Tente novamente.'
    try {
      const data = await response.json()
      if (data.message) message = data.message
    } catch {
      /* resposta sem corpo JSON */
    }
    const error = new Error(message)
    error.status = response.status
    throw error
  }

  if (response.status === 204) return null
  return response.json()
}

export const api = {
  get: (path) => request(path),
  post: (path, body) => request(path, { method: 'POST', body }),
}
