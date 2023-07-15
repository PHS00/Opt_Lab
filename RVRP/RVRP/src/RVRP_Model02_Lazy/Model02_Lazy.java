package RVRP_Model02_Lazy;

import ilog.concert.*;
import ilog.cplex.*;
import ilog.cplex.IloCplex.UnknownObjectException;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import Arc.Arc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class Model02_Lazy {
	static int n, m, C, N;
	static double[][] d;
	static int[] c;
	static int[][] addTime;
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
	
	public double[] algorithm(IloCplex cplex, double[][] x, int[] order) throws UnknownObjectException, IloException {
		// arc set
		int[][] C = new int[N][];
		for (int i = 0; i < N; i++) {
			C[i] = new int[m];
		}
		// increasing distance value at arc
		double[] pi = new double[N];

		for (int i = 0; i < N; i++) {
			int car = 0;
			// find considering vehicle
			for (int k = 0; k < m; k++) {
				if (x[order[i]][k] > 0.5) {
					car = k;
				}
			}
			// count Arc set number
			int cnt = 0;
			for (int j = 0; j < N; j++) {
				if (C[j][car] == 1)
					cnt++;
			}

			if (cnt < gamma) {
				pi[order[i]] = arc[order[i]].getDistance() + arc[order[i]].getAddTime();
				C[order[i]][car] = 1; // add Arc set
			} else {
				C[order[i]][car] = 1;
				int minIndex = order[i];
				for (int j = 0; j < N; j++) {
					if (C[j][car] == 1) { // about added arc
						minIndex = arc[minIndex].getAddTime() > arc[j].getAddTime() ? j : minIndex;
					}
				}
				pi[order[i]] = arc[order[i]].getDistance() + arc[order[i]].getAddTime() - arc[minIndex].getAddTime();
				C[minIndex][car] = 0;
			}
		}
		System.out.print("C[][] : ");
		for(int k = 0; k < m; k++) {
			for(int i = 0; i < N; i++) {
				if(C[i][k] == 1)
				System.out.print("("+i+")" + C[i][k] + " ");
			}
			System.out.println();
		}
		System.out.println();
		return pi;
	}
	
	//static int[] ordertest = {1, 2, 3, 4, 5};
	public double[] separation(IloCplex cplex, double[][] x) throws UnknownObjectException, IloException {
		
		int[] order = new int[N];
		for(int i = 0; i < N; i++) {
			order[i]= i;
		}
		
		double[] xval = new double[N];
		for(int i = 0; i < N; i++) {
			for(int k = 0; k < m; k++) {
				xval[i] = xval[i] + x[i][k];
			}
		}
		quickSort(xval, 0, N-1, order);
		
		return algorithm(cplex, x, order);
	}

	private static void quickSort(double[] xval,int start, int end, int[] order) throws UnknownObjectException, IloException {
		if (xval == null || xval.length == 0)
			return;
		if (start >= end)
			return;
        double pivot = 0.;
        int left = start;
        int right = end;
		pivot = xval[left + (right - left) / 2];
		while (left <= right) {
			while (xval[left] > pivot)	left++;
			while (xval[right] < pivot)	right--;
			if (left <= right) {
				double tmp = xval[left];    
		        int temp = order[left];
		        
		        xval[left] = xval[right];
		        order[left] = order[right];
		        
		        xval[right] = tmp;
		        order[right] = temp;
		        left++;
		        right--;
			}
		}
		
        if(start<right) quickSort(xval, start, right, order);
        if(end>left) quickSort(xval, left, end, order);

    }
    
	public void solveMe() throws IOException {

		n = Integer.parseInt(scanner.next()); // 전체 노드 개수
		m = Integer.parseInt(scanner.next()); // 전체 차량 대수
		C = Integer.parseInt(scanner.next()); // 각 차량의 최대 적재량

		d = new double[n][];
		for (int i = 0; i < n; i++) {
			d[i] = new double[n];
			for (int j = 0; j < n; j++) {
				d[i][j] = Double.parseDouble(scanner.next()); // distance data 입력
			}
		}

		c = new int[n]; // 각 node에서 원하는 용량

		for (int i = 0; i < n; i++) {
			c[i] = Integer.parseInt(scanner.next()); // data 입력
		}

		// ------------- 데이터 입력 ----------------

		addTime = new int[n][n]; // Additional time data setting
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				addTime[i][j] = (int) (d[i][j] * 0.2);
			}
		}
		
		N = n * (n - 1);

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

			for (int k = 0; k < m; k++) {
				for (int index1 = 0; index1 < N; index1++) {
					alpha[index1][k] = cplex.numVar(0, Double.MAX_VALUE);
				}
			}

			IloNumVar[] beta = new IloNumVar[m];
			for (int k = 0; k < m; k++) {
				beta[k] = cplex.numVar(0, Double.MAX_VALUE);
			}

			IloNumVar[][] x = new IloNumVar[n * (n - 1)][m];
			for (int i = 0; i < N; i++) {
				for (int k = 0; k < m; k++) {
					x[i][k] = cplex.numVar(0, 1, IloNumVarType.Int);
				}
			}

			IloNumVar[] u = new IloNumVar[n];
			for (int i = 1; i < n; i++) {
				u[i] = cplex.numVar(c[i], C);
			}

			// expressions and constraints

			IloLinearNumExpr[][] const1 = new IloLinearNumExpr[n][m];
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < m; k++) {
					const1[j][k] = cplex.linearNumExpr();
					for (int i = 0; i < N; i++) {
						if (arc[i].getDepot() == j) {
							const1[j][k].addTerm(1, x[i][k]);
						} else if (arc[i].getSource() == j) {
							const1[j][k].addTerm(-1, x[i][k]);
						}
					}
					cplex.addEq(const1[j][k], 0);
				}
			}

			IloLinearNumExpr[] const2 = new IloLinearNumExpr[n];
			for (int j = 1; j < n; j++) {
				const2[j] = cplex.linearNumExpr();
				for (int k = 0; k < m; k++) {
					for (int index2 = 0; index2 < N; index2++) {
						if (arc[index2].getDepot() == j)
							const2[j].addTerm(1, x[index2][k]);
					}
				}
				cplex.addEq(const2[j], 1);
			}

			IloLinearNumExpr[] const3 = new IloLinearNumExpr[m];
			for (int k = 0; k < m; k++) {
				const3[k] = cplex.linearNumExpr();
				for (int index1 = 0; index1 < N; index1++) {
					if (arc[index1].getSource() == 0)
						const3[k].addTerm(1, x[index1][k]);
				}
				cplex.addEq(const3[k], 1);
			}

			IloLinearNumExpr[] const4 = new IloLinearNumExpr[m];
			for (int k = 0; k < m; k++) {
				const4[k] = cplex.linearNumExpr();
				for (int index1 = 0; index1 < N; index1++) {
					const4[k].addTerm(c[arc[index1].getDepot()], x[index1][k]);
				}
				cplex.addLe(const4[k], C);
			}
			

			IloLinearNumExpr const5 = cplex.linearNumExpr();
			for (int k = 0; k < m; k++) {
				for (int index1 = 0; index1 < N; index1++) {
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
			// cplex.setOut(null);

			// solve

			cplex.setParam(IloCplex.DoubleParam.TimeLimit, 3600); // 시간 제한

			long startTime = System.currentTimeMillis();
			cplex.use(new LazyCallback(cplex, x, arc, obj));
			cplex.solve();
			long endTime = System.currentTimeMillis();
			
			// print result
			System.out.println("------------- Result -------------");
			bw.write("------------- Result -------------");
			bw.newLine();
			System.out.println("Time : " + (endTime - startTime) / 1000. + " sec");
			bw.write("Time : " + (endTime - startTime) / 1000. + " sec");
			bw.newLine();
			System.out.println("Number of gamma : " + gamma);
			bw.write("Number of gamma : " + gamma);
			bw.newLine();
			System.out.println("Object value : " + cplex.getObjValue());
			bw.write("Object value : " + cplex.getObjValue());
			bw.newLine();
			System.out.println("Number of branch : " + cplex.getNnodes());
			bw.write("Number of branch : " + cplex.getNnodes());
			bw.newLine();
			System.out.println("추가 제약식 개수 : " + LazyCallback.phase);
			bw.write("추가 제약식 개수 : " + LazyCallback.phase);
			bw.newLine();
			// calculate gap rate
			double ub = cplex.getObjValue();
			double lb = cplex.getBestObjValue();
			double gap = 100 * (ub - lb) / lb;
			System.out.println("Gap : " + gap + "%");
			bw.write("Gap : " + gap + "%");
			bw.newLine();
			
//			System.out.print("AddTime value : ");
//			int index = 0;
//			for(int i = 0; i < n; i++) {
//				for(int j = 0; j < n; j++) {
//					if(i == j) continue;
//					System.out.print("("+index+") " + arc[index++].getAddTime() + " ");
//				}
//				System.out.println();
//			}
//			System.out.println();
//			index = 0;
//			System.out.print("pi[] value : ");
//			for(int i = 0; i < n; i++) {
//				for(int j = 0; j < n; j++) {
//					if(i == j) continue;
//					System.out.print("("+index+") " + pi[index++] + " ");
//				}
//				System.out.println();
//			}
//			System.out.println();
			

//			for (int k = 0; k < m; k++) {
//				int i = 0;
//				int j = 0;
//				int index1 = 0;
//				while (true) {
//					if (cplex.getValue(x[index1][k]) > 0.5) {
//						j = arc[index1].getDepot();
//						System.out.println((k + 1) + "차량 방문 경로 : " + (i + 1) + " " + (j + 1));
//						bw.write((k + 1) + "차량 방문 경로 : " + (i + 1) + " " + (j + 1));
//						bw.newLine();
//						if (j == 0)
//							break;
//						i = j;
//						index1 = j * (n - 1);
//						continue;
//					}
//					index1++;
//				}
//			}

			bw.close();
			cplex.end();
		} catch (IloException exc) {
			exc.printStackTrace();
		}

	}

	public class LazyCallback extends IloCplex.LazyConstraintCallback {
		IloNumVar[][] x;
		IloNumVar eta;
		IloCplex cplex;
		Arc[] arc;
		static int phase;

		LazyCallback(IloCplex cplex, IloNumVar[][] x, Arc[] arc, IloNumVar eta) {
			this.cplex = cplex;
			this.x = x;
			this.phase = 0;
			this.arc = arc;
			this.eta = eta;
		}

		public void main() throws IloException {
			boolean[] Sub = new boolean[n];
			Sub[0] = true;
			int sumSub = 0;
			phase++;

			for (int k = 0; k < m; k++) {
				int i = 0;
				int j = 0;
				int index1 = 0;
				while (true) {
					if (getValue(x[index1][k]) > 0.5) {
						j = arc[index1].getDepot();
						if (j == 0)
							break;
						i = j;
						index1 = j * (n - 1);
						Sub[j] = true; // normal node check
						continue;
					}
					index1++;
				}
			}

			for (int i = 1; i < n; i++) {
				if (Sub[i] == false)
					sumSub++;
			}

			if (sumSub != 0) {
				IloLinearNumExpr const7 = cplex.linearNumExpr();
				for (int i = 1; i < n; i++) {
					if (Sub[i] == false) {
						for (int j = 1; j < n; j++) {
							if (i == j)
								continue;
							if (Sub[j] == false) {
								for (int index1 = i * (n - 1);; index1++) {
									if (arc[index1].getDepot() == j) {
										for (int k = 0; k < m; k++) {
											const7.addTerm(1, x[index1][k]);
											// System.out.println(i + ""+j+""+k);
										}
										break;
									}
								}
							}
						}
					}
				}
				add(cplex.le(const7, sumSub - 1));
			} 
		}

	}

}
