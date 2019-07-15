package codegen;

import ast.ASTNode;
import ast.PrimitiveType;
import ast.Type;

/**
 * Descriptor of identifiers
 */
public class SymbolInfo implements Symbol{
    private ASTNode node;
    private Type type;
    private boolean isConst;

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    private int localVarIndex = -1;

    SymbolInfo(ASTNode node) {
        this.node = node;
    }

    public ASTNode getNode() {
        return node;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getLocalVarIndex() {
        return localVarIndex;
    }

    void setLocalVarIndex(int index) {
        this.localVarIndex = index;
    }

    public String toString() {
        String str = "SymbolInfo: " + type;

        if (localVarIndex != -1) {
            str += ", lv = " + localVarIndex;
        }

        return str;
    }
}
