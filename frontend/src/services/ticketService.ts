import api from './api';
import type { Ticket, PageResponse, TicketPriority, TicketStatus } from '../types';

export const ticketService = {
  getAll: async (page = 0, size = 10): Promise<PageResponse<Ticket>> => {
    const response = await api.get(
      `/api/tickets?page=${page}&size=${size}&sortBy=createdAt&sortDir=desc`);
    return response.data;
  },

  getById: async (id: string): Promise<Ticket> => {
    const response = await api.get(`/api/tickets/${id}`);
    return response.data;
  },

  create: async (title: string, description: string,
                  priority: TicketPriority): Promise<Ticket> => {
    const response = await api.post('/api/tickets', {
      title, description, priority
    });
    return response.data;
  },

  update: async (id: string, data: {
    title?: string;
    description?: string;
    status?: TicketStatus;
    priority?: TicketPriority;
    assignedToId?: string;
  }): Promise<Ticket> => {
    const response = await api.put(`/api/tickets/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await api.delete(`/api/tickets/${id}`);
  },

  search: async (query: string,
                  page = 0): Promise<PageResponse<Ticket>> => {
    const response = await api.get(
      `/api/tickets/search?query=${query}&page=${page}`);
    return response.data;
  }
};
