CREATE TABLE users (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    email NVARCHAR(255) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    reset_token NVARCHAR(255),
    reset_token_expiry DATETIME2,
    email_verified BIT NOT NULL DEFAULT 0,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    account_locked BIT NOT NULL DEFAULT 0,
    lock_until DATETIME2,
    phone_number NVARCHAR(20),
    bio NVARCHAR(500),
    date_of_birth DATE,
    gender NVARCHAR(20),
    profile_picture_url NVARCHAR(500),
    created_at DATETIME2,
    updated_at DATETIME2,
    last_login DATETIME2
);

CREATE TABLE lifestyle_assessment (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    user_id UNIQUEIDENTIFIER NOT NULL UNIQUE,
    sleep_time TIME,
    wake_up_time TIME,
    meals_per_day INT,
    exercise_frequency NVARCHAR(10),
    study_work_hours DECIMAL(4,1),
    screen_time_hours DECIMAL(4,1),
    mood_level INT,
    mental_wellbeing_note NVARCHAR(MAX),
    created_at DATETIME2,
    updated_at DATETIME2,

    CONSTRAINT fk_lifestyle_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE ai_feedback (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    assessment_id UNIQUEIDENTIFIER NOT NULL UNIQUE,
    summary NVARCHAR(MAX) NOT NULL,
    positive_highlights NVARCHAR(MAX),
    suggestions NVARCHAR(MAX) NOT NULL,
    motivational_message NVARCHAR(MAX),
    ai_model_version NVARCHAR(255),
    disclaimer_shown BIT NOT NULL DEFAULT 1,
    risk_level NVARCHAR(255),
    created_at DATETIME2,
    updated_at DATETIME2,

    CONSTRAINT fk_ai_feedback_assessment
        FOREIGN KEY (assessment_id) REFERENCES lifestyle_assessment(id)
        ON DELETE CASCADE
);

CREATE TABLE ai_chat_conversations (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    user_id UNIQUEIDENTIFIER NOT NULL,
    title NVARCHAR(255),
    category NVARCHAR(50),
    is_active BIT NOT NULL DEFAULT 1,
    created_at DATETIME2,
    updated_at DATETIME2,

    CONSTRAINT fk_ai_chat_conversation_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE ai_chat_messages (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    conversation_id UNIQUEIDENTIFIER NOT NULL,
    role NVARCHAR(20) NOT NULL,
    content NVARCHAR(MAX) NOT NULL,
    is_saved BIT NOT NULL DEFAULT 0,
    tokens_used INT,
    ai_model_used NVARCHAR(50),
    metadata NVARCHAR(MAX),
    created_at DATETIME2,

    CONSTRAINT fk_ai_chat_message_conversation
        FOREIGN KEY (conversation_id) REFERENCES ai_chat_conversations(id)
        ON DELETE CASCADE
);

CREATE TABLE daily_checkin_questions (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    question NVARCHAR(MAX) NOT NULL,
    question_type NVARCHAR(50),
    category NVARCHAR(50),
    options NVARCHAR(MAX),
    is_active BIT NOT NULL DEFAULT 1,
    display_order INT DEFAULT 0,
    created_at DATETIME2
);

CREATE TABLE user_checkin_responses (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    user_id UNIQUEIDENTIFIER NOT NULL,
    question_id UNIQUEIDENTIFIER NOT NULL,
    answer NVARCHAR(MAX) NOT NULL,
    response_date DATE NOT NULL,
    metadata NVARCHAR(MAX),
    created_at DATETIME2,

    CONSTRAINT fk_checkin_response_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_checkin_response_question
        FOREIGN KEY (question_id) REFERENCES daily_checkin_questions(id),

    CONSTRAINT uq_user_question_response_date
        UNIQUE (user_id, question_id, response_date)
);

CREATE TABLE user_goals (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    user_id UNIQUEIDENTIFIER NOT NULL,
    goal_type NVARCHAR(50) NOT NULL,
    target_value DECIMAL(10,2),
    current_value DECIMAL(10,2),
    target_date DATE,
    start_date DATE NOT NULL,
    status NVARCHAR(20),
    progress_percentage INT,
    description NVARCHAR(500),
    notes NVARCHAR(MAX),
    created_at DATETIME2,
    updated_at DATETIME2,

    CONSTRAINT fk_user_goal_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE wellbeing_alerts (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    user_id UNIQUEIDENTIFIER NOT NULL,
    level NVARCHAR(20) NOT NULL,
    message NVARCHAR(MAX) NOT NULL,
    suggested_action NVARCHAR(MAX),
    is_resolved BIT NOT NULL DEFAULT 0,
    resolved_at DATETIME2,
    alert_category NVARCHAR(50),
    alert_data NVARCHAR(MAX),
    created_at DATETIME2,
    updated_at DATETIME2,

    CONSTRAINT fk_wellbeing_alert_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE notifications (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    user_id UNIQUEIDENTIFIER NOT NULL,
    type NVARCHAR(50) NOT NULL,
    title NVARCHAR(150) NOT NULL,
    message NVARCHAR(MAX) NOT NULL,
    action_url NVARCHAR(255),
    is_read BIT NOT NULL DEFAULT 0,
    read_at DATETIME2,
    created_at DATETIME2,
    updated_at DATETIME2,

    CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);