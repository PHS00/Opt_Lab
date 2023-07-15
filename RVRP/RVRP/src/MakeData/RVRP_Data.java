package MakeData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class RVRP_Data {

	static Scanner scanner; // input data
	static BufferedWriter bw; // write result
	String dataPath = "C:\\Users\\최적화_연구실_PC1\\OneDrive - Chonnam National University\\바탕 화면\\Optimize\\RVRP\\RVRP\\Data\\";
	String dataInstancePath = "C:\\Users\\최적화_연구실_PC1\\OneDrive - Chonnam National University\\바탕 화면\\Optimize\\RVRP\\RVRP\\Data_Instance\\";
	
	public void setScanner(String s) throws FileNotFoundException {
		this.scanner = new Scanner(new File(s));
	}

	public void setBw(String s) throws IOException {
		this.bw = new BufferedWriter(new FileWriter(s, false));
	}

	public void makeData() throws IOException {

		String dataName = "";
		while (dataName != null) {
			dataName = scanner.next().toString();

			// read data name
			// load data
			Scanner dataScanner = new Scanner(new File(dataPath + dataName + ".txt"));

			final int n = Integer.parseInt(dataScanner.next()); //
			final int m = Integer.parseInt(dataScanner.next()); //
			final int C = Integer.parseInt(dataScanner.next()); //
			
			Node[] node = new Node[n]; // node

			dataScanner.next();	// NODE_COORD_SECTION
			for (int i = 0; i < n; i++) {
				node[i] = new Node();
				Integer.parseInt(dataScanner.next());
				node[i].x = Integer.parseInt(dataScanner.next());
				node[i].y = Integer.parseInt(dataScanner.next());
			}

			int[][] dis = new int[n][];
			for (int i = 0; i < n; i++) {
				dis[i] = new int[n];
			}
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					if (i == j)
						dis[i][j] = 0;
					else
						dis[i][j] = RVRP_Data.getDistance(node[i], node[j]);
				}
			}

			dataScanner.next();	// DEMAND_SECTION 
			int[] demand = new int[n];
			demand[0] = 0;
			for (int i = 0; i < n; i++) {
				Integer.parseInt(dataScanner.next());
				demand[i] = Integer.parseInt(dataScanner.next());
			}

			setBw(dataInstancePath + dataName + "_Instance.txt");
			bw.write("" + n); // number of nodes
			bw.newLine();
			bw.write("" + m); // number of vehicle
			bw.newLine();
			bw.write("" + C); // number of vehicle
			bw.newLine();
			bw.newLine();

			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					bw.write(dis[i][j] + " ");
					System.out.print(dis[i][j] + " ");
				}
				bw.newLine();
				System.out.println();
			}
			bw.newLine();
			
			// addTime 0.1 ~ 0.2
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					bw.write((dis[i][j] * 0.1 + Math.random() * 0.1 * dis[i][j]) + " ");
					System.out.print(dis[i][j] + " ");
				}
				bw.newLine();
				System.out.println();
			}
			bw.newLine();
			
			// demand
			for (int i = 0; i < n; i++) {
				bw.write(demand[i] + " ");
				System.out.print(demand[i] + " ");
			}
			System.out.println();
			bw.newLine();

			bw.close();

			System.out.println("success!!");
		}
	}

	class Node {
		public int x, y;
	}

	public static int getDistance(Node a, Node b) {
		int x, y;
		x = (a.x - b.x) * (a.x - b.x);
		y = (a.y - b.y) * (a.y - b.y);
		return (int) (Math.sqrt(x + y) + 0.5);
	}

}
