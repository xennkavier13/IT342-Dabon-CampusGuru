import { Navigate, Route, Routes, useNavigate } from 'react-router-dom';
import type { ReactNode } from 'react';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import TutorCreateListingPage from './pages/TutorCreateListingPage';
import TutorDashboardPage from './pages/TutorDashboardPage';
import LearnerHomePage from './pages/LearnerHomePage';
import LearnerListingDetailsPage from './pages/LearnerListingDetailsPage';
import { useAuth } from './hooks/useAuth';
import './App.css';

const RoleHomeRedirect = () => {
  const { user } = useAuth();

  if (user?.role === 'TUTOR') {
    return <Navigate to="/tutor/dashboard" replace />;
  }

  if (user?.role === 'LEARNER') {
    return <Navigate to="/learner/home" replace />;
  }

  return <Navigate to="/login" replace />;
};

const LoginRoute = () => {
  const navigate = useNavigate();
  const { isAuthenticated, user } = useAuth();

  if (isAuthenticated) {
    if (user?.role === 'TUTOR') {
      return <Navigate to="/tutor/dashboard" replace />;
    }
    return <Navigate to="/learner/home" replace />;
  }

  return (
    <LoginPage
      onLoginSuccess={() => navigate('/')}
      onNavigateToRegister={() => navigate('/register')}
    />
  );
};

const RegisterRoute = () => {
  const navigate = useNavigate();
  return (
    <RegisterPage
      onRegisterSuccess={() => navigate('/login')}
      onNavigateToLogin={() => navigate('/login')}
    />
  );
};

const TutorOnlyRoute = ({ children }: { children: ReactNode }) => {
  const { isAuthenticated, user } = useAuth();
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  if (user?.role !== 'TUTOR') {
    return <Navigate to="/" replace />;
  }
  return children;
};

const LearnerOnlyRoute = ({ children }: { children: ReactNode }) => {
  const { isAuthenticated, user } = useAuth();
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  if (user?.role !== 'LEARNER') {
    return <Navigate to="/" replace />;
  }
  return children;
};

function App() {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return (
      <Routes>
        <Route path="/login" element={<LoginRoute />} />
        <Route path="/register" element={<RegisterRoute />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    );
  };

  return (
    <Routes>
      <Route path="/" element={<RoleHomeRedirect />} />

      <Route
        path="/tutor/dashboard"
        element={
          <TutorOnlyRoute>
            <TutorDashboardPage />
          </TutorOnlyRoute>
        }
      />
      <Route
        path="/tutor/listings/new"
        element={
          <TutorOnlyRoute>
            <TutorCreateListingPage />
          </TutorOnlyRoute>
        }
      />

      <Route
        path="/learner/home"
        element={
          <LearnerOnlyRoute>
            <LearnerHomePage />
          </LearnerOnlyRoute>
        }
      />
      <Route
        path="/learner/listings/:id"
        element={
          <LearnerOnlyRoute>
            <LearnerListingDetailsPage />
          </LearnerOnlyRoute>
        }
      />

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default App;
