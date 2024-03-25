function initDriver() {
	fetch("http://localhost:8080/PROBOT/InitDriverServlet", {
		method: 'POST',
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded'
		},
		body: ""
	})
	.then(response => {
		console.log(response);
		if (!response.ok) {
			//throw new Error('Network response was not ok');
		}
		return response.text();
	})
	.then(data => {
		console.log(data);
	})
	.catch(error => {
		console.error('There was a problem with the fetch operation:', error);
	});
}

initDriver();

let currentSection = 1;

    function showIntroPictures() {
        // Hide the current section
        document.getElementById(`introPic${currentSection}`).classList.remove('visible');

        // Increment the section number or reset to 1 if it exceeds 3
        currentSection = (currentSection % 2) + 1;

        // Show the next section
        document.getElementById(`introPic${currentSection}`).classList.add('visible');
        // document.getElementById(`introPic${currentSection}`).style.transition = 'opacity 1s ease';
    }
    setInterval(showIntroPictures, 5000);


    // Animation for feature picture
    function showFeaturePictures(){
        var elms = document.getElementsByClassName("featurePicture");

        for (var i = 0; i < elms.length; i++) {
            elms[i].classList.remove("animatedPicture");
        }


        for (var i = 0; i < elms.length; i++) {
            elms[i].classList.add("animatedPicture");
            console.log(i);
        }
    }

    // setInterval(showFeaturePictures, 5000);