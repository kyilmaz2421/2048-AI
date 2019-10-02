import java.util.*;
import java.lang.String;
public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "Let's take LeetCode contest";
		String q = reverseWords(s);
		System.out.print("cewe "+q);
		
	}
	
	 public static String reverseWords(String s) {
		        Stack<String> stack = new Stack<String>(); 
			        StringBuilder temp = new StringBuilder();
			        for(int i=s.length()-1;i>-1;i--){
			            if(s.charAt(i)==' '||i==0){ //is a space
			                if(i==0) temp.append(s.charAt(i)); //edge case
			                stack.add(temp.toString());
			                temp.delete(0,temp.length());
			            }else{
			            	temp.append(s.charAt(i)); //reversing the word
			            }
			        }
			        if(!stack.isEmpty()) temp.append(stack.pop());
			        while(!stack.isEmpty()){
			            temp.append(" ");
			            temp.append(stack.pop());
			        }
			        return temp.toString();

	    }


}
