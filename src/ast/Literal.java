package ast;

public abstract class Literal extends BaseASTNode {
    private PrimitiveType type;

    public Literal(PrimitiveType type) {
        super(NodeType.LITERAL);
        this.type = type;
    }

    public PrimitiveType getType() {
        return type;
    }
}
