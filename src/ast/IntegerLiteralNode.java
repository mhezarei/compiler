package ast;

public class IntegerLiteralNode extends Literal {
    private int value;

    public IntegerLiteralNode(int value) {
        super(PrimitiveType.INT);
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value+"";
    }
}
