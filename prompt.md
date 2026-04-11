# Context & Role
Act as an expert Full-Stack Software Engineer. I need you to implement the core frontend and backend for a peer-to-peer tutoring marketplace called "CampusGuru." 

# Tech Stack Constraint
* **Backend:** Java 21, Spring Boot 3.x, Spring Data JPA, Spring Web, Spring Security, MySQL.
* **Frontend:** React 18, TypeScript, Tailwind CSS, Axios.

# Scope & Feature Boundary
STRICTLY limit the implementation to the following features. **Authentication and login/registration are ALREADY implemented.** Do NOT generate auth flows. Instead, you MUST use the real authenticated user's data (ID and Role). On the backend, extract the user from `SecurityContextHolder`. On the frontend, assume an existing `useAuth()` hook provides the `currentUser` object.

Tutors and Learners MUST have distinct UIs.

---

## PART 1: BACKEND (Spring Boot + MySQL)

### 1. Database Entities (JPA)
Create the following entities with Lombok annotations for getters/setters/constructors. Relate them properly assuming a `User` entity already exists:
* **User (Existing but assume these fields):** `id` (Long), `firstName` (String), `lastName` (String), `role` (Enum: LEARNER, TUTOR, ADMIN).
* **Listing:** `id` (Long), `tutor` (ManyToOne -> User), `subject` (String), `availableTime` (String - e.g., "Mon, Wed, Fri 3:00 PM - 5:00 PM"), `contactInfo` (String), `proofOfCompetenceUrl` (String).
* **Booking:** `id` (Long), `listing` (ManyToOne -> Listing), `learner` (ManyToOne -> User), `requestedTime` (String), `paymentType` (String), `status` (Enum: PENDING, ACCEPTED, DECLINED), `meetingLink` (String, nullable), `declineReason` (String, nullable).

### 2. REST API Controllers & Services
Implement the following endpoints. **CRITICAL:** Extract the `tutorId` or `learnerId` securely from the authenticated user's token/session via Spring Security, do NOT trust the client to send their own user ID.
* `POST /api/listings`: Create a new listing (assigned to the currently authenticated Tutor).
* `GET /api/listings`: Fetch all recently posted listings (for the Learner home feed).
* `GET /api/listings/{id}`: Fetch details of a specific listing.
* `POST /api/bookings`: Create a new booking request. Payload should include listing ID, requested time, and payment type. Assign the learner as the currently authenticated user.
* `GET /api/bookings/pending`: Fetch all PENDING bookings for the *currently authenticated* tutor to display on their dashboard.
* `PUT /api/bookings/{id}/status`: Update the status of a booking. Ensure the logged-in tutor owns the listing.
    * If `status` is ACCEPTED, require `meeting_link` in the payload.
    * If `status` is DECLINED, require `decline_reason` in the payload.

---

## PART 2: FRONTEND (React + TypeScript + Tailwind CSS)

Create a responsive UI with a white and primary blue (`#2463EB`) color scheme. Assume a standard layout with a Navbar. Assume you can call `const { user } = useAuth();` to get the logged-in user.

### 1. Tutor Features & UI
* **Create Listing Page:** A form where tutors can input:
    * Subject name.
    * Available time for booking.
    * Contact Information (MS Teams, Social Media).
    * A file upload input for "Proof of Competence". (Implement the UI for the file upload, and mock the actual upload logic to return a dummy URL for now if S3/Supabase isn't hooked up yet).
    * A "Submit for Review" button.
* **Tutor Dashboard:** * A "Pending Requests" data table displaying `Student Name`, `Subject`, and `Requested Time`.
    * Each row should have an **Accept** and **Decline** button.
    * **Accept Modal:** Clicking Accept opens a modal prompting the tutor to paste a meeting link (Zoom, Meet, Teams). Submitting triggers the PUT status endpoint.
    * **Decline Modal:** Clicking Decline opens a modal prompting the tutor to type a reason for denial. Submitting triggers the PUT status endpoint.

### 2. Learner Features & UI
* **Learner Home Page:** * A grid layout showing recently posted listings fetched from the backend. 
    * Each card displays the Tutor's Name, Subject, Available Time, Asking Price (can default to Free/Placeholder), and a "View Listing" button.
* **Listing Details Page:** * Shows data specific to the listing: Subject, Available Time, Contact Info, and a way to view the Proof of Competence.
    * Includes a dropdown/input field to select `Payment Type` (e.g., Cash, GCash).
    * A "Book this tutor" button that submits the booking request.

---

## Execution Steps
Please generate the code in the following order:
1. Spring Boot Models, Repositories, and Enums.
2. Spring Boot Services and REST Controllers (ensuring SecurityContextHolder is used for user IDs).
3. React API/Axios service file for making backend calls (assume Axios interceptors are already handling the JWT attachment).
4. React Components for the **Tutor** views (Create Listing Form, Dashboard Table, Accept/Decline Modals).
5. React Components for the **Learner** views (Home Feed Grid, Listing Details Page, Booking Form).

Output the code comprehensively, with clear file names at the top of each code block. Use functional components and React Hooks for the frontend.