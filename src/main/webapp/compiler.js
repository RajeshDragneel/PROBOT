var language = "java";

var fileTypes = {
	java: "java",
	javascript: "js",
	python: "py",
	c: "c",
	cpp: "cpp"
}

const handleFileChange = (event) => {
    const file = event.target.files[0];

    if (file) {
        const fileReader = new FileReader();

        fileReader.onload = function(e) {
            const value = e.target.result;
            editorEditable.setValue(value.replaceAll("\u00a0", " ").replaceAll("·", " "));
            editorEditable.clearSelection();
        };

        fileReader.readAsText(file);
    }
};

const fileInput = document.getElementById('uploadButton');

fileInput.addEventListener('change', handleFileChange);

var inputElement = document.getElementById("inputTag");

inputElement.addEventListener("keypress", (event) => {
  if (event.key == "Enter" && inputElement.value != "") {
	console.log(event.key);
	sendInput();
	inputElement.value = "";
  }
})

function downloadCode() {
	var content = editorEditable.getValue();
	console.log(content);
	var blob = new Blob([content], { type: 'text/plain' });
	var downloadLink = document.createElement("a");
	downloadLink.href = window.URL.createObjectURL(blob);
	downloadLink.download = "MyCode." + fileTypes[language];
	document.body.appendChild(downloadLink);
	downloadLink.click();
	document.body.removeChild(downloadLink);
}

async function beautify() {
	document.getElementById("outputArea").innerText = "";
	try {
		const response = await fetch("http://localhost:8080/PROBOT/BeautifyServlet", {
			method: 'POST',
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded'
			},
			body: "code=" + encodeURIComponent(editorEditable.getValue()) + "&language=" + encodeURIComponent(lang)
		});

		if (!response.ok) {
			throw new Error('Network response was not ok');
		}

		const data = await response.text();
		console.log(data);
		if (data != "") {
			if (data.startsWith("error")) {
				document.getElementById("outputArea").innerText = data;
			}
			else {
				editorEditable.setValue(data);
				editorEditable.clearSelection();
			}
		}
	} catch (error) {
		console.error('There was a problem with the fetch operation:', error);
	}
}

async function execute() {
	document.getElementById("outputArea").innerText = "";
	await beautify();
	try {
		const response = await fetch("http://localhost:8080/PROBOT/CompilerServlet", {
			method: 'POST',
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded'
			},
			body: "code=" + encodeURIComponent(editorEditable.getValue()) + "&language=" + encodeURIComponent(lang)
		});

		if (!response.ok) {
			throw new Error('Network response was not ok');
		}

		const data = await response.text();
		
		var regexPattern = /(Error:|error:|EnDiNg|ERROR!|java:\d+|bash:\s+|:\s+command not found|At least one public class is required in main file|Code is missing|Error while connecting with the server)/;
		
		if(regexPattern.test(data)){
			document.getElementById("outputArea").style.color="red";
		}
		else{
			document.getElementById("outputArea").style.color="white";
		}
		document.getElementById("outputArea").innerText= data;
	} catch (error) {
		console.error('There was a problem with the fetch operation:', error);
	}
}

function sendInput() {
	console.log(document.getElementById("inputTag").value);
	fetch("http://localhost:8080/PROBOT/InputServlet", {
		method: 'POST',
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded'
		},
		body: "input=" + encodeURIComponent(document.getElementById("inputTag").value) + "&language=" + encodeURIComponent(lang)
	})
		.then(response => {
			console.log(response);
			if (!response.ok) {
				throw new Error('Network response was not ok');
			}
			return response.text();
		})
		.then(data => {
			console.log(data);
			if (data != "") document.getElementById("outputArea").innerText = data;
		})
		.catch(error => {
			console.error('There was a problem with the fetch operation:', error);
		});
}

function scannerListener() {
	fetch("http://localhost:8080/PROBOT/ScannerListenerServlet", {
		method: 'POST',
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded'
		},
		body: "code=" + encodeURIComponent(editorEditable.getValue()) + "&language=" + encodeURIComponent(lang)
	})
		.then(response => {
			console.log(response);
			if (!response.ok) {
				throw new Error('Network response was not ok');
			}
			return response.text();
		})
		.then(data => {
			console.log(data);
			if (data != "") {
				document.getElementById("outputArea").innerText = data;
				document.getElementById("inputTag").focus();
			}
		})
		.catch(error => {
			console.error('There was a problem with the fetch operation:', error);
		});
}
  
  
const chatbox = document.querySelector(".chatbox");
const chatInput = document.querySelector(".chatInput textarea");
const sendChatBtn = document.querySelector(".chatInput span");
const chatbotToggler = document.querySelector(".chatbotToggler");
const closeBtn = document.querySelector(".closeBtn");
const chatbot = document.querySelector('.chatbot');
const chatContainer = document.getElementById('chatContainer');

let rect1 = chatbot.getBoundingClientRect();
let rect2 = chatbotToggler.getBoundingClientRect();

const translateX = rect2.left - rect1.left;
const translateY = rect2.top - rect1.top;

chatbot.style.transform = `translate(${translateX}px, ${translateY}px) scale(0)`;

let userMessage = null;
const inputInitHeight = chatInput.scrollHeight;

const createChatLiOut = (message, className) => {
    
    const chatLi = document.createElement("li");
    chatLi.classList.add("chat", `${className}`);
    let chatContent =  `<p></p><span class="profileImg"></span>`;
    chatLi.innerHTML = chatContent;
    chatLi.querySelector("p").textContent = message;
    return chatLi;
}


const copyResponse = (copyBtn) => {
    const reponseTextElement = copyBtn.parentElement;
    console.log(reponseTextElement.innerText);
    navigator.clipboard.writeText(reponseTextElement.innerText.replace(/\\n/g, '\n').replace("content_copy\n","").replace("Copy\n","",1).replace("java\n", "", 1).replace("python\n", "", 1).replace("py\n", "", 1).replace("c++\n", "", 1).replace("cpp\n", "", 1).replace("javascript\n", "", 1).replace("js\n", "", 1).replace("c\n", "", 1).replace("Run\n","",1));
    copyBtn.textContent = "done";
    setTimeout(() => copyBtn.textContent = "content_copy", 1000);
}

const runcode = (runBtn) =>{
	const reponseTextElement = runBtn.parentElement;
	console.log(reponseTextElement.innerText);
    var code =   reponseTextElement.innerText.replace(/\\n/g, '\n').replace("content_copy\n","").replace("Copy\n","",1).replace("java\n", "", 1).replace("python\n", "", 1).replace("py\n", "", 1).replace("c++\n", "", 1).replace("cpp\n", "", 1).replace("javascript\n", "", 1).replace("js\n", "", 1).replace("c\n", "", 1).replace("Run\n","",1);
    document.body.classList.toggle("showChatbot");
    chatContainer.classList.toggle("blurEffect");
    editorEditable.setValue(code);
    editorEditable.clearSelection();
    lang="";
    execute();
}

const createChatLi = (message, className) => {
  
  const chatLi = document.createElement("li");
  chatLi.classList.add("chat", `${className}`);
  let chatContent =`<span class="roboImg"></span><p></p>`;
  chatLi.innerHTML = chatContent;
  chatLi.querySelector("p").innerHTML = message;
  return chatLi; 
}


const generateResponse = (chatElement) => {
    const messageElement = chatElement.querySelector("p");
    
    fetch("http://localhost:8080/PROBOT/ChatbotServlet", {
        method: "POST",
        headers: {
            "Content-type": "application/x-www-form-urlencoded"
        },
        body: "userMessage=" + encodeURIComponent(userMessage)
    })
    .then(response => {
        if (!response.ok) {
            messageElement.classList.add("error");
        	messageElement.textContent = "Oops! Network issue";
        }
       return response.text();
    })
    .then(data => {
		messageElement.innerHTML = "";
		console.log(data);
        messageElement.innerHTML =data;
    })
    .catch(() => {
        messageElement.classList.add("error");
        messageElement.textContent = "Oops! Something went wrong. Please try again.";
    })
    .finally(() => {
        chatbox.scrollTo(0, chatbox.scrollHeight);
    });
}

const handleChat = () => {
    userMessage = chatInput.value.trim();
    console.log(userMessage);
    if(!userMessage) return;

    chatInput.value = "";
    chatInput.style.height = `${inputInitHeight}px`;

    chatbox.appendChild(createChatLiOut(userMessage, "outgoing"));
    chatbox.scrollTo(0, chatbox.scrollHeight);

        const incomingChatLi = createChatLi(`<div class="typingAnimation">
        <div class="typingDot" style="--delay: 0.2s"></div>
        <div class="typingDot" style="--delay: 0.3s"></div>
        <div class="typingDot" style="--delay: 0.4s"></div>
    </div>`, "incoming");
        chatbox.appendChild(incomingChatLi);
        chatbox.scrollTo(0, chatbox.scrollHeight);
        generateResponse(incomingChatLi);
}

chatInput.addEventListener("input", () => {
    chatInput.style.height = `${inputInitHeight}px`;
    chatInput.style.height = `${chatInput.scrollHeight}px`;
    let input = chatInput.innerText;
    
    chatInput.innerHTML = input;
});

chatInput.addEventListener("keydown", (e) => {
    if(e.key === "Enter" && !e.shiftKey && window.innerWidth > 800) {
        e.preventDefault();
        handleChat();
    }
});

sendChatBtn.addEventListener("click", handleChat);

chatbotToggler.addEventListener("click", () => {
    document.body.classList.toggle("showChatbot");
    chatContainer.classList.toggle("blurEffect");
    chatInput.focus();
});

function addAndRemoveWaitInRun() {
	checkOutputArea();
}

function checkOutputArea() {
	var intervalID = setInterval(function() {
    var myDiv = document.getElementById('outputArea');
    var myButton = document.getElementById('run');
    if (myDiv.innerHTML.trim() === '') {
        myButton.disabled = true;
        myButton.classList.add('wait');
        document.getElementById("runTest").style.display = "none";
        document.getElementById("runLoading").style.display = "inline";
        setTimeout(function() {
            myButton.classList.remove('cursor');
        }, 2000);
    } else {
        document.getElementById("runLoading").style.display = "none";
        document.getElementById("runTest").style.display = "inline";
        myButton.classList.add('cursor');
        myButton.classList.remove('wait');
        myButton.disabled = false;
        clearInterval(intervalID);
    }
}, 1000);
}





const clearBtn = document.getElementById("clearBtn");

clearBtn.addEventListener("click", () => {
	document.getElementById("outputArea").innerText = " ";
});


var langArr=["js","java","py","c","cpp"];
var modeLang = ["javascript","java","python","c_cpp","c_cpp"];
var compilerLang=["javascript","java","python","c","cpp"];

const guessLang = new GuessLang();
var lang="java";
async function runGuessLang(code) {
  var result = await guessLang.runModel(code);
  for (let index = 0; index < result.length; index++) {
    if (langArr.includes(result[index].languageId)) {
    console.log(result[index].languageId);
    lang = compilerLang[langArr.indexOf(result[index].languageId)];
    editorEditable.session.setMode("ace/mode/"+modeLang[langArr.indexOf(result[index].languageId)]);
    let langIcons = document.getElementsByClassName("langIcons");
	document.getElementsByClassName("chosenLanguage")[0].classList.remove("chosenLanguage");
	document.getElementById(lang).classList.add("chosenLanguage");
   	break;
   }      
}
}

let editor = document.querySelector("#editor");
let editorEditable = ace.edit("editor");
editorEditable.setTheme("ace/theme/cobalt");
editorEditable.session.setMode("ace/mode/java");
editorEditable.setOptions({
  autoCloseBrackets: true,
});

editorEditable.getSession().on("change", function(e) {
  runGuessLang(editorEditable.getValue());
});


editorEditable.setValue("public class HelloWorld {\n	public static void main(String[] args) {\n        System.out.println(\"Hello world!\");\n    }\n}");
editorEditable.clearSelection();

