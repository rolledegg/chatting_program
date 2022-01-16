package chatting_program;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;


public class ChatClient implements ActionListener, FocusListener {

	Socket socket = null;
	PrintWriter printWriter = null;

	// =================================For GUI==============================================//
	public Frame frameLogin = new Frame("Login");
	public Frame frameChatting = new Frame("Chatting Program");
	private Font font1 = new Font("SanSerif", Font.BOLD, 15);
	private Font font2 = new Font("SanSerif", Font.BOLD, 20);
	private Font font3 = new Font("바탕", Font.ITALIC, 30);

	// 이름 입력
	GridBagLayout gbLayout = new GridBagLayout();
	private Label labelUser = new Label("User Name", Label.CENTER);
	private Label labelCenter = new Label("");
	public static JTextField textFieldName = new JTextField(10);
	public JButton btnConnect = new RoundedButton("접속");
	public JButton btnDisconnect = new RoundedButton("종료");

	// 채팅 프로그램
	public static String host, port, username, str;
	public Label labTop = new Label(" ");
	public Label labPerNum = new Label("      Connected :");
	public int count = 0;
	public Label labPerCount = new Label(String.valueOf(count));
	public static JTextField textFieldMsg = new JTextField("메세지를 입력해주세요", 28);
	public static JTextArea textArea = new JTextArea();
	public static JScrollPane scrollPane = new JScrollPane(textArea);
	public JButton btnsendMsg = new RoundedButton("▲");
	public JButton btnDisconnect2 = new RoundedButton("종료");
	public List listClient = new List(20);
	CheckboxGroup checkBoxGroup = new CheckboxGroup();
	Checkbox checkChatSecret = new Checkbox("Secret", checkBoxGroup, false);
	Checkbox checkChatEvery = new Checkbox("All", checkBoxGroup, true);

	public static Panel pMain = new Panel(new BorderLayout(2, 2));
	public static Panel pLeft = new Panel(new BorderLayout(2, 2));
	public static Panel pLeftNorth = new Panel(new BorderLayout());
	public static Panel pLeftCenter = new Panel(new BorderLayout(2, 2));
	public static Panel pLeftSouth = new Panel(new BorderLayout(2, 2));
	public static Panel pRight = new Panel(new BorderLayout(2, 2));
	public static Panel pRightNorth = new Panel(new BorderLayout());
	public static Panel pRightCenter = new Panel(new BorderLayout());
	public static Panel pRightSouth = new Panel(new BorderLayout());

	public ChatClient() {

	}

	public void launchLoginFrame() {
		frameLogin.setTitle("Login");
		frameLogin.setLayout(gbLayout);
		frameLogin.setBackground(new Color(121, 171, 255));


		labelUser.setFont(font2);
		labelUser.setForeground(Color.white);
	
		gbinsert(labelUser, 4, 6, 1, 1);
		gbinsert(textFieldName, 3, 7, 3, 1);
		gbinsert(new Label(""), 3, 8, 3, 1);
		gbinsert(btnConnect, 3, 9, 1, 1);
		gbinsert(new Label(""), 4, 9, 1, 1);
		gbinsert(btnDisconnect, 5, 9, 1, 1);
		
		textFieldName.setBackground(Color.white);
		textFieldName.setForeground(new Color(121, 171, 255));
		textFieldName.requestFocus();
		textFieldName.setBorder(
				BorderFactory.createCompoundBorder(new CustomeBorder(), new EmptyBorder(new Insets(15, 25, 15, 25))));


		frameLogin.pack();

		frameLogin.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(1);
			}
		});

		frameLogin.setBounds(200, 300, 340, 340);// 위치,크기
		frameLogin.setResizable(false);
		frameLogin.setVisible(true);

		btnConnect.addActionListener(this);
		btnDisconnect.addActionListener(this);
	}

	public void launchChattingFrame() {
		textFieldMsg.setBackground(Color.white);
		textFieldMsg.setForeground(new Color(121, 171, 255));
		//textField.requestFocus();
		textFieldMsg.setBorder(
				BorderFactory.createCompoundBorder(new CustomeBorder(), new EmptyBorder(new Insets(15, 25, 15, 25))));
		
		
		scrollPane.setBorder(null);
		textArea.setForeground(new Color(121, 171, 255));
		textArea.setBorder(
				BorderFactory.createCompoundBorder(new CustomeBorder(), new EmptyBorder(new Insets(15, 25, 15, 25))));
		
		labPerNum.setFont(font1);
		labPerCount.setFont(font1);
		
		//오른쪽		
		pRightNorth.add("North", new Label(" "));
		pRightNorth.add("West", labPerNum);
		pRightNorth.add("Center", labPerCount);
		pRight.add("North", pRightNorth);

		Panel chkpan = new Panel();
		chkpan.add(checkChatSecret);
		chkpan.add(checkChatEvery);
		pRightCenter.add("North", chkpan);
		pRightCenter.add("South", new Label(" "));
		pRightCenter.add("East", new Label(" "));
		pRightCenter.add("West", new Label(" "));
		pRightCenter.add("Center", listClient);
		pRight.add("Center", pRightCenter);
		// 목록

		pRightSouth.add("East", new Label(" "));
		pRightSouth.add("West", new Label(" "));
		pRightSouth.add("South", new Label(" "));
		pRightSouth.add("Center", btnDisconnect2);
		pRight.add("South", pRightSouth);
		pRight.setBackground(Color.white);
		pMain.add("East", pRight);
		// 

		//욎쪽
		listClient.add(" *** 현재 접속자 ***");
		pLeftNorth.add("North", new Label(" "));
		pLeftNorth.add("Center", labTop);
		pLeft.add("North", pLeftNorth);

		pLeftCenter.add("North", new Label(" "));
		pLeftCenter.add("South", new Label(" "));
		pLeftCenter.add("East", new Label(" "));
		pLeftCenter.add("West", new Label(" "));
		pLeftCenter.add("Center", scrollPane);
		pLeft.add("Center", pLeftCenter);

		pLeftSouth.add("West", new Label(" "));
		pLeftSouth.add("Center", textFieldMsg);
		pLeftSouth.add("East", btnsendMsg);
		pLeftSouth.add("South", new Label(" "));
		pLeft.add("South", pLeftSouth);
		pLeft.setBackground(new Color(121, 171, 255));
		pMain.add("West", pLeft);
		
		//중앙
		labelCenter.setBackground(new Color(121, 171, 255));		
		pMain.add("Center",labelCenter);		
		pMain.setBackground(new Color(121, 171, 255));	

		frameChatting.add(pMain);

		textFieldMsg.addActionListener(this);
		textFieldMsg.addFocusListener(this);
		btnsendMsg.addActionListener(this);
		btnDisconnect2.addActionListener(this);

		frameChatting.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(1);
			}
		});

		frameChatting.setResizable(false);
		frameChatting.setBounds(100, 200, 630, 500);
		frameChatting.setVisible(true);
	}// 채팅창 부분 add

	public void gbinsert(Component c, int x, int y, int w, int h) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		gbc.ipadx = 10;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.insets = new Insets(2, 2, 0, 4);
		gbLayout.setConstraints(c, gbc);
		frameLogin.add(c);
	}
	
	class CustomeBorder extends AbstractBorder {
		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			// TODO Auto-generated method stubs
			super.paintBorder(c, g, x, y, width, height);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(new BasicStroke(12));
			g2d.drawRoundRect(x, y, width - 1, height - 1, 25, 25);
		}
	}
	
	public class RoundedButton extends JButton {
		public RoundedButton() {
			super();
			decorate();
		}

		public RoundedButton(String text) {
			super(text);
			decorate();
		}

		public RoundedButton(Action action) {
			super(action);
			decorate();
		}

		public RoundedButton(Icon icon) {
			super(icon);
			decorate();
		}

		public RoundedButton(String text, Icon icon) {
			super(text, icon);
			decorate();
		}

		protected void decorate() {
			setBorderPainted(false);
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Color c = new Color(255, 246, 18); // 배경색 결정
			Color o = new Color(0, 0, 0); // 글자색 결정
			int width = getWidth();
			int height = getHeight();
			Graphics2D graphics = (Graphics2D) g;
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if (getModel().isArmed()) {
				graphics.setColor(c.darker());
			} else if (getModel().isRollover()) {
				graphics.setColor(c.brighter());
			} else {
				graphics.setColor(c);
			}
			graphics.fillRoundRect(0, 0, width, height, 30, 30);
			FontMetrics fontMetrics = graphics.getFontMetrics();
			Rectangle stringBounds = fontMetrics.getStringBounds(this.getText(), graphics).getBounds();
			int textX = (width - stringBounds.width) / 2;
			int textY = (height - stringBounds.height) / 2 + fontMetrics.getAscent();
			graphics.setColor(o);
			graphics.setFont(getFont());
			graphics.drawString(getText(), textX, textY);
			graphics.dispose();
			super.paintComponent(g);
		}
	}
	// =================================GUI 완성==============================================//

	// =================================For EVENT==============================================//
	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		if (ae.getSource() == btnConnect) {
			frameLogin.setVisible(false);
			frameLogin.dispose();

			host = "172.30.1.5";
			port = "8787";
			username = textFieldName.getText();

			launchChattingFrame();
			labTop.setFont(font2);
			labTop.setForeground(Color.white);
			labTop.setText("    [ 2021-2 ] Chatting Program User: " + username + " ");
			connect(host, port, username);
		}
		if (ae.getSource() == btnsendMsg || ae.getSource() == textFieldMsg) {
			sendToServer();
			textFieldMsg.setText("");
			textFieldMsg.requestFocus();
		}

		if (ae.getSource() == btnDisconnect2 || ae.getSource() == btnDisconnect) {
			System.exit(1);
		}
	}
	
	// =================================For CHatting==============================================//
	public void connect(String host, String port, String username) {
		try {
			socket = new Socket(host, Integer.parseInt(port));
			System.out.println("채팅방에 접속하였습니다.");
			
			ClientThread clientThred = new ClientThread(socket, this);
			clientThred.start();
			
			printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
			printWriter.println(username);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	public void sendToServer() {
		try {
			String msg = textFieldMsg.getText();
			if (checkChatEvery.getState() == true) {// to all
				printWriter.println(msg);
			} else {// secret
				try {
					String name = listClient.getSelectedItem();
					printWriter.println(("/s" + name + "-" + msg));
					textArea.append("▶▶ Only to " + name + " - " + msg + "\n");
				} catch (Exception ex) {
					textArea.append(ex.getMessage());
				}
			}
		} catch (Exception ex) {
		}
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		textFieldMsg.setText("");
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
	}


	// 리스트에 이름 추가 /삭제
	public void addName(String user) {
		System.out.println("*** " + user + " 입장 ***");
		listClient.add(user);
	}

	public void removeName(String user) {
		System.out.println("*** " + user + " 퇴장 ***");
		listClient.remove(user);
	}



//================================	main() ========================================//
	public static void main(String[] args) {
		ChatClient client = new ChatClient();
		client.launchLoginFrame();
	}

}

class ClientThread extends Thread {
	Socket ssocketc = null;
	BufferedReader br = null;
	String str;
	String user;
	ChatClient client;

	public ClientThread(Socket socket, ChatClient client) {
		this.ssocketc = socket;
		this.client = client;
	}

	@Override
	public void run() {
		try {
			br = new BufferedReader(new InputStreamReader(ssocketc.getInputStream()));// 서버에서 넘어온값 받기
			while (true) {
				str = br.readLine();
				System.out.println(str);
				if (str.indexOf("/a") == 0) {
					user = str.substring(2);
					System.out.println("123 :" + user);
					client.count++;
					client.labPerCount.setText(String.valueOf(client.count));
					client.addName(user);

				} else if (str.indexOf("/d") == 0) {
					user = str.substring(2);
					client.count--;
					client.labPerCount.setText(String.valueOf(client.count));
					client.removeName(user);

				} else {
					client.textArea.append(str + "\n");
				}
			}
		} catch (Exception ex) {
			System.out.println("에러");
		}
	}

}
