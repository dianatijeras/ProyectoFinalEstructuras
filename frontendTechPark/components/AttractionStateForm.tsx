'use client'

import { useState } from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { StatusBadge } from '@/components/StatusBadge'
import { atraccionService } from '@/services/atraccionService'
import { Settings, Loader2, CheckCircle, AlertCircle, X } from 'lucide-react'
import type { Atraccion, EstadoAtraccion } from '@/types'
import { toast } from 'sonner'

interface AttractionStateFormProps {
  atraccion: Atraccion
  onUpdated?: () => void
  onCancel?: () => void
}

const estados: { value: EstadoAtraccion; label: string; description: string }[] = [
  { value: 'ACTIVA', label: 'Activa', description: 'Funcionando normalmente' },
  { value: 'EN_MANTENIMIENTO', label: 'En Mantenimiento', description: 'En revisión técnica' },
  { value: 'CERRADA', label: 'Cerrada', description: 'No disponible' },
]

export function AttractionStateForm({ 
  atraccion,
  onUpdated,
  onCancel 
}: AttractionStateFormProps) {
  const [estado, setEstado] = useState<EstadoAtraccion>(atraccion.estado)
  const [motivo, setMotivo] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (estado !== 'ACTIVA' && !motivo.trim()) {
      setError('Debe proporcionar un motivo para el cambio de estado')
      return
    }

    setIsLoading(true)
    setError(null)

    try {
      await atraccionService.cambiarEstado(atraccion.id, {
        estado,
        motivo: motivo.trim() || undefined,
      })
      
      toast.success('Estado actualizado exitosamente')
      onUpdated?.()
    } catch (err) {
      setError(
        err instanceof Error 
          ? err.message 
          : 'Error al actualizar el estado.'
      )
      toast.error('Error al actualizar estado')
    } finally {
      setIsLoading(false)
    }
  }

  const handleRegisterRevision = async () => {
    if (!motivo.trim()) {
      setError('Debe proporcionar una descripción de la revisión')
      return
    }

    setIsLoading(true)
    setError(null)

    try {
      await atraccionService.registrarRevision(atraccion.id, {
        descripcion: motivo.trim(),
        resultado: 'PENDIENTE',
      })
      
      toast.success('Revisión técnica registrada')
      onUpdated?.()
    } catch (err) {
      setError(
        err instanceof Error 
          ? err.message 
          : 'Error al registrar la revisión.'
      )
      toast.error('Error al registrar revisión')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Card className="border-0 shadow-sm">
      <CardHeader className="pb-2">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Settings className="h-5 w-5 text-blue-600" />
            <CardTitle className="text-lg">Gestionar Atracción</CardTitle>
          </div>
          {onCancel && (
            <Button variant="ghost" size="icon" onClick={onCancel}>
              <X className="h-4 w-4" />
            </Button>
          )}
        </div>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Attraction Info */}
          <div className="p-3 bg-gray-50 rounded-lg">
            <div className="flex items-center justify-between">
              <div>
                <p className="font-medium text-gray-900">{atraccion.nombre}</p>
                <p className="text-xs text-gray-500">ID: {atraccion.id}</p>
              </div>
              <StatusBadge status={atraccion.estado} />
            </div>
          </div>

          {error && (
            <Alert variant="destructive">
              <AlertCircle className="h-4 w-4" />
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}

          {/* State Selection */}
          <div className="space-y-2">
            <Label>Nuevo Estado</Label>
            <Select value={estado} onValueChange={(v) => setEstado(v as EstadoAtraccion)}>
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {estados.map((est) => (
                  <SelectItem key={est.value} value={est.value}>
                    <div>
                      <span className="font-medium">{est.label}</span>
                      <span className="text-xs text-gray-500 ml-2">
                        - {est.description}
                      </span>
                    </div>
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          {/* Reason/Description */}
          <div className="space-y-2">
            <Label htmlFor="motivo">
              {estado === 'ACTIVA' ? 'Notas (opcional)' : 'Motivo del cambio'}
            </Label>
            <Textarea
              id="motivo"
              value={motivo}
              onChange={(e) => setMotivo(e.target.value)}
              placeholder={
                estado === 'EN_MANTENIMIENTO'
                  ? 'Describa el tipo de mantenimiento...'
                  : estado === 'CERRADA'
                  ? 'Motivo del cierre...'
                  : 'Notas adicionales...'
              }
              rows={3}
            />
          </div>

          {/* Actions */}
          <div className="space-y-2">
            <Button 
              type="submit"
              className="w-full bg-blue-600 hover:bg-blue-700"
              disabled={isLoading}
            >
              {isLoading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Actualizando...
                </>
              ) : (
                <>
                  <CheckCircle className="mr-2 h-4 w-4" />
                  Cambiar Estado
                </>
              )}
            </Button>

            {atraccion.estado === 'EN_MANTENIMIENTO' && (
              <Button 
                type="button"
                variant="outline"
                className="w-full"
                onClick={handleRegisterRevision}
                disabled={isLoading}
              >
                Registrar Revisión Técnica
              </Button>
            )}
          </div>
        </form>
      </CardContent>
    </Card>
  )
}
