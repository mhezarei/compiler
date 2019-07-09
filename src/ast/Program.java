package ast;

/**
 * Start node
 */
public class Program extends BaseASTNode {
    public Program() {
        super(NodeType.COMPILATION_UNIT);
    }
}
