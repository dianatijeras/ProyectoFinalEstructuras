'use client'

import { useState, useEffect } from 'react'
import { useAuth } from '@/contexts/AuthContext'
import { ZonesPanel } from '@/components/ZonesPanel'
import { AttractionsTable } from '@/components/AttractionsTable'
import { WeatherAlertPanel } from '@/components/WeatherAlertPanel'
import { MaintenanceAlertsPanel } from '@/components/MaintenanceAlertsPanel'
import { DataLoader } from '@/components/DataLoader'
import { AdminManagementPanel } from '@/components/AdminManagementPanel'
import { IncidentForm } from '@/components/IncidentForm'
import { parqueService } from '@/services/parqueService'
import { atraccionService } from '@/services/atraccionService'
import { alertasService } from '@/services/alertasService'
import { incidentesService } from '@/services/incidentesService'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Skeleton } from '@/components/ui/skeleton'
import { 
  Settings, 
  MapPin, 
  Ticket, 
  CloudRain,
  Wrench,
  Database,
  UserPlus,
  AlertTriangle,
  Loader2,
  Shield,
  CloudLightning
} from 'lucide-react'
import type { Zona, Atraccion, AlertaClima, AlertaMantenimiento, TipoAlertaClima, IncidenteOperativo } from '@/types'
import { toast } from 'sonner'

export default function AdminPage() {
  const { user } = useAuth()
  const [zonas, setZonas] = useState<Zona[]>([])
  const [atracciones, setAtracciones] = useState<Atraccion[]>([])
  const [alertasClima, setAlertasClima] = useState<AlertaClima[]>([])
  const [alertasMantenimiento, setAlertasMantenimiento] = useState<AlertaMantenimiento[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [selectedAtraccion, setSelectedAtraccion] = useState<Atraccion | null>(null)
  const [incidentes, setIncidentes] = useState<IncidenteOperativo[]>([])
  const [soluciones, setSoluciones] = useState<Record<string, string>>({})

  // Weather alert form
  const [selectedAlertType, setSelectedAlertType] = useState<TipoAlertaClima>('LLUVIA_FUERTE')
  const [isCreatingAlert, setIsCreatingAlert] = useState(false)

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    setIsLoading(true)
    setError(null)

    try {
      const [zonasData, atraccionesData, climaData, mantenimientoData, incidentesData] = await Promise.all([
        parqueService.getZonas(),
        atraccionService.getAll(),
        alertasService.getAlertasClima(),
        alertasService.getAlertasMantenimiento(),
        incidentesService.listar(),
      ])

      setZonas(zonasData)
      setAtracciones(atraccionesData)
      setAlertasClima(climaData)
      setAlertasMantenimiento(mantenimientoData)
      setIncidentes(incidentesData)
    } catch (err) {
      console.error('Error loading admin data:', err)
      setError('No se pudo conectar con el servidor.')
    } finally {
      setIsLoading(false)
    }
  }

  const handleCreateWeatherAlert = async () => {
    setIsCreatingAlert(true)

    try {
      const result = await alertasService.crearAlertaClima({
        tipo: selectedAlertType,
        mensaje: selectedAlertType === 'LLUVIA_FUERTE' 
          ? 'Alerta de lluvia fuerte en el parque'
          : 'Alerta de tormenta eléctrica - Evacuar zonas expuestas',
      })

      toast.success(`Alerta de ${selectedAlertType === 'LLUVIA_FUERTE' ? 'lluvia fuerte' : 'tormenta eléctrica'} creada`)
      
      if (result.atraccionesAfectadas && result.atraccionesAfectadas.length > 0) {
        toast.info(`${result.atraccionesAfectadas.length} atracciones afectadas`)
      }

      loadData()
      window.dispatchEvent(new Event('techpark:notifications-refresh'))
    } catch (err) {
      toast.error('Error al crear la alerta')
    } finally {
      setIsCreatingAlert(false)
    }
  }

  const handleFinalizeWeatherAlert = async (id: string) => {
    try {
      await alertasService.finalizarAlertaClima(id)
      toast.success('Alerta climatica finalizada')
      loadData()
      window.dispatchEvent(new Event('techpark:notifications-refresh'))
    } catch (err) {
      toast.error('Error al finalizar la alerta climatica')
    }
  }

  const handleDataLoaded = () => {
    loadData()
    toast.success('Datos cargados exitosamente')
  }

  const handleResolverIncidente = async (id: string) => {
    try {
      await incidentesService.resolver(id, { solucion: soluciones[id] || 'Incidente resuelto por administración' })
      toast.success('Incidente resuelto')
      loadData()
      window.dispatchEvent(new Event('techpark:notifications-refresh'))
    } catch {
      toast.error('No se pudo resolver el incidente')
    }
  }

  if (isLoading) {
    return <AdminSkeleton />
  }

  const activeAlerts = alertasClima.filter(a => a.activa)
  const pendingMaintenance = alertasMantenimiento.filter(a => !a.resuelta)

  return (
    <div className="space-y-6">
      {/* Error Alert */}
      {error && (
        <Alert className="bg-yellow-50 border-yellow-200">
          <AlertTriangle className="h-4 w-4 text-yellow-600" />
          <AlertDescription className="text-yellow-800">
            {error}
          </AlertDescription>
        </Alert>
      )}

      {/* Admin Info Card */}
      <Card className="border-0 shadow-sm bg-gradient-to-r from-red-500 to-red-600 text-white">
        <CardContent className="p-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <div className="w-16 h-16 rounded-full bg-white/20 flex items-center justify-center">
                <Shield className="h-8 w-8" />
              </div>
              <div>
                <h2 className="text-2xl font-bold">{user?.nombre || 'Administrador'}</h2>
                <p className="text-red-100">Panel de Administración</p>
              </div>
            </div>
            <div className="hidden md:flex gap-6">
              <div className="text-center">
                <p className="text-3xl font-bold">{zonas.length}</p>
                <p className="text-sm text-red-100">Zonas</p>
              </div>
              <div className="text-center">
                <p className="text-3xl font-bold">{atracciones.length}</p>
                <p className="text-sm text-red-100">Atracciones</p>
              </div>
              <div className="text-center">
                <p className="text-3xl font-bold">{activeAlerts.length}</p>
                <p className="text-sm text-red-100">Alertas Activas</p>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Main Tabs */}
      <Tabs defaultValue="overview" className="space-y-4">
        <TabsList className="flex flex-wrap">
          <TabsTrigger value="overview" className="flex items-center gap-1">
            <Settings className="h-4 w-4" />
            General
          </TabsTrigger>
          <TabsTrigger value="zones" className="flex items-center gap-1">
            <MapPin className="h-4 w-4" />
            Zonas
          </TabsTrigger>
          <TabsTrigger value="attractions" className="flex items-center gap-1">
            <Ticket className="h-4 w-4" />
            Atracciones
          </TabsTrigger>
          <TabsTrigger value="visitor-admin" className="flex items-center gap-1">
            <UserPlus className="h-4 w-4" />
            Visitante
          </TabsTrigger>
          <TabsTrigger value="operator-admin" className="flex items-center gap-1">
            <Wrench className="h-4 w-4" />
            Operador
          </TabsTrigger>
          <TabsTrigger value="management" className="flex items-center gap-1">
            <Settings className="h-4 w-4" />
            Gestión
          </TabsTrigger>
          <TabsTrigger value="weather" className="flex items-center gap-1">
            <CloudRain className="h-4 w-4" />
            Clima
          </TabsTrigger>
          <TabsTrigger value="maintenance" className="flex items-center gap-1">
            <Wrench className="h-4 w-4" />
            Mantenimiento
          </TabsTrigger>
          <TabsTrigger value="data" className="flex items-center gap-1">
            <Database className="h-4 w-4" />
            Datos
          </TabsTrigger>
        </TabsList>

        <TabsContent value="overview" className="space-y-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Weather Alert Creator */}
            <Card className="border-0 shadow-sm">
              <CardHeader>
                <div className="flex items-center gap-2">
                  <CloudLightning className="h-5 w-5 text-purple-600" />
                  <CardTitle className="text-lg">Disparar Alerta Climática</CardTitle>
                </div>
                <CardDescription>
                  Cree una alerta climática para cerrar atracciones afectadas
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <Select 
                  value={selectedAlertType} 
                  onValueChange={(v) => setSelectedAlertType(v as TipoAlertaClima)}
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="LLUVIA_FUERTE">
                      <div className="flex items-center gap-2">
                        <CloudRain className="h-4 w-4 text-blue-500" />
                        Lluvia Fuerte
                      </div>
                    </SelectItem>
                    <SelectItem value="TORMENTA_ELECTRICA">
                      <div className="flex items-center gap-2">
                        <CloudLightning className="h-4 w-4 text-purple-500" />
                        Tormenta Eléctrica
                      </div>
                    </SelectItem>
                  </SelectContent>
                </Select>

                <div className="p-3 bg-yellow-50 rounded-lg text-sm text-yellow-700">
                  <p className="font-medium">Advertencia:</p>
                  <p>Esta acción cerrará automáticamente las atracciones expuestas al clima seleccionado.</p>
                </div>

                <Button 
                  onClick={handleCreateWeatherAlert}
                  className="w-full bg-purple-600 hover:bg-purple-700"
                  disabled={isCreatingAlert}
                >
                  {isCreatingAlert ? (
                    <>
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                      Creando alerta...
                    </>
                  ) : (
                    <>
                      <CloudLightning className="mr-2 h-4 w-4" />
                      Disparar Alerta
                    </>
                  )}
                </Button>
              </CardContent>
            </Card>

            {/* Active Alerts Summary */}
            <Card className="border-0 shadow-sm">
              <CardHeader>
                <CardTitle className="text-lg">Resumen de Alertas</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div className="p-4 bg-blue-50 rounded-lg text-center">
                    <CloudRain className="h-8 w-8 text-blue-600 mx-auto mb-2" />
                    <p className="text-2xl font-bold text-blue-700">{activeAlerts.length}</p>
                    <p className="text-sm text-blue-600">Alertas Clima</p>
                  </div>
                  <div className="p-4 bg-orange-50 rounded-lg text-center">
                    <Wrench className="h-8 w-8 text-orange-600 mx-auto mb-2" />
                    <p className="text-2xl font-bold text-orange-700">{pendingMaintenance.length}</p>
                    <p className="text-sm text-orange-600">Mantenimiento</p>
                  </div>
                </div>
                
                {activeAlerts.length > 0 && (
                  <div className="space-y-2">
                    <p className="text-sm font-medium text-gray-700">Alertas activas:</p>
                    {activeAlerts.map((alert) => (
                      <div 
                        key={alert.id}
                        className="p-2 bg-gray-50 rounded text-sm flex items-center justify-between"
                      >
                        <span>{alert.tipo === 'LLUVIA_FUERTE' ? 'Lluvia Fuerte' : 'Tormenta'}</span>
                        <div className="flex items-center gap-2">
                          <span className="text-gray-500">
                            {alert.atraccionesAfectadas?.length || 0} afectadas
                          </span>
                          <Button size="sm" variant="outline" onClick={() => handleFinalizeWeatherAlert(alert.id)}>
                            Finalizar
                          </Button>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </CardContent>
            </Card>
          </div>
        </TabsContent>

        <TabsContent value="zones">
          <ZonesPanel zonas={zonas} />
        </TabsContent>

        <TabsContent value="visitor-admin">
          <AdminManagementPanel zonas={zonas} atracciones={atracciones} onChanged={loadData} seccion="visitantes" />
        </TabsContent>

        <TabsContent value="operator-admin">
          <AdminManagementPanel zonas={zonas} atracciones={atracciones} onChanged={loadData} seccion="operadores" />
        </TabsContent>

        <TabsContent value="management">
          <AdminManagementPanel zonas={zonas} atracciones={atracciones} onChanged={loadData} seccion="gestion" />
        </TabsContent>

        <TabsContent value="attractions" className="space-y-4">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2">
              <AttractionsTable atracciones={atracciones} showActions onSelect={setSelectedAtraccion} />
            </div>
            <Card className="border-0 shadow-sm">
              <CardHeader><CardTitle>Detalles de atracción</CardTitle></CardHeader>
              <CardContent className="space-y-2 text-sm">
                {selectedAtraccion ? (
                  <>
                    <p><strong>ID:</strong> {selectedAtraccion.id}</p>
                    <p><strong>Nombre:</strong> {selectedAtraccion.nombre}</p>
                    <p><strong>Zona:</strong> {selectedAtraccion.zonaNombre}</p>
                    <p><strong>Estado:</strong> {selectedAtraccion.estado}</p>
                    <p><strong>Capacidad ciclo:</strong> {selectedAtraccion.capacidadPorCiclo}</p>
                    <p><strong>Edad mínima:</strong> {selectedAtraccion.edadMinima}</p>
                    <p><strong>Altura mínima:</strong> {selectedAtraccion.alturaMinima}</p>
                    <p><strong>Costo adicional:</strong> ${selectedAtraccion.costoAdicional || 0}</p>
                    <p><strong>En cola:</strong> {selectedAtraccion.colaActual || 0}</p>
                    <p><strong>Visitantes acumulados:</strong> {selectedAtraccion.contadorAcumuladoVisitantes || 0}</p>
                    <p><strong>Incidentes:</strong> {selectedAtraccion.incidentesOperativos || 0}</p>
                  </>
                ) : <p className="text-gray-500">Seleccione “Ver detalles” en una atracción.</p>}
              </CardContent>
            </Card>
          </div>
          <IncidentForm atracciones={atracciones} onCreated={loadData} />
          <Card className="border-0 shadow-sm">
            <CardHeader><CardTitle>Incidentes operativos</CardTitle></CardHeader>
            <CardContent className="space-y-3">
              {incidentes.length === 0 ? <p className="text-sm text-gray-500">No hay incidentes registrados.</p> : incidentes.map((incidente) => (
                <div key={incidente.id} className="rounded border bg-gray-50 p-3 text-sm space-y-2">
                  <div className="flex items-center justify-between gap-2">
                    <div>
                      <p className="font-medium">{incidente.atraccionNombre} · {incidente.gravedad}</p>
                      <p>{incidente.descripcion}</p>
                      <p className="text-gray-500">{new Date(incidente.fechaHora).toLocaleString('es-CO')}</p>
                      <p>Estado: {incidente.resuelto ? 'Resuelto' : 'Pendiente'}</p>
                      {incidente.solucion && <p>Solución: {incidente.solucion}</p>}
                    </div>
                  </div>
                  {!incidente.resuelto && (
                    <div className="grid grid-cols-1 md:grid-cols-[1fr_auto] gap-2">
                      <div>
                        <Label>Descripción de solución</Label>
                        <Input placeholder="Ej: El técnico revisó y resolvió el problema" value={soluciones[incidente.id] || ''} onChange={(e) => setSoluciones({ ...soluciones, [incidente.id]: e.target.value })} />
                      </div>
                      <Button className="self-end" onClick={() => handleResolverIncidente(incidente.id)}>Resolver</Button>
                    </div>
                  )}
                </div>
              ))}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="weather">
          <WeatherAlertPanel alertas={alertasClima} onFinalize={handleFinalizeWeatherAlert} />
        </TabsContent>

        <TabsContent value="maintenance">
          <MaintenanceAlertsPanel alertas={alertasMantenimiento} />
        </TabsContent>

        <TabsContent value="data">
          <DataLoader onDataLoaded={handleDataLoaded} />
        </TabsContent>
      </Tabs>
    </div>
  )
}

function AdminSkeleton() {
  return (
    <div className="space-y-6">
      <Skeleton className="h-32" />
      <Skeleton className="h-12 w-full max-w-2xl" />
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Skeleton className="h-64" />
        <Skeleton className="h-64" />
      </div>
    </div>
  )
}
