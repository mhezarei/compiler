package ast;

public class StringLiteralNode extends Literal {
    private String value;

    public StringLiteralNode(String value) {
        super(PrimitiveType.STRING);
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value+"";
    }
}
