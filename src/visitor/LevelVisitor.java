/*
 * CSE 431S Programming Assignment 3
 */

package visitor;


import ast.ASTNode;

/**
 * An AST visitor which keeps track of the level of the tree it's on.  (Useful
 * for nicely formatted printing.)
 */
public abstract class LevelVisitor implements ASTVisitor {
    private int level;

    
    /**
     * Constructs a LevelVisitor.
     */
    protected LevelVisitor() {
        level = -1;
    }


    /**
     * Increments the nest level.
     * 
     * @param node  the current node of the traversal.
     */
    public final void previsit(ASTNode node) {
        ++level;
        executePrevisit(node);
    }


    /**
     * Decrements the nest level.
     * 
     * @param node  the current node of the traversal.
     */
    public final void postvisit(ASTNode node) {
        executePostvisit(node);
        --level;
    }
    
    
    /**
     * Returns the current nest level.
     * 
     * @return the current nest level.
     */
    public int getLevel() {
        return level;
    }

    
    /**
     * Returns a String of spaces whose length is proportional to the nest level.
     * 
     * @return a String of spaces whose length is proportional to the nest level.
     */
    protected String indent() {
        String offset = "";
        for (int i = 0; i < level; ++i)
            offset += "    ";

        return offset;
    }
}
