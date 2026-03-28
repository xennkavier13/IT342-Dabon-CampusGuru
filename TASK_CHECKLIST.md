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

### Mobile (Android) - Authentication Phase 2
- [x] Add Retrofit + Gson API client for auth endpoints
- [x] Add auth models for mobile request/response handling
- [x] Create `AuthApiService` with `/api/auth/register` and `/api/auth/login`
- [x] Implement `SessionManager` for JWT persistence (EncryptedSharedPreferences with fallback)
- [x] Add `AuthInterceptor` to attach `Bearer <token>` to non-auth requests
- [x] Implement MVVM auth stack (`AuthRepository`, `AuthViewModel`, `AuthViewModelFactory`)
- [x] Build `LoginActivity` and `RegisterActivity` with API integration
- [x] Build XML auth screens using ConstraintLayout + Material TextInputLayout
- [x] Add loading states and toast-based error/success feedback
- [x] Wire launcher flow to login screen via Android Manifest

### Cross-Platform Auth Contract Alignment
- [x] Align mobile register payload with backend/web contract:
	- [x] `firstName`
	- [x] `lastName`
	- [x] `institutionalEmail`
	- [x] `password`
	- [x] `role` (LEARNER/TUTOR)
	- [x] `username` derived from institutional email
- [x] Add mobile confirm-password field and validation
- [x] Add mobile learner/tutor role selector in registration form
- [x] Update mobile login payload to backend-compatible `username` field

### Backend - Auth Access and Stability Fixes
- [x] Add explicit CORS configuration in Spring Security filter chain
- [x] Permit preflight `OPTIONS` requests to prevent auth endpoint blocking
- [x] Keep `/api/auth/**` endpoints publicly accessible
- [x] Fix `JwtAuthenticationFilter` method signature with `@NonNull` annotations
- [x] Update login logic to support username OR institutional email lookup

### Android Build and Runtime Fixes
- [x] Resolve Material theme/resource linking errors
- [x] Enable `buildConfig` generation for `BuildConfig.DEBUG` usage
- [x] Add network security config for local cleartext dev API access (`10.0.2.2`)
- [x] Fix registration layout ConstraintLayout attribute names for successful AAPT linking

