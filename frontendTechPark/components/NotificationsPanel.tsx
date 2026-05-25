import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Bell, Info, AlertTriangle, Gift } from 'lucide-react'
import type { Notificacion } from '@/types'

interface NotificationsPanelProps {
  notificaciones: Notificacion[]
}

export function NotificationsPanel({ notificaciones }: NotificationsPanelProps) {
  const getNotificationIcon = (tipo: string) => {
    switch (tipo) {
      case 'ALERTA':
        return <AlertTriangle className="h-5 w-5 text-orange-500" />
      case 'PROMOCION':
        return <Gift className="h-5 w-5 text-green-500" />
      case 'INFO':
      default:
        return <Info className="h-5 w-5 text-blue-500" />
    }
  }

  const getNotificationStyle = (tipo: string) => {
    switch (tipo) {
      case 'ALERTA':
        return 'border-l-4 border-orange-400'
      case 'PROMOCION':
        return 'border-l-4 border-green-400'
      case 'INFO':
      default:
        return 'border-l-4 border-blue-400'
    }
  }

  return (
    <Card className="border-0 shadow-sm">
      <CardHeader>
        <div className="flex items-center gap-2">
          <Bell className="h-5 w-5 text-orange-600" />
          <CardTitle>Notificaciones</CardTitle>
        </div>
        <CardDescription>
          Mensajes y alertas importantes del parque
        </CardDescription>
      </CardHeader>
      <CardContent>
        {notificaciones.length === 0 ? (
          <div className="text-center py-8 text-gray-500">
            <Bell className="h-12 w-12 mx-auto mb-2 text-gray-300" />
            <p>No hay notificaciones</p>
            <p className="text-sm">Las alertas y mensajes aparecerán aquí</p>
          </div>
        ) : (
          <div className="space-y-3">
            {notificaciones.map((notificacion) => (
              <div
                key={notificacion.id}
                className={`p-4 rounded-lg ${getNotificationStyle(notificacion.tipo)}`}
              >
                <div className="flex items-start gap-3">
                  {getNotificationIcon(notificacion.tipo)}
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-1">
                      <span className="font-medium text-gray-900">
                        {notificacion.titulo}
                      </span>
                    </div>
                    <p className="text-sm text-gray-600">{notificacion.mensaje}</p>
                    <p className="text-xs text-gray-400 mt-2">
                      {new Date(notificacion.fecha).toLocaleString('es-CO')}
                    </p>
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
