package ast;

public class ExpressionNode extends BaseASTNode {
    private boolean isIdentifier;
    private String resultName;
    private PrimitiveType type;

    public ExpressionNode() {
        super(NodeType.EXPRESSION_STATEMENT);
    }


    public boolean isIdentifier() {
        return isIdentifier;
    }

    public void setIsIdentifier() throws Exception {
        if (getChild(0).getNodeType() == NodeType.VAR_USE) {
            //EXPR -> VAR_USE -> ID
            IdentifierNode id = ((IdentifierNode) getChild(0).getChild(0));
            resultName = "%" + id.getValue();
            if (id.getSymbolInfo() == null)
                throw new Exception(id.getValue() + " not declared");
            type = id.getSymbolInfo().getType();
        } else {
            //EXPR -> LITERAL
            Literal literal = (Literal) getChild(0);
            resultName = getChild(0).toString();
            type = literal.getType();

        }
        isIdentifier = true;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    public PrimitiveType getType() {
        return type;
    }
}
