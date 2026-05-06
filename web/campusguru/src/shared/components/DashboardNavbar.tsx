import React from 'react';
import Button from '@shared/components/Button';

interface DashboardNavbarProps {
  username?: string;
  onLogout: () => void;
}

const DashboardNavbar: React.FC<DashboardNavbarProps> = ({ onLogout }) => {
  return (
    <nav className="bg-white shadow-md">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="relative flex h-16 items-center justify-between">
          <div className="flex items-center">
            <h1 className="text-2xl font-bold text-blue-500">CampusGuru</h1>
          </div>

          <a href="#" className="absolute left-1/2 -translate-x-1/2 text-sm font-medium text-blue-600 hover:text-blue-700">
            Home
          </a>

          <div className="flex items-center">
            <Button onClick={onLogout} variant="primary">
              Logout
            </Button>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default DashboardNavbar;
