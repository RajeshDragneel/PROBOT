package com.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class CommonDriver {
	private WebDriver driver = null;
	private JavascriptExecutor jsExecutor = null;
	private Map<String, String> WindowHandlers = null;
	private List<String> allLinks = null;
	private static CommonDriver cd = null;
	
	private CommonDriver() {
		ChromeOptions options = new ChromeOptions();
	    options.addArguments("--disable-blink-features=AutomationControlled");
	    options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
		driver = new ChromeDriver(options);
		driver.manage().window().maximize();
		jsExecutor = (JavascriptExecutor) driver;
		allLinks = new ArrayList<String>();
		allLinks.add("https://www.tutorialspoint.com/python/online-python-compiler.php");
		allLinks.add("https://www.tutorialspoint.com/compile_c_online.php");
		allLinks.add("https://www.programiz.com/javascript/online-compiler/");
		allLinks.add("https://www.tutorialspoint.com/java/online-java-compiler.php");
		allLinks.add("https://www.tutorialspoint.com/online_c_formatter.htm");
		allLinks.add("https://www.tutorialspoint.com/online_java_formatter.htm");
		allLinks.add("https://formatter.org/python-formatter");
		allLinks.add("https://www.tutorialspoint.com/online_javascript_formatter.htm");
		allLinks.add("https://www.tutorialspoint.com/compile_cpp_online.php");

		allLinks.forEach(value -> {
			if(WindowHandlers != null) {
				jsExecutor.executeScript("window.open('" + value + "')");
			}
			else {
				WindowHandlers =new HashMap<String, String>();
				driver.get(value);
			}
		});

		driver.getWindowHandles().forEach( value -> {
			driver.switchTo().window(value);
			String currentLink = driver.getCurrentUrl();
			if(currentLink.contains("python-compiler")) WindowHandlers.put("pythonc", value);
			else if(currentLink.contains("compile_cpp")) WindowHandlers.put("cppc", value);
			else if(currentLink.contains("javascript_formatter")) WindowHandlers.put("javascriptf", value);
			else if(currentLink.contains("python-formatter")) WindowHandlers.put("pythonf", value);
			else if(currentLink.contains("c_formatter")) {
				WindowHandlers.put("cppf", value);
				WindowHandlers.put("cf", value);
			}
			else if(currentLink.contains("java-compiler")) WindowHandlers.put("javac", value);
			else if(currentLink.contains("javascript/")) WindowHandlers.put("javascriptc", value);
			else if(currentLink.contains("java_formatter")) WindowHandlers.put("javaf", value);
			else if(currentLink.contains("c_online")) WindowHandlers.put("cc", value);
		});
	}
	
	public static CommonDriver getInstance() {
		if(cd == null) cd = new CommonDriver();
		return cd;
	}
	
	public WebDriver getDriver() {
		if(driver == null) driver = new ChromeDriver();
		return driver;
	}
	
	public String getWindowHandlerFor(String window) {
		return WindowHandlers.get(window);
	}
}