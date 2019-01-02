<%--
  ~ Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
  ~
  ~  WSO2 Inc. licenses this file to you under the Apache License,
  ~  Version 2.0 (the "License"); you may not use this file except
  ~  in compliance with the License.
  ~  You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied.  See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  --%>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.wso2.carbon.identity.mgt.endpoint.IdentityManagementEndpointConstants" %>
<%@ page import="org.wso2.carbon.identity.mgt.endpoint.IdentityManagementEndpointUtil" %>
<%@ page import="org.wso2.carbon.identity.mgt.endpoint.client.ApiException" %>
<%@ page import="org.wso2.carbon.identity.mgt.endpoint.client.api.NotificationApi" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="org.wso2.carbon.identity.mgt.endpoint.client.model.*" %>
<%@ page import="org.wso2.carbon.identity.mgt.endpoint.client.model.Error" %>
<%@ page import="org.owasp.encoder.Encode" %>
<%@page import = "java.util.logging.Logger" %>
<jsp:directive.include file="localize.jsp"/>

<%
    Logger logger = Logger.getLogger(this.getClass().getName());
    String passwordHistoryErrorCode = "20035";
    String passwordPatternErrorCode = "22001";

    String otp =
            IdentityManagementEndpointUtil.getStringValue(request.getSession().getAttribute("confirmationKey"));

    String newPassword = request.getParameter("reset-password");
    String callback = request.getParameter("callback");
    String confirmationKey = request.getParameter("confirmationKey");
    String tenantDomain = request.getParameter(IdentityManagementEndpointConstants.TENANT_DOMAIN);
    String username = request.getParameter("username");
    String relayingParty = request.getParameter("relyingParty");
    String sp = request.getParameter("sp");
    String commonAuthCallerPath = request.getParameter("commonAuthCallerPath");
    String sessionDataKey = request.getParameter("sessionDataKey");
    logger.info("---------- otp:" + otp );
    logger.info("---------- confirmationkey:" + confirmationKey);

    if (StringUtils.isBlank(callback)) {
        callback = IdentityManagementEndpointUtil.getUserPortalUrl(
                application.getInitParameter(IdentityManagementEndpointConstants.ConfigConstants.USER_PORTAL_URL));
    }
    if (StringUtils.isNotBlank(newPassword)) {

        NotificationApi notificationApi = new NotificationApi();

        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();
        List<Property> properties = new ArrayList<Property>();
        Property property = new Property();
        property.setKey("callback");
        property.setValue(URLEncoder.encode(callback, "UTF-8"));
        properties.add(property);

        Property tenantProperty = new Property();
        tenantProperty.setKey(IdentityManagementEndpointConstants.TENANT_DOMAIN);
        if (tenantDomain == null) {
            tenantDomain = IdentityManagementEndpointConstants.SUPER_TENANT;
        }
        tenantProperty.setValue(URLEncoder.encode(tenantDomain, "UTF-8"));
        properties.add(tenantProperty);



        resetPasswordRequest.setKey(confirmationKey);
        resetPasswordRequest.setPassword(newPassword);
        resetPasswordRequest.setProperties(properties);

        try {
            notificationApi.setPasswordPost(resetPasswordRequest);
        } catch (ApiException e) {

            Error error = new Gson().fromJson(e.getMessage(), Error.class);
            request.setAttribute("error", true);
            if (error != null) {
                request.setAttribute("errorMsg", error.getDescription());
                request.setAttribute("errorCode", error.getCode());
            }

            if (passwordHistoryErrorCode.equals(error.getCode()) || passwordPatternErrorCode.equals(error.getCode())) {
                request.getRequestDispatcher("custom-password-reset.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("error.jsp").forward(request, response);
            }
            return;
        }


    } else {
        request.setAttribute("error", true);
        request.setAttribute("errorMsg", IdentityManagementEndpointUtil.i18n(recoveryResourceBundle,
                "Password.cannot.be.empty"));
        request.getRequestDispatcher("custom-password-reset.jsp").forward(request, response);
        return;
    }

    
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link href="libs/bootstrap_3.3.5/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/Roboto.css" rel="stylesheet">
    <link href="css/custom-common.css" rel="stylesheet">
</head>
<body>
<div class="container">

    <div id="infoModel" class="modal fade" role="dialog">
   
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                   
                    <h4 class="modal-title">
                        <%=IdentityManagementEndpointUtil.i18n(recoveryResourceBundle,"Information")%>
                    </h4>
                </div>
                 <form method="post" action="/commonauth" id="passwordResetCompleteForm1">
                <div>
                    <p>
                        <%=IdentityManagementEndpointUtil.i18n(recoveryResourceBundle,"Updated.the.password.successfully")%>
                    </p>
                </div>
                
                <!-- custom part -->
                            <%       
                
                            if (callback != null) {
                            %>
                            <div>
                                <input type="hidden" name="callback" value="<%=Encode.forHtmlAttribute(callback)%>"/>
                            </div>
                            <%
                                }
                            %>
                            <%
                                if (tenantDomain != null) {
                            %>
                            <div>
                                <input type="hidden" name="tenantdomain"
                                       value="<%=Encode.forHtmlAttribute(tenantDomain)%>"/>
                            </div>
                            <%
                                }
                            %>
                            <%
                                if (username != null) {
                            %>
                            <div>
                                <input type="hidden" name="username"
                                       value="<%=Encode.forHtmlAttribute(username)%>"/>
                            </div>
                            <%
                                }
                            %>
                                <%
                                if (confirmationKey != null) {
                            %>
                            <div>
                                <input type="hidden" name="confirmationKey"
                                       value="<%=Encode.forHtmlAttribute(confirmationKey)%>"/>
                            </div>
                                <%
                                }
                            %>
                                <%
                                if (relayingParty != null) {
                            %>
                            <div>
                                <input type="hidden" name="relayingParty"
                                       value="<%=Encode.forHtmlAttribute(relayingParty)%>"/>
                            </div>
                                <%
                                }
                            %>
                            <%
                                if (sp != null) {
                            %>
                            <div>
                                <input type="hidden" name="sp"
                                       value="<%=Encode.forHtmlAttribute(sp)%>"/>
                            </div>
                                <%
                                }
                            %>
                            
                            <%
                                if (sessionDataKey != null) {
                            %>
                            <div>
                                <input type="hidden" name="sessionDataKey"
                                       value="<%=Encode.forHtmlAttribute(sessionDataKey)%>"/>
                            </div>
                                <%
                                }
                            %>
                            
                             <div>
                                <input type="hidden" name="reset-password"
                                       value="<%=Encode.forHtmlAttribute(newPassword)%>"/>
                            </div>
                
                    </form>
                
                    <!-- finish submitting -->
                
                <div class="modal-footer">
                    <button type="submit" id="btn-submit" class="btn btn-default" data-dismiss="modal">
                        <%=IdentityManagementEndpointUtil.i18n(recoveryResourceBundle,"Close")%>
                    </button>
                </div>
            </div>
        </div>
    </div>

</div>
<script src="libs/jquery_1.11.3/jquery-1.11.3.js"></script>
<script src="libs/bootstrap_3.3.5/js/bootstrap.min.js"></script>
<script type="application/javascript">
    $(document).ready(function () {
        $("#passwordResetCompleteForm1").submit();
        
    });
</script>
</body>
</html>
