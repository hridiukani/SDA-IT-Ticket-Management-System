export type Role = 'ROLE_USER' | 'ROLE_TECHNICIAN' |
                   'ROLE_MANAGER' | 'ROLE_ADMIN';

export type TicketStatus = 'OPEN' | 'IN_PROGRESS' |
                            'RESOLVED' | 'CLOSED';

export type TicketPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface User {
  id: string;
  username: string;
  email: string;
  role: Role;
  enabled: boolean;
  createdAt: string;
}

export interface Ticket {
  id: string;
  title: string;
  description: string;
  status: TicketStatus;
  priority: TicketPriority;
  createdBy: User;
  assignedTo: User | null;
  createdAt: string;
  updatedAt: string;
  resolvedAt: string | null;
  commentCount: number;
}

export interface Comment {
  id: string;
  content: string;
  user: User;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  user: User;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface ApiError {
  status: number;
  message: string;
  timestamp: string;
  path: string;
  validationErrors?: Record<string, string>;
}
