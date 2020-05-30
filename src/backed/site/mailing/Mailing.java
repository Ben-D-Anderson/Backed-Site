package backed.site.mailing;

import backed.site.util.Settings;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Mailing {

	private final String email = (String) Settings.getInstance().getConfig().getMailing().get("email");
	private final String password = (String) Settings.getInstance().getConfig().getMailing().get("password");
	private final String host = (String) Settings.getInstance().getConfig().getMailing().get("host");
	private final int port = ((Double) Settings.getInstance().getConfig().getMailing().get("port")).intValue();

	private Session session;
	private static Mailing instance;

	public Mailing() {
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.socketFactory.port", String.valueOf(port));
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", String.valueOf(port));
		
		session = Session.getDefaultInstance(props,
				new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(email, password);
			}
		});
	}

	public static Mailing getInstance() {
		if (instance == null) instance = new Mailing();
		return instance;
	}
	
	public void sendEmail(String to, PremadeEmail premadeEmail) {
		MailingQueue.getInstance().addToQueue(() -> {
			try {
				MimeMessage message = new MimeMessage(session);
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
				message.setSubject(premadeEmail.getTitle().toString());
				message.setContent(premadeEmail.getContent().toString(), "text/html");

				Transport.send(message);
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
		});
	}
	
}