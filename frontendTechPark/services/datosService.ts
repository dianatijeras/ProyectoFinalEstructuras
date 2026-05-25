import { ApiError } from '@/lib/api'
import api from '@/lib/api'
import type { CargaDatosResponse } from '@/types'

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'

type RawResponse = string | { mensaje?: string; data?: string }

const normalizeResponse = (mensaje: string, data?: string): CargaDatosResponse => ({
  success: true,
  mensaje,
  registrosCargados: undefined,
  data,
})

export const datosService = {
  cargarEjemplo: async (): Promise<CargaDatosResponse> => {
    const response = await api.post<RawResponse>('/api/datos/cargar-ejemplo')
    if (typeof response === 'string') return normalizeResponse('Datos de ejemplo cargados', response)
    return normalizeResponse(response.mensaje || 'Datos de ejemplo cargados', response.data)
  },

  cargarCSV: async (file: File): Promise<CargaDatosResponse> => {
    const formData = new FormData()
    formData.append('file', file)

    const response = await fetch(`${API_BASE_URL}/api/datos/cargar-csv/upload`, {
      method: 'POST',
      body: formData,
    })

    const text = await response.text()
    const payload = text ? JSON.parse(text) : undefined

    if (!response.ok || payload?.ok === false) {
      throw new ApiError(
        payload?.mensaje || payload?.message || `Error ${response.status}: ${response.statusText}`,
        response.status,
        payload
      )
    }

    return normalizeResponse(payload?.mensaje || 'CSV cargado correctamente', payload?.data)
  },
}

export default datosService
