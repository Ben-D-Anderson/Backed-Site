package com.terraboxstudios.backed.site.mailing;

import com.terraboxstudios.backed.site.util.Settings;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class Mailing {

	private final String email = (String) Settings.getInstance().getConfig().getMailing().get("email");
	private final String password = (String) Settings.getInstance().getConfig().getMailing().get("password");
	private final String host = (String) Settings.getInstance().getConfig().getMailing().get("host");
	private final int port = ((Double) Settings.getInstance().getConfig().getMailing().get("port")).intValue();

	private static Mailing instance;

	public static Mailing getInstance() {
		if (instance == null) instance = new Mailing();
		return instance;
	}
	
	public void sendEmail(String to, PremadeEmail premadeEmail) {
		MailingService.getInstance().addToQueue(() -> {
			try {
				Properties props = new Properties();
				props.put("mail.smtp.host", host);
				props.put("mail.smtp.port", port);
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");

				Session session = Session.getDefaultInstance(props,
						new Authenticator() {
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(email, password);
							}
						});
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(email));
				message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
				message.setSubject(premadeEmail.getTitle().toString());
				message.setContent(premadeEmail.getContent().toString(), "text/html");
				message.setSentDate(new Date());

				Transport.send(message);
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
		});
	}
	
}