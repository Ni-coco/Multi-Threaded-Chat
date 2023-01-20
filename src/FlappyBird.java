/*import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Random;*/
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.*;
import java.awt.*;
import java.awt.Graphics;
import java.util.Random;
import java.util.TimerTask;
import java.util.Timer;



public class FlappyBird implements KeyListener {

    /* set Variable */
    private JFrame win;
    static ImageIcon[] sprite = new ImageIcon[6];
    static JLabel bird = new JLabel();
    static BackgroundPanel background = new BackgroundPanel();
    static JLabel uipressenter = new JLabel(new ImageIcon(new ImageIcon(FlappyBird.class.getClassLoader().getResource("img/flappybird/toplay.png")).getImage().getScaledInstance(250, 100, Image.SCALE_SMOOTH)));
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

    public FlappyBird (JFrame frame) {
        win = frame;
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
        for (int j = 0; j < sprite.length; j++)
            sprite[j] = new ImageIcon (new ImageIcon(FlappyBird.class.getClassLoader().getResource("img/flappybird/bluebird"+Integer.toString(j)+".png")).getImage().getScaledInstance(90, 60, Image.SCALE_SMOOTH));
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
                        for (int i = 1; i < sprite.length; i++) {
                            bird.setIcon(sprite[i]);
                            try {Thread.sleep(50);} catch (Exception e) {e.printStackTrace();}
                        }
                        prev_vel = 0;
                    }
                    else
                        bird.setIcon(sprite[0]);
                }
            }
        });
        spriteThread.start();
    }

    static int getPause() {
        return pause;
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

    private static Image[] background = new Image[2];
    private static Image img = new ImageIcon(FlappyBird.class.getClassLoader().getResource("img/flappybird/background.png")).getImage().getScaledInstance(2399, 720, Image.SCALE_SMOOTH);
    //static TimerTask task;
    private int x = 0;
    private int increment = 1;
    private static int image = 0;

    public BackgroundPanel() {
        //taskk();
        for (int i = 0; i < background.length; i++)
            background[i] = img;
        Thread t = new Thread(new Runnable() { //Each background is drawn after the other. -> -x + the size of the background
            @Override
            public void run() {
                for (;;) {
                    try { Thread.sleep(1);} catch (Exception e) {e.printStackTrace();}
                    while (FlappyBird.getPause() != 1) {
                        x += increment;
                        if (x >= img.getWidth(null)) {
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

    /*public static void taskk() {
        Timer timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("This code is executed every 5 to 10 seconds randomly");
                int delay = new Random().nextInt(5) + 5; // generates a random delay between 5 and 10 seconds
                timer.schedule(task, delay * 1000); // schedule the task again with the random delay
            }
        };
        timer.schedule(task, 0);
    }*/

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background[image], -x, 0, this);
        g.drawImage(background[(image + 1) % 2], -x + img.getWidth(null) - 1, 0, this);
    }
}