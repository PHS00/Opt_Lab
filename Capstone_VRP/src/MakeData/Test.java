package MakeData;

import java.util.Random;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Random random = new Random();
		int[] test = new int[100];
		for(int i = 0; i < test.length; i++) {
			
			test[i] = (int)(Math.random()*10);
			System.out.println(test[i]);
//			test[i] = (int)Math.abs((random.nextGaussian()*5+10));
//			int t = (int)randn_bm(1, 10, 1);
//			System.out.println(t);
		}
		
		
	}
	
	static double randn_bm(int min,int max,int skew) {
	    double u = 0, v = 0;
	    while (u == 0) u = Math.random(); //Converting [0,1) to (0,1)
	    while (v == 0) v = Math.random();
	    double num = Math.sqrt(-2.0 * Math.log(u)) * Math.cos(2.0 * Math.PI * v);

	    num = num / 10.0 + 0.5; // Translate to 0 -> 1
	    if (num > 1 || num < 0)
	        num = randn_bm(min, max, skew); // resample between 0 and 1 if out of range

	    else {
	        num = Math.pow(num, skew); // Skew
	        num *= max - min; // Stretch to fill range
	        num += min; // offset to min
	    }
	    return num;
	}

}
