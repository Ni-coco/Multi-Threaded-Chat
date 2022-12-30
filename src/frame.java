import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.*;

public class frame implements ActionListener, KeyListener {

    /* Related to Frame */
    private ImageIcon co = new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("img/connect.png")).getImage().getScaledInstance(10, 10, Image.SCALE_SMOOTH));
    private List<String> listofUser = new ArrayList<String>();
    private JFrame win = new JFrame();
    private JMenuBar menuBar = new JMenuBar();
    private JMenu connect = new JMenu("En ligne");
    private JMenuItem item = new JMenuItem();
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
    private String ip = GetJson.getIP();
    private int server;

    public frame (int aserver) {
        try {
            this.clientUsername = getUsername();
            setFrame();
            win.setVisible(true);
            this.socket = new Socket(ip, 8888);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            this.server = aserver;
            listen();
            sendEntered();
            if (aserver == 1) {
                win.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        GetJson.changeServer('n');
                    }
                });
                listofUser.add(clientUsername);
                displayConnected();
            }
        } catch (Exception e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void setFrame() {
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setSize(new Dimension(1080, 720));
        win.setVisible(true);
        win.getContentPane().setBackground(new Color(60, 63, 65));
        win.setLayout(new BorderLayout());

        menuBar.setBackground(Color.BLACK);
        menuBar.add(connect);
        win.setJMenuBar(menuBar);

        pn[0] = new JPanel();
        pn[0].setBackground(Color.BLACK);
        pn[0].setLayout(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(pn[0]);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
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
        win.addKeyListener(this);
        message.addKeyListener(this);
        btn.addActionListener(this);
        win.add(pn[1], BorderLayout.SOUTH);
    }

    public void sendEntered() {
        try {
            send(getColor() + "µ" + clientUsername);
        } catch (Exception e) {
            e.printStackTrace();
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void send(String msg) {
        try {
            bufferedWriter.write(msg);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void setConnected() {
        send(String.join("§", listofUser));
        displayConnected();
    }

    public void displayConnected () {
        connect.removeAll();
        for (int i = 0; i < listofUser.size(); i++) {
            item = new JMenuItem();
            item.setText(listofUser.get(i));
            item.setIcon(co);
            connect.add(item);
        }
    }

    public void listen() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JLabel label = new JLabel();
                String str;
                while (socket.isConnected()) {
                    try {
                        str = bufferedReader.readLine();
                        if (server != 1 && str.contains("§")) {
                            tmp = str.split("§");
                            listofUser.clear();
                            for (int i = 0; i < tmp.length; i++)
                                listofUser.add(tmp[i]);
                            displayConnected();
                        }
                        else if (str.contains("µ")) {
                            tmp = str.split("µ");
                            clientColor = new Color(Integer.parseInt(tmp[0].split(" ")[0]), Integer.parseInt(tmp[0].split(" ")[1]), Integer.parseInt(tmp[0].split(" ")[2]));
                            label = new JLabel(tmp[1]);
                            label.setFont(new Font("Arial", Font.PLAIN, 14));
                            label.setForeground(clientColor);
                            listm.add(label);
                            displayMsg();
                            str = tmp[1].substring(0,tmp[1].indexOf(" "));
                            if (server == 1 && !String.join("", listofUser).contains(str) && tmp[1].contains("has join the chat!")) {
                                listofUser.add(str);
                                setConnected();
                            }
                            else if (server == 1 && String.join("", listofUser).contains(str) && tmp[1].contains("has left the chat!")) {
                                listofUser.remove(str);
                                connect.removeAll();
                                setConnected();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        server = -1;
                        label = new JLabel("The server will be shut down. Prepare to re-run the program. Press Enter to continue");
                        label.setFont(new Font("Arial", Font.PLAIN, 14));
                        label.setForeground(Color.RED);
                        listm.add(label);
                        displayMsg();
                        while (server == -1) {System.out.print("");}
                        closeAll(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public String getUsername() {
        return JOptionPane.showInputDialog("Enter your username."); 
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

    public void cmd(String message) {
        try {
            if (message.charAt(0) == '/' && message.substring(1).equals("quit")) {
                if (server == 1) {
                    JLabel label = new JLabel("You're hosting the server, if your quitting everyone will be disconnected. Press Enter to confirm or Escape to cancel");
                    label.setFont(new Font("Arial", Font.PLAIN, 14));
                    label.setForeground(Color.RED);
                    listm.add(label);
                    displayMsg();
                    server = 2;
                }
                else
                    System.exit(0);
            }
            else if (message.charAt(0) == '/' && String.join("", listofUser).contains(message.substring(1, message.indexOf(' ')))) {
                send(getColor() + "µ" + clientUsername + ": " + message);
                JLabel label = new JLabel("to " + message.substring(1, message.indexOf(' ')) + ": " + message.substring(message.indexOf(' ')));
                label.setFont(new Font("Arial", Font.PLAIN, 14));
                label.setForeground(color);
                listm.add(label);
                displayMsg();
            }
            else
                send(getColor() + "µ" + clientUsername + ": " + message);
        } catch (Exception e) {}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btn) {
            if (!message.getText().equals("") && !(message.getText().length() > 116)) {
                try {
                    cmd(message.getText());
                    message.setText("");
                } catch (Exception a) {
                    closeAll(socket, bufferedReader, bufferedWriter);
                }
            }
            else if ((message.getText().length() > 116))
                JOptionPane.showMessageDialog(null, "Message too long", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!message.getText().equals("") && !(message.getText().length() > 116)) {
                try {
                    cmd(message.getText());
                    message.setText("");
                } catch (Exception a) {
                    closeAll(socket, bufferedReader, bufferedWriter);
                }
            }
            else if (server == 2) {
                GetJson.changeServer('n');
                System.exit(0);
            }
            if (server == -1) {
                server = 0;
            }
            else if ((message.getText().length() > 116)) 
                JOptionPane.showMessageDialog(null, "Message too long", "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (server == 2) {
                try {
                    JLabel label = new JLabel("You cancel your action.");
                    label.setFont(new Font("Arial", Font.PLAIN, 14));
                    label.setForeground(Color.RED);
                    listm.add(label);
                    displayMsg();
                    server = 1;
                } catch (Exception a) {}
            }
        }
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
        GetJson.changeServer('n');
        System.exit(0);
    }

    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
}
