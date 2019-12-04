package com.atmecs.assessmenttask.helpermethods;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class DateHandlingHelpers {
	
	public String getDateFormat(String outputDateType,int daysToBeAdded) {
		
		SimpleDateFormat outputDateFormat= new SimpleDateFormat(outputDateType);
		LocalDate dateNeeded = LocalDate.now();	
		LocalDate dateAfterAdded=dateNeeded.plusDays(daysToBeAdded);
		String date = outputDateFormat.format(java.sql.Date.valueOf(dateAfterAdded));
		return date;

	}
	public String addingDaysToCurrentDate(String daysToBeAdded) {
	
		LocalDate localDate= LocalDate.now();
		LocalDate requiredOffsetDate=localDate.plusDays(Integer.parseInt(daysToBeAdded));
		String requiredDate=requiredOffsetDate.toString();
		return requiredDate;
	}

}
