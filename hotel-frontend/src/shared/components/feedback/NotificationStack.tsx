import { Notification } from '@/entities';

type NotificationStackProps = {
  notifications: Notification[];
  onDismiss: (id: number) => void;
};

export function NotificationStack({ notifications, onDismiss }: NotificationStackProps) {
  return (
    <div
      style={{
        position: 'fixed',
        top: '1rem',
        right: '1rem',
        display: 'flex',
        flexDirection: 'column',
        gap: '0.5rem',
        zIndex: 1000,
      }}
    >
      {notifications.map(notification => (
        <div
          key={notification.id}
          style={{
            padding: '1rem',
            borderRadius: '4px',
            color: 'white',
            backgroundColor:
              notification.type === 'success'
                ? '#4caf50'
                : notification.type === 'error'
                  ? '#f44336'
                  : '#2196f3',
            boxShadow: '0 2px 5px rgba(0,0,0,0.2)',
            minWidth: '250px',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
          }}
        >
          <span>{notification.message}</span>
          <button
            onClick={() => onDismiss(notification.id)}
            style={{
              background: 'none',
              border: 'none',
              color: 'white',
              cursor: 'pointer',
              fontWeight: 'bold',
              fontSize: '1.2rem',
              marginLeft: '1rem',
            }}
          >
            ×
          </button>
        </div>
      ))}
    </div>
  );
}

