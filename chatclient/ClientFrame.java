package cn.xidian.chat;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

public class ClientFrame extends JFrame{

	private static final long serialVersionUID = -1016350602565972347L;
	private JComboBox combobox;
	private JTextArea textarea;
	private JTextField textfield;
	private JCheckBox checkbox;
	private JComboBox actionlist;
	private JButton login;
	private JButton logout;
	private Border border;
	private JTextField seperater;
	private JScrollPane js;
	
	Socket socket;
	ObjectOutputStream output;
	ObjectInputStream input;
	ClientReceiveThread recvThread;
	
	public ClientFrame(){
		super("client");
		Border bevelBorder=BorderFactory.createBevelBorder(BevelBorder.RAISED,
				Color.white,Color.white,Color.white,Color.white);
		Border emptyBorder=BorderFactory.createEmptyBorder(1, 1, 1, 1);
		border=BorderFactory.createCompoundBorder(bevelBorder, emptyBorder);
		
		login=new JButton("login");
		logout=new JButton("logout");
		logout.setEnabled(false);
		
		combobox=new JComboBox();
		combobox.addItem("all");
		combobox.setSelectedIndex(0);
		
		textarea=new JTextArea(20,20);
		textarea.setLineWrap(true);
		textarea.setEditable(false);
		js=new JScrollPane();
		js.getViewport().add(textarea);
		
		textfield=new JTextField("请输入您的昵称!",20);
		
		seperater=new JTextField("",20);
		seperater.setEditable(false);
		
		checkbox=new JCheckBox("悄悄话");
		checkbox.setSelected(false);
		
		actionlist=new JComboBox();
		actionlist.addItem("微笑着");
		actionlist.addItem("生气地");
		actionlist.addItem("小心地");
		actionlist.setSelectedIndex(0);
		
		login.setBorder(border);
		login.setBounds(30,30, 90, 30);
		logout.setBorder(border);
		logout.setBounds(150, 30, 90, 30);
		
		seperater.setBounds(30, 75, 300, 20);
		//textarea.setBounds(30, 110, 300, 300);
		js.setBounds(30, 110, 300, 300);
		combobox.setBounds(30, 425, 90, 30);
		actionlist.setBounds(150, 425, 90, 30);
		
		checkbox.setBounds(270, 425, 90, 30);
		textfield.setBounds(30, 470, 300, 20);
		
		Container c=getContentPane();
		c.setLayout(null);
		
		c.add(login, null);
		c.add(logout, null);
		c.add(seperater, null);
		c.add(js, null);
		c.add(combobox, null);
		c.add(actionlist, null);
		c.add(checkbox, null);
		c.add(textfield, null);
		
		login.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				LogIn();
			}
		});
		
		logout.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				LogOut();
			}
		});
		
		textfield.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				SendMessage();
			}
		});
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				LogOut();
				
				System.exit(0);
			}
		});
		
		setSize(370,570);
		setVisible(true);
	}
	
	public void LogIn(){
		try {
			socket =new Socket("127.0.0.1",8000);
			output=new ObjectOutputStream(socket.getOutputStream());
			output.flush();
			input=new ObjectInputStream(socket.getInputStream());
			output.writeObject(textfield.getText());
			output.flush();
			recvThread=new ClientReceiveThread(socket,output,input,combobox,textarea);
			recvThread.start();
			
			login.setEnabled(false);
			logout.setEnabled(true);
			textfield.setText("请输入聊天信息！");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public void LogOut(){
		login.setEnabled(true);
		logout.setEnabled(false);
		textfield.setText("请输入您的昵称！");
		/*if(socket.isClosed()){
			return;
		}*/
		combobox.removeAllItems();
		combobox.addItem("all");
		try {
			if(socket.isClosed()){
				return;
			}
			output.writeObject("用户下线");
			output.flush();
			input.close();
			output.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			//System.out.println("还没有初始化！");
		}
	}
	
	public void SendMessage(){
		String toSomebody=combobox.getSelectedItem().toString();
		String status="";
		if(checkbox.isSelected()){
			status="悄悄话";
		}
		
		String action=actionlist.getSelectedItem().toString();
		String message=textfield.getText();
		
		if(socket.isClosed()){
			return;
		}
		
		try {
			output.writeObject("聊天信息");
			output.flush();
			output.writeObject(toSomebody);
			output.flush();
			output.writeObject(status);
			output.flush();
			output.writeObject(action);
			output.flush();
			output.writeObject(message);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}


































































