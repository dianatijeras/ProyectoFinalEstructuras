import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { History, Clock } from 'lucide-react'
import type { HistorialVisita } from '@/types'

interface HistoryPanelProps {
  historial: HistorialVisita[]
}

export function HistoryPanel({ historial }: HistoryPanelProps) {
  return (
    <Card className="border-0 shadow-sm">
      <CardHeader>
        <div className="flex items-center gap-2">
          <History className="h-5 w-5 text-orange-600" />
          <CardTitle>Historial de Visitas</CardTitle>
        </div>
        <CardDescription>
          Registro de las atracciones que ha visitado
        </CardDescription>
      </CardHeader>
      <CardContent>
        {historial.length === 0 ? (
          <div className="text-center py-8 text-gray-500">
            <History className="h-12 w-12 mx-auto mb-2 text-gray-300" />
            <p>No hay historial de visitas</p>
            <p className="text-sm">Sus visitas a atracciones aparecerán aquí</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Atracción</TableHead>
                  <TableHead>Fecha y Hora</TableHead>
                  <TableHead className="text-right">
                    <Clock className="h-4 w-4 inline mr-1" />
                    Tiempo de Espera
                  </TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {historial.map((visita) => (
                  <TableRow key={visita.id}>
                    <TableCell className="font-medium">
                      {visita.atraccionNombre}
                    </TableCell>
                    <TableCell>
                      {new Date(visita.fecha).toLocaleString('es-CO', {
                        dateStyle: 'medium',
                        timeStyle: 'short',
                      })}
                    </TableCell>
                    <TableCell className="text-right">
                      {visita.tiempoEspera 
                        ? `${visita.tiempoEspera} min`
                        : '-'
                      }
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
        )}
        <div className="mt-4 text-sm text-gray-500">
          Total de visitas: {historial.length}
        </div>
      </CardContent>
    </Card>
  )
}
