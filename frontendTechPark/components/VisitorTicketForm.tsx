'use client'

import { useState } from 'react'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
import { Input } from '@/components/ui/input'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { visitanteService } from '@/services/visitanteService'
import { Ticket, Loader2, CheckCircle, AlertCircle } from 'lucide-react'
import type { Zona, TipoTicket } from '@/types'
import { toast } from 'sonner'

interface VisitorTicketFormProps {
  zonas: Zona[]
  visitanteDocumento: string
  onTicketPurchased?: () => void
}

const ticketTypes: { value: TipoTicket; label: string; description: string; price: string }[] = [
  { 
    value: 'GENERAL', 
    label: 'General', 
    description: 'Acceso básico a todas las zonas',
    price: '$50.000'
  },
  { 
    value: 'FAMILIAR', 
    label: 'Familiar', 
    description: 'Acceso para grupo familiar (hasta 5 personas)',
    price: '$180.000'
  },
  { 
    value: 'FAST_PASS', 
    label: 'Fast Pass', 
    description: 'Acceso prioritario sin filas de espera',
    price: '$120.000'
  },
]

export function VisitorTicketForm({ 
  zonas, 
  visitanteDocumento,
  onTicketPurchased 
}: VisitorTicketFormProps) {
  const [tipoTicket, setTipoTicket] = useState<TipoTicket>('GENERAL')
  const [zonaId, setZonaId] = useState<string>('__TODAS__')
  const [grupoFamiliar, setGrupoFamiliar] = useState<number>(2)
  const [isLoading, setIsLoading] = useState(false)
  const [success, setSuccess] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setError(null)
    setSuccess(false)

    try {
      await visitanteService.comprarTicket({
        documentoVisitante: visitanteDocumento,
        tipo: tipoTicket,
        zonaId: zonaId === '__TODAS__' ? undefined : zonaId,
        grupoFamiliar: tipoTicket === 'FAMILIAR' ? grupoFamiliar : undefined,
      })
      
      setSuccess(true)
      toast.success('Ticket comprado exitosamente')
      onTicketPurchased?.()
    } catch (err) {
      setError(
        err instanceof Error 
          ? err.message 
          : 'Error al comprar el ticket. Intente nuevamente.'
      )
      toast.error('Error al comprar el ticket')
    } finally {
      setIsLoading(false)
    }
  }

  const selectedTicketType = ticketTypes.find(t => t.value === tipoTicket)

  return (
    <Card className="border-0 shadow-sm">
      <CardHeader>
        <div className="flex items-center gap-2">
          <Ticket className="h-5 w-5 text-orange-600" />
          <CardTitle>Comprar Ticket</CardTitle>
        </div>
        <CardDescription>
          Seleccione el tipo de ticket y zona de acceso
        </CardDescription>
      </CardHeader>
      <CardContent>
        {success ? (
          <Alert className="bg-green-50 border-green-200">
            <CheckCircle className="h-4 w-4 text-green-600" />
            <AlertDescription className="text-green-800">
              Ticket comprado exitosamente. Disfrute su visita al parque.
            </AlertDescription>
          </Alert>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-6">
            {error && (
              <Alert variant="destructive">
                <AlertCircle className="h-4 w-4" />
                <AlertDescription>{error}</AlertDescription>
              </Alert>
            )}

            {/* Ticket Type Selection */}
            <div className="space-y-3">
              <Label>Tipo de Ticket</Label>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                {ticketTypes.map((type) => (
                  <div
                    key={type.value}
                    className={`p-4 rounded-lg border-2 cursor-pointer transition-all ${
                      tipoTicket === type.value
                        ? 'border-orange-500 bg-orange-50'
                        : 'border-gray-200 hover:border-gray-300'
                    }`}
                    onClick={() => setTipoTicket(type.value)}
                  >
                    <div className="flex items-center justify-between mb-2">
                      <span className="font-medium">{type.label}</span>
                      <span className="text-orange-600 font-bold">{type.price}</span>
                    </div>
                    <p className="text-sm text-gray-500">{type.description}</p>
                  </div>
                ))}
              </div>
            </div>

            {/* Family Size (only for FAMILIAR) */}
            {tipoTicket === 'FAMILIAR' && (
              <div className="space-y-2">
                <Label htmlFor="grupoFamiliar">Tamaño del Grupo Familiar</Label>
                <Select 
                  value={grupoFamiliar.toString()} 
                  onValueChange={(v) => setGrupoFamiliar(parseInt(v))}
                >
                  <SelectTrigger id="grupoFamiliar">
                    <SelectValue placeholder="Seleccione tamaño" />
                  </SelectTrigger>
                  <SelectContent>
                    {[2, 3, 4, 5].map((size) => (
                      <SelectItem key={size} value={size.toString()}>
                        {size} personas
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            )}

            {/* Zone Selection */}
            <div className="space-y-2">
              <Label htmlFor="zona">Zona de Acceso (Opcional)</Label>
              <Select value={zonaId} onValueChange={setZonaId}>
                <SelectTrigger id="zona">
                  <SelectValue placeholder="Todas las zonas" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="__TODAS__">Todas las zonas</SelectItem>
                  {zonas.map((zona) => (
                    <SelectItem key={zona.id} value={zona.id}>
                      {zona.nombre}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              <p className="text-xs text-gray-500">
                Puede seleccionar una zona específica o acceder a todas
              </p>
            </div>

            {/* Summary */}
            <div className="p-4 bg-gray-50 rounded-lg">
              <h4 className="font-medium mb-2">Resumen de Compra</h4>
              <div className="space-y-1 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-600">Tipo de Ticket:</span>
                  <span className="font-medium">{selectedTicketType?.label}</span>
                </div>
                {tipoTicket === 'FAMILIAR' && (
                  <div className="flex justify-between">
                    <span className="text-gray-600">Personas:</span>
                    <span className="font-medium">{grupoFamiliar}</span>
                  </div>
                )}
                <div className="flex justify-between">
                  <span className="text-gray-600">Zona:</span>
                  <span className="font-medium">
                    {zonaId !== '__TODAS__'
                      ? zonas.find(z => z.id === zonaId)?.nombre
                      : 'Todas las zonas'
                    }
                  </span>
                </div>
                <div className="flex justify-between pt-2 border-t mt-2">
                  <span className="font-medium">Total:</span>
                  <span className="font-bold text-orange-600">
                    {selectedTicketType?.price}
                  </span>
                </div>
              </div>
            </div>

            <Button 
              type="submit" 
              className="w-full bg-orange-600 hover:bg-orange-700"
              disabled={isLoading}
            >
              {isLoading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Procesando...
                </>
              ) : (
                <>
                  <Ticket className="mr-2 h-4 w-4" />
                  Comprar Ticket
                </>
              )}
            </Button>
          </form>
        )}
      </CardContent>
    </Card>
  )
}
