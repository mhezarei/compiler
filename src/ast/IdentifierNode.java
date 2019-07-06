package ast;

public class IdentifierNode extends BaseASTNode {
    private String value;
    
    public IdentifierNode(String value) {
        super(NodeType.IDENTIFIER);
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        String str = getNodeType() + " (" + value + ")";

        if (symbolInfo != null) {
            str += " (" + symbolInfo.toString() + ")";
        }

        return str;
    }
}
