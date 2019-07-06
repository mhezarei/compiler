package ast;

public class IntegerLiteralNode extends BaseASTNode {
    private int value;
    
    public IntegerLiteralNode(int value) {
        super(NodeType.INTEGER_LITERAL);
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return getNodeType() + " (" + value + ")";
    }
}
