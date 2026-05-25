import api from '@/lib/api'
import type {
  Visitante,
  HistorialVisita,
  Favorito,
  Notificacion,
  Ticket,
  CompraTicketRequest,
  ShowParque,
} from '@/types'
import {
  mapFavoritoFromApi,
  mapHistorialFromApi,
  mapNotificacionFromApi,
  mapTicketFromApi,
  mapVisitanteFromApi,
  type BackendAtraccion,
  type BackendNotificacion,
  type BackendRegistroVisita,
  type BackendTicket,
  type BackendVisitante,
} from './apiMappers'

export const visitanteService = {
  getAll: async (): Promise<Visitante[]> => {
    const response = await api.get<BackendVisitante[]>('/api/visitantes')
    return response.map(mapVisitanteFromApi)
  },

  getByDocumento: async (documento: string): Promise<Visitante> => {
    const response = await api.get<BackendVisitante>(`/api/visitantes/${documento}`)
    return mapVisitanteFromApi(response)
  },

  getHistorial: async (documento: string): Promise<HistorialVisita[]> => {
    const response = await api.get<BackendRegistroVisita[]>(`/api/visitantes/${documento}/historial`)
    return response.map(mapHistorialFromApi)
  },

  getFavoritos: async (documento: string): Promise<Favorito[]> => {
    const response = await api.get<BackendAtraccion[]>(`/api/visitantes/${documento}/favoritos`)
    return response.map(mapFavoritoFromApi)
  },

  addFavorito: async (documento: string, atraccionId: string): Promise<void> => {
    await api.post<BackendAtraccion[]>(`/api/visitantes/${documento}/favoritos/${atraccionId}`)
  },

  getNotificaciones: async (documento: string): Promise<Notificacion[]> => {
    const response = await api.get<BackendNotificacion[]>(`/api/visitantes/${documento}/notificaciones`)
    return response.map(mapNotificacionFromApi)
  },



  getShows: async (): Promise<ShowParque[]> => {
    return api.get<ShowParque[]>('/api/shows')
  },

  recargarSaldo: async (documento: string, valor: number): Promise<Visitante> => {
    const response = await api.patch<BackendVisitante>(`/api/visitantes/${encodeURIComponent(documento)}/saldo`, { valor })
    return mapVisitanteFromApi(response)
  },

  comprarTicket: async (request: CompraTicketRequest): Promise<Ticket> => {
    const response = await api.post<BackendTicket>('/api/tickets/comprar', {
      documentoVisitante: request.documentoVisitante,
      tipoTicket: request.tipoTicket || request.tipo,
      zonaId: request.zonaId || 'ZON-001',
      cantidadPersonasFamilia: request.cantidadPersonasFamilia || request.grupoFamiliar || 1,
    })
    return mapTicketFromApi(response)
  },
}

export default visitanteService
