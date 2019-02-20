## Custom PIP Attribute Finder.
### Introduction / Use case.
The main purpose of this attribute finder is mapping external user store role to an internal role. The exact use case is as follows.

We have a resource server that provide access to certain resources based on the user's roles, using XACML policies. For example, the system is configured to provide admin access if the user has the role **hospitaladmin**. Then we need to plug an external user store as the secondary and provide access to the system for those users too. Since this external user store has it's own roles and system only knows internal roles, we need to have a mapping and when evaluating XACML policies, we need to present the mapped internal roles for the users.

For this particular use case, we provide the role mapping in JSON format, as a property in the secondary user store configuration.  

- Property name: RoleMappingJSON
- Sample JSON: 
  ```
    [
        {
            "externalRole": "ge/admin",
            "internalRole": [
                "hospitaladmin",
                "hosptialadmin2"
            ]
        },
        {
            "externalRole": "ge/manager",
            "internalRole": [
                "hospitalmanger"
            ]
        }
    ]
  ```
  
In addition, we've implemented a simple caching layer on top of the attribute finder to avoid reading the user store configuration file each time. We also have an admin service exposed so that the cache can be cleared from an external SOAP call. 

### Applicable product versions.
Originally designed for WSO2 Identity Server 5.7.0

### How to use.
1. Build the project using the command ```mvn clean install```.
2. Copy the JAR file **target/org.wso2.custom.pip.attribute.finder-1.0.0.jar** to the **<IS_HOME>/repository/components/dropins** directory.
3. Open the **<IS_HOME>/repository/conf/identity/entitlement.properties** file and change below entry,
    ```PIP.AttributeDesignators.Designator.1=org.wso2.carbon.identity.entitlement.pip.DefaultAttributeFinder```
to the following.
  ```  PIP.AttributeDesignators.Designator.1=org.wso2.custom.pip.attribute.finder.CustomAttributeFinder```
4. Open the **<IS_HOME>/repository/conf/identity/identity.xml** file and add below entry.
    ```
        <CacheConfig>
            ...
            <CacheManager name="IdentityApplicationManagementCacheManager">
                ...
                <Cache name="RoleMappingCache" enable="true" timeout="300" capacity="5000" isDistributed="false"/>
            </CacheManager>
        </CacheConfig>
	```
	Note: _timeout="300"_ is the cache invalidation time in seconds.
5. Add the role mapping JSON to the user store xml file, as explained above.

### Testing the project.
1. Enable debug logs for the level **org.wso2.custom** and start the WSO2 Identity Server.
2. Send an entitlement request to the server that engages role mapping. 
For example, a user in the external user store, who has the role ge/admin, tries to access admin services in the system. 
In order to grant access, system need the role **hospitaladmin** which is an internal role.
3. Custom attribute finder will get hit now and the Role Mapping Cache should populate for the first request, printing following debug logs.
    ```
    ... -  Role Mapping Cache is invalidated. Repopulating...
    ... -  Adding Role Mapping Cache entry. Key: ge/admin, values: [hospitaladmin, hosptialadmin2]
    ... -  Adding Role Mapping Cache entry. Key: ge/manager, values: [hospitalmanger]
    ... -  Role Mapping Cache population completed.
    ... -  Role Mapping Cache hit. Key: ge/admin, values: [hospitaladmin, hosptialadmin2]
    ```
4. Since the ge/admin -> hospitaladmin mapping is present, the XACML policy evaluation should return permit.
5. Again execute the same entitlement request and logs will not be printed as the decision cache is in place.
4. Execute a new entitlement request with a different user similar to the first one. Since this request is not in the decision cache, the attribute finder will get hit and the Role Mapping cache will be used, printing following logs.
    ```
    ... -  Role Mapping Cache hit. Key: ge/manager, values: [hospitalmanger]
    ```
    
### Role Mapping Cache Admin Service.

- We’ve implemented an admin service so that the role mapping cache can be invalidated through a SOAP call. 
- WSDL: https://localhost:9443/services/RoleMappingCacheAdminService?wsdl 
- This is a simple API that does not require any parameters. Once this is called, the Role Mapping Cache will be invalidated and a debug log will be printed as follows.
    ```
    ... -  Role Mapping Cache is cleared by using the admin service
    ```
Note: The use case here is to call this admin service when updating the role mapping in the user store xml file. Otherwise, it’ll take maximum of $Role_mapping_cache_invalidation time to reflect those updates in the system.
