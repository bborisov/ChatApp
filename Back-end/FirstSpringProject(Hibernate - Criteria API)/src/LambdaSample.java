import java.util.function.BiFunction;

public class LambdaSample {
	
	public static void main(String[] args) {
		final BiFunction<Integer, Integer, Integer> sum = (param1, param2) -> {
			int result = param1;
			result += param2;
			return result;
		};
		System.out.println(sum.apply(5, 6));
		
	}

}
