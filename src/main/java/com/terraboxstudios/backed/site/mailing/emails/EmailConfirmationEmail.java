package com.terraboxstudios.backed.site.mailing.emails;

import com.terraboxstudios.backed.site.enums.Parameters;
import com.terraboxstudios.backed.site.enums.Servlets;
import com.terraboxstudios.backed.site.mailing.PremadeEmail;
import com.terraboxstudios.backed.site.util.Settings;
import com.terraboxstudios.backed.site.util.Utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class EmailConfirmationEmail implements PremadeEmail {

    private final StringBuilder title = new StringBuilder();
    private final StringBuilder content = new StringBuilder();

    public EmailConfirmationEmail(String code) {
        title.append("Backed - Email Confirmation");
        try {
            List<String> lines = Files.readAllLines(Paths.get(Utils.getPathOfEmail("EmailConfirmationHTML")), Charset.defaultCharset());
            for (String line : lines) {
                content.append(line
                        .replace("{CODE}", code)
                        .replace("{LINK}", ((String) Settings.getInstance().getConfig().getWebsiteHost()) + Servlets.CONFIRM_EMAIL.getUrlPattern() + "?" + Parameters.ConfirmEmail.CODE.getParam() + "=").replace("\"", ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public StringBuilder getTitle() {
        return title;
    }

    @Override
    public StringBuilder getContent() {
        return content;
    }

}
