package com.zzc;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) {
		String test = new String("abc");
		System.out.printf("%s\n",test);
		testStr(test);
		System.out.printf("%s\n",test);
	}
	
	public static void testStr(String test){
		test = "22222";
	}
}
