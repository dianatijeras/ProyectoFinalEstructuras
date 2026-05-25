import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { AlertBadge } from '@/components/StatusBadge'
import { CloudRain, CloudLightning, AlertTriangle } from 'lucide-react'
import type { AlertaClima } from '@/types'

interface WeatherAlertPanelProps {
  alertas: AlertaClima[]
  onFinalize?: (id: string) => void
}

export function WeatherAlertPanel({ alertas, onFinalize }: WeatherAlertPanelProps) {
  const getAlertIcon = (tipo: string) => {
    switch (tipo) {
      case 'LLUVIA_FUERTE':
        return <CloudRain className="h-5 w-5 text-blue-600" />
      case 'TORMENTA_ELECTRICA':
        return <CloudLightning className="h-5 w-5 text-purple-600" />
      default:
        return <AlertTriangle className="h-5 w-5 text-orange-600" />
    }
  }

  return (
    <Card className="border-0 shadow-sm">
      <CardHeader className="pb-2">
        <div className="flex items-center gap-2">
          <CloudRain className="h-5 w-5 text-blue-600" />
          <CardTitle className="text-lg">Alertas Climáticas</CardTitle>
        </div>
      </CardHeader>
      <CardContent>
        {alertas.length === 0 ? (
          <div className="text-center py-6 text-gray-500">
            <CloudRain className="h-12 w-12 mx-auto mb-2 text-gray-300" />
            <p>No hay alertas climáticas activas</p>
          </div>
        ) : (
          <div className="space-y-3">
            {alertas.map((alerta) => (
              <div
                key={alerta.id}
                className="p-3 rounded-lg bg-blue-50 border border-blue-100"
              >
                <div className="flex items-start gap-3">
                  {getAlertIcon(alerta.tipo)}
                  <div className="flex-1">
                    <div className="flex items-center justify-between gap-2 mb-1">
                      <div className="flex items-center gap-2">
                        <AlertBadge type={alerta.tipo} />
                        {alerta.activa ? (
                          <span className="text-xs text-green-600 font-medium">ACTIVA</span>
                        ) : (
                          <span className="text-xs text-gray-500 font-medium">FINALIZADA</span>
                        )}
                      </div>
                      {alerta.activa && onFinalize && (
                        <Button size="sm" variant="outline" onClick={() => onFinalize(alerta.id)}>
                          Finalizar
                        </Button>
                      )}
                    </div>
                    <p className="text-sm text-gray-700">{alerta.mensaje}</p>
                    <div className="mt-2 text-xs text-gray-500">
                      <p>Inicio: {new Date(alerta.fechaInicio).toLocaleString('es-CO')}</p>
                      {alerta.fechaFin && <p>Fin: {new Date(alerta.fechaFin).toLocaleString('es-CO')}</p>}
                      {alerta.atraccionesAfectadas && alerta.atraccionesAfectadas.length > 0 && (
                        <p className="mt-1">
                          Atracciones afectadas: {alerta.atraccionesAfectadas.length}
                        </p>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  )
}
