package test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CalculatorTest {

	private Calculator calculator;
	
	@Before
	public void setUp(){
		calculator = new Calculator();
	}
	
	@Test
	public void testRightAnswer() {
		int result = calculator.add(2, 5);
		
		Assert.assertEquals(7, result);
	}
	
	@Test(expected = NullPointerException.class)
	public void testFirstParamNull() {
		calculator.add(null, 5);
	}
	
	@Test(expected = NullPointerException.class)
	public void testSecondParamNull() {
		calculator.add(5, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFirstParamNegative() {
		calculator.add(-5, 5);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSecondParamNegative() {
		calculator.add(5, -5);
	}
	
}
