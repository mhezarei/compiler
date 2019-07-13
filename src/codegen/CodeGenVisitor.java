package codegen;

import ast.*;
import semantic.SymbolInfo;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CodeGenVisitor implements SimpleVisitor {
    private PrintStream stream;
    private int labelIndex;
    private String className;
    private Set<Signature> signatures=new HashSet<>();
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
            case GREATER_THAN:
            case GREATER_THAN_OR_EQUAL:
            case LESS_THAN:
            case LESS_THAN_OR_EQUAL:
            case EQUAL:
            case NOT_EQUAL:
                binaryOperation(node);
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

            case IF_STATEMENT:
                visitIfStatementNode(node);
                break;

            case LITERAL:
                visitLiteralNode(node);
                break;


            case METHOD_ACCESS:
                visitMethodAccessNode(node);
                break;

            case METHOD_DECLARATION:
                visitMethodDeclarationNode(node);
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
//        System.out.println("--THE NODE IN VAC IS " + node + " " + node.getNodeType());
//        System.out.println("--THE CHILDREN ARE " + node.getChildren());
        for (ASTNode child : node.getChildren()) {
            child.accept(this);
        }
//        System.out.println("--FINISHED VAC\n");
    }

    private void binaryOperation(ASTNode node) throws Exception {
        ExpressionNode parent = (ExpressionNode) node.getParent();
//        System.out.println("node is " + node);
//        System.out.println("parent is " + parent);
//        System.out.println("G parent is " + parent.getParent());
//        System.out.println("parent child is " + parent.getChildren());
        ExpressionNode e1 = (ExpressionNode) node.getChild(0);
//        System.out.println("first child is " + e1.getChildren());
        ExpressionNode e2 = (ExpressionNode) node.getChild(1);
//        System.out.println("second child is " + e2.getChildren());

        e1.accept(this);
        e2.accept(this);

        PrimitiveType resultType = checkType(e1, e2, node.getNodeType());
        // operands are casted now
        String op = getOperation(node.getNodeType(), e1);
        String result = "tmp" + getTemp();

        stream.println("\t%" + result + " = " + op + " " + resultType + " " + e1.getResultName() + ", " + e2.getResultName());

        ASTNode v = new BaseASTNode(NodeType.VAR_USE);
        ASTNode id = new IdentifierNode(result);
        SymbolInfo si = new SymbolInfo(id);
        si.setType(resultType);
        id.setSymbolInfo(si);
        v.addChild(id);
        v.setParent(parent);
//        System.out.println("the id node is " + id.toString());
//        System.out.println("the id node SI is " + id.getSymbolInfo());
//        System.out.println("the var use node is " + v.toString());
//        System.out.println("the var use SI node is " + v.getSymbolInfo());
        parent.setChildren(v);
        parent.setIsIdentifier();

//        System.out.println("finished the binary op " + node + "\n");
    }

    private String getOperation(NodeType nodeType, ExpressionNode e1) throws Exception {
        String result = "";

        switch (e1.getType()) {
            case FLOAT:
            case DOUBLE:
                result = "f";
                break;
            case LONG:
            case CHAR:
            case INT:
            case BOOL:
                if (nodeType == NodeType.GREATER_THAN ||
                        nodeType == NodeType.GREATER_THAN_OR_EQUAL ||
                        nodeType == NodeType.LESS_THAN ||
                        nodeType == NodeType.LESS_THAN_OR_EQUAL ||
                        nodeType == NodeType.EQUAL ||
                        nodeType == NodeType.NOT_EQUAL) {
                    result = "i";
                } else if (nodeType == NodeType.DIVISION || nodeType == NodeType.MOD) {
                    result = "s";
                }
        } // command has f for float, i for normal cmp and s for normal div and rem

        switch (nodeType) {
            case ADDITION:
                return result + "add";
            case SUBTRACTION:
                return result + "sub";
            case MULTIPLICATION:
                return result + "mul";
            case DIVISION:
                return result + "div";
            case MOD:
                return result + "rem";
            case EQUAL:
            case NOT_EQUAL:
            case GREATER_THAN:
            case GREATER_THAN_OR_EQUAL:
            case LESS_THAN:
            case LESS_THAN_OR_EQUAL:
                result = result + "cmp ";
                break;
            case BOOLEAN_AND:
            case ARITHMETIC_AND:
                return result + "and";
            case BOOLEAN_OR:
            case ARITHMETIC_OR:
                return result + "or";
            case XOR:
                return result + "xor";
        } // command has the name

        //Only compares can reach here
        switch (e1.getType()) {
            case FLOAT:
            case DOUBLE:
                result = result + "o";
                break;
            case LONG:
            case CHAR:
            case INT:
            case BOOL:
                if (nodeType != NodeType.EQUAL && nodeType != NodeType.NOT_EQUAL) {
                    result = result + "s";
                }
        } // has o for float, s for {le, ge, lt, gt} and nothing for integer eq and ne

        switch (nodeType) {
            case EQUAL:
                return result + "eq";
            case NOT_EQUAL:
                return result + "ne";
            case GREATER_THAN:
                return result + "gt";
            case GREATER_THAN_OR_EQUAL:
                return result + "ge";
            case LESS_THAN:
                return result + "lt";
            case LESS_THAN_OR_EQUAL:
                return result + "le";
        }

        throw new Exception("No operation found!");
    }

    private int getTemp() {
        int i = 0;

        while (usedTemps.contains(i))
            i++;
        usedTemps.add(i);

        return i;
    }

    private PrimitiveType checkType(ExpressionNode e1, ExpressionNode e2, NodeType nodeType) throws Exception {
        // todo reduce complexity
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
        throw new Exception("can't add");
    }

    private void visitUnaryMinusNode(ASTNode node) throws Exception {
        //todo
    }

    private void visitUnaryPlusNode(ASTNode node) throws Exception {
        visitAllChildren(node);
    }


    /*Assigns thing at top of stack,
      OR if it's a literal, pushes the literal then assigns that val
      OR if it's an ID loads the ID's value and assigns*/
    private void visitAssignNode(ASTNode node) throws Exception {
//        System.out.println("assign children are " + node.getChildren());
        //node -> EXPRESSION -> VAR_USE -> ID
        IdentifierNode idNode = (IdentifierNode) node.getChild(0).getChild(0).getChild(0);

        SymbolInfo si = idNode.getSymbolInfo();

        if (si == null)
            throw new Exception(idNode.getValue() + " not declared");

        /* Expression node */
        node.getChild(1).accept(this);

        ASTNode exprNode = node.getChild(1).getChild(0);

        if(exprNode.getNodeType()==NodeType.VAR_USE)
            exprNode=exprNode.getChild(0);

        stream.println("\t" + idNode + " = " + exprNode);
    }

    private void visitClassNode(ASTNode node) throws Exception {
        //todo "class" code
//        classNode = (ClassNode) node;

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

    private void visitWhileStatementNode(ASTNode node) {
        // todo "while" code
    }

    private void visitLiteralNode(ASTNode node) throws Exception {
        ((ExpressionNode) node.getParent()).setIsIdentifier();
    }
    private void visitMethodAccessNode(ASTNode node) throws Exception {
        //Check the signature
        IdentifierNode idNode = (IdentifierNode) node.getChild(0);
        String methodName = idNode.getValue();
        //todo must detect left hand side of assign
        TypeNode returnType = new TypeNode(NodeType.VOID, PrimitiveType.VOID);
        Signature newSig = new Signature(returnType, methodName);

        //Expression
        node.getChild(1).accept(this);

        stream.print("\tcall " + returnType + " @" + methodName + "(");
        stream.println(")");
        if (node.getParent() != null) //is an expr


        {
            node.getChild(1).accept(this);
        }

        returnGenerated = false;
    }

    private List<Signature.Argument> visitParameterNode(ASTNode node) throws Exception {
        //PARAMETER -> EXPR
        node.getChild(0).accept(this);
        returnGenerated = false;
        return null;
    }


    private void visitMethodDeclarationNode(ASTNode node) throws Exception {
//        System.out.println("IN METHOD DCL THE CHILDREN ARE " + node.getChildren());
        //type
        TypeNode returnType = (TypeNode) node.getChild(0);
        String returnSig = returnType.getType().getSignature();
        //identifier
        IdentifierNode idNode = (IdentifierNode) node.getChild(1);
        String methodName = idNode.getValue();

        Signature signature = new Signature(returnType, methodName);

        stream.print("define ");
        stream.print(returnSig + " @" + methodName);
        //arguments
        signature.addArgs(visitArgumentNode(node.getChild(2)));
        signatures.add(signature);

        stream.println(" {");
        stream.println("entry:");
        //block
        node.getChild(3).accept(this);

        //todo return must do
        if (!returnGenerated) {
            stream.println("  return");
            returnGenerated = true;
        }

        stream.println("}");
    }

    private List<Signature.Argument> visitArgumentNode(ASTNode node) throws Exception {
        List<Signature.Argument> arguments = new ArrayList<>();

        stream.print("(");
        ASTNode[] params = node.getChildren().toArray(new ASTNode[0]);
        for (int i = 0; i < params.length; i++) {
            if (i > 0)
                stream.print(",");

            ASTNode paramNode = params[i];
            //type
            TypeNode paramTypeNode = (TypeNode) paramNode.getChild(0);
            stream.print(paramTypeNode.getType().getSignature() + " ");
            //identifier
            IdentifierNode paramIDNode = (IdentifierNode) paramNode.getChild(1);
            stream.print("%" + paramIDNode.getValue());

            Signature.Argument argument = new Signature.Argument(paramTypeNode, paramIDNode.getValue());
            if (arguments.contains(argument))
                throw new Exception(paramIDNode.getValue() + " is declared before");
            arguments.add(argument);
        }
        stream.print(")");
        return arguments;
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
//        System.out.println("in VAR_USE");
        //todo need to understand
        IdentifierNode idNode = (IdentifierNode) node.getChild(0);
//        System.out.println("id node is " + node.getChild(0));
        ((ExpressionNode) node.getParent()).setIsIdentifier();
//        System.out.println("SURVIVED!");
        SymbolInfo si = idNode.getSymbolInfo();
        try {
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