package ast;

public class LongLiteralNode extends BaseASTNode {
    private long value;

    public LongLiteralNode(long value) {
        super(NodeType.LONG_LITERAL);
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value+"";
    }
}
