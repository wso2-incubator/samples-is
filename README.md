## Welcome to the WSO2 samples-is repository!
Sample IS is the repository where all samples related to WSO2 Identity Server are collected together. 

Please follow below steps to add new projects to the repository.
1. Add your project folder to the root of the repository.
2. Make sure to use non-sensitive, general and self-descriptive names for artifact-id, packages and classes.
2. Please make sure the project is buildable without any modifications.
4. Add the project to *Existing samples* section with a single line description.
3. Add a complete readme file inside the project folder. Please use the below template.

### README template

```
  ## <Project name>.
  ### Introduction / Use case.
  Please explain what your sample does and the use case. Use external links, images and diagrams if possible.
  
  ### Applicable product versions.
  List all WSO2 IS versions that can be used with this sample.
  
  ### How to use.
  Please provide detailed steps for using the sample. Include steps to building, patching, configurations that need to do, etc.
  
  ### Testing the project.
  Please provide steps for testing the sample. Provide example inputs and outputs as well.
  
 ```

### Existing samples.
 - [Custom Carbon Log Appender](custom-carbon-log-appender/) by [Vihanga Liyanage](https://github.com/vihanga-liyanage) - A carbon log appender that can intercept and modify logs of WSO2 Identity Server.
 - [Custom Claim Handler](custom-claim-handler/) by [Nilasini Thirunavukkarasu](https://github.com/nilasini) - A custom claim handler that is capable of adding some external claims.
 - [Custom Permission Claim Handler](custom-permission-claim-handler/) by [Nilasini Thirunavukkarasu](https://github.com/nilasini) - A custom claim handler that is capable of getting custom permissions along with the id token.
 - [Custom Password Generator](custom-password-generator/) by [Nilasini Thirunavukkarasu](https://github.com/nilasini) - Ability to generate random passwords that satisfies your own password policies.
 - [Custom User Operation Event Listener](custom-user-operation-event-listener/) by [Nilasini Thirunavukkarasu](https://github.com/nilasini) - Ability to generate random passwords that satisfies your own password policies.
 - [Custom Scope Handler](custom-scope-handler/) by [Abilashini Thiyagarajah](https://github.com/Abilashini) - A custom scope handler to remove the scopes which are not allowed from the token request and generate the access token with only the allowed scopes. 
 - [Custom Email Module](custom-email-module/) by [Abhishek De Silva](https://github.com/abhishekdesilva) - A custom email module to send notification emails with HTML content for the WSO2 IS 5.3.0 and below. 
 - [Custom Basic Authenticator for Force Password Reset via OTP](custom-basic-authenticator-for-force-password-otp/) by [Dinali Dabarera](https://github.com/gdrdabarera) - A custom basic authenticator which does force password reset via OTP.
 - [Custom Grant Type - BioMetric Grant](custom-grant-type/) by [Vihanga Liyanage](https://github.com/vihanga-liyanage) - A custom OAuth 2 grant type that can facilitate user authentication via biometric data.
 - [Custom PIP Attribute Finder](custom-pip-attribute-finder/) by [Vihanga Liyanage](https://github.com/vihanga-liyanage) - A custom PIP attribute finder that can map external user roles to internal roles based on a provided map.
 - [Custom Remote User Store Manager](custom-remote-user-store-manager/) by [Tharindu Bandara](https://github.com/tharindu-bandara) - An aggregator module to consume a remote user database as a secondary user store.
 - [Custom Authentication Request Handler](authentication-request-handler/) by [Vimukthi Perera](https://github.com/VimukthiPerera) - Authenticator that will authorize the users based on their role to a targeted SAML based application.
