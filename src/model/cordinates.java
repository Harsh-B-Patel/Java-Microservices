package model;

import java.util.*;

public class cordinates{
	
	private static List<cordinate> map;
	
	static {
		map = new ArrayList<>();
	}
	
	public static void add_map(String lat, String lon) {
		cordinate i = new cordinate (lat, lon);
		map.add(i);
	}
	
	public static String[] get_last_on_map() {
		if (map.size() > 0 ){
			String[] str = new String[2];
			str[0] = (map.get(map.size()-1)).lat;
			str[1] = (map.get(map.size()-1)).lon;
			return str;
		}
		else {
		return null;
		}
	}
	
	public static int length() {
		return map.size();
		
	}

	
}