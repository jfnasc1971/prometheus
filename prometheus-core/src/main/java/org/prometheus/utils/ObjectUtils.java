package org.prometheus.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class ObjectUtils {

	private static NumberFormat nf = null;
	
	static {
		nf = DecimalFormat.getInstance(new Locale("pt", "BR"));
	}

	public static final String toString(Object o, String defaultValue) {
		if (o != null && !"null".equalsIgnoreCase(o.toString().trim())) {
			return o.toString();
		}
		return defaultValue;
	}

	public static final String toString(Object o) {
		if (o != null) {
			return o.toString();
		}
		return null;
	}

	public static final Date toDate(Object o) {
		if (o != null && o instanceof Date) {
			return (Date) o;
		}
		return null;
	}

	public static final Long toLong(Object o) {
		if (o != null && o instanceof Number) {
			return ((Number) o).longValue();
		}
		return null;
	}

	public static final Long toLong(Object o, Long defaultValue) {
		if (o != null && o instanceof Number) {
			return ((Number) o).longValue();
		}
		return defaultValue;
	}

	public static final Double toDouble(Object o) {
		if (o != null && o instanceof Number) {
			return ((Number) o).doubleValue();
		}
		return null;
	}

	public static final Double toDouble(Object o, Double defaultValue) {
		if (o != null && o instanceof Number) {
			return ((Number) o).doubleValue();
		}
		return defaultValue;
	}

	public static final BigDecimal toBigDecimal(Object o) {
		return toBigDecimal(o, null);
	}

	public static final BigDecimal toBigDecimal(Object o, BigDecimal defaultValue) {
		
		BigDecimal result = defaultValue;
				
		try {
			if (o != null && o instanceof String) {
				result = BigDecimal.valueOf(nf.parse((String)o).doubleValue());
			}
			if (o != null && o instanceof Number) {
				result = BigDecimal.valueOf(((Number) o).doubleValue());
			}
		} catch (ParseException e) {
			// Does Nothing?
		}
		
		return result;
	}

}
