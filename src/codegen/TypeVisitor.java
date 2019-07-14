package codegen;


import ast.*;

import java.util.*;

/**
 * A visitor which attaches SymbolInfo to Identifier nodes and method signatures
 * to Class nodes.
 */
public class TypeVisitor implements SimpleVisitor {
    private SymbolTable symbolTable = new SymbolTable();
    private boolean inMethodBlock;

    @Override
    public void visit(ASTNode node) throws Exception {
        switch (node.getNodeType()) {
            case BLOCK:
                visitBlockNode(node);
                break;

            case IDENTIFIER:
                visitIdentifierNode(node);
                break;

            case METHOD_DECLARATION:
                visitMethodDeclarationNode(node);
                break;

            case ARGUMENT:
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
        if(!inMethodBlock) {
            symbolTable.enterScope();
            visitAllChildren(node);
            symbolTable.leaveScope();
        }else
            visitAllChildren(node);
    }

    private void visitIdentifierNode(ASTNode node) {
        IdentifierNode idNode = (IdentifierNode) node;
        String id = idNode.getValue();
        SymbolInfo si = symbolTable.get(id);
        node.setSymbolInfo(si);
    }

    private void visitMethodDeclarationNode(ASTNode node) throws Exception {
        TypeNode returnType = (TypeNode) node.getChild(0);
        IdentifierNode idNode = (IdentifierNode) node.getChild(1);
        String methodName = idNode.getValue();

        symbolTable.enterScope();
        inMethodBlock=true;
        Signature signature = new Signature(returnType.getType(), methodName);
        signature.addArgs(visitArgumentNode(node.getChild(2)));

        if (CodeGenVisitor.signatures.containsKey(methodName))
            if (methodName.equals("main")) {
                throw new Exception("main() declared before");
            } else {

                HashSet<Signature> signatures = CodeGenVisitor.signatures.get(methodName);
                if (signatures.contains(signature))
                    throw new Exception(methodName + "() with this signature declared before");
                if (signatures.stream().findAny().get().getReturnType() != returnType.getType())
                    throw new Exception(methodName + "() can't be declare by this return type");
                CodeGenVisitor.signatures.get(methodName).add(signature);
            }
        else {
            HashSet<Signature> set = new HashSet<>();
            set.add(signature);
            CodeGenVisitor.signatures.put(methodName, set);
        }

        visitAllChildren(node);
        symbolTable.leaveScope();
    }

    private List<Argument> visitArgumentNode(ASTNode node) {
        List<Argument> arguments = new ArrayList<>();

        ASTNode[] params = node.getChildren().toArray(new ASTNode[0]);
        for (ASTNode paramNode : params) {
            TypeNode paramTypeNode = (TypeNode) paramNode.getChild(0);
            IdentifierNode paramIDNode = (IdentifierNode) paramNode.getChild(1);

            arguments.add(new Argument(paramTypeNode.getType(), paramIDNode.getValue()));
        }

        return arguments;
    }

    private void visitVariableDeclarationNode(ASTNode node) throws Exception {
        //may has not child
        if (node.getChildren().isEmpty())
            return;

        TypeNode typePrimitive = null;
        IdentifierNode typeID = null;
        boolean isPrimitive;
        if (node.getChild(0) instanceof TypeNode) {
            isPrimitive = true;
            typePrimitive = (TypeNode) node.getChild(0);
        } else {
            isPrimitive = false;
            typeID = (IdentifierNode) node.getChild(0);
        }

        if (!isPrimitive && typeID.getSymbolInfo() == null)
            throw new Exception(typeID.getValue() + " not declared");

        for (int i = 1; i < node.getChildren().size(); i++) {
            IdentifierNode idNode;
            if (node.getChild(i) instanceof IdentifierNode)
                //DEC -> ID
                idNode = (IdentifierNode) node.getChild(i);
            else
                //DEC -> ASSIGN -> EXPR -> VAR_USE -> ID
                idNode = (IdentifierNode) node.getChild(i).getChild(0).getChild(0).getChild(0);
            String id = idNode.getValue();

            SymbolInfo si = new SymbolInfo(idNode);
            si.setType(typePrimitive.getType());
            idNode.setSymbolInfo(si);

            if(id.equals("d"))
                System.out.println("**");
            if (symbolTable.contains(id))
                throw new Exception(id + " declared before");

            symbolTable.put(id, si);
        }
        visitAllChildren(node);
    }
}
