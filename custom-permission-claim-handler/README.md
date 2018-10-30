# Custom Permission Claim Handler
### Introduction / Use case.

With this custom claim handler, we can get custom permissions along with the id token. Refer \[1] more more details.

### Applicable product versions.
Tested with IS-5.3.0

### How to use.
            
1. Stop the server if it is already running.

2. Build the project and copy the JAR file __org.wso2.permission.claim.handler-1.0.jar__ to the __<IS_HOME>/repository/components/dropins__ directory.

3. Change the <ClaimHandler> property of __<IS_HOME>/repository/conf/identity/application-authentication.xml__ file, with the custom handler's fully qualified name (for this example it's ```org.wso2.custom.claim.PermissionClaimHandler```).

4. Start the server

### Testing the project.

Note: In order to retrieve application's role's permission through id token, you have to enable the property __GetAllRolesOfUserEnabled__ in user-mgt.xml file. By default it is disabled.

5. Create a new Local Claim to represent permissions. (In this sample I have created a local claim with claim uri ```http://wso2.org/claims/permission```) (If you wish to use a different claim uri then you have to change the value of the constant ```PERMISSION_CLAIM``` in the code).

6. Create an External Claim with ```http://wso2.org/oidc/claim``` as _Dialect URI_ and for the _Mapped Local Claim_, select the local claim created above. For this sample it will be ```http://wso2.org/claims/permission```. (Let's say my External Claim URI is permission)

7. Create a new service provider and add the created claim ```http://wso2.org/claims/permission``` to _Requested Claims_ of the Service provider (Option available in Claim Configuration tab in service provider section).

8. Add the name of the external claim (My case it is permission) to the OIDC config file located in the registry __/_system/config/oidc__. You have to add the claim under the scope __openid__.

Sample request for implicit grant with response type id token would be:- 

https://localhost:9443/oauth2/authorize?response_type=id_token&client_id=<client_id>&scope=openid&redirect_uri=<redirect_uri>&nonce=<random_value>

Parse the retrieved id token then sample JSON would be as follows :- 
```
{
  "sub": "nilasini",
  "aud": [
    "LpCaE3Tyhf_ksaXJ7Pvq_Ke6gjca"
  ],
  "role": [
    "Internal/everyone",
    "login",
    "customRole"
  ],
  "azp": "LpCaE3Tyhf_ksaXJ7Pvq_Ke6gjca",
  "iss": "https://localhost:9443/oauth2/token",
  "permission": [
    "/permission/admin/login",
    " /permission/applications/playground2.com/mycustomclaim"
  ],
  "exp": 1532214545,
  "nonce": "qwdqdq",
  "iat": 1532210945
}
```
\[1] https://medium.com/@nilasini/retrieve-service-provider-wise-custom-permissions-as-a-claim-in-id-token-785a94ff5793
