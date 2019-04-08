## Custom Email Module.
### Introduction / Use case.
The implementation of the WSO2 IS 5.3.0 and below doesn’t render the HTML content (text/html) instead, it sends email 
notifications as plain text (text/plain). This is because it uses the Axis2 Client (org.apache.axis2.client) to send the 
emails and it is a limitation of the Axis2 Client.

This project is capable of rendering the HTML content as it uses the javax.mail library and this can activated using five 
simple steps for the WSO2 IS 5.3.0 and below.

### Applicable product versions.
WSO2 IS 5.3.0 and below

### How to use.
Please refer [1] to activate it.

[1] https://medium.com/@abhishekdesilva/how-to-send-notification-emails-with-html-content-for-wso2-api-manager-b1608c277057