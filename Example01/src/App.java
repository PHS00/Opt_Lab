import java.io.IOException;

import ilog.concert.*;
import ilog.cplex.*;

public class App {

	public static void main(String[] args) throws IOException {
		//model1();
		
		//HW_1.solveMe();
		
		//VRP_data vrpData = new VRP_data();
		//vrpData.makeData();
		
		VRP_Example01.solveMe();
		
	}
	
	public static void model1() {
		try {
			
			IloCplex cplex = new IloCplex();
			
			// variable
			IloNumVar x = cplex.numVar(0, Double.MAX_VALUE, "x");	// x >= 0
			IloNumVar y = cplex.numVar(0, Double.MAX_VALUE, "y");	// y >= 0
			
			// expressions
			IloLinearNumExpr objective = cplex.linearNumExpr();
			objective.addTerm(0.12, x);	// 목적함수 x 계수
			objective.addTerm(0.15, y);	// 목적함수 y 계수 (음수면 -0.15로 표현)
			
			// define objective
			cplex.addMinimize(objective);	// 목적함수 정의 : Minimize
			
			// define constrains
			cplex.addGe(cplex.sum(cplex.prod(60,x), cplex.prod(60, y)), 300);	// addGe : >=
					//	cplex.prod : multiple, cplex.sum : sum
			cplex.addGe(cplex.sum(cplex.prod(12,x), cplex.prod(6, y)), 36);	// addGe : >=
			cplex.addGe(cplex.sum(cplex.prod(10,x), cplex.prod(30, y)), 90);	// addGe : >=
			
			//	solve
			if( cplex.solve() ) {
				System.out.println("obj = " + cplex.getObjValue());
				System.out.println("x = " + cplex.getValue(x));
				System.out.println("y = " + cplex.getValue(y));
			}
			else {
				System.out.println("Model no solved");
			}
			
			cplex.end();
		}
		catch (IloException exc) {
			exc.printStackTrace();
		}
	}
	
}
