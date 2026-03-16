# Life Mentor Backend

Spring Boot backend for the Life Mentor platform, an AI-assisted lifestyle and wellbeing application focused on habit tracking, reflective check-ins, goal management, and supportive non-medical guidance.

## Overview

This service exposes REST APIs for:

- user authentication and account recovery
- profile management and profile picture handling
- lifestyle assessment capture and AI-generated feedback
- AI wellbeing chat conversations
- daily check-in questions, responses, analytics, and streaks
- personal wellbeing goals and progress tracking
- wellbeing summaries, alerts, and recommendations
- scheduled daily reminder emails for unanswered check-ins

The backend is designed for a React frontend, persists data in MySQL, and integrates with Gemini for conversational AI and assessment feedback.

## Core Capabilities

### Authentication and Security

- JWT-based stateless authentication
- user registration and login
- forgot-password and reset-password flow
- password hashing with BCrypt
- failed login attempt tracking and temporary account lock
- protected routes with Spring Security

### Profile Management

- fetch and update user profile
- change password
- upload and delete profile pictures
- delete account
- assessment completion status check

### Lifestyle Assessment

- create, update, fetch, and delete a lifestyle assessment
- tracks sleep, meals, exercise, study/work hours, screen time, mood, and wellbeing notes
- validates assessment rules such as sleep duration and hour limits
- generates structured AI feedback for submitted assessments

### AI Features

- Gemini-powered wellbeing chatbot
- conversation history and category-based chat sessions
- save chat messages and regenerate responses
- AI-generated assessment feedback with:
  - summary
  - positive highlights
  - suggestions
  - motivational message
  - risk level

### Daily Check-Ins

- fetch active daily check-in questions
- fetch questions by category
- submit batch or single daily responses
- prevent duplicate same-day responses
- retrieve today's or a selected date's responses
- streak tracking and completion status
- category-level analytics and mood trends
- seeded default questions for nutrition, sleep, stress, mood, exercise, productivity, and social connection

### Goals and Wellbeing

- create, update, complete, fetch, and delete personal goals
- progress percentage tracking
- active and overdue goals
- wellbeing summary generation
- alert generation and alert resolution
- date-range trend analysis
- daily recommendations based on recent user data

### Notifications and Scheduling

- scheduled daily check-in reminder emails
- reminder emails sent only to users who have not completed today's check-in
- configurable reminder question count
- scheduler foundation for weekly summaries and future alert notifications

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL
- Jakarta Validation
- Java Mail Sender
- JWT (`jjwt`)
- Gemini API

## Architecture

The backend follows a layered architecture:

- `controller`: request handling and REST endpoints
- `service`: business logic
- `repository`: data access
- `entity`: persistence models
- `dto`: request and response contracts
- `config`: security, async, seeding, storage, and app configuration
- `util`: prompt building and wellbeing analysis

## API Modules

### Auth

Base path: `/api/auth`

- register
- login
- logout
- forgot password
- reset password
- validate token

### Profile

Base path: `/api/profile`

- get profile
- update profile
- change password
- upload picture
- delete picture
- delete account
- deactivate account endpoint
- assessment status

### Lifestyle Assessment

Base path: `/api/lifestyle-assessment`

- create or update assessment
- get assessment
- update assessment
- delete assessment

### AI Feedback

Base path: `/api/ai-feedback`

- get feedback by assessment
- generate feedback
- delete feedback
- service health check

### AI Chat

Base path: `/api/ai-chat`

- send message
- list conversations
- get conversation history
- filter conversations by category
- delete conversation
- save message
- regenerate response

### Daily Check-In

Base path: `/api/daily-checkin`

- get active questions
- get questions by category
- submit batch responses
- submit single response
- get today's check-in
- get check-in by date
- get analytics
- get alerts
- get streak
- delete a response

### Goals

Base path: `/api/goals`

- create goal
- list goals
- list active goals
- list overdue goals
- get goal by id
- update goal
- update progress
- complete goal
- delete goal

### Wellbeing

Base path: `/api/wellbeing`

- get summary
- get active alerts
- resolve alert
- get trends
- get daily recommendations

## Getting Started

### Prerequisites

- Java 17+
- MySQL 8+
- Maven or Maven Wrapper
- Gemini API key

### 1. Clone the project

```bash
git clone <your-repository-url>
cd Life-Mentor
```

### 2. Configure secrets

Create a local `.env.properties` file in the project root:

```properties
GEMINI_API_KEY=your_gemini_api_key
GEMINI_MODEL=gemini-2.5-flash
```

The application imports that file through:

```properties
spring.config.import=optional:file:.env.properties
```

### 3. Configure database and mail

Update [application.properties](/c:/Users/Jenitha/Desktop/Life-Mentor/src/main/resources/application.properties) with your local:

- MySQL connection settings
- mail username and password
- frontend URL if needed

Important:
Do not commit real secrets to source control. Use local secret files or environment variables.

### 4. Run the application

Using Maven Wrapper:

```bash
mvnw spring-boot:run
```

Or:

```bash
mvn spring-boot:run
```

The service starts on:

```text
http://localhost:8080
```

## Configuration

Main runtime configuration lives in [application.properties](/c:/Users/Jenitha/Desktop/Life-Mentor/src/main/resources/application.properties).

Notable settings include:

- server port
- datasource URL, username, password
- JWT secret and expiry
- mail server credentials
- Gemini API URL, key, model, token limit, temperature
- file upload directories
- reminder scheduling flags
- wellbeing thresholds
- goal reminder settings

## Background Jobs

Scheduled jobs are enabled and currently include:

- daily check-in reminders at 8:00 PM
- weekly wellbeing summary placeholder
- wellbeing alert check placeholder

Reminder implementation lives in [ReminderScheduler.java](/c:/Users/Jenitha/Desktop/Life-Mentor/src/main/java/com/lifementor/entity/ReminderScheduler.java).

## Seeded Daily Check-In Questions

When the database has no daily check-in questions, the backend seeds defaults such as:

- Did you have your meals on time today?
- Did you drink enough water today?
- How many hours did you sleep last night?
- How stressed do you feel today?
- How is your day going overall?
- Did you do any physical activity today?
- Did you feel productive today?
- Did you connect with someone important to you today?

Seeder implementation:
[DailyCheckinQuestionSeeder.java](/c:/Users/Jenitha/Desktop/Life-Mentor/src/main/java/com/lifementor/config/DailyCheckinQuestionSeeder.java)

## Security Notes

- JWT is used for authenticated APIs
- file upload validation restricts uploads to image content
- passwords are hashed before persistence
- account lock logic protects against repeated failed logins
- password reset tokens expire

## Project Structure

```text
src/main/java/com/lifementor
├── config
├── controller
├── dto
├── entity
├── exception
├── filter
├── repository
├── service
├── util
└── LifeMentorApplication.java
```

## Limitations and Next Improvements

Areas that can still be extended:

- in-app notification center instead of email-only reminders
- push notifications
- full weekly wellbeing summary delivery
- richer alert automation
- admin management for check-in question templates
- stronger secret management via environment-specific config

## Disclaimer

Life Mentor is a wellbeing support platform and not a medical or diagnostic system. AI outputs and recommendations are designed to be supportive, non-clinical, and should not replace professional medical or mental health advice.
