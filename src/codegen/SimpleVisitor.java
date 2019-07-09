/*
 * CSE 431S Programming Assignment 3
 */

package codegen;


import ast.ASTNode;

/**
 * A simplified AST Visitor interface.
 */
public interface SimpleVisitor {
    void visit(ASTNode node) throws Exception;
}
