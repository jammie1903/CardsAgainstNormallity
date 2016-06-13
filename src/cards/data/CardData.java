package cards.data;

import java.util.*;

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
            instance.questionDeck = shuffle(instance.questionDeck);
        }
    }

    private static <T> Queue<T> shuffle(Queue<T> queue) {
        List<T> tempList = new ArrayList<>(queue);
        Collections.shuffle(tempList);
        return new ArrayDeque<>(tempList);
    }

    public static void loadAnswerDeck(List<String> answers) {
        if (answers != null) {
            answers.forEach((answer) -> {
                if (!instance.answerDeck.contains(answer)) {
                    instance.answerDeck.add(answer);
                }
            });
            instance.answerDeck = shuffle(instance.answerDeck);
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
}
