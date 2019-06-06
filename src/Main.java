import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		FileReader fileReader = new FileReader("src/Code.txt");
		CompilerScanner yylex = new CompilerScanner(fileReader);
		Element scanned = yylex.scanFunction();
		
		
		
		fileReader.close();
	}
}