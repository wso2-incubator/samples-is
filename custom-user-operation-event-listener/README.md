# Custom User Operation Event Listener

### Introduction / Use case.
This user operation event listener is designed to assign password policy satisfied random passwords for ask password users.
Refer [1] to understand the implementation details.

### Applicable product versions.
Tested with IS-5.5.0

### How to use.
1. Stop the server if it is already running.
2. Build the project using the command ```mvn clean install```
3. Copy the JAR file __org.wso2.carbon.sample.user.operation.event.listener-1.0.0.jar__ from the target directory to __<IS_HOME>/repository/components/dropins__ folder
4. Start the server.

### Testing the project.
1. Enable ask password and password policy feature.
2. Add a user with ask password option.
3. User will be added successfully.

[1] https://medium.com/@nilasini/handle-password-policies-while-using-ask-password-option-in-wso2is-5-5-0-79335e64c148
