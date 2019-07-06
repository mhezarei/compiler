package ast;

public class TypeNode extends BaseASTNode {
    private PrimitiveType type;

    public TypeNode(NodeType nodeType, PrimitiveType type) {
        super(nodeType);
        this.type = type;
    }
    
    public PrimitiveType getType() {
        return type;
    }
}
