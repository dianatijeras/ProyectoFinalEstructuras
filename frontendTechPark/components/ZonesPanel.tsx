import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { StatusBadge } from '@/components/StatusBadge'
import { MapPin, Ticket, Users } from 'lucide-react'
import type { Zona } from '@/types'

interface ZonesPanelProps {
  zonas: Zona[]
}

export function ZonesPanel({ zonas }: ZonesPanelProps) {
  if (zonas.length === 0) {
    return (
      <Card className="border-0 shadow-sm">
        <CardContent className="py-8 text-center text-gray-500">
          No hay zonas disponibles
        </CardContent>
      </Card>
    )
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      {zonas.map((zona) => {
        const atraccionesActivas = zona.atracciones?.filter(a => a.estado === 'ACTIVA').length || 0
        const totalAtracciones = zona.atracciones?.length || 0

        return (
          <Card key={zona.id} className="border-0 shadow-sm hover:shadow-md transition-shadow">
            <CardHeader className="pb-2">
              <div className="flex items-start justify-between">
                <div className="flex items-center gap-2">
                  <div className="w-10 h-10 rounded-lg bg-orange-100 flex items-center justify-center">
                    <MapPin className="h-5 w-5 text-orange-600" />
                  </div>
                  <div>
                    <CardTitle className="text-lg">{zona.nombre}</CardTitle>
                    <p className="text-xs text-gray-500">ID: {zona.id}</p>
                  </div>
                </div>
              </div>
            </CardHeader>
            <CardContent className="space-y-3">
              {zona.descripcion && (
                <p className="text-sm text-gray-600">{zona.descripcion}</p>
              )}
              
              <div className="flex items-center gap-4 text-sm">
                <div className="flex items-center gap-1 text-gray-600">
                  <Ticket className="h-4 w-4" />
                  <span>{totalAtracciones} atracciones</span>
                </div>
                <div className="flex items-center gap-1 text-gray-600">
                  <Users className="h-4 w-4" />
                  <span>Máx: {zona.capacidadMaxima}</span>
                </div>
              </div>

              <div className="flex items-center gap-2">
                <Badge className="bg-green-100 text-green-700 hover:bg-green-100">
                  {atraccionesActivas} activas
                </Badge>
                {totalAtracciones - atraccionesActivas > 0 && (
                  <Badge className="bg-gray-100 text-gray-700 hover:bg-gray-100">
                    {totalAtracciones - atraccionesActivas} inactivas
                  </Badge>
                )}
              </div>

              {zona.atracciones && zona.atracciones.length > 0 && (
                <div className="pt-2 border-t border-gray-100">
                  <p className="text-xs font-medium text-gray-500 mb-2">Atracciones:</p>
                  <div className="flex flex-wrap gap-1">
                    {zona.atracciones.slice(0, 5).map((atraccion) => (
                      <div 
                        key={atraccion.id}
                        className="flex items-center gap-1 text-xs bg-gray-50 px-2 py-1 rounded"
                      >
                        <span className="truncate max-w-24">{atraccion.nombre}</span>
                        <StatusBadge status={atraccion.estado} className="text-[10px] px-1 py-0" />
                      </div>
                    ))}
                    {zona.atracciones.length > 5 && (
                      <span className="text-xs text-gray-400 px-2 py-1">
                        +{zona.atracciones.length - 5} más
                      </span>
                    )}
                  </div>
                </div>
              )}
            </CardContent>
          </Card>
        )
      })}
    </div>
  )
}
