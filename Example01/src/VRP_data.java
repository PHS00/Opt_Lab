import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class VRP_data {

	public void makeData() throws IOException {

		final int n = 20;	// ��� ����
		final int m = 5;	// ���� ���
		
		boolean[][] position = new boolean[100][];
		for (int i = 0; i < 100; i++) {
			position[i] = new boolean[100];
		}

		int cnt = 0;
		int x, y;
		Node[] node = new Node[n]; // node ��ü (��ġ����)

		while (cnt != n) {
			x = (int) (Math.random() * 99) + 1;
			y = (int) (Math.random() * 99) + 1;
			if (position[x][y])
				continue; // �̹� �����ϸ� �ٸ� ��ǥ�� ����
			position[x][y] = true; // ��ǥ�� �ִٰ� ǥ��
			node[cnt] = new Node();
			node[cnt].x = x;
			node[cnt].y = y;
			cnt++;
		}

		double[][] dis = new double[n][];
		for (int i = 0; i < n; i++) {
			dis[i] = new double[n];
		}
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i == j)
					dis[i][j] = 0;
				dis[i][j] = VRP_data.getDistance(node[i], node[j]);
			}
		}

		int sum = 0;
		int[] demand = new int[n];
		demand[0] = 0;
		for (int i = 1; i < n; i++) {
			demand[i] = (int) (Math.random() * 10) + 1;
			sum += demand[i];
		}
		
		int capacity;
		capacity = (int)((sum/m) * 1.5);
		
		// data�� �ؽ�Ʈ ���Ͽ� �Է� n, m, �Ÿ�, demand, �뷮

		@SuppressWarnings("resource")
		BufferedWriter bw = new BufferedWriter(new FileWriter("VRP_data.txt", false));
		bw.write("" + n); // number of nodes
		bw.newLine();
		bw.write("" + m); // number of vehicle
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
		bw.write("" + capacity);
		bw.newLine();
		bw.close();
		
		System.out.println("success!!");
	}

	class Node {
		public int x, y;

	}

	public static double getDistance(Node a, Node b) {
		int x, y;
		x = (a.x - b.x) * (a.x - b.x);
		y = (a.y - b.y) * (a.y - b.y);
		return (double)Math.sqrt(x + y);
	}

}
