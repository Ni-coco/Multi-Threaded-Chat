import javax.swing.*;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.IOException;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;


public class frame implements ActionListener, KeyListener {

    private JFrame win = new JFrame();
    private JPanel pn[] = new JPanel[2];
    private JTextField message = new JTextField();
    private JButton btn = new JButton("Envoyer");
    private Color color = getRGB();
    private List<JLabel> mm = new ArrayList<JLabel>();
    private int i = 0;
    private GridBagConstraints c = new GridBagConstraints();

    public frame () {
        setFrame();
        win.setVisible(true);
    }

    public void setFrame() {
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setSize(1080, 720);
        win.setVisible(true);
        win.getContentPane().setBackground(Color.BLACK);
        win.setLayout(new BorderLayout());

        pn[0] = new JPanel();
        pn[0].setBackground(Color.BLACK);
        pn[0].setLayout(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(pn[0]);
        scrollPane.setBackground(Color.BLACK);
        win.add(scrollPane, BorderLayout.CENTER);

        pn[1] = new JPanel();
        pn[1].setBackground(Color.BLACK);
        pn[1].setLayout(new BorderLayout());
        pn[1].add(message, BorderLayout.CENTER);
        pn[1].add(btn, BorderLayout.EAST);
        message.setBackground(Color.BLACK);
        message.addKeyListener(this);
        btn.addActionListener(this);
        win.add(pn[1], BorderLayout.SOUTH);
    }

    public Color getRGB() {
        Random random = new Random();
        double r = random.nextDouble() * 255;
        double g = random.nextDouble() * 255;
        double b = random.nextDouble() * 255;
        return new Color((int)r, (int)g, (int)b);
    }

    public void Display() {
        c.gridy = i;
        pn[0].add(mm.get(i), c);
        System.out.println("i = " + i);
        i++;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btn) {
            if (!message.getText().equals("")) {
                JLabel label = new JLabel(message.getText());
                label.setFont(new Font("Arial", Font.PLAIN, 14));
                label.setForeground(color);
                mm.add(label);
                message.setText("");
                Display();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!message.getText().equals("")) {
                JLabel label = new JLabel(message.getText());
                label.setFont(new Font("Arial", Font.PLAIN, 14));
                label.setForeground(color);
                mm.add(label);
                message.setText("");
                Display();
            }
        }
    }
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

}
