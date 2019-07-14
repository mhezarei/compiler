package codegen;


import ast.*;

import java.util.*;

/**
 * A visitor which attaches SymbolInfo to Identifier nodes and method signatures
 * to Class nodes.
 */
public class MethodVisitor implements SimpleVisitor {
    private Map<String, HashSet<Signature>> signatures = CodeGenVisitor.signatures;

    @Override
    public void visit(ASTNode node) throws Exception {
        switch (node.getNodeType()) {
            case METHOD_DECLARATION:
                visitMethodDeclarationNode(node);
                break;

            case ARGUMENT:
            case VARIABLE_DECLARATION:
                visitVariableDeclarationNode(node);
                break;

            case START:
                visitAllChildren(node);
                checkMain();
                break;

            default:
                visitAllChildren(node);
        }
    }

    private void checkMain() throws Exception {
        if (signatures.get("main")==null||signatures.get("main").isEmpty())
            throw new Exception("there is no main in code");
    }

    private void visitAllChildren(ASTNode node) throws Exception {
        for (ASTNode child : node.getChildren()) {
            child.accept(this);
        }
    }


    private void visitMethodDeclarationNode(ASTNode node) throws Exception {
        TypeNode returnType = (TypeNode) node.getChild(0);
        IdentifierNode idNode = (IdentifierNode) node.getChild(1);
        String methodName = idNode.getValue();


        Signature signature = new Signature(returnType.getType(), methodName);
        signature.addArgs(visitArgumentNode(node.getChild(2)));

        if (signatures.containsKey(methodName))
            if (methodName.equals("main")) {
                throw new Exception("main() declared before");
            } else {

                HashSet<Signature> signatureSet = signatures.get(methodName);
                Optional<Signature> aSig = signatureSet.stream().findAny();
                if (signatureSet.contains(signature))
                    throw new Exception(methodName + "() with this signature declared before");
                if (aSig.isPresent() && (aSig.get().getReturnType() != returnType.getType()))
                    throw new Exception(methodName + "() can't be declare by this return type");
                signatureSet.add(signature);
            }
        else {
            HashSet<Signature> set = new HashSet<>();
            set.add(signature);
            signatures.put(methodName, set);
        }

        visitAllChildren(node);
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

        visitAllChildren(node);
    }
}
