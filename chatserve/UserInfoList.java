package cn.xidian.chat;

public class UserInfoList {
	Node root;
	Node pointer;
	int count;
	
	public UserInfoList(){
		root=new Node();
		root.next=null;
		pointer=null;
		count=0;
	}
	
	public void add(Node n){
		pointer=root;
		
		while(pointer.next!=null){
			pointer=pointer.next;
		}
		
		pointer.next=n;
		n.next=null;
		count++;
	}
	
	public void del(Node n){
		pointer=root;
		
		while(pointer.next!=null){
			if(pointer.next==n){
				pointer.next=pointer.next.next;
				count--;
				break;
			}
		}
		
	}
	
	public int getCount(){
		return count;
	}
	
	public Node find(String username){
		
		if(count==0){
			return null;
		}
		
		pointer=root;
		
		while(pointer.next!=null){
			pointer=pointer.next;
			if(pointer.username.equalsIgnoreCase(username)){
				return pointer;
			}
		}
		return null;
	}
	
	public Node find(int index){
		
		if(count==0){
			return null;
		}
		
		if(index<0){
			return null;
		}
		
		pointer=root;
		
		int i=0;
		while(i<index+1){
			if(pointer.next!=null){
				pointer=pointer.next;
			}else{
				return null;
			}
			i++;
		}
		
		return pointer;
	}
}
























































