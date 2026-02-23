import { useAuth } from '../../context/AuthContext';
import type { Role } from '../../types';

export default function Navbar() {
  const { user, logout, isAuthenticated } = useAuth();

  if (!isAuthenticated) return null;

  const getRoleBadgeColor = (role: Role) => {
    switch (role) {
      case 'ROLE_ADMIN':
        return 'bg-red-100 text-red-800';
      case 'ROLE_MANAGER':
        return 'bg-purple-100 text-purple-800';
      case 'ROLE_TECHNICIAN':
        return 'bg-blue-100 text-blue-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const formatRole = (role: Role) => {
    return role.replace('ROLE_', '');
  };

  return (
    <nav className="bg-white shadow-sm border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex items-center">
            <a href="/" className="flex items-center gap-2">
              <svg
                className="w-8 h-8 text-blue-600"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M15 5v2m0 4v2m0 4v2M5 5a2 2 0 00-2 2v3a2 2 0 110 4v3a2 2 0 002 2h14a2 2 0 002-2v-3a2 2 0 110-4V7a2 2 0 00-2-2H5z"
                />
              </svg>
              <span className="text-xl font-bold text-gray-900">
                IT Tickets
              </span>
            </a>
          </div>

          <div className="flex items-center gap-4">
            <div className="flex items-center gap-2">
              <span className="text-sm text-gray-600">
                {user?.username}
              </span>
              <span
                className={`badge ${getRoleBadgeColor(user?.role ?? 'ROLE_USER')}`}
              >
                {formatRole(user?.role ?? 'ROLE_USER')}
              </span>
            </div>
            <button
              onClick={logout}
              className="text-sm text-gray-500 hover:text-gray-700
                         transition-colors"
            >
              Logout
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
}
