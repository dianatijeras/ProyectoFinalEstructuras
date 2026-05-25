import api from '@/lib/api'
import type { ResumenParque, EstadoParque, Zona } from '@/types'
import {
  mapAtraccionFromApi,
  mapEstadoParqueFromApi,
  mapResumenFromApi,
  mapZonaFromApi,
  type BackendAtraccion,
  type BackendResumen,
  type BackendZona,
} from './apiMappers'
import { atraccionService } from './atraccionService'
import { alertasService } from './alertasService'

export const parqueService = {
  getResumen: async (): Promise<ResumenParque> => {
    const [resumen, atracciones, alertasClima, alertasMantenimiento] = await Promise.all([
      api.get<BackendResumen>('/api/parque/resumen'),
      atraccionService.getAll().catch(() => []),
      alertasService.getAlertasClima().catch(() => []),
      alertasService.getAlertasMantenimiento().catch(() => []),
    ])
    return mapResumenFromApi(resumen, atracciones, alertasClima, alertasMantenimiento)
  },

  getEstado: async (): Promise<EstadoParque> => {
    const response = await api.get<{ resumen?: BackendResumen; zonas?: unknown[] }>('/api/parque/estado')
    return mapEstadoParqueFromApi(response)
  },

  getZonas: async (): Promise<Zona[]> => {
    const [zonas, atracciones] = await Promise.all([
      api.get<BackendZona[]>('/api/zonas'),
      api.get<BackendAtraccion[]>('/api/atracciones').catch(() => []),
    ])
    const mappedAtracciones = atracciones.map(mapAtraccionFromApi)
    return zonas.map((zona) => mapZonaFromApi(zona, mappedAtracciones))
  },

  getZona: async (id: string): Promise<Zona> => {
    const [zona, atracciones] = await Promise.all([
      api.get<BackendZona>(`/api/zonas/${id}`),
      api.get<BackendAtraccion[]>('/api/atracciones').catch(() => []),
    ])
    return mapZonaFromApi(zona, atracciones.map(mapAtraccionFromApi))
  },
}

export default parqueService
