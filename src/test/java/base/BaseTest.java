package base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.Status;

import extentListeners.ExtentListeners;
import io.github.bonigarcia.wdm.WebDriverManager;
import utilities.DbManager;
import utilities.ExcelReader;
import utilities.MonitoringMail;

public class BaseTest {

	public static WebDriver driver;
	private static Properties or=new Properties();
	private static Properties config=new Properties();
	private static FileInputStream fis;
	public static Logger log=Logger.getLogger(BaseTest.class);
	public static ExcelReader excel=new ExcelReader("./src/test/resources/excel/testdata.xlsx");
	public static MonitoringMail mail=new MonitoringMail();
	public static WebDriverWait wait;
	public static WebElement dropdown;
	
	
	public static void click(String locatorKey) {
		
		try{ 
			if(locatorKey.endsWith("_CSS")) {
			driver.findElement(By.cssSelector(or.getProperty(locatorKey))).click();
		}else if(locatorKey.endsWith("_XPATH")) {
			driver.findElement(By.xpath(or.getProperty(locatorKey))).click();
		}else if(locatorKey.endsWith("_ID")) {
			driver.findElement(By.id(or.getProperty(locatorKey))).click();
		}
		
		log.info("Clickiing on the Element : "+ locatorKey);
		ExtentListeners.test.log(Status.INFO, "Clickiing on the Element : "+ locatorKey);
	 }catch(Throwable t) {
		
		log.error("Error while Clickiing on the Element : "+ locatorKey+ "error messege : " +t.getMessage());
		ExtentListeners.test.log(Status.FAIL, "Error while Clickiing on the Element : "+ locatorKey+ "error messege : " +t.getMessage());
		
		Assert.fail(t.getMessage());
	  }
	}
	
	public static boolean isElementPresent(String locatorKey) {
		
		try {
			if(locatorKey.endsWith("_CSS")) {
				driver.findElement(By.cssSelector(or.getProperty(locatorKey)));
			}else if(locatorKey.endsWith("_XPATH")) {
				driver.findElement(By.xpath(or.getProperty(locatorKey)));
			}else if(locatorKey.endsWith("_ID")) {
				driver.findElement(By.id(or.getProperty(locatorKey)));
			}
			
		}catch(Throwable t) {
			log.info("Element not found : "+locatorKey);
			ExtentListeners.test.log(Status.INFO, "Element not found : "+locatorKey);
			return false;
		}
		
		log.info("Finding an Element : " +locatorKey);
		ExtentListeners.test.log(Status.INFO, "Finding an Element : " +locatorKey);
		return true;
	}
	
	public static void type(String locatorKey, String value) {
		try {
			if (locatorKey.endsWith("_XPATH")) {
				driver.findElement(By.xpath(or.getProperty(locatorKey))).sendKeys(value);
			} else if (locatorKey.endsWith("_CSS")) {
				driver.findElement(By.cssSelector(or.getProperty(locatorKey))).sendKeys(value);
			} else if (locatorKey.endsWith("_ID")) {
				driver.findElement(By.id(or.getProperty(locatorKey))).sendKeys(value);
			}
			log.info("typing in an Element : " + locatorKey + " entered the value as : " + value);
			ExtentListeners.test.log(Status.INFO,
					"typing in an Element : " + locatorKey + " entered the value as : " + value);
		} catch (Throwable t) {

			log.error("Error while typing in an Element : " + locatorKey + " error message : " + t.getMessage());
			ExtentListeners.test.log(Status.FAIL,
					"Error while typing in an Element : " + locatorKey + " error message : " + t.getMessage());
			Assert.fail(t.getMessage());

		}
	}
	
	public static void select(String locatorKey, String value) {
		
		try {
			if(locatorKey.endsWith("_XPATH")) {
				dropdown=driver.findElement(By.xpath(or.getProperty(locatorKey)));	
			}else if(locatorKey.endsWith("_CSS")) {
				dropdown=driver.findElement(By.cssSelector(or.getProperty(locatorKey)));
			}else if(locatorKey.endsWith("_ID")) {
				dropdown =driver.findElement(By.id(or.getProperty(locatorKey)));
			}
			
			Select select=new Select(dropdown);
			select.selectByVisibleText(value);
			log.info("Selecting an element : "+locatorKey+"selected the value as : "+value);
			ExtentListeners.test.log(Status.INFO, "Selecting an element : "+locatorKey+"selected the value as : "+value);
			
		}catch(Throwable t) {
			log.error("Error while selecting the Element : "+locatorKey+"error messege : "+t.getMessage());
			ExtentListeners.test.log(Status.INFO, "Error while selecting the Element : "+locatorKey+"error messege : "+t.getMessage());
			Assert.fail(t.getMessage());
		}
	}
	
	@BeforeSuite
	public void setUp()  {
		PropertyConfigurator.configure("./src/test/resources/properties/log4j.properties");
		
			try {
				fis=new FileInputStream("./src/test/resources/properties/config.properties");
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			}
			
			try {
				config.load(fis);
				log.info("Config prpoerties loaded !!");
			} catch (IOException e) {
			
				e.printStackTrace();
			}
			
			try {
				fis=new FileInputStream("./src/test/resources/properties/OR.properties");
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			}
			
			try {
				or.load(fis);
				log.info("OR prpoerties loaded !!");
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
				
				System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"\\src\\test\\resources\\executable\\chromedriver.exe");
				driver=new ChromeDriver();
				log.info("Launching the Chrome Browser !!!");
			
			driver.get(config.getProperty("testsiteurl"));
			log.info("Navigated to : " + config.getProperty("testsiteurl"));
			driver.manage().window().maximize();
			driver.manage().timeouts()
					.implicitlyWait(Duration.ofSeconds(Integer.parseInt(config.getProperty("implicit.wait"))));
			wait = new WebDriverWait(driver, Duration.ofSeconds(Integer.parseInt(config.getProperty("explicit.wait"))));
			
			try {
				DbManager.setMysqlDbConnection();
				log.info("DB connection estabilished !");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
	}
	
	@AfterSuite
	public void tearDown() {

		driver.quit();
		log.info("Test Execution completed !!!");
	}
	
	
}
