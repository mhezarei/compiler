package semantic;


import ast.*;
import codegen.SimpleVisitor;

/**
 * A visitor which attaches SymbolInfo to Identifier nodes and method signatures
 * to Class nodes.
 */
public class TypeVisitor implements SimpleVisitor {
    private SymbolTable symbolTable = new SymbolTable();
    private PrimitiveType currentType;
    private ClassNode classNode;

    @Override
    public void visit(ASTNode node) throws Exception {
        switch (node.getNodeType()) {
            case BLOCK:
                visitBlockNode(node);
                break;

            case CLASS:
                visitClassNode(node);
                break;

            case IDENTIFIER:
                visitIdentifierNode(node);
                break;

            case LOCAL_VAR_DECLARATION:
                visitLocalVarDeclarationNode(node);
                break;

            case METHOD_DECLARATION:
                visitMethodDeclarationNode(node);
                break;

            case PARAMETER:
                visitParameterNode(node);
                break;

            case VARIABLE_DECLARATION:
                visitVariableDeclarationNode(node);
                break;

            default:
                visitAllChildren(node);
        }
    }

    private void visitAllChildren(ASTNode node) throws Exception {
        for (ASTNode child : node.getChildren()) {
            child.accept(this);
        }
    }

    private void visitBlockNode(ASTNode node) throws Exception {
        symbolTable.enterScope();
        visitAllChildren(node);
        symbolTable.leaveScope();
    }

    private void visitClassNode(ASTNode node) throws Exception {
        classNode = (ClassNode) node;

        symbolTable.enterScope();
        visitAllChildren(node);
        symbolTable.leaveScope();
    }

    /**
     * Sets symbolInfo
     */
    private void visitIdentifierNode(ASTNode node) {
        IdentifierNode idNode = (IdentifierNode) node;
        String id = idNode.getValue();
        SymbolInfo si = symbolTable.get(id);
        node.setSymbolInfo(si);
    }

    private void visitLocalVarDeclarationNode(ASTNode node) throws Exception {
        //todo need to understand
        TypeNode typeNode = (TypeNode) node.getChild(0);
        currentType = typeNode.getType();

        node.getChild(1).accept(this);

        currentType = null;
    }

    private void visitMethodDeclarationNode(ASTNode node) throws Exception {
        //0 -> 1
        IdentifierNode idNode = (IdentifierNode) node.getChild(1);
        String methodName = idNode.getValue();

        StringBuilder sig = new StringBuilder(methodName + "(");

        //seems to wrong
        for (ASTNode paramNode : node.getChild(2).getChildren()) {
            TypeNode typeNode = (TypeNode) paramNode.getChild(1);
            sig.append(typeNode.getType().getSignature());
        }

        sig.append(")");

        //todo seems to wrong
        TypeNode typeNode = (TypeNode) node.getChild(0);
        sig.append(typeNode.getType().getSignature());

//        classNode.putMethodSig(methodName, sig.toString());

        symbolTable.enterScope();
        visitAllChildren(node);
        symbolTable.leaveScope();
    }

    private void visitParameterNode(ASTNode node) throws Exception {
        TypeNode typeNode = (TypeNode) node.getChild(1);
        currentType = typeNode.getType();

        node.getChild(0).accept(this);

        currentType = null;
    }

    private void visitVariableDeclarationNode(ASTNode node) throws Exception {
        //may has not child
        if (node.getChildren().isEmpty())
            return;

        TypeNode typePrimitive=null;
        IdentifierNode typeID=null;
        boolean isPrimitive;
        if (node.getChild(0) instanceof TypeNode) {
            isPrimitive=true;
            typePrimitive = (TypeNode) node.getChild(0);
        } else {
            isPrimitive=false;
            typeID = (IdentifierNode) node.getChild(0);
        }

        if(!isPrimitive && typeID.getSymbolInfo()==null)
            throw new Exception(typeID.getValue()+" not declared");


        IdentifierNode idNode = (IdentifierNode) node.getChild(1);
        String id = idNode.getValue();

        SymbolInfo si = new SymbolInfo(node);
        si.setType(currentType);
        idNode.setSymbolInfo(si);

        symbolTable.put(id, si);

        visitAllChildren(node);
    }
}
