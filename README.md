# EcoCarbon-MRV Web V1

## Tech Stack
- Backend: Spring Boot 3, MySQL, Redis
- Frontend: Vue 3 + Vite

## Project Structure
- `backend`: Java backend service
- `frontend`: Vue frontend page
- `database_init.sql`: Database initialization script

## Backend Start
1. Create MySQL database and tables:
   - Run `database_init.sql`
2. Make sure Redis is running at `localhost:6379`
3. Edit `backend/src/main/resources/application.yml` if needed
4. Start backend:
   - `cd backend`
   - `mvn spring-boot:run`

Backend default user:
- username: `admin`
- password: `123456`

## Frontend Start
1. Install dependencies:
   - `cd frontend`
   - `npm install`
2. Start app:
   - `npm run dev`
3. Open browser:
   - `http://localhost:5173`

## Implemented APIs
- `POST /api/v1/auth/login` login and Redis token session
- `GET /api/v1/projects` project list
- `POST /api/v1/projects` create project
- `POST /api/v1/carbon/inventory` calculate inventory
