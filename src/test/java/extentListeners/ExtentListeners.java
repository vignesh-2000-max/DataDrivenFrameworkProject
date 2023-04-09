package extentListeners;

import java.io.IOException;
import java.util.Date;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public class ExtentListeners implements ITestListener{
	
	static Date d=new Date();
	static String fileName=d.toString().replace(";", "_").replace(" ", "_")+".html";
	
	private static ExtentReports extent= ExtentManager.createInstance(".\\reports\\"+fileName);
	
	public static ExtentTest test;
	
	public void onTestStart(ITestResult result) {
		
		test=extent.createTest(result.getClass().getName()+"     @TestCase : "+result.getMethod().getMethodName());
		
	}

	public void onTestSuccess(ITestResult result) {
	
		String methodName=result.getMethod().getMethodName();
		String logText="<b>"+" TEST CASE : "+ methodName.toUpperCase()+" PASSED "+"</b>";
		Markup m=MarkupHelper.createLabel(logText, ExtentColor.GREEN);
		test.pass(m);
	}

	public void onTestFailure(ITestResult result) {

		try {
			//Capturing the Screenshots for failed conditions..
			ExtentManager.captureScreenshot();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		String methodName=result.getMethod().getMethodName();
		String logText="<b>"+" TEST CASE : "+ methodName.toUpperCase()+" FAILED "+"</b>";
		
		
			test.fail("<b><font color=red>" + "Screenshot of failure" + "</font></b><br>",MediaEntityBuilder.createScreenCaptureFromPath(ExtentManager.fileName)
					.build());
		
		
		Markup m=MarkupHelper.createLabel(logText, ExtentColor.RED);
        test.log(Status.FAIL, m);
	}

	public void onTestSkipped(ITestResult result) {
		
		String methodName=result.getMethod().getMethodName();
		String logText="<b>" +" TEST CASE : "+methodName.toUpperCase()+ " SKIPPED"+"</br>";
		Markup m=MarkupHelper.createLabel(logText, ExtentColor.ORANGE);
		test.skip(m);
		
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		
		
	}

	public void onTestFailedWithTimeout(ITestResult result) {
		
	}

	public void onStart(ITestContext context) {
		
	}

	public void onFinish(ITestContext context) {
		
		if(extent!=null) {
			
			extent.flush();
		}
		
	}
	
}