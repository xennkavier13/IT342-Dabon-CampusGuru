import { useState, useEffect } from 'react';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import { authService } from './services/authService';
import './App.css';

type Page = 'login' | 'register' | 'dashboard';

function App() {
  const [currentPage, setCurrentPage] = useState<Page>('login');
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    // Check if user is already logged in
    const authenticated = authService.isAuthenticated();
    setIsAuthenticated(authenticated);
    if (authenticated) {
      setCurrentPage('dashboard');
    }
  }, []);

  const handleLoginSuccess = () => {
    setIsAuthenticated(true);
    setCurrentPage('dashboard');
  };

  const handleRegisterSuccess = () => {
    setCurrentPage('login');
  };

  const handleLogout = () => {
    setIsAuthenticated(false);
    setCurrentPage('login');
  };

  return (
    <>
      {currentPage === 'login' && (
        <LoginPage
          onLoginSuccess={handleLoginSuccess}
          onNavigateToRegister={() => setCurrentPage('register')}
        />
      )}
      {currentPage === 'register' && (
        <RegisterPage
          onRegisterSuccess={handleRegisterSuccess}
          onNavigateToLogin={() => setCurrentPage('login')}
        />
      )}
      {currentPage === 'dashboard' && isAuthenticated && (
        <DashboardPage onLogout={handleLogout} />
      )}
    </>
  );
}

export default App;
