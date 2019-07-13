package typechecker;

import ast.ASTNode;

public class TypeChecker {
    private ASTNode firstOperand;
    private ASTNode secondOperand;

    public TypeChecker(ASTNode firstOperand, ASTNode secondOperand) {
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
    }

    public boolean isFloat() {
        // to-do

        return false;
    }

    public boolean isInteger() {
        // to-do

        return false;
    }
}
