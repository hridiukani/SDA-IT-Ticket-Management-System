import { useQuery } from '@tanstack/react-query'
import { ticketService } from '../services/ticketService'
import { useAuth } from '../context/AuthContext'
import { Link } from 'react-router-dom'
import { Ticket, Clock, CheckCircle, AlertCircle, Plus } from 'lucide-react'

export default function DashboardPage() {
  const { user } = useAuth()

  const { data: ticketsData, isLoading } = useQuery({
    queryKey: ['tickets-dashboard'],
    queryFn: () => ticketService.getAll(0, 100)
  })

  const tickets = ticketsData?.content || []

  const stats = {
    total: tickets.length,
    open: tickets.filter(t => t.status === 'OPEN').length,
    inProgress: tickets.filter(t => t.status === 'IN_PROGRESS').length,
    resolved: tickets.filter(t => t.status === 'RESOLVED').length,
  }

  const recentTickets = tickets.slice(0, 5)

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">
            Welcome back, {user?.username}!
          </h1>
          <p className="text-gray-600 mt-1">
            Here's an overview of your tickets
          </p>
        </div>
        <Link to="/tickets/new" className="btn-primary">
          <Plus className="h-5 w-5" />
          New Ticket
        </Link>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="card">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Total Tickets</p>
              <p className="text-3xl font-bold text-gray-900 mt-1">
                {stats.total}
              </p>
            </div>
            <Ticket className="h-12 w-12 text-gray-400" />
          </div>
        </div>

        <div className="card">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Open</p>
              <p className="text-3xl font-bold text-blue-600 mt-1">
                {stats.open}
              </p>
            </div>
            <AlertCircle className="h-12 w-12 text-blue-400" />
          </div>
        </div>

        <div className="card">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">In Progress</p>
              <p className="text-3xl font-bold text-yellow-600 mt-1">
                {stats.inProgress}
              </p>
            </div>
            <Clock className="h-12 w-12 text-yellow-400" />
          </div>
        </div>

        <div className="card">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Resolved</p>
              <p className="text-3xl font-bold text-green-600 mt-1">
                {stats.resolved}
              </p>
            </div>
            <CheckCircle className="h-12 w-12 text-green-400" />
          </div>
        </div>
      </div>

      {/* Recent Tickets */}
      <div className="card">
        <h2 className="text-xl font-bold text-gray-900 mb-4">
          Recent Tickets
        </h2>
        {isLoading ? (
          <div className="text-center py-8 text-gray-500">Loading...</div>
        ) : recentTickets.length === 0 ? (
          <div className="text-center py-8 text-gray-500">
            No tickets yet. Create one to get started!
          </div>
        ) : (
          <div className="space-y-3">
            {recentTickets.map(ticket => (
              <Link
                key={ticket.id}
                to={`/tickets/${ticket.id}`}
                className="block p-4 border border-gray-200 rounded-lg
                           hover:border-blue-300 hover:bg-blue-50
                           transition-colors"
              >
                <div className="flex justify-between items-start">
                  <div className="flex-1">
                    <h3 className="font-medium text-gray-900">
                      {ticket.title}
                    </h3>
                    <p className="text-sm text-gray-600 mt-1">
                      Created by {ticket.createdBy.username}
                    </p>
                  </div>
                  <div className="flex gap-2">
                    <span className={`badge ${
                      ticket.status === 'OPEN' ? 'bg-blue-100 text-blue-800' :
                      ticket.status === 'IN_PROGRESS' ? 'bg-yellow-100 text-yellow-800' :
                      ticket.status === 'RESOLVED' ? 'bg-green-100 text-green-800' :
                      'bg-gray-100 text-gray-800'
                    }`}>
                      {ticket.status.replace('_', ' ')}
                    </span>
                    <span className={`badge ${
                      ticket.priority === 'CRITICAL' ? 'bg-red-100 text-red-800' :
                      ticket.priority === 'HIGH' ? 'bg-orange-100 text-orange-800' :
                      ticket.priority === 'MEDIUM' ? 'bg-yellow-100 text-yellow-800' :
                      'bg-gray-100 text-gray-800'
                    }`}>
                      {ticket.priority}
                    </span>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        )}
        <Link
          to="/tickets"
          className="block text-center text-blue-600 hover:text-blue-700
                     font-medium mt-4"
        >
          View all tickets →
        </Link>
      </div>
    </div>
  )
}
