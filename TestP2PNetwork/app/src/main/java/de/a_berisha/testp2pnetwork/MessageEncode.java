package de.a_berisha.testp2pnetwork;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageEncode {

	/*
	 * 	Method getData with only 1 Parameter rawData returns only the 
	 * 	String between 2 curly braces
	 */
	public static String getData(String rawData){
		Matcher matcher = Pattern.compile("\\{(.*)\\}").matcher(rawData);
        String str = "";
        if(matcher.find()){
        	str = matcher.group(1);
        }
        return str;
	}
	
	/*
	 * 	Method getData with 2 Parameters, rawData and delimiter returns 
	 * 	a String Array with all Strings between 2 curly braces that sperate
	 * 	with the delimiter
	 */
	public static String[] getData(String rawData, String delimiter){
		return getData(rawData).split(delimiter);
	}
	
}
