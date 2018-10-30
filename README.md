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
 - [Custom Carbon Log Appender](custom-carbon-log-appender/) - A carbon log appender that can intercept and modify logs of WSO2 Identity Server.
 - [Custom Claim Handler](custom-claim-handler/) - A custom claim handler that is capable of adding some external claims.
 - [Custom Permission Claim Handler](custom-permission-claim-handler/) - A custom claim handler that is capable of getting custom permissions along with the id token.
