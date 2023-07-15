package MainApp;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import RVRP_Model01_Uvariable.Model01_Uvariable;
import RVRP_Model02_Lazy.Model02_Lazy;
import RVRP_Model03_LazyCut.Model03_LazyCut;
import RVRP_Model04_Lazy_U.Model04_Lazy_U;
import RVRP_Model05_algo.Model05_algo;
import ilog.concert.*;
import ilog.cplex.*;
import ilog.cplex.IloCplex.UnknownObjectException;

public class App {

	public static void main(String[] args) throws IOException, IloException {

		String dataPath = "C:\\Users\\최적화_연구실_PC1\\OneDrive - Chonnam National University\\바탕 화면\\Optimize\\RVRP\\RVRP\\Data\\";
		String dataInstancePath = "C:\\Users\\최적화_연구실_PC1\\OneDrive - Chonnam National University\\바탕 화면\\Optimize\\RVRP\\RVRP\\Data_Instance\\";
		String resultPath = "C:\\Users\\최적화_연구실_PC1\\OneDrive - Chonnam National University\\바탕 화면\\Optimize\\RVRP\\RVRP\\Result\\";
		String instance = "_Instance";
		String result = "_Result";
		String model = "_Model";
		String txt = ".txt";
		String gamma = "_gamma";

		Model01_Uvariable m1 = new Model01_Uvariable();

		Model02_Lazy m2 = new Model02_Lazy();

		Model03_LazyCut m3 = new Model03_LazyCut();

		Model04_Lazy_U m4 = new Model04_Lazy_U();

		Model05_algo m5 = new Model05_algo();

		String dataNameFile = "";
		String Name = ""; // data name
		String problem = ""; // starting data file
		String dataInstanceName = ""; // data instance file name
		String dataInstanceFileScanner = ""; // data instance file scanner
		String resultFileName = ""; // result file name

		
		dataNameFile = "Name";
		problem = "gr21"; // set data instance name
		int k =3;	
		
		//setting vehicles
		m1.setVehicle(k);
		m4.setVehicle(k);
		m5.setVehicle(k);
		
		// set kind of method
		String method = "_Method01";

		Scanner dataNameFileScanner = new Scanner(new File(dataInstancePath + problem + "\\" + dataNameFile + txt));
		
		// read data name
		Name = dataNameFileScanner.next();

		// set starting point
//		String dataName = "P_0_G_5";
//		while (!Name.equals(dataName)) {
//			Name = dataNameFileScanner.next();
//		}

		while (Name != null) {

			dataInstanceName = Name;
			dataInstanceFileScanner = dataInstancePath +  problem + "\\" + dataInstanceName + txt;
			
			method = "_Method01";
			m1.setScanner(dataInstanceFileScanner);
			resultFileName = problem + "_"+ Name + method + result + txt;
			m1.setBw(resultPath + resultFileName);
			m1.solveMe();
			
			method = "_Method04";
			m4.setScanner(dataInstanceFileScanner);
			resultFileName = problem + "_"+ Name + method + result + txt;
			m4.setBw(resultPath + resultFileName);
			m4.solveMe();

			method = "_Method05";
			m5.setScanner(dataInstanceFileScanner);
			resultFileName = problem + "_"+ Name + method + result + txt;
			m5.setBw(resultPath + resultFileName);
			m5.solveMe();
//				switch (method) {
//				case "_Method01":
//					m1.setScanner(dataInstanceFileScanner);
//					resultFileName = problem + "_"+ Name + method + result + txt;
//					m1.setBw(resultPath + resultFileName);
//					m1.solveMe();
//					break;
//
//				case "_Method04":
//					m4.setScanner(dataInstanceFileScanner);
//					resultFileName = problem + "_"+ Name + method + result + txt;
//					m4.setBw(resultPath + resultFileName);
//					m4.solveMe();
//					break;
//
//				case "_Method05":
//					m5.setScanner(dataInstanceFileScanner);
//					resultFileName = problem + "_"+ Name + method + result + txt;
//					m5.setBw(resultPath + resultFileName);
//					m5.solveMe();
//					break;
//				}
			Name = dataNameFileScanner.next();

		}
		

	}

}
