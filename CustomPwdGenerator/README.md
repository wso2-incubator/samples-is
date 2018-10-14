# CustomPwdGenerator

### Introduction / Use case.
Create custom random password generator. If you have your own password policy constraint, then when you create the random password for ask password option, you can create a random password which satisfies your password policy constraints by writing a custom random password generator. This custom password generator which generates the password with length 9 and it doesn't full fill any password pattern policy, you have to change the code according to your requirements.

### Applicable product versions.
Tested with IS-5.3.0

### How to use.
1. Build the custom password generator source and get jar file from tatrget folder.
2. Copy the jar file into <IS_HOME>/repository/components/lib directory.
3. Start the server.

### Testing the project.
You need to configure your custom password generator class form the management console in order to use it instead of DefaultPasswordGenerator. Please do the following steps.
1. Click Resident under Identity Providers found in the Main tab of the IS management console.
2. Expand the Account Management Policies tab.
3. Under User Onboarding section replace the value of Temporary password generation extension classs by specifying the custom password generator.
4. Configure password policy to length 9
5. Create a user using ask password option. User will be created successfully.
