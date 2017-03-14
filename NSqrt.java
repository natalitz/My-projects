import java.util.Scanner;

public class NSqrt {

	public static void main(String[] args) {

		double x;
		int n;
		Scanner sc = new Scanner(System.in);
		System.out.println("Please enter a double: ");
		x= sc.nextDouble();
		System.out.println("Please enter an int: ");
		n = sc.nextInt();
		calcNSqrt(x,n);
	}

	private static void calcNSqrt(double x, int n) {
		double small = 1.0;
		double big = x;
		binarySearch(x, n, small, big);
		
	}

	private static void binarySearch(double x, int n, double small, double big) {
		double pivot = (big+small)/2;
		double mult = 1.0;
		for(int i = 0; i<n; i++){
			mult *= pivot;
		}
		if (mult < x && (x-mult) > 0.01){
			binarySearch(x, n, pivot, big);
		}
		else if(mult > x && (mult-x) > 0.01){
			binarySearch(x, n, small, pivot);
		}
		else {
			System.out.println("n sqrt is "+String.format("%.2f", pivot));
		}

	}

}
