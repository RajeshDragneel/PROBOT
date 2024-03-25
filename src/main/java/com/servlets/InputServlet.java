package com.servlets;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/InputServlet")
public class InputServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public InputServlet() {}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			System.out.println("InputServlet");
			WebDriver driver = CompilerServlet.driver;
			WebElement outputArea = driver.findElement(By.cssSelector("#terminal > div.ace_scroller > div"));
			
			String input = request.getParameter("input");
			
			if(input == null) {
				response.getWriter().write(outputArea.getAttribute("innerText").replaceAll("EnDiNg\n|sCoBj\n|sCoBj|EnDiNg", ""));
				return;
			}
			
			String language = request.getParameter("language");

			WebElement terminalInputArea = driver.findElement(By.cssSelector("#terminal > textarea"));
			
			terminalInputArea.sendKeys(input);
			terminalInputArea.sendKeys(Keys.ENTER);
			
			Thread.sleep(1000);

			String output = outputArea.getAttribute("innerText").replaceAll("EnDiNg\n|sCoBj\n|sCoBj|EnDiNg", "");

			if(language.equals("javascript")) output = output.substring(output.indexOf("\n") + 1, output.length());

			if(output != "") response.getWriter().write(output);
		}
		catch (Exception e) {
			e.printStackTrace();
//			if(CompilerServlet.driver != null) response.getWriter().write("An error occured while connecting to server\nPlease reload the page and try again\nNote: make sure to copy the code before reloading the page");
		}
	}

}