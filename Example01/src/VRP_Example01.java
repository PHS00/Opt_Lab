import ilog.concert.*;
import ilog.cplex.*;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class VRP_Example01 {
	
	public static void solveMe() throws IOException {
		
		Scanner scanner = new Scanner(new File("C:\\Users\\최적화_연구실_PC1\\OneDrive - Chonnam National University\\바탕 화면\\Optimize\\Example01\\VRP_data.txt"));
		
		int n = Integer.parseInt(scanner.next());	// ��ü ��� ����
		int m = Integer.parseInt(scanner.next());	// ��ü ���� ���
		
		double[][] d = new double[n][];		// distance data	
		
		for(int i = 0; i < n; i++) {
			d[i] = new double[n];
			for(int j = 0; j < n; j++) {
				d[i][j] = Double.parseDouble(scanner.next());	// distance data �Է�
			}
		}
		
		int[] c = new int[n];	// �� node���� ���ϴ� �뷮
		
		for(int i = 0; i < n; i++) {
			c[i] = Integer.parseInt(scanner.next());	// data �Է�
		}
		
		int C = Integer.parseInt(scanner.next());	// �� ������ �ִ� ���緮
		
		// ------------- ������ �Է� ----------------
		
		try {
			// define new model
			IloCplex cplex = new IloCplex();
			
			// variable
			IloNumVar[][][] x = new IloNumVar[n][n][m];	// k ������ (i,j) ��� �湮 ����
			for(int i=0; i<n; i++) {
				for(int j=0; j<n; j++) {
					for(int k = 0; k<m; k++) {
						x[i][j][k] = cplex.numVar(0, 1, IloNumVarType.Int);
					}
				}
			}
			
			IloNumVar[] u = new IloNumVar[n];
			for(int i = 1; i<n; i++) {
				u[i] = cplex.numVar(c[i], C);
			}
						
			// expressions and constraints
			IloLinearNumExpr[][] const1 = new IloLinearNumExpr[n][m];
			for (int j = 0; j < n; j++) {
				for(int k = 0; k< m; k++) {
					const1[j][k] = cplex.linearNumExpr();
					for(int i = 0; i<n; i++) {	
						const1[j][k].addTerm(1, x[i][j][k]);	
						const1[j][k].addTerm(-1, x[j][i][k]);
					}
					cplex.addEq(const1[j][k], 0);	
				}
			}
			
			IloLinearNumExpr[] const2 = new IloLinearNumExpr[n];
			for(int j = 1; j<n; j++) {
				const2[j] = cplex.linearNumExpr();
				for(int k = 0; k < m; k++) {
					for(int i = 0; i<n; i++) {
						const2[j].addTerm(1, x[i][j][k]);
					}
				}
				cplex.addEq(const2[j], 1);
			}
			
			IloLinearNumExpr[] const3 = new IloLinearNumExpr[m];
			for(int k = 0; k<m; k++) {
				const3[k] = cplex.linearNumExpr();
				for(int j = 1; j<n; j++) {
					const3[k].addTerm(1, x[0][j][k]);
				}
				cplex.addEq(const3[k], 1);
			}
			
			IloLinearNumExpr[][][] const4 = new IloLinearNumExpr[n][n][m];
			for(int i = 1; i<n; i++) {
				for(int j = 1; j<n; j++) {
					for(int k = 0; k<m; k++) {
						if(i != j) {
							const4[i][j][k] = cplex.linearNumExpr();
							const4[i][j][k].addTerm(1, u[i]);
							const4[i][j][k].addTerm(-1, u[j]);
							const4[i][j][k].addTerm(C, x[i][j][k]);
							cplex.addLe(const4[i][j][k], C-c[j]);
						}
					}
				}
			}
			
			for(int i = 0; i < n; i++) {
				for(int k = 0; k < m; k++) {
					cplex.addEq(x[i][i][k], 0);
					}
			}
			
			IloLinearNumExpr objective = cplex.linearNumExpr();
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					for(int k = 0; k<m; k++) {
						objective.addTerm(d[i][j], x[i][j][k]);
					}
				}
			}
			
			// define objective
			cplex.addMinimize(objective);
			
			cplex.setParam(IloCplex.Param.Simplex.Display, 0);
			
			// solve
			BufferedWriter bw = new BufferedWriter(new FileWriter("VRP_Example01_result.txt", false));
			
			long startTime = System.currentTimeMillis();
			if(cplex.solve()) {
				long endTime = System.currentTimeMillis();
				System.out.println("Success!");
				System.out.println("걸린시간 : "+ (endTime-startTime)/1000. + "초");
				bw.write("걸린시간 : " + (endTime-startTime)/1000. + "초");
				bw.newLine();
				
				for (int k = 0; k < m; k++) {
					int i = 0;
					int j = 0;
					while(j != n) {
						if (cplex.getValue(x[i][j][k]) > 0.5) {
							System.out.println((k+1) + "차량 이동경로 : " + (i+1) + " " + (j+1));
							bw.write((k+1) + "차량 이동경로 : " + (i+1) + " " + (j+1));
							bw.newLine();
							i = j;
							j = -1;
							if(i == 0) break;
						}
						j++;
					}
					
				}
				
				for(int i = 1; i < n; i++) {
					System.out.print(cplex.getValue(u[i]) + " ");
				}
				
			}
			else {
				System.out.println("proble not solved");
			}
			
			bw.close();
			cplex.end();
		}
		catch(IloException exc){
			exc.printStackTrace();
		}
		
	}
	
}
