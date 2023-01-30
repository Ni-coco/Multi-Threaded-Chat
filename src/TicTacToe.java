import java.awt.event.MouseMotionAdapter;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Image;
import java.awt.Font;
import javax.swing.*;
import java.util.*;

public class TicTacToe {

    /* Related to Frame */
    static List<Integer> take = new ArrayList<Integer>();
    static char[] arr = new char[9];
    static ImageIcon[] icon = new ImageIcon[2];
    static Image round = new ImageIcon(TicTacToe.class.getClassLoader().getResource("sprite/tictactoe/round.png")).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
    static Image cross = new ImageIcon(TicTacToe.class.getClassLoader().getResource("sprite/tictactoe/cross.png")).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
    static JPanel[] pn = new JPanel[3];
    static JLabel[] space = new JLabel[9];
    static JButton[] btn = new JButton[2];
    static JLabel[] player = new JLabel[2];
    static JLabel w = new JLabel();
    static int turn = 0;
    static String data;
    static String username;
    static String versus;
    static int ticIcon;
    static int vsSpace = -1;
    static int vsIcon = -1;
    static int closest = -1;

    static public void setFrame(JFrame win, String user, String vs, int tic) {
        try {
            resetVariable();
            for (int i = 0; i < 9; i++)
                arr[i] = ' ';
            icon[0] = new ImageIcon(round);
            icon[1] = new ImageIcon(cross);
            player[0] = new JLabel(user);
            player[1] = new JLabel("Waiting for an Oponment...");
            if (vs != null)
            player[1].setText(vs);
            ticIcon = tic;
            username = user;
            versus = vs;
            win.setSize(1080, 720);
            win.setResizable(false);
            setUI(win, player);
            if (versus != null)
                setLabel();
            resetall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void resetVariable() {
        turn = 0;
        vsSpace = -1;
        vsIcon = -1;
        closest = -1;
    }

    static public void setUI(JFrame win, JLabel[] player) {
        try {
            pn[0] = new backgroundPanelTic();
            pn[0].setLayout(null);

            //Panel[1]
            pn[1] = new JPanel();
            pn[1].setLayout(new FlowLayout(1, 200, 10));
            pn[1].add(player[0]);
            pn[1].add(player[1]);

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
                pn[0].add(space[k]);
                i += 185;
            }

            //Adding Panels
            pn[0].add(pn[1]);
            //pn[0].setComponentZOrder(pn[1], 0);
            pn[1].setBounds(0, 0, 1080, 40);
            pn[1].setOpaque(false);
            pn[0].repaint();
            pn[0].revalidate();
            win.add(pn[0], BorderLayout.CENTER);
            win.repaint();
            win.revalidate();

            //MouseListener
            pn[0].addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    closest = getClosest(e, space);
                    if (!take.contains(closest)) {
                        space[closest].setIcon(icon[getIcon()]);
                        space[closest].setVisible(true);
                        if (vsSpace != -1 && vsIcon != -1 ) {
                            space[vsSpace].setIcon(icon[vsIcon]);
                            space[vsSpace].setVisible(true);
                        }
                        for (int i = 0; i < space.length; i++)
                            if (i != closest && !take.contains(i) && i != vsSpace)
                                space[i].setVisible(false);
                        if (turn == ticIcon && versus != null)
                            data = "/tictactoeµ" + "movedµ" + Integer.toString(ticIcon) + "µ" + Integer.toString(closest);
                    }
                }
            });
            pn[0].addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (turn == ticIcon && versus != null) {
                        int closest = getClosest(e, space);
                        if (!take.contains(closest)) {
                            space[closest].setIcon(icon[getIcon()]);
                            arr[closest] = (char)(ticIcon + '0');
                            space[closest].setVisible(true);
                            take.add(closest);
                            data = "/tictactoeµ" + "pressedµ" + Integer.toString(ticIcon) + "µ" + Integer.toString(closest);
                        }
                    }
                }
            });

            /* Panel[2] for winner */
            pn[2] = new JPanel();
            pn[2].setLayout(new GridBagLayout());
            pn[2].setBackground(Color.BLACK);
            GridBagConstraints c = new GridBagConstraints();
            c.gridy = 0;

            //Label for winner
            w.setForeground(Color.WHITE);
            w.setFont(new Font("Arial", Font.BOLD, 55));
            //Constraints
            c.insets = new Insets(10, 10, 40, 10);
            c.gridy = 0;
            pn[2].add(w, c);
            btn[0] = new JButton("Exit");
            btn[0].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == btn[0]) {
                        resetall();
                        win.remove(pn[2]);
                        data = "/getFrameBack()";
                    }
            }});
            btn[0].setFont(new Font("Arial", Font.BOLD, 20));

            c.insets = new Insets(10, 10, 20, 10);
            c.ipadx = 40;
            c.ipady = 20;
            c.gridy = 1;
            pn[2].add(btn[0], c);

            resetall();
        } catch (Exception e) {e.printStackTrace();}
    }

    static public void setMoved(int nicon, int vsclosest) {
        vsSpace = vsclosest;
        vsIcon = nicon;
        space[vsclosest].setIcon(icon[nicon]);
        space[vsclosest].setVisible(true);
        if (vsclosest != closest && closest != -1 && !take.contains(closest))
            space[closest].setIcon(icon[getIcon()]);
        for (int i = 0; i < space.length; i++)
            if (i != vsclosest && i != closest && !take.contains(i))
                space[i].setVisible(false);
    }

    static public void setPressed(int nicon, int vsclosest, JFrame win) {
        space[vsclosest].setIcon(icon[nicon]);
        space[vsclosest].setVisible(true);
        if (!take.contains(vsclosest))
            take.add(vsclosest);
        arr[vsclosest] = (char)(nicon + '0');
        turn = turn == 0 ? 1 : 0;
        if (check_win(arr) != -1) {
            setWinner(check_win(arr) == ticIcon ? username + " win!" : versus + " win!", win);
            if (turn != ticIcon)
                data = "/doneµ" + (check_win(arr) == ticIcon ? username : versus) + "µ" + (check_win(arr) == ticIcon ? versus : username) + "µTictacToe";
        }
        else if (take.size() == 9) {
            setWinner("draw!", win);
            if (turn != ticIcon)
                data = "/drawµ" + username + "µ" + versus  + "µTictacToe";
        }
        setLabel();
    }

    static public void setWinner (String winner, JFrame win) {
        for (int i = 0; i < 2; i++)
                win.remove(pn[i]);
            w.setText(winner);
            win.add(pn[2], BorderLayout.CENTER);
            win.revalidate();
            win.repaint();
    }

    static public void setLabel() {
        player[0].setText(turn == ticIcon ? username + " turn!" : username);
        player[1].setText(turn == ticIcon ? versus : versus + " turn!");
    }

    static public void setVersus(String vs) {
        player[1].setText(vs);
        versus = vs;
        setLabel();
    }

    static public int getIcon() {
        return ticIcon == 0 ? 0 : 1;
    }

    static public String getData() {
        return data;
    }

    static public void setTurn(int i) {
        turn = i;
    }

    static public int getTurn () {
        return turn;
    }
    static public void setTic(int i) {
        ticIcon = i;
    }

    static public int check_win(char[] arr) {
        if (arr[0] != ' ' && arr[0] == arr[1] && arr[0] == arr[2])
            return arr[0] == '1' ? 1 : 0;
        else if (arr[3] != ' ' && arr[3] == arr[4] && arr[3] == arr[5])
            return arr[3] == '1' ? 1 : 0;
        else if (arr[6] != ' ' && arr[6] == arr[7] && arr[6] == arr[8])
            return arr[6] == '1' ? 1 : 0;
        else if (arr[0] != ' ' && arr[0] == arr[3] && arr[0] == arr[6])
            return arr[0] == '1' ? 1 : 0;
        else if (arr[1] != ' ' && arr[1] == arr[4] && arr[1] == arr[7])
            return arr[1] == '1' ? 1 : 0;
        else if (arr[2] != ' ' && arr[2] == arr[5] && arr[2] == arr[8])
            return arr[2] == '1' ? 1 : 0;
        else if (arr[0] != ' ' && arr[0] == arr[4] && arr[0] == arr[8])
            return arr[0] == '1' ? 1 : 0;
        else if (arr[2] != ' ' && arr[2] == arr[4] && arr[2] == arr[6])
            return arr[2] == '1' ? 1 : 0;
        return -1;
    }

    static public void resetall() {
        take.clear();
        for (int i = 0; i < 9; i++) {
            arr[i] = ' ';
            space[i].setVisible(false);
        }
    }

    static public double dist(int x1, int y1, JLabel component) {
        int componentX = component.getX() + component.getWidth() / 2;
        int componentY = component.getY() + component.getHeight() / 2;
        return Math.sqrt((x1 - componentX) * (x1 - componentX) + (y1 - componentY) * (y1 - componentY));
    }

    static public int getClosest(MouseEvent e, JLabel[] space) {
        int closest = 0;
        double shortestDistance = dist(e.getX(), e.getY(), space[0]);
        for (int i = 0; i < space.length; i++) {
            double d = dist(e.getX(), e.getY(), space[i]);
            if (d < shortestDistance) {
                closest = i;
                shortestDistance = d;
            }
        }
        return closest;
    }

    static public void removeGame(JFrame win) {
        resetall();
        for (int i = 0; i < 2; i++) {
            pn[i].removeAll();
            win.remove(pn[i]);
        }
    }
}

class backgroundPanelTic extends JPanel {
    private static Image imgBackground = new ImageIcon(TicTacToe.class.getClassLoader().getResource("sprite/tictactoe/background.png")).getImage().getScaledInstance(1080, 700, Image.SCALE_SMOOTH);

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(imgBackground, 0, 0, this);
    }
}