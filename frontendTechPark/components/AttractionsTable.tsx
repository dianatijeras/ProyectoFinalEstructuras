'use client'

import { useState } from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { Input } from '@/components/ui/input'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { StatusBadge } from '@/components/StatusBadge'
import { Search, Filter, Clock, Users } from 'lucide-react'
import type { Atraccion, EstadoAtraccion } from '@/types'

interface AttractionsTableProps {
  atracciones: Atraccion[]
  onSelect?: (atraccion: Atraccion) => void
  showActions?: boolean
}

export function AttractionsTable({ 
  atracciones, 
  onSelect,
  showActions = false 
}: AttractionsTableProps) {
  const [search, setSearch] = useState('')
  const [statusFilter, setStatusFilter] = useState<string>('all')

  const filteredAtracciones = atracciones.filter((atraccion) => {
    const matchesSearch = 
      atraccion.nombre.toLowerCase().includes(search.toLowerCase()) ||
      atraccion.tipo.toLowerCase().includes(search.toLowerCase()) ||
      (atraccion.zonaNombre?.toLowerCase().includes(search.toLowerCase()) ?? false)
    
    const matchesStatus = 
      statusFilter === 'all' || atraccion.estado === statusFilter

    return matchesSearch && matchesStatus
  })

  const getTipoLabel = (tipo: string) => {
    const labels: Record<string, string> = {
      MONTANA_RUSA: 'Montaña Rusa',
      CARRUSEL: 'Carrusel',
      RUEDA_DE_LA_FORTUNA: 'Rueda de la Fortuna',
      CASA_DEL_TERROR: 'Casa del Terror',
      TOBOGAN_ACUATICO: 'Tobogán Acuático',
      SILLAS_VOLADORAS: 'Sillas Voladoras',
      TREN_FANTASMA: 'Tren Fantasma',
      SIMULADOR: 'Simulador',
      JUEGO_MECANICO: 'Juego Mecánico',
    }
    return labels[tipo] || tipo
  }

  return (
    <Card className="border-0 shadow-sm">
      <CardHeader className="pb-4">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <CardTitle>Atracciones</CardTitle>
          <div className="flex flex-col sm:flex-row gap-2">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
              <Input
                placeholder="Buscar atracción..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="pl-9 w-full sm:w-64"
              />
            </div>
            <Select value={statusFilter} onValueChange={setStatusFilter}>
              <SelectTrigger className="w-full sm:w-40">
                <Filter className="h-4 w-4 mr-2" />
                <SelectValue placeholder="Estado" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Todos</SelectItem>
                <SelectItem value="ACTIVA">Activas</SelectItem>
                <SelectItem value="EN_MANTENIMIENTO">En Mantenimiento</SelectItem>
                <SelectItem value="CERRADA">Cerradas</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>
      </CardHeader>
      <CardContent>
        {filteredAtracciones.length === 0 ? (
          <div className="text-center py-8 text-gray-500">
            No se encontraron atracciones
          </div>
        ) : (
          <div className="overflow-x-auto">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Nombre</TableHead>
                  <TableHead>Tipo</TableHead>
                  <TableHead>Zona</TableHead>
                  <TableHead>Estado</TableHead>
                  <TableHead className="text-center">
                    <Clock className="h-4 w-4 inline mr-1" />
                    Espera
                  </TableHead>
                  <TableHead className="text-center">
                    <Users className="h-4 w-4 inline mr-1" />
                    Cola
                  </TableHead>
                  <TableHead className="text-center">Acumulado</TableHead>
                  {showActions && <TableHead className="text-right">Acciones</TableHead>}
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredAtracciones.map((atraccion) => (
                  <TableRow 
                    key={atraccion.id}
                    className={onSelect ? 'cursor-pointer hover:bg-gray-50' : ''}
                    onClick={() => onSelect?.(atraccion)}
                  >
                    <TableCell className="font-medium">{atraccion.nombre}</TableCell>
                    <TableCell className="text-gray-600">
                      {getTipoLabel(atraccion.tipo)}
                    </TableCell>
                    <TableCell className="text-gray-600">
                      {atraccion.zonaNombre || '-'}
                    </TableCell>
                    <TableCell>
                      <StatusBadge status={atraccion.estado} />
                    </TableCell>
                    <TableCell className="text-center">
                      {atraccion.tiempoEsperaEstimado 
                        ? `${atraccion.tiempoEsperaEstimado} min`
                        : '-'
                      }
                    </TableCell>
                    <TableCell className="text-center">
                      {atraccion.colaActual ?? '-'}
                    </TableCell>
                    <TableCell className="text-center">
                      {atraccion.contadorAcumuladoVisitantes ?? 0}
                    </TableCell>
                    {showActions && (
                      <TableCell className="text-right">
                        <Button 
                          variant="outline" 
                          size="sm"
                          onClick={(e) => {
                            e.stopPropagation()
                            onSelect?.(atraccion)
                          }}
                        >
                          Ver detalles
                        </Button>
                      </TableCell>
                    )}
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
        )}
        <div className="mt-4 text-sm text-gray-500">
          Mostrando {filteredAtracciones.length} de {atracciones.length} atracciones
        </div>
      </CardContent>
    </Card>
  )
}
