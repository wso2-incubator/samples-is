# Custom Password Generator

### Introduction / Use case.

If you have your own password policies within your organization, you can create random passwords which satisfies your password policies by writing a custom password generator.
This particular custom password generator will generate passwords with length 9. You can modify the code according to your requirements, to generate more policy satisfying passwords.

### Applicable product versions.
Tested with IS-5.3.0

### How to use.
1. Build the custom password generator source using ```mvn clean install``` command.
2. Copy the JAR file into __<IS_HOME>/repository/components/lib__ directory.
3. Start the server.

### Testing the project.
You need to configure your custom password generator class form the management console in order to use it instead of the _DefaultPasswordGenerator_. Please do the following steps.
1. Click _Resident_ under _Identity Providers_ found in the Main tab of the IS management console.
2. Expand the _Account Management Policies_ tab.
3. Under _User Onboarding_ section, replace the value of Temporary password generation extension class by specifying your custom password generator.
4. Configure password policy to length 9.
5. Create a user using ask password option. User will be created successfully.
