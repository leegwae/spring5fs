package main;

import ch07.IterCalculator;
import ch07.RecCalculator;
import ch07.ExeTimeCaculator;

public class MainProxy {

	public static void main(String[] args) {
		ExeTimeCaculator c1 = new ExeTimeCaculator(new IterCalculator());
		c1.factorial(10);
		
		ExeTimeCaculator c2 = new ExeTimeCaculator(new RecCalculator());
		c2.factorial(10);
	}

}
