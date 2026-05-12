import type { Notification } from '../types/notification.types';

interface NotificationListProps {
  notifications: Notification[];
  loading: boolean;
  onMarkAsRead: (id: string) => void;
}

const typeConfig: Record<string, { color: string; icon: string; label: string }> = {
  BOOKING_RECEIVED: {
    color: 'text-blue-600 bg-blue-50',
    icon: '📩',
    label: 'New Booking',
  },
  BOOKING_ACCEPTED: {
    color: 'text-emerald-600 bg-emerald-50',
    icon: '✅',
    label: 'Accepted',
  },
  BOOKING_DECLINED: {
    color: 'text-red-600 bg-red-50',
    icon: '❌',
    label: 'Declined',
  },
};

const NotificationList = ({ notifications, loading, onMarkAsRead }: NotificationListProps) => {
  if (loading) {
    return (
      <div className="flex items-center justify-center px-4 py-8">
        <div className="h-5 w-5 animate-spin rounded-full border-2 border-blue-500 border-t-transparent" />
        <span className="ml-2 text-sm text-slate-500">Loading...</span>
      </div>
    );
  }

  if (notifications.length === 0) {
    return (
      <div className="px-4 py-8 text-center">
        <p className="text-sm text-slate-500">No notifications yet.</p>
      </div>
    );
  }

  return (
    <div className="max-h-96 overflow-y-auto">
      {notifications.map((notification) => {
        const config = typeConfig[notification.type] || typeConfig.BOOKING_RECEIVED;
        const timeAgo = formatTimeAgo(notification.createdAt);

        return (
          <button
            key={notification.id}
            onClick={() => {
              if (!notification.isRead) onMarkAsRead(notification.id);
            }}
            className={`flex w-full items-start gap-3 border-b border-slate-50 px-4 py-3 text-left transition hover:bg-slate-50 ${
              !notification.isRead ? 'bg-blue-50/30' : ''
            }`}
          >
            {/* Type icon */}
            <span className={`mt-0.5 flex h-8 w-8 shrink-0 items-center justify-center rounded-full text-sm ${config.color}`}>
              {config.icon}
            </span>

            <div className="min-w-0 flex-1">
              <div className="flex items-center gap-2">
                <span className={`text-xs font-bold uppercase tracking-wider ${config.color.split(' ')[0]}`}>
                  {config.label}
                </span>
                {!notification.isRead && (
                  <span className="h-2 w-2 rounded-full bg-blue-500" />
                )}
              </div>
              <p className="mt-0.5 text-sm text-slate-700 line-clamp-2">{notification.message}</p>
              <p className="mt-1 text-xs text-slate-400">{timeAgo}</p>
            </div>
          </button>
        );
      })}
    </div>
  );
};

function formatTimeAgo(dateString: string): string {
  const now = new Date();
  const date = new Date(dateString);
  const diffMs = now.getTime() - date.getTime();
  const diffMins = Math.floor(diffMs / 60000);

  if (diffMins < 1) return 'Just now';
  if (diffMins < 60) return `${diffMins}m ago`;
  const diffHours = Math.floor(diffMins / 60);
  if (diffHours < 24) return `${diffHours}h ago`;
  const diffDays = Math.floor(diffHours / 24);
  if (diffDays < 7) return `${diffDays}d ago`;
  return date.toLocaleDateString();
}

export default NotificationList;
