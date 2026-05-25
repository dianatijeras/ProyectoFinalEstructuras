import { Card, CardContent } from '@/components/ui/card'
import { cn } from '@/lib/utils'

interface StatCardProps {
  title: string
  value: string | number
  description?: string
  icon?: React.ReactNode
  trend?: {
    value: number
    isPositive: boolean
  }
  className?: string
  iconClassName?: string
}

export function StatCard({
  title,
  value,
  description,
  icon,
  trend,
  className,
  iconClassName,
}: StatCardProps) {
  return (
    <Card className={cn('border-0 shadow-sm', className)}>
      <CardContent className="p-6">
        <div className="flex items-start justify-between">
          <div className="space-y-1">
            <p className="text-sm font-medium text-gray-500">{title}</p>
            <p className="text-2xl font-bold text-gray-900">{value}</p>
            {description && (
              <p className="text-xs text-gray-500">{description}</p>
            )}
            {trend && (
              <p
                className={cn(
                  'text-xs font-medium',
                  trend.isPositive ? 'text-green-600' : 'text-red-600'
                )}
              >
                {trend.isPositive ? '+' : '-'}{Math.abs(trend.value)}%
              </p>
            )}
          </div>
          {icon && (
            <div
              className={cn(
                'w-12 h-12 rounded-lg flex items-center justify-center',
                iconClassName || 'bg-orange-100'
              )}
            >
              {icon}
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  )
}
