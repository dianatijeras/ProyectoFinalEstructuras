// =====================
// Enums
// =====================

export type Rol = 'ADMINISTRADOR' | 'OPERADOR' | 'VISITANTE'

export type EstadoAtraccion = 'ACTIVA' | 'EN_MANTENIMIENTO' | 'CERRADA'

export type TipoAtraccion =
  | 'ACUATICA'
  | 'MECANICA_ALTURA'
  | 'OTRA'
  | 'MONTANA_RUSA'
  | 'CARRUSEL'
  | 'RUEDA_DE_LA_FORTUNA'
  | 'CASA_DEL_TERROR'
  | 'TOBOGAN_ACUATICO'
  | 'SILLAS_VOLADORAS'
  | 'TREN_FANTASMA'
  | 'SIMULADOR'
  | 'JUEGO_MECANICO'

export type TipoTicket = 'GENERAL' | 'FAMILIAR' | 'FAST_PASS'

export type TipoAlertaClima = 'LLUVIA_FUERTE' | 'TORMENTA_ELECTRICA'

// =====================
// User & Auth
// =====================

export interface Usuario {
  id: string
  nombre: string
  documento: string
  rol: Rol
}

export interface LoginRequest {
  documento: string
  password: string
}

// =====================
// Park & Zones
// =====================

export interface ResumenParque {
  nombreParque: string
  capacidadActual: number
  capacidadMaxima: number
  totalZonas: number
  totalAtracciones: number
  atraccionesActivas: number
  atraccionesEnMantenimiento: number
  atraccionesCerradas: number
  alertasClimaActivas: number
  alertasMantenimientoPendientes: number
}

export interface EstadoParque {
  abierto: boolean
  horaApertura: string
  horaCierre: string
  mensaje?: string
}

export interface Zona {
  id: string
  nombre: string
  descripcion?: string
  capacidadMaxima: number
  aforoActual?: number
  cantidadAtracciones?: number
  cantidadOperadores?: number
  atracciones: Atraccion[]
  operadorAsignado?: string
  disponible?: boolean
  estadoDisponibilidad?: string
}

// =====================
// Attractions
// =====================

export interface Atraccion {
  id: string
  nombre: string
  tipo: TipoAtraccion
  estado: EstadoAtraccion
  zonaId: string
  zonaNombre?: string
  capacidadPorCiclo: number
  duracionMinutos: number
  alturaMinima?: number
  restricciones?: string[]
  tiempoEsperaEstimado?: number
  colaActual?: number
  costoAdicional?: number
  incidentesOperativos?: number
  edadMinima?: number
  contadorAcumuladoVisitantes?: number
  visitantesCicloActual?: number
  operadoresResponsables?: number
}

export interface CambioEstadoRequest {
  estado: EstadoAtraccion
  motivo?: string
  documentoOperador?: string
}

export interface RevisionTecnica {
  documentoOperador?: string
  descripcion: string
  resultado: 'APROBADA' | 'RECHAZADA' | 'PENDIENTE' | 'SATISFACTORIA' | 'FALLIDA'
  observaciones?: string
}

// =====================
// Visitors & Tickets
// =====================

export interface Visitante {
  id?: string
  documento: string
  nombre: string
  email?: string
  telefono?: string
  ticketActivo?: Ticket
  visitasHoy: number
  edad?: number
  estatura?: number
  saldoVirtual?: number
  enCola?: boolean
  ubicacionActual?: string
  ubicacionActualId?: string
}

export interface Ticket {
  id: string
  tipo: TipoTicket
  visitanteDocumento: string
  zonaId?: string
  fechaCompra: string
  fechaExpiracion: string
  activo: boolean
  grupoFamiliar?: number
  precio?: number
  estado?: string
  prioridad?: number
}

export interface CompraTicketRequest {
  documentoVisitante: string
  tipo: TipoTicket
  tipoTicket?: TipoTicket
  zonaId?: string
  grupoFamiliar?: number
  cantidadPersonasFamilia?: number
}

export interface HistorialVisita {
  id: string
  atraccionId: string
  atraccionNombre: string
  fecha: string
  tiempoEspera?: number
}

export interface Favorito {
  atraccionId: string
  atraccionNombre: string
  fechaAgregado: string
}

export interface Notificacion {
  id: string
  tipo: 'INFO' | 'ALERTA' | 'PROMOCION' | 'MANTENIMIENTO' | 'CLIMA' | 'ESTADO_ATRACCION' | 'INCIDENTE' | 'COLA' | 'SHOW'
  titulo: string
  mensaje: string
  fecha: string
}

// =====================
// Queues
// =====================

export interface Cola {
  atraccionId: string
  atraccionNombre: string
  visitantesEnCola: number
  tiempoEsperaEstimado: number
  visitantes?: VisitanteCola[]
}

export interface VisitanteCola {
  documento: string
  nombre: string
  posicion: number
  horaIngreso: string
  esFastPass: boolean
}

export interface UnirseColaRequest {
  documentoVisitante: string
  atraccionId: string
  idAtraccion?: string
}

export interface PosicionCola {
  posicion: number
  tiempoEsperaEstimado: number
  atraccionNombre: string
}

// =====================
// Map & Routes
// =====================

export interface NodoMapa {
  id: string
  nombre: string
  tipo: TipoAtraccion | 'ENTRADA' | 'SALIDA' | 'SERVICIOS'
  estado?: EstadoAtraccion
  zonaId?: string
  zonaNombre?: string
  zonaDisponible?: boolean
  x: number
  y: number
}

export interface AristaMapa {
  origenId: string
  destinoId: string
  peso: number
  distanciaMetros?: number
  tiempoMinutos?: number
}

export interface MapaParque {
  nodos: NodoMapa[]
  aristas: AristaMapa[]
}

export interface Ruta {
  nodos: NodoMapa[]
  distanciaTotal: number
  tiempoEstimado: number
  pasos: PasoRuta[]
}

export interface PasoRuta {
  desde: string
  hacia: string
  distancia: number
  tiempo: number
}

// =====================
// Alerts
// =====================

export interface AlertaClima {
  id: string
  tipo: TipoAlertaClima
  fechaInicio: string
  fechaFin?: string
  activa: boolean
  mensaje: string
  atraccionesAfectadas: string[]
}

export interface AlertaMantenimiento {
  id: string
  atraccionId: string
  atraccionNombre: string
  tipo: 'PREVENTIVO' | 'CORRECTIVO' | 'URGENTE'
  descripcion: string
  fechaReporte: string
  fechaResolucion?: string
  resuelta: boolean
  prioridad: 'ALTA' | 'MEDIA' | 'BAJA'
}

export interface CrearAlertaClimaRequest {
  tipo: TipoAlertaClima
  tipoClima?: TipoAlertaClima
  mensaje?: string
}

// =====================
// Reports
// =====================

export interface ReporteJornada {
  fecha: string
  ingresosTotales: number
  ticketsVendidos: TicketsVendidosReporte
  atraccionesMasVisitadas: AtraccionVisitadaReporte[]
  tiemposEsperaPromedio: TiempoEsperaReporte[]
  cierresPorClima: CierreClimaReporte[]
  alertasMantenimiento: AlertaMantenimientoReporte[]
  incidentesOperativos: IncidenteReporte[]
}

export interface TicketsVendidosReporte {
  general: number
  familiar: number
  fastPass: number
  total: number
  ingresoTotal: number
}

export interface AtraccionVisitadaReporte {
  atraccionId: string
  nombre: string
  visitas: number
  porcentaje: number
}

export interface TiempoEsperaReporte {
  atraccionId: string
  nombre: string
  tiempoPromedio: number
  tiempoMaximo: number
}

export interface CierreClimaReporte {
  hora: string
  tipo: TipoAlertaClima
  duracionMinutos: number
  atraccionesAfectadas: number
}

export interface AlertaMantenimientoReporte {
  atraccionNombre: string
  tipo: string
  duracionMinutos: number
}

export interface IncidenteReporte {
  hora: string
  descripcion: string
  resuelto: boolean
}



export interface ShowParque {
  id: string
  nombre: string
  horario: string
  duracion: number
  estado: string
  mensaje: string
}

export interface UsuarioActivo {
  id: string
  nombre: string
  documento: string
  rol: Rol
  ticketActivo: boolean
  tipoTicket?: TipoTicket
  ubicacionActual?: string
  saldoVirtual?: number
}

export interface ActualizarVisitanteRequest {
  nombre?: string
  edad?: number
  estatura?: number
  saldoVirtual?: number
  password?: string
}

export interface ActualizarOperadorRequest {
  nombre?: string
  edad?: number
  password?: string
}

export interface ResolverIncidenteRequest {
  solucion: string
}

// =====================
// Data Loading
// =====================

export interface CargaDatosResponse {
  success: boolean
  mensaje: string
  registrosCargados?: number
  data?: string
}


export interface Operador {
  id: string
  nombre: string
  documento: string
  edad: number
  zonaAsignadaId?: string
  zonaAsignadaNombre?: string
  atraccionesAsignadas?: string[]
}

export interface CrearVisitanteRequest {
  nombre: string
  documento: string
  edad: number
  password?: string
  estatura: number
  saldoVirtual: number
}

export interface CrearOperadorRequest {
  nombre: string
  documento: string
  edad: number
  password?: string
  zonaId?: string
}

export interface CrearZonaRequest {
  id: string
  nombre: string
  capacidadMaxima: number
  disponible?: boolean
}

export interface ActualizarZonaRequest {
  nombre?: string
  capacidadMaxima?: number
  disponible?: boolean
}

export interface AristaAtraccionRequest {
  idDestino: string
  peso: number
}

export interface CrearAtraccionRequest {
  id: string
  nombre: string
  tipo: TipoAtraccion
  zonaId: string
  capacidadMaximaPorCiclo: number
  alturaMinima: number
  edadMinima: number
  costoAdicional: number
  estadoInicial: EstadoAtraccion
  tiempoEstimadoEspera?: number
  motivoCierre?: string
  aristas?: AristaAtraccionRequest[]
}

export interface ActualizarAtraccionRequest {
  nombre?: string
  tipo?: TipoAtraccion
  zonaId?: string
  capacidadMaximaPorCiclo?: number
  alturaMinima?: number
  edadMinima?: number
  costoAdicional?: number
  estado?: EstadoAtraccion
  motivoCierre?: string
  tiempoEstimadoEspera?: number
  aristas?: AristaAtraccionRequest[]
}

export interface IncidenteOperativo {
  id: string
  atraccionId: string
  atraccionNombre: string
  descripcion: string
  gravedad: string
  fechaHora: string
  resuelto: boolean
  solucion?: string
}

export interface CrearIncidenteRequest {
  idAtraccion: string
  descripcion: string
  gravedad: string
}
