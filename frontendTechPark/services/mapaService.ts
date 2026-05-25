import api from '@/lib/api'
import type { MapaParque, Ruta, NodoMapa } from '@/types'
import { mapBfsFromApi, mapMapaFromApi, mapRutaFromApi, type BackendMapa, type BackendRuta } from './apiMappers'

let lastMapa: MapaParque | undefined

export const mapaService = {
  getMapa: async (): Promise<MapaParque> => {
    const response = await api.get<BackendMapa>('/api/mapa')
    lastMapa = mapMapaFromApi(response)
    return lastMapa
  },

  getRuta: async (origenId: string, destinoId: string): Promise<Ruta> => {
    const response = await api.get<BackendRuta>(`/api/mapa/ruta?origenId=${encodeURIComponent(origenId)}&destinoId=${encodeURIComponent(destinoId)}`)
    return mapRutaFromApi(response, lastMapa)
  },

  getBFS: async (origenId: string): Promise<NodoMapa[]> => {
    const response = await api.get<string[]>(`/api/mapa/bfs/${origenId}`)
    return mapBfsFromApi(response, lastMapa)
  },
}

export default mapaService
