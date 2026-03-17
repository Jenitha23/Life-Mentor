package com.lifementor.util;

import com.lifementor.entity.LifestyleAssessment;
import org.springframework.stereotype.Component;

@Component
public class AIPromptBuilder {

    public String buildWellbeingCoachPrompt(String category) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are Life Mentor, a compassionate wellbeing chatbot. ");
        prompt.append("Answer the user's question clearly and naturally, then give helpful lifestyle tips when useful. ");
        prompt.append("Guidelines:\n");
        prompt.append("1. Be empathetic, supportive, and non-judgmental\n");
        prompt.append("2. Give practical tips based on what the user asks\n");
        prompt.append("3. Do not give medical diagnoses or treatment advice\n");
        prompt.append("4. If the user seems distressed, gently encourage support from a trusted person or qualified professional\n");
        prompt.append("5. Keep replies conversational and useful\n");
        prompt.append("6. Focus on habits, routines, motivation, reflection, and self-care\n");
        prompt.append("7. When useful, end with a short action plan or 2-3 simple tips\n\n");

        if (category != null) {
            switch (category.toUpperCase()) {
                case "SLEEP":
                    prompt.append("Focus on sleep hygiene, better bedtime habits, and calming routines.\n");
                    break;
                case "EXERCISE":
                    prompt.append("Focus on sustainable movement, motivation, and beginner-friendly activity.\n");
                    break;
                case "NUTRITION":
                    prompt.append("Focus on balanced meals, hydration, and small food improvements.\n");
                    break;
                case "STRESS":
                    prompt.append("Focus on grounding, stress relief, recovery, and healthy coping strategies.\n");
                    break;
                case "MOTIVATION":
                    prompt.append("Focus on consistency, momentum, and celebrating small wins.\n");
                    break;
                case "MOOD":
                    prompt.append("Focus on emotional wellbeing, self-kindness, and mood-supporting habits.\n");
                    break;
                default:
                    prompt.append("Provide general wellbeing support and encouragement.\n");
            }
        }

        return prompt.toString();
    }

    public String buildAssessmentFeedbackSystemPrompt() {
        return """
                You are Life Mentor, a supportive lifestyle and wellbeing coach.
                Analyze the provided assessment and produce non-medical supportive guidance.
                Return valid JSON only using this exact shape:
                {
                  "summary": "short paragraph",
                  "positiveHighlights": "short paragraph or bullet-style text",
                  "suggestions": "2-4 practical tips in plain text",
                  "motivationalMessage": "short encouraging message",
                  "riskLevel": "LOW|MEDIUM|HIGH"
                }

                Rules:
                - Be warm, encouraging, and non-judgmental
                - Do not diagnose or use clinical language
                - Base advice only on the provided assessment data
                - Keep suggestions realistic and easy to follow
                - If mood is low, acknowledge it gently and encourage healthy support-seeking
                - Do not include markdown fences
                """;
    }

    public String buildAssessmentFeedbackUserPrompt(LifestyleAssessment assessment) {
        long sleepHours = calculateSleepDuration(assessment);

        StringBuilder prompt = new StringBuilder();
        prompt.append("Create personalised feedback for this lifestyle assessment.\n");
        prompt.append("Sleep duration hours: ").append(sleepHours).append('\n');
        prompt.append("Meals per day: ").append(assessment.getMealsPerDay()).append('\n');
        prompt.append("Exercise frequency: ").append(assessment.getExerciseFrequency()).append('\n');
        prompt.append("Study/work hours: ").append(assessment.getStudyWorkHours()).append('\n');
        prompt.append("Screen time hours: ").append(assessment.getScreenTimeHours()).append('\n');
        prompt.append("Mood level (1-5): ").append(assessment.getMoodLevel()).append('\n');
        prompt.append("Mental wellbeing note: ")
                .append(assessment.getMentalWellbeingNote() == null || assessment.getMentalWellbeingNote().isBlank()
                        ? "No additional note provided"
                        : assessment.getMentalWellbeingNote())
                .append('\n');
        return prompt.toString();
    }

    public String getFallbackResponse(String category) {
        return "I'm having trouble reaching the AI service right now. Please try again in a moment, and ask your question again so I can respond with wellbeing guidance and practical tips.";
    }

    private long calculateSleepDuration(LifestyleAssessment assessment) {
        if (assessment.getSleepTime() == null || assessment.getWakeUpTime() == null) {
            return 0;
        }

        long hours = java.time.Duration.between(assessment.getSleepTime(), assessment.getWakeUpTime()).toHours();
        if (hours < 0) {
            hours += 24;
        }
        return hours;
    }
}
