package codegen;

import ast.*;

import java.io.PrintStream;
import java.util.*;

public class CodeGenVisitor implements SimpleVisitor {
    private  int tempIndex;
    private PrintStream stream;
    private int labelIndex;
    static Map<String, HashSet<Signature>> signatures = new HashMap<>();
    private SymbolTable symbolTable = new SymbolTable();
    private boolean inMethodBlock;

    public CodeGenVisitor(PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public void visit(ASTNode node) throws Exception {
        switch (node.getNodeType()) {
            case ASSIGN:
                visitAssignNode(node);
                break;

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
                visitBinaryOperation(node);
                break;

            case BOOLEAN_NOT:
            case UNARY_PLUS:
            case UNARY_MINUS:
            case BITWISE_NEGATIVE:
            case POST_DECREMENT:
            case POST_INCREMENT:
            case PRE_DECREMENT:
            case PRE_INCREMENT:
                visitUnaryOperation(node);
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
                break;

            case RETURN_STATEMENT:
                visitReturnStatementNode(node);
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

            case SIZEOF:
                visitSizeOfNode(node);
                break;

            case ADD_ASSIGN:
            case DIV_ASSIGN:
            case SUB_ASSIGN:
            case MULT_ASSIGN:
                visitOpAssNode(node);
                break;

            case ARGUMENT:
            case VARIABLE_DECLARATION:
                visitVariableDeclaration(node);
                break;

            case BLOCK:
                visitBlockNode(node);
                break;

            case IDENTIFIER:
                visitIdentifierNode(node);
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
            case BOOLEAN_TYPE:
            case CHAR_TYPE:
            case START:
            case DECLARATIONS:
            case DOUBLE_TYPE:
            case EMPTY_STATEMENT:
            case FIELD_DECLARATION:
            case NULL_LITERAL:
            case INT_TYPE:
            case LOCAL_VAR_DECLARATION:
            case VOID:
            default:
                visitAllChildren(node);
        }
    }

    private void visitOpAssNode(ASTNode node) throws Exception {
        System.out.println("####saw opass " + node.getChild(0).getChild(1).getChild(0));
        visitBinaryOperation(node.getChild(0).getChild(1).getChild(0));
        visitAssignNode(node.getChild(0));
        System.out.println("####finished op ass " + node.getChild(0).getChild(1).getChild(0));
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
        System.out.println("--THE NODE IN VAC IS " + node + " " + node.getNodeType());
        System.out.println("--THE CHILDREN ARE " + node.getChildren());
        for (ASTNode child : node.getChildren()) {
            child.accept(this);
        }
        System.out.println("--FINISHED VAC\n");
    }

    private void visitBlockNode(ASTNode node) throws Exception {
        if (!inMethodBlock) {
            symbolTable.enterScopeType();
            visitAllChildren(node);
            symbolTable.leaveScopeType();
        } else
            visitAllChildren(node);
    }

    private void visitIdentifierNode(ASTNode node) {
        IdentifierNode idNode = (IdentifierNode) node;
        String id = idNode.getValue();
        SymbolInfo si = symbolTable.get(id);
        node.setSymbolInfo(si);
    }

    private void visitVariableDeclaration(ASTNode node) throws Exception {
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
            if (node.getChild(i) instanceof IdentifierNode) {
                //DEC -> ID
                idNode = (IdentifierNode) node.getChild(i);
            } else {
                //DEC -> ASSIGN -> EXPR -> VAR_USE -> ID
                idNode = (IdentifierNode) node.getChild(i).getChild(0).getChild(0).getChild(0);

                node.getChild(i).getChild(1).accept(this);
            }
            String id = idNode.getValue();

            SymbolInfo si = new SymbolInfo(idNode);
            si.setType(typePrimitive.getType());
            idNode.setSymbolInfo(si);

            if (symbolTable.contains(id))
                throw new Exception(id + " declared before");

            symbolTable.put(id, si);
        }
        if (node.getNodeType() == NodeType.VARIABLE_DECLARATION) {
            TypeNode type = (TypeNode) node.getChild(0);
            for (int i = 1; i < node.getChildren().size(); i++) {
                IdentifierNode id;
                if ((node.getChild(i) instanceof IdentifierNode))
                    id = (IdentifierNode) node.getChild(i);
                else
                    id = (IdentifierNode) node.getChild(i).getChild(0).getChild(0).getChild(0);

                stream.println("\t%" + id.getValue() + " = alloca " + type.getType() + ", align " + alignNum());

                if (!(node.getChild(i) instanceof IdentifierNode)) {
                    ExpressionNode rightSide= (ExpressionNode) node.getChild(i).getChild(1);
                    //todo fix after cast fix
//                    PrimitiveType castType= cast(id.getSymbolInfo().getType(), rightSide);
//                    stream.println("\tstore " + castType + " " + rightSide.getResultName() + ", " + castType + "* " + id + ", align " + alignNum());
                }
            }
        }
    }

    private String alignNum() {
        //todo must set
        //need to understand
        return "4";
    }

    private void visitUnaryOperation(ASTNode node) throws Exception {
        ExpressionNode parent = (ExpressionNode) node.getParent();
        ExpressionNode e = (ExpressionNode) node.getChild(0);

        e.accept(this);

        String op = getUnaryOperationCommand(node.getNodeType(), e);
        stream.println(op);

        reduceExpressionNode(op.substring(1, op.indexOf('=') - 1), parent, e.getType());
    }

    private String getUnaryOperationCommand(NodeType nodeType, ExpressionNode e) throws Exception {
        if (!e.isIdentifier())
            throw new Exception(e + " not generated");

        PrimitiveType pt = e.getType();
        String command = "\t%" + getTemp();
        command += " = ";

        switch (nodeType) {
            case PRE_DECREMENT:
            case POST_DECREMENT:
                if (pt == PrimitiveType.INT) {
                    command += "sub i32 ";
                    command += e.getResultName();
                    command += ", 1";
                } else if (pt == PrimitiveType.CHAR) {
                    command += "sub i8";
                    command += e.getResultName();
                    command += ", 1";
                } else {
                    throw new Exception("POST/PRE DEC bad type");
                }
            case POST_INCREMENT:
            case PRE_INCREMENT:
                if (pt == PrimitiveType.INT) {
                    command += "add i32";
                    command += e.getResultName();
                    command += ", 1";
                } else if (pt == PrimitiveType.CHAR) {
                    command += "add i8";
                    command += e.getResultName();
                    command += ", 1";
                } else {
                    throw new Exception("POST/PRE INC bad type");
                }

            case BITWISE_NEGATIVE:
                if (pt == PrimitiveType.FLOAT) {
                    command += "fneg float ";
                    command += e.getResultName();
                } else if (pt == PrimitiveType.DOUBLE) {
                    command += "fneg double ";
                    command += e.getResultName();
                } else {
                    throw new Exception("bitwise neg bad type");
                }

            case UNARY_PLUS:
                break;

            case UNARY_MINUS:
                if (pt == PrimitiveType.INT) {
                    command += "sub i32";
                    command += " 0, ";
                    command += e.getResultName();
                } else if (pt == PrimitiveType.BOOL) {
                    command += "sub i1";
                    command += " 0, ";
                    command += e.getResultName();
                } else if (pt == PrimitiveType.CHAR) {
                    command += "sub i8";
                    command += " 0, ";
                    command += e.getResultName();
                } else if (pt == PrimitiveType.FLOAT) {
                    command += "fsub float";
                    command += " 0.0, ";
                    command += e.getResultName();
                } else if (pt == PrimitiveType.DOUBLE) {
                    command += "fsub double";
                    command += " 0.0, ";
                    command += e.getResultName();
                } else if (pt == PrimitiveType.LONG) {
                    command += "fsub i64";
                    command += " 0.0, ";
                    command += e.getResultName();
                } else {
                    throw new Exception("UMINUS bad type");
                }

            case BOOLEAN_NOT:
                //todo boolean not
        }


        return command;
    }

    private void reduceExpressionNode(String result, ExpressionNode parent, PrimitiveType resultType) throws Exception {
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

    private void visitBinaryOperation(ASTNode node) throws Exception {
        ExpressionNode parent = (ExpressionNode) node.getParent();
        System.out.println("node is " + node);
        System.out.println("parent is " + parent);
        System.out.println("G parent is " + parent.getParent());
        System.out.println("parent child is " + parent.getChildren());
        ExpressionNode e1 = (ExpressionNode) node.getChild(0);
//        System.out.println("first child is " + e1.getChildren());
        ExpressionNode e2 = (ExpressionNode) node.getChild(1);
//        System.out.println("second child is " + e2.getChildren());

        e1.accept(this);
        e2.accept(this);

        PrimitiveType resultType = checkBinaryOpType(e1.getType(), e2.getType(), node.getNodeType());
        // operands are casted now
        String op = getBinaryOperationCommand(node.getNodeType(), resultType);
        String result = "" + getTemp();

        stream.println("\t%" + result + " = " + op + " " + resultType + " " + e1.getResultName() + ", " + e2.getResultName());


        reduceExpressionNode(result, parent, resultType);

        /*System.out.println("the id node is " + id.toString());
        System.out.println("the id node SI is " + id.getSymbolInfo());
        System.out.println("the var use node is " + v.toString());
        System.out.println("the var use SI node is " + v.getSymbolInfo());*/
        System.out.println("finished the binary op " + node + "\n");
    }

    private String getBinaryOperationCommand(NodeType nodeType, PrimitiveType pt) throws Exception {
        String result = "";

        switch (pt) {
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
        switch (pt) {
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

    private PrimitiveType checkBinaryOpType(PrimitiveType e1, PrimitiveType e2, NodeType nodeType) throws Exception {
        //todo auto
        switch (nodeType) {
            case BOOLEAN_AND:
            case BOOLEAN_OR:
            case EQUAL:
            case NOT_EQUAL:
            case GREATER_THAN:
            case GREATER_THAN_OR_EQUAL:
            case LESS_THAN:
            case LESS_THAN_OR_EQUAL:
                switch (e1) {
                    case INT:
                    case BOOL:
                    case CHAR:
                    case LONG:
                        switch (e2) {
                            case INT:
                            case CHAR:
                            case BOOL:
                            case LONG:
                                return PrimitiveType.INT;
                            case DOUBLE:
                            case FLOAT:
                                return PrimitiveType.FLOAT;
                            case STRING:
                            case VOID:
                                throw new Exception("can't add");
                            case AUTO:
                        }
                    case FLOAT:
                    case DOUBLE:
                        switch (e2) {
                            case INT:
                            case LONG:
                            case DOUBLE:
                            case FLOAT:
                            case CHAR:
                            case BOOL:
                                return PrimitiveType.FLOAT;
                            case STRING:
                            case VOID:
                                throw new Exception("can't add");
                            case AUTO:
                        }
                    case STRING:
                    case VOID:
                        throw new Exception("can't add");
                    case AUTO:
                }

            case ADDITION:
            case SUBTRACTION:
            case MULTIPLICATION:
            case MOD:
            case DIVISION:
                switch (e1) {
                    case INT:
                    case BOOL:
                        switch (e2) {
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
                        }
                    case CHAR:
                        switch (e2) {
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
                        }
                    case LONG:
                        switch (e2) {
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
                        }
                    case FLOAT:
                        switch (e2) {
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
                        }
                    case DOUBLE:
                        switch (e2) {
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
                        }
                    case STRING:
                    case VOID:
                        throw new Exception("can't add");
                    case AUTO:
                }
                break;

            case ARITHMETIC_AND:
            case ARITHMETIC_OR:
            case XOR:
                if ((e1 == PrimitiveType.INT || e1 == PrimitiveType.LONG) &&
                        (e2 == PrimitiveType.INT || e2 == PrimitiveType.LONG)) {
                    if (e1 == PrimitiveType.INT && e2 == PrimitiveType.INT) {
                        return PrimitiveType.INT;
                    } else {
                        return PrimitiveType.LONG;
                    }
                } else {
                    throw new Exception("can't add");
                }
        }
        throw new Exception("general can't do " + nodeType);
    }

    private  int getTemp() {
        return tempIndex++;
    }

     ExpressionNode cast(PrimitiveType type1, ExpressionNode e2) throws Exception {
        PrimitiveType type2=e2.getType();
        PrimitiveType type=null;
        if (type1 == type2)
            return e2;

        switch (type1) {
            case INT:
                switch (type2) {
                    case FLOAT:
                    case DOUBLE:
                    case BOOL:
                        type= type2;
                        break;
                    case CHAR:
                        type= type1;
                    case AUTO:
                    case VOID:
                    case STRING:
                    case LONG:
                        throw new Exception("can't cast");
                }
                break;
            case DOUBLE:
            case FLOAT:
            case BOOL:
            case VOID:
                type= type1;
                break;
            case CHAR:
                if (type2 == PrimitiveType.INT) {
                    type = type2;
                    break;
                }
            case STRING:
            case AUTO:
            case LONG:
                throw new Exception("can't cast");
        }

        String result = ""+getTemp();

//        stream.print("\t%"+result+" = sitofp "+);

        ExpressionNode parent=new ExpressionNode();

        reduceExpressionNode(result,parent, type);

        return parent;
    }

    private void visitSizeOfNode(ASTNode node) throws Exception {
        ExpressionNode parent = (ExpressionNode) node.getParent();
        PrimitiveType type = ((TypeNode) node.getChild(0)).getType();

        node.getChild(0).accept(this);

        int val;

        switch (type) {
            case BOOL:
            case CHAR:
            case VOID:
                val = 1;
                break;
            case INT:
            case FLOAT:
                val = 4;
                break;
            case LONG:
            case DOUBLE:
                val = 8;
                break;
            case STRING:
            case AUTO:
                throw new Exception("bad types for sizeof");
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        //reduce
        IntegerLiteralNode iln = new IntegerLiteralNode(val);
        iln.setParent(parent);
        parent.setChildren(iln);
    }

    private void visitIfStatementNode(ASTNode node) throws Exception {
        // BIG TODO NEED TO FIX SCOPE PROBLEMS
        // BIG TODO NEED TO FIX SCOPE PROBLEMS
        // BIG TODO NEED TO FIX SCOPE PROBLEMS
        String ifType = (node.getChildren().size() == 2) ? "if" : ((node.getChildren().size() == 3) ? "if_else" : "invalid");

        visitBinaryOperation(node.getChild(0).getChild(0));

        String result = ((IdentifierNode) node.getChild(0).getChild(0).getChild(0)).getValue();
        String ifTrueLabel = "%" + generateLabel();
        String ifFalseLabel = "%" + generateLabel();

        stream.println("\tbr i1 %" + result + ", label " + ifTrueLabel + ", label " + ifFalseLabel);
        stream.println(ifTrueLabel.substring(1) + ":");

        visitBlockNode(node.getChild(1));

        stream.println(ifFalseLabel.substring(1) + ":");

        if (ifType.equals("if_else")) {
            visitBlockNode(node.getChild(2));
        }

        // BIG TODO NEED TO FIX SCOPE PROBLEMS
        // BIG TODO NEED TO FIX SCOPE PROBLEMS
        // BIG TODO NEED TO FIX SCOPE PROBLEMS
    }

    /*Assigns thing at top of stack,
      OR if it's a literal, pushes the literal then assigns that val
      OR if it's an ID loads the ID's value and assigns*/
    private void visitAssignNode(ASTNode node) throws Exception {
        //node -> EXPRESSION -> VAR_USE -> ID
        IdentifierNode idNode = (IdentifierNode) node.getChild(0).getChild(0).getChild(0);

        visitAllChildren(node);

        ExpressionNode leftSide = (ExpressionNode) node.getChild(0);
        ExpressionNode rightSide = (ExpressionNode) node.getChild(1);

        stream.println("\tstore " + rightSide.getType() + " " + rightSide.getResultName() + ", " + leftSide.getType() + "* " + idNode + ", align " + alignNum());

        System.out.println("assign children are " + node.getChildren());


        SymbolInfo si = idNode.getSymbolInfo();

        if (si == null)
            throw new Exception(idNode.getValue() + " not declared");

        /* Expression node */
        node.getChild(1).accept(this);

        ASTNode exprNode = node.getChild(1).getChild(0);

        if (exprNode.getNodeType() == NodeType.VAR_USE)
            exprNode = exprNode.getChild(0);

        stream.println("\t" + idNode + " = " + exprNode);
    }

    /*private void visitClassNode(ASTNode node) throws Exception {
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

    }*/

    private void visitWhileStatementNode(ASTNode node) {

    }

    private void visitLiteralNode(ASTNode node) throws Exception {
        ((ExpressionNode) node.getParent()).setIsIdentifier();
    }

    private void visitMethodAccessNode(ASTNode node) throws Exception {
        //Check the signature
        IdentifierNode idNode = (IdentifierNode) node.getChild(0);
        String methodName = idNode.getValue();

        //if there is not a method with this name
        if (!signatures.containsKey(methodName))
            throw new Exception(methodName + "() not declared");

        PrimitiveType leftHandType;

        if (node.getParent().getParent().getNodeType() == NodeType.ASSIGN) {
            //it is an assign
            ExpressionNode leftHandExpr = (ExpressionNode) node.getParent().getParent().getChild(0);
            leftHandExpr.accept(this);

            leftHandType = leftHandExpr.getType();
        } else
            leftHandType = PrimitiveType.VOID;
        Optional<Signature> aSigFromSet = signatures.get(methodName).stream().findAny();
        PrimitiveType returnType;
        if (aSigFromSet.isPresent())
            returnType = aSigFromSet.get().getReturnType();
        else
            //if there is not a method with this name
            throw new Exception(methodName + "() not declared");

        Set<Signature> signatureSet = signatures.get(methodName);

        Signature newSig = new Signature(returnType, methodName);
        List<Argument> argumentList = new ArrayList<>();

        List<ASTNode> parameters = node.getChild(1).getChildren();
        for (ASTNode child : parameters)
            child.getChild(0).accept(this);

        //all of parameters accepted, now we can count them
        for (ASTNode parameter : parameters) {
            ExpressionNode expr = (ExpressionNode) parameter.getChild(0);
            argumentList.add(new Argument(expr.getType(), ""));
        }
        newSig.addArgs(argumentList);

        //if parameters are not match
        if (!signatureSet.contains(newSig))
            throw new Exception(methodName + "() with this signature not declared");

        //now get parameter list of the declared signature
        for (Signature signature : signatureSet)
            if (signature.checkArguments(newSig)) {
                argumentList = signature.getArgs();
                break;
            }


        boolean isExpr=false;
        ExpressionNode parent=null;
        stream.print("\t");
        if (node.getParent().getNodeType() == NodeType.EXPRESSION_STATEMENT) {
            //it is an expression
            String result = "" + getTemp();

            parent = (ExpressionNode) node.getParent();
            reduceExpressionNode(result, parent, returnType);

            stream.print("%" + result + " = ");
            isExpr=true;
        }
        //print call instruction
        stream.print("call " + returnType + " @" + methodName);
        visitParameterNode(node.getChild(1), argumentList);

        if(isExpr)
            cast(leftHandType, parent);
    }

    private void visitParameterNode(ASTNode node, List<Argument> argumentList) {
        stream.print("(");
        ASTNode[] params = node.getChildren().toArray(new ASTNode[0]);
        Argument[] arguments = argumentList.toArray(new Argument[0]);
        for (int i = 0; i < params.length; i++) {
            if (i > 0)
                stream.print(",");
            ASTNode paramNode = params[i];
            ExpressionNode expr = (ExpressionNode) paramNode.getChild(0);
            stream.print(arguments[i].getType() + " ");
            ASTNode paramValue = expr.getChild(0);
            if (paramValue.getNodeType() == NodeType.VAR_USE)
                //this an id
                paramValue = paramValue.getChild(0);
            stream.print(paramValue);
        }
        stream.println(")");
    }


    private void visitMethodDeclarationNode(ASTNode node) throws Exception {
        //type
        TypeNode returnType = (TypeNode) node.getChild(0);
        String returnSig = returnType.getType().getSignature();
        //identifier
        IdentifierNode idNode = (IdentifierNode) node.getChild(1);
        String methodName = idNode.getValue();

        stream.print("define ");

        stream.print(returnSig + " @" + methodName);

        symbolTable.enterScopeType();
        inMethodBlock = true;
        visitArgumentNode(node.getChild(2));

        stream.println(" {");
        stream.println("entry:");
        //block
        node.getChild(3).accept(this);
        symbolTable.leaveScopeType();
        stream.println("}");
    }

    private void visitArgumentNode(ASTNode node) throws Exception {
        //set symInfos
        visitAllChildren(node);

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
        }
        stream.print(")");
    }

    private void visitReturnStatementNode(ASTNode node) throws Exception {
        visitAllChildren(node);

        //return type in signature
        ASTNode n = node;
        while (n.getNodeType() != NodeType.METHOD_DECLARATION) {
            n = n.getParent();
        }
        PrimitiveType returnType = ((TypeNode) n.getChild(0)).getType();

        stream.print("\tret " + returnType);

        if (node.getChildren().isEmpty()) {
            //return; must be void
            if (returnType != PrimitiveType.VOID)
                throw new Exception("return type is wrong");
        } else {
            if (returnType == PrimitiveType.VOID)
                throw new Exception("return type is wrong");
            ExpressionNode returnExpr = ((ExpressionNode) node.getChild(0));
            ExpressionNode newExpr=new ExpressionNode();
            try {
                cast(returnType, returnExpr);
                stream.print(" " + returnExpr.getResultName());
            } catch (Exception e) {
                throw new Exception("return type is wrong");
            }
        }

        stream.println();
    }

    private void visitVarUse(ASTNode node) throws Exception {
        visitAllChildren(node);

        IdentifierNode id = (IdentifierNode) node.getChild(0);

        String result = "" + getTemp();

        stream.println("\t%" + result + " = load "+id.getSymbolInfo().getType()+", " + id.getSymbolInfo().getType() + "* " + id + ", align " + alignNum());

        //this var is not in this scope
        if (symbolTable.get(id.getValue()) == null)
            throw new Exception(id.getValue() + " is not in this scope");

        ((ExpressionNode) node.getParent()).setIsIdentifier();

        reduceExpressionNode(result, (ExpressionNode) node.getParent(), id.getSymbolInfo().getType());
    }

    private String generateLabel() {
        return "label" + (++labelIndex);
    }

    private void outputMainMethod() {
        //todo "main" code
        stream.println("");
        stream.println(";");
        stream.println("; main method");
        stream.println(";");
        stream.println(".method public static main([Ljava/lang/String;)V");
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