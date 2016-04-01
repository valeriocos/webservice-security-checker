// Get the template HTML and remove it from the doument
  var previewNode = document.querySelector("#template");
  previewNode.id = "";
  var previewTemplate = previewNode.parentNode.innerHTML;
  previewNode.parentNode.removeChild(previewNode);

  var myDropzone = new Dropzone(document.body, { // Make the whole body a dropzone
    url: "SecurityCheckerServlet", // Set the url
    thumbnailWidth: 80,
    thumbnailHeight: 80,
    parallelUploads: 1,
    maxFiles: 1,
    acceptedFiles: ".zip",
    maxFilesize: 10,
    previewTemplate: previewTemplate,
    autoProcessQueue: false,
    autoQueue: true, // Make sure the files aren't queued until manually added
    previewsContainer: "#previews", // Define the container to display the previews
    clickable: ".fileinput-button" // Define the element that should be used as click trigger to select files.
  });
  
  	
  	myDropzone.on("sending", function(file) {
  	  // Show the total progress bar when upload starts
  	  document.querySelector("#total-progress").style.opacity = "1";
  	});
  	
  	// Update the total progress bar
  	myDropzone.on("totaluploadprogress", function(progress) {
  	  document.querySelector("#total-progress .progress-bar").style.width = progress + "%";
  	});
  	
  	myDropzone.on("complete", function(file) {
  	  // Show the total progress bar when upload starts
      document.querySelector("#total-progress").style.opacity = "0";
      document.querySelector("#previews .cancel").removeAttribute("disabled");
    });

	document.querySelector("#actions .start").onclick = function() {
	  document.querySelector("#previews .cancel").setAttribute("disabled", "disabled");
	  myDropzone.processQueue();
	};
	
	document.querySelector("#actions .cancel").onclick = function() {
	  myDropzone.removeAllFiles(true);
	};