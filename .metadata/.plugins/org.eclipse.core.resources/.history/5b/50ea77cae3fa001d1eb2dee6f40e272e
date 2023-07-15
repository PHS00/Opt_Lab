package MakeData;

import java.io.IOException;

import ilog.concert.*;
import ilog.cplex.*;

public class App {

	public static void main(String[] args) throws IOException {
		
		String dataPath = "C:\\Users\\최적화_연구실_PC1\\OneDrive - Chonnam National University\\바탕 화면\\Optimize\\Capstone_VRP\\Data\\";
		String txt = ".txt";

		RVRP_Data makeData = new RVRP_Data();
		
		String dataName = dataPath + "DataName_P" + txt;
		makeData.setScanner(dataName);
		
		makeData.makeData();

		
	}

}
