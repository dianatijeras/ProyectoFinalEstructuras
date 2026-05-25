'use client'

import { useEffect, useState } from 'react'
import { reportesService } from '@/services/reportesService'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Skeleton } from '@/components/ui/skeleton'
import { BarChart3, DollarSign, Ticket, Clock, CloudRain, Wrench, AlertTriangle } from 'lucide-react'
import type { ReporteJornada } from '@/types'

export default function ReportsPage() {
  const [reporte, setReporte] = useState<ReporteJornada | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    loadReporte()
    const intervalo = window.setInterval(loadReporte, 10000)
    window.addEventListener('focus', loadReporte)
    return () => {
      window.clearInterval(intervalo)
      window.removeEventListener('focus', loadReporte)
    }
  }, [])

  const loadReporte = async () => {
    setIsLoading(true)
    setError(null)
    try {
      const data = await reportesService.getReporteJornada()
      setReporte(data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo cargar el reporte')
    } finally {
      setIsLoading(false)
    }
  }

  if (isLoading) {
    return <ReportsSkeleton />
  }

  if (error) {
    return (
      <Alert className="bg-yellow-50 border-yellow-200">
        <AlertTriangle className="h-4 w-4 text-yellow-600" />
        <AlertDescription className="text-yellow-800">{error}</AlertDescription>
      </Alert>
    )
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-gray-900">Reporte de Jornada</h2>
        <p className="text-gray-500">Datos calculados desde las estructuras del backend</p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <Card className="border-0 shadow-sm">
          <CardContent className="p-4 flex items-center gap-4">
            <div className="w-12 h-12 rounded-full bg-green-100 flex items-center justify-center">
              <DollarSign className="h-6 w-6 text-green-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-gray-900">${reporte?.ingresosTotales?.toLocaleString() || 0}</p>
              <p className="text-sm text-gray-500">Ingresos diarios</p>
            </div>
          </CardContent>
        </Card>
        <Card className="border-0 shadow-sm">
          <CardContent className="p-4 flex items-center gap-4">
            <div className="w-12 h-12 rounded-full bg-blue-100 flex items-center justify-center">
              <Ticket className="h-6 w-6 text-blue-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-gray-900">{reporte?.atraccionesMasVisitadas?.length || 0}</p>
              <p className="text-sm text-gray-500">Atracciones visitadas</p>
            </div>
          </CardContent>
        </Card>
        <Card className="border-0 shadow-sm">
          <CardContent className="p-4 flex items-center gap-4">
            <div className="w-12 h-12 rounded-full bg-orange-100 flex items-center justify-center">
              <CloudRain className="h-6 w-6 text-orange-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-gray-900">{reporte?.cierresPorClima?.length || 0}</p>
              <p className="text-sm text-gray-500">Cierres por clima</p>
            </div>
          </CardContent>
        </Card>
        <Card className="border-0 shadow-sm">
          <CardContent className="p-4 flex items-center gap-4">
            <div className="w-12 h-12 rounded-full bg-yellow-100 flex items-center justify-center">
              <Wrench className="h-6 w-6 text-yellow-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-gray-900">{reporte?.alertasMantenimiento?.length || 0}</p>
              <p className="text-sm text-gray-500">Alertas mantenimiento</p>
            </div>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card className="border-0 shadow-sm">
          <CardHeader>
            <CardTitle className="flex items-center gap-2"><BarChart3 className="h-5 w-5" />Atracciones más visitadas</CardTitle>
          </CardHeader>
          <CardContent>
            {reporte?.atraccionesMasVisitadas?.length ? (
              <div className="space-y-3">
                {reporte.atraccionesMasVisitadas.map((item) => (
                  <div key={item.atraccionId} className="flex items-center justify-between border-b pb-2">
                    <span className="font-medium">{item.nombre}</span>
                    <span className="text-sm text-gray-500">{item.visitas} visitas</span>
                  </div>
                ))}
              </div>
            ) : <p className="text-gray-500">No hay visitas registradas todavía</p>}
          </CardContent>
        </Card>

        <Card className="border-0 shadow-sm">
          <CardHeader>
            <CardTitle className="flex items-center gap-2"><Clock className="h-5 w-5" />Tiempos promedio de espera</CardTitle>
          </CardHeader>
          <CardContent>
            {reporte?.tiemposEsperaPromedio?.length ? (
              <div className="space-y-3">
                {reporte.tiemposEsperaPromedio.map((item) => (
                  <div key={item.atraccionId} className="flex items-center justify-between border-b pb-2">
                    <span className="font-medium">{item.nombre}</span>
                    <span className="text-sm text-gray-500">{item.tiempoPromedio} min</span>
                  </div>
                ))}
              </div>
            ) : <p className="text-gray-500">No hay tiempos registrados</p>}
          </CardContent>
        </Card>

        <Card className="border-0 shadow-sm">
          <CardHeader>
            <CardTitle className="flex items-center gap-2"><Wrench className="h-5 w-5" />Mantenimiento</CardTitle>
          </CardHeader>
          <CardContent>
            {reporte?.alertasMantenimiento?.length ? (
              <ul className="space-y-2 text-sm text-gray-700">
                {reporte.alertasMantenimiento.map((item, index) => (
                  <li key={index} className="border-b pb-2">{item.atraccionNombre}</li>
                ))}
              </ul>
            ) : <p className="text-gray-500">No hay alertas de mantenimiento</p>}
          </CardContent>
        </Card>

        <Card className="border-0 shadow-sm">
          <CardHeader>
            <CardTitle className="flex items-center gap-2"><AlertTriangle className="h-5 w-5" />Incidentes operativos</CardTitle>
          </CardHeader>
          <CardContent>
            {reporte?.incidentesOperativos?.length ? (
              <ul className="space-y-2 text-sm text-gray-700">
                {reporte.incidentesOperativos.map((item, index) => (
                  <li key={index} className="border-b pb-2">{item.descripcion}</li>
                ))}
              </ul>
            ) : <p className="text-gray-500">No hay incidentes operativos</p>}
          </CardContent>
        </Card>
      </div>
    </div>
  )
}

function ReportsSkeleton() {
  return (
    <div className="space-y-6">
      <Skeleton className="h-8 w-64" />
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {[...Array(4)].map((_, i) => <Skeleton key={i} className="h-28" />)}
      </div>
      <Skeleton className="h-96" />
    </div>
  )
}
