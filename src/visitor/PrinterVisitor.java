package visitor;

import ast.ASTNode;

import java.io.PrintStream;


/**
 * An AST visitor which prints the tree.
 */
public class PrinterVisitor extends LevelVisitor {
    private PrintStream stream;

    
    /**
     * Constructs a PrinterVisitor which will print to the specified stream.
     * 
     * @param stream  the stream to which to print.
     */
    public PrinterVisitor(PrintStream stream) {
        this.stream = stream;
    }

    
    /**
     * Prints the current node.
     * 
     * @param node  the current node in the traversal.
     */
    public void executePrevisit(ASTNode node) {
        stream.println(indent() + node.toString());
    }


    public void executePostvisit(ASTNode node) {
        /* do nothing */
    }
}
