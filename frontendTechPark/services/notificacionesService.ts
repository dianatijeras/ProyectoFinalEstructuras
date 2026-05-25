import api from '@/lib/api'
import type { Notificacion } from '@/types'

type BackendAnyNotification = {
  id: string
  tipo?: string
  titulo?: string
  mensaje: string
  fechaHora?: string
  fecha?: string
}

export const notificacionesService = {
  getGlobales: async (documento?: string): Promise<Notificacion[]> => {
    const query = documento ? `?documento=${encodeURIComponent(documento)}` : ''
    const response = await api.get<BackendAnyNotification[]>(`/api/notificaciones${query}`)
    return response.map((n) => ({
      id: n.id,
      tipo: (n.tipo === 'CLIMA' || n.tipo === 'MANTENIMIENTO') ? 'ALERTA' : (n.tipo as Notificacion['tipo']) || 'INFO',
      titulo: n.titulo || n.tipo || 'Notificacion',
      mensaje: n.mensaje,
      fecha: n.fechaHora || n.fecha || new Date().toISOString(),
    }))
  },
}

export default notificacionesService
