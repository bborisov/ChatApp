package com.ibm.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.validators.Validator;

public class EmailValidator implements Validator {

	private static final String EMAIL_REGEX = "[A-Z0-9_]+@[A-Z0-9]+\\.[A-Z]{2,4}";

	private String email;

	public EmailValidator(String email) {
		this.email = email;
	}

	@Override
	public boolean isValid() {
		Pattern pattern = Pattern.compile(EMAIL_REGEX);
		Matcher matcher = pattern.matcher(email.toUpperCase());

		if (matcher.matches()) {
			return true;
		}

		return false;
	}

}
