import java.awt.event.MouseMotionAdapter;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.Font;
import javax.swing.*;
import java.util.*;


public class TicTacToe {

    /* Related to Frame */
    static List<Integer> x = new ArrayList<Integer>();
    static List<Integer> y = new ArrayList<Integer>();
    static List<Integer> take = new ArrayList<Integer>();
    static char[] arr = new char[9];
    static ImageIcon cross = new ImageIcon(new ImageIcon(TicTacToe.class.getClassLoader().getResource("img/cross.png")).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
    static ImageIcon round = new ImageIcon(new ImageIcon(TicTacToe.class.getClassLoader().getResource("img/round.png")).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
    static JPanel[] pn = new JPanel[2];
    static JLabel[] player = new JLabel[2];
    static JLabel[] space = new JLabel[9];
    static JButton[] btn = new JButton[2];
    static JLabel w = new JLabel();
    static int turn = 0;
    static String data;
    static String username;

    static public void setFrame(JFrame win, String user) {
        username = user;
        win.setResizable(false);
        setUI(win);

        for (;;) {
            setTurn();
            int winning = check_win(arr);
            if (winning != 0 || take.size() == 9) {
                System.out.println("here");
                setWinner(winning, win);
            }
        }
    }

    static public void setUI(JFrame win) {
        pn[0] = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Stroke stroke = new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(stroke);
                g.setColor(Color.WHITE);
                int i = (int)Math.round(win.getWidth()/2.42);
                int j = (int)Math.round(win.getHeight()/14.4);
                g.drawLine(i, j, i, (int)Math.round(j*11.6));
                g.drawLine((int)Math.round(i*1.405), j, (int)Math.round(i*1.405), (int)Math.round(j*11.6));
                g.drawLine((int)Math.round(i*0.615), (int)Math.round(j*4.44), (int)Math.round(i*1.753), (int)Math.round(j*4.44));
                g.drawLine((int)Math.round(i*0.615), (int)Math.round(j*8.16), (int)Math.round(i*1.753), (int)Math.round(j*8.16));
            }
        };

        //Panel Background and Layout
        pn[0].setBackground(Color.BLACK);
        pn[0].setLayout(null);

        //Labels for panel[0]
        for(int i = 0; i < space.length; i++)
            space[i] = new JLabel();
        int i = 300;
        int j = 95;
        for (int k = 0; k < space.length; k++) {
            if (k % 3 == 0 && k != 0) {
                i = 300;
                j += 180;
            }
            space[k].setBounds(i, j, 100, 100);
            space[k].setVisible(false);
            x.add(i);
            y.add(j);
            pn[0].add(space[k]);
            i += 185;
        }

        /* Panel[1] for winner */
        /*pn[1] = new JPanel();
        pn[1].setLayout(new GridBagLayout());
        pn[1].setBackground(Color.BLACK);
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;*/

        //Label for winner
        /*w.setForeground(Color.WHITE);
        w.setFont(new Font("Arial", Font.BOLD, 55));
        //Constraints
        c.insets = new Insets(10, 10, 40, 10);
        c.gridy = 0;
        pn[2].add(w, c);*/

        //btn[]
        /*btn[0] = new JButton("Remake!");
        btn[1] = new JButton("Exit");
        btn[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == btn[0]) {
                win.getContentPane().remove(pn[2]);
                pn[0].setVisible(true);
            }
        }});
        btn[1].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
        }});
        btn[0].setFont(new Font("Arial", Font.BOLD, 20));
        btn[1].setFont(new Font("Arial", Font.BOLD, 20));

        c.insets = new Insets(10, 10, 20, 10);
        c.ipadx = 40;
        c.ipady = 20;
        c.gridy = 1;
        pn[1].add(btn[0], c);
        c.gridy = 2;
        pn[1].add(btn[1], c);*/

        //Adding Panels
        win.add(pn[0], BorderLayout.CENTER);

        //MouseListener
        pn[0].addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int closest = getclosest(e, x, y);
                data = Integer.toString(0) + "µ" + Integer.toString(closest);
                if (turn == 0 && !take.contains(closest)) {
                    space[closest].setIcon(cross);
                    space[closest].setVisible(true);
                }
                else if (turn == 1 && !take.contains(closest)) {
                    space[closest].setIcon(round);
                    space[closest].setVisible(true);
                }
                for (int i = 0; i < space.length; i++)
                    if (i != closest && !take.contains(i))
                        space[i].setVisible(false);
            }
        });
        pn[0].addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int closest = getclosest(e, x, y);
                data = Integer.toString(0) + "µ" + Integer.toString(closest);
                if (turn == 0 && !take.contains(closest)) {
                    space[closest].setIcon(cross);
                    arr[closest] = 'x';
                    turn++;
                }
                else if (turn == 1 && !take.contains(closest)) {
                    space[closest].setIcon(round);
                    arr[closest] = 'o';
                    turn--;
                }
                space[closest].setVisible(true);
                take.add(closest);
            }
        });

        resetall();
    }

    static public String getData() {
        return data;
    }

    
    static public void setTurn() {
        player[turn%2].setText("Player " + (turn%2 == 0 ? 1 : 2) + " turn!");
        player[turn%2].setForeground(Color.RED);
        player[(turn%2 == 0 ? 1 : 0)].setText("Player " + (turn%2 == 0 ? 2 : 1));
        player[(turn%2 == 0 ? 1 : 0)].setForeground(Color.BLACK);
    }

    static public int check_win(char[] arr) {
        if (arr[0] == arr[1] && arr[0] == arr[2])
            return arr[0] == 'x' ? 1 : 2;
        else if (arr[3] == arr[4] && arr[3] == arr[5])
            return arr[3] == 'x' ? 1 : 2;
        else if (arr[6] == arr[7] && arr[6] == arr[8])
            return arr[6] == 'x' ? 1 : 2;
        else if (arr[0] == arr[3] && arr[0] == arr[6])
            return arr[0] == 'x' ? 1 : 2;
        else if (arr[1] == arr[4] && arr[1] == arr[7])
            return arr[1] == 'x' ? 1 : 2;
        else if (arr[2] == arr[5] && arr[2] == arr[8])
            return arr[2] == 'x' ? 1 : 2;
        else if (arr[0] == arr[4] && arr[0] == arr[8])
            return arr[0] == 'x' ? 1 : 2;
        else if (arr[2] == arr[4] && arr[2] == arr[6])
            return arr[2] == 'x' ? 1 : 2;
        return 0;
    }

    static public void setWinner(int winner, JFrame win) {
        resetall();
        pn[0].setVisible(false);
        win.add(pn[1], BorderLayout.CENTER);
        if (winner != 0)
            w.setText("Player " + winner + " win!");
        else
            w.setText("Draw!");
    }

    static public void resetall() {
        take.clear();
        for (int i = 0; i < 9; i++) {
            arr[i] = (char)i;
            if (space[i] != null)
                space[i].setVisible(false);
        }
    }

    static public int DistSquared(MouseEvent e, int x, int y) {
        int diffX = e.getX() - x;
        int diffY = e.getY() - y;
        return (diffX*diffX+diffY*diffY);
    }

    static public int getclosest(MouseEvent e, List<Integer> x, List<Integer> y) {
        int closest = 0;
        int ShortestDistance = DistSquared(e, x.get(0), y.get(0));
        for (int i = 0; i < space.length; i++) {
            int d = DistSquared(e, x.get(i), y.get(i));
            if (d < ShortestDistance) {
                closest = i;
                ShortestDistance = d;
            }
        }
        return closest;
    }
}