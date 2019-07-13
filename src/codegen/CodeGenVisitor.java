package codegen;

import ast.*;
import semantic.SymbolInfo;
import typechecker.TypeChecker;

import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintStream;

public class CodeGenVisitor implements SimpleVisitor {
    private FileWriter fw;
    private int labelIndex;
    private String className;
    private boolean returnGenerated;

    public CodeGenVisitor(FileWriter fw) {
        this.fw = fw;
    }

    @Override
    public void visit(ASTNode node) throws Exception {
        switch (node.getNodeType()) {
            case ADDITION:
                visitAdditionNode(node);
                break;

            case ASSIGN:
                visitAssignNode(node);
                break;

            case BOOLEAN_AND:
                visitBooleanAndNode(node);
                break;

            case BOOLEAN_LITERAL:
                visitBooleanLiteralNode(node);
                break;

            case BOOLEAN_NOT:
                visitBooleanNotNode(node);
                break;

            case BOOLEAN_OR:
                visitBooleanOrNode(node);
                break;

            case DIVISION:
                visitDivisionNode(node);
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

            case INTEGER_LITERAL:
                visitIntegerLiteralNode(node);
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

            case MULTIPLICATION:
                visitMultiplicationNode(node);
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

            case SUBTRACTION:
                visitSubtractionNode(node);
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

            case CHAR_LITERAL:
                visitCharLiteralNode(node);
                break;

            case CHAR_TYPE:
                visitCharTypeNode(node);
                break;

            case BOOLEAN_TYPE:
                visitBooleanTypeNode(node);
                break;

            case DOUBLE_TYPE:
                visitDoubleTypeNode(node);
                break;

            case FLOAT_LITERAL:
                visitFloatLiteralNode(node);
                break;

            case IDENTIFIER:
                visitIDNode(node);
                break;

            case INT_TYPE:
                visitIntTypeNode(node);
                break;

            case NULL_LITERAL:
                visitNullLiteralNode(node);
                break;

            case POST_DECREMENT:
                visitPostDecNode(node);
                break;

            case POST_INCREMENT:
                visitPostIncNode(node);
                break;

            case PRE_DECREMENT:
                visitPreDecNode(node);
                break;

            case PRE_INCREMENT:
                visitPreIncNode(node);
                break;

            case STRING_LITERAL:
                visitStringLiteralNode(node);
                break;

            case VOID:
                visitVoidNode(node);
                break;

            case BLOCK:
                visitBlockNode(node);
                break;

            case DECLARATIONS:

            case ARGUMENTS:

            case LOCAL_VAR_DECLARATION:

            case VARIABLE_DECLARATION:

            case EMPTY_STATEMENT:

            case EXPRESSION_STATEMENT:

            case FIELD_DECLARATION:

            default:
                visitAllChildren(node);
                break;
        }
    }


    ////////////////////  My Helpers  /////////////////////////////
    /**
     * Output the code to push this node's resulting value onto the stack
     */
    private int getVarIndexFromIDNode(ASTNode idNode) {
        return ((IdentifierNode) idNode).getSymbolInfo().getLocalVarIndex();
    }
    private int getValueFromIntNode(ASTNode intLitNode) {
        return ((IntegerLiteralNode) intLitNode).getValue();
    }


    ////////////////////////////////////////////
    ////////////////////////////////////////////
    private void visitAllChildren(ASTNode node) throws Exception {
        for (ASTNode child : node.getChildren()) {
            child.accept(this);
        }
    }

    private void visitAdditionNode(ASTNode node) throws Exception {
        // add i32 (or i64) %tmp, %z
        // fadd float (or double) %a, %b
        System.out.println("{{ADDITION}}:");
        visitAllChildren(node);

        TypeChecker tc = new TypeChecker(node.getChild(0), node.getChild(1));

        if (tc.isFloat()) {
            fw.write("fadd ");
            // recognize float type
        } else if (tc.isInteger()) {
            fw.write("add ");
            // recognize integer type
        }

        // get the values.
    }
    private void visitSubtractionNode(ASTNode node) throws Exception {
        // sub i32 (or i64) %a, %b
        // fsub float (or double) %a, %b
        System.out.println("{{SUBTRACTION}}");
        visitAllChildren(node);

    }

    private void visitDivisionNode(ASTNode node) throws Exception {
        // udiv (or sdiv) i32 (or i64) %a, %b
        // fdiv float (or double) %a, %b
        System.out.println("{{DIVISION}}");
        visitAllChildren(node);
    }

    private void visitMultiplicationNode(ASTNode node) throws Exception {
        // mul i32 (or i64) %a, %b
        // fmul float (or double) %a, %b
        System.out.println("{{MULTIPLICATION}}");
        visitAllChildren(node);
    }

    private void visitUnaryMinusNode(ASTNode node) throws Exception {
        // sub i32 (or i64) 0, %b
        // fsub float (or double) 0.0, %b
        System.out.println("{{UMINUS}}");
        visitAllChildren(node);
    }

    private void visitUnaryPlusNode(ASTNode node) throws Exception {
        System.out.println("{{UPLUS}}");
        visitAllChildren(node);
        // nothing to be done
    }


    /*Assigns thing at top of stack,
      OR if it's a literal, pushes the literal then assigns that val
      OR if it's an ID loads the ID's value and assigns*/
    private void visitAssignNode(ASTNode node) throws Exception {
        System.out.println("{{ASSIGNMENT}}");

        IdentifierNode idNode = (IdentifierNode) node.getChild(0).getChild(0);
        SymbolInfo si = idNode.getSymbolInfo();
        int lvIndex = si.getLocalVarIndex();

        /* Expression node */
        node.getChild(1).accept(this);
    }

    private void visitBooleanAndNode(ASTNode node) throws Exception {
        // sub i32 (or i64) 0, %b
        // fsub float (or double) 0.0, %b
        System.out.println("{{UMINUS}}");
        visitAllChildren(node);
    }
    private void visitBooleanNotNode(ASTNode node) throws Exception {
        // sub i32 (or i64) 0, %b
        // fsub float (or double) 0.0, %b
        System.out.println("{{UMINUS}}");
        visitAllChildren(node);
    }

    private void visitBooleanOrNode(ASTNode node) throws Exception {
        // todo "boolOr" code
        System.out.println("node is " + node);
        System.out.println("children are" + node.getChildren());
        visitAllChildren(node);
    }

    private void visitEqualNode(ASTNode node) throws Exception {
        // todo "EQ" code
        System.out.println("node is " + node);
        System.out.println("children are" + node.getChildren());
        visitAllChildren(node);
    }

    private void visitGreaterThanNode(ASTNode node) throws Exception {
        // todo "GT" code
        System.out.println("node is " + node);
        System.out.println("children are" + node.getChildren());
        visitAllChildren(node);
    }

    private void visitGreaterThanOrEqualNode(ASTNode node) throws Exception {
        // todo "GE" code
        System.out.println("node is " + node);
        System.out.println("children are" + node.getChildren());
        visitAllChildren(node);
    }

    private void visitNotEqualNode(ASTNode node) throws Exception {
        // todo "NE" code
        System.out.println("node is " + node);
        System.out.println("children are" + node.getChildren());
        visitAllChildren(node);
    }

    private void visitLessThanNode(ASTNode node) throws Exception {
        // todo "LT" code
        System.out.println("node is " + node);
        System.out.println("children are" + node.getChildren());
        visitAllChildren(node);
    }

    private void visitLessThanOrEqualNode(ASTNode node) throws Exception {
        // todo "LE" code
        System.out.println("node is " + node);
        System.out.println("children are" + node.getChildren());
        visitAllChildren(node);
    }

    private void visitIfStatementNode(ASTNode node) throws Exception {
        //todo "if" code
        System.out.println("; if statement");
        node.getChild(0).accept(this); //predicate
        System.out.println("  iconst_0");
        String endIfLabel = generateLabel();
        System.out.println("ifeq " + endIfLabel);//if predicate false, then skip the "do if true" block


        node.getChild(0).accept(this); //print the "do if true" block
        System.out.println("  goto " + endIfLabel); //bypass the "do If True" Block

        if (node.getChildren().size() == 3) //We have an else statement
        {
            String elseLabel = generateLabel();
            System.out.println(elseLabel); //The else block
            node.getChild(2).accept(this);
        }

    }

    private void visitWhileStatementNode(ASTNode node) throws Exception {
        // todo "while" code
    }

    private void visitIntegerLiteralNode(ASTNode node) {
        //todo "Integer literal" code
        int val = getValueFromIntNode(node);
        System.out.println("  ldc " + val);
    }

    private void visitBooleanLiteralNode(ASTNode node) {
        //todo "boolean literal" code
        BooleanLiteralNode boolNode = (BooleanLiteralNode) node;
        if (boolNode.getValue()) {
            System.out.println("  iconst_1");
        } else {
            System.out.println("  iconst_0");
        }
    }

    private void visitMethodAccessNode(ASTNode node) throws Exception {
        //todo
    }

    private void visitMethodDeclarationNode(ASTNode node) throws Exception {
        System.out.println("in method_dcl");
        //todo "method dec" code
        System.out.println("node is " + node);
        System.out.println("children are " + node.getChildren());
        TypeNode typeNode = (TypeNode) node.getChild(0);
        String returnType = typeNode.getType().getSignature();

        IdentifierNode idNode = (IdentifierNode) node.getChild(1);
        String methodName = idNode.getValue();

        System.out.println();
        System.out.println(";");
        System.out.println("; method");
        System.out.println(";");

        System.out.print(".method public static " + methodName + "(");
        node.getChild(1).accept(this);
        System.out.println(")" + returnType);

        System.out.println("  .limit locals 10");
        System.out.println("  .limit stack 10");

        node.getChild(3).accept(this);

        if (!returnGenerated) {
            System.out.println("  return");
            returnGenerated = true;
        }

        System.out.println(".end method");
        System.out.println("method_dcl finished\n");
    }

    private void visitParameterNode(ASTNode node) throws Exception {
        System.out.println("in param node");
        // todo "parameters" code
        TypeNode typeNode = (TypeNode) node.getChild(1);
        String typeSig = typeNode.getType().getSignature();
        System.out.print(typeSig);
        returnGenerated = false;
        System.out.println("param node finished");
    }

    private void visitReturnStatementNode(ASTNode node) throws Exception {
        // todo "return" code
        visitAllChildren(node);

        returnGenerated = true;
        if (node.getChildren().size() == 0) {
            System.out.println("  return");
        } else {
            System.out.println("  ireturn");
        }
    }

    private void visitVarUse(ASTNode node) {
        //todo need to understand
        IdentifierNode idNode = (IdentifierNode) node.getChild(0);
        SymbolInfo si = idNode.getSymbolInfo();
        int lvIndex = si.getLocalVarIndex();
        System.out.println("  iload " + lvIndex);
        returnGenerated = false;
    }

    private String generateLabel() {
        return "label" + (++labelIndex);
    }

    private void outputDefaultConstructor() {
        //todo need to understand
        System.out.println("");
        System.out.println(";");
        System.out.println("; standard constructor");
        System.out.println(";");
        System.out.println(".method public <init>()V");
        System.out.println("  aload_0");
        System.out.println("  invokenonvirtual java/lang/Object/<init>()V");
        System.out.println("  return");
        System.out.println(".end method");
        System.out.println("");
    }

    private void outputMainMethod() {
        //todo "main" code
        System.out.println("");
        System.out.println(";");
        System.out.println("; main method");
        System.out.println(";");
        System.out.println(".method public static main([Ljava/lang/String;)V");
        System.out.println("  invokestatic " + className + "/program()V");
        System.out.println("  return");
        System.out.println(".end method");
        System.out.println("");
    }

    private void outputPrintlnMethod() {
        //todo "println" code
        System.out.println("");
        System.out.println(";");
        System.out.println("; println method");
        System.out.println(";");
        System.out.println(".method public static println(I)V");
        System.out.println("  .limit stack 2");
        System.out.println("  getstatic java/lang/System/out Ljava/io/PrintStream;");
        System.out.println("  iload_0");
        System.out.println("  invokevirtual java/io/PrintStream/println(I)V");
        System.out.println("  return");
        System.out.println(".end method");
        System.out.println("");
    }

    private void visitCharLiteralNode(ASTNode node) throws Exception {
        System.out.println("node is " + node);
    }

    private void visitBooleanTypeNode(ASTNode node) throws Exception {
        System.out.println("node is " + node);
    }

    private void visitIDNode(ASTNode node) throws Exception {
        IdentifierNode idNode = (IdentifierNode) node;
        System.out.println("this ID node is " + idNode.getValue());
        System.out.println("this ID node SI is " + idNode.getSymbolInfo());
    }

    private void visitDoubleTypeNode(ASTNode node) throws Exception {
        System.out.println("node is " + node);
    }

    private void visitFloatLiteralNode(ASTNode node) throws Exception {
        System.out.println("node is " + node);
    }

    private void visitIntTypeNode(ASTNode node) throws Exception {
        System.out.println("node is " + node);
    }

    private void visitNullLiteralNode(ASTNode node) throws Exception {
        System.out.println("node is " + node);
    }

    private void visitPostDecNode(ASTNode node) throws Exception {
        System.out.println("node is " + node);
        visitAllChildren(node);
    }

    private void visitPostIncNode(ASTNode node) throws Exception {
        System.out.println("node is " + node);
        visitAllChildren(node);
    }

    private void visitPreDecNode(ASTNode node) throws Exception {
        System.out.println("node is " + node);
        visitAllChildren(node);
    }

    private void visitPreIncNode(ASTNode node) throws Exception {
        System.out.println("node is " + node);
        visitAllChildren(node);
    }

    private void visitStringLiteralNode(ASTNode node) throws Exception {
        System.out.println("node is " + node);
    }

    private void visitVoidNode(ASTNode node) throws Exception {
        System.out.println("node is " + node);
    }

    private void visitCharTypeNode(ASTNode node) throws Exception {
        System.out.println("node is " + node);
    }

    private void visitBlockNode(ASTNode node) throws Exception {
        System.out.println("node is " + node);
        System.out.println("children are " + node.getChildren());
        visitAllChildren(node);
    }
}
