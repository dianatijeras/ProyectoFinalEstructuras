'use client'

import { useState, useMemo } from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Badge } from '@/components/ui/badge'
import { StatusBadge } from '@/components/StatusBadge'
import { mapaService } from '@/services/mapaService'
import { Map, ZoomIn, ZoomOut, RotateCcw, Navigation, Loader2 } from 'lucide-react'
import type { MapaParque, Atraccion, Ruta, NodoMapa, EstadoAtraccion } from '@/types'
import { toast } from 'sonner'

interface GraphMapProps {
  mapa: MapaParque
  atracciones: Atraccion[]
  ubicacionActualId?: string
}

export function GraphMap({ mapa, atracciones, ubicacionActualId }: GraphMapProps) {
  const [zoom, setZoom] = useState(1)
  const [selectedNode, setSelectedNode] = useState<NodoMapa | null>(null)
  const [highlightedRoute, setHighlightedRoute] = useState<Ruta | null>(null)
  const [origen, setOrigen] = useState<string>('')
  const [destino, setDestino] = useState<string>('')
  const [isCalculating, setIsCalculating] = useState(false)

  // Calculate viewport bounds from nodes
  const bounds = useMemo(() => {
    if (mapa.nodos.length === 0) return { minX: 0, maxX: 100, minY: 0, maxY: 100 }
    
    const xs = mapa.nodos.map(n => n.x)
    const ys = mapa.nodos.map(n => n.y)
    
    return {
      minX: Math.min(...xs) - 10,
      maxX: Math.max(...xs) + 10,
      minY: Math.min(...ys) - 10,
      maxY: Math.max(...ys) + 10,
    }
  }, [mapa.nodos])

  // Get attraction data for a node
  const getAtraccionForNode = (nodeId: string): Atraccion | undefined => {
    return atracciones.find(a => a.id === nodeId)
  }

  // Get color based on state
  const getNodeColor = (estado?: EstadoAtraccion): string => {
    switch (estado) {
      case 'ACTIVA':
        return '#22c55e' // green-500
      case 'EN_MANTENIMIENTO':
        return '#f59e0b' // amber-500
      case 'CERRADA':
        return '#ef4444' // red-500
      default:
        return '#6b7280' // gray-500
    }
  }

  // Transform node coordinates to SVG space.
  // Se deja padding interno para que nodos y textos no queden cortados por los bordes.
  const SVG_PADDING = 12
  const SVG_INNER_SIZE = 100 - SVG_PADDING * 2

  const transformX = (x: number) => {
    const width = bounds.maxX - bounds.minX || 1
    return SVG_PADDING + ((x - bounds.minX) / width) * SVG_INNER_SIZE
  }

  const transformY = (y: number) => {
    const height = bounds.maxY - bounds.minY || 1
    return SVG_PADDING + ((y - bounds.minY) / height) * SVG_INNER_SIZE
  }

  // Check if edge is part of highlighted route
  const isEdgeInRoute = (origenId: string, destinoId: string): boolean => {
    if (!highlightedRoute) return false
    
    for (let i = 0; i < highlightedRoute.nodos.length - 1; i++) {
      const from = highlightedRoute.nodos[i].id
      const to = highlightedRoute.nodos[i + 1].id
      if ((from === origenId && to === destinoId) || (from === destinoId && to === origenId)) {
        return true
      }
    }
    return false
  }

  // Calculate route
  const handleCalculateRoute = async () => {
    if (!origen || !destino || origen === destino) return

    setIsCalculating(true)
    try {
      const ruta = await mapaService.getRuta(origen, destino)
      setHighlightedRoute(ruta)
      toast.success('Ruta calculada')
    } catch (err) {
      toast.error('Error al calcular ruta')
    } finally {
      setIsCalculating(false)
    }
  }

  const clearRoute = () => {
    setHighlightedRoute(null)
    setOrigen('')
    setDestino('')
  }

  const formatEdgeWeight = (arista: { peso?: number; distanciaMetros?: number; tiempoMinutos?: number }) => {
    const peso = arista.peso ?? arista.distanciaMetros ?? arista.tiempoMinutos ?? 0
    return peso > 0 ? `${peso}` : '0'
  }

  const zoneGroups = useMemo(() => {
    const groups = new globalThis.Map<string, { nombre: string; disponible: boolean; nodos: NodoMapa[] }>()
    for (const nodo of mapa.nodos) {
      const key = nodo.zonaId || nodo.zonaNombre || 'SIN_ZONA'
      const actual = groups.get(key) || { nombre: nodo.zonaNombre || 'Sin zona', disponible: nodo.zonaDisponible !== false, nodos: [] }
      actual.disponible = actual.disponible && nodo.zonaDisponible !== false
      actual.nodos.push(nodo)
      groups.set(key, actual)
    }

    return Array.from(groups.entries()).map(([id, group]) => {
      const xs = group.nodos.map(n => transformX(n.x))
      const ys = group.nodos.map(n => transformY(n.y))
      const minX = Math.min(...xs)
      const maxX = Math.max(...xs)
      const minY = Math.min(...ys)
      const maxY = Math.max(...ys)
      const nombreNormalizado = group.nombre
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .toLowerCase()

      // Aventura y Acuática tenían más espacio abajo; por eso su título se ubica al final del grupo.
      const titlePosition: 'top' | 'bottom' =
        nombreNormalizado.includes('aventura') || nombreNormalizado.includes('acuatica')
          ? 'bottom'
          : 'top'

      const zonePaddingX = 12
      const zonePaddingTop = titlePosition === 'top' ? 12 : 9
      const zonePaddingBottom = titlePosition === 'bottom' ? 15 : 10
      const x = Math.max(3, minX - zonePaddingX)
      const y = Math.max(3, minY - zonePaddingTop)
      const width = Math.min(94 - x, maxX - minX + zonePaddingX * 2)
      const height = Math.min(94 - y, maxY - minY + zonePaddingTop + zonePaddingBottom)

      return {
        id,
        nombre: group.nombre,
        disponible: group.disponible,
        titlePosition,
        x,
        y,
        width,
        height,
        labelY: titlePosition === 'bottom' ? y + height - 4 : y + 5,
      }
    })
  }, [mapa.nodos, bounds])

  return (
    <div className="space-y-4">
      {/* Route Calculator */}
      <Card className="border-0 shadow-sm">
        <CardContent className="p-4">
          <div className="flex flex-wrap items-end gap-4">
            <div className="flex-1 min-w-48">
              <label className="text-sm font-medium text-gray-700 mb-1 block">Origen</label>
              <Select value={origen} onValueChange={setOrigen}>
                <SelectTrigger>
                  <SelectValue placeholder="Seleccione origen" />
                </SelectTrigger>
                <SelectContent>
                  {mapa.nodos.map((nodo) => (
                    <SelectItem key={nodo.id} value={nodo.id}>
                      {nodo.nombre}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="flex-1 min-w-48">
              <label className="text-sm font-medium text-gray-700 mb-1 block">Destino</label>
              <Select value={destino} onValueChange={setDestino}>
                <SelectTrigger>
                  <SelectValue placeholder="Seleccione destino" />
                </SelectTrigger>
                <SelectContent>
                  {mapa.nodos.map((nodo) => (
                    <SelectItem key={nodo.id} value={nodo.id}>
                      {nodo.nombre}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <Button 
              onClick={handleCalculateRoute}
              disabled={!origen || !destino || origen === destino || isCalculating}
              className="bg-orange-600 hover:bg-orange-700"
            >
              {isCalculating ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                <>
                  <Navigation className="h-4 w-4 mr-2" />
                  Calcular
                </>
              )}
            </Button>
            {highlightedRoute && (
              <Button variant="outline" onClick={clearRoute}>
                Limpiar
              </Button>
            )}
          </div>

          {/* Route Info */}
          {highlightedRoute && (
            <div className="mt-4 p-3 bg-orange-50 rounded-lg">
              <div className="flex items-center justify-between">
                <span className="font-medium text-orange-900">Ruta encontrada</span>
                <div className="flex gap-4 text-sm text-orange-700">
                  <span>{highlightedRoute.distanciaTotal}m</span>
                  <span>{highlightedRoute.tiempoEstimado} min</span>
                </div>
              </div>
              <div className="mt-2 flex flex-wrap gap-1">
                {highlightedRoute.nodos.map((nodo, i) => (
                  <span key={nodo.id} className="text-sm">
                    <span className={`px-2 py-0.5 rounded ${
                      i === 0 ? 'bg-green-200 text-green-800' : 
                      i === highlightedRoute.nodos.length - 1 ? 'bg-red-200 text-red-800' : 
                      'bg-white text-gray-700'
                    }`}>
                      {nodo.nombre}
                    </span>
                    {i < highlightedRoute.nodos.length - 1 && (
                      <span className="text-orange-400 mx-1">→</span>
                    )}
                  </span>
                ))}
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      {/* Map */}
      <Card className="border-0 shadow-sm">
        <CardHeader className="pb-2">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Map className="h-5 w-5 text-orange-600" />
              <CardTitle className="text-lg">Visualización del Grafo</CardTitle>
            </div>
            <div className="flex items-center gap-2">
              <Button 
                variant="outline" 
                size="icon" 
                onClick={() => setZoom(z => Math.max(0.5, z - 0.25))}
              >
                <ZoomOut className="h-4 w-4" />
              </Button>
              <span className="text-sm text-gray-500 w-12 text-center">
                {Math.round(zoom * 100)}%
              </span>
              <Button 
                variant="outline" 
                size="icon" 
                onClick={() => setZoom(z => Math.min(2, z + 0.25))}
              >
                <ZoomIn className="h-4 w-4" />
              </Button>
              <Button 
                variant="outline" 
                size="icon" 
                onClick={() => { setZoom(1); setSelectedNode(null) }}
              >
                <RotateCcw className="h-4 w-4" />
              </Button>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          {/* Legend */}
          <div className="flex flex-wrap gap-4 mb-4 text-sm">
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 rounded-full bg-green-500" />
              <span>Activa</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 rounded-full bg-amber-500" />
              <span>Mantenimiento</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 rounded-full bg-red-500" />
              <span>Cerrada</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 rounded-full bg-gray-500" />
              <span>Otro</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 rounded border border-green-500 bg-green-50" />
              <span>Zona disponible</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 rounded border border-red-500 bg-red-50" />
              <span>Zona no disponible</span>
            </div>
            {ubicacionActualId && (
              <div className="flex items-center gap-2">
                <div className="w-4 h-4 rounded-full border-2 border-purple-600 bg-purple-100" />
                <span>Tu ubicación actual</span>
              </div>
            )}
          </div>

          {/* SVG Map */}
          <div 
            className="bg-gray-50 rounded-lg overflow-hidden"
            style={{ height: '500px' }}
          >
            <svg
              viewBox="0 0 100 100"
              className="w-full h-full"
              style={{ transform: `scale(${zoom})`, transformOrigin: 'center' }}
            >

              {/* Zone groups */}
              {zoneGroups.map((zona) => (
                <g key={`zona-${zona.id}`}>
                  <rect
                    x={zona.x}
                    y={zona.y}
                    width={zona.width}
                    height={zona.height}
                    rx="2"
                    fill={zona.disponible ? '#ecfdf5' : '#fef2f2'}
                    stroke={zona.disponible ? '#10b981' : '#ef4444'}
                    strokeWidth="0.25"
                    strokeDasharray="1,1"
                    opacity="0.75"
                  />
                  <text
                    x={zona.x + zona.width / 2}
                    y={zona.labelY}
                    fontSize="3"
                    fill={zona.disponible ? '#047857' : '#b91c1c'}
                    fontWeight="bold"
                    textAnchor="middle"
                    stroke="#ffffff"
                    strokeWidth="0.55"
                    paintOrder="stroke"
                  >
                    {zona.nombre} · {zona.disponible ? 'Disponible' : 'No disponible'}
                  </text>
                </g>
              ))}

              {/* Edges */}
              {mapa.aristas.map((arista, index) => {
                const fromNode = mapa.nodos.find(n => n.id === arista.origenId)
                const toNode = mapa.nodos.find(n => n.id === arista.destinoId)
                if (!fromNode || !toNode) return null

                const isHighlighted = isEdgeInRoute(arista.origenId, arista.destinoId)

                return (
                  <g key={index}>
                    <line
                      x1={transformX(fromNode.x)}
                      y1={transformY(fromNode.y)}
                      x2={transformX(toNode.x)}
                      y2={transformY(toNode.y)}
                      stroke={isHighlighted ? '#0d9488' : '#d1d5db'}
                      strokeWidth={isHighlighted ? 0.8 : 0.3}
                      strokeDasharray={isHighlighted ? '' : '1,1'}
                    />
                    {/* Weight label */}
                    <text
                      x={(transformX(fromNode.x) + transformX(toNode.x)) / 2}
                      y={(transformY(fromNode.y) + transformY(toNode.y)) / 2}
                      fontSize="3"
                      fill="#374151"
                      stroke="#ffffff"
                      strokeWidth="0.8"
                      paintOrder="stroke"
                      textAnchor="middle"
                    >
                      {formatEdgeWeight(arista)}
                    </text>
                  </g>
                )
              })}

              {/* Nodes */}
              {mapa.nodos.map((nodo) => {
                const atraccion = getAtraccionForNode(nodo.id)
                const isSelected = selectedNode?.id === nodo.id
                const isInRoute = highlightedRoute?.nodos.some(n => n.id === nodo.id)
                const isStart = highlightedRoute?.nodos[0]?.id === nodo.id
                const isEnd = highlightedRoute?.nodos[highlightedRoute.nodos.length - 1]?.id === nodo.id
                const isCurrentLocation = ubicacionActualId === nodo.id

                return (
                  <g
                    key={nodo.id}
                    onClick={() => setSelectedNode(nodo)}
                    style={{ cursor: 'pointer' }}
                  >
                    {/* Node circle */}
                    <circle
                      cx={transformX(nodo.x)}
                      cy={transformY(nodo.y)}
                      r={isCurrentLocation ? 4.6 : isSelected ? 4 : isInRoute ? 3.5 : 3}
                      fill={getNodeColor(atraccion?.estado || nodo.estado)}
                      stroke={isCurrentLocation ? '#7c3aed' : isStart ? '#22c55e' : isEnd ? '#ef4444' : isSelected ? '#0d9488' : 'white'}
                      strokeWidth={isCurrentLocation ? 1.3 : isSelected || isInRoute ? 1 : 0.5}
                    />
                    {/* Node label */}
                    <text
                      x={transformX(nodo.x)}
                      y={transformY(nodo.y) + 6}
                      fontSize="2.25"
                      fill="#374151"
                      textAnchor="middle"
                      fontWeight={isSelected || isCurrentLocation ? 'bold' : 'normal'}
                      stroke="#ffffff"
                      strokeWidth="0.35"
                      paintOrder="stroke"
                    >
                      {nodo.nombre.includes(' ') ? (
                        <>
                          <tspan x={transformX(nodo.x)} dy="0">{nodo.nombre.split(' ')[0]}</tspan>
                          <tspan x={transformX(nodo.x)} dy="2.7">{nodo.nombre.split(' ').slice(1).join(' ')}</tspan>
                        </>
                      ) : nodo.nombre}
                    </text>
                  </g>
                )
              })}
            </svg>
          </div>

          {/* Selected Node Info */}
          {selectedNode && (
            <div className="mt-4 p-4 bg-gray-50 rounded-lg">
              <div className="flex items-center justify-between">
                <div>
                  <h4 className="font-medium text-gray-900">{selectedNode.nombre}</h4>
                  <p className="text-sm text-gray-500">
                    ID: {selectedNode.id} | Posición: ({selectedNode.x}, {selectedNode.y})
                  </p>
                </div>
                {getAtraccionForNode(selectedNode.id) && (
                  <StatusBadge status={getAtraccionForNode(selectedNode.id)!.estado} />
                )}
              </div>
              {selectedNode.zonaNombre && (
                <p className="text-sm text-gray-600 mt-1">
                  Zona: {selectedNode.zonaNombre}
                </p>
              )}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
