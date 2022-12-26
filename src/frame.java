import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.io.*;
import java.net.*;

public class frame implements ActionListener, KeyListener {

    /* Related to Frame */
    private JFrame win = new JFrame();
    private JPanel pn[] = new JPanel[2];
    private JTextField message = new JTextField();
    private JButton btn = new JButton("Envoyer");
    private Color color = getRGB();
    private List<JLabel> listm = new ArrayList<JLabel>();
    private int gridy = 0;
    /* Related to Client */
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private Color clientColor;
    private String[] tmp;

    public frame () {
        try {
            setFrame();
            win.setVisible(true);
            this.socket = new Socket("192.168.1.46", 8888);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUsername = getUsername();
        } catch (Exception e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void setFrame() {
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setSize(new Dimension(1080, 720));
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
        message.setPreferredSize(new Dimension(680, 40));
        message.setFont(new Font("Arial", Font.PLAIN, 16));
        message.setForeground(color);
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

    public String getColor() {
        return Integer.toString(color.getRed()) + " " + Integer.toString(color.getGreen()) + " " + Integer.toString(color.getBlue());
    }

    public void displayMsg() {
        GridBagConstraints cstr = new GridBagConstraints();
        cstr.insets = new Insets(0, 10, 10, 0);
        cstr.gridy = gridy;
        cstr.weightx = 1;
        cstr.anchor = GridBagConstraints.WEST;
        pn[0].add(listm.get(gridy), cstr);
        gridy++;
        pn[0].revalidate();
        pn[0].repaint();
    }

    public String getUsername() {
        return JOptionPane.showInputDialog("Enter your username."); 
    }

    public void sendEntered() {
        try {
            bufferedWriter.write(getColor() + "/" + clientUsername);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listen() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()) {
                    try {
                        tmp = bufferedReader.readLine().split("/");
                        clientColor = new Color(Integer.parseInt(tmp[0].split(" ")[0]), Integer.parseInt(tmp[0].split(" ")[1]), Integer.parseInt(tmp[0].split(" ")[2]));
                        JLabel label = new JLabel(tmp[1]);
                        label.setFont(new Font("Arial", Font.PLAIN, 14));
                        label.setForeground(clientColor);
                        listm.add(label);
                        displayMsg();
                    } catch (Exception e) {
                        closeAll(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeAll (Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null)
                bufferedReader.close();
            if (bufferedWriter != null)
                bufferedWriter.close();
            if (socket != null)
                socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean check_message(String message) {
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btn) {
            if (!message.getText().equals("") && !(message.getText().length() > 116)) {
                try {
                    String tmp = message.getText();
                    bufferedWriter.write(getColor() + "/" + clientUsername + ": " + tmp);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    message.setText("");
                } catch (Exception a) {
                    closeAll(socket, bufferedReader, bufferedWriter);
                }
            }
            else
                JOptionPane.showMessageDialog(null, "Message too long", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!message.getText().equals("") && !(message.getText().length() > 116)) {
                try {
                    String tmp = message.getText();
                    bufferedWriter.write(getColor() + "/" + clientUsername + ": " + tmp);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    message.setText("");
                } catch (Exception a) {
                    closeAll(socket, bufferedReader, bufferedWriter);
                }
            }
            else
                JOptionPane.showMessageDialog(null, "Message too long", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
}
