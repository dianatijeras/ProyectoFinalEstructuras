'use client'

import { useState, useEffect } from 'react'
import { useAuth } from '@/contexts/AuthContext'
import { OperatorQueuePanel } from '@/components/OperatorQueuePanel'
import { AttractionStateForm } from '@/components/AttractionStateForm'
import { MaintenanceAlertsPanel } from '@/components/MaintenanceAlertsPanel'
import { AttractionsTable } from '@/components/AttractionsTable'
import { IncidentForm } from '@/components/IncidentForm'
import { atraccionService } from '@/services/atraccionService'
import { alertasService } from '@/services/alertasService'
import { parqueService } from '@/services/parqueService'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Badge } from '@/components/ui/badge'
import { Skeleton } from '@/components/ui/skeleton'
import { 
  Wrench, 
  Users, 
  Settings, 
  AlertTriangle,
  CheckCircle,
  XCircle,
  Clock
} from 'lucide-react'
import type { Atraccion, Zona, AlertaMantenimiento } from '@/types'

export default function OperatorPage() {
  const { user } = useAuth()
  const [atracciones, setAtracciones] = useState<Atraccion[]>([])
  const [zonas, setZonas] = useState<Zona[]>([])
  const [alertasMantenimiento, setAlertasMantenimiento] = useState<AlertaMantenimiento[]>([])
  const [selectedAtraccion, setSelectedAtraccion] = useState<Atraccion | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    setIsLoading(true)
    setError(null)

    try {
      const [atraccionesData, zonasData, alertasData] = await Promise.all([
        atraccionService.getAll(),
        parqueService.getZonas(),
        alertasService.getAlertasMantenimiento(),
      ])

      setAtracciones(atraccionesData)
      setZonas(zonasData)
      setAlertasMantenimiento(alertasData)
    } catch (err) {
      console.error('Error loading operator data:', err)
      setError('No se pudo conectar con el servidor.')
    } finally {
      setIsLoading(false)
    }
  }

  const handleAtraccionUpdated = () => {
    loadData()
    setSelectedAtraccion(null)
  }

  const handleQueueProcessed = () => {
    loadData()
  }

  if (isLoading) {
    return <OperatorSkeleton />
  }

  const activeAtracciones = atracciones.filter(a => a.estado === 'ACTIVA')
  const maintenanceAtracciones = atracciones.filter(a => a.estado === 'EN_MANTENIMIENTO')
  const closedAtracciones = atracciones.filter(a => a.estado === 'CERRADA')
  const pendingAlerts = alertasMantenimiento.filter(a => !a.resuelta)

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

      {/* Operator Info Card */}
      <Card className="border-0 shadow-sm bg-gradient-to-r from-blue-500 to-blue-600 text-white">
        <CardContent className="p-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <div className="w-16 h-16 rounded-full bg-white/20 flex items-center justify-center">
                <Wrench className="h-8 w-8" />
              </div>
              <div>
                <h2 className="text-2xl font-bold">{user?.nombre || 'Operador'}</h2>
                <p className="text-blue-100">Panel de Operaciones</p>
              </div>
            </div>
            <div className="hidden md:flex gap-4">
              <div className="text-center">
                <p className="text-3xl font-bold">{atracciones.length}</p>
                <p className="text-sm text-blue-100">Atracciones</p>
              </div>
              <div className="text-center">
                <p className="text-3xl font-bold">{pendingAlerts.length}</p>
                <p className="text-sm text-blue-100">Alertas Pendientes</p>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Quick Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <Card className="border-0 shadow-sm">
          <CardContent className="p-4 flex items-center gap-3">
            <div className="w-10 h-10 rounded-full bg-green-100 flex items-center justify-center">
              <CheckCircle className="h-5 w-5 text-green-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-gray-900">{activeAtracciones.length}</p>
              <p className="text-xs text-gray-500">Activas</p>
            </div>
          </CardContent>
        </Card>
        
        <Card className="border-0 shadow-sm">
          <CardContent className="p-4 flex items-center gap-3">
            <div className="w-10 h-10 rounded-full bg-yellow-100 flex items-center justify-center">
              <Wrench className="h-5 w-5 text-yellow-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-gray-900">{maintenanceAtracciones.length}</p>
              <p className="text-xs text-gray-500">Mantenimiento</p>
            </div>
          </CardContent>
        </Card>
        
        <Card className="border-0 shadow-sm">
          <CardContent className="p-4 flex items-center gap-3">
            <div className="w-10 h-10 rounded-full bg-red-100 flex items-center justify-center">
              <XCircle className="h-5 w-5 text-red-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-gray-900">{closedAtracciones.length}</p>
              <p className="text-xs text-gray-500">Cerradas</p>
            </div>
          </CardContent>
        </Card>
        
        <Card className="border-0 shadow-sm">
          <CardContent className="p-4 flex items-center gap-3">
            <div className="w-10 h-10 rounded-full bg-orange-100 flex items-center justify-center">
              <AlertTriangle className="h-5 w-5 text-orange-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-gray-900">{pendingAlerts.length}</p>
              <p className="text-xs text-gray-500">Alertas</p>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Main Tabs */}
      <Tabs defaultValue="attractions" className="space-y-4">
        <TabsList>
          <TabsTrigger value="attractions" className="flex items-center gap-1">
            <Settings className="h-4 w-4" />
            Atracciones
          </TabsTrigger>
          <TabsTrigger value="queues" className="flex items-center gap-1">
            <Users className="h-4 w-4" />
            Colas
          </TabsTrigger>
          <TabsTrigger value="maintenance" className="flex items-center gap-1">
            <Wrench className="h-4 w-4" />
            Mantenimiento
          </TabsTrigger>
        </TabsList>

        <TabsContent value="attractions" className="space-y-4">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2">
              <AttractionsTable 
                atracciones={atracciones}
                onSelect={setSelectedAtraccion}
                showActions
              />
            </div>
            <div>
              {selectedAtraccion ? (
                <AttractionStateForm 
                  atraccion={selectedAtraccion}
                  onUpdated={handleAtraccionUpdated}
                  onCancel={() => setSelectedAtraccion(null)}
                />
              ) : (
                <Card className="border-0 shadow-sm">
                  <CardContent className="py-8 text-center text-gray-500">
                    <Settings className="h-12 w-12 mx-auto mb-2 text-gray-300" />
                    <p>Seleccione una atracción</p>
                    <p className="text-sm">para ver opciones de gestión</p>
                  </CardContent>
                </Card>
              )}
            </div>
          </div>
        </TabsContent>

        <TabsContent value="queues">
          <OperatorQueuePanel 
            atracciones={activeAtracciones}
            onQueueProcessed={handleQueueProcessed}
          />
        </TabsContent>

        <TabsContent value="maintenance" className="space-y-4">
          <MaintenanceAlertsPanel alertas={alertasMantenimiento} />
          <IncidentForm atracciones={atracciones} onCreated={loadData} />
        </TabsContent>
      </Tabs>
    </div>
  )
}

function OperatorSkeleton() {
  return (
    <div className="space-y-6">
      <Skeleton className="h-32" />
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {[...Array(4)].map((_, i) => (
          <Skeleton key={i} className="h-20" />
        ))}
      </div>
      <Skeleton className="h-12 w-64" />
      <Skeleton className="h-96" />
    </div>
  )
}
