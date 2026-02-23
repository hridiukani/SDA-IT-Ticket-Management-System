import { useAuth } from '../../context/AuthContext'
import { Users } from 'lucide-react'

export default function AdminPage() {
  const { hasRole } = useAuth()

  if (!hasRole(['ROLE_ADMIN', 'ROLE_MANAGER'])) {
    return (
      <div className="text-center py-12">
        <p className="text-red-600 text-lg">Access Denied</p>
        <p className="text-gray-500 mt-2">
          You don't have permission to access this page
        </p>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-3">
        <Users className="h-8 w-8 text-blue-600" />
        <h1 className="text-3xl font-bold text-gray-900">Admin Panel</h1>
      </div>

      <div className="card">
        <p className="text-gray-600">
          User management features coming soon...
        </p>
      </div>
    </div>
  )
}
