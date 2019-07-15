package ast;

public class ArrayType implements Type {
    private PrimitiveType primitive;
    private String decSignature;
    private int num;
    private int align;

    public ArrayType(PrimitiveType primitive,int num) {
        this.primitive = primitive;
        this.num=num;
        decSignature="["+num+" x "+primitive.getSignature()+"]";
        align=primitive.getAlign()*(num/4);
    }

    public PrimitiveType getPrimitive() {
        return primitive;
    }

    public String getSignature() {
        return decSignature;
    }

    public int getNum() {
        return num;
    }

    public int getAlign() {
        return align;
    }

    @Override
    public String toString() {
        return decSignature;
    }
}
