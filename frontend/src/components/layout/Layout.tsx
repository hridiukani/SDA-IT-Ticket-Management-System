import { Outlet, Link, useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { LogOut, Home, Ticket, Settings, Plus } from 'lucide-react'

export default function Layout() {
  const { user, logout, hasRole } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const isActive = (path: string) => location.pathname === path

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Navigation Bar */}
      <nav className="bg-white border-b border-gray-200 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            {/* Logo and Title */}
            <div className="flex items-center gap-3">
              <Ticket className="h-8 w-8 text-blue-600" />
              <h1 className="text-xl font-bold text-gray-900">
                IT Ticket System
              </h1>
            </div>

            {/* Navigation Links */}
            <div className="flex items-center gap-6">
              <Link
                to="/dashboard"
                className={`flex items-center gap-2 px-3 py-2 rounded-lg
                  transition-colors ${
                  isActive('/dashboard')
                    ? 'bg-blue-50 text-blue-600'
                    : 'text-gray-600 hover:bg-gray-50'
                }`}
              >
                <Home className="h-5 w-5" />
                <span className="font-medium">Dashboard</span>
              </Link>

              <Link
                to="/tickets"
                className={`flex items-center gap-2 px-3 py-2 rounded-lg
                  transition-colors ${
                  isActive('/tickets')
                    ? 'bg-blue-50 text-blue-600'
                    : 'text-gray-600 hover:bg-gray-50'
                }`}
              >
                <Ticket className="h-5 w-5" />
                <span className="font-medium">Tickets</span>
              </Link>

              {hasRole(['ROLE_ADMIN', 'ROLE_MANAGER']) && (
                <Link
                  to="/admin"
                  className={`flex items-center gap-2 px-3 py-2 rounded-lg
                    transition-colors ${
                    isActive('/admin')
                      ? 'bg-blue-50 text-blue-600'
                      : 'text-gray-600 hover:bg-gray-50'
                  }`}
                >
                  <Settings className="h-5 w-5" />
                  <span className="font-medium">Admin</span>
                </Link>
              )}

              {/* User Menu */}
              <div className="flex items-center gap-4 ml-6 pl-6
                              border-l border-gray-200">
                <div className="text-right">
                  <div className="text-sm font-medium text-gray-900">
                    {user?.username}
                  </div>
                  <div className="text-xs text-gray-500">
                    {user?.role.replace('ROLE_', '')}
                  </div>
                </div>
                <button
                  onClick={handleLogout}
                  className="btn-secondary !px-3 !py-2"
                >
                  <LogOut className="h-4 w-4" />
                </button>
              </div>
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Outlet />
      </main>
    </div>
  )
}
