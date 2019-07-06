package ast;


import semantic.SymbolInfo;
import visitor.ASTVisitor;
import visitor.SimpleVisitor;

import java.util.ArrayList;
import java.util.List;

public class BaseASTNode implements ASTNode {
    protected ArrayList<ASTNode> children = new ArrayList<>();
    protected ASTNode parent;
    protected NodeType nodeType;
    protected SymbolInfo symbolInfo;
    
    public BaseASTNode(NodeType nodeType) {
        this.nodeType = nodeType;
    }
    
    public NodeType getNodeType() {
        return nodeType;
    }
    
    public void setSymbolInfo(SymbolInfo si) {
        this.symbolInfo = si;
    }
    
    public SymbolInfo getSymbolInfo() {
        return symbolInfo;
    }

    @Override
    public String toString() {
        String str = nodeType.toString();
        
        if (symbolInfo != null) {
            str += " (" + symbolInfo.toString() + ")";
        }
        
        return str;
    }

    @Override
    public void accept(SimpleVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.previsit(this);
        
        for (ASTNode child : children) {
            child.accept(visitor);
        }
        
        visitor.postvisit(this);
    }

    @Override
    public void addChild(ASTNode node) {
        children.add(node);
    }

    @Override
    public void addChild(int index, ASTNode node) {
        children.add(index, node);
    }

    @Override
    public void addChildren(List<ASTNode> nodes) {
        children.addAll(nodes);
    }

    @Override
    public ASTNode getChild(int index) {
        return children.get(index);
    }

    @Override
    public List<ASTNode> getChildren() {
        return children;
    }

    @Override
    public ASTNode getParent() {
        return parent;
    }

    @Override
    public void setParent(ASTNode parent) {
        this.parent = parent;
    }
}
