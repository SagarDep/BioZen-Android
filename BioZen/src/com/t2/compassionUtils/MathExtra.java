/*****************************************************************
BioZen

Copyright (C) 2011 The National Center for Telehealth and 
Technology

Eclipse Public License 1.0 (EPL-1.0)

This library is free software; you can redistribute it and/or
modify it under the terms of the Eclipse Public License as
published by the Free Software Foundation, version 1.0 of the 
License.

The Eclipse Public License is a reciprocal license, under 
Section 3. REQUIREMENTS iv) states that source code for the 
Program is available from such Contributor, and informs licensees 
how to obtain it in a reasonable manner on or through a medium 
customarily used for software exchange.

Post your updates and modifications to our GitHub or email to 
t2@tee2.org.

This library is distributed WITHOUT ANY WARRANTY; without 
the implied warranty of MERCHANTABILITY or FITNESS FOR A 
PARTICULAR PURPOSE.  See the Eclipse Public License 1.0 (EPL-1.0)
for more details.
 
You should have received a copy of the Eclipse Public License
along with this library; if not, 
visit http://www.opensource.org/licenses/EPL-1.0

*****************************************************************/
package com.t2.compassionUtils;

import java.util.Calendar;

public class MathExtra {
	public static double mean(double[] values) {
		double total = 0.0;
		for(double val: values) {
			total += val;
		}
		return total / values.length;
	}
	
	public static double variance(double[] values) {
		long n = 0;
		double mean = 0;
		double s = 0.0;
		
		for(double val: values) {
			++n;
			double delta = val - mean;
			mean += delta / n;
			s += delta * (val - mean);
		}
		
		return (s / n);
	}
	
	public static double stdDev(double[] values) {
		return Math.sqrt(variance(values));
	}
	
	public static void roundTime(Calendar cal, int calendarField) {
		cal.setTimeInMillis(roundTime(cal.getTimeInMillis(), calendarField));
	}
	
	public static long roundTime(long time, int calendarField) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		
		switch(calendarField) {
		case Calendar.YEAR:
			cal.set(Calendar.MONTH, Calendar.JANUARY);
		case Calendar.MONTH:
			cal.set(Calendar.DAY_OF_MONTH, 1);
		case Calendar.DAY_OF_MONTH:
		case Calendar.HOUR_OF_DAY:
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			break;
			
		}
		return cal.getTimeInMillis();
	}
	
	/**
	 * Scales data to 0-outputMax
	 * @param value - Input value to scale
	 * @param inputMax - Max practical value of input
	 * @param inputMin - Min practical value of input
	 * @param outputMax - Max value of scaled output
	 * @return
	 */
	public static float scaleData(float value, float inputMax, float inputMin, int outputMax) {
		float scaledValue = value - inputMin;
		if (scaledValue < 0) scaledValue = 0;
		scaledValue *= (outputMax / (inputMax - inputMin)); //  
		if (scaledValue > outputMax) scaledValue = outputMax;		

		return scaledValue;
	}	
	
	/**
	 * Scales data to 0-outputMax
	 * @param value - Input value to scale
	 * @param inputMax - Max practical value of input
	 * @param inputMin - Min practical value of input
	 * @param outputMax - Max value of scaled output
	 * @param gain - gain to use instead of calculated gain
	 * @return
	 */
	public static float scaleData(float value, float inputMax, float inputMin, int outputMax, float gain) {
		float scaledValue = value - inputMin;
		if (scaledValue < 0) scaledValue = 0;
		scaledValue *= gain; //  
		if (scaledValue > outputMax) scaledValue = outputMax;		

		return scaledValue;
	}	
	
}
