package ast;

public class CharLiteralNode extends BaseASTNode {
    private char value;
    
    public CharLiteralNode(char value) {
        super(NodeType.CHAR_LITERAL);
        this.value = value;
    }
    
    public char getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return getNodeType() + " (" + value + ")";
    }
}
