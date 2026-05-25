import api from '@/lib/api'
import type { ReporteJornada } from '@/types'
import { mapReporteFromApi, type BackendReporte } from './apiMappers'

export const reportesService = {
  getReporteJornada: async (): Promise<ReporteJornada> => {
    const response = await api.get<BackendReporte>('/api/reportes/jornada')
    return mapReporteFromApi(response)
  },
}

export default reportesService
