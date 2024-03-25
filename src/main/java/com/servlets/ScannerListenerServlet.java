package com.servlets;

import java.io.IOException;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ScannerListenerServlet")
public class ScannerListenerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static WebDriver driver = null;

    public ScannerListenerServlet() {}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("ScannerListener");
		String code = request.getParameter("code");
		String language = request.getParameter("language");
		try {
			if((language.equals("java") && code.contains("Scanner(System.in);")) || (language.equals("javascript") && code.contains("prompt(")) || (language.equals("python") && code.contains("input(")) || (language.equals("c") && code.contains("scanf(")) || (language.equals("cpp") && (code.contains("getLine(") || code.contains("cin")))) {
				
				while (driver == null) {
					driver = CompilerServlet.driver;
					System.out.println("check");
				}
				driver = CompilerServlet.driver;
				
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
				
				wait.until(ExpectedConditions.elementToBeSelected(By.cssSelector("#terminal > div.ace_scroller > div")));
				
				WebElement terminalArea = driver.findElement(By.cssSelector("#terminal > div.ace_scroller > div"));
				
				wait.until(ExpectedConditions.textToBePresentInElement(terminalArea, "sCoBj"));

				String out = terminalArea.getAttribute("innerText");
				
				if(language.equals("javascript")) out = out.substring(out.indexOf("\n") + 1, out.length());
				
				System.out.println(out);

				System.out.println("ScannerListener before success");
				if(out.contains("sCoBj")) {
					System.out.println("ScannerListener after success");
					response.getWriter().write(out.replaceAll("sCoBj\n|sCoBj", ""));
				}
			}else {
				System.out.println("ScannerListener out");
				return;
			}
		}
		catch (Exception e) {
//			return;
//			response.getWriter().write("Unexpected error while connecting to server");
			e.printStackTrace();
		}
	}

}