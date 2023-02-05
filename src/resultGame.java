import java.util.Random;

public class resultGame {

    static public String getWin(String winner, String looser, String game) {
        String[] str = new String[9];
        str[0] = winner + " outplayed " + looser + " and secured a victory in a round of " + game + " game.";
        str[1] = winner + " dominated " + looser + " with some impressive move in " + game + " game.";
        str[2] = winner + " crushed " + looser + " hopes of victory with a flawless performance in the " + game + " game.";
        str[3] = winner + " bested " + looser + " with some impressive moves and tactics in the " + game + " game.";
        str[4] = winner + " proved to be a better player than " + looser  + " and came out on top in the latest match of " + game + " game .";
        str[5] =  winner  + " easily defeated " + looser + " with their superior gameplay in the " + game + " game.";
        str[6] = winner + " emerged victorious after a tough battle against " + looser + " in the " + game + " game.";
        str[7] = winner + " showed no mercy as they defeated " + looser + " in the latest round of " + game + " game.";
        str[8] = winner + " smashed " + looser + " in the " + game + " game.";
        return str[new Random().nextInt(9)];
    }

    static public String getDraw(String p1, String p2, String game) {
        String[] str = new String[10];
        str[0] = p1 + " and " + p2 + " " + game + " game was so intense, the X and O formed their own country and declared a draw to prevent further conflict.";
        str[1] = p1 + " and " + p2 + " " + game + " game was so close, even the Tic Tac Toe gods couldn't decide a winner.";
        str[2] = p1 + " and " + p2 + " " + game + " game was so boring, even the board fell asleep.";
        str[3] = p1 + " and " + p2 + " " + game + " game was so intense, they both needed a nap after.";
        str[4] = p1 + " and " + p2 + " " + game + " game was so bad, it ended in a draw.";
        str[5] = p1 + " and " + p2 + " " + game + " game was so intense, they had to play again to determine the winner.";
        str[6] = p1 + " and " + p2 + " " + game + " game was so close, the tension was palpable.";
        str[7] = p1 + " and " + p2 + " " + game + " game was a showcase of incredible strategy and precision, resulting in a draw.";
        str[8] = p1 + " and " + p2 + " " + game + " game was a true test of endurance, both players were mentally and physically exhausted at the end of it, resulting in a draw";
        str[9] = p1 + " and " + p2 + " " + game + " game ended in a draw, a true showcase of skill.";
        return str[new Random().nextInt(10)];
    }

    static public String getFlap(String user, String record, String pastop) {
        String[] str = new String[10];
        if (user.equals(pastop))
            return user + " set a new record of " + record;
        str[0] = user + " beat the record of " + pastop + " and set the record to " + record + ".";
        return str[new Random().nextInt(1)];
    }
}
