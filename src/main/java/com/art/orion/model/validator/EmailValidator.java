package com.art.orion.model.validator;

import java.util.regex.Pattern;

public class EmailValidator {

    private EmailValidator() {
    }

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z\\d._%+-]+@[A-Z\\d.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String email) {
        return email != null && VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches();
    }
}

