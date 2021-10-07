package cn.xidian.chat;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

public class ServerFrame extends JFrame{

	private static final long serialVersionUID = -2032298873409953908L;
	private JButton jbStartServer;
	private JButton jbStopServer;
	private Border border;
	private ServerSocket serverSocket;
	private JComboBox combobox;
	private JTextArea textarea;
	private JTextField textfield;
	private JTextField systemMsg;
	private UserInfoList userInfoList;
	private JScrollPane js;
	
	private ServerListenThread listenThread;
	
	public ServerFrame(){
		super("ServerFrame");
		init();
		setSize(370,570);
		setVisible(true);
	}
	
	public void init(){
		Border bevelBorder=BorderFactory.createBevelBorder(BevelBorder.RAISED,
				Color.white,Color.white,Color.white,Color.white);
		Border emptyBorder=BorderFactory.createEmptyBorder(1, 1, 1, 1);
		border=BorderFactory.createCompoundBorder(bevelBorder, emptyBorder);
		jbStartServer=new JButton("startServer");
		jbStopServer=new JButton("stopServer");
		jbStopServer.setEnabled(false);
		
		jbStartServer.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				startServer();
			}
		});
		
		jbStopServer.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				stopServer();
			}
		});
		
		combobox=new JComboBox();
		combobox.insertItemAt("all", 0);
		combobox.setSelectedIndex(0);
		
		textarea=new JTextArea(20,20);
		//js.setEditable(false);
		textarea.setEditable(false);
		textarea.setLineWrap(true);
		js=new JScrollPane();
		js.getViewport().add(textarea);
		
		textfield=new JTextField(40);
		textfield.setEditable(false);
		
		systemMsg=new JTextField(40);
		systemMsg.setEnabled(false);
		
		systemMsg.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sendSystemMessage();
			}
		});
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				stopServer();
				System.exit(0);
			}
		});
		
		Container c=getContentPane();
		c.setLayout(null);
		
		jbStartServer.setBorder(border);
		jbStartServer.setBounds(30, 30, 90, 30);
		jbStopServer.setBorder(border);
		jbStopServer.setBounds(150, 30, 90, 30);
		
		textfield.setBounds(30,75,300,20);
		//textarea.setBounds(30,110,300,300);
		js.setBounds(30,110,300,300);
		combobox.setBounds(30, 425, 90, 30);
		systemMsg.setBounds(30, 470, 300, 20);
		
		c.add(jbStartServer, null);
		c.add(jbStopServer, null);
		c.add(textfield, null);
		c.add(js, null);
		c.add(combobox, null);
		c.add(systemMsg, null);
	}
	
	public void startServer(){
		try {
			serverSocket=new ServerSocket(8000,10);
			textarea.append("server start at 8000 port...\n");
			jbStartServer.setEnabled(false);
			jbStopServer.setEnabled(true);
			systemMsg.setEnabled(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		userInfoList=new UserInfoList();
		
		listenThread=new ServerListenThread(serverSocket,combobox,textarea,textfield,userInfoList);
		listenThread.start();
	}
	
	public void stopServer(){
		try {
			sendStopToAll();
			listenThread.isStop=true;
			serverSocket.close();
			 
			int count=userInfoList.getCount();
			
			int i=0;
			while(i<count){
				Node node=userInfoList.find(i);
				node.input.close();
				node.output.close();
				node.socket.close();
				i++;
			}
			jbStartServer.setEnabled(true);
			jbStopServer.setEnabled(false);
			systemMsg.setEnabled(false);
			
			combobox.removeAllItems();
			combobox.addItem("all");
			textfield.setText("");
		}catch(SocketException e){
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}catch(Exception e){
			//System.out.println("还没有初始化！");
		}
	}
	
	public void sendStopToAll(){
		int count=userInfoList.getCount();
		
		int i=0;
		
		while(i<count){
			Node node=userInfoList.find(i);
			if(node==null){
				i++;
				continue;
			}
			try {
				node.output.writeObject("服务关闭");
				node.output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			i++;
		}
	}
	
	public void sendSystemMessage(){
		String toSomebody=combobox.getSelectedItem().toString();
		String message=systemMsg.getText();
		if(toSomebody.equalsIgnoreCase("all")){
			sendMsgToAll(message);
		}else{
			Node node=userInfoList.find(toSomebody);
			try {
				node.output.writeObject("系统信息");
				node.output.flush();
				node.output.writeObject(message);
				node.output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}
	
	public void sendMsgToAll(String msg){
		int count=userInfoList.getCount();
		int i=0;
		while(i<count){
			Node node=userInfoList.find(i);
			if(node==null){
				i++;
				continue;
			}
			try {
				node.output.writeObject("系统信息");
				node.output.flush();
				node.output.writeObject(msg);
				node.output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			i++;
		}
	}
}





























































