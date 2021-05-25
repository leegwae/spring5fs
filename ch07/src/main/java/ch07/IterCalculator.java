package ch07;

public class IterCalculator implements Calculator {

	public long factorial(long num) {
		long result = 1;
		
		for (int i = 1; i < num + 1; i++) {
			result *= i;
		}
		
		return result;
	}

}
