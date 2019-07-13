package ast;

public class FloatLiteralNode extends Literal {
    private double value;

    public FloatLiteralNode(double value) {
        super(PrimitiveType.FLOAT);
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
