'use client'

import { useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { useAuth } from '@/contexts/AuthContext'
import { Loader2 } from 'lucide-react'

export default function HomePage() {
  const { isAuthenticated, user } = useAuth()
  const router = useRouter()

  useEffect(() => {
    if (isAuthenticated && user) {
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
    } else {
      router.push('/login')
    }
  }, [isAuthenticated, user, router])

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="text-center">
        <Loader2 className="h-8 w-8 animate-spin text-orange-600 mx-auto" />
        <p className="mt-2 text-gray-600">Cargando...</p>
      </div>
    </div>
  )
}
