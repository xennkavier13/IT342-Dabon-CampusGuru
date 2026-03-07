import React from 'react';
import { authService } from '../services/authService';
import DashboardNavbar from '../components/DashboardNavbar';

interface DashboardPageProps {
  onLogout?: () => void;
}

const DashboardPage: React.FC<DashboardPageProps> = ({ onLogout }) => {
  const user = authService.getCurrentUser();

  const handleLogout = () => {
    authService.logout();
    onLogout?.();
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <DashboardNavbar username={user?.username} onLogout={handleLogout} />

      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          <div className="bg-white rounded-lg shadow-md p-6">
            <h2 className="text-2xl font-bold mb-4">Dashboard</h2>

          </div>
        </div>
      </main>
    </div>
  );
};

export default DashboardPage;
