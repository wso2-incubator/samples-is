## Custom Basic Authenticator for Username only based Authentication.
### Introduction / Use case.
This is a custom basic authenticator allows the user to be authenticated only using the user name without prompting
for the password. 

### Applicable product versions.
IS 5.7.0

### How to use.
* Build the sample jar file using the following command

    ```mvn clean install```
* Copy the **org.wso2.carbon.identity.application.authenticator.custombasicauth-6.0.6.jar** build, found in the **target/** directory to the **<IS_HOME>/repository/components/dropins/** directory.
* Restart the server.
* In your service provider configurations, instead of Basic authenticator select the "custom-basic"  You can change this authenticator friendly name according to your preference in your source code.

### Testing the project.
* We will testing by configuring the custom basic authenticator as the second authentication step.
* We will also be configuring SMS OTP as the third authentication step. 
* Enable the user name validation in the **Identifier-First** step by setting the **ValidateUsername** to **true**.
* Apply the following configuration change to the **application-authentication.xml** file found in the **<IS_HOME>/repository/conf/identity/** directory. 
    
    ```
    <AuthenticatorConfig name="IdentifierExecutor" enabled="true">
       <Parameter name="ValidateUsername">true</Parameter>
    </AuthenticatorConfig>
    ```
* Create a Service Provider with **Local & Outbound Authentication Configurations** -> **Advanced Configuration**.
* Add 3 Authentication Steps
    * Step 1 - identifier-first
    * Step 2 - custom-basic (This is the newly deployed custom basic authenticator)
    * Step 3 - SMS OTP (Need to create a Identity Provider for this Federated Authenticator) 
* Follow the documentation to [1] configure SMS-OTP. 

* Custom Basic Authenticator with Username only Authentication is tested aganinst the following scenarios.
    * User created under super-tenant in the Primary user store (**admin@carbon.super/admin**)
    * User created under a tenant domain in the Primary user store (**testUser@test.com**)
    * User created under a tenant domain in the Secondary user store (**Secondary/testUser1@test.com**)
 
 [1]https://docs.wso2.com/display/IS570/Configuring+SMS+OTP