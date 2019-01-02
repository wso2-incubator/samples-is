## Custom Basic Authenticator for Force Password Reset via OTP.
### Introduction / Use case.
This is a custom basic authenticator which does force password reset via OTP

### Applicable product versions.
IS 5.3.0

### How to use.
* You can build the source code and get the jar, same jar is available in dropins folder.
* Put the jar in the dropins folder to the <carbon-home>/repository/components/dropins folder.
* Copy the two jsps in the accountrecoveryendpoint folder to the <carbon-home>/repository/deployment/server/webapps/accountrecoveryendpoint folder. You can put your own CSS and customize according to your theme. 
* Go to your <carbon-home>/repository/deployment/server/webapps/authenticationendpoint and open the login.jsp file and add the part "|| localAuthenticatorNames.contains("CustomBasicAuthenticator")*" to the line as shown below  . 
Sample login.jsp is available in the authenticationendpoint  in the resources folder.
 

 } else if (localAuthenticatorNames.size() > 0 && (localAuthenticatorNames.contains(BASIC_AUTHENTICATOR) || localAuthenticatorNames.contains("CustomBasicAuthenticator"))) {
 

 * Restart the server.
 * In your service provider configurations, instead of Basic authenticator select the "basicCustom"  You can change this authenticator friendly name according to your preference in your source code.

### Testing the project.
Follow up the documentation[1] to try out the scenario. Please note this will not prompt the second login page after password reset.
 
 [1]https://docs.wso2.com/display/IS530/Forced+Password+Reset#ForcedPasswordReset-PasswordResetviaOTP