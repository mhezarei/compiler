package codegen;

import ast.ASTNode;

public interface SimpleVisitor {
    void visit(ASTNode node) throws Exception;
}
