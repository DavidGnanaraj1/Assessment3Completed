package com.atmecs.assessmenttask.helpermethods;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import com.atmecs.Assessment3.assessmenttask.pageactions.AssertionHelpers;
import com.atmecs.Assessment3.assessmenttask.pageactions.PageActions;
import com.atmecs.Assessment3.assessmenttask.pageactions.PageActionsScrollDown;
import com.atmecs.assessmenttask.constants.FilePath;
import com.atmecs.assessmenttask.reports.ExtentReport;
import com.atmecs.assessmenttask.reports.LogReport;
import com.atmecs.assessmenttask.utils.ExcelFileReader;
import com.atmecs.assessmenttask.utils.ExcelFileWriter;
import com.atmecs.assessmenttask.utils.LocatorSeparator;
import com.atmecs.assessmenttask.utils.PropertiesFileReader;

public class BookingHotels_AutomationHelpers {
	PageActionsScrollDown pageScroll = new PageActionsScrollDown();
	Properties properties;
	PageActions pageactions;
	Properties testdata;
	LocatorSeparator separatelocator;
	LogReport log;
	AssertionHelpers assertionhelpers;
	String sheetName;
	ExcelFileReader excelreader;
	ExcelFileWriter excelwriter;
	ExtentReport extentReport = new ExtentReport();
	String noOfStars[];
	String noOfStarsExpected = "4 / 5";
	String dateSplittedToArray[];
	int checkInDateIntFormat;
	String checkInDate;
	int hotelPriceArray[];
	int priceIndex = 0, finalIndex=1;
	int lowestPriceIndex,pageIndex,allHotelPriceIndex=0;
    DateHandlingHelpers dateHelpers= new DateHandlingHelpers();
	int size;
	int noOfAdults;
	int noOfChilds, checkOutDate;


	public BookingHotels_AutomationHelpers() throws IOException {

		pageactions = new PageActions();
		properties = new PropertiesFileReader().loadingPropertyFile(FilePath.LOCATORS_FILE);
		testdata = new PropertiesFileReader().loadingPropertyFile(FilePath.EXPECTEDDATA_FILE);
		separatelocator = new LocatorSeparator();
		assertionhelpers = new AssertionHelpers();

		log = new LogReport();
		excelreader = new ExcelFileReader(FilePath.TESTDATA_FILE);
		excelwriter = new ExcelFileWriter();
	}

	public WebElement webElement(WebDriver driver,String elementLocator) {
		WebElement targetElement=driver.findElement(separatelocator.separatingLocators(properties.getProperty(elementLocator)));
		return targetElement;
	}
	
	public int calculatingCheckInDate() {

		LocalDate currentDate = LocalDate.now();
		LocalDate dateAfterTenDays = currentDate.plusDays(Integer.parseInt(testdata.getProperty("expdata.checkindate")));
		checkInDate = dateAfterTenDays.toString();

		dateSplittedToArray = new String[3];
		dateSplittedToArray = checkInDate.split("-");
		checkInDateIntFormat = Integer.parseInt(dateSplittedToArray[2]);
		return checkInDateIntFormat;

	}

	public void searchingTheHotels(WebDriver driver) throws InterruptedException {
		
		JavascriptExecutor js = (JavascriptExecutor) driver;
		noOfAdults = Integer.parseInt(testdata.getProperty("expdata.noofadults"));
		noOfChilds = Integer.parseInt(testdata.getProperty("expdata.noofchild"));
		sheetName = testdata.getProperty("expdata.sheetname");

		assertionhelpers.assertingPageTitle(driver, "expdata.pagetitle");
		log.info("PageTitle validated");
        Thread.sleep(3000);
       
      //  driver.findElement(By.xpath("(//div[@class='select2-search'])[4]")).click();
		driver.findElement(By.xpath("//input[@id=\"s2id_autogen2\"]")).sendKeys(testdata.getProperty("expdata.cityname"));
       
        pageactions.clickingTheElement(driver, properties.getProperty("loc.hoteldestinationsearch"));
        pageactions.clickingTheElement(driver, properties.getProperty("loc.checkindate"));
	    String checkInDate=Integer.toString(calculatingCheckInDate());
	    
		WebElement checkInDateElement=driver.findElement(By.xpath("(//div[@data-date='"+checkInDate+"'])[1]"));
		checkInDateElement.click();
		String checkOutDate=Integer.toString(calculatingCheckInDate()+2);
		WebElement checkOutDateElement=driver.findElement(By.xpath("(//div[@data-date='"+checkOutDate+"'])[2]"));
		checkOutDateElement.click();
		
		pageactions.clickingTheElement(driver, properties.getProperty("loc.checkoutbox"));
	for(int adultsIndex=0;adultsIndex<noOfAdults-2;adultsIndex++) {
		pageactions.clickingTheElement(driver, properties.getProperty("loc.adultplusbtn"));	
		pageactions.clickingTheElement(driver, properties.getProperty("loc.outerarea"));
		Thread.sleep(3000);
		}
	for(int childIndex=0;childIndex<noOfChilds;childIndex++) {
			pageactions.clickingTheElement(driver, properties.getProperty("loc.childplusbtn"));		
			pageactions.clickingTheElement(driver, properties.getProperty("loc.outerarea"));
		}
		pageactions.clickingTheElement(driver,properties.getProperty("loc.button"));	
	   	log.info("Redirecting to the Searched results");
	}

	public void findingTheFourStarRatedHotelAtLowestPrice(WebDriver driver) throws InterruptedException {
	
		assertionhelpers.assertingPageTitle(driver,"expdata.hotelresultstitle");
		log.info("Result of Hotel Search page verified");
		List<Double> allHotelPriceList = new ArrayList<Double>();
		List<Double> listOfHotelPrice = new ArrayList<Double>();
		List<WebElement> hotelRatingsElement = driver.findElements(By.xpath("//span[@class='bg-primary']"));
		List<WebElement> hotelPriceElement=driver.findElements(By.xpath("//div[@class='price']/span"));
		String priceArray[];
		
		noOfStars = new String[hotelRatingsElement.size()];
		
		WebElement viewMoreButton = driver.findElement(separatelocator.separatingLocators(properties.getProperty("loc.viewmore")));
 for (int index = 0; index < hotelRatingsElement.size(); index++) {

			pageScroll.pageScrollDownTillElementVisible(driver, hotelRatingsElement.get(index));
			noOfStars[index] = hotelRatingsElement.get(index).getText();
	        List<WebElement> priceArrayElement=driver.findElements(By.xpath("//div[@class='price']/span"));
	        	
 if (!(hotelRatingsElement.get(index).isDisplayed())) {
				
				pageScroll.pageScrollDownTillElementVisible(driver, viewMoreButton);
				pageactions.clickingTheElement(driver, properties.getProperty("loc.viewmore"));
				pageScroll.pageScrollDownTillElementVisible(driver, hotelRatingsElement.get(index));
				noOfStars[index] = hotelRatingsElement.get(index).getText();
			}

	        String allHotelsPriceText = driver
					.findElement(By
							.xpath("(//div[@class='product-long-item']//div[@class='price']/span)[" + (index+1) + "]"))
					.getText();
			
			String allHotelPriceTextArray[]= new String [2];
			allHotelPriceTextArray=allHotelsPriceText.split("\\s");
	
			double allHotelPrice=Double.parseDouble(allHotelPriceTextArray[1]);
			allHotelPriceList.add(allHotelPriceIndex, allHotelPrice);
		
			allHotelPriceIndex++;
	   if (noOfStars[index].contentEquals(noOfStarsExpected)) {
					
					String priceText = driver
							.findElement(By
									.xpath("(//div[@class='product-long-item']//div[@class='price']/span)[" + (index+1) + "]"))
							.getText();
					
					String priceTextArray[]= new String [2];
					priceTextArray=priceText.split("\\s");
			
					double removedSymbols=Double.parseDouble(priceTextArray[1]);
					listOfHotelPrice.add(priceIndex, removedSymbols);
					priceIndex++;
				}
		}
		log.info("Found all the Hotel in the 4 star rating with lowest price");
		
		List<Double> listOfHotelPriceAfterSorting = new ArrayList<Double>();

		for (int duplicatingIndex = 0; duplicatingIndex < listOfHotelPrice.size(); duplicatingIndex++) {
			
			listOfHotelPriceAfterSorting.add(duplicatingIndex, listOfHotelPrice.get(duplicatingIndex));
		}

		Collections.sort(listOfHotelPriceAfterSorting);
		
        System.out.println(listOfHotelPriceAfterSorting.get(0));
        
        System.out.println(Double.toString(listOfHotelPriceAfterSorting.get(0)));
        int finalIndex=allHotelPriceList.indexOf(listOfHotelPriceAfterSorting.get(0));
        System.out.println(finalIndex);
        List<WebElement>listofhotelname=driver.findElements(separatelocator.separatingLocators(properties.getProperty("loc.firsthotelname")));
        String hotelName=listofhotelname.get(finalIndex).getText();
        System.out.println(hotelName);
        driver.navigate().back();
        pageactions.clickingTheElement(driver, properties.getProperty("loc.button"));


			List<WebElement> detailsButton = driver
					.findElements(separatelocator.separatingLocators(properties.getProperty("loc.detailsbtn")));

			for(int index=0;index<4;index++) {
			
					pageScroll.pageScrollDownTillElementVisible(driver, detailsButton.get(finalIndex));
			
			  if (!(detailsButton.get(finalIndex).isDisplayed())) {
				pageScroll.pageScrollDownTillElementVisible(driver, viewMoreButton);
				pageactions.clickingTheElement(driver, properties.getProperty("loc.viewmore"));
				
			} else {
				pageScroll.pageScrollDownTillElementVisible(driver, detailsButton.get(finalIndex));
				detailsButton.get(finalIndex).click();
				break;
			}
	}	
}
	public void hotelsSearchModification(WebDriver driver) throws IOException {
		
		pageactions.clickingTheElement(driver, properties.getProperty("loc.modifybtn"));
		detailsValidation(driver);
		pageactions.clickingTheElement(driver, properties.getProperty("loc.modifybtn"));
	
		
	}
	public void hotelDetailValidation(WebDriver driver) throws IOException {
		
		driver.findElement(separatelocator.separatingLocators(properties.getProperty("loc.hotelsdetailsbutton")));
		assertionhelpers.assertingStringTexts(driver, "loc.hotelname", testdata.getProperty("expdata.hotelname"));
		pageScroll.pageScrollDownTillElementVisible(driver, webElement(driver,"loc.checkout"));
	    assertionhelpers.assertingStringTexts(driver, "loc.hotelplace", testdata.getProperty("expdata.hoteladdress"));
        detailsValidation(driver);
	
	}
   public void detailsValidation(WebDriver driver) throws IOException {
	   
	    assertionhelpers.assertingStringTexts(driver, "loc.modifydestination",testdata.getProperty("expdata.hoteldestination"));
        Assert.assertEquals(driver.findElement(separatelocator.separatingLocators(properties.getProperty("loc.adultno"))).getAttribute("value"),testdata.getProperty("expdata.noofadults"));
		
        String actualCheckInDate=driver.findElement(separatelocator.separatingLocators(properties.getProperty("loc.checkincart"))).getAttribute("value");
        System.out.println(actualCheckInDate);
		String expectedCheckInDate=dateHelpers.getDateFormat("dd-MM-yyyy",Integer.parseInt(testdata.getProperty("expdata.checkindate")));
		Assert.assertEquals(driver.findElement(separatelocator.separatingLocators(properties.getProperty("loc.childno"))).getAttribute("value"),testdata.getProperty("expdata.noofchildmodify"));
        Assert.assertEquals(actualCheckInDate, expectedCheckInDate);
      
		String actualCheckOutDate=driver.findElement(separatelocator.separatingLocators(properties.getProperty("loc.checkoutcart"))).getAttribute("value");
		String expectedCheckOutDate=dateHelpers.getDateFormat("dd-MM-yyyy",Integer.parseInt(testdata.getProperty("expdata.checkoutdate")));
		Assert.assertEquals(actualCheckOutDate, expectedCheckOutDate);
		System.out.println(actualCheckInDate+actualCheckOutDate);
   }
}
