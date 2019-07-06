
import ast.Program;
import codegen.CodeGenVisitor;
import codegen.LocalVarMapVisitor;
import parser.Parser;
import scanner.Scanner;
import semantic.TypeVisitor;
import visitor.PrinterVisitor;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class Compiler {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Please enter file name");
            System.exit(-1);
        }

        Compiler compiler = new Compiler(args[0]);
        compiler.run();

    }

    private String source;

    private Compiler(String source) {
        this.source = source;
    }

    private void run() throws Exception {
        processFile(new File(source));
    }

    private void processFile(File sourceFile) throws Exception {
        Program cu = parse(sourceFile);
        performSemanticAnalysis(cu);
        performLocalVarMapping(cu);
        generateCode(cu);
    }

    private Program parse(File sourceFile) throws Exception {
        FileInputStream istream = new FileInputStream(source);
        DataInputStream distream = new DataInputStream(new BufferedInputStream(istream));

        Parser parser = new Parser(new Scanner(distream));
        parser.parse();
        return parser.getRoot();
    }

    /**
     * Debugging pass: print the ASTs.
     */
    private void printAST(Program cu) {
        cu.accept(new PrinterVisitor(System.out));
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
