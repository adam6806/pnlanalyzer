package com.github.adam6806.pnlanalyzer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.adam6806.pnlanalyzer.security.user.Invite;
import com.sendgrid.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class SendGridEmailService {

    private SendGrid sendGridClient;

    @Autowired
    public SendGridEmailService(SendGrid sendGridClient) {
        this.sendGridClient = sendGridClient;
    }

    public void sendText(String from, String to, String subject, String body) {
        Response response = sendEmail(from, to, subject, new Content("text/plain", body));
        System.out.println("Status Code: " + response.getStatusCode() + ", Body: " + response.getBody() + ", Headers: "
                + response.getHeaders());
    }

    public void sendHTML(String from, String to, String subject, String body) {
        Response response = sendEmail(from, to, subject, new Content("text/html", body));
        System.out.println("Status Code: " + response.getStatusCode() + ", Body: " + response.getBody() + ", Headers: "
                + response.getHeaders());
    }

    private Response sendEmail(String from, String to, String subject, Content content) {
        Mail mail = new Mail(new Email(from), subject, new Email(to), content);
        mail.setReplyTo(new Email("abc@gmail.com"));
        Request request = new Request();
        Response response = null;
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            this.sendGridClient.api(request);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return response;
    }

    public Response sendUserInvite(Invite invite) {

        Mail mail = new Mail();

        Email fromEmail = new Email();
        fromEmail.setName("PNL Analyzer");
        fromEmail.setEmail("asmith0935@gmail.com");
        mail.setFrom(fromEmail);


        DynamicPersonalization personalization = new DynamicPersonalization();
        Email to = new Email();
        to.setName(invite.getFirstName() + " " + invite.getLastName());
        to.setEmail(invite.getEmail());
        personalization.addTo(to);
        personalization.addDynamicTemplateData("firstname", invite.getFirstName());
        personalization.addDynamicTemplateData("adminfirstname", invite.getAdminFirstName());
        personalization.addDynamicTemplateData("adminlastname", invite.getAdminLastName());
        personalization.addDynamicTemplateData("baseurl", System.getenv("INVITE_BASE_URL"));
        personalization.addDynamicTemplateData("inviteId", invite.getId().toString());
        mail.addPersonalization(personalization);
        mail.setTemplateId("d-f3ddeb9047924a8994c21a7cb09deeb1");
        Request request = new Request();
        Response response = null;
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            this.sendGridClient.api(request);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return response;
    }

    class DynamicPersonalization extends Personalization {

        @JsonProperty("dynamic_template_data")
        private Map<String, String> dynamicTemplateData;

        @JsonProperty("dynamic_template_data")
        public Map<String, String> getDynamicTemplateData() {
            return this.dynamicTemplateData == null ? Collections.emptyMap() : this.dynamicTemplateData;
        }

        public void addDynamicTemplateData(String key, String value) {
            if (this.dynamicTemplateData == null) {
                this.dynamicTemplateData = new HashMap();
                this.dynamicTemplateData.put(key, value);
            } else {
                this.dynamicTemplateData.put(key, value);
            }

        }
    }
}
