import api from './api';
import type { Comment } from '../types';

export const commentService = {
  getByTicket: async (ticketId: string): Promise<Comment[]> => {
    const response = await api.get(
      `/api/tickets/${ticketId}/comments`);
    return response.data;
  },

  add: async (ticketId: string,
               content: string): Promise<Comment> => {
    const response = await api.post(
      `/api/tickets/${ticketId}/comments`, { content });
    return response.data;
  },

  delete: async (ticketId: string,
                  commentId: string): Promise<void> => {
    await api.delete(
      `/api/tickets/${ticketId}/comments/${commentId}`);
  }
};
