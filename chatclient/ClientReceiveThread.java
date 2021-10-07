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
				if(type.equalsIgnoreCase("ϵͳ��Ϣ")){
					String sysmsg=(String)input.readObject();
					textarea.append("ϵͳ��Ϣ��"+sysmsg);
				}else if(type.equalsIgnoreCase("����ر�")){
					output.close();
					input.close();
					socket.close();
					textarea.append("�������ѹرգ�\n");
					break;
				}else if(type.equalsIgnoreCase("������Ϣ")){
					String message=(String)input.readObject();
					textarea.append(message);
				}else if(type.equalsIgnoreCase("�û��б�")){
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
				System.out.println("�׽����Ѿ��رգ������쳣��");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}



























































