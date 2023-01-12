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

    /* Related to MsgFrame */
    private ImageIcon co = new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("img/connect.png")).getImage().getScaledInstance(10, 10, Image.SCALE_SMOOTH));
    private List<String> listofUser = new ArrayList<String>();
    private JFrame win = new JFrame();
    private JMenuBar menuBar = new JMenuBar();
    private JMenu connectMenu = new JMenu("En ligne");
    private JMenu gamesMenu = new JMenu("Games");
    private JMenu helpMenu = new JMenu("Help");
    private JMenu clientMenu = new JMenu();
    private JMenuItem clientItem = new JMenuItem();
    private JMenuItem tictactoeItem = new JMenuItem("TicTacToe");
    private JMenuItem helpItemuser = new JMenuItem("/user");
    private JMenuItem helpItemcolor = new JMenuItem("/color");
    private JMenuItem helpItemquit = new JMenuItem("/quit");
    private JMenuItem disconnect = new JMenuItem("Disconnect");
    private JPanel[] pnmsg = new JPanel[2];
    private JScrollPane scrollPane = new JScrollPane();
    private JTextField message = new JTextField();
    private JButton btnmsg = new JButton("Envoyer");
    private Color color = getRGB();
    private List<JLabel> listm = new ArrayList<JLabel>();
    private int gridy = 0;
    private int tictactoe = 0;
    private String versus = null;
    private String prevVersus;
    private String dataGame;
    private int tic = 0;
    /* Related to Client */
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private Color clientColor;
    private String[] tmp;
    private String ip = GetJson.getIP();
    private int server;

    /* 2 soucis a regler
     * Le dernier space qui apparait au moment de relancer
     * systeme de tour quand on relance chelou
     */

    public frame(int aserver) {
        try {
            this.socket = new Socket(ip, 8888);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            this.server = aserver;
            String firstOutput;
            String msgInputDialog = "Enter your username";
            do {
                this.clientUsername = getUsername(msgInputDialog);
                firstOutput = bufferedReader.readLine();
                msgInputDialog = "The username is Already in use";
            } while (firstOutput.equals("take"));
            setFrame();
            listen();
            win.setVisible(true);
            setFirst(server, firstOutput);
            win.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (aserver == 1)
                        GetJson.changeServer('n');
                    if (tictactoe == 1) {
                        send(getColor() + "µ" + clientUsername + " has left TicTacToe game");
                        send("/tic--");
                    }
                }
            });
            if (aserver == 1) {
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
        menuBar.add(connectMenu);
        tictactoeItem.addActionListener(this);
        gamesMenu.add(tictactoeItem);
        menuBar.add(gamesMenu);
        helpItemcolor.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        helpItemuser.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        helpItemquit.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        helpItemcolor.addActionListener(this);
        helpItemuser.addActionListener(this);
        helpItemquit.addActionListener(this);
        helpMenu.add(helpItemcolor);
        helpMenu.add(helpItemuser);
        helpMenu.add(helpItemquit);
        menuBar.add(helpMenu);
        clientMenu.setText(clientUsername);
        clientMenu.setIcon(co);
        clientMenu.setIconTextGap(1);
        disconnect.addActionListener(this);
        disconnect.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        clientMenu.add(disconnect);
        menuBar.add(clientMenu);
        win.setJMenuBar(menuBar);

        pnmsg[0] = new JPanel();
        pnmsg[0].setBackground(Color.BLACK);
        pnmsg[0].setLayout(new GridBagLayout());
        scrollPane.setViewportView(pnmsg[0]);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
        scrollPane.setBackground(Color.BLACK);
        win.add(scrollPane, BorderLayout.CENTER);

        pnmsg[1] = new JPanel();
        pnmsg[1].setBackground(Color.BLACK);
        pnmsg[1].setLayout(new BorderLayout());
        pnmsg[1].add(message, BorderLayout.CENTER);
        pnmsg[1].add(btnmsg, BorderLayout.EAST);
        message.setBackground(Color.BLACK);
        message.setPreferredSize(new Dimension(680, 40));
        message.setFont(new Font("Arial", Font.PLAIN, 16));
        message.setForeground(color);
        win.addKeyListener(this);
        message.addKeyListener(this);
        btnmsg.addActionListener(this);
        win.add(pnmsg[1], BorderLayout.SOUTH);
    }

    public void listen() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String str;
                while (socket.isConnected()) {
                    try {
                        str = bufferedReader.readLine();
                        if (str != null) {
                            if (tictactoe == 1 && str.contains("µ") && str.split("µ")[0].equals("/tictactoe") && str.split("µ").length == 4) {
                                if (str.split("µ")[1].equals("moved"))
                                    TicTacToe.setMoved(Integer.parseInt(str.split("µ")[2]), Integer.parseInt(str.split("µ")[3]));
                                else if (str.split("µ")[1].equals("pressed"))
                                    TicTacToe.setPressed(Integer.parseInt(str.split("µ")[2]), Integer.parseInt(str.split("µ")[3]), win);
                            }
                            else if (server != 1 && str.contains("//") && str.split(" ", 2)[0].equals("(MsgHistoric=)")) {
                                String[] tmp1 = str.split("//");
                                for (int i = 1; i < tmp1.length; i++) {
                                    String[] tmp2 = tmp1[i].split("µ");
                                    clientColor = new Color(Integer.parseInt(tmp2[0].split(" ")[0]), Integer.parseInt(tmp2[0].split(" ")[1]), Integer.parseInt(tmp2[0].split(" ")[2]));
                                    if (versus == null && tmp2[1].split(" ", 2)[1].equals("has entered TicTacToe game")) {
                                        versus = tmp2[1].split(" ", 2)[0];
                                        if (tmp2[1].split(" ", 2)[1].equals(versus) && tmp2[1].split(" ", 2)[1].equals("has left TicTacToe game"))
                                            versus = null;
                                    }
                                    setLabel(tmp2[1], clientColor);
                                }
                            }
                            else if (str.contains("µ") && str.contains(" ") && str.split("µ", 2)[1].split(" ", 2)[1].equals("has entered TicTacToe game")) {
                                tmp = str.split("µ", 2);
                                clientColor = new Color(Integer.parseInt(tmp[0].split(" ")[0]), Integer.parseInt(tmp[0].split(" ")[1]), Integer.parseInt(tmp[0].split(" ")[2]));
                                setLabel(tmp[1], clientColor);
                                if (!tmp[1].split(" ", 2)[0].equals(clientUsername)) {
                                    if (versus != null)
                                        prevVersus = versus;
                                    versus = tmp[1].split(" ", 2)[0];
                                    if (tictactoe == 1)
                                        TicTacToe.setVersus(versus);
                                }
                            }
                            else if (str.contains("µ") && str.contains(" ") && str.split("µ", 2)[1].split(" ", 2)[1].equals("has left TicTacToe game")) {
                                tmp = str.split("µ", 2);
                                clientColor = new Color(Integer.parseInt(tmp[0].split(" ")[0]), Integer.parseInt(tmp[0].split(" ")[1]), Integer.parseInt(tmp[0].split(" ")[2]));
                                setLabel(tmp[1], clientColor);
                                if (tictactoe == 0 && versus.equals(str.split("µ", 2)[1].split(" ", 2)[0]))
                                    versus = prevVersus;
                                if (tictactoe == 1 && tic == 1) {
                                    tic = 0;
                                    TicTacToe.setTic(0);
                                    TicTacToe.setVersus("Waiting for an Oponment...");
                                }
                            }
                            else if (str.equals("/tic--"))
                                tic += -1;
                            else if (str.equals("/tic++"))
                                tic += 1;
                            else if (str.equals("/ticfull"))
                                tic = 2;
                            else if (server != 1 && str.contains("§")) {
                                tmp = str.split("§");
                                listofUser.clear();
                                for (int i = 0; i < tmp.length; i++)
                                    listofUser.add(tmp[i]);
                                displayConnected();
                            }
                            else if (str.contains("µ") && !str.split("µ")[0].equals("/tictactoe") && !str.split("µ")[1].equals(clientUsername + " changed their color") && !str.split(" ", 2)[0].equals("(MsgHistoric=)")) {
                                if (str.split("µ")[0].equals("/tic="))
                                    tic = Integer.parseInt(str.split("µ")[1]);
                                else {
                                    tmp = str.split("µ");
                                    clientColor = new Color(Integer.parseInt(tmp[0].split(" ")[0]), Integer.parseInt(tmp[0].split(" ")[1]), Integer.parseInt(tmp[0].split(" ")[2]));
                                    if (tmp[1].split(" ", 2)[1].equals("changed their color"))
                                        setChangedColor(tmp[1].split(" ", 2)[0], clientColor);
                                    setLabel(tmp[1], clientColor);
                                    str = tmp[1].substring(0,tmp[1].indexOf(" "));
                                    if (server == 1 && !String.join("", listofUser).contains(str) && tmp[1].contains("has join the chat!")) {
                                        listofUser.add(str);
                                        sendPast();
                                        setConnected();
                                    }
                                    else if (server == 1 && String.join("", listofUser).contains(str) && tmp[1].contains("has left the chat!")) {
                                        listofUser.remove(str);
                                        connectMenu.removeAll();
                                        setConnected();
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        if (server == 0)
                            JOptionPane.showConfirmDialog(null, "The server will be shut down. Prepare to re-run the program.", "", JOptionPane.CLOSED_OPTION);
                        closeAll(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void dataTicTacToe() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (tictactoe == 1) {
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {e.printStackTrace();}
                    if (dataGame != TicTacToe.getData()) {
                        dataGame = TicTacToe.getData();
                        if (dataGame.contains("µ") && dataGame.split("µ")[0].equals("/done"))
                            send(getColor() + "µ" + resultGame.getWin(dataGame.split("µ")[1], dataGame.split("µ")[2], dataGame.split("µ")[3]));
                        else if (dataGame.equals("/getFrameBack()")) {
                            versus = null;
                            getFrameBack();
                            win.revalidate();
                            win.repaint();
                        }
                        else
                            send(dataGame);
                    }
                }
            }
        }).start();
    }

    public void cmd(String msg) {
        try {
            if (msg.charAt(0) == '/') {
                if (msg.substring(0).equals("/quit")) {
                    if (server == 1) {
                        int res = JOptionPane.showConfirmDialog(null, "You're hosting the server, if your quitting everyone will be disconnected.\nAre you sure you want to be disconnected?", "Confirm", JOptionPane.YES_NO_OPTION);
                        if (res == JOptionPane.YES_OPTION) {
                            closeAll(socket, bufferedReader, bufferedWriter);
                        }
                    }
                    else {
                        int res = JOptionPane.showConfirmDialog(null, "Are you sure you want to be disconnected?", "Confirm", JOptionPane.YES_NO_OPTION);
                        if (res == JOptionPane.YES_OPTION) {
                            System.exit(0);
                        }
                    }
                }
                else if (msg.contains(" ") && checkUser(msg.split(" ", 2)[0]) && !msg.split(" ", 2)[0].substring(1).equals(clientUsername)) {
                    send(getColor() + "µ" + clientUsername + ": " + msg);
                    setLabel("to " + msg.substring(1, msg.indexOf(' ')) + ": " + msg.substring(msg.indexOf(' ')), color);
                }
                else if (msg.substring(0).equals("/color")) {
                    this.color = getRGB();
                    message.setForeground(color);
                    setLabel("You changed your color", color);
                    setChangedColor(clientUsername, color);
                    send(getColor() + "µ" + clientUsername + " changed their color");
                }
                else
                    setLabel("You probably misswrite your command. Please try again", Color.RED);
            }
            else
                send(getColor() + "µ" + clientUsername + ": " + msg);
        } catch (Exception e) { 
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void displayMsg() {
        GridBagConstraints cstr = new GridBagConstraints();
        cstr.insets = new Insets(0, 10, 10, 0);
        cstr.gridy = gridy;
        cstr.weightx = 1;
        cstr.anchor = GridBagConstraints.WEST;
        pnmsg[0].add(listm.get(gridy), cstr);
        gridy++;
        pnmsg[0].revalidate();
        pnmsg[0].repaint();
    }

    public void displayConnected() {
        connectMenu.removeAll();
        for (int i = 0; i < listofUser.size(); i++) {
            clientItem = new JMenuItem();
            clientItem.setText(listofUser.get(i));
            clientItem.setIcon(co);
            connectMenu.add(clientItem);
        }
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

    public String getUsername(String str) {
        String tmp = JOptionPane.showInputDialog(str); 
        while (!tmp.matches("^[a-zA-Z0-9]+$")) {
            tmp = JOptionPane.showInputDialog("The username must contain only letters and numbers");
        }
        send(getColor() + "µ" + tmp);
        return tmp;
    }

    public void setFirst(int server, String str) {
        if (server == 1)
            setLabel(str.split("µ")[1], color);
        else if (str.contains("µ") && str.split("µ")[0].equals("/tic="))
            tic = Integer.parseInt(str.split("µ")[1]);
        else {            
            String[] tmp1 = str.split("//");
            for (int i = 1; i < tmp1.length; i++) {
                String[] tmp2 = tmp1[i].split("µ", 2);
                clientColor = new Color(Integer.parseInt(tmp2[0].split(" ")[0]), Integer.parseInt(tmp2[0].split(" ")[1]), Integer.parseInt(tmp2[0].split(" ")[2]));
                if (tmp2[1].split(" ", 2)[1].equals("has entered TicTacToe game"))
                    versus = tmp2[1].split(" ", 2)[0];
                setLabel(tmp2[1], clientColor);
            }
        }
    }

    public void setConnected() {
        send(String.join("§", listofUser));
        displayConnected();
    }

    public void setChangedColor(String user, Color color) {
        String str = "";
        for (int i = 0; i < listm.size(); i++) {
            str = listm.get(i).getText();
            if (str.substring(0, listm.get(i).getText().indexOf(" ")).equals(user + ":"))
                listm.get(i).setForeground(color);
            else if (str.equals(user + " has join the chat!"))
                listm.get(i).setForeground(color);
            else if (str.split(":", 2)[0].equals(user + " to you"))
                listm.get(i).setForeground(color);
            else if (str.substring(0, 3).equals("to "))
                listm.get(i).setForeground(color);
        }
    }

    public void setLabel(String str, Color color) {
        JLabel label = new JLabel(str);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(color);
        listm.add(label);
        displayMsg();
    }

    public void sendEntered() {
        try {
            send(getColor() + "µ" + clientUsername);
        } catch (Exception e) {
            e.printStackTrace();
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendPast() {
        String str = "(MsgHistoric=) //";
        for (int i = 0; i < listm.size(); i++) {
            if (!listm.get(i).getText().split(" ", 2)[0].equals("to") && !listm.get(i).getText().split(" ", 2)[1].split(":", 2)[0].equals("to you")) {
                if (listm.get(i).getText().split(" ", 2)[0].equals("You")) {
                    if (listm.get(i).getText().split(" ", 2)[1].charAt(0) == 'c')
                        str += listm.get(i).getForeground().getRed() + " " + listm.get(i).getForeground().getGreen() + " " + listm.get(i).getForeground().getBlue() + "µ" + listm.get(0).getText().split(" ", 2)[0] + " changed their color//";
                }
                else
                    str += listm.get(i).getForeground().getRed() + " " + listm.get(i).getForeground().getGreen() + " " + listm.get(i).getForeground().getBlue() + "µ" + listm.get(i).getText() + "//";
            }
        }
        send(str);
    }

    public void send(String msg) {
        try {
            bufferedWriter.write(msg);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (Exception e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public boolean checkUser(String str) {
        for (int i = 0; i < listofUser.size(); i++) {
            if (str.equals("/" + listofUser.get(i)) && !str.equals(clientUsername))
                return true;
        }
        return false;
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        if (server == 1)
                GetJson.changeServer('n');
        try {
            if (socket != null)
                socket.close();
            if (bufferedReader != null)
                bufferedReader.close();
            if (bufferedWriter != null)
                bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public void getFrameBack() {
        tictactoe = 0;
        pnmsg[0].setVisible(true);
        pnmsg[1].setVisible(true);
        scrollPane.setVisible(true);
        win.setResizable(true);
        send("/tic--");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnmsg) {
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
        if (e.getSource() == tictactoeItem) {
            if (tictactoe == 1) {
                getFrameBack();
                send(getColor() + "µ" + clientUsername + " has left TicTacToe game");
                TicTacToe.removeGame(win);
            }
            else if (tic < 2 && tictactoe == 0) {
                dataTicTacToe();
                tictactoe = 1;
                pnmsg[0].setVisible(false);
                pnmsg[1].setVisible(false);
                scrollPane.setVisible(false);
                send("/tic++");
                send(getColor() + "µ" + clientUsername + " has entered TicTacToe game");
                TicTacToe.setFrame(win, clientUsername, versus, tic);
            }
            else
                JOptionPane.showMessageDialog(null, "Sorry two person are playing", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
        if (e.getSource() == disconnect) {
            cmd("/quit");
        }
        if (e.getSource() == helpItemcolor) {
            JOptionPane.showMessageDialog(null, "Use it to change the color of your message", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
        if (e.getSource() == helpItemuser) {
            JOptionPane.showMessageDialog(null, "Use it to send a private message to a user who's connected.\n/user msg.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
        if (e.getSource() == helpItemquit) {
            JOptionPane.showMessageDialog(null, "Use it to quit the program.\nIf you're hosting the server a confirm action will be asked.", "Information", JOptionPane.INFORMATION_MESSAGE);
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
            else if ((message.getText().length() > 116)) 
                JOptionPane.showMessageDialog(null, "Message too long", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
}
