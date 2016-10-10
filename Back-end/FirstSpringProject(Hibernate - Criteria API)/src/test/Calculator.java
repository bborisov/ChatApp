package test;

public class Calculator {

	public Integer add(Integer a, Integer b){
		
		if(a < 0 || b < 0 ){
			throw new IllegalArgumentException();
		}
		
		return a + b;
	}
}
