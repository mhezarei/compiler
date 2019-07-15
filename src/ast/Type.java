package ast;

public interface Type {
    String getSignature();
    int getAlign();
    PrimitiveType getPrimitive();
}