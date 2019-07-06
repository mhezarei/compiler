package ast;

import java.util.HashMap;

public class ClassNode extends BaseASTNode {
    private HashMap<String, String> methodMap = new HashMap<String, String>();

    public ClassNode() {
        super(NodeType.CLASS);
        methodMap.put("println", "println(I)V");
    }
    
    public void putMethodSig(String name, String sig) {
        methodMap.put(name, sig);
    }
    
    public String getMethodSig(String name) {
        return methodMap.get(name);
    }
}
