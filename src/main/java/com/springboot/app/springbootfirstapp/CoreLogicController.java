package com.springboot.app.springbootfirstapp;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.app.springbootfirstapp.user.UserService;
import com.springboot.app.springbootfirstapp.user.UserStatService;

@CrossOrigin(origins = "*", allowedHeaders = "*") // cross origin resource sharing will allow requests from any origin
@RestController

@RequestMapping("/api")
public class CoreLogicController {

    @GetMapping("/getq") // returns a random question
    public String randq() throws Exception {
        String question = QuestGenerator.getRandomQuestion();
        System.out.println("GET Question: " + question); // TODO remove
        return question;
    }

    @GetMapping("/bot") // returns the bot's answer to a question
    public String botscore(@RequestParam String question) throws Exception {
        String botAnswer = QuestGenerator.botAnswer(question);
        System.out.println("Bot Answer: " + botAnswer); // TODO remove
        return botAnswer;
    }

    @GetMapping("/useHint") // returns a hint as a string
    public String hint(@RequestParam String question, @RequestHeader("Access-Token") String accessToken)
            throws Exception {
        String username = UserService.parseAccessToken(accessToken); // subtract hint from user's hint count
        int hintCount = UserStatService.getScoreByType(username, "available_hints"); // get hint countß
        if (hintCount <= 0) { // check if hint count is greater than 0
            return "You have no hints left!";
        }
        UserStatService.updateScoreByType(username, hintCount - 1, "available_hints"); // decrement hint count
        // generate hint
        String hint = HintGenerator.makeHint(question);
        System.out.println("Hint: " + hint); // TODO remove
        // add a space between each character
        hint = hint.replaceAll(".", "$0 ");
        return hint;
    }

    @GetMapping("/getHintCount") // returns available hint count
    public int getHintCount(@RequestHeader("Access-Token") String accessToken) throws Exception {
        String username = UserService.parseAccessToken(accessToken);
        int hintCount = UserStatService.getScoreByType(username, "available_hints");
        System.out.println("GET Hint Count: " + hintCount); // TODO remove
        return hintCount;
    }

    @GetMapping("/getCoinCount") // returns available coins
    public int getCoinCount(@RequestHeader("Access-Token") String accessToken) throws Exception {
        String username = UserService.parseAccessToken(accessToken);
        int hintCount = UserStatService.getScoreByType(username, "coins");
        System.out.println("GET Coin Count: " + hintCount); // TODO remove
        return hintCount;
    }

    @GetMapping("/getCurrentScore") // returns the user's score
    public int getCurrentScore(@RequestHeader("Access-Token") String accessToken) throws Exception {
        String username = UserService.parseAccessToken(accessToken);
        int score = UserStatService.getScoreByType(username, "current_score");
        System.out.println("GET Current Score: " + score); // TODO remove
        return score;
    }

    @GetMapping("/getHighScore") // returns the user's score
    public int getHighScore(@RequestHeader("Access-Token") String accessToken) throws Exception {
        String username = UserService.parseAccessToken(accessToken);
        int score = UserStatService.getScoreByType(username, "high_score");
        System.out.println("GET High Score: " + score); // TODO remove
        return score;
    }

    @GetMapping("/getNumGames") // returns the number of games played
    public int numGamesPlayed(@RequestHeader("Access-Token") String accessToken) throws Exception {
        String username = UserService.parseAccessToken(accessToken);
        int numGamesPlayed = UserStatService.getScoreByType(username, "games_played");
        System.out.println("GET Number of Games Played: " + numGamesPlayed); // TODO remove
        return numGamesPlayed;
    }

    @GetMapping("/getHighScoreLeaderboard") // returns the high score leaderboard as a 2d array
    public String[][] getHighScoreLeaderboard() throws Exception {
        String[][] leaderboard = UserStatService.getHighScoreLeaderboard();
        System.out.println("GET High Score Leaderboard: " + leaderboard); // TODO remove
        return leaderboard;
    }

    @PostMapping("/checkans") // returns 0 if the answer is incorrect, and length of matched word if correct
    public int checkans(@RequestParam String question, @RequestParam String answer) throws Exception {
        System.out.println(QuestGenerator.checkAnswer(question, answer)); // TODO remove
        return QuestGenerator.checkAnswer(question, answer);
    }

    @PostMapping("/updateCurrentScore")
    public String results(@RequestHeader("Access-Token") String accessToken, @RequestParam String playerAnswer,
            @RequestParam String botAnswer, @RequestParam String question) throws Exception {

        if (UserService.parseAccessToken(accessToken) == null) {
            return "Server Error, sorry for the inconvenience! Please try logging in again or make sure your browsers time is up to date.";
            // access token is invalid due to an unforeseen error

        } else {

            if (playerAnswer.equals("")) {
                return "Please enter an answer... ";
            }

            int score = QuestGenerator.checkAnswer(question, playerAnswer);

            if (score < 1) {
                return "Invalid answer. ";
            }

            String username = UserService.parseAccessToken(accessToken);

            // compare lengths of playerAnswer and botAnswer in LONGEST MODE
            int pal = score; // checkAnswer returns matchedWord.replaceAll("\\s+", "").length(); or 0
            int bal = botAnswer.replaceAll("\\s+", "").length();

            if (pal > bal) {

                int currentScore = UserStatService.getScoreByType(username, "current_score");
                int balPalDiff = pal - bal;
                int updatedScore = currentScore + balPalDiff;
                UserStatService.updateScoreByType(username, updatedScore, "current_score");

                return ("You WIN! Your answer was " + balPalDiff + " letters longer than the bot's answer! ");
            } else if (pal < bal) {
                int balPalDiff = bal - pal;
                return "You LOSE! The bot's answer was " + balPalDiff + " letters longer than your answer! ";
            } else {
                return "Its a tie... Both answers were " + pal + " letters long! ";
            }
        }
    }

    @PostMapping("/endGame")
    public String endGame(@RequestHeader("Access-Token") String accessToken) throws Exception {
        if (UserService.parseAccessToken(accessToken) == null) {
            return "Invalid access token";
        } else {
            String username = UserService.parseAccessToken(accessToken);
            int currentScore = UserStatService.getScoreByType(username, "current_score");
            int highScore = UserStatService.getScoreByType(username, "high_score");
            UserStatService.updateScoreByType(username, 0, "current_score");
            if (currentScore > highScore) {
                UserStatService.updateScoreByType(username, currentScore, "high_score");
                return ("New high score: " + currentScore + " points!");
            }
            UserStatService.updateScoreByType(username, 0, "current_score");
            return "You scored " + currentScore + " points!";
        }
    }

    @PostMapping("/buyHint")
    public void buyHint(@RequestHeader("Access-Token") String accessToken) throws Exception {
        String username = UserService.parseAccessToken(accessToken);
        int hintCount = UserStatService.getScoreByType(username, "available_hints");
        int coins = UserStatService.getScoreByType(username, "coins");
        if (coins >= 10) {
            UserStatService.updateScoreByType(username, hintCount + 1, "available_hints");
            UserStatService.updateScoreByType(username, coins - 10, "coins");
        }
    }

}
