'use client'

import { useState, useRef } from 'react'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { datosService } from '@/services/datosService'
import { Database, Upload, RefreshCw, Loader2, CheckCircle, AlertCircle, FileUp } from 'lucide-react'
import { toast } from 'sonner'

interface DataLoaderProps {
  onDataLoaded?: () => void
}

export function DataLoader({ onDataLoaded }: DataLoaderProps) {
  const [isLoadingExample, setIsLoadingExample] = useState(false)
  const [isUploadingCSV, setIsUploadingCSV] = useState(false)
  const [selectedFile, setSelectedFile] = useState<File | null>(null)
  const [result, setResult] = useState<{ success: boolean; message: string } | null>(null)
  const fileInputRef = useRef<HTMLInputElement>(null)

  const handleLoadExample = async () => {
    setIsLoadingExample(true)
    setResult(null)

    try {
      const response = await datosService.cargarEjemplo()
      setResult({
        success: response.success,
        message: response.mensaje || 'Datos de ejemplo cargados exitosamente',
      })
      toast.success('Datos de ejemplo cargados')
      onDataLoaded?.()
    } catch (err) {
      setResult({
        success: false,
        message: err instanceof Error ? err.message : 'Error al cargar datos de ejemplo',
      })
      toast.error('Error al cargar datos')
    } finally {
      setIsLoadingExample(false)
    }
  }

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      if (file.type !== 'text/csv' && !file.name.endsWith('.csv')) {
        toast.error('Por favor seleccione un archivo CSV')
        return
      }
      setSelectedFile(file)
    }
  }

  const handleUploadCSV = async () => {
    if (!selectedFile) {
      toast.error('Seleccione un archivo primero')
      return
    }

    setIsUploadingCSV(true)
    setResult(null)

    try {
      const response = await datosService.cargarCSV(selectedFile)
      setResult({
        success: response.success,
        message: response.mensaje || `${response.registrosCargados || 0} registros cargados`,
      })
      toast.success('Archivo CSV cargado exitosamente')
      setSelectedFile(null)
      if (fileInputRef.current) {
        fileInputRef.current.value = ''
      }
      onDataLoaded?.()
    } catch (err) {
      setResult({
        success: false,
        message: err instanceof Error ? err.message : 'Error al cargar archivo CSV',
      })
      toast.error('Error al cargar CSV')
    } finally {
      setIsUploadingCSV(false)
    }
  }

  return (
    <div className="space-y-6">
      {/* Result Alert */}
      {result && (
        <Alert className={result.success ? 'bg-green-50 border-green-200' : 'bg-red-50 border-red-200'}>
          {result.success ? (
            <CheckCircle className="h-4 w-4 text-green-600" />
          ) : (
            <AlertCircle className="h-4 w-4 text-red-600" />
          )}
          <AlertDescription className={result.success ? 'text-green-800' : 'text-red-800'}>
            {result.message}
          </AlertDescription>
        </Alert>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Load Example Data */}
        <Card className="border-0 shadow-sm">
          <CardHeader>
            <div className="flex items-center gap-2">
              <RefreshCw className="h-5 w-5 text-orange-600" />
              <CardTitle className="text-lg">Cargar Datos de Ejemplo</CardTitle>
            </div>
            <CardDescription>
              Reinicie el sistema con datos de ejemplo predefinidos
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="p-4 bg-orange-50 rounded-lg">
              <h4 className="font-medium text-orange-900 mb-2">Datos incluidos:</h4>
              <ul className="text-sm text-orange-700 space-y-1 list-disc list-inside">
                <li>Zonas del parque</li>
                <li>Atracciones con estados</li>
                <li>Visitantes de prueba</li>
                <li>Operadores asignados</li>
                <li>Configuración de grafo/mapa</li>
              </ul>
            </div>

            <div className="p-3 bg-yellow-50 rounded-lg text-sm text-yellow-700">
              <p className="font-medium">Advertencia:</p>
              <p>Esta acción reemplazará todos los datos actuales del sistema.</p>
            </div>

            <Button 
              onClick={handleLoadExample}
              className="w-full bg-orange-600 hover:bg-orange-700"
              disabled={isLoadingExample}
            >
              {isLoadingExample ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Cargando datos...
                </>
              ) : (
                <>
                  <RefreshCw className="mr-2 h-4 w-4" />
                  Cargar Datos de Ejemplo
                </>
              )}
            </Button>
          </CardContent>
        </Card>

        {/* Upload CSV */}
        <Card className="border-0 shadow-sm">
          <CardHeader>
            <div className="flex items-center gap-2">
              <Upload className="h-5 w-5 text-blue-600" />
              <CardTitle className="text-lg">Cargar Archivo CSV</CardTitle>
            </div>
            <CardDescription>
              Importe datos desde un archivo CSV
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="p-4 bg-blue-50 rounded-lg">
              <h4 className="font-medium text-blue-900 mb-2">Formato esperado:</h4>
              <ul className="text-sm text-blue-700 space-y-1 list-disc list-inside">
                <li>Archivo en formato CSV</li>
                <li>Columnas separadas por coma</li>
                <li>Primera fila con encabezados</li>
                <li>Codificación UTF-8</li>
              </ul>
            </div>

            {/* File Input */}
            <div className="space-y-2">
              <Input
                ref={fileInputRef}
                type="file"
                accept=".csv"
                onChange={handleFileChange}
                className="cursor-pointer"
              />
              {selectedFile && (
                <p className="text-sm text-gray-600">
                  Archivo seleccionado: <span className="font-medium">{selectedFile.name}</span>
                  <span className="text-gray-400 ml-2">
                    ({(selectedFile.size / 1024).toFixed(1)} KB)
                  </span>
                </p>
              )}
            </div>

            <Button 
              onClick={handleUploadCSV}
              className="w-full bg-blue-600 hover:bg-blue-700"
              disabled={isUploadingCSV || !selectedFile}
            >
              {isUploadingCSV ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Subiendo archivo...
                </>
              ) : (
                <>
                  <FileUp className="mr-2 h-4 w-4" />
                  Subir Archivo CSV
                </>
              )}
            </Button>
          </CardContent>
        </Card>
      </div>

      {/* Information Card */}
      <Card className="border-0 shadow-sm bg-gray-50">
        <CardContent className="p-4">
          <div className="flex items-start gap-3">
            <Database className="h-5 w-5 text-gray-600 mt-0.5" />
            <div className="text-sm text-gray-600">
              <p className="font-medium text-gray-900 mb-1">Información importante:</p>
              <ul className="space-y-1">
                <li>Los datos se cargan directamente en el backend Spring Boot</li>
                <li>Asegúrese de que el servidor backend esté ejecutándose en el puerto 8080</li>
                <li>Los cambios se reflejarán inmediatamente en todas las secciones del sistema</li>
              </ul>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
