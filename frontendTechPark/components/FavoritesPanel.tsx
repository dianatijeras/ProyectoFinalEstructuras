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
import { StatusBadge } from '@/components/StatusBadge'
import { visitanteService } from '@/services/visitanteService'
import { Heart, Plus, Loader2, Trash2 } from 'lucide-react'
import type { Favorito, Atraccion } from '@/types'
import { toast } from 'sonner'

interface FavoritesPanelProps {
  favoritos: Favorito[]
  atracciones: Atraccion[]
  visitanteDocumento: string
  onAddedFavorite?: () => void
}

export function FavoritesPanel({ 
  favoritos, 
  atracciones,
  visitanteDocumento,
  onAddedFavorite 
}: FavoritesPanelProps) {
  const [selectedAtraccion, setSelectedAtraccion] = useState<string>('')
  const [isLoading, setIsLoading] = useState(false)

  // Filter out attractions that are already favorites
  const availableAtracciones = atracciones.filter(
    a => !favoritos.some(f => f.atraccionId === a.id)
  )

  const handleAddFavorite = async () => {
    if (!selectedAtraccion) return

    setIsLoading(true)
    try {
      await visitanteService.addFavorito(visitanteDocumento, selectedAtraccion)
      toast.success('Atracción agregada a favoritos')
      setSelectedAtraccion('')
      onAddedFavorite?.()
    } catch (err) {
      toast.error('Error al agregar favorito')
    } finally {
      setIsLoading(false)
    }
  }

  const getAtraccionData = (atraccionId: string) => {
    return atracciones.find(a => a.id === atraccionId)
  }

  return (
    <Card className="border-0 shadow-sm">
      <CardHeader>
        <div className="flex items-center gap-2">
          <Heart className="h-5 w-5 text-red-500" />
          <CardTitle>Mis Favoritos</CardTitle>
        </div>
        <CardDescription>
          Guarde sus atracciones favoritas para acceder rápidamente
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-6">
        {/* Add Favorite */}
        <div className="flex gap-2">
          <Select value={selectedAtraccion} onValueChange={setSelectedAtraccion}>
            <SelectTrigger className="flex-1">
              <SelectValue placeholder="Agregar atracción a favoritos" />
            </SelectTrigger>
            <SelectContent>
              {availableAtracciones.map((atraccion) => (
                <SelectItem key={atraccion.id} value={atraccion.id}>
                  {atraccion.nombre}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <Button 
            onClick={handleAddFavorite}
            disabled={isLoading || !selectedAtraccion}
            className="bg-orange-600 hover:bg-orange-700"
          >
            {isLoading ? (
              <Loader2 className="h-4 w-4 animate-spin" />
            ) : (
              <Plus className="h-4 w-4" />
            )}
          </Button>
        </div>

        {/* Favorites List */}
        {favoritos.length === 0 ? (
          <div className="text-center py-8 text-gray-500">
            <Heart className="h-12 w-12 mx-auto mb-2 text-gray-300" />
            <p>No tiene atracciones favoritas</p>
            <p className="text-sm">Agregue atracciones para acceder rápidamente</p>
          </div>
        ) : (
          <div className="space-y-3">
            {favoritos.map((favorito) => {
              const atraccion = getAtraccionData(favorito.atraccionId)
              return (
                <div
                  key={favorito.atraccionId}
                  className="flex items-center justify-between p-4 bg-gray-50 rounded-lg"
                >
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-full bg-red-100 flex items-center justify-center">
                      <Heart className="h-5 w-5 text-red-500 fill-red-500" />
                    </div>
                    <div>
                      <p className="font-medium">{favorito.atraccionNombre}</p>
                      <p className="text-xs text-gray-500">
                        Agregado: {new Date(favorito.fechaAgregado).toLocaleDateString('es-CO')}
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    {atraccion && (
                      <StatusBadge status={atraccion.estado} />
                    )}
                  </div>
                </div>
              )
            })}
          </div>
        )}
      </CardContent>
    </Card>
  )
}
