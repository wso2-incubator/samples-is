# Extend the Authentication Request Handler

## Steps to deploy the sample

1. Build the sample.

```
mvn clean install
```
2. Shutdown the Identity Server if it is running.

3. Move the authentication.request.handler-1.0.0.jar inside the target folder to <IS_HOME>/repository/components/lib/ directory.

  When you are introducing a new jar which was not available previously to the setup, you need to keep them in the   <IS_HOME>/repository/components/lib/ directory.

4. Navigate to <IS_HOME>/repository/conf/identity/ directory and edit the application-authentication.xml file as follows.

Replace the AuthenticationRequsetHandler with the new class name.
```
<Extensions>
  . . .
 <AuthenticationRequestHandler>org.wso2.carbon.identity.framework.extended.ExtendedAuthenticationRequestHandler</AuthenticationRequestHandler>              
 . . .
</Extensions>
```

6. Start the WSO2 server.

## Functionality

The sample will authorize the users based on their role to a targeted SAML based application.

Here in the sample, the users with the role "trip" will be authorized when they log into the application "TripAction" configured with SAML2 web SSO.

When a user tries to log in, the sample will be executed and it will authenticate and authorize the user as follows.

- Default authentication.
  - If authentication is successful,
    - Check authentication context.
        - If it is SAML,
          - The application which the user is trying to log in is checked.
            - If "TripAction"
                - Check for the roles assigned to the user.
                  - If the "trip" role is available, user is authorized.
                  - If "trip" role is not available, the login is not authorized.
            - If not "TripAction", the user is authenticated.
        - If not SAML, the user is authenticated.
    - If authentication is unsuccessful, the user will be not checked for authorization.

Please note that this sample contains the helper methods to extract information required for authorization.
