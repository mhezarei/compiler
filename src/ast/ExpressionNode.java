package ast;

public class ExpressionNode extends BaseASTNode {
    private boolean isIdentifier;
    private String resultName;
    private Type type;

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
            if (!id.getValue().startsWith("%")) {
                resultName = "%" + id.getValue();
            } else {
                resultName = id.getValue();
            }
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
