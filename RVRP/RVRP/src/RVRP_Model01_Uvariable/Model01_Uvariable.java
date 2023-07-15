package RVRP_Model01_Uvariable;
import ilog.concert.*;
import ilog.cplex.*;
import java.util.Scanner;

import Arc.Arc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class Model01_Uvariable {
	
	static int n, m, C, N;	
	static double[][] d;	// distance of each node
	static int[] c;			// demand
	static double[][] addTime;		
	static Arc[] arc;
	int gamma;	// number of selection
	Scanner scanner;	// input data
	BufferedWriter bw; 	// write result
	
	public void setGamma(int gamma) {
		this.gamma = gamma;
	}
	
	public void setScanner(String s) throws FileNotFoundException {
		this.scanner = new Scanner(new File(s));
	}
		
	public void setBw(String s) throws IOException {
		this.bw = new BufferedWriter(new FileWriter(s, false));
	}
	public void setVehicle(int k) {
		this.m = k;
	}
	public int getGamma() {
		return this.gamma;
	}
	
	public void solveMe() throws IOException, NumberFormatException {
		
		String string = "";
		string = scanner.next();
		
		while(!string.equals("Customers:")) {
			string = scanner.next();
		}
		n = Integer.parseInt(scanner.next()) + 1;
		
		while(!string.equals("Dimension:")) {
			string = scanner.next();
		}
		N = Integer.parseInt(scanner.next());
//		m = Integer.parseInt(scanner.next()); // ��ü ���� ���
		
		while(!string.equals("Capacity:")) {
			string = scanner.next();
		}
		C = Integer.parseInt(scanner.next());
		
		while(!string.equals("Demands:")) {
			string = scanner.next();
		}
		c = new int[n];	
		for (int i = 0; i < n; i++) {
			c[i] = Integer.parseInt(scanner.next()); 
		}
		
		while(!string.equals("Gamma:")) {
			string = scanner.next();
		}
		gamma = Integer.parseInt(scanner.next());
		
		while(!string.equals("Mean Vector:")) {
			string = scanner.nextLine();
		}
		d = new double[n][n];
		for (int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if(i == j) continue;
				d[i][j] = Double.parseDouble(scanner.next()); // distance data �Է�
			}
		}
		
		while(!string.equals("Deviation Vector:")) {
			string = scanner.nextLine();
		}
		addTime = new double[n][n]; // Additional time data setting
		for (int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if(i == j) continue;
				addTime[i][j] = Double.parseDouble(scanner.next()); // distance data �Է�
			}

		}

		
		
		// ------------- ������ �Է� ----------------
		
		
//		N = n * (n - 1);

		arc = new Arc[N];
		int index = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i == j)
					continue;
				arc[index] = new Arc(index++, i, j, d[i][j], addTime[i][j]);
			}
		}
		
		try {
			// define new model
			IloCplex cplex = new IloCplex();
			// variable
			IloNumVar obj = cplex.numVar(0, Double.MAX_VALUE);
			
			IloNumVar[][] alpha = new IloNumVar[N][m];
					
			for(int k = 0; k<m; k++) {
				for(int index1 = 0; index1 < N; index1++) {
					alpha[index1][k] = cplex.numVar(0, Double.MAX_VALUE);
				}
			}
			
			IloNumVar[] beta = new IloNumVar[m];
			for(int k = 0; k < m; k++) {
				beta[k] = cplex.numVar(0, Double.MAX_VALUE);
			}
			
			IloNumVar[][] x = new IloNumVar[n*(n-1)][m];
			for(index = 0 ; index < N; index++) {
				for(int k = 0; k < m; k++) {
					x[index][k] = cplex.numVar(0, 1, IloNumVarType.Int);			}
			}
			
			IloNumVar[] u = new IloNumVar[n];
			for(int i = 1; i<n; i++) {
				u[i] = cplex.numVar(c[i], C);
			}
						
			IloLinearNumExpr[][] const1 = new IloLinearNumExpr[n][m];
			for (int j = 0; j < n; j++) {
				for(int k = 0; k< m; k++) {
					const1[j][k] = cplex.linearNumExpr();
					for(int index1 = 0; index1 < N; index1++) {
						if(arc[index1].getDepot() == j) {
							const1[j][k].addTerm(1, x[index1][k]);	
						}
						else if(arc[index1].getSource() == j) {
							const1[j][k].addTerm(-1, x[index1][k]);
						}
					}
					cplex.addEq(const1[j][k], 0);	
				}
			}

			IloLinearNumExpr[] const2 = new IloLinearNumExpr[n];
			for(int j = 1; j<n; j++) {
				const2[j] = cplex.linearNumExpr();
				for(int k = 0; k < m; k++) {
					for(int index2 = 0; index2 < N; index2++) {
						if(arc[index2].getDepot() == j)
						const2[j].addTerm(1, x[index2][k]);
					}
				}
				cplex.addEq(const2[j], 1);
			}

			IloLinearNumExpr[] const3 = new IloLinearNumExpr[m];
			for(int k = 0; k<m; k++) {
				const3[k] = cplex.linearNumExpr();
				for(int index1 = 0; index1 < N; index1++) {
					if(arc[index1].getSource() == 0)
					const3[k].addTerm(1, x[index1][k]);
				}
				cplex.addEq(const3[k], 1);
			}
			
			IloLinearNumExpr[][] const4 = new IloLinearNumExpr[N][m];
			for (int k = 0; k < m; k++) {
				for (int index1 = 0; index1 < N; index1++) {
					if(arc[index1].getSource() != 0 && arc[index1].getDepot() != 0) {
						const4[index1][k] = cplex.linearNumExpr();
						const4[index1][k].addTerm(1, u[arc[index1].getSource()]);
						const4[index1][k].addTerm(-1, u[arc[index1].getDepot()]);
						const4[index1][k].addTerm(C, x[index1][k]);
						cplex.addLe(const4[index1][k], C - c[arc[index1].getDepot()]);
					}
				}
			}
			

			IloLinearNumExpr const5 = cplex.linearNumExpr();
			for(int k = 0; k < m; k++) {
				for(int index1 = 0; index1 < N; index1++) {
					const5.addTerm(arc[index1].distance, x[index1][k]);
					const5.addTerm(1, alpha[index1][k]);
				}
				const5.addTerm(gamma, beta[k]);
			}
			cplex.addGe(obj, const5);
			
			IloLinearNumExpr[][] const6 = new IloLinearNumExpr[N][m];
			IloLinearNumExpr[][] const6_sub1 = new IloLinearNumExpr[N][m];
			for (int k = 0; k < m; k++) {
				for (int index1 = 0; index1 < N; index1++) {
					const6[index1][k] = cplex.linearNumExpr();
					const6[index1][k].addTerm(1, alpha[index1][k]);
					const6[index1][k].addTerm(1, beta[k]);
					const6_sub1[index1][k] = cplex.linearNumExpr();
					const6_sub1[index1][k].addTerm(arc[index1].getAddTime(), x[index1][k]);
					cplex.addGe(const6[index1][k], const6_sub1[index1][k]);
				}
			}
			
			// define objective
			cplex.addMinimize(obj);
			
			cplex.setParam(IloCplex.Param.Simplex.Display, 0);
			//cplex.setOut(null);
			
			// solve
			cplex.setParam(IloCplex.DoubleParam.TimeLimit, 3600); 	//�ð� ����
			cplex.setParam(IloCplex.Param.Threads, 1);	// callback
			cplex.setParam(IloCplex.Param.Preprocessing.Presolve, false);	// ��ó�� x
			long startTime = System.currentTimeMillis();

			cplex.solve();
			long endTime = System.currentTimeMillis();
			
			// print result
			// Time
			System.out.println(""+ (endTime-startTime)/1000.);
			bw.write(""+ (endTime-startTime)/1000.);
			bw.newLine();
			// Number of gamma
			System.out.println("" + gamma);
			bw.write("" + gamma);
			bw.newLine();
			
			if(cplex.getStatus().equals(IloCplex.Status.Feasible) || cplex.getStatus().equals(IloCplex.Status.Optimal)) {
				// Obj Value
				System.out.println("" + cplex.getObjValue());
				bw.write("" + cplex.getObjValue());
				bw.newLine();
				// Number of branch
				System.out.println("" + cplex.getNnodes());
				bw.write("" + cplex.getNnodes());
				bw.newLine();
				// calculate gap rate
				double ub = cplex.getObjValue();
				double lb = cplex.getBestObjValue();
				double gap = 100 * (ub - lb) / lb;
				System.out.println("" + gap);
				bw.write("" + gap);
				bw.newLine();			
			}else {
				System.out.println("Error");
				bw.write("Error");
				bw.newLine();							
			}
			
			

//			for (int k = 0; k < m; k++) {
//				int i = 0;
//				int j = 0;
//				int index1 = 0;
//				while(true) {
//					if (cplex.getValue(x[index1][k]) > 0.5) {
//						j = arc[index1].getDepot();
//						System.out.println((k + 1) + "���� �湮 ��� : " + (i + 1) + " " + (j + 1));
//						bw.write((k + 1) + "���� �湮 ��� : " + (i + 1) + " " + (j + 1));
//						bw.newLine();
//						if (j == 0) break;
//						i = j;
//						index1 = j * (n - 1);
//						continue;
//					}
//					index1++;
//				}
//			}
			
			bw.close();
			cplex.end();
		}
		catch(IloException exc){
			exc.printStackTrace();
		}
		
	}
	
}
