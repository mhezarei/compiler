package ast;

/**
 * An enum that shows type of a var
 */
public enum PrimitiveType {
    BOOL ("i8"),
    CHAR ("i8"),
    INT ("i32"),
    LONG("i64"),
    DOUBLE ("double"),
    FLOAT ("float"),
    VOID ("void"),
    //todo
    STRING(""),
    AUTO("");

    private final String signature;

    PrimitiveType(String signature) {
        this.signature = signature;
    }
    
    public String getSignature() {
        return signature;    
    }

    @Override
    public String toString() {
        return signature;
    }}
