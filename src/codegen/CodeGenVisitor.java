package codegen;

import ast.*;

import java.io.PrintStream;
import java.util.*;

public class CodeGenVisitor implements SimpleVisitor {
    private int tempIndex;
    private PrintStream stream;
    private int labelIndex;
    static Map<String, HashSet<Signature>> signatures = new HashMap<>();
    private SymbolTable symbolTable = new SymbolTable();
    private int blockIndex;

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

			case FOR_STATEMENT:
			case FOREACH_STATEMENT:
			case REPEAT_STATEMENT:
				visitLoopNode(node);
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

			case SWITCH_STATEMENT:
				visitSwitchNode(node);
				break;

			case STRUCT_DECLARATION:
			case CONTINUE_STATEMENT:
			case BREAK_STATEMENT:
			case STRING_TYPE:
			case FLOAT_TYPE:
			case LONG_TYPE:
			case AUTO_TYPE:
			case CASE_STATEMENT:
			case PARAMETERS:
			case VARIABLE_CONST_DECLARATION:
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

    private void visitSwitchNode(ASTNode node) throws Exception {
        // BIG TODO SCOPE
        String defaultLabel = generateLabel();
        String[] labels = new String[node.getChild(1).getChildren().size()];

		node.getChild(0).accept(this);

		String result = ((IdentifierNode) node.getChild(0).getChild(0).getChild(0)).getValue();

		PrimitiveType type = (node.getChild(0).getChild(0).getChild(0)).getSymbolInfo().getType();

		stream.println("\tswitch " + type + " %" + result + ", label %" + defaultLabel + " [");

		int i = 0;
		for (ASTNode block : node.getChild(1).getChildren()) {
			labels[i] = generateLabel();
			stream.println("\t\ti32 " + ((IntegerLiteralNode) block.getChild(1)).getValue() + ", label %" + labels[i]);
			i++;
		}

		stream.println("\t]");

		i = 0;
		for (ASTNode block : node.getChild(1).getChildren()) {
			stream.println(labels[i] + ":");
			block.getChild(0).accept(this);
			i++;
		}

		stream.println(defaultLabel + ":");
		node.getChild(2).accept(this);
	}

	private void visitForEachNode(ASTNode node) {

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
		for (ASTNode child : node.getChildren()) {
			child.accept(this);
		}
	}

    private void visitBlockNode(ASTNode node) throws Exception {
        if (node.getParent().getNodeType() != NodeType.METHOD_DECLARATION) {
            symbolTable.enterScopeType(getBlock());
            visitAllChildren(node);
            symbolTable.leaveScopeType(blockIndex-1+"");
        } else
            visitAllChildren(node);
    }

    private String getBlock() {
        return "" + blockIndex++;
    }

    private void visitIdentifierNode(ASTNode node) {
        IdentifierNode idNode = (IdentifierNode) node;
        String id = idNode.getValue();
        SymbolInfo si = (SymbolInfo) symbolTable.get(id);
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

				stream.println("\t%" + id.getValue() + " = alloca " + type.getType() + ", align " + type.getType().getAlign());

				if (!(node.getChild(i) instanceof IdentifierNode)) {
					ExpressionNode rightSide = (ExpressionNode) node.getChild(i).getChild(1);
					ExpressionNode cast = cast(id.getSymbolInfo().getType(), rightSide);
					stream.println("\tstore " + cast.getType() + " " + cast.getResultName() + ", " + cast.getType() + "* " + id + ", align " + type.getType().getAlign());
				}
			}
		}
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
		String op = getBinaryOperationCommand(e1.getType(), e2.getType(), node.getNodeType(), resultType);

		if (op.startsWith("f")) {
			if (!(e1.getType() == PrimitiveType.FLOAT || e1.getType() == PrimitiveType.DOUBLE)) {
				e1 = cast(e2.getType(), e1);
				e1.accept(this);
			} else if (!(e2.getType() == PrimitiveType.FLOAT || e2.getType() == PrimitiveType.DOUBLE)) {
				e2 = cast(e1.getType(), e2);
				e2.accept(this);
			}
		}

		String result = "" + getTemp();

		// only for icmp
		if (op.contains("cmp")) {
			if (e1.getType() != e2.getType()) {
				if (e1.getType() == PrimitiveType.LONG || e2.getType() == PrimitiveType.LONG) {
					throw new Exception("can't cmp long");
				} else {
					switch (e1.getType()) {
						case INT:
							switch (e2.getType()) {
								case CHAR:
								case BOOL:
									e2 = cast(PrimitiveType.INT, e2);
									e2.accept(this);
									break;
							}
							break;
						case BOOL:
							switch (e2.getType()) {
								case INT:
									e1 = cast(PrimitiveType.INT, e1);
									break;
								case CHAR:
									e1 = cast(PrimitiveType.CHAR, e1);
							}
							e1.accept(this);
							break;
						case CHAR:
							switch (e2.getType()) {
								case INT:
									e1 = cast(PrimitiveType.INT, e1);
									e1.accept(this);
									break;
								case BOOL:
									e2 = cast(PrimitiveType.CHAR, e2);
									e2.accept(this);
									break;
							}
							break;
					}
				}
			}
			stream.println("\t%" + result + " = " + op + " " + e1.getType() + " " + e1.getResultName() + ", " + e2.getResultName());
		} else {
			stream.println("\t%" + result + " = " + op + " " + resultType + " " + e1.getResultName() + ", " + e2.getResultName());
		}

		reduceExpressionNode(result, parent, resultType);

		System.out.println("finished the binary op " + node + "\n");
	}

	private String getBinaryOperationCommand(PrimitiveType e1Type, PrimitiveType e2Type, NodeType nodeType, PrimitiveType resultType) throws Exception {
		String result = "";

		switch (resultType) {
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
					if (e1Type == PrimitiveType.FLOAT || e1Type == PrimitiveType.DOUBLE
							|| e2Type == PrimitiveType.FLOAT || e2Type == PrimitiveType.DOUBLE) {
						result = "f";
					} else {
						result = "i";
					}
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
		switch (resultType) {
			case FLOAT:
			case DOUBLE:
				result = result + "o";
				break;
			case LONG:
			case CHAR:
			case INT:
			case BOOL:
				if (nodeType != NodeType.EQUAL && nodeType != NodeType.NOT_EQUAL) {
					if (e1Type == PrimitiveType.FLOAT || e1Type == PrimitiveType.DOUBLE
							|| e2Type == PrimitiveType.FLOAT || e2Type == PrimitiveType.DOUBLE) {
						result += "o";
					} else {
						result += "s";
					}
				}
		} // has o for float and double, s for {le, ge, lt, gt} and nothing for integer eq and ne

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
		return tempIndex++;
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
					case FLOAT:
					case DOUBLE:
						switch (e2) {
							case INT:
							case LONG:
							case DOUBLE:
							case FLOAT:
							case CHAR:
							case BOOL:
								return PrimitiveType.BOOL;
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

	ExpressionNode cast(PrimitiveType resultType, ExpressionNode e2) throws Exception {
		System.out.println("IN CAST to " + resultType + " from " + e2.getType());
		PrimitiveType type2 = e2.getType();
		String op = null;

		if (resultType == type2)
			return e2;

		switch (resultType) {
			case INT:
				switch (type2) {
					case FLOAT:
					case DOUBLE:
						op = "fptosi";
						break;
					case BOOL:
						op = "zext";
						break;
					case CHAR:
						op = "sext";
						break;
					case LONG:
					case AUTO:
					case VOID:
					case STRING:
						throw new Exception("can't cast");
				}
				break;
			case DOUBLE:
				switch (type2) {
					case INT:
					case BOOL:
						op = "sitofp";
						break;
					case FLOAT:
						op = "fpext";
						break;
					case CHAR:
					case LONG:
					case AUTO:
					case VOID:
					case STRING:
						throw new Exception("can't cast");
				}
				break;
			case FLOAT:
				switch (type2) {
					case INT:
					case BOOL:
						op = "sitofp";
						break;
					case DOUBLE:
						op = "fptrunc";
						break;
					case CHAR:
					case LONG:
					case AUTO:
					case VOID:
					case STRING:
						throw new Exception("can't cast");
				}
				break;

			case CHAR:
				switch (type2) {
					case INT:
					case BOOL:
						op = "trunc";
						break;
					case DOUBLE:
					case FLOAT:
					case LONG:
					case AUTO:
					case VOID:
					case STRING:
						throw new Exception("can't cast");
				}
				break;
			case LONG:
				if (type2 == PrimitiveType.BOOL) {
					op = "zext";
					break;
				}
			case STRING:
			case AUTO:
			case BOOL:
			case VOID:
				throw new Exception("can't cast");
		}

		String result = "" + getTemp();

		stream.println("\t%" + result + " = " + op + " " + type2 + " " + e2.getResultName() + " to " + resultType);

		ExpressionNode parent = new ExpressionNode();

		reduceExpressionNode(result, parent, resultType);

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

	private void visitAssignNode(ASTNode node) throws Exception {
		// node -> EXPRESSION -> VAR_USE -> ID
		IdentifierNode idNode = (IdentifierNode) node.getChild(0).getChild(0).getChild(0);

		visitAllChildren(node);

		ExpressionNode leftSide = (ExpressionNode) node.getChild(0);
		ExpressionNode rightSide = (ExpressionNode) node.getChild(1);

		ExpressionNode resultOfCast = cast(leftSide.getType(), rightSide);

		stream.println("\tstore " + resultOfCast.getType() + " " + rightSide.getResultName() + ", " + resultOfCast.getType() + "* %" + idNode.getValue() + ", align " + resultOfCast.getType().getAlign());

		System.out.println("assign children are " + node.getChildren());


		SymbolInfo si = idNode.getSymbolInfo();

		if (si == null)
			throw new Exception(idNode.getValue() + " not declared");

		/* Expression node */
		node.getChild(1).accept(this);
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

	private void visitLoopNode(ASTNode node) throws Exception {
		// todo FOREACH
		if (node.getNodeType() == NodeType.FOR_STATEMENT) {
			visitForNode(node);
		} else if (node.getNodeType() == NodeType.REPEAT_STATEMENT) {
			visitRepeatNode(node);
		}
	}

	private void visitRepeatNode(ASTNode node) throws Exception {
		// BIG TODO SCOPE
		String loopLabel = generateLabel();
		String outLabel = generateLabel();

		stream.println(loopLabel + ":");
		node.getChild(0).accept(this);

		node.getChild(1).accept(this);
		String result = ((IdentifierNode) node.getChild(1).getChild(0).getChild(0)).getValue();
		stream.println("\tbr i1 %" + result + ", label %" + loopLabel + ", label %" + outLabel);

		stream.println(outLabel + ":");
	}

    private void visitIfStatementNode(ASTNode node) throws Exception {
        String ifType;
        if (node.getChildren().size() == 2) ifType = "if";
        else ifType = (node.getChildren().size() == 3) ? "if_else" : "invalid";

		node.getChild(0).getChild(0).accept(this);

		String result = ((IdentifierNode) node.getChild(0).getChild(0).getChild(0)).getValue();
		String ifTrueLabel = "%" + generateLabel();
		String ifFalseLabel = "%" + generateLabel();

		stream.println("\tbr i1 %" + result + ", label " + ifTrueLabel + ", label " + ifFalseLabel);
		stream.println(ifTrueLabel.substring(1) + ":");

		node.getChild(1).accept(this);

		stream.println(ifFalseLabel.substring(1) + ":");

		if (ifType.equals("if_else")) {
			node.getChild(2).accept(this);
		}
	}

    private void visitForNode(ASTNode node) throws Exception {
        String loopLabel = generateLabel();
        String mainLoopLabel = generateLabel();
        String outLabel = generateLabel();

		// child[0] is first assignment
		node.getChild(0).accept(this);

		// putting the label for the loop (loop label)
		stream.println(loopLabel + ":");

		// child[1] is the expr to be checked every time which is the br command
		node.getChild(1).accept(this);
		String result = ((IdentifierNode) node.getChild(1).getChild(0).getChild(0)).getValue();
		stream.println("\tbr i1 %" + result + ", label %" + mainLoopLabel + ", label %" + outLabel);

		// main loop label
		stream.println(mainLoopLabel + ":");

		// child[3] is the block
		node.getChild(3).accept(this);

		// child[2] is the assignment OR the expr to be done after block
		node.getChild(2).accept(this);

		// the unconditional branch to loop
		stream.println("\tbr label %" + loopLabel);

		// out of loop
		stream.println(outLabel + ":");
	}

	private String generateLabel() {
		return "label" + (++labelIndex);
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


		boolean isExpr = false;
		ExpressionNode parent = null;
		stream.print("\t");
		if (node.getParent().getNodeType() == NodeType.EXPRESSION_STATEMENT) {
			//it is an expression
			String result = "" + getTemp();

			parent = (ExpressionNode) node.getParent();
			reduceExpressionNode(result, parent, returnType);

			stream.print("%" + result + " = ");
			isExpr = true;
		}
		//print call instruction
		stream.print("call " + returnType + " @" + methodName);
		visitParameterNode(node.getChild(1), argumentList);

		if (isExpr)
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

        visitArgumentNode(node.getChild(2));

        stream.println(" {");
        stream.println("entry:");
        //block
        node.getChild(3).accept(this);
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
        if(((IdentifierNode) node.getChild(0)).getValue().equals("b"))
            System.out.println("***");
        visitAllChildren(node);

		IdentifierNode id = (IdentifierNode) node.getChild(0);

		String result = "" + getTemp();

        if (id.getSymbolInfo() == null)
            throw new Exception(id.getValue() + " not declared");

        stream.println("\t%" + result + " = load " + id.getSymbolInfo().getType() + ", " + id.getSymbolInfo().getType() + "* " + id + ", align " + id.getSymbolInfo().getType().getAlign());

		//this var is not in this scope
		if (symbolTable.get(id.getValue()) == null)
			throw new Exception(id.getValue() + " is not in this scope");

		((ExpressionNode) node.getParent()).setIsIdentifier();

		reduceExpressionNode(result, (ExpressionNode) node.getParent(), id.getSymbolInfo().getType());
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