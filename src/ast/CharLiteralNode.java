package ast;

public class CharLiteralNode extends Literal {
    private char value;
    
    public CharLiteralNode(char value) {
        super(PrimitiveType.CHAR);
        this.value = value;
    }
    
    public char getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value+"";
    }
}
