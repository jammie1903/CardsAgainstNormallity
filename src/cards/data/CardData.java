package cards.data;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class CardData {
    private Queue<String> questionDeck = new ArrayDeque<>();
    private Queue<String> answerDeck = new ArrayDeque<>();

    private static CardData instance = new CardData();

    private CardData() {
    }

    public static void loadQuestionDeck(List<String> questions) {
        if (questions != null) {
            questions.forEach((question) -> {
                if (!instance.questionDeck.contains(question)) {
                    instance.questionDeck.add(question);
                }
            });
        }
    }

    public static void loadAnswerDeck(List<String> answers) {
        if (answers != null) {
            answers.forEach((answer) -> {
                if (!instance.answerDeck.contains(answer)) {
                    instance.answerDeck.add(answer);
                }
            });
        }
    }

    public static String drawQuestionCard() {
        String question = instance.questionDeck.remove();
        instance.questionDeck.offer(question);
        return question;
    }

    public static String drawAnswerCard() {
        String answer = instance.answerDeck.remove();
        instance.answerDeck.offer(answer);
        return answer;
    }

    public static void returnCardToQuestionDeck(String question) {
        instance.questionDeck.add(question);
    }

    public static void returnCardToAnswerDeck(String answer) {
        instance.answerDeck.add(answer);
    }
}
