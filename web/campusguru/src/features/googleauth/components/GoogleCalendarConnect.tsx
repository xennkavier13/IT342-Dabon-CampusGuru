import { useEffect, useState } from 'react';
import { googleAuthService } from '../services/googleAuthService';

interface GoogleCalendarConnectProps {
  onConnected?: () => void;
}

const GoogleCalendarConnect = ({ onConnected }: GoogleCalendarConnectProps) => {
  const [connected, setConnected] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkStatus = async () => {
      try {
        const { connected: isConnected } = await googleAuthService.getConnectionStatus();
        setConnected(isConnected);
        if (isConnected && onConnected) onConnected();
      } catch {
        // Silently fail — user may not have connected yet
      } finally {
        setLoading(false);
      }
    };

    void checkStatus();

    // Also check if we just returned from OAuth (query param)
    const params = new URLSearchParams(window.location.search);
    if (params.get('google_connected') === 'true') {
      setConnected(true);
      // Clean up the URL
      window.history.replaceState({}, '', window.location.pathname);
    }
  }, [onConnected]);

  const handleConnect = () => {
    // Redirect the browser to the backend OAuth initiation endpoint.
    // We pass the JWT token as a query param because the browser redirect
    // can't include the Authorization header.
    const token = localStorage.getItem('authToken');
    window.location.href = `http://localhost:8080/api/google-auth/connect?token=${token}`;
  };

  if (loading) {
    return (
      <div className="flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-4 py-3">
        <div className="h-4 w-4 animate-spin rounded-full border-2 border-blue-500 border-t-transparent" />
        <span className="text-sm text-slate-500">Checking calendar connection...</span>
      </div>
    );
  }

  if (connected) {
    return (
      <div className="flex items-center gap-2 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3">
        <svg className="h-5 w-5 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
        </svg>
        <span className="text-sm font-semibold text-emerald-700">Google Calendar Connected</span>
      </div>
    );
  }

  return (
    <button
      onClick={handleConnect}
      className="flex items-center gap-3 rounded-xl border border-slate-200 bg-white px-5 py-3 shadow-sm transition hover:border-blue-300 hover:shadow-md active:scale-[0.98]"
    >
      {/* Google Calendar icon */}
      <svg className="h-6 w-6" viewBox="0 0 24 24" fill="none">
        <rect x="3" y="4" width="18" height="18" rx="3" stroke="#4285F4" strokeWidth="1.5" />
        <path d="M3 9h18" stroke="#4285F4" strokeWidth="1.5" />
        <circle cx="8" cy="14" r="1.5" fill="#EA4335" />
        <circle cx="12" cy="14" r="1.5" fill="#34A853" />
        <circle cx="16" cy="14" r="1.5" fill="#FBBC04" />
      </svg>
      <div className="text-left">
        <p className="text-sm font-semibold text-slate-800">Connect Google Calendar</p>
        <p className="text-xs text-slate-500">Required to manage availability &amp; bookings</p>
      </div>
    </button>
  );
};

export default GoogleCalendarConnect;
