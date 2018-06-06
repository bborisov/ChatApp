package com.ibm.validators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ibm.constants.TestConstants;

public class StringValidatorTest {

	private StringValidator stringValidator;

	@Test
	public void testIsValid() {
		stringValidator = new StringValidator(TestConstants.STRING, 50);

		assertTrue(stringValidator.isValid());
	}

	@Test
	public void testIsNotValidTooLongString() {
		stringValidator = new StringValidator(String.format("%1$51s", TestConstants.STRING), 50);

		assertFalse(stringValidator.isValid());
	}

	@Test
	public void testIsNotValidNullString() {
		stringValidator = new StringValidator(null, 50);

		assertFalse(stringValidator.isValid());
	}

	@Test
	public void testIsNotValidEmptyString() {
		stringValidator = new StringValidator("", 50);

		assertFalse(stringValidator.isValid());
	}

}
