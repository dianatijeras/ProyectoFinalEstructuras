'use client'

import { useEffect, useMemo, useState } from 'react'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { adminService } from '@/services/adminService'
import { visitanteService } from '@/services/visitanteService'
import type { Atraccion, EstadoAtraccion, Operador, TipoAtraccion, UsuarioActivo, Visitante, Zona } from '@/types'
import { Loader2, UserPlus, Wrench, Link2, AlertCircle, Search, MapPinned, Pencil, FerrisWheel, Users } from 'lucide-react'
import { toast } from 'sonner'

interface Props {
  zonas: Zona[]
  atracciones: Atraccion[]
  onChanged?: () => void
  seccion?: 'visitantes' | 'operadores' | 'gestion'
}

const tiposAtraccion: TipoAtraccion[] = ['ACUATICA', 'MECANICA_ALTURA', 'OTRA']
const estadosAtraccion: EstadoAtraccion[] = ['ACTIVA', 'EN_MANTENIMIENTO', 'CERRADA']

export function AdminManagementPanel({ zonas, atracciones, onChanged, seccion = 'gestion' }: Props) {
  const [operadores, setOperadores] = useState<Operador[]>([])
  const [visitantes, setVisitantes] = useState<Visitante[]>([])
  const [usuariosActivos, setUsuariosActivos] = useState<UsuarioActivo[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [busqueda, setBusqueda] = useState('')
  const [resultados, setResultados] = useState<Atraccion[]>([])

  const primeraZona = zonas[0]?.id || ''
  const primeraAtraccion = atracciones[0]?.id || ''

  const [visitante, setVisitante] = useState({ nombre: '', documento: '', edad: 18, password: '123', estatura: 1.6, saldoVirtual: 30000 })
  const [visitanteEdit, setVisitanteEdit] = useState({ documento: '', nombre: '', edad: 18, password: '', estatura: 1.6, saldoVirtual: 0 })
  const [operador, setOperador] = useState({ nombre: '', documento: '', edad: 25, password: '123', zonaId: primeraZona })
  const [operadorEdit, setOperadorEdit] = useState({ documento: '', nombre: '', edad: 25, password: '' })
  const [zonaNueva, setZonaNueva] = useState({ id: '', nombre: '', capacidadMaxima: 100, disponible: true })
  const [zonaEdit, setZonaEdit] = useState({ id: primeraZona, nombre: '', capacidadMaxima: 100, disponible: true })
  const [atraccionNueva, setAtraccionNueva] = useState({ id: '', nombre: '', tipo: 'OTRA' as TipoAtraccion, zonaId: primeraZona, capacidadMaximaPorCiclo: 10, alturaMinima: 1.0, edadMinima: 6, costoAdicional: 0, estadoInicial: 'ACTIVA' as EstadoAtraccion, tiempoEstimadoEspera: 5, motivoCierre: '', aristasTexto: '' })
  const [atraccionEdit, setAtraccionEdit] = useState({ id: primeraAtraccion, nombre: '', tipo: 'OTRA' as TipoAtraccion, zonaId: primeraZona, capacidadMaximaPorCiclo: 10, alturaMinima: 1.0, edadMinima: 6, costoAdicional: 0, estado: 'ACTIVA' as EstadoAtraccion, tiempoEstimadoEspera: 5, motivoCierre: '', aristasTexto: '' })
  const [asignacion, setAsignacion] = useState({ documentoOperador: '', zonaId: primeraZona, idAtraccion: primeraAtraccion })

  useEffect(() => { cargarTodo().catch(() => undefined) }, [])
  useEffect(() => {
    if (!operador.zonaId && primeraZona) setOperador((prev) => ({ ...prev, zonaId: primeraZona }))
    if (!zonaEdit.id && primeraZona) setZonaEdit((prev) => ({ ...prev, id: primeraZona }))
    if (!atraccionNueva.zonaId && primeraZona) setAtraccionNueva((prev) => ({ ...prev, zonaId: primeraZona }))
    if (!atraccionEdit.zonaId && primeraZona) setAtraccionEdit((prev) => ({ ...prev, zonaId: primeraZona }))
    if (!asignacion.zonaId && primeraZona) setAsignacion((prev) => ({ ...prev, zonaId: primeraZona }))
    if (!asignacion.idAtraccion && primeraAtraccion) setAsignacion((prev) => ({ ...prev, idAtraccion: primeraAtraccion }))
    if (!atraccionEdit.id && primeraAtraccion) setAtraccionEdit((prev) => ({ ...prev, id: primeraAtraccion }))
  }, [primeraZona, primeraAtraccion])
  useEffect(() => {
    const zona = zonas.find((z) => z.id === zonaEdit.id)
    if (zona) setZonaEdit((prev) => ({ ...prev, nombre: zona.nombre, capacidadMaxima: zona.capacidadMaxima, disponible: zona.disponible !== false }))
  }, [zonaEdit.id, zonas])
  useEffect(() => {
    const atraccion = atracciones.find((a) => a.id === atraccionEdit.id)
    if (atraccion) setAtraccionEdit((prev) => ({
      ...prev,
      nombre: atraccion.nombre,
      tipo: atraccion.tipo,
      zonaId: atraccion.zonaId || primeraZona,
      capacidadMaximaPorCiclo: atraccion.capacidadPorCiclo,
      alturaMinima: atraccion.alturaMinima || 0,
      edadMinima: atraccion.edadMinima || 0,
      costoAdicional: atraccion.costoAdicional || 0,
      estado: atraccion.estado,
      tiempoEstimadoEspera: atraccion.tiempoEsperaEstimado || 0,
      motivoCierre: '',
    }))
  }, [atraccionEdit.id, atracciones, primeraZona])
  useEffect(() => {
    const v = visitantes.find((x) => x.documento === visitanteEdit.documento)
    if (v) setVisitanteEdit({ documento: v.documento, nombre: v.nombre, edad: v.edad || 0, password: '', estatura: v.estatura || 0, saldoVirtual: v.saldoVirtual || 0 })
  }, [visitanteEdit.documento, visitantes])
  useEffect(() => {
    const op = operadores.find((x) => x.documento === operadorEdit.documento)
    if (op) setOperadorEdit({ documento: op.documento, nombre: op.nombre, edad: op.edad || 0, password: '' })
  }, [operadorEdit.documento, operadores])

  const parseAristas = (texto: string) => texto.split('\n').map((linea) => linea.trim()).filter(Boolean).map((linea) => {
    const [idDestino, pesoTexto] = linea.split(',').map((v) => v.trim())
    return { idDestino, peso: Number(pesoTexto) }
  }).filter((a) => a.idDestino && a.peso > 0)

  const cargarTodo = async () => {
    const [ops, vis, activos] = await Promise.all([
      adminService.listarOperadores(),
      visitanteService.getAll(),
      adminService.usuariosActivos(''),
    ])
    setOperadores(ops); setVisitantes(vis); setUsuariosActivos(activos)
  }

  const run = async (action: () => Promise<unknown>, ok: string) => {
    setLoading(true); setError(null)
    try {
      await action()
      toast.success(ok)
      await cargarTodo()
      onChanged?.()
      window.dispatchEvent(new Event('techpark:notifications-refresh'))
    } catch (err) {
      const msg = err instanceof Error ? err.message : 'No se pudo completar la acción'
      setError(msg); toast.error(msg)
    } finally { setLoading(false) }
  }

  const crearVisitante = () => {
    if (visitante.edad < 0 || visitante.estatura < 0 || visitante.saldoVirtual < 0) { setError('Edad, estatura y saldo no pueden ser negativos'); toast.error('Datos inválidos'); return }
    return run(() => adminService.crearVisitante(visitante), 'Visitante creado')
  }
  const modificarVisitante = () => {
    if (!visitanteEdit.documento) return
    if (visitanteEdit.edad < 0 || visitanteEdit.estatura < 0 || visitanteEdit.saldoVirtual < 0) { setError('Edad, estatura y saldo no pueden ser negativos'); toast.error('Datos inválidos'); return }
    return run(() => adminService.modificarVisitante(visitanteEdit.documento, visitanteEdit), 'Visitante actualizado')
  }
  const crearOperador = () => {
    if (operador.edad < 0) { setError('La edad del operador no puede ser negativa'); toast.error('Datos inválidos'); return }
    return run(() => adminService.crearOperador(operador), 'Operador creado')
  }
  const modificarOperador = () => {
    if (!operadorEdit.documento) return
    if (operadorEdit.edad < 0) { setError('La edad del operador no puede ser negativa'); toast.error('Datos inválidos'); return }
    return run(() => adminService.modificarOperador(operadorEdit.documento, operadorEdit), 'Operador actualizado')
  }
  const crearAtraccionPayload = useMemo(() => ({
    id: atraccionNueva.id, nombre: atraccionNueva.nombre, tipo: atraccionNueva.tipo, zonaId: atraccionNueva.zonaId,
    capacidadMaximaPorCiclo: atraccionNueva.capacidadMaximaPorCiclo, alturaMinima: atraccionNueva.alturaMinima,
    edadMinima: atraccionNueva.edadMinima, costoAdicional: atraccionNueva.costoAdicional, estadoInicial: atraccionNueva.estadoInicial,
    tiempoEstimadoEspera: atraccionNueva.tiempoEstimadoEspera, motivoCierre: atraccionNueva.motivoCierre || undefined, aristas: parseAristas(atraccionNueva.aristasTexto),
  }), [atraccionNueva])
  const buscar = async () => setResultados(await adminService.buscarAtracciones(busqueda))

  const visitantesFiltrados = visitantes.filter(v => `${v.nombre} ${v.documento}`.toLowerCase().includes(busqueda.toLowerCase()))
  const operadoresFiltrados = operadores.filter(o => `${o.nombre} ${o.documento}`.toLowerCase().includes(busqueda.toLowerCase()))

  return (
    <div className="space-y-6">
      {error && <Alert variant="destructive"><AlertCircle className="h-4 w-4" /><AlertDescription>{error}</AlertDescription></Alert>}

      {seccion === 'visitantes' && (
        <>
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <Card className="border-0 shadow-sm">
              <CardHeader><CardTitle className="flex gap-2 items-center"><UserPlus className="h-5 w-5" />Crear visitante</CardTitle><CardDescription>El saldo no puede ser negativo</CardDescription></CardHeader>
              <CardContent className="space-y-3">
                <Label>Nombre del visitante</Label><Input placeholder="Ej: Ana Pérez" value={visitante.nombre} onChange={(e) => setVisitante({ ...visitante, nombre: e.target.value })} />
                <Label>Documento</Label><Input placeholder="Ej: 1001" value={visitante.documento} onChange={(e) => setVisitante({ ...visitante, documento: e.target.value })} />
                <div className="grid grid-cols-3 gap-2">
                  <div><Label>Edad</Label><Input type="number" min={0} value={visitante.edad} onChange={(e) => setVisitante({ ...visitante, edad: Number(e.target.value) })} /></div>
                  <div><Label>Estatura</Label><Input type="number" min={0} step="0.01" value={visitante.estatura} onChange={(e) => setVisitante({ ...visitante, estatura: Number(e.target.value) })} /></div>
                  <div><Label>Saldo virtual</Label><Input type="number" min={0} value={visitante.saldoVirtual} onChange={(e) => setVisitante({ ...visitante, saldoVirtual: Number(e.target.value) })} /></div>
                </div>
                <Label>Contraseña o clave</Label><Input value={visitante.password} onChange={(e) => setVisitante({ ...visitante, password: e.target.value })} />
                <Button disabled={loading} className="w-full" onClick={crearVisitante}>{loading ? <Loader2 className="h-4 w-4 animate-spin" /> : 'Crear visitante'}</Button>
              </CardContent>
            </Card>
            <Card className="border-0 shadow-sm">
              <CardHeader><CardTitle>Modificar visitante</CardTitle></CardHeader>
              <CardContent className="space-y-3">
                <Select value={visitanteEdit.documento} onValueChange={(documento) => setVisitanteEdit({ ...visitanteEdit, documento })}><SelectTrigger><SelectValue placeholder="Seleccione visitante" /></SelectTrigger><SelectContent>{visitantes.map(v => <SelectItem key={v.documento} value={v.documento}>{v.nombre} ({v.documento})</SelectItem>)}</SelectContent></Select>
                <Label>Nombre</Label><Input value={visitanteEdit.nombre} onChange={(e) => setVisitanteEdit({ ...visitanteEdit, nombre: e.target.value })} />
                <div className="grid grid-cols-3 gap-2">
                  <div><Label>Edad</Label><Input type="number" min={0} value={visitanteEdit.edad} onChange={(e) => setVisitanteEdit({ ...visitanteEdit, edad: Number(e.target.value) })} /></div>
                  <div><Label>Estatura</Label><Input type="number" min={0} step="0.01" value={visitanteEdit.estatura} onChange={(e) => setVisitanteEdit({ ...visitanteEdit, estatura: Number(e.target.value) })} /></div>
                  <div><Label>Saldo</Label><Input type="number" min={0} value={visitanteEdit.saldoVirtual} onChange={(e) => setVisitanteEdit({ ...visitanteEdit, saldoVirtual: Number(e.target.value) })} /></div>
                </div>
                <Label>Nueva clave opcional</Label><Input value={visitanteEdit.password} onChange={(e) => setVisitanteEdit({ ...visitanteEdit, password: e.target.value })} />
                <Button disabled={loading || !visitanteEdit.documento} className="w-full" onClick={modificarVisitante}>Modificar visitante</Button>
              </CardContent>
            </Card>
          </div>
          <Card className="border-0 shadow-sm">
            <CardHeader><CardTitle>Lista y búsqueda de visitantes/usuarios activos</CardTitle></CardHeader>
            <CardContent className="space-y-3">
              <Input placeholder="Buscar por nombre o documento" value={busqueda} onChange={(e) => setBusqueda(e.target.value)} />
              <div className="grid grid-cols-1 md:grid-cols-2 gap-2 text-sm">
                {visitantesFiltrados.map(v => <div key={v.documento} className="p-3 rounded border bg-gray-50"><p className="font-medium">{v.nombre} · {v.documento}</p><p>Ticket: {v.ticketActivo ? (v.ticketActivo.activo ? 'Activo' : v.ticketActivo.estado === 'EXPIRADO' ? 'Expirado' : 'Inactivo') : 'Inactivo'}</p><p>Saldo: ${(v.saldoVirtual || 0).toLocaleString('es-CO')}</p><p>Ubicación: {v.ubicacionActual || 'Sin visita registrada'}</p></div>)}
              </div>
              <p className="text-xs text-gray-500">Usuarios activos con ticket: {usuariosActivos.filter(u => u.ticketActivo).length}</p>
            </CardContent>
          </Card>
        </>
      )}

      {seccion === 'operadores' && (
        <>
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <Card className="border-0 shadow-sm">
              <CardHeader><CardTitle className="flex gap-2 items-center"><Wrench className="h-5 w-5" />Crear operador</CardTitle></CardHeader>
              <CardContent className="space-y-3">
                <Label>Nombre del operador</Label><Input value={operador.nombre} onChange={(e) => setOperador({ ...operador, nombre: e.target.value })} />
                <Label>Documento</Label><Input value={operador.documento} onChange={(e) => setOperador({ ...operador, documento: e.target.value })} />
                <Label>Edad</Label><Input type="number" min={0} value={operador.edad} onChange={(e) => setOperador({ ...operador, edad: Number(e.target.value) })} />
                <Label>Contraseña o clave</Label><Input value={operador.password} onChange={(e) => setOperador({ ...operador, password: e.target.value })} />
                <Label>Zona asignada</Label><Select value={operador.zonaId} onValueChange={(zonaId) => setOperador({ ...operador, zonaId })}><SelectTrigger><SelectValue placeholder="Zona" /></SelectTrigger><SelectContent>{zonas.map(z => <SelectItem key={z.id} value={z.id}>{z.nombre}</SelectItem>)}</SelectContent></Select>
                <Button disabled={loading} className="w-full" onClick={crearOperador}>Crear operador</Button>
              </CardContent>
            </Card>
            <Card className="border-0 shadow-sm">
              <CardHeader><CardTitle>Modificar operador</CardTitle></CardHeader>
              <CardContent className="space-y-3">
                <Select value={operadorEdit.documento} onValueChange={(documento) => setOperadorEdit({ ...operadorEdit, documento })}><SelectTrigger><SelectValue placeholder="Seleccione operador" /></SelectTrigger><SelectContent>{operadores.map(o => <SelectItem key={o.documento} value={o.documento}>{o.nombre} ({o.documento})</SelectItem>)}</SelectContent></Select>
                <Label>Nombre</Label><Input value={operadorEdit.nombre} onChange={(e) => setOperadorEdit({ ...operadorEdit, nombre: e.target.value })} />
                <Label>Edad</Label><Input type="number" min={0} value={operadorEdit.edad} onChange={(e) => setOperadorEdit({ ...operadorEdit, edad: Number(e.target.value) })} />
                <Label>Nueva clave opcional</Label><Input value={operadorEdit.password} onChange={(e) => setOperadorEdit({ ...operadorEdit, password: e.target.value })} />
                <Button disabled={loading || !operadorEdit.documento} className="w-full" onClick={modificarOperador}>Modificar operador</Button>
              </CardContent>
            </Card>
          </div>

          <Card className="border-0 shadow-sm">
            <CardHeader><CardTitle className="flex items-center gap-2"><Link2 className="h-5 w-5" />Asignación de operadores</CardTitle><CardDescription>Asignar, remover o cambiar zona del operador</CardDescription></CardHeader>
            <CardContent className="grid grid-cols-1 md:grid-cols-5 gap-3">
              <Select value={asignacion.documentoOperador} onValueChange={(documentoOperador) => setAsignacion({ ...asignacion, documentoOperador })}><SelectTrigger><SelectValue placeholder="Operador" /></SelectTrigger><SelectContent>{operadores.map(o => <SelectItem key={o.documento} value={o.documento}>{o.nombre} ({o.documento})</SelectItem>)}</SelectContent></Select>
              <Select value={asignacion.zonaId} onValueChange={(zonaId) => setAsignacion({ ...asignacion, zonaId })}><SelectTrigger><SelectValue placeholder="Zona" /></SelectTrigger><SelectContent>{zonas.map(z => <SelectItem key={z.id} value={z.id}>{z.nombre}</SelectItem>)}</SelectContent></Select>
              <Button disabled={loading || !asignacion.documentoOperador || !asignacion.zonaId} onClick={() => run(() => adminService.asignarOperadorZona(asignacion.documentoOperador, asignacion.zonaId), 'Operador asignado a zona')}>Asignar/cambiar zona</Button>
              <Button variant="outline" disabled={loading || !asignacion.documentoOperador} onClick={() => run(() => adminService.removerOperadorZona(asignacion.documentoOperador), 'Operador removido de zona')}>Remover zona</Button>
              <Select value={asignacion.idAtraccion} onValueChange={(idAtraccion) => setAsignacion({ ...asignacion, idAtraccion })}><SelectTrigger><SelectValue placeholder="Atracción" /></SelectTrigger><SelectContent>{atracciones.map(a => <SelectItem key={a.id} value={a.id}>{a.nombre}</SelectItem>)}</SelectContent></Select>
              <Button disabled={loading || !asignacion.documentoOperador || !asignacion.idAtraccion} onClick={() => run(() => adminService.asignarOperadorAtraccion(asignacion.documentoOperador, asignacion.idAtraccion), 'Operador asignado a atracción')}>Asignar atracción</Button>
            </CardContent>
          </Card>

          <Card className="border-0 shadow-sm">
            <CardHeader><CardTitle className="flex items-center gap-2"><Users className="h-5 w-5" />Operadores registrados</CardTitle></CardHeader>
            <CardContent className="space-y-3">
              <Input placeholder="Buscar operador" value={busqueda} onChange={(e) => setBusqueda(e.target.value)} />
              {operadoresFiltrados.map((op) => (
                <div key={op.documento} className="rounded border bg-gray-50 p-3 text-sm">
                  <p className="font-medium">{op.nombre} · {op.documento}</p>
                  <p>Zona: {op.zonaAsignadaNombre || 'Sin zona'}</p>
                  <p>Atracciones: {op.atraccionesAsignadas && op.atraccionesAsignadas.length > 0 ? op.atraccionesAsignadas.join(', ') : 'Según zona asignada'}</p>
                </div>
              ))}
            </CardContent>
          </Card>
        </>
      )}

      {seccion === 'gestion' && (
        <>
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <Card className="border-0 shadow-sm">
              <CardHeader><CardTitle className="flex gap-2 items-center"><MapPinned className="h-5 w-5" />Crear zona</CardTitle></CardHeader>
              <CardContent className="space-y-3">
                <Label>ID de la zona</Label><Input value={zonaNueva.id} onChange={(e) => setZonaNueva({ ...zonaNueva, id: e.target.value })} />
                <Label>Nombre de la zona</Label><Input value={zonaNueva.nombre} onChange={(e) => setZonaNueva({ ...zonaNueva, nombre: e.target.value })} />
                <Label>Capacidad máxima</Label><Input type="number" min={1} value={zonaNueva.capacidadMaxima} onChange={(e) => setZonaNueva({ ...zonaNueva, capacidadMaxima: Number(e.target.value) })} />
                <Label>Estado o disponibilidad</Label><Select value={zonaNueva.disponible ? 'DISPONIBLE' : 'NO_DISPONIBLE'} onValueChange={(value) => setZonaNueva({ ...zonaNueva, disponible: value === 'DISPONIBLE' })}><SelectTrigger><SelectValue /></SelectTrigger><SelectContent><SelectItem value="DISPONIBLE">DISPONIBLE</SelectItem><SelectItem value="NO_DISPONIBLE">NO_DISPONIBLE</SelectItem></SelectContent></Select>
                <Button disabled={loading} className="w-full" onClick={() => run(() => adminService.crearZona(zonaNueva), 'Zona creada')}>Crear zona</Button>
              </CardContent>
            </Card>
            <Card className="border-0 shadow-sm">
              <CardHeader><CardTitle className="flex gap-2 items-center"><Pencil className="h-5 w-5" />Modificar zona</CardTitle></CardHeader>
              <CardContent className="space-y-3">
                <Label>Zona a modificar</Label><Select value={zonaEdit.id} onValueChange={(id) => setZonaEdit({ ...zonaEdit, id })}><SelectTrigger><SelectValue /></SelectTrigger><SelectContent>{zonas.map(z => <SelectItem key={z.id} value={z.id}>{z.nombre}</SelectItem>)}</SelectContent></Select>
                <Label>Nombre de la zona</Label><Input value={zonaEdit.nombre} onChange={(e) => setZonaEdit({ ...zonaEdit, nombre: e.target.value })} />
                <Label>Capacidad máxima</Label><Input type="number" min={1} value={zonaEdit.capacidadMaxima} onChange={(e) => setZonaEdit({ ...zonaEdit, capacidadMaxima: Number(e.target.value) })} />
                <Label>Estado o disponibilidad</Label><Select value={zonaEdit.disponible ? 'DISPONIBLE' : 'NO_DISPONIBLE'} onValueChange={(value) => setZonaEdit({ ...zonaEdit, disponible: value === 'DISPONIBLE' })}><SelectTrigger><SelectValue /></SelectTrigger><SelectContent><SelectItem value="DISPONIBLE">DISPONIBLE</SelectItem><SelectItem value="NO_DISPONIBLE">NO_DISPONIBLE</SelectItem></SelectContent></Select>
                <Button disabled={loading || !zonaEdit.id} className="w-full" onClick={() => run(() => adminService.modificarZona(zonaEdit.id, { nombre: zonaEdit.nombre, capacidadMaxima: zonaEdit.capacidadMaxima, disponible: zonaEdit.disponible }), 'Zona actualizada')}>Modificar zona</Button>
              </CardContent>
            </Card>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <Card className="border-0 shadow-sm">
              <CardHeader><CardTitle className="flex gap-2 items-center"><FerrisWheel className="h-5 w-5" />Crear atracción</CardTitle><CardDescription>Aristas: una por línea, formato ATR-001,5</CardDescription></CardHeader>
              <CardContent className="space-y-3">
                <div className="grid grid-cols-2 gap-2"><div><Label>ID de la atracción</Label><Input value={atraccionNueva.id} onChange={(e) => setAtraccionNueva({ ...atraccionNueva, id: e.target.value })} /></div><div><Label>Nombre de la atracción</Label><Input value={atraccionNueva.nombre} onChange={(e) => setAtraccionNueva({ ...atraccionNueva, nombre: e.target.value })} /></div></div>
                <div className="grid grid-cols-2 gap-2"><div><Label>Tipo de atracción</Label><Select value={atraccionNueva.tipo} onValueChange={(tipo) => setAtraccionNueva({ ...atraccionNueva, tipo: tipo as TipoAtraccion })}><SelectTrigger><SelectValue /></SelectTrigger><SelectContent>{tiposAtraccion.map(t => <SelectItem key={t} value={t}>{t}</SelectItem>)}</SelectContent></Select></div><div><Label>Zona</Label><Select value={atraccionNueva.zonaId} onValueChange={(zonaId) => setAtraccionNueva({ ...atraccionNueva, zonaId })}><SelectTrigger><SelectValue /></SelectTrigger><SelectContent>{zonas.map(z => <SelectItem key={z.id} value={z.id}>{z.nombre}</SelectItem>)}</SelectContent></Select></div></div>
                <div className="grid grid-cols-3 gap-2"><div><Label>Capacidad máxima por ciclo</Label><Input type="number" min={1} value={atraccionNueva.capacidadMaximaPorCiclo} onChange={(e) => setAtraccionNueva({ ...atraccionNueva, capacidadMaximaPorCiclo: Number(e.target.value) })} /></div><div><Label>Altura mínima requerida</Label><Input type="number" min={0} step="0.01" value={atraccionNueva.alturaMinima} onChange={(e) => setAtraccionNueva({ ...atraccionNueva, alturaMinima: Number(e.target.value) })} /></div><div><Label>Edad mínima requerida</Label><Input type="number" min={0} value={atraccionNueva.edadMinima} onChange={(e) => setAtraccionNueva({ ...atraccionNueva, edadMinima: Number(e.target.value) })} /></div></div>
                <div className="grid grid-cols-3 gap-2"><div><Label>Costo adicional</Label><Input type="number" min={0} value={atraccionNueva.costoAdicional} onChange={(e) => setAtraccionNueva({ ...atraccionNueva, costoAdicional: Number(e.target.value) })} /></div><div><Label>Tiempo estimado de espera</Label><Input type="number" min={0} value={atraccionNueva.tiempoEstimadoEspera} onChange={(e) => setAtraccionNueva({ ...atraccionNueva, tiempoEstimadoEspera: Number(e.target.value) })} /></div><div><Label>Estado inicial</Label><Select value={atraccionNueva.estadoInicial} onValueChange={(estadoInicial) => setAtraccionNueva({ ...atraccionNueva, estadoInicial: estadoInicial as EstadoAtraccion })}><SelectTrigger><SelectValue /></SelectTrigger><SelectContent>{estadosAtraccion.map(e => <SelectItem key={e} value={e}>{e}</SelectItem>)}</SelectContent></Select></div></div>
                <Label>Motivo de cierre o mantenimiento</Label><Input value={atraccionNueva.motivoCierre} onChange={(e) => setAtraccionNueva({ ...atraccionNueva, motivoCierre: e.target.value })} />
                <Label>Atracciones conectadas y peso/distancia</Label><textarea className="w-full min-h-20 rounded-md border border-input bg-background px-3 py-2 text-sm" placeholder={'ATR-001,5\nATR-003,7'} value={atraccionNueva.aristasTexto} onChange={(e) => setAtraccionNueva({ ...atraccionNueva, aristasTexto: e.target.value })} />
                <Button disabled={loading || !atraccionNueva.id || !atraccionNueva.nombre} className="w-full" onClick={() => run(() => adminService.crearAtraccion(crearAtraccionPayload), 'Atracción creada')}>Crear atracción</Button>
              </CardContent>
            </Card>
            <Card className="border-0 shadow-sm">
              <CardHeader><CardTitle className="flex gap-2 items-center"><Pencil className="h-5 w-5" />Modificar atracción</CardTitle></CardHeader>
              <CardContent className="space-y-3">
                <Label>Atracción</Label><Select value={atraccionEdit.id} onValueChange={(id) => setAtraccionEdit({ ...atraccionEdit, id })}><SelectTrigger><SelectValue /></SelectTrigger><SelectContent>{atracciones.map(a => <SelectItem key={a.id} value={a.id}>{a.nombre}</SelectItem>)}</SelectContent></Select>
                <Label>Nombre</Label><Input value={atraccionEdit.nombre} onChange={(e) => setAtraccionEdit({ ...atraccionEdit, nombre: e.target.value })} />
                <div className="grid grid-cols-2 gap-2"><Select value={atraccionEdit.tipo} onValueChange={(tipo) => setAtraccionEdit({ ...atraccionEdit, tipo: tipo as TipoAtraccion })}><SelectTrigger><SelectValue /></SelectTrigger><SelectContent>{tiposAtraccion.map(t => <SelectItem key={t} value={t}>{t}</SelectItem>)}</SelectContent></Select><Select value={atraccionEdit.zonaId} onValueChange={(zonaId) => setAtraccionEdit({ ...atraccionEdit, zonaId })}><SelectTrigger><SelectValue /></SelectTrigger><SelectContent>{zonas.map(z => <SelectItem key={z.id} value={z.id}>{z.nombre}</SelectItem>)}</SelectContent></Select></div>
                <div className="grid grid-cols-3 gap-2"><Input type="number" min={1} value={atraccionEdit.capacidadMaximaPorCiclo} onChange={(e) => setAtraccionEdit({ ...atraccionEdit, capacidadMaximaPorCiclo: Number(e.target.value) })} /><Input type="number" min={0} step="0.01" value={atraccionEdit.alturaMinima} onChange={(e) => setAtraccionEdit({ ...atraccionEdit, alturaMinima: Number(e.target.value) })} /><Input type="number" min={0} value={atraccionEdit.edadMinima} onChange={(e) => setAtraccionEdit({ ...atraccionEdit, edadMinima: Number(e.target.value) })} /></div>
                <div className="grid grid-cols-3 gap-2"><Input type="number" min={0} value={atraccionEdit.costoAdicional} onChange={(e) => setAtraccionEdit({ ...atraccionEdit, costoAdicional: Number(e.target.value) })} /><Input type="number" min={0} value={atraccionEdit.tiempoEstimadoEspera} onChange={(e) => setAtraccionEdit({ ...atraccionEdit, tiempoEstimadoEspera: Number(e.target.value) })} /><Select value={atraccionEdit.estado} onValueChange={(estado) => setAtraccionEdit({ ...atraccionEdit, estado: estado as EstadoAtraccion })}><SelectTrigger><SelectValue /></SelectTrigger><SelectContent>{estadosAtraccion.map(e => <SelectItem key={e} value={e}>{e}</SelectItem>)}</SelectContent></Select></div>
                <Input placeholder="Motivo si aplica" value={atraccionEdit.motivoCierre} onChange={(e) => setAtraccionEdit({ ...atraccionEdit, motivoCierre: e.target.value })} />
                <textarea className="w-full min-h-20 rounded-md border border-input bg-background px-3 py-2 text-sm" placeholder={'Agregar aristas nuevas, formato ATR-001,5'} value={atraccionEdit.aristasTexto} onChange={(e) => setAtraccionEdit({ ...atraccionEdit, aristasTexto: e.target.value })} />
                <Button disabled={loading || !atraccionEdit.id} className="w-full" onClick={() => run(() => adminService.modificarAtraccion(atraccionEdit.id, { nombre: atraccionEdit.nombre, tipo: atraccionEdit.tipo, zonaId: atraccionEdit.zonaId, capacidadMaximaPorCiclo: atraccionEdit.capacidadMaximaPorCiclo, alturaMinima: atraccionEdit.alturaMinima, edadMinima: atraccionEdit.edadMinima, costoAdicional: atraccionEdit.costoAdicional, estado: atraccionEdit.estado, motivoCierre: atraccionEdit.motivoCierre || undefined, tiempoEstimadoEspera: atraccionEdit.tiempoEstimadoEspera, aristas: parseAristas(atraccionEdit.aristasTexto) }), 'Atracción actualizada')}>Modificar atracción</Button>
              </CardContent>
            </Card>
          </div>
          <Card className="border-0 shadow-sm">
            <CardHeader><CardTitle className="flex items-center gap-2"><Search className="h-5 w-5" />Búsqueda con ABB</CardTitle><CardDescription>Busca atracciones registradas en la estructura de búsqueda del backend</CardDescription></CardHeader>
            <CardContent className="space-y-3"><div className="flex gap-2"><Input placeholder="Buscar por ID o nombre" value={busqueda} onChange={(e) => setBusqueda(e.target.value)} /><Button onClick={buscar}>Buscar</Button></div><div className="grid grid-cols-1 md:grid-cols-2 gap-2 text-sm">{resultados.map(a => <div key={a.id} className="p-2 rounded border bg-gray-50">{a.id} - {a.nombre} ({a.estado})</div>)}</div></CardContent>
          </Card>
        </>
      )}
    </div>
  )
}
