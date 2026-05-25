'use client'

import { useEffect, useState } from 'react'
import { StatCard } from '@/components/StatCard'
import { ZonesPanel } from '@/components/ZonesPanel'
import { AttractionsTable } from '@/components/AttractionsTable'
import { WeatherAlertPanel } from '@/components/WeatherAlertPanel'
import { MaintenanceAlertsPanel } from '@/components/MaintenanceAlertsPanel'
import { parqueService } from '@/services/parqueService'
import { atraccionService } from '@/services/atraccionService'
import { alertasService } from '@/services/alertasService'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { Skeleton } from '@/components/ui/skeleton'
import { Alert, AlertDescription } from '@/components/ui/alert'
import {
  Users,
  MapPin,
  Ticket,
  AlertTriangle,
  CheckCircle,
  Wrench,
  XCircle,
  CloudRain,
} from 'lucide-react'
import type { ResumenParque, Zona, Atraccion, AlertaClima, AlertaMantenimiento } from '@/types'

// Demo data for when backend is unavailable
const demoResumen: ResumenParque = {
  nombreParque: 'Tech-Park UQ',
  capacidadActual: 850,
  capacidadMaxima: 2000,
  totalZonas: 5,
  totalAtracciones: 15,
  atraccionesActivas: 10,
  atraccionesEnMantenimiento: 3,
  atraccionesCerradas: 2,
  alertasClimaActivas: 1,
  alertasMantenimientoPendientes: 4,
}

export default function DashboardPage() {
  const [resumen, setResumen] = useState<ResumenParque | null>(null)
  const [zonas, setZonas] = useState<Zona[]>([])
  const [atracciones, setAtracciones] = useState<Atraccion[]>([])
  const [alertasClima, setAlertasClima] = useState<AlertaClima[]>([])
  const [alertasMantenimiento, setAlertasMantenimiento] = useState<AlertaMantenimiento[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [usingDemo, setUsingDemo] = useState(false)

  useEffect(() => {
    loadData()
    const intervalo = window.setInterval(loadData, 10000)
    window.addEventListener('focus', loadData)
    return () => {
      window.clearInterval(intervalo)
      window.removeEventListener('focus', loadData)
    }
  }, [])

  const loadData = async () => {
    setIsLoading(true)
    setError(null)
    setUsingDemo(false)

    try {
      const [resumenData, zonasData, atraccionesData, climaData, mantenimientoData] = await Promise.all([
        parqueService.getResumen(),
        parqueService.getZonas(),
        atraccionService.getAll(),
        alertasService.getAlertasClima(),
        alertasService.getAlertasMantenimiento(),
      ])

      setResumen(resumenData)
      setZonas(zonasData)
      setAtracciones(atraccionesData)
      setAlertasClima(climaData)
      setAlertasMantenimiento(mantenimientoData)
    } catch (err) {
      console.error('Error loading dashboard data:', err)
      setError('No se pudo conectar con el servidor. Mostrando datos de demostración.')
      setUsingDemo(true)
      setResumen(demoResumen)
    } finally {
      setIsLoading(false)
    }
  }

  if (isLoading) {
    return <DashboardSkeleton />
  }

  const activeAlerts = alertasClima.filter(a => a.activa)
  const pendingMaintenance = alertasMantenimiento.filter(a => !a.resuelta)

  return (
    <div className="space-y-6">
      {/* Error/Demo Alert */}
      {error && (
        <Alert className="bg-yellow-50 border-yellow-200">
          <AlertTriangle className="h-4 w-4 text-yellow-600" />
          <AlertDescription className="text-yellow-800">
            {error}
          </AlertDescription>
        </Alert>
      )}

      {/* Park Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">
            {resumen?.nombreParque || 'Tech-Park UQ'}
          </h2>
          <p className="text-gray-500">
            Capacidad: {resumen?.capacidadActual?.toLocaleString()} / {resumen?.capacidadMaxima?.toLocaleString()} visitantes
          </p>
        </div>
        {usingDemo && (
          <span className="px-3 py-1 bg-yellow-100 text-yellow-700 text-sm rounded-full">
            Modo Demo
          </span>
        )}
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Zonas del Parque"
          value={resumen?.totalZonas || 0}
          icon={<MapPin className="h-6 w-6 text-orange-600" />}
          iconClassName="bg-orange-100"
        />
        <StatCard
          title="Total Atracciones"
          value={resumen?.totalAtracciones || 0}
          icon={<Ticket className="h-6 w-6 text-blue-600" />}
          iconClassName="bg-blue-100"
        />
        <StatCard
          title="Visitantes Actuales"
          value={resumen?.capacidadActual?.toLocaleString() || 0}
          description={`Máx: ${resumen?.capacidadMaxima?.toLocaleString()}`}
          icon={<Users className="h-6 w-6 text-indigo-600" />}
          iconClassName="bg-indigo-100"
        />
        <StatCard
          title="Alertas Activas"
          value={(resumen?.alertasClimaActivas || 0) + (resumen?.alertasMantenimientoPendientes || 0)}
          icon={<AlertTriangle className="h-6 w-6 text-orange-600" />}
          iconClassName="bg-orange-100"
        />
      </div>

      {/* Attraction Status Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <Card className="border-0 shadow-sm bg-green-50">
          <CardContent className="p-4 flex items-center gap-4">
            <div className="w-12 h-12 rounded-full bg-green-100 flex items-center justify-center">
              <CheckCircle className="h-6 w-6 text-green-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-green-700">
                {resumen?.atraccionesActivas || 0}
              </p>
              <p className="text-sm text-green-600">Atracciones Activas</p>
            </div>
          </CardContent>
        </Card>
        
        <Card className="border-0 shadow-sm bg-yellow-50">
          <CardContent className="p-4 flex items-center gap-4">
            <div className="w-12 h-12 rounded-full bg-yellow-100 flex items-center justify-center">
              <Wrench className="h-6 w-6 text-yellow-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-yellow-700">
                {resumen?.atraccionesEnMantenimiento || 0}
              </p>
              <p className="text-sm text-yellow-600">En Mantenimiento</p>
            </div>
          </CardContent>
        </Card>
        
        <Card className="border-0 shadow-sm bg-red-50">
          <CardContent className="p-4 flex items-center gap-4">
            <div className="w-12 h-12 rounded-full bg-red-100 flex items-center justify-center">
              <XCircle className="h-6 w-6 text-red-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-red-700">
                {resumen?.atraccionesCerradas || 0}
              </p>
              <p className="text-sm text-red-600">Cerradas</p>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Main Content Tabs */}
      <Tabs defaultValue="attractions" className="space-y-4">
        <TabsList>
          <TabsTrigger value="attractions">Atracciones</TabsTrigger>
          <TabsTrigger value="zones">Zonas</TabsTrigger>
          <TabsTrigger value="alerts">Alertas</TabsTrigger>
        </TabsList>

        <TabsContent value="attractions" className="space-y-4">
          <AttractionsTable atracciones={atracciones} />
        </TabsContent>

        <TabsContent value="zones" className="space-y-4">
          <ZonesPanel zonas={zonas} />
        </TabsContent>

        <TabsContent value="alerts" className="space-y-4">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <WeatherAlertPanel alertas={activeAlerts} />
            <MaintenanceAlertsPanel alertas={pendingMaintenance} />
          </div>
        </TabsContent>
      </Tabs>
    </div>
  )
}

function DashboardSkeleton() {
  return (
    <div className="space-y-6">
      <div className="space-y-2">
        <Skeleton className="h-8 w-48" />
        <Skeleton className="h-4 w-64" />
      </div>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {[...Array(4)].map((_, i) => (
          <Skeleton key={i} className="h-28" />
        ))}
      </div>
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        {[...Array(3)].map((_, i) => (
          <Skeleton key={i} className="h-24" />
        ))}
      </div>
      <Skeleton className="h-96" />
    </div>
  )
}
