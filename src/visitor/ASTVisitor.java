/*
 * CSE 431S Programming Assignment 3
 */

package visitor;

import ast.ASTNode;

/**
 * The AST Visitor interface.
 */
public interface ASTVisitor {
    void previsit(ASTNode node);
    void postvisit(ASTNode node);

    void executePrevisit(ASTNode node);
    void executePostvisit(ASTNode node);
}
