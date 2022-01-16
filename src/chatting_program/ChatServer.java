package chatting_program;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer {
	ServerSocket serverSocket;
	Socket socket;
	Vector threadVector = new Vector();
	Vector nameVector = new Vector();
	StringBuffer stringbuffer = new StringBuffer();

	public void serverStart() {
		try {
			serverSocket = new ServerSocket(8787);
			System.out.println("���� ����");
			while (true) {
				socket = serverSocket.accept();
				System.out.println(socket.getInetAddress() + " ����");
				ServerThread serverThread = new ServerThread(this, socket);
				threadVector.addElement(serverThread);
				serverThread.start();
			}
		} catch (Exception ex) {
			System.out.println("������������");
		}
	}


	public void sentToClient(String msg) {
		for (int i = 0; i < threadVector.size(); i++) {
			ServerThread thread = (ServerThread) threadVector.elementAt(i);
			thread.sendMsg(msg);
		}
	}

	//Client���� ������ ������Ʈ
	public void addClientName(ServerThread pt) {

		for (int i = 0; i < nameVector.size(); i++) {
			stringbuffer.append("/a");
			stringbuffer.append(nameVector.get(i));
			pt.sendMsg(stringbuffer.toString());
			stringbuffer.setLength(0);
		}
	}

	public void removeClientName(ServerThread pt) {
		for (int i = 0; i < nameVector.size(); i++) {
			stringbuffer.append("/r");
			stringbuffer.append(nameVector.get(i));
			pt.sendMsg(stringbuffer.toString());
			stringbuffer.setLength(0);
		}
	}

	public static void main(String[] args) {
		ChatServer ser = new ChatServer();
		ser.serverStart();
	}

	public class ServerThread extends Thread {

		Socket socket;
		BufferedReader in;
		OutputStream out;
		ChatServer server;
		String str;
		String name;

		public ServerThread(ChatServer s3, Socket s) {
			this.server = s3;
			this.socket = s;
		}

		public void run() {

			try {
				InputStreamReader isr = new InputStreamReader(socket.getInputStream());
				in = new BufferedReader(isr);
				out = socket.getOutputStream();
				PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
				name = in.readLine();

				server.sentToClient("/a" + name);
				server.addClientName(this);
				nameVector.add(name);

				server.sentToClient(">>> " + name + " ����");

				while (true) {
					str = in.readLine();
					System.out.println(str);
					if (str == null)
						return;
					if (str.charAt(1) == 's') {
						String receiverName = str.substring(2, str.indexOf('-')).trim();// �ӼӸ� ������� �̸�
						for (int i = 0; i < threadVector.size(); i++) {
							ServerThread thread = (ServerThread) threadVector.elementAt(i);
							if (receiverName.equals(thread.name)) {
								thread.sendMsg("���� From " + name + " - " + str.substring(str.indexOf('-') + 1));
								break;
							}
						}
					} else {
						server.sentToClient("<" + name + "> " + str);
					}
				} // while end
			} catch (Exception ex) {
				server.sentToClient("/d" + name);
				server.removeClientName(this);
				nameVector.remove(name);
				server.sentToClient("<<< " + name + " ����");
				threadVector.removeElement(this);
				System.out.println(socket.getInetAddress() + "�� ����");
			}
		}
		public void sendMsg(String st) {
			try {
				PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
				pw.println(st);
			} catch (Exception ex) {
			}
		}

	}

}
