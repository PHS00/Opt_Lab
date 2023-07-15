package CVRP01;

import java.io.IOException;

import ilog.concert.*;
import ilog.cplex.*;

public class App {

	public static void main(String[] args) throws IOException {

		//CVRP_Data makeData = new CVRP_Data();
		//makeData.makeData();
		
		CVRP_Example01.solveMe();
		
	}

}
