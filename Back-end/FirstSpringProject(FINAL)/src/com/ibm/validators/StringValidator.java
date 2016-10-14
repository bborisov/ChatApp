package com.ibm.validators;

public class StringValidator implements Validator {

	private String string;
	private int length;

	public StringValidator(String string, int length) {
		this.string = string;
		this.length = length;
	}

	@Override
	public boolean isValid() {
		if (string == null || string.trim().equals("") || string.length() > this.length) {
			return false;
		}

		return true;
	}

}
