# Custom Log4j2 Appender

### Introduction / Use case.

If you have a requirement to listen to the logs published from Identity Server, perform some business logic, and
 optionally publish data, this Log4j2 appender can be used. This sample has two components. One is log4j2 appender
  which will handle log events. Other one is an OSGi bundle which is deployed in the Identity Server runtime. This
   bundle is responsible to consume any service in the Identity Server and perform any business logic needs to be done
    with the log event. 

### Applicable product versions.
Tested with IS-5.10.0

### How to use.
1. Build the Custom Log4j2 Appender source using ```mvn clean install``` command.
2. Copy the two JAR files into __<IS_HOME>/repository/components/dropins__ directory.
3. Add following log4j2 configs to the repository/conf/log4j2.properties file.
   ```properties
   appender.CustomLog4j2LogAppender.type = CustomLog4j2LogAppender
   appender.CustomLog4j2LogAppender.name = CustomLog4j2LogAppender
   appender.CustomLog4j2LogAppender.filter.1.type = RegexFilter
   appender.CustomLog4j2LogAppender.filter.1.regex = .*\\| Action : Login \\|.*|.*\\| Action : Password reset \\|.*|.*\\| Action : Account Unlock \\|.*|.*\\| Action : Account Lock \\|.*
   appender.CustomLog4j2LogAppender.filter.1.onMatch = ACCEPT
   appender.CustomLog4j2LogAppender.filter.1.onMismatch = DENY
   appender.CustomLog4j2LogAppender.layout.type = PatternLayout
   appender.CustomLog4j2LogAppender.layout.pattern = TID: [%tenantId] [%d] [%X{Correlation-ID}] %5p {%c} - %mm%ex%n
   ```
4. Add the above appender as a reference to the AUDIT logger.
   ```properties
   ...
   logger.AUDIT_LOG.appenderRef.CustomLog4j2LogAppender.ref = CustomLog4j2LogAppender
   ...
   ```
5. Add the appender to the appenders list.
   ````properties
   appenders = ..., CustomLog4j2LogAppender
   ````
3. Start the server.

### Testing the project.
This sample appender is configured with a regex filter to filter out audit logs other than account lock, login and
 password reset. Therefore once deployed, please perform a login to a service provider and observer logs in the
  console. For more information, check the code. 
