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
import { Badge } from '@/components/ui/badge'
import { colasService } from '@/services/colasService'
import { Users, Play, Clock, Loader2, CheckCircle, AlertCircle } from 'lucide-react'
import type { Atraccion, Cola } from '@/types'
import { toast } from 'sonner'

interface OperatorQueuePanelProps {
  atracciones: Atraccion[]
  onQueueProcessed?: () => void
}

export function OperatorQueuePanel({ 
  atracciones,
  onQueueProcessed 
}: OperatorQueuePanelProps) {
  const [selectedAtraccion, setSelectedAtraccion] = useState<string>('')
  const [cola, setCola] = useState<Cola | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [isProcessing, setIsProcessing] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)

  const handleLoadQueue = async () => {
    if (!selectedAtraccion) return

    setIsLoading(true)
    setError(null)
    setCola(null)

    try {
      const result = await colasService.getCola(selectedAtraccion)
      setCola(result)
    } catch (err) {
      setError(
        err instanceof Error 
          ? err.message 
          : 'Error al cargar la cola.'
      )
    } finally {
      setIsLoading(false)
    }
  }

  const handleProcessNext = async () => {
    if (!selectedAtraccion) return

    setIsProcessing(true)
    setError(null)
    setSuccess(null)

    try {
      await colasService.procesarSiguiente(selectedAtraccion)
      setSuccess('Visitante procesado exitosamente')
      toast.success('Siguiente visitante procesado')
      
      // Reload queue
      handleLoadQueue()
      onQueueProcessed?.()
    } catch (err) {
      setError(
        err instanceof Error 
          ? err.message 
          : 'Error al procesar el siguiente visitante.'
      )
      toast.error('Error al procesar visitante')
    } finally {
      setIsProcessing(false)
    }
  }

  return (
    <Card className="border-0 shadow-sm">
      <CardHeader>
        <div className="flex items-center gap-2">
          <Users className="h-5 w-5 text-blue-600" />
          <CardTitle>Gestión de Colas</CardTitle>
        </div>
        <CardDescription>
          Procese visitantes en las colas virtuales de cada atracción
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-6">
        {/* Attraction Selection */}
        <div className="flex gap-2">
          <Select value={selectedAtraccion} onValueChange={setSelectedAtraccion}>
            <SelectTrigger className="flex-1">
              <SelectValue placeholder="Seleccione una atracción" />
            </SelectTrigger>
            <SelectContent>
              {atracciones.map((atraccion) => (
                <SelectItem key={atraccion.id} value={atraccion.id}>
                  <div className="flex items-center gap-2">
                    <span>{atraccion.nombre}</span>
                    {atraccion.colaActual !== undefined && (
                      <Badge variant="outline" className="text-xs">
                        {atraccion.colaActual} en cola
                      </Badge>
                    )}
                  </div>
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <Button 
            onClick={handleLoadQueue}
            variant="outline"
            disabled={isLoading || !selectedAtraccion}
          >
            {isLoading ? (
              <Loader2 className="h-4 w-4 animate-spin" />
            ) : (
              'Cargar Cola'
            )}
          </Button>
        </div>

        {/* Error/Success Messages */}
        {error && (
          <Alert variant="destructive">
            <AlertCircle className="h-4 w-4" />
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}

        {success && (
          <Alert className="bg-green-50 border-green-200">
            <CheckCircle className="h-4 w-4 text-green-600" />
            <AlertDescription className="text-green-800">{success}</AlertDescription>
          </Alert>
        )}

        {/* Queue Info */}
        {cola && (
          <div className="space-y-4">
            {/* Queue Summary */}
            <div className="p-4 bg-blue-50 rounded-lg">
              <div className="flex items-center justify-between mb-3">
                <h4 className="font-medium text-blue-900">{cola.atraccionNombre}</h4>
                <div className="flex items-center gap-4 text-sm">
                  <span className="flex items-center gap-1 text-blue-700">
                    <Users className="h-4 w-4" />
                    {cola.visitantesEnCola} en cola
                  </span>
                  <span className="flex items-center gap-1 text-blue-700">
                    <Clock className="h-4 w-4" />
                    ~{cola.tiempoEsperaEstimado} min
                  </span>
                </div>
              </div>
              
              <Button 
                onClick={handleProcessNext}
                className="w-full bg-blue-600 hover:bg-blue-700"
                disabled={isProcessing || cola.visitantesEnCola === 0}
              >
                {isProcessing ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Procesando...
                  </>
                ) : (
                  <>
                    <Play className="mr-2 h-4 w-4" />
                    Procesar Siguiente Visitante
                  </>
                )}
              </Button>
            </div>

            {/* Queue List */}
            {cola.visitantes && cola.visitantes.length > 0 ? (
              <div className="space-y-2">
                <h4 className="font-medium text-gray-900">Visitantes en Cola</h4>
                <div className="space-y-2 max-h-64 overflow-y-auto">
                  {cola.visitantes.map((visitante, index) => (
                    <div
                      key={visitante.documento}
                      className={`flex items-center justify-between p-3 rounded-lg ${
                        index === 0 ? 'bg-green-50 border border-green-200' : 'bg-gray-50'
                      }`}
                    >
                      <div className="flex items-center gap-3">
                        <span className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium ${
                          index === 0 
                            ? 'bg-green-200 text-green-700' 
                            : 'bg-gray-200 text-gray-700'
                        }`}>
                          {visitante.posicion}
                        </span>
                        <div>
                          <p className="font-medium text-gray-900">{visitante.nombre}</p>
                          <p className="text-xs text-gray-500">
                            Doc: {visitante.documento}
                          </p>
                        </div>
                      </div>
                      <div className="flex items-center gap-2">
                        {visitante.esFastPass && (
                          <Badge className="bg-yellow-100 text-yellow-700 hover:bg-yellow-100">
                            Fast Pass
                          </Badge>
                        )}
                        <span className="text-xs text-gray-500">
                          {new Date(visitante.horaIngreso).toLocaleTimeString('es-CO', {
                            hour: '2-digit',
                            minute: '2-digit',
                          })}
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            ) : (
              <div className="text-center py-8 text-gray-500">
                <Users className="h-12 w-12 mx-auto mb-2 text-gray-300" />
                <p>No hay visitantes en la cola</p>
              </div>
            )}
          </div>
        )}

        {/* Instructions */}
        {!cola && (
          <div className="p-4 bg-gray-50 rounded-lg text-sm text-gray-600">
            <p className="font-medium mb-1">Instrucciones:</p>
            <ul className="list-disc list-inside space-y-1">
              <li>Seleccione una atracción activa</li>
              <li>Cargue la cola para ver los visitantes en espera</li>
              <li>Use &quot;Procesar Siguiente&quot; para atender al próximo visitante</li>
              <li>Los visitantes con Fast Pass tienen prioridad</li>
            </ul>
          </div>
        )}
      </CardContent>
    </Card>
  )
}
