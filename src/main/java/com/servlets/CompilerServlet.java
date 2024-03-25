package com.servlets;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.common.CommonDriver;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/CompilerServlet")
public class CompilerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static WebDriver driver;
	public static String outputString = null;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("CompilerSevlet");
		
		String code = request.getParameter("code").replaceAll("\u00a0\n|\u00a0", " ").replaceAll("·", " ");
		
		String language = request.getParameter("language");
		
		boolean isJava = language.equals("java"), isJavascript = language.equals("javascript"), isPython = language.equals("python"), isC = language.equals("c"), isCPP = language.equals("cpp");
		
		
		try {
			
			driver = CommonDriver.getInstance().getDriver();
			
			JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
			
			driver.switchTo().window(CommonDriver.getInstance().getWindowHandlerFor(language + "c"));
			
			jsExecutor.executeScript("location.reload()");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
			
			try {
				Alert alert = wait.until(ExpectedConditions.alertIsPresent());
				alert.accept();
			} catch (Exception e) {
//				 System.out.println(e.getMessage());
			}
			
			WebElement inputArea = driver.findElement(By.cssSelector("#editor > textarea"));
			
			List<String> scannerList = getScannerObjects(code);
			
			System.out.println(scannerList);
			
			String pattern ="", printCommand = "";
			
			if(isJava) {
				pattern = "\\b((" + String.join("|", scannerList) + ")\\.next(?:Int|Line|Double|Float|Boolean|Byte|Long|Short)?)\\(";
				printCommand = "System.out.println";
			}
			else if(isJavascript) {
				pattern = "prompt\\(.*\\)";
				printCommand = "console.log";
			}
			else if(isPython) {
				pattern = "input\\(.*\\)";
				printCommand = "print";
			}
			else if(isC) {
				pattern = "scanf\\(.*\\)";
				printCommand = "printf";
			}
			else if(isCPP) {
				pattern = "getline\\([^)]*\\)|cin\\s*>>\\s*";
				printCommand = "std::cout << ";
			}
			
			String[] lines = code.split("\n");
			
			boolean isInMain = false;
			
			int leftCount = 0, rightCount = 0;
			String line;
			
			System.out.println(language);
			
			for(int i = 0; i < lines.length; i++) {
				line = lines[i];
				if((language.equals("java") && code.contains("Scanner(System.in);")) || (language.equals("javascript") && code.contains("prompt(")) || (language.equals("python") && code.contains("input(")) || (language.equals("c") && code.contains("scanf(")) || (language.equals("cpp") && (code.contains("getLine(") || code.contains("cin")))) {
					if(Pattern.compile(pattern).matcher(line).find()) lines[i] = printCommand + "(\"sCoBj\");\n" + line;
				}
				if(isJava || isC || isCPP) {
					if (line.contains("public static void main") && isJava || line.contains("int main()") && (isC || isCPP)) {
						isInMain = true;
					}
					if(leftCount > 0 || isInMain) {
						leftCount += line.chars().filter(c -> c == '{').count();
						rightCount += line.chars().filter(c -> c == '}').count();
						if ((isC || isCPP) && line.contains("return 0;")) {
							lines[i] = "	" + printCommand + "(\"EnDiNg\");\n" + line;
							isInMain = false;
						}
						else if(leftCount == rightCount && !isC && !isCPP) {
							isInMain = false;
							lines[i] = "	" + printCommand + "(\"EnDiNg\");\n" + line;
						}
					}
				}
				else {
					if (i == lines.length - 1) {
						lines[i] += "\n" + printCommand + "(\"EnDiNg\");";
					}
				}
			}

			code = String.join("\n", lines).replaceAll("\u00a0\n|\u00a0", " ").replaceAll("·", " ");
			
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection stringSelection = new StringSelection(code);
			clipboard.setContents(stringSelection, null);
			
			inputArea.sendKeys(Keys.chord(Keys.CONTROL, "a"));

			inputArea.sendKeys(Keys.chord(Keys.CONTROL, "v"));
			
			System.out.println(driver.findElement(By.cssSelector("#editor > div.ace_scroller > div")).getAttribute("innerText"));
						
			Thread.sleep(1000);
			inputArea.sendKeys(Keys.chord(Keys.CONTROL, Keys.ENTER));
			Thread.sleep(1000);
			WebElement outputArea = driver.findElement(By.cssSelector("#terminal > div.ace_scroller > div"));
			
			Pattern closingPattern = Pattern.compile("(Error:|error:|EnDiNg|ERROR!|java:\\d+|bash:\\s+|:\\s+command not found|At least one public class is required in main file|Code is missing|Error while connecting with the server)");
			
			long startTime = System.currentTimeMillis();

			while(!closingPattern.matcher(outputArea.getAttribute("innerText")).find()) {
				if(outputArea.getAttribute("innerText").endsWith("sCoBj")) {
					if(outputString == null) {
						outputString = outputArea.getAttribute("innerText");
					}
				}
				else if(System.currentTimeMillis() - startTime > 60000) {
					response.getWriter().write("Compilation timed out");
					driver.quit();
					return;
				}
			}
			
			String output = outputArea.getAttribute("innerText");
			
			if(isJavascript) output = output.substring(output.indexOf("\n")+ 1, output.length());

			output = output.replaceAll("EnDiNg\\n|sCoBj\\n|sCoBj|EnDiNg", "");
			
//			driver.quit();
			driver = null;
			
			System.out.println(output);
			
			response.getWriter().write(output);
			
		} catch (Exception e) {
//			driver.quit();
			driver = null;
			e.printStackTrace();
		}
	}

	public List<String> getScannerObjects(String code) {
		List<String> scannerVariables = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\bScanner\\s+(\\w+)\\s*=\\s*new\\s+Scanner\\(System\\.in\\);");
        Matcher matcher = pattern.matcher(code);

        while (matcher.find()) {
            scannerVariables.add(matcher.group(1));
        }

        return scannerVariables;
	}

}