//Program to convert Roman number string to integer.

import java.util.Scanner;

public class romanToInt {
	
	public static void main(String args []){

	
		System.out.println("Enter roman numerals for conversion to Integer.");
		Scanner input = new Scanner (System.in);
		String romanNumeral = input.nextLine();
		
		// if(isInvalidEntry(romanNumeral))
		// 	convertRomanToInt(romanNumeral);

		char p = 'Z';

		int p1 = Character.getNumericValue(p);

		System.out.println("p: " + p1);
	
		

	}


	// public static int convertRomanToInt(String roman){
	// 	int myString =0;
	// 	boolean flag = false;

	// 	for (int i = 0; i < roman.length(); i++){

	// 		if ((i < roman.length() - 1) && (romanToInt(roman.charAt(i)) < romanToInt(roman.charAt(i+1)))){
	// 			myString += romanToInt(roman.charAt(i+1)) - romanToInt(roman.charAt(i));
	// 			flag = true;
	// 		 	continue;}

	// 		if (flag == true){
	// 		 	flag = false;
	// 		 	continue;

	// 		}
	// 		else{
	// 			myString += romanToInt(roman.charAt(i));
	// 		}


					
	// 	}

	// 	System.out.println("Roman numeral: " + roman);
	// 	System.out.println("Integer: " + myString);
	// 	return myString;
	// } 


	


	public static int convertRomanToInt(String roman){
		int myString =0;
		boolean flag = false;
		
		for (int i = 0; i < roman.length(); i++){

			
			
			if (i < roman.length() - 1 ){

			 	if (roman.charAt(i) == 'I' && (roman.charAt(i+1) == 'V' || roman.charAt(i+1) == 'X')){
				 	flag = true;
				 	
				 	myString += romanToInt(roman.charAt(i+1)) - romanToInt(roman.charAt(i));
				 	System.out.println(myString);

				 	continue;

				}

			

			 	if (roman.charAt(i) == 'X' && (roman.charAt(i+1) == 'L' || roman.charAt(i+1) == 'C')){
			 		flag = true;
			 	
				 	myString += romanToInt(roman.charAt(i+1)) - romanToInt(roman.charAt(i));


				 	continue;

			 	}

			 	if (roman.charAt(i) == 'C' && (roman.charAt(i+1) == 'D' || roman.charAt(i+1) == 'M')){
			 		flag = true;
			 	
			 		
			 		myString += romanToInt(roman.charAt(i+1)) - romanToInt(roman.charAt(i));


			 		continue;

			 	}

				if (flag == true){
				 	System.out.println("Fourth");
				 	flag = false;
				 	continue;

				}
				else{
					System.out.println("Fifth");
					myString += romanToInt(roman.charAt(i));
					System.out.println(myString);
				}
			
			} if (i == (roman.length()-1) && flag == false ){
					System.out.println("Sixth");
					myString += romanToInt(roman.charAt(i));
					System.out.println(myString);
				}


			

		}
		System.out.println("Roman to integer is: " + myString);
		return myString;
	} 

	public static int romanToInt(char c) {
    		switch (c) {
       			case 'I': return 1;
        		case 'V': return 5;
       			case 'X': return 10;
        		case 'L': return 50;
        		case 'C': return 100;
        		case 'D': return 500;
        		case 'M': return 1000;
        		default: return 0;
    		}
	}


	public static boolean isInvalidEntry(String input){

		for (int i = 0; i < input.length() -1; i++){

			if (input.length() >=3){
				if ((input.charAt(i) == 'V' && input.charAt(i+1) == 'I' && input.charAt(i+2) == 'X' )){
				   	System.out.println("Invalid Roman numerals entry");
					return false;
				}
			}
		}


		// 	if ((input.charAt(i) == 'V' && (input.charAt(i+1) == 'X') || (input.charAt(i) == 'L' && input.charAt(i+1) == 'C') ||
		// 		input.charAt(i) == 'D' && input.charAt(i+1) == 'M') || (input.charAt(i) == 'V' && (input.charAt(i+1) == 'X')){
					

					
		// 	}


		// }
		return true;


	}


}


