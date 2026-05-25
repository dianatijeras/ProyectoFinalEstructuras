'use client'

import { DataLoader } from '@/components/DataLoader'

export default function DataPage() {
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-gray-900">Carga de Datos</h2>
        <p className="text-gray-500">Conecta con los endpoints reales de carga del backend Spring Boot</p>
      </div>
      <DataLoader />
    </div>
  )
}
