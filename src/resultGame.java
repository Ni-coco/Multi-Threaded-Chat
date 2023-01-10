import java.util.Random;

public class resultGame {

    static public String getWin(String winner, String looser, String game) {
        String[] win = new String[15];
        win[0] = winner + " outplayed " + looser + " and secured a victory in a round of " + game + " game.";
        win[1] = winner + " dominated " + looser + " with some impressive move in " + game + " game.";
        win[2] = looser + " couldn't keep up with " + winner + "superior strategy and lost a match of " + game + " game."; 
        win[3] = winner + " crushed " + looser + " hopes of victory with a flawless performance in the " + game + " game.";
        win[4] = looser + " had no chance against " + winner + " superior skills in the " + game + " game.";
        win[5] = winner + " bested " + looser + " with some impressive moves and tactics in the " + game + " game.";
        win[6] = looser + " was no match for " + winner + " quick thinking and strategy in the " + game + " game.";
        win[7] = winner + " proved to be a better player than " + looser  + " and came out on top in the latest match of " + game + " game .";
        win[8] = looser + " was left in the dust as " + winner + " dominated the " + game + " game.";
        win[9] =  winner  + " easily defeated " + looser + " with their superior gameplay in the " + game + " game.";
        win[10] = looser + " couldn't keep up with " + winner + " lightning-fast reflexes and lost the match of " + game + " game.";
        win[11] = winner + " emerged victorious after a tough battle against " + looser + " in the " + game + " game.";
        win[12] = looser + " didn't stand a chance against " + winner + " exceptional skills in the " + game + " game.";
        win[13] = winner + " showed no mercy as they defeated " + looser + " in the latest round of " + game + " game.";
        win[14] = winner + " smashed " + looser + " in the " + game + " game.";
        return win[new Random().nextInt(15)];
    }
}
