<!DOCTYPE html>
<html>



<body>
	<h1>TrufflePig API Tester</h1>
	<h3 id="state">No State Yet</h3>
	<%
	Cookie cookie = null;
    Cookie[] cookies = null;
	cookies = request.getCookies();
	boolean found = false;
	if( cookies != null ) {
    
        for (int i = 0; i < cookies.length; i++) {
           cookie = cookies[i];
           if ("deviceId".equals(cookie.getName()))
           {
               out.print("<h5>This is a registered device. Id is : " + cookie.getValue()+"</h5>");
               found = true;
           }
        }
     } 

    String deviceId = request.getParameter("deviceId");

	if (found == false && (deviceId == null || deviceId == ""))
	{
        out.println("<h5>This is not a registered device, Please SignUp</h5>");
    }
	else if (!found)
	{
		//User is registering the device for first time (user clicked the link from email) - set cookie in response
		cookie = new Cookie("deviceId", deviceId);
		cookie.setMaxAge(60*60*24*365);
		response.addCookie(cookie); 
		
        out.print("<h5>This is NOW a registered DeviceId : " + cookie.getValue()+"</h5>");
	}
  	%>
<!--   	<style>
  	textarea {
   resize: none;
}
  	</style>
 -->	<div id="content">
		<label for="endpointList">Choose an Endpoint</label>
		<select id=endpointList onchange="getEndpointSchema()"><option>Choose an Endpoint</option></select>
		<button type="button" onclick="submitEndpointRequest()">Submit Request</button>
		
		<div style="width:50%;">
    		<div style="float:left;">
			<p>Request XSD Reference</p>
			<textarea id="jsonXSD" name="jsonXSD" rows="20" cols="50" readonly></textarea>
	    	</div>
    		
    		<div style="float:right;">
			<p>Actual Request</p>
			<textarea id="request" name="request" rows="20" cols="50"></textarea>
    		</div>
		</div>

		<div style="width:100%;">
		<!-- label for="response">Response</label-->
		<p>Response</p>
  		<textarea id="response" name="response" rows="20" cols="120" readonly></textarea>
  		</div>

		<div style="width:100%;">
		<!-- label for="response">Response</label-->
		<p>Streaming</p>
  		<textarea id="streaming" name="streaming" rows="20" cols="120" readonly></textarea>
  		</div>
  
	</div>


	<script type="text/javascript">

	var SESSION_ID = "";
	
	
function CreateUUID() {
	  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
	    var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
	    return v.toString(16);
	  });
	}
	

function wait(ms){
	   var start = new Date().getTime();
	   var end = start;
	   while(end < start + ms) {
	     end = new Date().getTime();
	  }
	}
	
class Request {
	constructor(sessionId, endPoint, appRequestAsString) {
		this.createdTime = 'Oct 12, 2020 4:46:33 PM';//new Date(); Wed Dec 09 2020 07:45:47 GMT-0500 (Eastern Standard Time)
		this.traceId = CreateUUID();

		this.sessionId = sessionId;
		
		this.endPoint = endPoint;
		this.appRequestAsString = appRequestAsString;
	}
}

var ws = new WebSocket("ws://ec2-18-212-212-242.compute-1.amazonaws.com:8080");

function sendRequest(endpoint, appRequest){
	let request = new Request(SESSION_ID, endpoint, appRequest);
	console.log("Sending Request : " + JSON.stringify(request));
	
	ws.send(JSON.stringify(request));
}

function submitEndpointRequest(){
	var endpointList = document.getElementById("endpointList");
	var reqArea = document.getElementById("request")
	
	sendRequest(endpointList.value, reqArea.value);
}

function getEndpointSchema(){
	var endpointList = document.getElementById("endpointList");
	console.log("Sending request for Schema for: " + endpointList.value);
	
	sendRequest("EndpointSchema", endpointList.value);
}

function readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) {
        	return c.substring(nameEQ.length,c.length);
        }
    }
    return null;
}

this.ws.onmessage = function(msgevent) {
    var message = JSON.parse(msgevent.data);
    
    if (message.messageType == "Event"){
    	var event = JSON.parse(message.payload);
     	var streamingArea = document.getElementById("streaming");
    	streamingArea.value = event + "\n\n--------------------------------\n\n" + streamingArea.value;
    	return;
    }
    //It is a Response
    console.log("Raw Response:" + message.payload);

    var response = JSON.parse(message.payload);
    console.log("Received Response for endpoint :" + response.endpoint);
    
    var appResponse;
    if (response.requestSuccessful)
   	{
    	appResponse = JSON.parse(response.appResponseAsString);
   	}
    else
   	{
    	appResponse = response.errorMessage;
   	}
    console.log("App Response:" + appResponse);

 
    if (response.endpoint == "ListEndpoints")
   	{
        var endpointList = document.getElementById("endpointList");
    	var state = document.getElementById("state");
        
        var length = endpointList.options.length;
        for (i = length-1; i >= 0; i--) {
        	endpointList.options[i] = null;
        }
        
        appResponse.sort();
        for(var i = 0; i < appResponse.length; i++) {
            var opt = appResponse[i];
            var el = document.createElement("option");
            el.textContent = opt;
            el.value = opt;
            endpointList.appendChild(el);
        }
        
        if (readCookie("deviceId") == null)
        {
        	state.innerHTML  = "Welcome new user!"
        	endpointList.value = "SignUp";
        	getEndpointSchema();
        }
        else
       	{
        	//Automatically Login
        	var appPayload = "{\"authenticationType\":\"TokenBased\",\"id\":\"" + readCookie("deviceId") + "\"}"; 
        	sendRequest("Login", appPayload)
       	}
   	}
    else if (response.endpoint == "EndpointSchema")
   	{
    	var responseArea = document.getElementById("response");
    	var xsdArea = document.getElementById("jsonXSD");
    	xsdArea.value = appResponse;
    	responseArea.value = "";
   	}
    else if (response.endpoint == "Login")
    {
    	var state = document.getElementById("state");

    	if (appResponse.authenticated)
   		{
        	SESSION_ID = appResponse.sessionId;
        	state.innerHTML = "Logged In and Ready! With SessionId: " + SESSION_ID;
   		}
    	else
   		{
    		state.innerHTML = "Login process failed. FailureReason: " + appResponse.failureReason;
   		}
    }
    else
   	{
    	var responseArea = document.getElementById("response");
    	responseArea.value = JSON.stringify(appResponse);
   	}
};

this.ws.onopen = (e) => {
	if (e.target.readyState !== WebSocket.OPEN) return;

	sendRequest("ListEndpoints", "{}");
}
//wait(3000);
//this.ws.send(JSON.stringify(request));
</script>

</body>
</html>