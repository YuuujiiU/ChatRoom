package cn.xidian.chat;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Node {
	String username=null;
	Socket socket=null;
	ObjectOutputStream output=null;
	ObjectInputStream input=null;
	Node next=null;
}
