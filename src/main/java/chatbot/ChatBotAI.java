package chatbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatBotAI {
	private static final String API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-1.0-pro:generateContent";
    private static final String API_KEY = "AIzaSyDG1dAhsNEcY0HSzTjz_kR4FNWUcGpwkkQ";
    private static String msgStorePlace = "";

    public static String sendMessageToAI(String message) throws Exception {
    	message = message.replaceAll("\"", "\\\\\"");
    	String promptMsg ="";
    	if(msgStorePlace=="") {
    		promptMsg = "This was the message sent now: "+message;
    	}else {
    		promptMsg = "This was our previous conversation:(ignore if it is not related to the message sent now)\n "+ msgStorePlace + "\nThis was the message sent now:\n" +message;
    	}
    	
        URL url = new URL(API_URL + "?key=" + API_KEY);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String payload = "{\"contents\":[{\"role\":\"user\",\"parts\":[{\"text\":\"" + promptMsg + "\"}]}]}";

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = payload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        catch (IOException ioe) {
			throw new IOException("Problem in connecting");
		}

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            String replyText = response.toString().split("\"text\":")[1].split("\"\\}\\]")[0].trim().substring(1);
            msgStorePlace+="Mine: "+message+"\nYour's: "+replyText;
            System.out.println(response.toString());
            replyText = replyText.replaceAll("```(.*?)```", "<p class=\"codeSnippet\"><span onclick=\"copyResponse(this)\" class=\"material-symbols-rounded copyBtn\">content_copy</span><span id=\"copyTxt\">Copy</span><span id=\"runIcon\" onclick = \"addAndRemoveWaitInRun(); runcode(this);\" ></span><span id=\"runTxt\">Run</span>$1</p>").replaceAll("\\\\n", "<br>").replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>").replaceAll("\\\\\"", "\"").replaceAll("(?<!`)`([^`]+)`(?!`)", "<b>$1</b>").replaceAll("^###(.*?)$", "<h3>$1</h3>").replaceAll("^##(.*?)$", "<h2>$1</h2>").replaceAll("\\\\t", "	").replaceAll("Google", "ZS").replaceAll("Gemini", "ProBot").replaceAll("&", "&amp;").replaceAll("\\\\u003c", "&lt;").replaceAll("\\\\u003e", "&gt;").replaceAll("'", "&#39;");;
            return replyText;
        }
        catch (IOException e) {
        	e.printStackTrace();
			throw new IOException("Problem in connecting");
        }
        catch (Exception e) {
        	e.printStackTrace();
			throw new Exception("Sorry! Harm content");
		}
    }
}
