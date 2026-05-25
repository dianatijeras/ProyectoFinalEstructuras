'use client'

import Link from 'next/link'
import { usePathname } from 'next/navigation'
import { useAuth } from '@/contexts/AuthContext'
import { cn } from '@/lib/utils'
import {
  LayoutDashboard,
  User,
  Settings,
  Map,
  BarChart3,
  Database,
  Ticket,
  Wrench,
} from 'lucide-react'

interface NavItem {
  title: string
  href: string
  icon: React.ElementType
  roles: ('ADMINISTRADOR' | 'OPERADOR' | 'VISITANTE')[]
}

const navItems: NavItem[] = [
  {
    title: 'Dashboard',
    href: '/dashboard',
    icon: LayoutDashboard,
    roles: ['ADMINISTRADOR', 'OPERADOR', 'VISITANTE'],
  },
  {
    title: 'Visitante',
    href: '/visitor',
    icon: Ticket,
    roles: ['VISITANTE'],
  },
  {
    title: 'Operador',
    href: '/operator',
    icon: Wrench,
    roles: ['OPERADOR'],
  },
  {
    title: 'Administrador',
    href: '/admin',
    icon: Settings,
    roles: ['ADMINISTRADOR'],
  },
  {
    title: 'Mapa y Rutas',
    href: '/map',
    icon: Map,
    roles: ['ADMINISTRADOR', 'OPERADOR', 'VISITANTE'],
  },
  {
    title: 'Reportes',
    href: '/reports',
    icon: BarChart3,
    roles: ['ADMINISTRADOR', 'OPERADOR'],
  },
  {
    title: 'Carga de Datos',
    href: '/data',
    icon: Database,
    roles: ['ADMINISTRADOR'],
  },
]

export function AppSidebar() {
  const pathname = usePathname()
  const { user, hasRole } = useAuth()

  const filteredNavItems = navItems.filter((item) => 
    hasRole(item.roles)
  )

  return (
    <aside className="w-64 bg-white border-r border-gray-200 min-h-screen flex flex-col">
      {/* Logo */}
      <div className="p-6 border-b border-gray-200">
        <Link href="/dashboard" className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-lg bg-orange-600 flex items-center justify-center">
            <Ticket className="h-6 w-6 text-white" />
          </div>
          <div>
            <h1 className="font-bold text-gray-900">Tech-Park UQ</h1>
            <p className="text-xs text-gray-500">Sistema de Gestión</p>
          </div>
        </Link>
      </div>

      {/* Navigation */}
      <nav className="flex-1 p-4">
        <ul className="space-y-1">
          {filteredNavItems.map((item) => {
            const isActive = pathname === item.href || pathname.startsWith(`${item.href}/`)
            const Icon = item.icon

            return (
              <li key={item.href}>
                <Link
                  href={item.href}
                  className={cn(
                    'flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-medium transition-colors',
                    isActive
                      ? 'bg-orange-50 text-orange-700'
                      : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900'
                  )}
                >
                  <Icon className={cn('h-5 w-5', isActive ? 'text-orange-600' : 'text-gray-400')} />
                  {item.title}
                </Link>
              </li>
            )
          })}
        </ul>
      </nav>

      {/* User Info */}
      {user && (
        <div className="p-4 border-t border-gray-200">
          <div className="flex items-center gap-3 px-4 py-3 rounded-lg bg-gray-50">
            <div className="w-10 h-10 rounded-full bg-orange-100 flex items-center justify-center">
              <User className="h-5 w-5 text-orange-600" />
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium text-gray-900 truncate">
                {user.nombre}
              </p>
              <p className="text-xs text-gray-500 truncate">
                {user.rol}
              </p>
            </div>
          </div>
        </div>
      )}
    </aside>
  )
}
