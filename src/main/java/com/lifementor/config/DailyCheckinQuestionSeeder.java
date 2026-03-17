package com.lifementor.config;

import com.lifementor.entity.DailyCheckinQuestion;
import com.lifementor.repository.DailyCheckinQuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DailyCheckinQuestionSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DailyCheckinQuestionSeeder.class);

    private final DailyCheckinQuestionRepository questionRepository;

    public DailyCheckinQuestionSeeder(DailyCheckinQuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public void run(String... args) {
        if (questionRepository.count() > 0) {
            return;
        }

        List<DailyCheckinQuestion> questions = List.of(
                buildQuestion("Did you have your meals on time today?", "YES_NO", "NUTRITION", null, 1),
                buildQuestion("Did you drink enough water today?", "YES_NO", "NUTRITION", null, 2),
                buildQuestion("How many hours did you sleep last night?", "SCALE", "SLEEP", "{\"min\":1,\"max\":12}", 3),
                buildQuestion("How stressed do you feel today?", "SCALE", "STRESS", "{\"min\":1,\"max\":5}", 4),
                buildQuestion("How is your day going overall?", "SCALE", "MOOD", "{\"min\":1,\"max\":5}", 5),
                buildQuestion("Did you do any physical activity today?", "YES_NO", "EXERCISE", null, 6),
                buildQuestion("Did you feel productive today?", "YES_NO", "PRODUCTIVITY", null, 7),
                buildQuestion("Did you connect with someone important to you today?", "YES_NO", "SOCIAL", null, 8)
        );

        questionRepository.saveAll(questions);
        log.info("Seeded {} default daily check-in questions", questions.size());
    }

    private DailyCheckinQuestion buildQuestion(String questionText, String type, String category,
                                               String options, int displayOrder) {
        DailyCheckinQuestion question = new DailyCheckinQuestion();
        question.setQuestion(questionText);
        question.setQuestionType(type);
        question.setCategory(category);
        question.setOptions(options);
        question.setActive(true);
        question.setDisplayOrder(displayOrder);
        return question;
    }
}
