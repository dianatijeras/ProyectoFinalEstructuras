'use client'

import { createContext, useContext, useState, useEffect, ReactNode } from 'react'
import type { Usuario, Rol } from '@/types'
import { sessionService } from '@/services/authService'

interface AuthContextType {
  user: Usuario | null
  setUser: (user: Usuario | null) => void
  logout: () => void
  isAuthenticated: boolean
  hasRole: (roles: Rol | Rol[]) => boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUserState] = useState<Usuario | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    // Load user from localStorage on mount
    const storedUser = sessionService.getUser()
    if (storedUser) {
      setUserState(storedUser)
    }
    setIsLoading(false)
  }, [])

  const setUser = (newUser: Usuario | null) => {
    setUserState(newUser)
    if (newUser) {
      sessionService.setUser(newUser)
    } else {
      sessionService.clearUser()
    }
  }

  const logout = () => {
    setUserState(null)
    sessionService.clearUser()
  }

  const hasRole = (roles: Rol | Rol[]): boolean => {
    if (!user) return false
    const roleArray = Array.isArray(roles) ? roles : [roles]
    return roleArray.includes(user.rol)
  }

  if (isLoading) {
    return null // Or a loading spinner
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        setUser,
        logout,
        isAuthenticated: !!user,
        hasRole,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}
