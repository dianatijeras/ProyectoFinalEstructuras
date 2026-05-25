import { Badge } from '@/components/ui/badge'
import { cn } from '@/lib/utils'
import type { EstadoAtraccion } from '@/types'

interface StatusBadgeProps {
  status: EstadoAtraccion | string
  className?: string
}

export function StatusBadge({ status, className }: StatusBadgeProps) {
  const getStatusConfig = (status: string) => {
    switch (status) {
      case 'ACTIVA':
        return {
          label: 'Activa',
          className: 'bg-green-100 text-green-700 hover:bg-green-100',
        }
      case 'EN_MANTENIMIENTO':
        return {
          label: 'En Mantenimiento',
          className: 'bg-yellow-100 text-yellow-700 hover:bg-yellow-100',
        }
      case 'CERRADA':
        return {
          label: 'Cerrada',
          className: 'bg-red-100 text-red-700 hover:bg-red-100',
        }
      default:
        return {
          label: status,
          className: 'bg-gray-100 text-gray-700 hover:bg-gray-100',
        }
    }
  }

  const config = getStatusConfig(status)

  return (
    <Badge className={cn(config.className, className)}>
      {config.label}
    </Badge>
  )
}

interface AlertBadgeProps {
  type: 'LLUVIA_FUERTE' | 'TORMENTA_ELECTRICA' | string
  className?: string
}

export function AlertBadge({ type, className }: AlertBadgeProps) {
  const getAlertConfig = (type: string) => {
    switch (type) {
      case 'LLUVIA_FUERTE':
        return {
          label: 'Lluvia Fuerte',
          className: 'bg-blue-100 text-blue-700 hover:bg-blue-100',
        }
      case 'TORMENTA_ELECTRICA':
        return {
          label: 'Tormenta Eléctrica',
          className: 'bg-purple-100 text-purple-700 hover:bg-purple-100',
        }
      default:
        return {
          label: type,
          className: 'bg-gray-100 text-gray-700 hover:bg-gray-100',
        }
    }
  }

  const config = getAlertConfig(type)

  return (
    <Badge className={cn(config.className, className)}>
      {config.label}
    </Badge>
  )
}

interface PriorityBadgeProps {
  priority: 'ALTA' | 'MEDIA' | 'BAJA' | string
  className?: string
}

export function PriorityBadge({ priority, className }: PriorityBadgeProps) {
  const getPriorityConfig = (priority: string) => {
    switch (priority) {
      case 'ALTA':
        return {
          label: 'Alta',
          className: 'bg-red-100 text-red-700 hover:bg-red-100',
        }
      case 'MEDIA':
        return {
          label: 'Media',
          className: 'bg-yellow-100 text-yellow-700 hover:bg-yellow-100',
        }
      case 'BAJA':
        return {
          label: 'Baja',
          className: 'bg-green-100 text-green-700 hover:bg-green-100',
        }
      default:
        return {
          label: priority,
          className: 'bg-gray-100 text-gray-700 hover:bg-gray-100',
        }
    }
  }

  const config = getPriorityConfig(priority)

  return (
    <Badge className={cn(config.className, className)}>
      {config.label}
    </Badge>
  )
}
