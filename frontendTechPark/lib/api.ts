// Configuracion base para consumir el backend Spring Boot.
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'

type SpringApiResponse<T> = {
  ok?: boolean
  mensaje?: string
  message?: string
  data?: T
}

function isSpringApiResponse<T>(value: unknown): value is SpringApiResponse<T> {
  return !!value && typeof value === 'object' && ('ok' in value || 'data' in value || 'mensaje' in value)
}

export async function apiFetch<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const url = `${API_BASE_URL}${endpoint}`
  const hasBody = options.body !== undefined

  const config: RequestInit = {
    ...options,
    headers: {
      ...(hasBody ? { 'Content-Type': 'application/json' } : {}),
      ...options.headers,
    },
  }

  try {
    const response = await fetch(url, config)
    const text = await response.text()
    const payload = text ? JSON.parse(text) : undefined

    if (!response.ok) {
      const message = payload?.mensaje || payload?.message || `Error ${response.status}: ${response.statusText}`
      throw new ApiError(message, response.status, payload)
    }

    if (payload === undefined) {
      return {} as T
    }

    // El backend de Tech-Park responde como: { ok, mensaje, data }.
    // El frontend trabaja directamente con data para no acoplar pantallas al wrapper.
    if (isSpringApiResponse<T>(payload)) {
      if (payload.ok === false) {
        throw new ApiError(payload.mensaje || payload.message || 'La operacion no fue exitosa', response.status, payload)
      }
      return (payload.data ?? payload) as T
    }

    return payload as T
  } catch (error) {
    if (error instanceof ApiError) throw error
    throw new ApiError(
      'No se pudo conectar con el servidor. Verifique que el backend Spring Boot este ejecutandose en http://localhost:8080.',
      0,
      { originalError: error }
    )
  }
}

export class ApiError extends Error {
  status: number
  data: unknown

  constructor(message: string, status: number, data?: unknown) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.data = data
  }
}

const notifyUiChanged = () => {
  if (typeof window !== 'undefined') {
    window.dispatchEvent(new Event('techpark:notifications-refresh'))
  }
}

export const api = {
  get: <T>(endpoint: string) => apiFetch<T>(endpoint, { method: 'GET' }),
  post: async <T>(endpoint: string, body?: unknown) => {
    const result = await apiFetch<T>(endpoint, {
      method: 'POST',
      body: body !== undefined ? JSON.stringify(body) : undefined,
    })
    notifyUiChanged()
    return result
  },
  patch: async <T>(endpoint: string, body?: unknown) => {
    const result = await apiFetch<T>(endpoint, {
      method: 'PATCH',
      body: body !== undefined ? JSON.stringify(body) : undefined,
    })
    notifyUiChanged()
    return result
  },
  put: async <T>(endpoint: string, body?: unknown) => {
    const result = await apiFetch<T>(endpoint, {
      method: 'PUT',
      body: body !== undefined ? JSON.stringify(body) : undefined,
    })
    notifyUiChanged()
    return result
  },
  delete: async <T>(endpoint: string) => {
    const result = await apiFetch<T>(endpoint, { method: 'DELETE' })
    notifyUiChanged()
    return result
  },
}

export default api
