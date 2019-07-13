package ast;

public class ExpressionNode extends BaseASTNode {
    private boolean isIdentifier;
    private String resultName;

    public ExpressionNode() {
        super(NodeType.EXPRESSION_STATEMENT);
    }


    public boolean isIdentifier() {
        return isIdentifier;
    }

    public void setIsIdentifier() {
        if (getChild(0).getNodeType() == NodeType.VAR_USE)
            //EXPR -> VAR_USE -> ID
            resultName ="%"+((IdentifierNode) getChild(0).getChild(0)).getValue();
        else
            //EXPR -> LITERAL
            resultName=getChild(0).toString();
        isIdentifier = true;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName(String resultName) {
        this.resultName = resultName;
    }
}
