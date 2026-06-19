INSERT INTO daily_checkin_questions 
(id, question, question_type, category, options, is_active, display_order, created_at)
VALUES
(NEWID(), 'How was your mood today?', 'SCALE', 'MOOD', '[1,2,3,4,5]', 1, 1, SYSDATETIME()),

(NEWID(), 'How many hours did you sleep last night?', 'SCALE', 'SLEEP', '[1,2,3,4,5,6,7,8,9,10]', 1, 2, SYSDATETIME()),

(NEWID(), 'Did you exercise today?', 'YES_NO', 'EXERCISE', '["YES","NO"]', 1, 3, SYSDATETIME()),

(NEWID(), 'How many meals did you have today?', 'SCALE', 'NUTRITION', '[1,2,3,4,5]', 1, 4, SYSDATETIME()),

(NEWID(), 'How productive were you today?', 'SCALE', 'PRODUCTIVITY', '[1,2,3,4,5]', 1, 5, SYSDATETIME()),

(NEWID(), 'Write one thing you are grateful for today.', 'TEXT', 'GENERAL', NULL, 1, 6, SYSDATETIME());