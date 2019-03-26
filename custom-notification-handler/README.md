# Custom Notification Handler

### Introduction / Use case.
This custom notification handler can be used to achieve below requirements

1. Change the email template according to the user role during password recovery email notification flow.
2. Change the email template by passing a request attribute (to decide the email template type) during user
registration.

### Applicable product versions.
WSO2 IS 5.3.0

### How to use.
1. Build the custom-notification-handler using the command `mvn clean install`.
2. Copy the  copy the org.wso2.carbon.sample.CustomNotificationHandler-1.0.jar into **<PRODUCT_HOME>/repository/components/dropins** folder.
3. Open the **<PRODUCT_HOME>/repository/identity/identity-event.properties** and locate the following default
properties.
```
module.name.2=emailSend
emailSend.subscription.1=TRIGGER_NOTIFICATION
```
Change the above configuration as shown below
```
module.name.2=customEmailSend
customEmailSend.subscription.1=TRIGGER_NOTIFICATION
```
4. Start the server.

 ### Testing the project.
1. In the management console, browse to the Manager -> Email Templates -> Add  and add the custom email template name.
2. Browse to the Manager -> Email Templates -> Add and add the custom email template content.


#### Change the email template according to the user role during password recovery email notification flow

In order to achieve this requirement, we will have to add the role to email template mappings as a registry configuration.

1. Login to the management console.
2. Navigate to Main -> Registry -> Browse
3. Browse to the registry location **/_system/config/identity/config** (From the tree view travel to _system -> config -> identity -> config)
4. Add a new registry resource as the name "customRoleBasedEmailTemplateTypeConfig" (use the Method as "Create Text content").
5. Browse to the newly created registry resource (**/_system/config/identity/config/customRoleBasedEmailTemplateTypeConfig**).
6. Expand the properties tab and add the  property value pairs of role name and corresponding email template type for the role.

```
   propertyName= <role name> propertyValue= <custom email template type>
```
```
   e.g. propertyName = role1 propertyValue = PasswordResetForRole1
```
Then, when ever a password reset request is being sent, the custom notification handler will check whether the configured roles in the registry exists for the user. If such role exists, the custom email template type will be replaced with the email template type added in the registry property.



####  Change the email template by passing a request attribute (to decide the email template type) during user registration.


When calling the self user registration REST API, we need to add the desired email template type in the request parameter map under key "customTemplateType"

Sample request would be
```
curl -X POST -H "Authorization: Basic YWRtaW46YWRtaW4=" -H "Content-Type: application/json" -d '{"user": {"username": "kasun","realm": "PRIMARY", "password": "kasun","claims": [{"uri": "http://wso2.org/claims/givenname","value": "kim" },{"uri": "http://wso2.org/claims/emailaddress","value": "kasun@gmail.com"},{"uri": "http://wso2.org/claims/lastname","value": "Anderson"},{"uri": "http://wso2.org/claims/mobile","value": "+947721584558"} ] },"properties": [{"key":"customTemplateType","value":"CustomAccountConfirmationTemplateType"}]}' "https://localhost:9443/api/identity/user/v0.9/me" -k
```
Here the "CustomAccountConfirmationTemplateType" is the name of the custom email template type.

