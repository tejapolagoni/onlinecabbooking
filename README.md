# Online Cab Booking System (Basic)
Spring Boot + MySQL + React (Vite)

## Run
1. Install JDK 17, Maven, MySQL, Node.js.
2. Backend: create `backend/.env` with your local MySQL settings and a long random `ADMIN_SETUP_KEY`. Then run:
   `cd backend && mvn spring-boot:run`  (http://localhost:8080; database and tables are created automatically)
3. Frontend: create `frontend/.env` from `frontend/.env.example`, then run `cd frontend && npm install && npm run dev` (http://localhost:5173).

The `.env` files are for local development and are ignored by Git. For deployment, set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `ADMIN_SETUP_KEY`, and optionally `PORT` in the Render service's Environment settings. Admin registration requires the setup key; keep it private and share it only with trusted administrators. Set `VITE_API_BASE_URL` to `https://online-cab-booking.onrender.com/api` in the Vercel project's Environment Variables, then redeploy. Do not commit real credentials or rely on a local `.env` file being available on Render or Vercel.

## Default admin
admin@cab.com / admin123

## Flow to test
1. Register a Passenger and a Driver.
2. Passenger logs in and books a cab (PENDING).
3. Driver logs in, accepts (ACCEPTED), then completes (COMPLETED).
4. Passenger can cancel before completion.
5. Admin sees all bookings.

## API
POST /api/auth/register, POST /api/auth/login
POST /api/bookings, GET /api/bookings, GET /api/bookings/pending
GET /api/bookings/user/{id}, GET /api/bookings/driver/{id}
PUT /api/bookings/{id}/cancel, PUT /api/bookings/{id}/accept?driverId=, PUT /api/bookings/{id}/complete
GET /api/admin/stats
