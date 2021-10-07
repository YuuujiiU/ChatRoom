package cn.xidian.chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JComboBox;
import javax.swing.JTextArea;

public class ClientReceiveThread extends Thread{
	private JComboBox combobox;
	private JTextArea textarea;
	Socket socket;
	ObjectOutputStream output;
	ObjectInputStream input;
	
	public ClientReceiveThread(Socket socket,
			ObjectOutputStream output,
			ObjectInputStream input,
			JComboBox combobox,
			JTextArea textarea){
		this.socket=socket;
		this.output=output;
		this.input=input;
		this.combobox=combobox;
		this.textarea=textarea;
	}
	
	public void run(){
		while(!socket.isClosed()){
			try {
				String type=(String)input.readObject();
				if(type.equalsIgnoreCase("系统信息")){
					String sysmsg=(String)input.readObject();
					textarea.append("系统信息："+sysmsg);
				}else if(type.equalsIgnoreCase("服务关闭")){
					output.close();
					input.close();
					socket.close();
					textarea.append("服务器已关闭！\n");
					break;
				}else if(type.equalsIgnoreCase("聊天信息")){
					String message=(String)input.readObject();
					textarea.append(message);
				}else if(type.equalsIgnoreCase("用户列表")){
					String userlist=(String)input.readObject();
					String[] usernames=userlist.split("\n");
					combobox.removeAllItems();
					
					int i=0;
					combobox.addItem("all");
					while(i<usernames.length){
						combobox.addItem(usernames[i]);
						i++;
					}
					combobox.setSelectedIndex(0);
				}
			} catch (IOException e) {
				//e.printStackTrace();
				System.out.println("套接字已经关闭！或别的异常！");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}



























































