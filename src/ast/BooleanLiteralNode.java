package ast;


public class BooleanLiteralNode extends Literal {
    private boolean value;
    private int intVal;
    
    public BooleanLiteralNode(boolean value) {
        super(PrimitiveType.BOOL);
        this.value =  value;
        intVal = value ? 1 : 0;
    }

    public int getIntVal() {
        return intVal;
    }

    public boolean getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return intVal+"";
    }
}
