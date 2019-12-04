package com.atmecs.assessmenttask.helpermethods;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
	PageActions pagActions;
	Properties testData;
	LocatorSeparator separateLocators;
	LogReport log;
	AssertionHelpers assertionHelpers;
	String sheetName;
	ExcelFileReader excelReader;
	ExcelFileWriter excelWriter;
	ExtentReport extentReport = new ExtentReport();
	String noOfStars[];
	String noOfStarsExpected = "4 / 5";
	String dateSplittedToArray[];
	int checkInDateIntegerFormat;

	List<Double> listOfHotelPriceAfterSorting = new ArrayList<Double>();
	int hotelPriceArray[];
	int priceIndex = 0, finalIndex = 1;
	int lowestPriceIndex, pageIndex, allHotelPriceIndex = 0;
	DateHandlingHelpers dateHelpers = new DateHandlingHelpers();
	int size;
	int noOfAdults;
	int noOfChilds;
	String allHotelPriceTextArray[] = new String[2];

	List<Double> allHotelPriceList = new ArrayList<Double>();
	List<Double> listOfHotelPrice = new ArrayList<Double>();
	List<WebElement> hotelRatingsElement;
	List<WebElement> detailsButton;
	WebElement viewMoreButton;
	String allHotelsPriceText;
	double allHotelPrice;
	String priceText;
	String priceTextArray[] = new String[2];
	double removedSymbols;
	int rowNumber = 1;

	public BookingHotels_AutomationHelpers() throws IOException {

		pagActions = new PageActions();
		properties = new PropertiesFileReader().loadingPropertyFile(FilePath.LOCATORS_FILE);
		testData = new PropertiesFileReader().loadingPropertyFile(FilePath.EXPECTEDDATA_FILE);
		separateLocators = new LocatorSeparator();
		assertionHelpers = new AssertionHelpers();

		log = new LogReport();
		excelReader = new ExcelFileReader(FilePath.TESTDATA_FILE);
		excelWriter = new ExcelFileWriter();
	}

	/**
	 * This method is used to create the WebElement by giving the element locator as
	 * an input
	 */
	public WebElement webElement(WebDriver driver, String elementLocator) {

		WebElement targetElement = driver
				.findElement(separateLocators.separatingLocators(properties.getProperty(elementLocator)));
		return targetElement;
	}

	/**
	 * This method is used to get the current system date and add the number of days
	 * need to be added to the current date, and return the checkIn date
	 * 
	 * @throws IOException
	 */
	public int calculatingCheckInDate() throws IOException {
		sheetName = testData.getProperty("expdata.sheetname");

		LocalDate currentDate = LocalDate.now();

		LocalDate dateAfterTenDays = currentDate.plusDays(10);
		String checkInDate = dateAfterTenDays.toString();

		dateSplittedToArray = new String[3];
		dateSplittedToArray = checkInDate.split("-");
		checkInDateIntegerFormat = Integer.parseInt(dateSplittedToArray[2]);

		return checkInDateIntegerFormat;

	}

	/**
	 * To find out the available hotels for the required number of people and
	 * required date
	 * 
	 * @throws IOException
	 */
	public void searchingTheAvailableHotelsForTheRequiredDate(WebDriver driver)
			throws InterruptedException, IOException {

		String checkInDate = Integer.toString(calculatingCheckInDate());
		String checkOutDate = Integer.toString(calculatingCheckInDate() + 2);

		JavascriptExecutor js = (JavascriptExecutor) driver;

		noOfAdults = Integer.parseInt(testData.getProperty("expdata.noofadults"));
		noOfChilds = Integer.parseInt(testData.getProperty("expdata.noofchild"));
		sheetName = testData.getProperty("expdata.sheetname");

		assertionHelpers.assertingPageTitle(driver, "expdata.pagetitle");
		log.info("PageTitle validated");

		driver.findElement(separateLocators.separatingLocators(properties.getProperty("loc.cityname")))
				.sendKeys(testData.getProperty("expdata.cityname"));
		pagActions.clickingTheElement(driver, properties.getProperty("loc.hoteldestinationsearch"));
		pagActions.clickingTheElement(driver, properties.getProperty("loc.checkindate"));

		WebElement checkInDateElement = driver.findElement(By.xpath("(//div[@data-date='" + checkInDate + "'])[1]"));
		checkInDateElement.click();

		WebElement checkOutDateElement = driver.findElement(By.xpath("(//div[@data-date='" + checkOutDate + "'])[2]"));
		checkOutDateElement.click();
		pagActions.clickingTheElement(driver, properties.getProperty("loc.checkoutbox"));

		for (int adultsIndex = 0; adultsIndex < noOfAdults - 2; adultsIndex++) {

			pagActions.clickingTheElement(driver, properties.getProperty("loc.adultplusbtn"));
			pagActions.clickingTheElement(driver, properties.getProperty("loc.outerarea"));
			Thread.sleep(3000);
		}
		for (int childIndex = 0; childIndex < noOfChilds; childIndex++) {

			pagActions.clickingTheElement(driver, properties.getProperty("loc.childplusbtn"));
			pagActions.clickingTheElement(driver, properties.getProperty("loc.outerarea"));
		}
		pagActions.clickingTheElement(driver, properties.getProperty("loc.button"));
		log.info("Redirecting to the Hotel Search results");
	}

	/**
	 * This method finds out the four star rating hotel at the lowest price
	 *
	 */
	public void findingTheFourStarRatedHotelAtDifferentPriceRates(WebDriver driver) throws InterruptedException {

		List<WebElement> allHotelPricelist = driver
				.findElements(separateLocators.separatingLocators(properties.getProperty("loc.pricecommonlocator")));

		assertionHelpers.assertingPageTitle(driver, "expdata.hotelresultstitle");
		log.info("Result of Hotel Search page verified");

		hotelRatingsElement = driver
				.findElements(separateLocators.separatingLocators(properties.getProperty("loc.starratingselement")));
		detailsButton = driver
				.findElements(separateLocators.separatingLocators(properties.getProperty("loc.detailsbtn")));
		viewMoreButton = driver
				.findElement(separateLocators.separatingLocators(properties.getProperty("loc.viewmore")));
		noOfStars = new String[hotelRatingsElement.size()];

		for (int index = 0; index < hotelRatingsElement.size(); index++) {

			pageScroll.pageScrollDownTillElementVisible(driver, hotelRatingsElement.get(index));
			noOfStars[index] = hotelRatingsElement.get(index).getText();

			if (!(hotelRatingsElement.get(index).isDisplayed())) {

				pageScroll.pageScrollDownTillElementVisible(driver, viewMoreButton);
				pagActions.clickingTheElement(driver, properties.getProperty("loc.viewmore"));
				pageScroll.pageScrollDownTillElementVisible(driver, hotelRatingsElement.get(index));
				noOfStars[index] = hotelRatingsElement.get(index).getText();
			}
			allHotelsPriceText = allHotelPricelist.get(index).getText();
			allHotelPriceTextArray = allHotelsPriceText.split("\\s");
			allHotelPrice = Double.parseDouble(allHotelPriceTextArray[1]);
			allHotelPriceList.add(allHotelPriceIndex, allHotelPrice);

			allHotelPriceIndex++;

			if (noOfStars[index].contentEquals(noOfStarsExpected)) {

				priceText = allHotelPricelist.get(index).getText();
				priceTextArray = priceText.split("\\s");
				removedSymbols = Double.parseDouble(priceTextArray[1]);
				listOfHotelPrice.add(priceIndex, removedSymbols);

				priceIndex++;
			}
		}
		log.info("Found all the Hotel in the 4 star rating with lowest price");

		for (int duplicatingIndex = 0; duplicatingIndex < listOfHotelPrice.size(); duplicatingIndex++) {

			listOfHotelPriceAfterSorting.add(duplicatingIndex, listOfHotelPrice.get(duplicatingIndex));
		}

		Collections.sort(listOfHotelPriceAfterSorting);

		finalIndex = allHotelPriceList.indexOf(listOfHotelPriceAfterSorting.get(0));

		List<WebElement> listofhotelname = driver
				.findElements(separateLocators.separatingLocators(properties.getProperty("loc.firsthotelname")));
		String lowestPriceHotelName = listofhotelname.get(finalIndex).getText();
		log.info("Four Star hotel with LowestPrice  -" + lowestPriceHotelName);

		pageScroll.pageScrollDownTillElementVisible(driver, hotelRatingsElement.get(finalIndex));
		detailsButton.get(finalIndex).click();

	}

	public void validatingTheCustomerDetailsInHotelResultsPage(WebDriver driver) throws IOException {

		pagActions.clickingTheElement(driver, properties.getProperty("loc.modifybtn"));
		customerDetailsValidation(driver);

	}

	public void validatingDetailsInHotelDetailsPage(WebDriver driver) throws IOException {

		assertionHelpers.assertingStringTexts(driver, "loc.hotelname", testData.getProperty("expdata.hotelname"));
		pageScroll.pageScrollDownTillElementVisible(driver, webElement(driver, "loc.checkout"));
		assertionHelpers.assertingStringTexts(driver, "loc.hotelplace", testData.getProperty("expdata.hoteladdress"));
		customerDetailsValidation(driver);

	}

	public void customerDetailsValidation(WebDriver driver) throws IOException {

		assertionHelpers.assertingStringTexts(driver, "loc.modifydestination",
				testData.getProperty("expdata.hoteldestination"));
		Assert.assertEquals(
				driver.findElement(separateLocators.separatingLocators(properties.getProperty("loc.adultno")))
						.getAttribute("value"),
				testData.getProperty("expdata.noofadults"));

		String actualCheckInDate = driver
				.findElement(separateLocators.separatingLocators(properties.getProperty("loc.checkincart")))
				.getAttribute("value");

		String expectedCheckInDate = dateHelpers.getDateFormat("dd-MM-yyyy",
				Integer.parseInt(testData.getProperty("expdata.checkindate")));
		Assert.assertEquals(
				driver.findElement(separateLocators.separatingLocators(properties.getProperty("loc.childno")))
						.getAttribute("value"),
				testData.getProperty("expdata.noofchildmodify"));
		Assert.assertEquals(actualCheckInDate, expectedCheckInDate);

		String actualCheckOutDate = driver
				.findElement(separateLocators.separatingLocators(properties.getProperty("loc.checkoutcart")))
				.getAttribute("value");
		String expectedCheckOutDate = dateHelpers.getDateFormat("dd-MM-yyyy",
				Integer.parseInt(testData.getProperty("expdata.checkoutdate")));
		Assert.assertEquals(actualCheckOutDate, expectedCheckOutDate);

	}
}
