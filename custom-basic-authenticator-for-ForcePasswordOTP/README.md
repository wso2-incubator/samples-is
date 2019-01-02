# CustomBasicAuthenticator
This is a custom basic authenticator which does force password reset via OTP

* You can build the source code and get the jar, same jar is available in dropins folder.
* Put the jar in the dropins folder to the <carbon-home>/repository/components/dropins folder.
* Copy the two jsps in the accountrecoveryendpoint folder to the <carbon-home>/repository/deployment/server/webapps/accountrecoveryendpoint folder. You can put your own CSS and customize according to your theme. 
* Go to your <carbon-home>/repository/deployment/server/webapps/authenticationendpoint and open the login.jsp file and add the part "|| localAuthenticatorNames.contains("CustomBasicAuthenticator")*" to the line as shown below  . 
Sample login.jsp is available in the authenticationendpoint  in the resources folder.
 

 } else if (localAuthenticatorNames.size() > 0 && (localAuthenticatorNames.contains(BASIC_AUTHENTICATOR) || localAuthenticatorNames.contains("CustomBasicAuthenticator"))) {
 

 * Restart the server.
 * In your service provider configurations, instead of Basic authenticator select the "basicCustom"  You can change this authenticator friendly name according to your preference in your source code.

