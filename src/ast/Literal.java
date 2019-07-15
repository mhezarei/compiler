package ast;

public abstract class Literal extends BaseASTNode {
    private Type type;

    public Literal(Type type) {
        super(NodeType.LITERAL);
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
