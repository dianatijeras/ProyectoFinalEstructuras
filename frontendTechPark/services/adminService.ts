import api from '@/lib/api'
import type { ActualizarAtraccionRequest, ActualizarOperadorRequest, ActualizarVisitanteRequest, ActualizarZonaRequest, Atraccion, CrearAtraccionRequest, CrearOperadorRequest, CrearVisitanteRequest, CrearZonaRequest, IncidenteOperativo, Operador, ResolverIncidenteRequest, UsuarioActivo, Visitante, Zona } from '@/types'
import { mapAtraccionFromApi, mapVisitanteFromApi, mapZonaFromApi, type BackendAtraccion, type BackendVisitante, type BackendZona } from './apiMappers'

export type BackendOperador = {
  id: string
  nombre: string
  documento: string
  edad: number
  zonaAsignadaId?: string | null
  zonaAsignadaNombre?: string | null
  atraccionesAsignadas?: string[] | null
}

export function mapOperadorFromApi(data: BackendOperador): Operador {
  return {
    id: data.id,
    nombre: data.nombre,
    documento: data.documento,
    edad: data.edad,
    zonaAsignadaId: data.zonaAsignadaId || undefined,
    zonaAsignadaNombre: data.zonaAsignadaNombre || undefined,
    atraccionesAsignadas: data.atraccionesAsignadas || [],
  }
}

export const adminService = {
  crearVisitante: async (request: CrearVisitanteRequest): Promise<Visitante> => {
    const response = await api.post<BackendVisitante>('/api/admin/visitantes', request)
    return mapVisitanteFromApi(response)
  },
  modificarVisitante: async (documento: string, request: ActualizarVisitanteRequest): Promise<Visitante> => {
    const response = await api.patch<BackendVisitante>(`/api/admin/visitantes/${encodeURIComponent(documento)}`, request)
    return mapVisitanteFromApi(response)
  },
  usuariosActivos: async (q = ''): Promise<UsuarioActivo[]> => {
    return api.get<UsuarioActivo[]>(`/api/admin/usuarios-activos?q=${encodeURIComponent(q)}`)
  },
  crearZona: async (request: CrearZonaRequest): Promise<Zona> => {
    const response = await api.post<BackendZona>('/api/admin/zonas', request)
    return mapZonaFromApi(response)
  },
  modificarZona: async (id: string, request: ActualizarZonaRequest): Promise<Zona> => {
    const response = await api.patch<BackendZona>(`/api/admin/zonas/${encodeURIComponent(id)}`, request)
    return mapZonaFromApi(response)
  },
  crearOperador: async (request: CrearOperadorRequest): Promise<Operador> => {
    const response = await api.post<BackendOperador>('/api/admin/operadores', request)
    return mapOperadorFromApi(response)
  },
  modificarOperador: async (documento: string, request: ActualizarOperadorRequest): Promise<Operador> => {
    const response = await api.patch<BackendOperador>(`/api/admin/operadores/${encodeURIComponent(documento)}`, request)
    return mapOperadorFromApi(response)
  },
  listarOperadores: async (): Promise<Operador[]> => {
    const response = await api.get<BackendOperador[]>('/api/admin/operadores')
    return response.map(mapOperadorFromApi)
  },
  crearAtraccion: async (request: CrearAtraccionRequest): Promise<Atraccion> => {
    const response = await api.post<BackendAtraccion>('/api/admin/atracciones', request)
    return mapAtraccionFromApi(response)
  },
  modificarAtraccion: async (id: string, request: ActualizarAtraccionRequest): Promise<Atraccion> => {
    const response = await api.patch<BackendAtraccion>(`/api/admin/atracciones/${encodeURIComponent(id)}`, request)
    return mapAtraccionFromApi(response)
  },
  asignarOperadorZona: async (documentoOperador: string, zonaId: string): Promise<Operador> => {
    const response = await api.patch<BackendOperador>('/api/admin/operadores/asignar-zona', { documentoOperador, zonaId })
    return mapOperadorFromApi(response)
  },
  asignarOperadorAtraccion: async (documentoOperador: string, idAtraccion: string): Promise<void> => {
    await api.patch('/api/admin/operadores/asignar-atraccion', { documentoOperador, idAtraccion })
  },
  removerOperadorZona: async (documento: string): Promise<Operador> => {
    const response = await api.patch<BackendOperador>(`/api/admin/operadores/${encodeURIComponent(documento)}/remover-zona`, {})
    return mapOperadorFromApi(response)
  },
  resolverIncidente: async (id: string, request: ResolverIncidenteRequest): Promise<IncidenteOperativo> => {
    return api.patch<IncidenteOperativo>(`/api/incidentes/${encodeURIComponent(id)}/resolver`, request)
  },
  buscarZonas: async (q: string): Promise<Zona[]> => {
    const response = await api.get<BackendZona[]>(`/api/admin/buscar/zonas?q=${encodeURIComponent(q)}`)
    return response.map((z) => mapZonaFromApi(z))
  },
  buscarAtracciones: async (q: string): Promise<Atraccion[]> => {
    const response = await api.get<BackendAtraccion[]>(`/api/admin/buscar/atracciones?q=${encodeURIComponent(q)}`)
    return response.map(mapAtraccionFromApi)
  },
}

export default adminService
