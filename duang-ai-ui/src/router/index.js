import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/ai/chat',
      name: 'ChatView',
      component: () => import('@/views/ChatView.vue'),
    },
    {
      path: '/ai/chat/stream',
      name: 'ChatStreamView',
      component: () => import('@/views/ChatStreamView.vue'),
    },
    {
      path: '/ai/vl/chat',
      name: 'VlChatView',
      component: () => import('@/views/VlChatView.vue'),
    }
  ],
})

export default router
