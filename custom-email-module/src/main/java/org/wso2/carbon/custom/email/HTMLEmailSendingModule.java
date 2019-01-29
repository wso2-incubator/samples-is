/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.custom.email;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.TransportOutDescription;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.CarbonConfigurationContextFactory;
import org.wso2.carbon.identity.mgt.mail.AbstractEmailSendingModule;
import org.wso2.carbon.identity.mgt.mail.Notification;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * HTML email sending implementation
 */
public class HTMLEmailSendingModule extends AbstractEmailSendingModule {

    private static Log log = LogFactory.getLog(HTMLEmailSendingModule.class);
    Session session = null;
    private BlockingQueue<Notification> notificationQueue = new LinkedBlockingDeque<Notification>();

    @Override
    public void sendEmail() {

        try {
            Notification notification = notificationQueue.take();
            ConfigurationContext configContext = CarbonConfigurationContextFactory
                    .getConfigurationContext();
            TransportOutDescription transportOutDescription = null;
            if (configContext != null) {
                transportOutDescription =
                        configContext.getAxisConfiguration().getTransportOut(EmailConstants.MAIL_TO);
            }
            String smtpFrom =
                    transportOutDescription.getParameter(EmailConstants.MAIL_SMTP_FROM).getValue().toString();
            String contentType;
            if (transportOutDescription.getParameter(EmailConstants.MAIL_CONTENT_TYPE) != null) {
                contentType =
                        transportOutDescription.getParameter(EmailConstants.MAIL_CONTENT_TYPE).getValue().toString();
            } else {
                contentType = "text/plain";
            }
            Message message = new MimeMessage(getSessionObject());
            Address[] addresses = InternetAddress.parse(notification.getSendTo());
            message.setFrom(new InternetAddress(smtpFrom));
            message.setRecipients(Message.RecipientType.TO, addresses);
            message.setSubject(notification.getSubject());
            StringBuilder contents = new StringBuilder();
            contents.append(notification.getBody())
                    .append(System.getProperty("line.separator"))
                    .append(System.getProperty("line.separator"))
                    .append(notification.getFooter());
            message.setContent(contents.toString(), contentType);
            Transport.send(message);
            if (log.isDebugEnabled()) {
                log.debug("Email content : " + notification.getBody());
                log.debug("Email notification has been sent to " + notification.getSendTo());
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting until an element becomes available in the notification queue" +
                    " of the HTML Email Sending Module", e);
        } catch (AddressException e) {
            log.error("Error occurred while retriving the user's email address" +
                    " of the HTML Email Sending Module", e);
        } catch (MessagingException e) {
            log.error("Error occurred while setting parameters to the mime message" +
                    " of the HTML Email Sending Module", e);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    @Override
    public Notification getNotification() {

        return notificationQueue.peek();
    }

    @Override
    public void setNotification(Notification notification) {

        notificationQueue.add(notification);
    }

    /**
     * @return the javax session object which contains the smtp host, port, starttls, auth, user and password
     */
    public Session getSessionObject() {

        if (session == null) {
            ConfigurationContext configContext = CarbonConfigurationContextFactory
                    .getConfigurationContext();
            TransportOutDescription transportOutDescription = null;
            if (configContext != null) {
                transportOutDescription =
                        configContext.getAxisConfiguration().getTransportOut(EmailConstants.MAIL_TO);
            }
            String smtpHost =
                    transportOutDescription.getParameter(EmailConstants.MAIL_SMTP_HOST).getValue().toString();
            String smtpPort =
                    transportOutDescription.getParameter(EmailConstants.MAIL_SMTP_PORT).getValue().toString();
            String smtpStarttls =
                    transportOutDescription.getParameter(EmailConstants.MAIL_SMTP_STARTTLS).getValue().toString();
            String smtpAuth =
                    transportOutDescription.getParameter(EmailConstants.MAIL_SMTP_AUTH).getValue().toString();
            final String smtpUser = transportOutDescription.getParameter(EmailConstants.MAIL_SMTP_USER).getValue()
                    .toString();
            final String smtpPassword =
                    transportOutDescription.getParameter(EmailConstants.MAIL_SMTP_PASSWORD).getValue().toString();
            Properties properties = new Properties();
            properties.put(EmailConstants.MAIL_SMTP_HOST, smtpHost);
            properties.put(EmailConstants.MAIL_SMTP_PORT, smtpPort);
            properties.put(EmailConstants.MAIL_SMTP_STARTTLS, smtpStarttls);
            properties.put(EmailConstants.MAIL_SMTP_AUTH, smtpAuth);
            session = Session.getInstance(properties,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(smtpUser, smtpPassword);
                        }
                    });
        }
        return session;
    }
}
