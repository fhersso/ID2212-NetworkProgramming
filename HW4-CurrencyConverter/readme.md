#Currency converter with a Web-interface
Three-tier Web-based application in Java for on-line currency conversion that converts a specified amount from one currency to another, e.g. from SEK to EUR.
##Features
* The converter provides a Web-based client with a GUI (e.g., an HTML-form or an applet), which allows the user to choose a currency to convert from, and a currency to convert to; to enter an amount to be converted, and to submit the conversion request to the converter that runs on the server side of the application. 
* The converter processes the request and sends the result to the client to be displayed in the user's Web-browser on the client side.
* The project includes JSF, EJB and JPA technologies. The client submits a request to the converter web tier (JSF) that uses an EJB to process the conversion request and to generate the result of conversion that is displayed in the user's Web-browser.
