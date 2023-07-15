package MakeData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class MakeDataInstance {

	static Scanner scanner; // input data
	static BufferedWriter bw; // write result
	String dataPath = "C:\\Users\\최적화_연구실_PC1\\OneDrive - Chonnam National University\\바탕 화면\\Optimize\\Capstone\\Capstone\\Data\\";
	String dataInstancePath = "C:\\Users\\최적화_연구실_PC1\\OneDrive - Chonnam National University\\바탕 화면\\Optimize\\Capstone\\Capstone\\Data_instance\\";

	static int n = 0;
	static int m = 0;
	static int C = 0;
	static double[][] dis;
	static int[][] speed;
	static int[] delivery;
	static int[] pickup;
	static double[][] stv;

	public void setScanner(String s) throws FileNotFoundException {
		this.scanner = new Scanner(new File(s));
	}

	public void setBw(String s) throws IOException {
		this.bw = new BufferedWriter(new FileWriter(s, false));
	}

	public String readDataToString(Scanner scanner) {
		return scanner.next().toString();
	}

	public int readDataToInt(Scanner dataScanner) {
		return Integer.parseInt(dataScanner.next());
	}

	public void readData(Scanner scanner) {
		scanner.next();
	}

	public int creatRandomValue(int min, int max) {
		return (int) (Math.random() * (max - min)) + min;
	}

	public double creatRandomValue(double min, double max) {
		return (double) (Math.random() * (max - min)) + min;
	}

	public int sum(int[] a) {
		int sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += a[i];
		}
		return sum;
	}

	public void writeDataFile() {
		try {
			bw.write("" + n);
			bw.newLine();
			bw.write("" + m); // number of vehicle
			bw.newLine();
			bw.write("" + C); // number of vehicle
			bw.newLine();
			bw.newLine();

			// distance
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					bw.write(dis[i][j] + " ");
					System.out.print(dis[i][j] + " ");
				}
				bw.newLine();
				System.out.println();
			}
			bw.newLine();
			System.out.println();

			// speed
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					bw.write(speed[i][j] + " ");
					System.out.print(speed[i][j] + " ");
				}
				bw.newLine();
				System.out.println();
			}
			bw.newLine();
			System.out.println();
			
			// stv
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					bw.write(stv[i][j] + " ");
					System.out.print(stv[i][j] + " ");
				}
				bw.newLine();
				System.out.println();
			}
			bw.newLine();
			System.out.println();

			// demand
			for (int i = 0; i < n; i++) {
				bw.write(delivery[i] + " ");
				System.out.print(delivery[i] + " ");
			}
			System.out.println();
			bw.newLine();

			// pickup
			for (int i = 0; i < n; i++) {
				bw.write(pickup[i] + " ");
				System.out.print(pickup[i] + " ");
			}
			System.out.println();
			bw.newLine();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // number of nodes
	}

	public void makeData() throws IOException {

		String dataTitle = "";

		// read data name
		// load data
		while (dataTitle != null) {
			dataTitle = readDataToString(scanner);
//			dataTitle = "P-n20-k2";
			Scanner dataScanner = new Scanner(new File(dataPath + dataTitle + ".txt"));

			String string = "";
			string = readDataToString(dataScanner);

			while (!string.equals("trucks:")) {
				string = readDataToString(dataScanner);
			}
			m = readDataToInt(dataScanner);

			while (!string.equals("DIMENSION")) {
				string = readDataToString(dataScanner);
			}
			string = readDataToString(dataScanner);
			n = readDataToInt(dataScanner);
//			m = Integer.parseInt(scanner.next()); // ��ü ���� ���

			while (!string.equals("CAPACITY")) {
				string = readDataToString(dataScanner);
			}
			string = readDataToString(dataScanner);
			C = readDataToInt(dataScanner);

			while (!string.equals("NODE_COORD_SECTION")) {	// NODE_COORD_SECTION
				string = readDataToString(dataScanner);
			}
			Node[] node = new Node[n]; // node
			for (int i = 0; i < n; i++) {
				node[i] = new Node();
				readData(dataScanner);
				node[i].x = readDataToInt(dataScanner);
				node[i].y = readDataToInt(dataScanner);
			}

			while (!string.equals("DEMAND_SECTION")) { // DEMAND_SECTION
				string = readDataToString(dataScanner);
			}
			delivery = new int[n];
			// To Do section -> random delivery demand
			for (int i = 0; i < n; i++) {
				readData(dataScanner);
				delivery[i] = readDataToInt(dataScanner);
			}

			// calculate distance
			dis = new double[n][n];
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					if (i == j)
						dis[i][j] = 0;
					else
						dis[i][j] = MakeDataInstance.getDistance(node[i], node[j]);
				}
			}

			// create speed value
			speed = new int[n][n];
			for (int i = 0; i < n; i++) {
				for (int j = i + 1; j < n; j++) {
					speed[i][j] = creatRandomValue(10, 100);
					speed[j][i] = speed[i][j];
				}
			}
			// stv
			stv = new double[n][n];
			for (int i = 0; i < n; i++) {
				for (int j = i + 1; j < n; j++) {
					stv[i][j] = creatRandomValue(0.1, 1.0);
					stv[j][i] = stv[i][j];
				}
			}
			
			// pick-up demand
			pickup = new int[n];
			for (int i = 0; i < n; i++) {
				pickup[i] = creatRandomValue(0, 10);
			}
			pickup[0] = 0;
			int sum = sum(pickup);
			double avPickup = (double) sum / n;
			C += avPickup; // updated vehicle capacity

			setBw(dataInstancePath + dataTitle + "_Instance.txt");

			writeDataFile();

//			// addTime 0.1 ~ 0.2
//			for (int i = 0; i < n; i++) {
//				for (int j = 0; j < n; j++) {
//					bw.write((dis[i][j] * 0.1 + Math.random() * 0.1 * dis[i][j]) + " ");
//					System.out.print(dis[i][j] + " ");
//				}
//				bw.newLine();
//				System.out.println();
//			}
//			bw.newLine();
//
			bw.close();

			System.out.println("success!!");
//			break;
		}
	}

	class Node {
		public int x, y;
	}

	public static double getDistance(Node a, Node b) {
		int x, y;
		x = (a.x - b.x) * (a.x - b.x);
		y = (a.y - b.y) * (a.y - b.y);
		return (Math.sqrt(x + y) + 0.5);
	}

}
