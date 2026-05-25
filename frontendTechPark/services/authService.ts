import api from '@/lib/api'
import type { Usuario, LoginRequest } from '@/types'
import { mapUsuarioFromApi } from './apiMappers'

export const authService = {
  login: async (credentials: LoginRequest): Promise<Usuario> => {
    const response = await api.post<{ id: string; nombre: string; documento: string; rol: string }>('/api/auth/login', credentials)
    return mapUsuarioFromApi(response)
  },
}

export const sessionService = {
  setUser: (user: Usuario): void => {
    if (typeof window !== 'undefined') localStorage.setItem('techpark_user', JSON.stringify(user))
  },

  getUser: (): Usuario | null => {
    if (typeof window === 'undefined') return null
    const stored = localStorage.getItem('techpark_user')
    if (!stored) return null
    try {
      return JSON.parse(stored) as Usuario
    } catch {
      return null
    }
  },

  clearUser: (): void => {
    if (typeof window !== 'undefined') localStorage.removeItem('techpark_user')
  },

  isAuthenticated: (): boolean => sessionService.getUser() !== null,
}

export default authService
