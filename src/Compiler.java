import ast.Program;
import codegen.CodeGenVisitor;
import codegen.LocalVarMapVisitor;
import parser.Parser;
import scanner.Scanner;
import semantic.TypeVisitor;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;

public class Compiler {
    public static void main(String[] args) throws Exception {
        String source="src/Code.txt";

        Compiler compiler = new Compiler(source);
        compiler.run();
    }

    private String source;

    private Compiler(String source) {
        this.source = source;
    }

    private void run() throws Exception {
        processFile();
    }

    private void processFile() throws Exception {
        Program cu = parse();
        performSemanticAnalysis(cu);
        performLocalVarMapping(cu);
        generateCode(cu);
    }

    private Program parse() throws Exception {
        FileInputStream inStream = new FileInputStream(source);
        DataInputStream distress = new DataInputStream(new BufferedInputStream(inStream));

        Parser parser = new Parser(new Scanner(distress));
        parser.parse();
        return parser.getRoot();
    }

    private void performSemanticAnalysis(Program cu) throws Exception {
        cu.accept(new TypeVisitor());
    }

    private void performLocalVarMapping(Program cu) throws Exception {
        cu.accept(new LocalVarMapVisitor());
    }

    private void generateCode(Program cu) throws Exception {
        cu.accept(new CodeGenVisitor(System.out));
    }
}
