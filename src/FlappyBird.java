import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.*;
import java.awt.*;
import java.awt.Graphics;
import java.util.List;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
//import javax.swing.border.LineBorder;

public class FlappyBird implements KeyListener {

    /* set Variable */
    private JFrame win;
    static JLabel uipressenter = new JLabel(new ImageIcon(new ImageIcon(FlappyBird.class.getClassLoader().getResource("sprite/flappybird/ui/toplay.png")).getImage().getScaledInstance(400, 100, Image.SCALE_SMOOTH)));
    static JLabel uipause = new JLabel(new ImageIcon(new ImageIcon(FlappyBird.class.getClassLoader().getResource("sprite/flappybird/ui/pause.png")).getImage().getScaledInstance(250, 60, Image.SCALE_SMOOTH)));
    static JLabel youDie = new JLabel(new ImageIcon(new ImageIcon(FlappyBird.class.getClassLoader().getResource("sprite/flappybird/ui/youdie.png")).getImage().getScaledInstance(240, 50, Image.SCALE_SMOOTH)));
    static ImageIcon yourscore = new ImageIcon((new ImageIcon(FlappyBird.class.getClassLoader().getResource("sprite/flappybird/ui/yourscore.png")).getImage().getScaledInstance(480, 60, Image.SCALE_SMOOTH)));
    static ImageIcon thebest = new ImageIcon((new ImageIcon(FlappyBird.class.getClassLoader().getResource("sprite/flappybird/ui/thebest.png")).getImage().getScaledInstance(300, 30, Image.SCALE_SMOOTH)));
    static ImageIcon replayquit = new ImageIcon((new ImageIcon(FlappyBird.class.getClassLoader().getResource("sprite/flappybird/ui/replayquit.png")).getImage().getScaledInstance(600, 30, Image.SCALE_SMOOTH)));
    static ImageIcon deadbird = new ImageIcon((new ImageIcon(FlappyBird.class.getClassLoader().getResource("sprite/flappybird/player/bluebirddead.png")).getImage().getScaledInstance(90, 60, Image.SCALE_SMOOTH)));
    static ImageIcon[] spritePlayer = new ImageIcon[6];
    static List<ImageIcon> spriteEnemy = new ArrayList<ImageIcon>();
    static ImageIcon[] spriteScore = new ImageIcon[10];
    static JLabel player = new JLabel();
    static BackgroundPanel background = null;
    static JPanel[] pn = new JPanel[2];
    static JButton btn = new JButton("Exit");
    static String data = "";
    static String username;
    static int pause = 1;
    static double vertical_velocity = 0;
    static double prev_vel = 0;
    static double gravity = 0.1;
    static List<JLabel> listEnemy = new ArrayList<JLabel>();
    static JLabel score = new JLabel();
    static double scoring = 0;
    static int widthwin;
    static double speedEnemy = 2;
    static int spawnEnemy = 4;
    static Thread gameThread;
    static Thread spritePThread;
    static Thread spriteEThread;
    static Thread enemyThread;
    static Thread hitThread;
    static List<Rectangle> enemyRec = new ArrayList<Rectangle>();
    static int dead = 0;
    static int finalscore = 0;
    static JPanel finalpanel = new JPanel();
    static int bestScore;
    static int flap = 1;
    static String versus;

    static public void resetAll() {
        data = "";
        pause = 1;
        vertical_velocity = 0;
        prev_vel = 0;
        gravity = 0.1;
        scoring = 0;
        spawnEnemy = 4;
        dead = 0;
        listEnemy.clear();
        enemyRec.clear();
        finalpanel.removeAll();
        if (background != null)
            background.removeAll();
    }

    public FlappyBird (JFrame frame, String user) {
        resetAll();
        win = frame;
        username = user;
        widthwin = win.getWidth();
        background = new BackgroundPanel();
        win.add(background);
        win.setSize(1080, 720);
        win.setResizable(false);
        background.requestFocus();
        if (background.getKeyListeners().length == 0)
            background.addKeyListener(this);
        setUI(win);
        runGame(win);
        getBestScore();
    }

    static public void setUI(JFrame win) {
        for (int i = 0; i < spritePlayer.length; i++)
            spritePlayer[i] = new ImageIcon(new ImageIcon(FlappyBird.class.getClassLoader().getResource("sprite/flappybird/player/bluebird"+Integer.toString(i)+".png")).getImage().getScaledInstance(90, 60, Image.SCALE_SMOOTH));
        for (int i = 0; i < 3; i++)
            spriteEnemy.add(new ImageIcon(new ImageIcon(FlappyBird.class.getClassLoader().getResource("sprite/flappybird/enemy/"+Integer.toString(i)+".png")).getImage().getScaledInstance(100, 70, Image.SCALE_SMOOTH)));
        for (int i = 0; i < spriteScore.length; i++)
            spriteScore[i] = new ImageIcon(new ImageIcon(FlappyBird.class.getClassLoader().getResource("sprite/flappybird/score/"+Integer.toString(i)+".png")).getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
        player.setIcon(spritePlayer[0]);
        score.setIcon(spriteScore[0]);
        background.removeAll();
        background.setLayout(new BorderLayout());
        background.add(uipressenter, BorderLayout.CENTER);
    }

    static void runGame(JFrame win) {
        gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                spriteP();
                runEnemy();
                getHit();
                while (frame.getFlappy() == 1 || flap == 1) {
                    try {Thread.sleep(1);} catch (Exception e) {e.printStackTrace();}
                    while (win.isVisible() && pause == 0 && gravity == 0.1) {
                        if (scoring >= 10)
                            speedEnemy = scoring > 29 ? 5 : (scoring / 10) + 2;
                        spawnEnemy = scoring > 49 ? 2 : scoring > 19 ? 3 : 4;
                        if (Arrays.asList(background.getComponents()).contains(uipause)) {
                            background.remove(uipause);
                            background.setLayout(null);
                            background.revalidate();
                            background.repaint();
                        }
                        else if (Arrays.asList(background.getComponents()).contains(uipressenter)) {
                            background.setLayout(null);
                            background.remove(uipressenter);
                            background.add(player);
                            player.setBounds(40, 10, 100, 100);
                            background.add(score);
                            ImageIcon tmp = getScoreIcon((int)scoring, 0, 0);
                            score.setIcon(tmp);
                            score.setBounds((widthwin / 2) - (tmp.getIconWidth() / 2), 20, 100, 100);
                            score.setSize(tmp.getIconWidth(), tmp.getIconHeight());
                            background.revalidate();
                            background.repaint();
                        }
                        try {Thread.sleep(10);} catch (Exception e) {e.printStackTrace();}
                        vertical_velocity += gravity;
                        if (dead == 1 && player.getY() >= 593) {
                            gravity = 0;
                            vertical_velocity = 0;
                            for (int i = 0; i < listEnemy.size(); i++)
                                background.remove(listEnemy.get(i));
                            win.repaint();
                            win.revalidate();
                        }
                        if (player.getY() + vertical_velocity < -50)
                            vertical_velocity = 0;
                        else if (player.getY() >= 592 && dead == 0) {
                            dead = 1;
                            finalscore = (int)scoring;
                            try {Thread.sleep(10);} catch (Exception e) {e.printStackTrace();}
                            player.setIcon(deadbird);
                        }
                        player.setLocation(player.getX(), (int)(player.getY() + vertical_velocity));
                        for (int i = 0; i < listEnemy.size(); i++)
                            listEnemy.get(i).setLocation(listEnemy.get(i).getX() - (int)speedEnemy , listEnemy.get(i).getY());
                    }
                    if (!Arrays.asList(background.getComponents()).contains(uipressenter) && !Arrays.asList(background.getComponents()).contains(uipause) && dead != 1) {
                        background.setLayout(new BorderLayout());
                        background.add(uipause, BorderLayout.CENTER);
                        background.revalidate();
                        background.repaint();
                    }
                    else if (dead == 1 && !Arrays.asList(finalpanel.getComponents()).contains(youDie)) {
                        data = finalscore > bestScore ? "/doneµ" + username + "µ" + Integer.toString(finalscore) + "µ" + versus : "null";
                        setBestScore();
                        background.remove(score);
                        finalpanel.setLayout(new GridLayout(0, 1));
                        finalpanel.setOpaque(false);
                        finalpanel.add(youDie);
                        score.setIcon(getScore(yourscore, finalscore, 0));
                        score.setHorizontalAlignment(JLabel.CENTER);
                        finalpanel.add(score);
                        finalpanel.add(new JLabel(getScore(thebest, finalscore < bestScore ? bestScore : finalscore, 1)));
                        finalpanel.add(new JLabel(replayquit));
                        finalpanel.setBounds(0, 0, 1064, 680);
                        background.add(finalpanel);
                        background.repaint();
                        background.revalidate();
                        break;
                    }
                }
            }
        });
        gameThread.start();
    }

    static public void getBestScore() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                versus = GetJson.getBestScore(1);
                bestScore = Integer.parseInt(GetJson.getBestScore(0));
            }
        }).start();
    }

    static public void setBestScore() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (finalscore > bestScore)
                    GetJson.setBestScore(finalscore, username);
            }
        }).start();
    }

    static public ImageIcon getScore(ImageIcon img1, int score, int dummy) {
        ImageIcon icon1 = img1;
        ImageIcon icon2 = getBlackColor(getScoreIcon(score, 0, 0));
        int countdigit = Integer.toString(score).length();
        double divi = 0.35;
        if (dummy == 0)
            icon2 = new ImageIcon(icon2.getImage().getScaledInstance(icon2.getIconWidth(), icon1.getIconHeight(), Image.SCALE_SMOOTH));
        else {
            for (int i = 1; i < countdigit; i++)
                divi += 0.4;
            icon2 = new ImageIcon(icon2.getImage().getScaledInstance((int)(icon2.getIconHeight() * divi), icon1.getIconHeight(), Image.SCALE_SMOOTH));
        }
        int width = icon1.getIconWidth() + icon2.getIconWidth();
        int height = Math.max(icon1.getIconHeight(), icon2.getIconHeight());
        BufferedImage combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = combinedImage.createGraphics();
        icon1.paintIcon(null, g, 0, 0);
        icon2.paintIcon(null, g, icon1.getIconWidth(), 0);
        g.dispose();
        return new ImageIcon(combinedImage);
    }

    static public ImageIcon getBlackColor(ImageIcon original) {
        ImageIcon originalIcon = original;
        BufferedImage originalImage = new BufferedImage(originalIcon.getIconWidth(), originalIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = originalImage.createGraphics();
        originalIcon.paintIcon(null, g, 0, 0);
        g.dispose();
        BufferedImage blackImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < originalImage.getWidth(); x++) {
            for (int y = 0; y < originalImage.getHeight(); y++) {
                int originalPixel = originalImage.getRGB(x, y);
                int alpha = (originalPixel >> 24) & 0xff;
                int blackPixel = (alpha << 24) | (0 << 16) | (0 << 8) | 0;
                blackImage.setRGB(x, y, blackPixel);
            }
        }
        return new ImageIcon(blackImage);
    }

    static void getHit() {
        hitThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Rectangle playerRec = new Rectangle(player.getX(), player.getY() + (player.getHeight() / 3), player.getWidth(), player.getHeight() / 2);
                while (dead == 0 && frame.getFlappy() == 1) {
                    try {Thread.sleep(1);} catch (Exception e) {e.printStackTrace();}
                    for (int i = 0; i < listEnemy.size(); i++) {
                        playerRec.setRect(player.getX() + (player.getWidth() / 10), player.getY() + (player.getHeight() / 3), (int)(player.getWidth() / 1.5), (int)(player.getHeight() / 3));
                        enemyRec.get(i).setRect(listEnemy.get(i).getX() + (listEnemy.get(i).getWidth() / 10), listEnemy.get(i).getY() + (listEnemy.get(i).getHeight() / 3), (int)(listEnemy.get(i).getWidth() / 1.5), (int)(listEnemy.get(i).getHeight() / 3));
                        if (playerRec.intersects(enemyRec.get(i))) {
                            dead = 1;
                            finalscore = (int)scoring;
                            vertical_velocity = 0;
                            try {Thread.sleep(10);} catch (Exception e) {e.printStackTrace();}
                            player.setIcon(deadbird);

                        }
                    }
                }
            }
        });
        hitThread.start();
    }

    static void spriteP() {
        spritePThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (dead == 0 && frame.getFlappy() == 1) {
                    if (prev_vel != 0 && prev_vel + 3 > vertical_velocity) {
                        for (int i = 1; i < spritePlayer.length; i++) {
                            player.setIcon(spritePlayer[i]);
                            try {Thread.sleep(50);} catch (Exception e) {e.printStackTrace();}
                            if (dead == 1)
                                break;
                        }
                        prev_vel = 0;
                    }
                    else
                        player.setIcon(spritePlayer[0]);
                    }
                }
            });
            spritePThread.start();
        }

    static public void spriteE() {
        spriteEThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ImageIcon tmp;
                int j;
                while (dead == 0 && frame.getFlappy() == 1) {
                    try {Thread.sleep(1);} catch (Exception e) {e.printStackTrace();}
                    while (getPause() != 1 && player.getY() <= 593) {
                        for (int i = 0; i < listEnemy.size(); i++) {
                            j = spriteEnemy.indexOf(listEnemy.get(i).getIcon());
                            listEnemy.get(i).setIcon(spriteEnemy.get(j == 2 ? 0 : ++j));
                            if (listEnemy.get(i).getX() <= -100) {
                                background.remove(listEnemy.get(i));
                                listEnemy.remove(i);
                                enemyRec.remove(i);
                                tmp = getScoreIcon((int)++scoring, 0, 0);
                                score.setIcon(tmp);
                                score.setBounds((widthwin / 2) - (tmp.getIconWidth() / 2), 20, 100, 100);
                                score.setSize(tmp.getIconWidth(), tmp.getIconHeight());
                                background.repaint();
                                background.revalidate();
                                i--;
                            }
                        }
                        try {Thread.sleep(150);} catch (Exception e) {e.printStackTrace();}
                    }
                }
            }
        });
        spriteEThread.start();
    }

    static public ImageIcon getScoreIcon(int scoring, int width, int height) {
        if (scoring < 10) {
            return spriteScore[scoring];
        } else {
            int currentDigit = scoring % 10;
            ImageIcon ones = spriteScore[currentDigit];
            int onesWidth = ones.getIconWidth();
            int onesHeight = ones.getIconHeight();
            height = Math.max(height, onesHeight);
            ImageIcon tens = getScoreIcon(scoring / 10, 0, height);
            BufferedImage combined = new BufferedImage(tens.getIconWidth() + onesWidth, height, BufferedImage.TYPE_INT_ARGB);
            Graphics g = combined.getGraphics();
            g.drawImage(tens.getImage(), 0, 0, null);
            g.drawImage(ones.getImage(),tens.getIconWidth(), 0, null);
            return new ImageIcon(combined);
        }
    }

    static public void runEnemy() {
        enemyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                spriteE();
                while (dead == 0 && frame.getFlappy() == 1) {
                    try {Thread.sleep((new Random().nextInt(spawnEnemy)) * 1000);} catch (Exception e) {e.printStackTrace();}
                    if (scoring != 0 && ((((int)scoring + listEnemy.size()) % 10 == 0)) && !(scoring > 20)) {
                        try {Thread.sleep(7000);} catch (Exception e) {e.printStackTrace();}
                        if (dead != 1)
                            newEnemy();
                    }
                    else if (getPause() != 1 && dead != 1)
                        newEnemy();
                }
            }
        });
        enemyThread.start();
    }

    static public void newEnemy() {
        JLabel label = new JLabel(spriteEnemy.get(new Random().nextInt(3)));
        label.setBounds(1000, new Random().nextInt(560), 100, 100);
        background.add(label);
        background.revalidate();
        background.repaint();
        listEnemy.add(label);
        enemyRec.add(new Rectangle(label.getX(), label.getY() + (label.getHeight() / 2), label.getWidth(), (int)(label.getHeight() / 2)));
    }

    static int getPause() {
        return pause;
    }
 
    static public void removeGame(JFrame win) {
        resetAll();
        win.remove(background);
        win.remove(finalpanel);
        win.revalidate();
        win.repaint();
    }

    static int getDead() {
        return dead;
    }

    public void resetGame() {
        resetAll();
        win.remove(finalpanel);
        win.repaint();
        win.revalidate();
        setUI(win);
        runGame(win);
        getBestScore();
    }

    static public String getData() {
        return data;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (pause != 1) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (dead == 0) {
                    prev_vel = vertical_velocity;
                    if (!(vertical_velocity < -15))
                        vertical_velocity += -4;
                }
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            pause = pause == 0 ? 1 : 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER && dead == 1) {
            resetGame();
        }
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE && dead == 1) {
            dead = 2;
            removeGame(win);
            data = "/getFrameBack()";
        }
    }

    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
}

class BackgroundPanel extends JPanel {

    private static Image[] background = new Image[2];
    private static Image imgBackground = new ImageIcon(FlappyBird.class.getClassLoader().getResource("sprite/flappybird/background.png")).getImage().getScaledInstance(2399, 720, Image.SCALE_SMOOTH);
    private int x = 0;
    private int increment = 1;
    private static int image = 0;

    public BackgroundPanel() {
        for (int i = 0; i < background.length; i++)
            background[i] = imgBackground;
        LaunchBackground();
    }

    public void LaunchBackground() {
        Thread t = new Thread(new Runnable() { //Each background is drawn after the other. -> -x + the size of the background
            @Override
            public void run() {
                while (FlappyBird.getDead() != 2) {
                    try {Thread.sleep(1);} catch (Exception e) {e.printStackTrace();}
                    while (FlappyBird.getPause() != 1 && FlappyBird.getDead() == 0) {
                        x += increment;
                        if (x >= imgBackground.getWidth(null)) {
                            x = 0;
                            image = (image + 1) % 2;
                        }
                       repaint(); 
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        t.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background[image], -x, 0, this);
        g.drawImage(background[(image + 1) % 2], -x + imgBackground.getWidth(null) - 1, 0, this);
    }
}