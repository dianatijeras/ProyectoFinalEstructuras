'use client'

import { useState, useEffect } from 'react'
import { useAuth } from '@/contexts/AuthContext'
import { VisitorTicketForm } from '@/components/VisitorTicketForm'
import { QueuePanel } from '@/components/QueuePanel'
import { FavoritesPanel } from '@/components/FavoritesPanel'
import { HistoryPanel } from '@/components/HistoryPanel'
import { RoutePlanner } from '@/components/RoutePlanner'
import { GraphMap } from '@/components/GraphMap'
import { visitanteService } from '@/services/visitanteService'
import { atraccionService } from '@/services/atraccionService'
import { parqueService } from '@/services/parqueService'
import { mapaService } from '@/services/mapaService'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Badge } from '@/components/ui/badge'
import { Skeleton } from '@/components/ui/skeleton'
import { 
  Ticket, 
  Clock, 
  Heart, 
  History, 
  MapPin,
  User,
  AlertTriangle,
  Wallet,
  UserCircle 
} from 'lucide-react'
import type { 
  Visitante, 
  Atraccion, 
  Zona, 
  HistorialVisita, 
  Favorito,
  ShowParque,
  MapaParque,
} from '@/types'
import { toast } from 'sonner'

export default function VisitorPage() {
  const { user } = useAuth()
  const [visitante, setVisitante] = useState<Visitante | null>(null)
  const [atracciones, setAtracciones] = useState<Atraccion[]>([])
  const [zonas, setZonas] = useState<Zona[]>([])
  const [historial, setHistorial] = useState<HistorialVisita[]>([])
  const [favoritos, setFavoritos] = useState<Favorito[]>([])
  const [shows, setShows] = useState<ShowParque[]>([])
  const [mapa, setMapa] = useState<MapaParque | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [valorRecarga, setValorRecarga] = useState<number>(10000)
  const [recargando, setRecargando] = useState(false)

  useEffect(() => {
    loadData()
  }, [user])

  const loadData = async () => {
    setIsLoading(true)
    setError(null)

    try {
      const documento = user?.documento || '1001'
      
      const [
        atraccionesData, 
        zonasData,
        showsData,
        mapaData,
      ] = await Promise.all([
        atraccionService.getAll(),
        parqueService.getZonas(),
        visitanteService.getShows(),
        mapaService.getMapa(),
      ])

      setAtracciones(atraccionesData)
      setZonas(zonasData)
      setShows(showsData)
      setMapa(mapaData)

      // Try to load visitor-specific data
      try {
        const [visitanteData, historialData, favoritosData] = await Promise.all([
          visitanteService.getByDocumento(documento),
          visitanteService.getHistorial(documento),
          visitanteService.getFavoritos(documento),
        ])
        setVisitante(visitanteData)
        setHistorial(historialData)
        setFavoritos(favoritosData)
      } catch {
        // Visitor data might not be available, that's ok
        setVisitante({
          documento: documento,
          nombre: user?.nombre || 'Visitante',
          visitasHoy: 0,
        })
      }
    } catch (err) {
      console.error('Error loading visitor data:', err)
      setError('No se pudo conectar con el servidor.')
    } finally {
      setIsLoading(false)
    }
  }

  const handleTicketPurchased = () => {
    loadData()
  }

  const handleJoinedQueue = () => {
    loadData()
  }

  const handleAddedFavorite = () => {
    loadData()
  }

  const handleRecargarSaldo = async () => {
    const documento = visitante?.documento || user?.documento
    if (!documento) return
    if (valorRecarga <= 0) {
      toast.error('La recarga debe ser mayor que cero')
      return
    }
    setRecargando(true)
    try {
      const actualizado = await visitanteService.recargarSaldo(documento, valorRecarga)
      setVisitante(actualizado)
      toast.success('Saldo recargado')
      loadData()
    } catch (err) {
      toast.error(err instanceof Error ? err.message : 'No se pudo recargar saldo')
    } finally {
      setRecargando(false)
    }
  }


  if (isLoading) {
    return <VisitorSkeleton />
  }

  const activeAtracciones = atracciones.filter(a => a.estado === 'ACTIVA')

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

      {/* Visitor Info Card */}
      <Card className="border-0 shadow-sm bg-gradient-to-r from-orange-500 to-orange-600 text-white">
        <CardContent className="p-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <div className="w-16 h-16 rounded-full bg-white/20 flex items-center justify-center">
                <User className="h-8 w-8" />
              </div>
              <div>
                <h2 className="text-2xl font-bold">{visitante?.nombre || user?.nombre}</h2>
                <p className="text-orange-100">Documento: {visitante?.documento || user?.documento}</p>
              </div>
            </div>
            <div className="text-right">
              {visitante?.ticketActivo ? (
                <div>
                  <Badge className="bg-white text-orange-600 hover:bg-white">
                    <Ticket className="h-3 w-3 mr-1" />
                    Ticket {visitante.ticketActivo.tipo}
                  </Badge>
                  <p className="text-sm text-orange-100 mt-1">
                    Visitas hoy: {visitante.visitasHoy}
                  </p>
                  <p className="text-sm text-orange-100">
                    Saldo: ${(visitante?.saldoVirtual || 0).toLocaleString('es-CO')}
                  </p>
                </div>
              ) : (
                <div className="space-y-1">
                  <Badge className="bg-white/20 text-white hover:bg-white/20">
                    Sin ticket activo
                  </Badge>
                  <p className="text-sm text-orange-100">
                    Saldo: ${(visitante?.saldoVirtual || 0).toLocaleString('es-CO')}
                  </p>
                </div>
              )}
            </div>
          </div>
        </CardContent>
      </Card>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card className="border-0 shadow-sm">
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-lg"><UserCircle className="h-5 w-5 text-orange-600" />Perfil del visitante</CardTitle>
          </CardHeader>
          <CardContent className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-sm">
            <p><strong>Nombre:</strong> {visitante?.nombre || user?.nombre}</p>
            <p><strong>Documento:</strong> {visitante?.documento || user?.documento}</p>
            <p><strong>Edad:</strong> {visitante?.edad ?? 'No registrada'}</p>
            <p><strong>Estatura:</strong> {visitante?.estatura ? `${visitante.estatura} m` : 'No registrada'}</p>
            <p><strong>Saldo virtual:</strong> ${(visitante?.saldoVirtual || 0).toLocaleString('es-CO')}</p>
            <p><strong>Ticket activo:</strong> {visitante?.ticketActivo?.tipo || 'Sin ticket activo'}</p>
            <p><strong>Dentro del parque:</strong> {visitante?.ticketActivo?.activo ? 'Sí' : 'No registrado'}</p>
            <p><strong>En cola:</strong> {visitante?.enCola ? 'Sí' : 'No'}</p>
            <p><strong>Ubicación actual:</strong> {visitante?.ubicacionActual || 'Aún no ha visitado una atracción'}</p>
            <p><strong>Favoritos:</strong> {favoritos.length}</p>
            <p><strong>Historial:</strong> {historial.length} visita(s)</p>
          </CardContent>
        </Card>

        <Card className="border-0 shadow-sm">
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-lg"><Wallet className="h-5 w-5 text-orange-600" />Recargar saldo virtual</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            <Label htmlFor="recarga">Valor a recargar</Label>
            <Input id="recarga" type="number" min={1} value={valorRecarga} onChange={(e) => setValorRecarga(Number(e.target.value))} />
            <Button disabled={recargando || valorRecarga <= 0} onClick={handleRecargarSaldo} className="w-full bg-orange-600 hover:bg-orange-700">
              {recargando ? 'Recargando...' : 'Recargar saldo'}
            </Button>
            <p className="text-xs text-gray-500">No se permiten recargas negativas ni en cero.</p>
          </CardContent>
        </Card>
      </div>

      {/* Main Tabs */}
      <Tabs defaultValue="ticket" className="space-y-4">
        <TabsList className="flex flex-wrap gap-1">
          <TabsTrigger value="ticket" className="flex items-center gap-1">
            <Ticket className="h-4 w-4" />
            <span className="hidden sm:inline">Comprar Ticket</span>
          </TabsTrigger>
          <TabsTrigger value="queue" className="flex items-center gap-1">
            <Clock className="h-4 w-4" />
            <span className="hidden sm:inline">Cola Virtual</span>
          </TabsTrigger>
          <TabsTrigger value="favorites" className="flex items-center gap-1">
            <Heart className="h-4 w-4" />
            <span className="hidden sm:inline">Favoritos</span>
          </TabsTrigger>
          <TabsTrigger value="history" className="flex items-center gap-1">
            <History className="h-4 w-4" />
            <span className="hidden sm:inline">Historial</span>
          </TabsTrigger>
          <TabsTrigger value="route" className="flex items-center gap-1">
            <MapPin className="h-4 w-4" />
            <span className="hidden sm:inline">Calcular Ruta</span>
          </TabsTrigger>
          <TabsTrigger value="show" className="flex items-center gap-1">
            <Clock className="h-4 w-4" />
            <span className="hidden sm:inline">Show</span>
          </TabsTrigger>
        </TabsList>

        <TabsContent value="ticket">
          <VisitorTicketForm 
            zonas={zonas}
            visitanteDocumento={visitante?.documento || user?.documento || ''}
            onTicketPurchased={handleTicketPurchased}
          />
        </TabsContent>

        <TabsContent value="queue">
          <QueuePanel 
            atracciones={activeAtracciones}
            visitanteDocumento={visitante?.documento || user?.documento || ''}
            onJoinedQueue={handleJoinedQueue}
          />
        </TabsContent>

        <TabsContent value="favorites">
          <FavoritesPanel 
            favoritos={favoritos}
            atracciones={atracciones}
            visitanteDocumento={visitante?.documento || user?.documento || ''}
            onAddedFavorite={handleAddedFavorite}
          />
        </TabsContent>

        <TabsContent value="history">
          <HistoryPanel historial={historial} />
        </TabsContent>

        <TabsContent value="route">
          <div className="space-y-4">
            <RoutePlanner atracciones={atracciones} />
            {mapa && (
              <GraphMap
                mapa={mapa}
                atracciones={atracciones}
                ubicacionActualId={visitante?.ubicacionActualId}
              />
            )}
          </div>
        </TabsContent>

        <TabsContent value="show">
          <Card className="border-0 shadow-sm">
            <CardHeader>
              <CardTitle>Show del Café</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              {shows.length === 0 ? (
                <p className="text-sm text-gray-500">No hay horarios disponibles para el Show del Café.</p>
              ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                  {shows.map((show, index) => (
                    <div key={show.id} className="rounded border bg-gray-50 p-3 text-sm">
                      <p className="font-medium">Horario {index + 1}: {new Date(show.horario).toLocaleTimeString('es-CO', { hour: 'numeric', minute: '2-digit' })}</p>
                      <p>Duración: {show.duracion || 30} minutos</p>
                      <Badge variant={show.estado === 'EN_CURSO' ? 'default' : 'outline'}>{show.mensaje || show.estado}</Badge>
                    </div>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  )
}

function VisitorSkeleton() {
  return (
    <div className="space-y-6">
      <Skeleton className="h-32" />
      <Skeleton className="h-12 w-full max-w-2xl" />
      <Skeleton className="h-96" />
    </div>
  )
}
