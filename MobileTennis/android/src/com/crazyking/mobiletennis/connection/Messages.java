package com.crazyking.mobiletennis.connection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Adrian Berisha on 07.04.2017.
 */

/**
 *  Syntax for the Messages:
 *      {key1:"value1",key2:"value2",key3:"value3",...}
 */
public class Messages {

    /**
     *
     * @param rawData   A data string with the correct syntax.
     *                  An example with the correct syntax at the top.
     * @return          Returns the command string which will be execute
     */
    public static String getCommand(String rawData){
        HashMap<String, String> map = getDataMap(rawData);
        if(map == null){
            return "";
        }else if(map.isEmpty()){
            return "";
        }
        return map.get(Constants.CMD.KEY);
    }

    /**
     *
     * @param rawData   A data string with the correct syntax
     *                  An example with the correct syntax at the top
     * @param key       A string key to get a value from the message
     * @return          Returns the value, or a empty string, if the key is not found
     */
    public static String getValue(String rawData, String key){
        HashMap<String,String> map = getDataMap(rawData);
        if(map == null){
            return "";
        }else if(map.isEmpty()){
            return "";
        }
        return map.get(key);
    }

    /**
     * Generate a Data String with only the command
     *
     * @param cmd   A data string with the correct syntax
     *              An example with the correct syntax at the top.
     * @return      Returns a complete command Message
     */
    public static String getDataStr(String cmd){
        return getDataStr(cmd, (HashMap<String, String>) null);
    }

    /**
     *
     * @param cmd   The command string which will be execute
     * @param data  A HashMap with all data information for the command string
     * @return      Returns a correct command string
     */
    public static String getDataStr(String cmd, HashMap<String, String> data){
        if(data == null)
            data = new HashMap<>();
        data.put(Constants.CMD.KEY, cmd);
        return getDataStr(data);
    }
    /**
     * Syntax for the data string: {key1:"value1",key2:"value2",key3:"value3",...}
     * This string representation generate the class Messages
     *
     * @param rawData   A data string in the right syntax as above
     * @return          Returns a HashMap with all string pairs that found in the data string
     */
    public static HashMap<String, String> getDataMap(String rawData){
        HashMap<String, String> data = new HashMap<>();

        // Regex to test if String is correct
        String regex = "(?:\\{(?:(\\w+):\"((?:\\w*[\\s]*[-.,]*)*)\"[,|\\}])*)?";

        // Regex to get all Keys and Values
        String regexMatcher = "(\\w+):\"((?:\\w*[\\s]*[-.,]*)*)\"";

        if(rawData.matches(regex)){
            Matcher m = Pattern.compile(regexMatcher).matcher(rawData);
            while(m.find()){
                data.put(m.group(1), m.group(2));
            }
            return data;

        }else {
            return data;
        }
    }

    /**
     *
     * @param data  A HashMap with all string pairs for the data string
     * @return      Returns a correct command string
     */
    public static String getDataStr(HashMap<String, String> data){
        String message = "";
        if(data.keySet().size() > 0) {
            Iterator<String> key = data.keySet().iterator();
            Iterator<String> value = data.values().iterator();

            // Add the opening curved bracket and the first key + value
            message = "{" + key.next() + ":\""+ value.next()+"\"";

            // Add all keys + values
            while(key.hasNext() && value.hasNext()){
                message += (","+key.next() + ":\""+value.next()+"\"");
            }
            // Add the closing curved bracket
            message += "}";
            return message;
        }else {
            return message;
        }
    }

    /**
     *
     * @param cmd   The command which will be execute
     * @param data  All the data string pairs.
     *              Even => Key String
     *              Odd  => Value String
     * @return      Returns a correct command string
     */
    public static String getDataStr(String cmd, String ... data){
        if(data.length % 2 == 1)
            return "";

        HashMap<String, String> dataMap = new HashMap<>();

        for (int i=0; i<data.length; i+=2) {
            dataMap.put(data[i],data[i+1]);
        }

        return getDataStr(cmd, dataMap);

    }
}
