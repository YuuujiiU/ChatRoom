package cn.xidian.chat;

import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerReceiveThread extends Thread{
	JTextArea textarea;
	JTextField textfield;
	JComboBox combobox;
	Node client;
	UserInfoList userInfoList;
	
	public boolean isStop;
	
	public ServerReceiveThread(JTextArea textarea,
			JTextField textfield,
			JComboBox combobox,
			Node client,
			UserInfoList userInfoList){
		this.combobox=combobox;
		this.textarea=textarea;
		this.textfield=textfield;
		this.userInfoList=userInfoList;
		this.client=client;
		
		isStop=false;
	}
	
	public void run(){
		sendUserList();
		
		while(!isStop&&!client.socket.isClosed()){
			try {
				String type=(String)client.input.readObject();
				if(type.equalsIgnoreCase("聊天信息")){
					String toSomebody=(String)client.input.readObject();
					String status=(String)client.input.readObject();
					String action=(String)client.input.readObject();
					String message=(String)client.input.readObject();
					
					String msg="["+client.username+"]"+action+"对["+toSomebody+"]说\""+message+"\"\n";
					if(status.equalsIgnoreCase("悄悄话")){
						msg="<悄悄话>"+msg;
					}
					textarea.append(msg);
					if(toSomebody.equalsIgnoreCase("all")){
						sendToAll(msg);
					}
					else{
						client.output.writeObject("聊天信息");
						client.output.flush();
						client.output.writeObject(msg);
						client.output.flush();
						
						Node node=userInfoList.find(toSomebody);
						if(node!=null){
							node.output.writeObject("聊天信息");
							node.output.flush();
							node.output.writeObject(msg);
							node.output.flush();
						}
					}
				}else if(type.equalsIgnoreCase("用户下线")){
					Node node=userInfoList.find(client.username);
					
					userInfoList.del(node);
					
					String msg="用户["+client.username+"]下线\n";
					int count=userInfoList.getCount();
					
					combobox.removeAllItems();
					combobox.addItem("all");
					int i=0;
					while(i<count){
						node=userInfoList.find(i);
						if(node==null){
							i++;
							continue;
						}
						combobox.addItem(node.username);
						i++;
					}
					combobox.setSelectedIndex(0);
					
					textarea.append(msg);
					textfield.setText("在线用户"+userInfoList.getCount()+"人\n");
					sendToAll(msg);
					sendUserList();
					break;
				}
			} catch (IOException e) {
				//e.printStackTrace();
				System.out.println("连接客户端的套接字已关闭");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendToAll(String msg){
		int count=userInfoList.getCount();
		int i=0;
		while(i<count){
			Node node=userInfoList.find(i);
			if(node==null){
				i++;
				continue;
			}
			try {
				node.output.writeObject("聊天信息");
				node.output.flush();
				node.output.writeObject(msg);
				node.output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}			
			i++;
		}
	}
	
	public void sendUserList(){
		String userlist="";
		int count=userInfoList.getCount();
		
		int i=0;
		
		while(i<count){
			Node node=userInfoList.find(i);
			if(node==null){
				i++;
				continue;
			}
			userlist+=node.username;
			userlist+="\n";
			i++;
		}
		
		i=0;
		while(i<count){
			Node node=userInfoList.find(i);
			if(node==null){
				i++;
				continue;
			}
			try {
				node.output.writeObject("用户列表");
				node.output.flush();
				node.output.writeObject(userlist);
				node.output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			i++;
		}
	}
}

















































