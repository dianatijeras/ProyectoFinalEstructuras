import api from '@/lib/api'
import type { AlertaClima, AlertaMantenimiento, CrearAlertaClimaRequest } from '@/types'
import { mapAlertaClimaCreated, mapAlertaClimaFromApi, mapAlertaMantenimientoFromApi } from './apiMappers'

export const alertasService = {
  crearAlertaClima: async (request: CrearAlertaClimaRequest): Promise<AlertaClima> => {
    const tipo = request.tipoClima || request.tipo
    const id = await api.post<string>('/api/alertas/clima', { tipoClima: tipo })
    return mapAlertaClimaCreated(id, tipo)
  },

  getAlertasMantenimiento: async (): Promise<AlertaMantenimiento[]> => {
    const response = await api.get<string[]>('/api/alertas/mantenimiento')
    return response.map(mapAlertaMantenimientoFromApi)
  },

  getAlertasClima: async (): Promise<AlertaClima[]> => {
    const response = await api.get<string[]>('/api/alertas/clima')
    return response.map(mapAlertaClimaFromApi)
  },

  finalizarAlertaClima: async (id: string): Promise<string> => {
    return api.patch<string>(`/api/alertas/clima/${encodeURIComponent(id)}/finalizar`, {})
  },
}

export default alertasService
