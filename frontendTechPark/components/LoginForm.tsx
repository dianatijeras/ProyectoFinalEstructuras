'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { useAuth } from '@/contexts/AuthContext'
import { authService } from '@/services/authService'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Loader2, Ticket, AlertCircle } from 'lucide-react'

export function LoginForm() {
  const [documento, setDocumento] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const { setUser } = useAuth()
  const router = useRouter()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    setIsLoading(true)

    try {
      const user = await authService.login({ documento, password })
      setUser(user)
      
      // Redirect based on role
      switch (user.rol) {
        case 'ADMINISTRADOR':
          router.push('/dashboard')
          break
        case 'OPERADOR':
          router.push('/operator')
          break
        case 'VISITANTE':
          router.push('/visitor')
          break
        default:
          router.push('/dashboard')
      }
    } catch (err) {
      setError(
        err instanceof Error 
          ? err.message 
          : 'Error al iniciar sesión. Verifique sus credenciales.'
      )
    } finally {
      setIsLoading(false)
    }
  }

  const testUsers = [
    { rol: 'Administrador', documento: '3001', clave: '123' },
    { rol: 'Operador', documento: '2001', clave: '123' },
    { rol: 'Visitante', documento: '1001', clave: '123' },
  ]

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-orange-50 to-cyan-100 p-4">
      <div className="w-full max-w-md space-y-6">
        {/* Logo and Title */}
        <div className="text-center space-y-2">
          <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-orange-600 text-white mb-4">
            <Ticket className="h-8 w-8" />
          </div>
          <h1 className="text-3xl font-bold text-gray-900">Tech-Park UQ</h1>
          <p className="text-gray-600">Sistema de Gestión de Parque de Diversiones</p>
        </div>

        {/* Login Card */}
        <Card className="border-0 shadow-xl">
          <CardHeader className="space-y-1">
            <CardTitle className="text-2xl text-center">Iniciar Sesión</CardTitle>
            <CardDescription className="text-center">
              Ingrese sus credenciales para acceder al sistema
            </CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-4">
              {error && (
                <Alert variant="destructive">
                  <AlertCircle className="h-4 w-4" />
                  <AlertDescription>{error}</AlertDescription>
                </Alert>
              )}
              
              <div className="space-y-2">
                <Label htmlFor="documento">Documento</Label>
                <Input
                  id="documento"
                  type="text"
                  placeholder="Ingrese su documento"
                  value={documento}
                  onChange={(e) => setDocumento(e.target.value)}
                  required
                  disabled={isLoading}
                />
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="password">Contraseña</Label>
                <Input
                  id="password"
                  type="password"
                  placeholder="Ingrese su contraseña"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  disabled={isLoading}
                />
              </div>

              <Button 
                type="submit" 
                className="w-full bg-orange-600 hover:bg-orange-700"
                disabled={isLoading}
              >
                {isLoading ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Iniciando sesión...
                  </>
                ) : (
                  'Iniciar Sesión'
                )}
              </Button>
            </form>
          </CardContent>
        </Card>

        {/* Test Users Card */}
        <Card className="border-0 shadow-lg bg-white/80 backdrop-blur">
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-700">
              Usuarios de Prueba
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-2">
              {testUsers.map((user) => (
                <div 
                  key={user.documento}
                  className="flex items-center justify-between p-2 rounded-lg bg-gray-50 hover:bg-gray-100 transition-colors cursor-pointer"
                  onClick={() => {
                    setDocumento(user.documento)
                    setPassword(user.clave)
                  }}
                >
                  <div>
                    <span className="font-medium text-gray-900">{user.rol}</span>
                    <span className="text-gray-500 text-sm ml-2">
                      Doc: {user.documento}
                    </span>
                  </div>
                  <span className="text-xs text-gray-400">
                    Clave: {user.clave}
                  </span>
                </div>
              ))}
            </div>
            <p className="text-xs text-gray-500 mt-3 text-center">
              Haga clic en un usuario para autocompletar
            </p>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
