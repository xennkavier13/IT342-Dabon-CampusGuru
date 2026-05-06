import { Link, useNavigate } from 'react-router-dom';
import Button from '@shared/components/Button';
import { useAuth } from '@features/auth';

interface MarketplaceNavbarProps {
  title: string;
}

const MarketplaceNavbar = ({ title }: MarketplaceNavbarProps) => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const role = user?.role;

  return (
    <header className="sticky top-0 z-20 border-b border-blue-100 bg-white/95 backdrop-blur">
      <div className="mx-auto flex w-full max-w-6xl items-center justify-between px-4 py-3 sm:px-6 lg:px-8">
        <div>
          <p className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-600">CampusGuru</p>
          <h1 className="text-xl font-bold text-slate-900">{title}</h1>
        </div>

        <nav className="flex items-center gap-2">
          {role === 'TUTOR' && (
            <>
              <Link to="/tutor/dashboard" className="rounded-md px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-blue-50">
                Dashboard
              </Link>
              <Link to="/tutor/listings/new" className="rounded-md px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-blue-50">
                Create Listing
              </Link>
            </>
          )}

          {role === 'LEARNER' && (
            <Link to="/learner/home" className="rounded-md px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-blue-50">
              Browse Tutors
            </Link>
          )}

          <Button onClick={handleLogout}>Logout</Button>
        </nav>
      </div>
    </header>
  );
};

export default MarketplaceNavbar;
