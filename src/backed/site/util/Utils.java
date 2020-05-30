package backed.site.util;

import org.apache.commons.validator.routines.EmailValidator;

public class Utils {

	public static boolean isValidEmail(String email) {
		return EmailValidator.getInstance().isValid(email);
	}

	public static String getPathOfEmail(String name) { return ClassPath.getInstance().getWebInfPath() + "emails/" + name + ".html"; }

}
