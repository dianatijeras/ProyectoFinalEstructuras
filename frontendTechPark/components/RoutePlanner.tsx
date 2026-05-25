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
import { mapaService } from '@/services/mapaService'
import { MapPin, Navigation, Loader2, ArrowRight, Clock, Ruler } from 'lucide-react'
import type { Atraccion, Ruta } from '@/types'
import { toast } from 'sonner'

interface RoutePlannerProps {
  atracciones: Atraccion[]
}

export function RoutePlanner({ atracciones }: RoutePlannerProps) {
  const [origen, setOrigen] = useState<string>('')
  const [destino, setDestino] = useState<string>('')
  const [isLoading, setIsLoading] = useState(false)
  const [ruta, setRuta] = useState<Ruta | null>(null)
  const [error, setError] = useState<string | null>(null)

  const handleCalculateRoute = async () => {
    if (!origen || !destino) {
      setError('Seleccione origen y destino')
      return
    }

    if (origen === destino) {
      setError('El origen y destino deben ser diferentes')
      return
    }

    setIsLoading(true)
    setError(null)
    setRuta(null)

    try {
      const result = await mapaService.getRuta(origen, destino)
      setRuta(result)
      toast.success('Ruta calculada exitosamente')
    } catch (err) {
      setError(
        err instanceof Error 
          ? err.message 
          : 'Error al calcular la ruta. Intente nuevamente.'
      )
      toast.error('Error al calcular la ruta')
    } finally {
      setIsLoading(false)
    }
  }

  const origenAtraccion = atracciones.find(a => a.id === origen)
  const destinoAtraccion = atracciones.find(a => a.id === destino)

  return (
    <Card className="border-0 shadow-sm">
      <CardHeader>
        <div className="flex items-center gap-2">
          <Navigation className="h-5 w-5 text-orange-600" />
          <CardTitle>Calcular Ruta</CardTitle>
        </div>
        <CardDescription>
          Encuentre el camino más corto entre dos atracciones
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-6">
        {error && (
          <Alert variant="destructive">
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}

        {/* Route Selection */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="space-y-2">
            <label className="text-sm font-medium">Origen</label>
            <Select value={origen} onValueChange={setOrigen}>
              <SelectTrigger>
                <SelectValue placeholder="Seleccione origen" />
              </SelectTrigger>
              <SelectContent>
                {atracciones.map((atraccion) => (
                  <SelectItem key={atraccion.id} value={atraccion.id}>
                    <div className="flex items-center gap-2">
                      <MapPin className="h-4 w-4 text-green-500" />
                      {atraccion.nombre}
                    </div>
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          
          <div className="space-y-2">
            <label className="text-sm font-medium">Destino</label>
            <Select value={destino} onValueChange={setDestino}>
              <SelectTrigger>
                <SelectValue placeholder="Seleccione destino" />
              </SelectTrigger>
              <SelectContent>
                {atracciones.map((atraccion) => (
                  <SelectItem key={atraccion.id} value={atraccion.id}>
                    <div className="flex items-center gap-2">
                      <MapPin className="h-4 w-4 text-red-500" />
                      {atraccion.nombre}
                    </div>
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </div>

        <Button 
          onClick={handleCalculateRoute}
          className="w-full bg-orange-600 hover:bg-orange-700"
          disabled={isLoading || !origen || !destino}
        >
          {isLoading ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              Calculando ruta...
            </>
          ) : (
            <>
              <Navigation className="mr-2 h-4 w-4" />
              Calcular Ruta Óptima
            </>
          )}
        </Button>

        {/* Route Result */}
        {ruta && (
          <div className="space-y-4">
            {/* Route Summary */}
            <div className="p-4 bg-orange-50 rounded-lg">
              <div className="flex items-center justify-between mb-3">
                <h4 className="font-medium text-orange-900">Ruta Encontrada</h4>
                <div className="flex items-center gap-4 text-sm">
                  <span className="flex items-center gap-1 text-orange-700">
                    <Ruler className="h-4 w-4" />
                    {ruta.distanciaTotal} m
                  </span>
                  <span className="flex items-center gap-1 text-orange-700">
                    <Clock className="h-4 w-4" />
                    {ruta.tiempoEstimado} min
                  </span>
                </div>
              </div>
              
              {/* Visual Route */}
              <div className="flex items-center gap-2 flex-wrap">
                {ruta.nodos.map((nodo, index) => (
                  <div key={nodo.id} className="flex items-center gap-2">
                    <div className={`px-3 py-1 rounded-full text-sm ${
                      index === 0 
                        ? 'bg-green-100 text-green-700' 
                        : index === ruta.nodos.length - 1
                        ? 'bg-red-100 text-red-700'
                        : 'bg-white text-gray-700'
                    }`}>
                      {nodo.nombre}
                    </div>
                    {index < ruta.nodos.length - 1 && (
                      <ArrowRight className="h-4 w-4 text-orange-400" />
                    )}
                  </div>
                ))}
              </div>
            </div>

            {/* Route Steps */}
            {ruta.pasos && ruta.pasos.length > 0 && (
              <div className="space-y-2">
                <h4 className="font-medium text-gray-900">Pasos de la Ruta</h4>
                <div className="space-y-2">
                  {ruta.pasos.map((paso, index) => (
                    <div
                      key={index}
                      className="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
                    >
                      <div className="flex items-center gap-2">
                        <span className="w-6 h-6 rounded-full bg-orange-100 text-orange-700 text-sm flex items-center justify-center font-medium">
                          {index + 1}
                        </span>
                        <span className="text-sm">
                          {paso.desde} → {paso.hacia}
                        </span>
                      </div>
                      <div className="text-sm text-gray-500">
                        {paso.distancia}m / {paso.tiempo} min
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}

        {/* Info */}
        <div className="p-4 bg-blue-50 rounded-lg text-sm text-blue-700">
          <p className="font-medium mb-1">Información:</p>
          <ul className="list-disc list-inside space-y-1">
            <li>La ruta se calcula usando el algoritmo de Dijkstra</li>
            <li>Se considera la distancia y tiempo de caminata</li>
            <li>Los tiempos son estimados y pueden variar</li>
          </ul>
        </div>
      </CardContent>
    </Card>
  )
}
