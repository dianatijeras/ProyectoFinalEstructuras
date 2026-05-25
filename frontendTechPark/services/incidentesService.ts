import api from '@/lib/api'
import type { CrearIncidenteRequest, IncidenteOperativo, ResolverIncidenteRequest } from '@/types'

export const incidentesService = {
  registrar: async (request: CrearIncidenteRequest): Promise<IncidenteOperativo> => {
    return api.post<IncidenteOperativo>('/api/incidentes', request)
  },
  listar: async (): Promise<IncidenteOperativo[]> => {
    return api.get<IncidenteOperativo[]>('/api/incidentes')
  },
  resolver: async (id: string, request: ResolverIncidenteRequest): Promise<IncidenteOperativo> => {
    return api.patch<IncidenteOperativo>(`/api/incidentes/${encodeURIComponent(id)}/resolver`, request)
  },
}

export default incidentesService
