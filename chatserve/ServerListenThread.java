package cn.xidian.chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;

import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerListenThread extends Thread{
	ServerSocket server;
	
	JComboBox combobox;
	JTextArea textarea;
	JTextField textfield;
	UserInfoList userInfoList;
	
	Node client;
	ServerReceiveThread recvThread;
	
	public boolean isStop;
	
	public ServerListenThread(ServerSocket server,
			JComboBox combobox,
			JTextArea textarea,
			JTextField textfield,
			UserInfoList userInfoList){
		this.server=server;
		this.combobox=combobox;
		this.textarea=textarea;
		this.textfield=textfield;
		this.userInfoList=userInfoList;
		
		isStop=false;
	}
	
	public void run(){
		while(!isStop&&!server.isClosed()){
			client=new Node();
			try {
				client.socket=server.accept();
				client.output=new ObjectOutputStream(client.socket.getOutputStream());
				client.output.flush();
				client.input=new ObjectInputStream(client.socket.getInputStream());
				client.username=(String)client.input.readObject();
				combobox.addItem(client.username);
				userInfoList.add(client);
				textarea.append("user["+client.username+"]����"+"\n");
				textfield.setText("�����û�"+userInfoList.getCount()+"��\n");
				
				recvThread=new ServerReceiveThread(textarea,textfield,combobox,client,userInfoList);
				recvThread.start();
			} catch (IOException e) {
				//System.out.println("�����쳣�������׽����Ѿ��رգ������쳣��");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 
		}
	}
}


















































