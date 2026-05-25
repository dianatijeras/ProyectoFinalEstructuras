import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { PriorityBadge } from '@/components/StatusBadge'
import { Badge } from '@/components/ui/badge'
import { Wrench, AlertTriangle, CheckCircle } from 'lucide-react'
import type { AlertaMantenimiento } from '@/types'

interface MaintenanceAlertsPanelProps {
  alertas: AlertaMantenimiento[]
}

export function MaintenanceAlertsPanel({ alertas }: MaintenanceAlertsPanelProps) {
  const getTypeIcon = (tipo: string) => {
    switch (tipo) {
      case 'URGENTE':
        return <AlertTriangle className="h-4 w-4 text-red-600" />
      case 'CORRECTIVO':
        return <Wrench className="h-4 w-4 text-orange-600" />
      case 'PREVENTIVO':
        return <CheckCircle className="h-4 w-4 text-blue-600" />
      default:
        return <Wrench className="h-4 w-4 text-gray-600" />
    }
  }

  const getTypeLabel = (tipo: string) => {
    const labels: Record<string, string> = {
      URGENTE: 'Urgente',
      CORRECTIVO: 'Correctivo',
      PREVENTIVO: 'Preventivo',
    }
    return labels[tipo] || tipo
  }

  return (
    <Card className="border-0 shadow-sm">
      <CardHeader className="pb-2">
        <div className="flex items-center gap-2">
          <Wrench className="h-5 w-5 text-orange-600" />
          <CardTitle className="text-lg">Alertas de Mantenimiento</CardTitle>
        </div>
      </CardHeader>
      <CardContent>
        {alertas.length === 0 ? (
          <div className="text-center py-6 text-gray-500">
            <Wrench className="h-12 w-12 mx-auto mb-2 text-gray-300" />
            <p>No hay alertas de mantenimiento pendientes</p>
          </div>
        ) : (
          <div className="space-y-3">
            {alertas.map((alerta) => (
              <div
                key={alerta.id}
                className={`p-3 rounded-lg border ${
                  alerta.prioridad === 'ALTA'
                    ? 'bg-red-50 border-red-100'
                    : alerta.prioridad === 'MEDIA'
                    ? 'bg-yellow-50 border-yellow-100'
                    : 'bg-gray-50 border-gray-100'
                }`}
              >
                <div className="flex items-start gap-3">
                  {getTypeIcon(alerta.tipo)}
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-1 flex-wrap">
                      <span className="font-medium text-gray-900">
                        {alerta.atraccionNombre}
                      </span>
                      <Badge variant="outline" className="text-xs">
                        {getTypeLabel(alerta.tipo)}
                      </Badge>
                      <PriorityBadge priority={alerta.prioridad} />
                    </div>
                    <p className="text-sm text-gray-700">{alerta.descripcion}</p>
                    <div className="mt-2 text-xs text-gray-500">
                      <p>Reportado: {new Date(alerta.fechaReporte).toLocaleString('es-CO')}</p>
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
