package ast;

/**
 * A class to keep a type like int or char
 */
public class TypeNode extends BaseASTNode {
    private Type type;

    public TypeNode(NodeType nodeType, Type type) {
        super(nodeType);
        this.type = type;
    }
    
    public Type getType() {
        return type;
    }
}
