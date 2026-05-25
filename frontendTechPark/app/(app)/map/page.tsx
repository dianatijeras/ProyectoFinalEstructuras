'use client'

import { useState, useEffect } from 'react'
import { GraphMap } from '@/components/GraphMap'
import { RoutePlanner } from '@/components/RoutePlanner'
import { mapaService } from '@/services/mapaService'
import { atraccionService } from '@/services/atraccionService'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { Alert, AlertDescription } from '@/components/ui/alert'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { Skeleton } from '@/components/ui/skeleton'
import { Map, Navigation, List, AlertTriangle } from 'lucide-react'
import type { MapaParque, Atraccion } from '@/types'

export default function MapPage() {
  const [mapa, setMapa] = useState<MapaParque | null>(null)
  const [atracciones, setAtracciones] = useState<Atraccion[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    setIsLoading(true)
    setError(null)

    try {
      const [mapaData, atraccionesData] = await Promise.all([
        mapaService.getMapa(),
        atraccionService.getAll(),
      ])

      setMapa(mapaData)
      setAtracciones(atraccionesData)
    } catch (err) {
      console.error('Error loading map data:', err)
      setError('No se pudo conectar con el servidor.')
    } finally {
      setIsLoading(false)
    }
  }

  if (isLoading) {
    return <MapSkeleton />
  }

  return (
    <div className="space-y-6">
      {/* Error Alert */}
      {error && (
        <Alert className="bg-yellow-50 border-yellow-200">
          <AlertTriangle className="h-4 w-4 text-yellow-600" />
          <AlertDescription className="text-yellow-800">
            {error}
          </AlertDescription>
        </Alert>
      )}

      {/* Header */}
      <div className="flex items-center gap-3">
        <div className="w-12 h-12 rounded-lg bg-orange-100 flex items-center justify-center">
          <Map className="h-6 w-6 text-orange-600" />
        </div>
        <div>
          <h2 className="text-2xl font-bold text-gray-900">Mapa del Parque</h2>
          <p className="text-gray-500">
            Visualización del grafo y cálculo de rutas
          </p>
        </div>
      </div>

      {/* Main Tabs */}
      <Tabs defaultValue="map" className="space-y-4">
        <TabsList>
          <TabsTrigger value="map" className="flex items-center gap-1">
            <Map className="h-4 w-4" />
            Mapa Visual
          </TabsTrigger>
          <TabsTrigger value="route" className="flex items-center gap-1">
            <Navigation className="h-4 w-4" />
            Calcular Ruta
          </TabsTrigger>
          <TabsTrigger value="data" className="flex items-center gap-1">
            <List className="h-4 w-4" />
            Datos del Grafo
          </TabsTrigger>
        </TabsList>

        <TabsContent value="map">
          {mapa ? (
            <GraphMap mapa={mapa} atracciones={atracciones} />
          ) : (
            <Card className="border-0 shadow-sm">
              <CardContent className="py-12 text-center text-gray-500">
                <Map className="h-16 w-16 mx-auto mb-4 text-gray-300" />
                <p>No hay datos del mapa disponibles</p>
                <p className="text-sm">Cargue datos de ejemplo desde el panel de administración</p>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="route">
          <RoutePlanner atracciones={atracciones} />
        </TabsContent>

        <TabsContent value="data">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Nodes Table */}
            <Card className="border-0 shadow-sm">
              <CardHeader>
                <CardTitle className="text-lg">Nodos (Atracciones)</CardTitle>
              </CardHeader>
              <CardContent>
                {mapa && mapa.nodos.length > 0 ? (
                  <div className="overflow-x-auto max-h-96">
                    <Table>
                      <TableHeader>
                        <TableRow>
                          <TableHead>ID</TableHead>
                          <TableHead>Nombre</TableHead>
                          <TableHead>Tipo</TableHead>
                          <TableHead>Posición</TableHead>
                        </TableRow>
                      </TableHeader>
                      <TableBody>
                        {mapa.nodos.map((nodo) => (
                          <TableRow key={nodo.id}>
                            <TableCell className="font-mono text-xs">{nodo.id}</TableCell>
                            <TableCell className="font-medium">{nodo.nombre}</TableCell>
                            <TableCell className="text-gray-600">{nodo.tipo}</TableCell>
                            <TableCell className="text-gray-500">
                              ({nodo.x}, {nodo.y})
                            </TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </div>
                ) : (
                  <p className="text-center py-4 text-gray-500">No hay nodos disponibles</p>
                )}
              </CardContent>
            </Card>

            {/* Edges Table */}
            <Card className="border-0 shadow-sm">
              <CardHeader>
                <CardTitle className="text-lg">Aristas (Caminos)</CardTitle>
              </CardHeader>
              <CardContent>
                {mapa && mapa.aristas.length > 0 ? (
                  <div className="overflow-x-auto max-h-96">
                    <Table>
                      <TableHeader>
                        <TableRow>
                          <TableHead>Origen</TableHead>
                          <TableHead>Destino</TableHead>
                          <TableHead className="text-right">Peso</TableHead>
                        </TableRow>
                      </TableHeader>
                      <TableBody>
                        {mapa.aristas.map((arista, index) => (
                          <TableRow key={index}>
                            <TableCell className="font-mono text-xs">{arista.origenId}</TableCell>
                            <TableCell className="font-mono text-xs">{arista.destinoId}</TableCell>
                            <TableCell className="text-right">
                              {arista.peso}
                              {arista.distanciaMetros && (
                                <span className="text-gray-400 text-xs ml-1">
                                  ({arista.distanciaMetros}m)
                                </span>
                              )}
                            </TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </div>
                ) : (
                  <p className="text-center py-4 text-gray-500">No hay aristas disponibles</p>
                )}
              </CardContent>
            </Card>
          </div>

          {/* Graph Summary */}
          {mapa && (
            <Card className="border-0 shadow-sm mt-6 bg-gray-50">
              <CardContent className="p-4">
                <div className="flex items-center gap-8 justify-center">
                  <div className="text-center">
                    <p className="text-3xl font-bold text-gray-900">{mapa.nodos.length}</p>
                    <p className="text-sm text-gray-600">Nodos</p>
                  </div>
                  <div className="h-12 w-px bg-gray-300" />
                  <div className="text-center">
                    <p className="text-3xl font-bold text-gray-900">{mapa.aristas.length}</p>
                    <p className="text-sm text-gray-600">Aristas</p>
                  </div>
                  <div className="h-12 w-px bg-gray-300" />
                  <div className="text-center">
                    <p className="text-3xl font-bold text-gray-900">
                      {mapa.aristas.length > 0 
                        ? (mapa.aristas.reduce((sum, a) => sum + a.peso, 0) / mapa.aristas.length).toFixed(1)
                        : 0
                      }
                    </p>
                    <p className="text-sm text-gray-600">Peso Promedio</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}
        </TabsContent>
      </Tabs>
    </div>
  )
}

function MapSkeleton() {
  return (
    <div className="space-y-6">
      <div className="flex items-center gap-3">
        <Skeleton className="w-12 h-12 rounded-lg" />
        <div className="space-y-2">
          <Skeleton className="h-8 w-48" />
          <Skeleton className="h-4 w-64" />
        </div>
      </div>
      <Skeleton className="h-12 w-72" />
      <Skeleton className="h-[500px]" />
    </div>
  )
}
