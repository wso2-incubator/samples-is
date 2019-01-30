## Custom Grant Type - BioMetric Grant
### Introduction / Use case.
This custom grant type can be used to authenticate users using there bio metric data. Please note that you need a way to collect users biometric data and send them with the token request and an external service that can validate those bio data. This is only a sample that provides the required infrastructure. 

### Applicable product versions.
Tested with IS-5.3.0

### How to use.
1) Stop the server if it is already running.
2) Build the project using following command,
  ```mvn clean install```
3) Copy the jar file __biometric-grant-1.0.0.jar__ from the target directory to __<IS_HOME>/repository/components/dropins__ folder.
4) Configure the following in the __<IS_HOME>/repository/conf/identity/identity.xml__ file under the __SupportedGrantTypes__ element and start the server.
    ```
    <SupportedGrantType>
        <GrantTypeName>biometric</GrantTypeName>
        <GrantTypeHandlerImplClass>org.wso2.sample.identity.oauth2.grant.biometric.BiometricGrant</GrantTypeHandlerImplClass>
        <GrantTypeValidatorImplClass>org.wso2.sample.identity.oauth2.grant.biometric.BiometricGrantValidator</GrantTypeValidatorImplClass>
    </SupportedGrantType>
    ```

### Testing the project.
6) Create a new service provider by clicking __Add__ under __Service Providers__ section.
5) Then click on __Inbound Authentication Configuration__ > __OAuth/OpenID Connect Configuration__ and click on __Configure__.
5) You should see biometric grant is listed with Allowed Grant Types. Provide a call back URI and save the configuration.
5) Then you can use the generated client ID and the client secret to invoke a token request using this new custom grant, as follows.
    ```
    curl --user <CLIENT_ID>:<CLIENT_SECRET> -k -d "grant_type=biometric&biodata=<BIO_DATA>" -H "Content-Type: application/x-www-form-urlencoded" https://localhost:9443/oauth2/token
    ```
5) Below is a sample response.
    ```
    {"access_token":"cfd64675-3803-3c8d-85f6-88fb13a059b9","refresh_token":"61f7dc1a-df99-3e1f-b767-9163cba8a824","token_type":"Bearer","expires_in":3640}
    ```

### Making modifications.
1) You can use the method [isValidBioData](/src/main/java/org/wso2/sample/identity/oauth2/grant/biometric/BiometricGrant.java#L104) method to call the external web service to validate the users bio metric data.

