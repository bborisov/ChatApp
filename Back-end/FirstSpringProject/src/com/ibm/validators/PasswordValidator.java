package com.ibm.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator implements Validator {

	private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d$@$!%*#?&]{6,}$";

	private String password;

	public PasswordValidator(String password) {
		this.password = password;
	}

	@Override
	public boolean isValid() {
		Pattern pattern = Pattern.compile(PASSWORD_REGEX);
		Matcher matcher = pattern.matcher(password);

		if (matcher.matches()) {
			return true;
		}

		return false;
	}

}
