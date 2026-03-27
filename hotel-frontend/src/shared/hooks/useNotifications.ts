import { useCallback, useEffect, useRef, useState } from 'react';
import { Notification, NotificationType } from '@/entities';

const AUTO_DISMISS_MS = 5000;

export function useNotifications() {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const timeoutIdsRef = useRef<number[]>([]);

  const dismissNotification = useCallback((id: number) => {
    setNotifications(prev => prev.filter(notification => notification.id !== id));
  }, []);

  const showNotification = useCallback((message: string, type: NotificationType = 'info') => {
    const id = Date.now();

    setNotifications(prev => [...prev, { id, message, type }]);

    const timeoutId = window.setTimeout(() => {
      setNotifications(prev => prev.filter(notification => notification.id !== id));
      timeoutIdsRef.current = timeoutIdsRef.current.filter(activeId => activeId !== timeoutId);
    }, AUTO_DISMISS_MS);

    timeoutIdsRef.current.push(timeoutId);
  }, []);

  useEffect(() => {
    return () => {
      timeoutIdsRef.current.forEach(timeoutId => window.clearTimeout(timeoutId));
      timeoutIdsRef.current = [];
    };
  }, []);

  return {
    notifications,
    showNotification,
    dismissNotification,
  };
}

