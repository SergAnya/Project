package com.bykov.project.conference.services;

import com.bykov.project.conference.exceptions.FailedToSendException;
import com.bykov.project.conference.exceptions.TooEarlyDateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.bykov.project.conference.dto.ResourceEnum;
import com.bykov.project.conference.dao.DaoConference;
import com.bykov.project.conference.dao.DaoFactory;
import com.bykov.project.conference.dto.DTOSubscription;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;


public class ServiceMailSend implements Runnable {
    private final static Logger LOG = LogManager.getLogger(ServiceMailSend.class);
    private final static ResourceBundle EMAIL_BUNDLE = ResourceBundle.getBundle(ResourceEnum.EMAIL.getBundleName());
    private final static ResourceBundle ACCESS_BUNDLE = ResourceBundle.getBundle(ResourceEnum.ACCESS.getBundleName());
    private static final DaoConference DAO_CONFERENCE = DaoFactory.getInstance().getConferenceDao();
    private static final ServiceUser SERVICE_USER = new ServiceUser();
    private final String username = ACCESS_BUNDLE.getString("email.app.address");
    private final String password = ACCESS_BUNDLE.getString("email.password");

    @Override
    public void run() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        LOG.trace("Authenticated");
        //sending emails in one session for better performance
        try {
            Transport transport = session.getTransport("smtp");
            transport.connect();
            LOG.trace("Transport connected");

            List<Message> messages = buildMessages(session, DAO_CONFERENCE.getSubscriptionsList("en_US"));
            for (Message message : messages) {
                Transport.send(message);
            }

            transport.close();
            LOG.info("All messages have been sent");
        } catch (MessagingException e) {
            LOG.info("Cant send" + e);
            throw new FailedToSendException(e);
        }

    }


    private List<Message> buildMessages(Session session, List<DTOSubscription> subscriptions) throws MessagingException {
        List<Message> messages = new ArrayList<>();
        List<String> subscribedEmails = SERVICE_USER.getUserSubscribedEmails();
        List<Long> conferenceIds = DAO_CONFERENCE.getAllConferenceIdsInSubscriptions();

        for (String email : subscribedEmails) {
            for (Long id : conferenceIds) {
                if (isAppropriateSubscription(email, id, subscriptions)) {
                    try {
                        messages.add(createMessage(session, email, getAppropriateSubscriptions(email, id, subscriptions)));
                    } catch (TooEarlyDateException ignored) {
                        LOG.trace("Too early date of conference to notificate");
                    }
                }
            }
        }

        return messages;

    }


    private Message createMessage(Session session, String userEmail, List<DTOSubscription> subscriptions) throws MessagingException, TooEarlyDateException {
        Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress(EMAIL_BUNDLE.getString("app.email.address")));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
        message.setSubject(EMAIL_BUNDLE.getString("email.subject"));

        if (!checkDate(subscriptions.get(0).getConferenceDateTime())) {
            throw new TooEarlyDateException();
        }

        StringBuilder emailText = new StringBuilder()
                .append(EMAIL_BUNDLE.getString("email.text.start"))
                .append(subscriptions.get(0).getUserName())
                .append(" ")
                .append(subscriptions.get(0).getUserSurname())
                .append(EMAIL_BUNDLE.getString("email.text.why"))
                .append(EMAIL_BUNDLE.getString("email.text.conference"))
                .append(subscriptions.get(0).getConferenceTopic())
                .append(EMAIL_BUNDLE.getString("email.text.location"))
                .append(subscriptions.get(0).getConferenceLocation())
                .append(EMAIL_BUNDLE.getString("email.text.reports"));
        LOG.trace("Starting to put reports in email");

        subscriptions.forEach(subscription -> {
            if (checkDate(subscription.getReportDateTime())) {
                emailText.append(EMAIL_BUNDLE.getString("email.text.report.date.time"))
                        .append(subscription.getReportDateTime().format(
                                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
                        .append(EMAIL_BUNDLE.getString("email.text.report.topic"))
                        .append(subscription.getReportTopic())
                        .append(EMAIL_BUNDLE.getString("email.text.speaker"))
                        .append(subscription.getSpeakerName())
                        .append(" ")
                        .append(subscription.getSpeakerSurname());
            }
        });
        emailText.append(EMAIL_BUNDLE.getString("email.text.thanks"));
        message.setText(emailText.toString());

        return message;
    }


    private boolean checkDate(LocalDateTime dateTime) {
        return DAYS.between(LocalDateTime.now(), dateTime) < Long.parseLong(EMAIL_BUNDLE.getString("notificate.before.days"));
    }


    private List<DTOSubscription> getAppropriateSubscriptions(String email, Long conferenceId, List<DTOSubscription> subscriptions) {
        return subscriptions.stream()
                .filter(subscription -> subscription.getUserEmail().equals(email) && subscription.getConferenceId() == conferenceId)
                .collect(Collectors.toCollection(ArrayList::new));
    }


    private boolean isAppropriateSubscription(String email, Long id, List<DTOSubscription> subscriptions) {
        return getAppropriateSubscriptions(email, id, subscriptions).size() > 0;
    }

}


