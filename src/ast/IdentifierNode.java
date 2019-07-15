package ast;

public class IdentifierNode extends BaseASTNode {
    private String value;
    private boolean inConst;
    
    public IdentifierNode(String value) {
        super(NodeType.IDENTIFIER);
        this.value = value;
    }

    public boolean isInConst() {
        return inConst;
    }

    public void setInConst(boolean inConst) {
        this.inConst = inConst;
    }

    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return "%"+value;
    }
}
