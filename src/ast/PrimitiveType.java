package ast;

/**
 * An enum that shows type of a var
 */
public enum PrimitiveType {
    BOOL ("Z"),
    CHAR ("C"),
    DOUBLE ("D"),
    INT ("I"),
    VOID ("V"),
    LONG(""),
    STRING(""),
    AUTO(""),
    FLOAT (" ");

    /*todo need to change to llvm syntax*/
    private final String signature;
    
    PrimitiveType(String signature) {
        this.signature = signature;
    }
    
    public String getSignature() {
        return signature;    
    }
}
