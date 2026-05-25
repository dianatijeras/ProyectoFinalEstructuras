import api from '@/lib/api'
import type { Cola, UnirseColaRequest, PosicionCola } from '@/types'
import { sessionService } from './authService'
import { mapColaFromApi, mapPosicionFromApi, type BackendCola } from './apiMappers'

export const colasService = {
  unirse: async (request: UnirseColaRequest): Promise<PosicionCola> => {
    const response = await api.post<BackendCola>('/api/colas/unirse', {
      documentoVisitante: request.documentoVisitante,
      idAtraccion: request.idAtraccion || request.atraccionId,
    })
    return mapPosicionFromApi(response)
  },

  procesarSiguiente: async (atraccionId: string): Promise<void> => {
    const user = sessionService.getUser()
    await api.post<string>('/api/colas/procesar-siguiente', {
      documentoOperador: user?.documento || '2001',
      idAtraccion: atraccionId,
    })
  },

  getCola: async (atraccionId: string): Promise<Cola> => {
    const response = await api.get<BackendCola>(`/api/colas/${atraccionId}`)
    return mapColaFromApi(response)
  },

  getPosicion: async (atraccionId: string, documentoVisitante: string): Promise<PosicionCola> => {
    const response = await api.get<BackendCola>(`/api/colas/${atraccionId}/posicion/${documentoVisitante}`)
    return mapPosicionFromApi(response)
  },
}

export default colasService
