import { useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { ticketService } from '../../services/ticketService'
import { commentService } from '../../services/commentService'
import { useAuth } from '../../context/AuthContext'
import { ArrowLeft, MessageSquare, Trash2, Edit } from 'lucide-react'
import type { TicketStatus, TicketPriority } from '../../types'

export default function TicketDetailPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { user, hasRole } = useAuth()
  const [commentContent, setCommentContent] = useState('')
  const [isEditing, setIsEditing] = useState(false)
  const [editStatus, setEditStatus] = useState<TicketStatus>('OPEN')
  const [editPriority, setEditPriority] = useState<TicketPriority>('MEDIUM')

  const { data: ticket, isLoading: ticketLoading } = useQuery({
    queryKey: ['ticket', id],
    queryFn: () => ticketService.getById(id!),
    enabled: !!id
  })

  const { data: comments = [], isLoading: commentsLoading } = useQuery({
    queryKey: ['comments', id],
    queryFn: () => commentService.getByTicket(id!),
    enabled: !!id
  })

  const addCommentMutation = useMutation({
    mutationFn: () => commentService.add(id!, commentContent),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['comments', id] })
      queryClient.invalidateQueries({ queryKey: ['ticket', id] })
      setCommentContent('')
    }
  })

  const updateTicketMutation = useMutation({
    mutationFn: () => ticketService.update(id!, {
      status: editStatus,
      priority: editPriority
    }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ticket', id] })
      setIsEditing(false)
    }
  })

  const deleteTicketMutation = useMutation({
    mutationFn: () => ticketService.delete(id!),
    onSuccess: () => {
      navigate('/tickets')
    }
  })

  if (ticketLoading) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2
                        border-blue-600" />
      </div>
    )
  }

  if (!ticket) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-500">Ticket not found</p>
        <Link to="/tickets" className="text-blue-600 hover:text-blue-700 mt-2
                                        inline-block">
          ← Back to tickets
        </Link>
      </div>
    )
  }

  const canEdit = hasRole(['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_TECHNICIAN'])
  const canDelete = ticket.createdBy.id === user?.id ||
                    hasRole(['ROLE_ADMIN'])

  const getStatusColor = (status: TicketStatus) => {
    switch (status) {
      case 'OPEN': return 'bg-blue-100 text-blue-800'
      case 'IN_PROGRESS': return 'bg-yellow-100 text-yellow-800'
      case 'RESOLVED': return 'bg-green-100 text-green-800'
      case 'CLOSED': return 'bg-gray-100 text-gray-800'
    }
  }

  const getPriorityColor = (priority: TicketPriority) => {
    switch (priority) {
      case 'CRITICAL': return 'bg-red-100 text-red-800'
      case 'HIGH': return 'bg-orange-100 text-orange-800'
      case 'MEDIUM': return 'bg-yellow-100 text-yellow-800'
      case 'LOW': return 'bg-gray-100 text-gray-800'
    }
  }

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <button
            onClick={() => navigate('/tickets')}
            className="btn-secondary"
          >
            <ArrowLeft className="h-5 w-5" />
          </button>
          <h1 className="text-3xl font-bold text-gray-900">Ticket Details</h1>
        </div>
        {canDelete && (
          <button
            onClick={() => {
              if (window.confirm('Are you sure you want to delete this ticket?')) {
                deleteTicketMutation.mutate()
              }
            }}
            className="btn-danger"
          >
            <Trash2 className="h-4 w-4" />
            Delete
          </button>
        )}
      </div>

      {/* Ticket Info */}
      <div className="card">
        <div className="flex justify-between items-start mb-6">
          <div className="flex-1">
            <h2 className="text-2xl font-bold text-gray-900 mb-2">
              {ticket.title}
            </h2>
            <p className="text-gray-600 whitespace-pre-wrap">
              {ticket.description || 'No description provided'}
            </p>
          </div>
          {canEdit && !isEditing && (
            <button
              onClick={() => {
                setEditStatus(ticket.status)
                setEditPriority(ticket.priority)
                setIsEditing(true)
              }}
              className="btn-secondary ml-4"
            >
              <Edit className="h-4 w-4" />
              Edit
            </button>
          )}
        </div>

        {isEditing ? (
          <div className="border-t border-gray-200 pt-6 space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Status
                </label>
                <select
                  value={editStatus}
                  onChange={(e) => setEditStatus(e.target.value as TicketStatus)}
                  className="input-field"
                >
                  <option value="OPEN">Open</option>
                  <option value="IN_PROGRESS">In Progress</option>
                  <option value="RESOLVED">Resolved</option>
                  <option value="CLOSED">Closed</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Priority
                </label>
                <select
                  value={editPriority}
                  onChange={(e) => setEditPriority(e.target.value as TicketPriority)}
                  className="input-field"
                >
                  <option value="LOW">Low</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="HIGH">High</option>
                  <option value="CRITICAL">Critical</option>
                </select>
              </div>
            </div>
            <div className="flex gap-3">
              <button
                onClick={() => updateTicketMutation.mutate()}
                disabled={updateTicketMutation.isPending}
                className="btn-primary"
              >
                {updateTicketMutation.isPending ? 'Saving...' : 'Save Changes'}
              </button>
              <button
                onClick={() => setIsEditing(false)}
                className="btn-secondary"
              >
                Cancel
              </button>
            </div>
          </div>
        ) : (
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4
                          border-t border-gray-200 pt-6">
            <div>
              <p className="text-sm text-gray-600 mb-1">Status</p>
              <span className={`badge ${getStatusColor(ticket.status)}`}>
                {ticket.status.replace('_', ' ')}
              </span>
            </div>
            <div>
              <p className="text-sm text-gray-600 mb-1">Priority</p>
              <span className={`badge ${getPriorityColor(ticket.priority)}`}>
                {ticket.priority}
              </span>
            </div>
            <div>
              <p className="text-sm text-gray-600 mb-1">Created By</p>
              <p className="text-sm font-medium text-gray-900">
                {ticket.createdBy.username}
              </p>
            </div>
            <div>
              <p className="text-sm text-gray-600 mb-1">Assigned To</p>
              <p className="text-sm font-medium text-gray-900">
                {ticket.assignedTo?.username || (
                  <span className="text-gray-400 italic">Unassigned</span>
                )}
              </p>
            </div>
          </div>
        )}

        <div className="grid grid-cols-2 gap-4 mt-4 pt-4 border-t border-gray-200">
          <div>
            <p className="text-sm text-gray-600">Created</p>
            <p className="text-sm text-gray-900">
              {new Date(ticket.createdAt).toLocaleString()}
            </p>
          </div>
          {ticket.resolvedAt && (
            <div>
              <p className="text-sm text-gray-600">Resolved</p>
              <p className="text-sm text-gray-900">
                {new Date(ticket.resolvedAt).toLocaleString()}
              </p>
            </div>
          )}
        </div>
      </div>

      {/* Comments Section */}
      <div className="card">
        <h3 className="text-xl font-bold text-gray-900 mb-4 flex items-center gap-2">
          <MessageSquare className="h-5 w-5" />
          Comments ({comments.length})
        </h3>

        {/* Add Comment Form */}
        <form
          onSubmit={(e) => {
            e.preventDefault()
            if (commentContent.trim()) {
              addCommentMutation.mutate()
            }
          }}
          className="mb-6"
        >
          <textarea
            value={commentContent}
            onChange={(e) => setCommentContent(e.target.value)}
            className="input-field resize-none"
            rows={3}
            placeholder="Add a comment..."
            maxLength={1000}
          />
          <div className="flex justify-between items-center mt-2">
            <p className="text-xs text-gray-500">
              {commentContent.length}/1000 characters
            </p>
            <button
              type="submit"
              disabled={!commentContent.trim() || addCommentMutation.isPending}
              className="btn-primary"
            >
              {addCommentMutation.isPending ? 'Posting...' : 'Post Comment'}
            </button>
          </div>
        </form>

        {/* Comments List */}
        {commentsLoading ? (
          <div className="text-center py-8 text-gray-500">
            Loading comments...
          </div>
        ) : comments.length === 0 ? (
          <div className="text-center py-8 text-gray-500">
            No comments yet. Be the first to comment!
          </div>
        ) : (
          <div className="space-y-4">
            {comments.map(comment => (
              <div
                key={comment.id}
                className="border border-gray-200 rounded-lg p-4"
              >
                <div className="flex justify-between items-start mb-2">
                  <div>
                    <p className="font-medium text-gray-900">
                      {comment.user.username}
                    </p>
                    <p className="text-xs text-gray-500">
                      {new Date(comment.createdAt).toLocaleString()}
                    </p>
                  </div>
                </div>
                <p className="text-gray-700 whitespace-pre-wrap">
                  {comment.content}
                </p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
