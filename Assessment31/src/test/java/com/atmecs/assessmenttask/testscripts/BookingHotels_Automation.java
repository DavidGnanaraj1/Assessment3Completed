package com.atmecs.assessmenttask.testscripts;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import com.atmecs.Assessment3.assessmenttask.pageactions.AssertionHelpers;
import com.atmecs.Assessment3.assessmenttask.pageactions.PageActions;
import com.atmecs.Assessment3.assessmenttask.testbase.TestBase;
import com.atmecs.assessmenttask.constants.FilePath;
import com.atmecs.assessmenttask.helpermethods.BookingHotels_AutomationHelpers;
import com.atmecs.assessmenttask.helpermethods.DateHandlingHelpers;
import com.atmecs.assessmenttask.reports.ExtentReport;
import com.atmecs.assessmenttask.reports.LogReport;
import com.atmecs.assessmenttask.utils.ExcelFileReader;
import com.atmecs.assessmenttask.utils.ExcelFileWriter;
import com.atmecs.assessmenttask.utils.LocatorSeparator;
import com.atmecs.assessmenttask.utils.PropertiesFileReader;

public class BookingHotels_Automation extends TestBase {
	
	JavascriptExecutor js = (JavascriptExecutor) driver;
	int size;
	int noOfAdults, checkInDate;
	int noOfChilds, checkOutDate;
	Properties properties;
	PageActions pageactions = new PageActions();
	Properties testdata;
	LocatorSeparator separatelocator = new LocatorSeparator();
	LogReport log= new LogReport();
	AssertionHelpers assertionhelpers = new AssertionHelpers();
	String sheetName;
	ExcelFileReader excelreader;
	ExcelFileWriter excelwriter = new ExcelFileWriter();
	ExtentReport extentReport = new ExtentReport();
	BookingHotels_AutomationHelpers bookingHotelsHelpers = new BookingHotels_AutomationHelpers();
    DateHandlingHelpers dateHandlingHelpers= new DateHandlingHelpers();
	
	
    
    public BookingHotels_Automation() throws IOException {

		properties = new PropertiesFileReader().loadingPropertyFile(FilePath.LOCATORS_FILE);
		testdata = new PropertiesFileReader().loadingPropertyFile(FilePath.EXPECTEDDATA_FILE);
		excelreader = new ExcelFileReader(FilePath.TESTDATA_FILE);
		
	}

	/**
	 * This method is to function the automated  booking of lowest price four star hotel 
	 * The automation process is splitted into four methods: 1.Searching the Hotel by entering the required details
	 * 2.To validate the details in the  Hotel Results Page 3.Sorting the hotel price of four star hotels
	 * 4.Validating customer and hotel details in Hotel Details Page
	 */
	
    
     @Test
	public void bookingACheapFourStarHotelForSixMembers() throws IOException, InterruptedException {

        bookingHotelsHelpers.searchingTheAvailableHotelsForTheRequiredDate(driver);
	    bookingHotelsHelpers.validatingTheCustomerDetailsInHotelResultsPage(driver);
		bookingHotelsHelpers.findingTheFourStarRatedHotelAtDifferentPriceRates(driver);
        bookingHotelsHelpers.validatingDetailsInHotelDetailsPage(driver);
        
	}
   
 

}
