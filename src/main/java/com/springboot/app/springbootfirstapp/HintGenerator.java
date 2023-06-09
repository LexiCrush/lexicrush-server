package com.springboot.app.springbootfirstapp;

import java.util.Locale;
import java.util.Random;

public class HintGenerator {

    public static String formatHint(String input, int n) {
        // start with a string of len(input) underscores
        String hint = "";
        for (int i = 0; i < input.length(); i++) {
            hint += "_";
        }
        // replace n random underscores with the correct letter
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            int randIndex = rand.nextInt(input.length());
            hint = hint.substring(0, randIndex) + input.charAt(randIndex) + hint.substring(randIndex + 1);
        }
        return hint;
    }

    public static String makeHint(String question) throws Exception {

        String[] parsedQuestion = QuestionUtil.parseQuestion(question); // parseQuestion
        String questType = parsedQuestion[0];
        String possibleAnswer = QuestGenerator.botAnswer(question);
        possibleAnswer = possibleAnswer.toLowerCase(Locale.ENGLISH);
        if (questType.equals(QuestionUtil.QUEST_TYPE_ANY)) { // if the question type is any, apply the formatHint method
            String hint = "";
            String[] words = possibleAnswer.split(" ");
            for (String word : words) {
                hint += formatHint(word, 10) + " ";
            }
            String final_hint = hint;
            return final_hint.trim();
        }

        if (questType.equals(QuestionUtil.QUEST_TYPE_BEGIN)) {
            String tempStr = possibleAnswer.substring(1, possibleAnswer.length()); // remove the first char
            String[] words = tempStr.split(" ");
            String hint = "";
            for (String word : words) {
                hint += formatHint(word, 10) + " ";
            }
            // add the first char from the possibleAnswer and tempStr
            String final_hint = possibleAnswer.charAt(0) + hint;
            return final_hint.trim();
        }

        if (questType.equals(QuestionUtil.QUEST_TYPE_END)) {
            String tempStr = possibleAnswer.substring(0, possibleAnswer.length() - 1); // remove the last char
            String[] words = tempStr.split(" ");
            String hint = "";
            for (String word : words) {
                hint += formatHint(word, 10) + " ";
            }
            // if hint has a space at the end, remove it
            if (hint.charAt(hint.length() - 1) == ' ') {
                hint = hint.substring(0, hint.length() - 1);
            }
            String final_hint = hint + possibleAnswer.charAt(possibleAnswer.length() - 1);
            return final_hint.trim();
        }

        return null;
    }
}
