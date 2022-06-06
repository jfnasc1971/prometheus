package org.prometheus.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	private static final SimpleDateFormat sdf = new SimpleDateFormat();
	
	public static final String format(Date arg0) {
		String result = null;
		
		try {
			sdf.applyPattern("dd/MM/yyyy");
			result = sdf.format(arg0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	public static final Date parse(String arg0) {
		Date result = null;
		
		try {
			sdf.applyPattern("yyyy-MM-dd");
			result = sdf.parse(arg0);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
