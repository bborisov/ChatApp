package com.ibm.validators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ibm.constants.TestConstants;

public class EmailValidatorTest {

	private EmailValidator emailValidator;

	@Test
	public void testIsValid() {
		emailValidator = new EmailValidator(TestConstants.EMAIL);

		assertTrue(emailValidator.isValid());
	}

	@Test
	public void testIsNotValid() {
		emailValidator = new EmailValidator(TestConstants.INVALID_EMAIL);

		assertFalse(emailValidator.isValid());
	}

}
