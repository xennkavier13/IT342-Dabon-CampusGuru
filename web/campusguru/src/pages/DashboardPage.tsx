import React from 'react';
import { authService } from '../services/authService';
import Button from '../components/Button';

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
      <nav className="bg-white shadow-md">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <h1 className="text-2xl font-bold text-gray-800">CampusGuru</h1>
            </div>
            <div className="flex items-center">
              <span className="text-gray-700 mr-4">
                Welcome, <strong>{user?.username || 'User'}</strong>!
              </span>
              <Button onClick={handleLogout} variant="secondary">
                Logout
              </Button>
            </div>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          <div className="bg-white rounded-lg shadow-md p-6">
            <h2 className="text-2xl font-bold mb-4">Dashboard</h2>
            <p className="text-gray-600 mb-4">
              You have successfully logged in to CampusGuru!
            </p>
            
            {user && (
              <div className="bg-blue-50 border border-blue-200 rounded p-4">
                <h3 className="font-semibold text-lg mb-2">User Information</h3>
                <p><strong>Username:</strong> {user.username}</p>
                <p><strong>User ID:</strong> {user.userId}</p>
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  );
};

export default DashboardPage;
