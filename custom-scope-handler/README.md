## Custom Scope Handler

### Introduction / Use case.
This custom scope handler can be used to remove the scopes other than the allowed scopes from the token request. (This sample considers Scope_A, Scope_B and Scope_C as allowed scopes)

### Applicable product versions.
Tested with WSO2 IS 5.7.0

### How to use.
1. Build the custom-scope-handler using the command `mvn clean install`.
2. Copy the CustomScopeHandler-1.0.0.jar located inside the target to the directory, **<IS_Home>/repository/components/dropins**
3. Add the classpath of the scope handler to the identity.xml file located at **<IS_Home>/repository/conf/identity** under the tag ScopeHandlers
`<ScopeHandler class="org.wso2.custom.scope.handler.CustomScopeHandler" />`
4. Start the server.

 ### Testing the project.
1. Register a service provider in the IS
2. Send a token request with multiple scopes (including allowed scopes)
3. The token response will contain only the allowed scopes

Example Request:
```
curl -v -X POST --basic -u <CLIENT_ID>:<CLIENT_SECRET> -H 'Content-Type: application/x-www-form-urlencoded;charset=UTF-8' -k -d 'grant_type=client_credentials&scope=Scope_A Scope_B Scope_C Scope_D' https://localhost:9443/oauth2/token
```

Response:
```
{"access_token":"c13b02d8-68b1-365c-847c-c14290b173e4","scope":"Scope_A Scope_B Scope_C","token_type":"Bearer","expires_in":3600}
```