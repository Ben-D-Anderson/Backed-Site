package backed.site.mysql;

import backed.site.util.Settings;

import javax.servlet.http.Cookie;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class MySQL {

	/*
	 * MYSQL BASE METHODS AND VARIABLES
	 */

	private final String username = (String) Settings.getInstance().getConfig().getMysql().get("username");
	private final String password = (String) Settings.getInstance().getConfig().getMysql().get("password");
	private final String host = (String) Settings.getInstance().getConfig().getMysql().get("host");
	private final String database = (String) Settings.getInstance().getConfig().getMysql().get("database");
	private final int port = ((Double) Settings.getInstance().getConfig().getMysql().get("port")).intValue();

	private static MySQL instance;
	
	public MySQL() {
		String[] tables = {
				"cookies (username VARCHAR(255), cookie VARCHAR(255), expiry BIGINT)",
				"users (username VARCHAR(255), email VARCHAR(255), password VARCHAR(255), salt BLOB, email_confirmed BOOLEAN)",
				"email_codes (code VARCHAR(255), username VARCHAR(255), expiry BIGINT)",
				"storage_ids (id VARCHAR(255), username VARCHAR(255))",
				"encryption_keys (enc_key TEXT, username VARCHAR(255))"
				};
		for (String table : tables) {
			try (Connection connection = getConnection()) {
				connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + table).execute();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		cacheAllUUIDs();
	}

	private void cacheAllUUIDs() {
		try (Connection connection = getConnection()) {
			PreparedStatement statement = connection
					.prepareStatement("SELECT id FROM storage_ids");
			ResultSet results = statement.executeQuery();
			while (results.next()) {
				registeredStorageIDs.add(results.getString("id"));
			}

			statement = connection
					.prepareStatement("SELECT code FROM email_codes");
			results = statement.executeQuery();
			while (results.next()) {
				registeredConfirmationCodes.add(results.getString("code"));
			}

			statement = connection
					.prepareStatement("SELECT cookie FROM cookies");
			results = statement.executeQuery();
			while (results.next()) {
				registeredCookies.add(results.getString("cookie"));
			}

			statement = connection
					.prepareStatement("SELECT enc_key FROM encryption_keys");
			results = statement.executeQuery();
			while (results.next()) {
				registeredEncryptionKeys.add(results.getString("enc_key"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static MySQL getInstance() {
		if (instance == null) instance = new MySQL();
		return instance;
	}

	private Connection getConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + 
					this.port + "/" + this.database + "?useSSL=false", this.username, this.password);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * ENCRYPTION KEY METHODS
	 */
	private List<String> registeredEncryptionKeys = new ArrayList<String>();

	public String generateEncryptionKey() {
		String value = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
		while (registeredEncryptionKeys.contains(value))
			value = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();

		registeredEncryptionKeys.add(value);
		return value;
	}

	public String getEncryptionKeyFromUsername(String username) {
		try (Connection connection = getConnection()) {
			PreparedStatement statement = connection
					.prepareStatement("SELECT enc_key FROM encryption_keys WHERE username=?");
			statement.setString(1, username);
			ResultSet results = statement.executeQuery();
			if (results.next()) {
				return results.getString("enc_key");
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setEncryptionKey(String encryptionKey, String username) {
		try (Connection connection = getConnection()) {
			PreparedStatement statement = connection
					.prepareStatement("INSERT INTO encryption_keys (enc_key, username) VALUES (?, ?)");
			statement.setString(1, encryptionKey);
			statement.setString(2, username);

			statement.execute();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * STORAGE ID METHODS
	 */

	private List<String> registeredStorageIDs = new ArrayList<String>();

	public String generateStorageID() {
		String value = UUID.randomUUID().toString();
		while (registeredStorageIDs.contains(value))
			value = UUID.randomUUID().toString();

		registeredStorageIDs.add(value);
		return value;
	}

	public String getStorageIDFromUsername(String username) {
		try (Connection connection = getConnection()) {
			PreparedStatement statement = connection
					.prepareStatement("SELECT id FROM storage_ids WHERE username=?");
			statement.setString(1, username);
			ResultSet results = statement.executeQuery();
			if (results.next()) {
				return results.getString("id");
			}
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setStorageID(String storageID, String username) {
		try (Connection connection = getConnection()) {
			PreparedStatement statement = connection
					.prepareStatement("INSERT INTO storage_ids (id, username) VALUES (?, ?)");
			statement.setString(1, storageID);
			statement.setString(2, username);

			statement.execute();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * HASHING AND SALT METHODS
	 */

	private byte[] getSalt(String username) {
		try (Connection connection = getConnection()) {
			PreparedStatement statement = connection
					.prepareStatement("SELECT salt FROM users WHERE username=?");
			statement.setString(1, username);
			ResultSet results = statement.executeQuery();
			if (results.next()) {
				return results.getBytes("salt");
			}
			return null;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		catch (NullPointerException e) {
			return null;
		}
		return null;
	}

	private String getSecurePassword(String password, byte[] salt) {
		if (salt == null) return "x";
		String generatedPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salt);
			byte[] bytes = md.digest(password.getBytes());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			generatedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedPassword;
	}

	private byte[] getRandomSalt() {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		return salt;
	}

	/*
	 * COOKIE METHODS
	 */

	public List<String> registeredCookies = new ArrayList<String>();

	public Cookie generateCookie() {
		String value = UUID.randomUUID().toString();
		while (registeredCookies.contains(value))
			value = UUID.randomUUID().toString();

		registeredCookies.add(value);
		return new Cookie("auth_session", value);
	}

	public String getUsernameFromCookie(String value) {
		try (Connection connection = getConnection()) {
			PreparedStatement statement = connection
					.prepareStatement("SELECT username FROM cookies WHERE cookie=?");
			statement.setString(1, value);
			ResultSet results = statement.executeQuery();
			if (results.next()) {
				return results.getString("username");
			}
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getCookieFromUsername(String username) {
		try (Connection connection = getConnection()) {
			PreparedStatement statement = connection
					.prepareStatement("SELECT cookie FROM cookies WHERE username=?");
			statement.setString(1, username);
			ResultSet results = statement.executeQuery();
			if (results.next()) {
				return results.getString("cookie");
			}
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean isCookieValid(String value) {
		long current = Calendar.getInstance().getTimeInMillis();
		try (Connection connection = getConnection()) {
			PreparedStatement statement = connection
					.prepareStatement("SELECT * FROM cookies WHERE cookie=? AND expiry>=?");
			statement.setString(1, value);
			statement.setLong(2, current);

			ResultSet results = statement.executeQuery();
			if (results.next()) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public void setCookie(String username, Cookie cookie) {
		String value = cookie.getValue();
		int age = cookie.getMaxAge();
		long expiry = Calendar.getInstance().getTimeInMillis() + (age * 1000);

		try (Connection connection = getConnection()) {
			if (getCookieFromUsername(username) != null) {
				PreparedStatement statement = connection.prepareStatement("UPDATE cookies SET cookie=?, expiry=? WHERE username=?");
				statement.setString(1, value);
				statement.setLong(2, expiry);
				statement.setString(3, username);

				statement.executeUpdate();
			} else {
				PreparedStatement statement = connection.prepareStatement("INSERT INTO cookies (username, cookie, expiry) VALUES (?, ?, ?)");
				statement.setString(1, username);
				statement.setString(2, value);
				statement.setLong(3, expiry);

				statement.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void expireCookie(String value) {
		try (Connection connection = getConnection()) {
			PreparedStatement statement = connection.prepareStatement("DELETE FROM cookies WHERE cookie=?");
			statement.setString(1, value);

			statement.executeUpdate();

			registeredCookies.remove(value);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * USER METHODS
	 */
	
	public String getEmailFromUsername(String username) {
		try (Connection connection = getConnection()) {
			PreparedStatement statement = connection
					.prepareStatement("SELECT email FROM users WHERE username=? AND email_confirmed=?");
			statement.setString(1, username);
			statement.setBoolean(2, true);
			
			ResultSet results = statement.executeQuery();
			if (results.next()) {
				return results.getString("email");
			}
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean checkLogin(String username, String password) {
		password = getSecurePassword(password, getSalt(username));
		try (Connection connection = getConnection()) {
			PreparedStatement statement = connection
					.prepareStatement("SELECT * FROM users WHERE username=? AND password=? AND email_confirmed=?");
			statement.setString(1, username);
			statement.setString(2, password);
			statement.setBoolean(3, true);

			ResultSet results = statement.executeQuery();
			if (results.next()) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public void createUser(String username, String email, String password) {
		byte[] salt = getRandomSalt();
		password = getSecurePassword(password, salt);
		try (Connection connection = getConnection()) {
			PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, email, password, salt, email_confirmed) VALUES (?, ?, ?, ?, ?)");
			statement.setString(1, username);
			statement.setString(2, email);
			statement.setString(3, password);
			statement.setBytes(4, salt);
			statement.setBoolean(5, false);

			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Email Confirmations
	 */

	private List<String> registeredConfirmationCodes = new ArrayList<String>();

	public String generateConfirmationCode() {
		String code = UUID.randomUUID().toString();
		while (registeredConfirmationCodes.contains(code))
			code = UUID.randomUUID().toString();

		registeredConfirmationCodes.add(code);
		return code;
	}

	public String getUsernameFromConfirmationCode(String code) {
		try (Connection connection = getConnection()) {
			PreparedStatement statement = connection
					.prepareStatement("SELECT username FROM email_codes WHERE code=? AND expiry>=?");
			statement.setString(1, code);
			statement.setLong(2, Calendar.getInstance().getTimeInMillis());
			ResultSet results = statement.executeQuery();
			if (results.next()) {
				return results.getString("username");
			}
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getUnknownValidityConfirmationCodeFromUsername(String username) {
		try (Connection connection = getConnection()) {
			PreparedStatement statement = connection
					.prepareStatement("SELECT code FROM email_codes WHERE username=?");
			statement.setString(1, username);
			ResultSet results = statement.executeQuery();
			if (results.next()) {
				return results.getString("code");
			}
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void createConfirmationCode(String username, String code) {
		int age = 3600;
		long expiry = Calendar.getInstance().getTimeInMillis() + (age * 1000);
		try (Connection connection = getConnection()) {
			if (getUnknownValidityConfirmationCodeFromUsername(username) != null) {
				PreparedStatement statement = connection.prepareStatement("UPDATE email_codes SET code=?, expiry=? WHERE username=?");
				statement.setString(1, code);
				statement.setLong(2, expiry);
				statement.setString(3, username);

				statement.executeUpdate();
			} else {
				PreparedStatement statement = connection.prepareStatement("INSERT INTO email_codes (code, username, expiry) VALUES (?, ?, ?)");
				statement.setString(1, code);
				statement.setString(2, username);
				statement.setLong(3, expiry);

				statement.execute();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void confirmEmail(String code) {
		try (Connection connection = getConnection()) {
			PreparedStatement statement2 = connection
					.prepareStatement("UPDATE users SET email_confirmed=? WHERE username=?");
			statement2.setBoolean(1, true);
			statement2.setString(2, getUsernameFromConfirmationCode(code));
			
			statement2.executeUpdate();
			
			PreparedStatement statement = connection
					.prepareStatement("DELETE FROM email_codes WHERE code=?");
			statement.setString(1, code);

			statement.execute();

			registeredConfirmationCodes.remove(code);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
