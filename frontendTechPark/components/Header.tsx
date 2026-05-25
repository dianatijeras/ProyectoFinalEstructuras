'use client'

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { useAuth } from '@/contexts/AuthContext'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Badge } from '@/components/ui/badge'
import { Bell, LogOut, User, ChevronDown } from 'lucide-react'
import { notificacionesService } from '@/services/notificacionesService'
import type { Notificacion } from '@/types'

interface HeaderProps {
  title?: string
}

export function Header({ title }: HeaderProps) {
  const { user, logout } = useAuth()
  const router = useRouter()
  const [notificaciones, setNotificaciones] = useState<Notificacion[]>([])

  useEffect(() => {
    if (!user) return

    const cargarNotificaciones = () => {
      notificacionesService.getGlobales(user.documento).then(setNotificaciones).catch(() => setNotificaciones([]))
    }

    cargarNotificaciones()
    const intervalo = window.setInterval(cargarNotificaciones, 8000)
    window.addEventListener('techpark:notifications-refresh', cargarNotificaciones)
    window.addEventListener('focus', cargarNotificaciones)

    return () => {
      window.clearInterval(intervalo)
      window.removeEventListener('techpark:notifications-refresh', cargarNotificaciones)
      window.removeEventListener('focus', cargarNotificaciones)
    }
  }, [user])

  const handleLogout = () => {
    logout()
    router.push('/login')
  }

  const getRoleBadgeColor = (rol: string) => {
    switch (rol) {
      case 'ADMINISTRADOR':
        return 'bg-red-100 text-red-700 hover:bg-red-100'
      case 'OPERADOR':
        return 'bg-blue-100 text-blue-700 hover:bg-blue-100'
      case 'VISITANTE':
        return 'bg-green-100 text-green-700 hover:bg-green-100'
      default:
        return 'bg-gray-100 text-gray-700 hover:bg-gray-100'
    }
  }

  return (
    <header className="h-16 bg-white border-b border-gray-200 px-6 flex items-center justify-between">
      {/* Title */}
      <div>
        {title && (
          <h1 className="text-xl font-semibold text-gray-900">{title}</h1>
        )}
      </div>

      {/* Right side */}
      <div className="flex items-center gap-4">
        {/* Notifications */}
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" size="icon" className="relative">
              <Bell className="h-5 w-5 text-gray-500" />
              {notificaciones.length > 0 && (
                <span className="absolute -top-1 -right-1 h-4 w-4 rounded-full bg-red-500 text-[10px] font-medium text-white flex items-center justify-center">
                  {Math.min(notificaciones.length, 9)}
                </span>
              )}
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="w-80">
            <DropdownMenuLabel>Notificaciones</DropdownMenuLabel>
            <DropdownMenuSeparator />
            {notificaciones.length === 0 ? (
              <DropdownMenuItem className="text-gray-500">No hay notificaciones activas</DropdownMenuItem>
            ) : (
              notificaciones.slice(0, 6).map((n) => (
                <DropdownMenuItem key={n.id} className="flex flex-col items-start gap-1 whitespace-normal">
                  <span className="font-medium">{n.titulo}</span>
                  <span className="text-xs text-gray-500">{n.mensaje}</span>
                  <span className="text-[10px] text-gray-400">{new Date(n.fecha).toLocaleString('es-CO')}</span>
                </DropdownMenuItem>
              ))
            )}
          </DropdownMenuContent>
        </DropdownMenu>

        {/* User Menu */}
        {user && (
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" className="flex items-center gap-2 h-auto py-2">
                <div className="w-8 h-8 rounded-full bg-orange-100 flex items-center justify-center">
                  <User className="h-4 w-4 text-orange-600" />
                </div>
                <div className="text-left hidden sm:block">
                  <p className="text-sm font-medium text-gray-900">{user.nombre}</p>
                  <Badge className={`text-[10px] ${getRoleBadgeColor(user.rol)}`}>
                    {user.rol}
                  </Badge>
                </div>
                <ChevronDown className="h-4 w-4 text-gray-400" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="w-56">
              <DropdownMenuLabel>
                <div>
                  <p className="font-medium">{user.nombre}</p>
                  <p className="text-xs text-gray-500">ID: {user.id}</p>
                  <p className="text-xs text-gray-500">Doc: {user.documento}</p>
                </div>
              </DropdownMenuLabel>
              <DropdownMenuSeparator />
              <DropdownMenuItem onClick={handleLogout} className="text-red-600 cursor-pointer">
                <LogOut className="mr-2 h-4 w-4" />
                Cerrar Sesión
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        )}
      </div>
    </header>
  )
}
