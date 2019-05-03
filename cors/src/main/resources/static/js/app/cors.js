/**
 * 
 */

var createCORSRequest = function(method, url) {
  var xhr = new XMLHttpRequest();
  if ("withCredentials" in xhr) {
    // Most browsers.
    xhr.open(method, url, true);
  } else if (typeof XDomainRequest != "undefined") {
    // IE8 & IE9
    xhr = new XDomainRequest();
    xhr.open(method, url);
  } else {
    // CORS not supported.
    xhr = null;
  }
  return xhr;
};

// localhost -> your domain
// 7777 -> your port number
var url = 'http://localhost:7777/actuator/httptrace';
var method = 'GET';
var xhr = createCORSRequest(method, url);

xhr.onload = function() {
  // Success code goes here.
	var content = xhr.responseText;
	document.getElementById('cors_content').innerHTML = content;
};

xhr.onerror = function() {
  // Error code goes here.
};

xhr.send();