package ast;


public class BooleanLiteralNode extends Literal {
    private boolean value;
    
    public BooleanLiteralNode(boolean value) {
        super(PrimitiveType.BOOL);
        this.value = value;
    }
    
    public boolean getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value+"";
    }
}
