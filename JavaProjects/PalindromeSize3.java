//Print all palindromes of size greater than equal to 3 of a given string. (DP)



public class PalindromeSize3 {

	
	public static void main(String[] args){
	        
   	 	String str = "cabbaabbacasdasdsdsdsdassadsadasdasadsadasda";
    		System.out.println("String: " + str);
    		findPalindromes(str, 3);
	}


	public static void findPalindromes(String str, int min){


		if (str.length() < 3) { System.out.println("Returning");return;}

		for (int i = 0; i < (str.length() - 2); i++){
			String checkStr = str.substring (0, i+3);
			//System.out.println("Stringsssss: " + checkStr);
			if ((checkStr.length() == 3) && checkStr.charAt(0) == checkStr.charAt(2))
				System.out.println("Palindrome ******: "+ checkStr);		
			else{

			    if (checkStr.charAt(0) == checkStr.charAt(checkStr.length()- 1)){
					//System.out.println("D:" + checkStr);
					if (checkIfPalindrome (checkStr))
						System.out.println("Palindrome ******: "+ checkStr);	
					
			    }
			    else{
				
				

			    }
			
			}
		
			
       			



		}
		
		//System.out.println("recurse : " + str.substring (1, str.length()));
		findPalindromes(str.substring (1, str.length()), min);






	}


	public static boolean checkIfPalindrome(String cstr){
		
		boolean flag = true; 
		for (int j = 1; j < cstr.length ()-1; j++){
			if (cstr.charAt(j) != cstr.charAt(cstr.length()- (j+1)))
				flag = false;
			
		}
		return flag;


	}













}
