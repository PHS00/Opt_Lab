package CVRP01;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CVRP_Data {

	public void makeData() throws IOException {

		Scanner scanner = new Scanner(new File("C:\\Users\\최적화_연구실_PC1\\OneDrive - Chonnam National University\\바탕 화면\\Optimize\\CVRP\\CVRP_Exam01_Data.txt"));
		
		final int n = Integer.parseInt(scanner.next());	// ��� ����
		final int m = Integer.parseInt(scanner.next());	// ���� ���
		final int C = Integer.parseInt(scanner.next());	// ���� ��ü �뷮
		
		Node[] node = new Node[n]; // node ��ü (��ġ����)

		for(int i = 0; i < n; i++) {
			node[i] = new Node();
			node[i].x = Integer.parseInt(scanner.next());
			node[i].y = Integer.parseInt(scanner.next());
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
					dis[i][j] = CVRP_Data.getDistance(node[i], node[j]);
			}
		}

		int[] demand = new int[n];
		demand[0] = 0;
		for (int i = 1; i < n; i++) {
			demand[i] = Integer.parseInt(scanner.next());
		}
		
		// data�� �ؽ�Ʈ ���Ͽ� �Է� n, m, �Ÿ�, demand, �뷮

		@SuppressWarnings("resource")
		BufferedWriter bw = new BufferedWriter(new FileWriter("CVRP_Data_Instance.txt", false));
		bw.write("" + n); // number of nodes
		bw.newLine();
		bw.write("" + m); // number of vehicle
		bw.newLine();
		bw.write("" + C); // number of vehicle
		bw.newLine();

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				bw.write(dis[i][j] + " ");
				System.out.print(dis[i][j] + " ");
			}
			bw.newLine();
			System.out.println();
		}
		for (int i = 0; i < n; i++) {
			bw.write(demand[i] + " ");
			System.out.print(demand[i] + " ");
		}
		System.out.println();
		bw.newLine();

		bw.close();
		
		System.out.println("success!!");
	}

	class Node {
		public int x, y;
	}

	public static int getDistance(Node a, Node b) {
		int x, y;
		x = (a.x - b.x) * (a.x - b.x);
		y = (a.y - b.y) * (a.y - b.y);
		return (int)(Math.sqrt(x + y) + 0.5);
	}

}
