package Cap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Write {

	public static Scanner setScanner(String s) throws FileNotFoundException {
		return new Scanner(new File(s));
	}
	
	public static BufferedWriter getBw(String s) throws IOException {
		return new BufferedWriter(new FileWriter(s, false));
	}
	
}
