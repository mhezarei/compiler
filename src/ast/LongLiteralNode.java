package ast;

public class LongLiteralNode extends Literal {
    private long value;

    public LongLiteralNode(long value) {
        super(PrimitiveType.LONG);
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
