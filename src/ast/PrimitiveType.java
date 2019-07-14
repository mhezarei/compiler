package ast;

/**
 * An enum that shows type of a var
 */
public enum PrimitiveType {
    BOOL ("i8", 1),
    CHAR ("i8", 1),
    INT ("i32", 4),
    LONG("i64", 8),
    DOUBLE ("double", 8),
    FLOAT ("float", 4),
    //todo
    VOID ("void", 0),
    STRING("", 0),
    AUTO("", 0);

    private final String signature;
    private final int align;

    PrimitiveType(String signature, int align) {
        this.signature = signature;
        this.align = align;
    }
    
    public String getSignature() {
        return signature;    
    }

    @Override
    public String toString() {
        return signature;
    }

    public int getAlign() {
        return align;
    }}
