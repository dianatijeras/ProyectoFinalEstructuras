import api from '@/lib/api'
import type { Atraccion, CambioEstadoRequest, RevisionTecnica } from '@/types'
import { sessionService } from './authService'
import { mapAtraccionFromApi, type BackendAtraccion } from './apiMappers'

const getDocumentoOperador = (documento?: string) => {
  const user = sessionService.getUser()
  return documento || user?.documento || '2001'
}

const mapResultadoRevision = (resultado: RevisionTecnica['resultado']) => {
  if (resultado === 'RECHAZADA' || resultado === 'FALLIDA') return 'FALLIDA'
  return 'SATISFACTORIA'
}

export const atraccionService = {
  getAll: async (): Promise<Atraccion[]> => {
    const response = await api.get<BackendAtraccion[]>('/api/atracciones')
    return response.map(mapAtraccionFromApi)
  },

  getById: async (id: string): Promise<Atraccion> => {
    const response = await api.get<BackendAtraccion>(`/api/atracciones/${id}`)
    return mapAtraccionFromApi(response)
  },

  getByEstado: async (estado: string): Promise<Atraccion[]> => {
    const response = await api.get<BackendAtraccion[]>(`/api/atracciones/estado/${estado}`)
    return response.map(mapAtraccionFromApi)
  },

  cambiarEstado: async (id: string, request: CambioEstadoRequest): Promise<Atraccion> => {
    const response = await api.patch<BackendAtraccion>(`/api/atracciones/${id}/estado`, {
      estado: request.estado,
      motivo: request.motivo,
      documentoOperador: getDocumentoOperador(request.documentoOperador),
    })
    return mapAtraccionFromApi(response)
  },

  registrarRevision: async (id: string, revision: RevisionTecnica): Promise<void> => {
    await api.post<string>(`/api/atracciones/${id}/revision`, {
      documentoOperador: getDocumentoOperador(revision.documentoOperador),
      descripcion: revision.descripcion,
      resultado: mapResultadoRevision(revision.resultado),
    })
  },
}

export default atraccionService
