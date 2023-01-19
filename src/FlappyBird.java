/*import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Random;*/
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.*;
import java.awt.*;
import java.awt.Graphics;


public class FlappyBird implements KeyListener {

    /* set Variable */
    private JFrame win;
    static int flappybird;
    static ImageIcon[] sprite = new ImageIcon[3];
    static Image bird1 = new ImageIcon(FlappyBird.class.getClassLoader().getResource("img/flappybird/1.png")).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
    static Image bird2 = new ImageIcon(FlappyBird.class.getClassLoader().getResource("img/flappybird/2.png")).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
    static Image bird3 = new ImageIcon(FlappyBird.class.getClassLoader().getResource("img/flappybird/3.png")).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
    static JLabel bird = new JLabel();
    static BackgroundPanel background = new BackgroundPanel();
    static JLabel uipressenter = new JLabel(new ImageIcon(new ImageIcon(FlappyBird.class.getClassLoader().getResource("img/flappybird/toplay.png")).getImage().getScaledInstance(200, 100, Image.SCALE_SMOOTH)));
    static JLabel uipause = new JLabel(new ImageIcon(new ImageIcon(FlappyBird.class.getClassLoader().getResource("img/flappybird/pause.png")).getImage().getScaledInstance(200, 70, Image.SCALE_SMOOTH)));
    static JPanel[] pn = new JPanel[2];
    static JButton btn = new JButton("Exit");
    static String data;
    static String username;
    static int pause = 1;
    static double vertical_velocity = 0;
    static double prev_vel = 0;
    static double vertical_velocity_max = 5;
    static double gravity = 0.1;
    static Thread gameThread;
    static Thread spriteThread;


    // velocity

    public FlappyBird (JFrame frame, int i) {
        win = frame;
        flappybird = i;
        win.add(background);
        win.setSize(1080, 720);
        win.setResizable(false);
        background.requestFocus();
        if (background.getKeyListeners().length == 0)
            background.addKeyListener(this);
        setUI(win);
        if (gameThread == null) {
            runGame(win);
            runSprite(); 
        }
    }

    static public void setUI(JFrame win) {
        sprite[0] = new ImageIcon(bird1);
        sprite[1] = new ImageIcon(bird2);
        sprite[2] = new ImageIcon(bird3);
        bird.setIcon(sprite[0]);
        background.removeAll();
        background.setLayout(new BorderLayout());
        background.add(uipressenter, BorderLayout.CENTER);
        uipressenter.setBounds(50, 50, 100, 100);
    }

    static public void runGame(JFrame win) {
        gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    try {Thread.sleep(1);} catch (Exception e) {e.printStackTrace();}
                    while (win.isVisible() && pause == 0) {
                        if (Arrays.asList(background.getComponents()).contains(uipause)) {
                            background.remove(uipause);
                            background.revalidate();
                            background.repaint();
                        }
                        else if (Arrays.asList(background.getComponents()).contains(uipressenter)) {
                            background.setLayout(null);
                            background.remove(uipressenter);
                            background.add(bird);
                            bird.setBounds(40, 10, 100, 100);
                            background.revalidate();
                            background.repaint();
                        }
                        try {Thread.sleep(10);} catch (Exception e) {e.printStackTrace();}
                        vertical_velocity += gravity;
                        if (bird.getY() + vertical_velocity < -50)
                            vertical_velocity = 0;
                        else if (bird.getY() > 600)
                            vertical_velocity = -1;
                        bird.setLocation(bird.getX(), (int)(bird.getY() + vertical_velocity));
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

    static public void runSprite() {
        spriteThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (prev_vel != 0 && prev_vel + 3 > vertical_velocity) {
                        bird.setIcon(sprite[1]);
                        try {Thread.sleep(100);} catch (Exception e) {e.printStackTrace();}
                        bird.setIcon(sprite[2]);
                        try {Thread.sleep(100);} catch (Exception e) {e.printStackTrace();}
                        prev_vel = 0;
                    }
                    else
                        bird.setIcon(sprite[0]);
                }
            }
        });
        spriteThread.start();
    }

    static public void removeGame(JFrame win) {
        pause = 1;
        background.removeAll();
        win.remove(background);
        win.revalidate();
        win.repaint();
        
        /*for (int i = 0; i < 1; i++) {
            pn[i].removeAll();
            win.remove(pn[i]);
        }*/
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (pause != 1) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                prev_vel = vertical_velocity;
                if (!(vertical_velocity < -15))
                    vertical_velocity += -4;
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
    private static Image background = new ImageIcon(FlappyBird.class.getClassLoader().getResource("img/flappybird/background.png")).getImage().getScaledInstance(9600, 720, Image.SCALE_SMOOTH);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, this);
    }
}