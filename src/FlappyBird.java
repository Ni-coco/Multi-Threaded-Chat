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
    static JLabel uipressenter = new JLabel(new ImageIcon(new ImageIcon(FlappyBird.class.getClassLoader().getResource("img/flappybird/toplay.png")).getImage().getScaledInstance(250, 100, Image.SCALE_SMOOTH)));
    static JLabel uipause = new JLabel(new ImageIcon(new ImageIcon(FlappyBird.class.getClassLoader().getResource("img/flappybird/pause.png")).getImage().getScaledInstance(200, 70, Image.SCALE_SMOOTH)));
    static ImageIcon[] spritePlayer = new ImageIcon[6];
    static List<ImageIcon> spriteEnemy = new ArrayList<ImageIcon>();
    static ImageIcon[] spriteScore = new ImageIcon[10];
    static JLabel player = new JLabel();
    static BackgroundPanel background = new BackgroundPanel();
    static JPanel[] pn = new JPanel[2];
    static JButton btn = new JButton("Exit");
    static String data;
    static String username;
    static int pause = 1;
    static double vertical_velocity = 0;
    static double prev_vel = 0;
    static double vertical_velocity_max = 5;
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

    public FlappyBird (JFrame frame) {
        win = frame;
        widthwin = win.getWidth();
        win.add(background);
        win.setSize(1080, 720);
        win.setResizable(false);
        background.requestFocus();
        if (background.getKeyListeners().length == 0)
            background.addKeyListener(this);
        setUI(win);
        if (gameThread == null)
            runGame(win);
    }

    static public void setUI(JFrame win) {
        for (int i = 0; i < spritePlayer.length; i++)
            spritePlayer[i] = new ImageIcon(new ImageIcon(FlappyBird.class.getClassLoader().getResource("img/flappybird/player/bluebird"+Integer.toString(i)+".png")).getImage().getScaledInstance(90, 60, Image.SCALE_SMOOTH));
        for (int i = 0; i < 3; i++)
            spriteEnemy.add(new ImageIcon(new ImageIcon(FlappyBird.class.getClassLoader().getResource("img/flappybird/enemy/"+Integer.toString(i)+".png")).getImage().getScaledInstance(100, 70, Image.SCALE_SMOOTH)));
        for (int i = 0; i < spriteScore.length; i++)
            spriteScore[i] = new ImageIcon(new ImageIcon(FlappyBird.class.getClassLoader().getResource("img/flappybird/score/"+Integer.toString(i)+".png")).getImage().getScaledInstance(40, 60, Image.SCALE_SMOOTH));
        player.setIcon(spritePlayer[0]);
        score.setIcon(spriteScore[0]);
        background.removeAll();
        background.setLayout(new BorderLayout());
        background.add(uipressenter, BorderLayout.CENTER);
        uipressenter.setBounds(50, 50, 100, 100);
    }

    static void runGame(JFrame win) {
        gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                spriteP();
                runEnemy();
                getHit();
                for (;;) {
                    try {Thread.sleep(1);} catch (Exception e) {e.printStackTrace();}
                    while (win.isVisible() && pause == 0) {
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
                        }
                        if (player.getY() + vertical_velocity < -50)
                            vertical_velocity = 0;
                        else if (player.getY() >= 593 && dead == 0) {
                            dead = 1;
                            finalscore = (int)scoring;
                            try {Thread.sleep(10);} catch (Exception e) {e.printStackTrace();}
                            player.setIcon(new ImageIcon(new ImageIcon(FlappyBird.class.getClassLoader().getResource("img/flappybird/player/bluebirddead.png")).getImage().getScaledInstance(90, 60, Image.SCALE_SMOOTH)));
                        }
                        player.setLocation(player.getX(), (int)(player.getY() + vertical_velocity));
                        for (int i = 0; i < listEnemy.size(); i++)
                            listEnemy.get(i).setLocation(listEnemy.get(i).getX() - (int)speedEnemy , listEnemy.get(i).getY());
                    }
                    if (!Arrays.asList(background.getComponents()).contains(uipressenter) && !Arrays.asList(background.getComponents()).contains(uipause)) {
                        background.setLayout(new BorderLayout());
                        background.add(uipause, BorderLayout.CENTER);
                        background.revalidate();
                        background.repaint();
                    }

                }
            }
        });
        gameThread.start();
    }

    static void getHit() {
        hitThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Rectangle playerRec = new Rectangle(player.getX(), player.getY() + (player.getHeight() / 3), player.getWidth(), player.getHeight() / 2);
                while (dead == 0) {
                    try {Thread.sleep(1);} catch (Exception e) {e.printStackTrace();}
                    for (int i = 0; i < listEnemy.size(); i++) {
                        playerRec.setRect(player.getX() + (player.getWidth() / 10), player.getY() + (player.getHeight() / 3), (int)(player.getWidth() / 1.5), (int)(player.getHeight() / 3));
                        enemyRec.get(i).setRect(listEnemy.get(i).getX() + (listEnemy.get(i).getWidth() / 10), listEnemy.get(i).getY() + (listEnemy.get(i).getHeight() / 3), (int)(listEnemy.get(i).getWidth() / 1.5), (int)(listEnemy.get(i).getHeight() / 3));
                        if (playerRec.intersects(enemyRec.get(i))) {
                            dead = 1;
                            finalscore = (int)scoring;
                            try {Thread.sleep(10);} catch (Exception e) {e.printStackTrace();}
                            player.setIcon(new ImageIcon(new ImageIcon(FlappyBird.class.getClassLoader().getResource("img/flappybird/player/bluebirddead.png")).getImage().getScaledInstance(90, 60, Image.SCALE_SMOOTH)));

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
                while (dead == 0) {
                    if (prev_vel != 0 && prev_vel + 3 > vertical_velocity) {
                        for (int i = 1; i < spritePlayer.length; i++) {
                            player.setIcon(spritePlayer[i]);
                            try {Thread.sleep(50);} catch (Exception e) {e.printStackTrace();}
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
                while (dead == 0) {
                    try {Thread.sleep(1);} catch (Exception e) {e.printStackTrace();}
                    while (listEnemy.size() > 0 && getPause() != 1) {
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
                while (dead == 0) {
                    try {Thread.sleep((new Random().nextInt(spawnEnemy)) * 1000);} catch (Exception e) {e.printStackTrace();}
                    if (scoring != 0 && ((((int)scoring + listEnemy.size()) % 10 == 0)) && !(scoring > 20)) {
                        try {Thread.sleep(7000);} catch (Exception e) {e.printStackTrace();}
                        newEnemy();
                    }
                    else if (getPause() != 1)
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
        pause = 1;
        listEnemy.clear();
        background.removeAll();
        win.remove(background);
        gameThread.interrupt();
        spritePThread.interrupt();
        spriteEThread.interrupt();
        enemyThread.interrupt();
        hitThread.interrupt();
        win.revalidate();
        win.repaint();
    }

    static int getDead() {
        return dead;
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
    }

    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
}

class BackgroundPanel extends JPanel {

    private static Image[] background = new Image[2];
    private static Image imgBackground = new ImageIcon(FlappyBird.class.getClassLoader().getResource("img/flappybird/background.png")).getImage().getScaledInstance(2399, 720, Image.SCALE_SMOOTH);
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
                while (FlappyBird.getDead() == 0) {
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