-- =====================================================
-- V3 Migration: Fix users.date_of_birth column type
-- Purpose:
-- Hibernate expects date_of_birth as VARCHAR/NVARCHAR
-- because User.java uses String dateOfBirth.
-- Existing table has DATE, so this migration converts it.
-- =====================================================

IF EXISTS (
    SELECT 1
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'users'
      AND COLUMN_NAME = 'date_of_birth'
)
BEGIN
    ALTER TABLE users
    ALTER COLUMN date_of_birth NVARCHAR(255) NULL;
END
GO