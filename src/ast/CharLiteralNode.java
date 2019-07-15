package ast;

public class CharLiteralNode extends Literal {
    private char value;
    private int intVal;
    
    public CharLiteralNode(char value) {
        super(PrimitiveType.CHAR);
        this.value = value;
        intVal = (int) value;
    }

    public int getIntVal() {
        return intVal;
    }

    public char getValue() {
        return value;
    }

    @Override
    public String toString() {
        return intVal+"";
    }
}
