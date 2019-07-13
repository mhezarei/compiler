package ast;

public class FloatLiteralNode extends BaseASTNode {
    private double value;
    
    public FloatLiteralNode(double value) {
        super(NodeType.FLOAT_LITERAL);
        this.value = value;
    }
    
    public double getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value+"";
    }
}
