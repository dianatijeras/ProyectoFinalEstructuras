'use client'

import { useEffect } from 'react'
import { useRouter, usePathname } from 'next/navigation'
import { useAuth } from '@/contexts/AuthContext'
import { AppSidebar } from '@/components/AppSidebar'
import { Header } from '@/components/Header'
import { Toaster } from '@/components/ui/sonner'

const pageTitles: Record<string, string> = {
  '/dashboard': 'Dashboard',
  '/visitor': 'Panel de Visitante',
  '/operator': 'Panel de Operador',
  '/admin': 'Panel de Administrador',
  '/map': 'Mapa y Rutas',
  '/reports': 'Reportes',
  '/data': 'Carga de Datos',
}

export default function AppLayout({
  children,
}: {
  children: React.ReactNode
}) {
  const { isAuthenticated, user } = useAuth()
  const router = useRouter()
  const pathname = usePathname()

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login')
    }
  }, [isAuthenticated, router])

  if (!isAuthenticated || !user) {
    return null
  }

  const pageTitle = pageTitles[pathname] || 'Tech-Park UQ'

  return (
    <div className="flex min-h-screen bg-gray-50">
      <AppSidebar />
      <div className="flex-1 flex flex-col">
        <Header title={pageTitle} />
        <main className="flex-1 p-6 overflow-auto">
          {children}
        </main>
      </div>
      <Toaster />
    </div>
  )
}
