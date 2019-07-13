package codegen;

import ast.*;
import semantic.SymbolInfo;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

public class CodeGenVisitor implements SimpleVisitor {
    private PrintStream stream;
    private int labelIndex;
    private String className;
    private ClassNode classNode = new ClassNode();
    private boolean returnGenerated;
    private Set<Integer> usedTemps = new HashSet<>();

    public CodeGenVisitor(PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public void visit(ASTNode node) throws Exception {
        switch (node.getNodeType()) {
            case DIVISION:
            case ADDITION:
            case SUBTRACTION:
            case BOOLEAN_AND:
            case BOOLEAN_OR:
            case MULTIPLICATION:
            case ARITHMETIC_AND:
            case ARITHMETIC_OR:
            case XOR:
            case MOD:
                twoOperandOperation(node);
                break;

            case ASSIGN:
                visitAssignNode(node);
                break;

            case BOOLEAN_NOT:
                visitBooleanNotNode(node);
                break;

            case CLASS:
                visitClassNode(node);
                break;

            case EQUAL:
                visitEqualNode(node);
                break;

            case GREATER_THAN:
                visitGreaterThanNode(node);
                break;

            case GREATER_THAN_OR_EQUAL:
                visitGreaterThanOrEqualNode(node);
                break;

            case IF_STATEMENT:
                visitIfStatementNode(node);
                break;

            case LITERAL:
                visitLiteralNode(node);
                break;

            case LESS_THAN:
                visitLessThanNode(node);
                break;

            case LESS_THAN_OR_EQUAL:
                visitLessThanOrEqualNode(node);
                break;

            case METHOD_ACCESS:
                visitMethodAccessNode(node);
                break;

            case METHOD_DECLARATION:
                visitMethodDeclarationNode(node);
                break;

            case NOT_EQUAL:
                visitNotEqualNode(node);
                break;

            case PARAMETER:
                visitParameterNode(node);
                break;

            case RETURN_STATEMENT:
                visitReturnStatementNode(node);
                break;

            case UNARY_MINUS:
                visitUnaryMinusNode(node);
                break;

            case UNARY_PLUS:
                visitUnaryPlusNode(node);
                break;

            case VAR_USE:
                visitVarUse(node);
                break;

            case REPEAT_STATEMENT:
                visitWhileStatementNode(node);
                break;
            case ARGUMENTS:
                visitArgumentNode(node);
                break;

            case EXPRESSION_STATEMENT:
                visitExpressionNode(node);
                break;
            case DECREMENT:
                //todo;
                break;
            case INCREMENT:
                //todo
                break;
            case SIZEOF:
                //todo
                break;
            case ADD_ASSIGN:
                //todo
                break;
            case DIV_ASSIGN:
                //todo
                break;
            case SUB_ASSIGN:
                //todo
                break;
            case MULT_ASSIGN:
                //todo
                break;
            case BITWISE_NEGATIVE:
                //todo
                break;
            case STRUCT_DECLARATION:
            case CONTINUE_STATEMENT:
            case FOREACH_STATEMENT:
            case BREAK_STATEMENT:
            case STRING_TYPE:
            case FLOAT_TYPE:
            case LONG_TYPE:
            case AUTO_TYPE:
            case SWITCH_STATEMENT:
            case CASE_STATEMENT:
            case PARAMETERS:
            case VARIABLE_CONST_DECLARATION:
            case FOR_STATEMENT:
            case ARGUMENT:
            case BLOCK:
            case BOOLEAN_TYPE:
            case CHAR_TYPE:
            case START:
            case DECLARATIONS:
            case DOUBLE_TYPE:
            case EMPTY_STATEMENT:
            case FIELD_DECLARATION:
            case IDENTIFIER:
            case NULL_LITERAL:
            case INT_TYPE:
            case LOCAL_VAR_DECLARATION:
            case POST_DECREMENT:
            case POST_INCREMENT:
            case PRE_DECREMENT:
            case PRE_INCREMENT:
            case VARIABLE_DECLARATION:
            case VOID:
            default:
                visitAllChildren(node);
        }
    }

    private void visitExpressionNode(ASTNode node) throws Exception {
        ExpressionNode expressionNode = (ExpressionNode) node;

        if (!expressionNode.isIdentifier()) {
            ASTNode child = node.getChild(0);
            child.setParent(node);
            child.accept(this);
        }
    }

    private void visitAllChildren(ASTNode node) throws Exception {
        for (ASTNode child : node.getChildren()) {
            child.accept(this);
        }
    }

    private void twoOperandOperation(ASTNode node) throws Exception {
        ExpressionNode parent = (ExpressionNode) node.getParent();
        ExpressionNode e1 = (ExpressionNode) node.getChild(0);
        ExpressionNode e2 = (ExpressionNode) node.getChild(1);

        e1.accept(this);
        e2.accept(this);

        String op = getOperation(node.getNodeType());

        String result = "tmp" + getTemp();
        PrimitiveType resultType = checkType(e1, e2, node.getNodeType());
        if (resultType == PrimitiveType.FLOAT || resultType == PrimitiveType.DOUBLE)
            op = "f" + op;

        stream.println("\t%" + result + " = " + op + " " + resultType + " " + e1.getResultName() + ", " + e2.getResultName());

        ASTNode v = new BaseASTNode(NodeType.VAR_USE);
        ASTNode id = new IdentifierNode(result);
        SymbolInfo si = new SymbolInfo(id);
        si.setType(resultType);
        id.setSymbolInfo(si);
        v.addChild(id);
        v.setParent(parent);
        parent.setChildren(v);
        parent.setIsIdentifier();
    }

    private String getOperation(NodeType nodeType) throws Exception {
        switch (nodeType) {
            case ADDITION:
                return "add";
            //todo other operations
        }

        throw new Exception("operation not detected");
    }

    private int getTemp() {
        int i = 0;

        while (usedTemps.contains(i))
            i++;
        usedTemps.add(i);

        return i;
    }

    private PrimitiveType checkType(ExpressionNode e1, ExpressionNode e2, NodeType nodeType) throws Exception {
        //todo reduce complexity
        //todo should cast
        if (!e1.isIdentifier())
            throw new Exception(e1 + " not generated");
        if (!e2.isIdentifier())
            throw new Exception(e2 + " not generated");

        switch (nodeType) {
            case ADDITION:
            case SUBTRACTION:
            case MULTIPLICATION:
            case MOD:
            case DIVISION:
                switch (e1.getType()) {
                    case INT:
                    case BOOL:
                        switch (e2.getType()) {
                            case INT:
                            case CHAR:
                            case BOOL:
                                return PrimitiveType.INT;
                            case LONG:
                                return PrimitiveType.LONG;
                            case DOUBLE:
                                return PrimitiveType.DOUBLE;
                            case FLOAT:
                                return PrimitiveType.FLOAT;
                            case STRING:
                            case VOID:
                                throw new Exception("can't add");
                            case AUTO:
                                //todo
                        }
                    case CHAR:
                        switch (e2.getType()) {
                            case INT:
                            case BOOL:
                            case CHAR:
                                return PrimitiveType.INT;
                            case LONG:
                                return PrimitiveType.LONG;
                            case DOUBLE:
                            case FLOAT:
                            case STRING:
                            case VOID:
                                throw new Exception("can't add");
                            case AUTO:
                                //todo
                        }
                    case LONG:
                        switch (e2.getType()) {
                            case INT:
                            case LONG:
	                        case BOOL:
                            case CHAR:
                                return PrimitiveType.LONG;
                            case DOUBLE:
                                return PrimitiveType.DOUBLE;
                            case FLOAT:
                                return PrimitiveType.FLOAT;
                            case STRING:
                            case VOID:
                                throw new Exception("can't add");
                            case AUTO:
                                //todo
                        }
                    case FLOAT:
                        switch (e2.getType()) {
                            case INT:
                            case LONG:
	                        case BOOL:
                            case FLOAT:
	                        case CHAR:
                                return PrimitiveType.FLOAT;
                            case DOUBLE:
                                return PrimitiveType.DOUBLE;
                            case STRING:
                            case VOID:
                                throw new Exception("can't add");
                            case AUTO:
                                //todo
                        }
                    case DOUBLE:
                        switch (e2.getType()) {
                            case INT:
                            case LONG:
                            case DOUBLE:
                            case FLOAT:
	                        case CHAR:
	                        case BOOL:
                                return PrimitiveType.DOUBLE;
                            case STRING:
                            case VOID:
                                throw new Exception("can't add");
                            case AUTO:
                                //todo
                        }
                    case STRING:
                    case VOID:
                        throw new Exception("can't add");
                    case AUTO:
                        //todo
                }

            case ARITHMETIC_AND:
            case ARITHMETIC_OR:
            case XOR:
                if ((e1.getType() == PrimitiveType.INT || e1.getType() == PrimitiveType.LONG) &&
		                (e2.getType() == PrimitiveType.INT || e2.getType() == PrimitiveType.LONG)) {
	                if (e1.getType() == PrimitiveType.INT && e2.getType() == PrimitiveType.INT) {
		                return PrimitiveType.INT;
	                } else {
		                return PrimitiveType.LONG;
	                }
                } else {
                    throw new Exception("can't add");
                }
        }

        return null;
    }

    private void visitUnaryMinusNode(ASTNode node) throws Exception {

    }

    private void visitUnaryPlusNode(ASTNode node) throws Exception {
        visitAllChildren(node);
    }


    /*Assigns thing at top of stack,
      OR if it's a literal, pushes the literal then assigns that val
      OR if it's an ID loads the ID's value and assigns*/
    private void visitAssignNode(ASTNode node) throws Exception {
        //node -> EXPRESSION -> VAR_USE -> ID
        IdentifierNode idNode = (IdentifierNode) node.getChild(0).getChild(0).getChild(0);

        SymbolInfo si = idNode.getSymbolInfo();

        if (si == null)
            throw new Exception(idNode.getValue() + " not declared");

        /* Expression node */
        node.getChild(1).accept(this);

        IdentifierNode exprNode = (IdentifierNode) node.getChild(1).getChild(0).getChild(0);

        stream.println("\t" + idNode + " = " + exprNode);
    }

    private void visitClassNode(ASTNode node) throws Exception {
        //todo "class" code
        classNode = (ClassNode) node;

        IdentifierNode idNode = (IdentifierNode) node.getChild(0);
        className = idNode.getValue();

        stream.println(".class public " + className);
        stream.println(".super java/lang/Object");
        stream.println("");

        outputDefaultConstructor();
        outputMainMethod();
        outputPrintlnMethod();

        returnGenerated = false;

        node.getChild(1).accept(this);

    }

    private void visitBooleanNotNode(ASTNode node) {
        //todo
    }

    private void visitEqualNode(ASTNode node) throws Exception {
        // todo "EQ" code
    }

    private void visitGreaterThanNode(ASTNode node) throws Exception {
        // todo "GT" code
    }

    private void visitGreaterThanOrEqualNode(ASTNode node) throws Exception {
        // todo "GE" code
    }

    private void visitNotEqualNode(ASTNode node) throws Exception {
        // todo "NE" code
    }

    private void visitLessThanNode(ASTNode node) throws Exception {
        // todo "LT" code
    }

    private void visitLessThanOrEqualNode(ASTNode node) throws Exception {
        // todo "LE" code
    }

    private void visitIfStatementNode(ASTNode node) throws Exception {
        //todo "if" code
        stream.println("; if statement");
        node.getChild(0).accept(this); //predicate
        stream.println("  iconst_0");
        String endIfLabel = generateLabel();
        stream.println("ifeq " + endIfLabel);//if predicate false, then skip the "do if true" block


        node.getChild(0).accept(this); //print the "do if true" block
        stream.println("  goto " + endIfLabel); //bypass the "do If True" Block

        if (node.getChildren().size() == 3) //We have an else statement
        {
            String elseLabel = generateLabel();
            stream.println(elseLabel); //The else block
            node.getChild(2).accept(this);
        }

    }

    private void visitWhileStatementNode(ASTNode node) throws Exception {
        // todo "while" code
    }

    private void visitLiteralNode(ASTNode node) throws Exception {
        ((ExpressionNode) node.getParent()).setIsIdentifier();
    }

    private void visitBooleanLiteralNode(ASTNode node) {
        //todo "boolean literal" code
        BooleanLiteralNode boolNode = (BooleanLiteralNode) node;
        if (boolNode.getValue()) {
            stream.println("  iconst_1");
        } else {
            stream.println("  iconst_0");
        }
    }

    private void visitMethodAccessNode(ASTNode node) throws Exception {
        //todo need to understand
        node.getChild(1).accept(this);

        IdentifierNode idNode = (IdentifierNode) node.getChild(0);
        String methodName = idNode.getValue();
        String sig = classNode.getMethodSig(methodName);
//        String sig = methodName;
        stream.println("  invokestatic " + className + "/" + sig);
        returnGenerated = false;
    }

    private void visitMethodDeclarationNode(ASTNode node) throws Exception {
        //type
        TypeNode typeNode = (TypeNode) node.getChild(0);
        String returnType = typeNode.getType().getSignature();
        //identifier
        IdentifierNode idNode = (IdentifierNode) node.getChild(1);
        String methodName = idNode.getValue();

        stream.print("define " + returnType + " @" + methodName);
        //arguments
        node.getChild(2).accept(this);
        stream.println(" {");
        stream.println("entry:");
        //block
        node.getChild(3).accept(this);

        if (!returnGenerated) {
            stream.println("  return");
            returnGenerated = true;
        }

        stream.println("}");
    }

    private void visitArgumentNode(ASTNode node) {
        stream.print("(");
        ASTNode[] params = node.getChildren().toArray(new ASTNode[0]);
        for (int i = 0; i < params.length; i++) {
            ASTNode paramNode = params[i];
            //type
            TypeNode paramTypeNode = (TypeNode) paramNode.getChild(0);
            stream.print(paramTypeNode.getType().getSignature() + " ");
            //identifier
            IdentifierNode paramIDNode = (IdentifierNode) paramNode.getChild(1);
            stream.print("%" + paramIDNode.getValue());
            if (i > 0 && i < params.length - 1)
                stream.print(",");
        }
        stream.print(")");
    }

    private void visitParameterNode(ASTNode node) throws Exception {
        // todo "parameters" code
        TypeNode typeNode = (TypeNode) node.getChild(1);
        String typeSig = typeNode.getType().getSignature();
        stream.print(typeSig);
        returnGenerated = false;
    }

    private void visitReturnStatementNode(ASTNode node) throws Exception {
        // todo "return" code
        visitAllChildren(node);

        returnGenerated = true;
        if (node.getChildren().isEmpty()) {
            stream.println("  return");
        } else {
            stream.println("  ireturn");
        }
    }

    private void visitVarUse(ASTNode node) throws Exception {
        //todo need to understand
        IdentifierNode idNode = (IdentifierNode) node.getChild(0);
        ((ExpressionNode) node.getParent()).setIsIdentifier();
        SymbolInfo si = idNode.getSymbolInfo();
        try {
            int lvIndex = si.getLocalVarIndex();
            returnGenerated = false;
        } catch (NullPointerException e) {
            throw new Exception(idNode.getValue() + " not declared");
        }
    }

    private String generateLabel() {
        return "label" + (++labelIndex);
    }

    private void outputDefaultConstructor() {
        //todo need to understand
        stream.println("");
        stream.println(";");
        stream.println("; standard constructor");
        stream.println(";");
        stream.println(".method public <init>()V");
        stream.println("  aload_0");
        stream.println("  invokenonvirtual java/lang/Object/<init>()V");
        stream.println("  return");
        stream.println(".end method");
        stream.println("");
    }

    private void outputMainMethod() {
        //todo "main" code
        stream.println("");
        stream.println(";");
        stream.println("; main method");
        stream.println(";");
        stream.println(".method public static main([Ljava/lang/String;)V");
        stream.println("  invokestatic " + className + "/program()V");
        stream.println("  return");
        stream.println(".end method");
        stream.println("");
    }

    private void outputPrintlnMethod() {
        //todo "println" code
        stream.println("");
        stream.println(";");
        stream.println("; println method");
        stream.println(";");
        stream.println(".method public static println(I)V");
        stream.println("  .limit stack 2");
        stream.println("  getstatic java/lang/System/out Ljava/io/PrintStream;");
        stream.println("  iload_0");
        stream.println("  invokevirtual java/io/PrintStream/println(I)V");
        stream.println("  return");
        stream.println(".end method");
        stream.println("");
    }
}