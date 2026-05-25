'use client'

import { useState } from 'react'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { StatusBadge } from '@/components/StatusBadge'
import { colasService } from '@/services/colasService'
import { Clock, Users, Loader2, CheckCircle, AlertCircle } from 'lucide-react'
import type { Atraccion, PosicionCola } from '@/types'
import { toast } from 'sonner'

interface QueuePanelProps {
  atracciones: Atraccion[]
  visitanteDocumento: string
  onJoinedQueue?: () => void
}

export function QueuePanel({ 
  atracciones, 
  visitanteDocumento,
  onJoinedQueue 
}: QueuePanelProps) {
  const [selectedAtraccion, setSelectedAtraccion] = useState<string>('')
  const [isLoading, setIsLoading] = useState(false)
  const [posicion, setPosicion] = useState<PosicionCola | null>(null)
  const [error, setError] = useState<string | null>(null)

  const handleJoinQueue = async () => {
    if (!selectedAtraccion) {
      setError('Seleccione una atracción')
      return
    }

    setIsLoading(true)
    setError(null)

    try {
      const result = await colasService.unirse({
        documentoVisitante: visitanteDocumento,
        atraccionId: selectedAtraccion,
      })
      
      setPosicion(result)
      toast.success('Se unió a la cola exitosamente')
      onJoinedQueue?.()
    } catch (err) {
      setError(
        err instanceof Error 
          ? err.message 
          : 'Error al unirse a la cola. Intente nuevamente.'
      )
      toast.error('Error al unirse a la cola')
    } finally {
      setIsLoading(false)
    }
  }

  const handleCheckPosition = async () => {
    if (!selectedAtraccion) {
      setError('Seleccione una atracción')
      return
    }

    setIsLoading(true)
    setError(null)

    try {
      const result = await colasService.getPosicion(selectedAtraccion, visitanteDocumento)
      setPosicion(result)
    } catch (err) {
      setError(
        err instanceof Error 
          ? err.message 
          : 'No se encontró en la cola de esta atracción.'
      )
    } finally {
      setIsLoading(false)
    }
  }

  const selectedAtraccionData = atracciones.find(a => a.id === selectedAtraccion)

  return (
    <Card className="border-0 shadow-sm">
      <CardHeader>
        <div className="flex items-center gap-2">
          <Clock className="h-5 w-5 text-orange-600" />
          <CardTitle>Cola Virtual</CardTitle>
        </div>
        <CardDescription>
          Únase a la cola virtual de una atracción para evitar esperas físicas
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-6">
        {error && (
          <Alert variant="destructive">
            <AlertCircle className="h-4 w-4" />
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}

        {/* Attraction Selection */}
        <div className="space-y-2">
          <Select value={selectedAtraccion} onValueChange={setSelectedAtraccion}>
            <SelectTrigger>
              <SelectValue placeholder="Seleccione una atracción" />
            </SelectTrigger>
            <SelectContent>
              {atracciones.map((atraccion) => (
                <SelectItem key={atraccion.id} value={atraccion.id}>
                  <div className="flex items-center gap-2">
                    <span>{atraccion.nombre}</span>
                    {atraccion.colaActual !== undefined && (
                      <span className="text-xs text-gray-500">
                        ({atraccion.colaActual} en cola)
                      </span>
                    )}
                  </div>
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        {/* Selected Attraction Info */}
        {selectedAtraccionData && (
          <div className="p-4 bg-gray-50 rounded-lg space-y-3">
            <div className="flex items-center justify-between">
              <h4 className="font-medium">{selectedAtraccionData.nombre}</h4>
              <StatusBadge status={selectedAtraccionData.estado} />
            </div>
            <div className="grid grid-cols-2 gap-4 text-sm">
              <div className="flex items-center gap-2 text-gray-600">
                <Users className="h-4 w-4" />
                <span>Cola actual: {selectedAtraccionData.colaActual ?? 'N/A'}</span>
              </div>
              <div className="flex items-center gap-2 text-gray-600">
                <Clock className="h-4 w-4" />
                <span>
                  Espera: {selectedAtraccionData.tiempoEsperaEstimado 
                    ? `${selectedAtraccionData.tiempoEsperaEstimado} min` 
                    : 'N/A'
                  }
                </span>
              </div>
            </div>
          </div>
        )}

        {/* Position Info */}
        {posicion && (
          <Alert className="bg-green-50 border-green-200">
            <CheckCircle className="h-4 w-4 text-green-600" />
            <AlertDescription className="text-green-800">
              <div className="space-y-1">
                <p className="font-medium">Posición en cola: #{posicion.posicion}</p>
                <p>Atracción: {posicion.atraccionNombre}</p>
                <p>Tiempo estimado: {posicion.tiempoEsperaEstimado} minutos</p>
              </div>
            </AlertDescription>
          </Alert>
        )}

        {/* Actions */}
        <div className="flex gap-3">
          <Button 
            onClick={handleJoinQueue}
            className="flex-1 bg-orange-600 hover:bg-orange-700"
            disabled={isLoading || !selectedAtraccion}
          >
            {isLoading ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Procesando...
              </>
            ) : (
              <>
                <Users className="mr-2 h-4 w-4" />
                Unirse a la Cola
              </>
            )}
          </Button>
          <Button 
            onClick={handleCheckPosition}
            variant="outline"
            disabled={isLoading || !selectedAtraccion}
          >
            Ver Posición
          </Button>
        </div>

        {/* Queue Instructions */}
        <div className="p-4 bg-blue-50 rounded-lg text-sm text-blue-700">
          <p className="font-medium mb-1">Instrucciones:</p>
          <ul className="list-disc list-inside space-y-1">
            <li>Seleccione la atracción a la que desea unirse</li>
            <li>Presione &quot;Unirse a la Cola&quot; para reservar su lugar</li>
            <li>Recibirá una notificación cuando sea su turno</li>
            <li>Si tiene Fast Pass, tendrá prioridad en la cola</li>
          </ul>
        </div>
      </CardContent>
    </Card>
  )
}
