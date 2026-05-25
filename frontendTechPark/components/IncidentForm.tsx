'use client'

import { useState } from 'react'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Textarea } from '@/components/ui/textarea'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Label } from '@/components/ui/label'
import { Input } from '@/components/ui/input'
import { incidentesService } from '@/services/incidentesService'
import type { Atraccion } from '@/types'
import { AlertTriangle, Loader2 } from 'lucide-react'
import { toast } from 'sonner'

export function IncidentForm({ atracciones, onCreated }: { atracciones: Atraccion[]; onCreated?: () => void }) {
  const [idAtraccion, setIdAtraccion] = useState(atracciones[0]?.id || '')
  const [descripcion, setDescripcion] = useState('')
  const [gravedad, setGravedad] = useState('MEDIA')
  const [loading, setLoading] = useState(false)

  const submit = async () => {
    setLoading(true)
    try {
      await incidentesService.registrar({ idAtraccion, descripcion, gravedad })
      setDescripcion('')
      toast.success('Incidente registrado')
      onCreated?.()
    } catch (err) {
      toast.error(err instanceof Error ? err.message : 'No se pudo registrar el incidente')
    } finally {
      setLoading(false)
    }
  }

  return (
    <Card className="border-0 shadow-sm">
      <CardHeader><CardTitle className="flex items-center gap-2"><AlertTriangle className="h-5 w-5 text-orange-600" />Registrar incidente operativo</CardTitle><CardDescription>Alimenta el apartado de incidentes en reportes</CardDescription></CardHeader>
      <CardContent className="space-y-3">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
          <div className="space-y-2"><Label>Atracción</Label><Select value={idAtraccion} onValueChange={setIdAtraccion}><SelectTrigger><SelectValue placeholder="Atracción" /></SelectTrigger><SelectContent>{atracciones.map(a => <SelectItem key={a.id} value={a.id}>{a.nombre}</SelectItem>)}</SelectContent></Select></div>
          <div className="space-y-2"><Label>Gravedad</Label><Select value={gravedad} onValueChange={setGravedad}><SelectTrigger><SelectValue /></SelectTrigger><SelectContent><SelectItem value="BAJA">Baja</SelectItem><SelectItem value="MEDIA">Media</SelectItem><SelectItem value="ALTA">Alta</SelectItem></SelectContent></Select></div>
        </div>
        <Textarea placeholder="Descripción del incidente" value={descripcion} onChange={(e) => setDescripcion(e.target.value)} />
        <Button disabled={loading || !idAtraccion || !descripcion.trim()} onClick={submit}>{loading ? <Loader2 className="h-4 w-4 animate-spin" /> : 'Registrar incidente'}</Button>
      </CardContent>
    </Card>
  )
}
