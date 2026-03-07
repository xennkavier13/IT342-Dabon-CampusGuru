# CampusGuru Task Checklist

## Finished Tasks

### Project Setup
- [x] Initialize backend using Spring Boot
- [x] Initialize frontend using React + TypeScript + Vite
- [x] Set up Supabase connection configuration
- [x] Create basic project structure for backend and frontend

### Backend - Authentication Core
- [x] Create `User` entity with learner/tutor role support
- [x] Create `UserRepository` for user lookup and validation
- [x] Implement registration logic in `UserService`
- [x] Implement login logic in `UserService`
- [x] Add password hashing using `BCryptPasswordEncoder`
- [x] Create auth DTOs (`RegisterRequest`, `LoginRequest`, `AuthResponse`)
- [x] Create auth endpoints (`/api/auth/register`, `/api/auth/login`)

### Backend - JWT Security
- [x] Add JWT dependencies (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`)
- [x] Create `JwtService` for token generation and validation
- [x] Create `JwtAuthenticationFilter` for Bearer token parsing
- [x] Keep `/api/auth/**` endpoints public and protect other endpoints
- [x] Return JWT token in authentication response

### Frontend - Authentication UI
- [x] Build Login page with form validation and API integration
- [x] Build Register page with form validation and role selection
- [x] Add basic page flow between Login, Register, and Dashboard

