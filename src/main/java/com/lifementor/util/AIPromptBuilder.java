package com.lifementor.util;

import org.springframework.stereotype.Component;

@Component
public class AIPromptBuilder {

    public String buildWellbeingCoachPrompt(String category) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are a compassionate, supportive wellbeing coach. ");
        prompt.append("Your role is to help users improve their mental and physical wellbeing. ");
        prompt.append("Guidelines:\n");
        prompt.append("1. Always be empathetic and non-judgmental\n");
        prompt.append("2. Provide practical, actionable advice\n");
        prompt.append("3. Never give medical advice or diagnoses\n");
        prompt.append("4. Encourage professional help when appropriate\n");
        prompt.append("5. Keep responses positive and motivating\n");
        prompt.append("6. Focus on small, achievable steps\n");
        prompt.append("7. Acknowledge the user's feelings and experiences\n\n");

        if (category != null) {
            switch (category.toUpperCase()) {
                case "SLEEP":
                    prompt.append("Focus on sleep hygiene, relaxation techniques, and improving sleep quality. ");
                    prompt.append("Suggest consistent sleep schedules and calming bedtime routines.\n");
                    break;
                case "EXERCISE":
                    prompt.append("Focus on physical activity, exercise motivation, and overcoming barriers. ");
                    prompt.append("Suggest fun, accessible ways to stay active.\n");
                    break;
                case "NUTRITION":
                    prompt.append("Focus on healthy eating habits, mindful eating, and balanced nutrition. ");
                    prompt.append("Suggest small dietary improvements.\n");
                    break;
                case "STRESS":
                    prompt.append("Focus on stress management, relaxation, and coping strategies. ");
                    prompt.append("Suggest mindfulness, breathing exercises, and self-care.\n");
                    break;
                case "MOTIVATION":
                    prompt.append("Focus on building motivation, setting goals, and maintaining momentum. ");
                    prompt.append("Celebrate small wins and encourage consistency.\n");
                    break;
                default:
                    prompt.append("Provide general wellbeing support and encouragement.\n");
            }
        }

        return prompt.toString();
    }

    public String getFallbackResponse(String category) {
        if ("SLEEP".equalsIgnoreCase(category)) {
            return "I understand that sleep can be challenging sometimes. Have you tried establishing a consistent bedtime routine? Even 15 minutes of relaxation before bed can make a difference. Remember, small steps lead to better rest over time.";
        } else if ("EXERCISE".equalsIgnoreCase(category)) {
            return "It's great that you're thinking about physical activity! Remember, any movement counts - even a short walk around the block. What's one small activity you could try today?";
        } else if ("NUTRITION".equalsIgnoreCase(category)) {
            return "Making healthy food choices is a journey. Try adding one extra serving of vegetables to your meals today. Small changes add up!";
        } else if ("STRESS".equalsIgnoreCase(category)) {
            return "It's completely normal to feel stressed sometimes. Take a deep breath with me: breathe in for 4 counts, hold for 4, and out for 4. How do you feel after that?";
        } else if ("MOTIVATION".equalsIgnoreCase(category)) {
            return "Every journey begins with a single step. What's one small thing you can do today to move toward your goal? I believe in you!";
        } else {
            return "Thank you for sharing. Remember to be kind to yourself today. Is there something specific you'd like to talk about regarding your wellbeing?";
        }
    }
}