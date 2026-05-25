import type {
  AlertaClima,
  AlertaMantenimiento,
  Atraccion,
  Cola,
  EstadoParque,
  Favorito,
  HistorialVisita,
  MapaParque,
  NodoMapa,
  Notificacion,
  PosicionCola,
  ReporteJornada,
  ResumenParque,
  Ruta,
  Ticket,
  Usuario,
  Visitante,
  Zona,
} from '@/types'

export type BackendAtraccion = {
  id: string
  nombre: string
  tipo: string
  capacidadMaximaPorCiclo: number
  alturaMinima: number
  edadMinima: number
  costoAdicional: number
  contadorAcumuladoVisitantes: number
  tiempoEstimadoEspera: number
  estado: string
  motivoCierre?: string | null
  zonaId?: string | null
  zonaNombre?: string | null
  tamanioCola: number
  incidentesOperativos: number
  visitantesCicloActual?: number
  operadoresResponsables?: number
}

export type BackendZona = {
  id: string
  nombre: string
  capacidadMaxima: number
  aforoActual: number
  cantidadAtracciones: number
  cantidadOperadores: number
  disponible?: boolean
}

export type BackendResumen = {
  nombre: string
  aforoActual: number
  capacidadMaxima: number
  cantidadZonas: number
  cantidadAtracciones: number
}

export type BackendVisitante = {
  id: string
  nombre: string
  documento: string
  edad: number
  estatura: number
  saldoVirtual: number
  ticketActivo?: string | null
  ticketActivoTipo?: string | null
  ticketActivoEstado?: string | null
  enCola: boolean
  ubicacionActual?: string | null
  ubicacionActualId?: string | null
}

export type BackendTicket = {
  id: string
  tipo: string
  precio: number
  estado: string
  prioridad: number
  documentoVisitante: string
}

export type BackendCola = {
  idAtraccion: string
  nombreAtraccion: string
  tamanioCola: number
  tiempoEstimadoEspera: number
  posicion?: number | null
  mensaje?: string | null
}

export type BackendMapa = {
  nodos: Array<{
    id: string
    nombreAtraccion: string
    estado: string
    tipo: string
    zona?: string | null
    zonaId?: string | null
    zonaDisponible?: boolean | null
  }>
  aristas: Array<{
    origenId: string
    destinoId: string
    peso: number
  }>
}

export type BackendRuta = {
  origenId: string
  destinoId: string
  distanciaTotal: number
  nodos: string[]
}

export type BackendRegistroVisita = {
  atraccionId: string
  atraccionNombre: string
  fechaHora: string
  tipoTicket: string
  costoDeducido: number
}

export type BackendNotificacion = {
  id: string
  mensaje: string
  tipo: string
  fechaHora: string
}

export type BackendReporte = {
  fecha: string
  ingresosDiarios: number
  atraccionesMasVisitadas: BackendAtraccion[]
  tiemposPromedioEspera: string[]
  cierresPorClima: number
  alertasMantenimiento: string[]
  atraccionesConMasIncidentes: BackendAtraccion[]
  incidentesOperativos?: Array<{ id:string; atraccionId:string; atraccionNombre:string; descripcion:string; gravedad:string; fechaHora:string; resuelto:boolean; solucion?: string | null }>
}

const normalizeRol = (rol: string): Usuario['rol'] => {
  const value = rol?.toUpperCase()
  if (value === 'ADMIN') return 'ADMINISTRADOR'
  if (value === 'ADMINISTRADOR' || value === 'OPERADOR' || value === 'VISITANTE') return value
  return 'VISITANTE'
}

const normalizeTipoAtraccion = (tipo: string): Atraccion['tipo'] => {
  const value = tipo?.toUpperCase()
  if (value === 'ACUATICA' || value === 'MECANICA_ALTURA' || value === 'OTRA') return value
  return 'OTRA'
}

const parseIdNombre = (value: string): { id: string; nombre: string } => {
  const [id, ...nombreParts] = value.split(' - ')
  return {
    id: id?.trim() || value,
    nombre: nombreParts.join(' - ').trim() || value,
  }
}

export function mapUsuarioFromApi(data: { id: string; nombre: string; documento: string; rol: string }): Usuario {
  return {
    id: data.id,
    nombre: data.nombre,
    documento: data.documento,
    rol: normalizeRol(data.rol),
  }
}

export function mapResumenFromApi(data: BackendResumen, atracciones: Atraccion[] = [], alertasClima: AlertaClima[] = [], alertasMantenimiento: AlertaMantenimiento[] = []): ResumenParque {
  return {
    nombreParque: data.nombre,
    capacidadActual: data.aforoActual,
    capacidadMaxima: data.capacidadMaxima,
    totalZonas: data.cantidadZonas,
    totalAtracciones: data.cantidadAtracciones,
    atraccionesActivas: atracciones.filter((a) => a.estado === 'ACTIVA').length,
    atraccionesEnMantenimiento: atracciones.filter((a) => a.estado === 'EN_MANTENIMIENTO').length,
    atraccionesCerradas: atracciones.filter((a) => a.estado === 'CERRADA').length,
    alertasClimaActivas: alertasClima.filter((a) => a.activa).length,
    alertasMantenimientoPendientes: alertasMantenimiento.filter((a) => !a.resuelta).length,
  }
}

export function mapEstadoParqueFromApi(data: { resumen?: BackendResumen; zonas?: unknown[] }): EstadoParque {
  return {
    abierto: true,
    horaApertura: '08:00',
    horaCierre: '18:00',
    mensaje: data?.resumen ? `Aforo actual: ${data.resumen.aforoActual}/${data.resumen.capacidadMaxima}` : 'Estado disponible',
  }
}

export function mapAtraccionFromApi(data: BackendAtraccion): Atraccion {
  return {
    id: data.id,
    nombre: data.nombre,
    tipo: normalizeTipoAtraccion(data.tipo),
    estado: data.estado as Atraccion['estado'],
    zonaId: data.zonaId || '',
    zonaNombre: data.zonaNombre || undefined,
    capacidadPorCiclo: data.capacidadMaximaPorCiclo,
    duracionMinutos: Math.max(1, Math.round((data.tiempoEstimadoEspera || 5) / Math.max(data.tamanioCola, 1))),
    alturaMinima: data.alturaMinima,
    restricciones: [`Edad minima: ${data.edadMinima}`, `Altura minima: ${data.alturaMinima} m`],
    tiempoEsperaEstimado: data.tiempoEstimadoEspera,
    colaActual: data.tamanioCola,
    costoAdicional: data.costoAdicional,
    incidentesOperativos: data.incidentesOperativos,
    edadMinima: data.edadMinima,
    contadorAcumuladoVisitantes: data.contadorAcumuladoVisitantes,
    visitantesCicloActual: data.visitantesCicloActual || 0,
    operadoresResponsables: data.operadoresResponsables || 0,
  }
}

export function mapZonaFromApi(data: BackendZona, atracciones: Atraccion[] = []): Zona {
  return {
    id: data.id,
    nombre: data.nombre,
    descripcion: `Aforo: ${data.aforoActual}/${data.capacidadMaxima}. Operadores: ${data.cantidadOperadores}`,
    capacidadMaxima: data.capacidadMaxima,
    aforoActual: data.aforoActual,
    cantidadAtracciones: data.cantidadAtracciones,
    cantidadOperadores: data.cantidadOperadores,
    atracciones: atracciones.filter((a) => a.zonaId === data.id),
    operadorAsignado: data.cantidadOperadores > 0 ? `${data.cantidadOperadores} operador(es)` : undefined,
    disponible: data.disponible ?? data.aforoActual < data.capacidadMaxima,
    estadoDisponibilidad: (data.disponible ?? data.aforoActual < data.capacidadMaxima) ? "DISPONIBLE" : "NO_DISPONIBLE",
  }
}

export function mapVisitanteFromApi(data: BackendVisitante): Visitante {
  return {
    id: data.id,
    documento: data.documento,
    nombre: data.nombre,
    edad: data.edad,
    estatura: data.estatura,
    saldoVirtual: data.saldoVirtual,
    ticketActivo: data.ticketActivo
      ? {
          id: data.ticketActivo,
          tipo: (data.ticketActivoTipo as Ticket['tipo']) || 'GENERAL',
          visitanteDocumento: data.documento,
          fechaCompra: new Date().toISOString(),
          fechaExpiracion: new Date().toISOString(),
          activo: data.ticketActivoEstado ? data.ticketActivoEstado === 'ACTIVO' : true,
          estado: data.ticketActivoEstado || 'ACTIVO',
        }
      : undefined,
    visitasHoy: 0,
    enCola: data.enCola,
    ubicacionActual: data.ubicacionActual || undefined,
    ubicacionActualId: data.ubicacionActualId || undefined,
  }
}

export function mapTicketFromApi(data: BackendTicket): Ticket {
  return {
    id: data.id,
    tipo: data.tipo as Ticket['tipo'],
    visitanteDocumento: data.documentoVisitante,
    fechaCompra: new Date().toISOString(),
    fechaExpiracion: new Date().toISOString(),
    activo: data.estado === 'ACTIVO',
    precio: data.precio,
    estado: data.estado,
    prioridad: data.prioridad,
  }
}

export function mapColaFromApi(data: BackendCola): Cola {
  return {
    atraccionId: data.idAtraccion,
    atraccionNombre: data.nombreAtraccion,
    visitantesEnCola: data.tamanioCola,
    tiempoEsperaEstimado: data.tiempoEstimadoEspera,
  }
}

export function mapPosicionFromApi(data: BackendCola): PosicionCola {
  return {
    posicion: data.posicion ?? 0,
    tiempoEsperaEstimado: data.tiempoEstimadoEspera,
    atraccionNombre: data.nombreAtraccion,
  }
}

export function mapMapaFromApi(data: BackendMapa): MapaParque {
  const zoneOrder = ['ZON-001', 'ZON-002', 'ZON-003', 'ZON-004']
  const zonePositions: Record<string, { x: number; y: number }> = {
    'ZON-001': { x: 120, y: 90 },
    'ZON-002': { x: 520, y: 90 },
    'ZON-003': { x: 120, y: 340 },
    'ZON-004': { x: 520, y: 340 },
    'SIN_ZONA': { x: 340, y: 560 },
  }
  const counters = new Map<string, number>()

  return {
    nodos: data.nodos.map((n) => {
      const zonaKey = n.zonaId || 'SIN_ZONA'
      const base = zonePositions[zonaKey] || zonePositions.SIN_ZONA
      const index = counters.get(zonaKey) || 0
      counters.set(zonaKey, index + 1)
      const col = index % 3
      const row = Math.floor(index / 3)

      return {
        id: n.id,
        nombre: n.nombreAtraccion || n.id,
        tipo: normalizeTipoAtraccion(n.tipo),
        estado: n.estado as NodoMapa['estado'],
        zonaNombre: n.zona || undefined,
        zonaId: n.zonaId || undefined,
        zonaDisponible: n.zonaDisponible ?? true,
        x: base.x + col * 90,
        y: base.y + row * 70,
      }
    }),
    aristas: data.aristas.map((a) => ({
      origenId: a.origenId,
      destinoId: a.destinoId,
      peso: a.peso,
      distanciaMetros: a.peso,
      tiempoMinutos: Math.max(1, Math.round(a.peso / 60)),
    })),
  }
}

export function mapRutaFromApi(data: BackendRuta, mapa?: MapaParque): Ruta {
  const nodos: NodoMapa[] = data.nodos.map((value, index) => {
    const parsed = parseIdNombre(value)
    const existing = mapa?.nodos.find((n) => n.id === parsed.id)
    return existing || {
      id: parsed.id,
      nombre: parsed.nombre,
      tipo: 'OTRA',
      estado: 'ACTIVA',
      x: 120 + index * 120,
      y: 100,
    }
  })

  return {
    nodos,
    distanciaTotal: data.distanciaTotal,
    tiempoEstimado: Math.max(1, Math.round(data.distanciaTotal / 60)),
    pasos: nodos.slice(0, -1).map((nodo, index) => ({
      desde: nodo.nombre,
      hacia: nodos[index + 1].nombre,
      distancia: 0,
      tiempo: 0,
    })),
  }
}

export function mapBfsFromApi(data: string[], mapa?: MapaParque): NodoMapa[] {
  return data.map((value, index) => {
    const parsed = parseIdNombre(value)
    return mapa?.nodos.find((n) => n.id === parsed.id) || {
      id: parsed.id,
      nombre: parsed.nombre,
      tipo: 'OTRA',
      estado: 'ACTIVA',
      x: 120 + index * 120,
      y: 100,
    }
  })
}

export function mapHistorialFromApi(data: BackendRegistroVisita, index: number): HistorialVisita {
  return {
    id: `${data.atraccionId}-${index}`,
    atraccionId: data.atraccionId,
    atraccionNombre: data.atraccionNombre,
    fecha: data.fechaHora,
    tiempoEspera: 0,
  }
}

export function mapFavoritoFromApi(data: BackendAtraccion): Favorito {
  return {
    atraccionId: data.id,
    atraccionNombre: data.nombre,
    fechaAgregado: new Date().toISOString(),
  }
}

export function mapNotificacionFromApi(data: BackendNotificacion): Notificacion {
  return {
    id: data.id,
    tipo: data.tipo === 'CLIMA' || data.tipo === 'MANTENIMIENTO' ? 'ALERTA' : (data.tipo as Notificacion['tipo']) || 'INFO',
    titulo: data.tipo.replace('_', ' '),
    mensaje: data.mensaje,
    fecha: data.fechaHora,
  }
}

export function mapAlertaClimaFromApi(value: string): AlertaClima {
  const parts = value.split(';')
  const id = parts[0] || `CLI-${Date.now()}`
  const tipo = (parts[1] || 'LLUVIA_FUERTE') as AlertaClima['tipo']
  const afectadas = Number((parts.find((p) => p.startsWith('afectadas=')) || 'afectadas=0').split('=')[1]) || 0
  const activa = (parts.find((p) => p.startsWith('activa=')) || 'activa=true').split('=')[1] !== 'false'
  const fecha = (parts.find((p) => p.startsWith('fecha=')) || '').replace('fecha=', '')

  return {
    id,
    tipo,
    fechaInicio: fecha || new Date().toISOString(),
    fechaFin: activa ? undefined : new Date().toISOString(),
    activa,
    mensaje: tipo === 'TORMENTA_ELECTRICA' ? 'Tormenta electrica en el parque' : 'Lluvia fuerte en el parque',
    atraccionesAfectadas: Array.from({ length: afectadas }, (_, i) => `ATR-${i + 1}`),
  }
}

export function mapAlertaMantenimientoFromApi(value: string): AlertaMantenimiento {
  const parts = value.split(';')
  const id = parts[0] || `MAN-${Date.now()}`
  const atraccionId = parts[1] || ''
  const atraccionNombre = parts[2] || 'Atraccion'
  const atendida = (parts.find((p) => p.startsWith('atendida=')) || 'atendida=false').split('=')[1] === 'true'

  return {
    id,
    atraccionId,
    atraccionNombre,
    tipo: 'PREVENTIVO',
    descripcion: atendida ? 'Revision tecnica atendida' : 'Mantenimiento preventivo pendiente',
    fechaReporte: new Date().toISOString(),
    resuelta: atendida,
    prioridad: 'ALTA',
  }
}

export function mapAlertaClimaCreated(id: string, tipo: AlertaClima['tipo']): AlertaClima {
  return {
    id,
    tipo,
    fechaInicio: new Date().toISOString(),
    activa: true,
    mensaje: tipo === 'TORMENTA_ELECTRICA' ? 'Tormenta electrica activada' : 'Lluvia fuerte activada',
    atraccionesAfectadas: [],
  }
}

export function mapReporteFromApi(data: BackendReporte): ReporteJornada {
  return {
    fecha: data.fecha,
    ingresosTotales: data.ingresosDiarios,
    ticketsVendidos: {
      general: 0,
      familiar: 0,
      fastPass: 0,
      total: 0,
      ingresoTotal: data.ingresosDiarios,
    },
    atraccionesMasVisitadas: data.atraccionesMasVisitadas.map((a) => ({
      atraccionId: a.id,
      nombre: a.nombre,
      visitas: a.contadorAcumuladoVisitantes,
      porcentaje: 0,
    })),
    tiemposEsperaPromedio: data.tiemposPromedioEspera.map((texto, index) => ({
      atraccionId: `TE-${index + 1}`,
      nombre: texto,
      tiempoPromedio: Number(texto.match(/\d+/)?.[0] || 0),
      tiempoMaximo: Number(texto.match(/\d+/)?.[0] || 0),
    })),
    cierresPorClima: data.cierresPorClima > 0 ? [{
      hora: new Date().toISOString(),
      tipo: 'LLUVIA_FUERTE',
      duracionMinutos: 0,
      atraccionesAfectadas: data.cierresPorClima,
    }] : [],
    alertasMantenimiento: data.alertasMantenimiento.map((texto) => ({
      atraccionNombre: texto,
      tipo: 'PREVENTIVO',
      duracionMinutos: 0,
    })),
    incidentesOperativos: (data.incidentesOperativos && data.incidentesOperativos.length > 0)
      ? data.incidentesOperativos.map((i) => ({ hora: i.fechaHora, descripcion: `${i.atraccionNombre}: ${i.descripcion} (${i.gravedad})`, resuelto: i.resuelto }))
      : data.atraccionesConMasIncidentes.map((a) => ({ hora: new Date().toISOString(), descripcion: `${a.nombre}: ${a.incidentesOperativos} incidente(s) operativo(s)`, resuelto: false, })),
  }
}
