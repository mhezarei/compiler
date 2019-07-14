package codegen;

import ast.PrimitiveType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class Argument {
    private PrimitiveType type;
    private String name;

    Argument(PrimitiveType type, String name) {
        this.type = type;
        this.name = name;
    }

    public PrimitiveType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Argument argument = (Argument) o;
        return Objects.equals(name, argument.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name);
    }
}

public class Signature {
    private PrimitiveType returnType;
    private String name;
    private List<Argument> args = new ArrayList<>();

    Signature(PrimitiveType returnType, String name) {
        this.returnType = returnType;
        this.name = name;
    }

    void addArgs(List<Argument> arguments) {
        args.addAll(arguments);
    }

    public PrimitiveType getReturnType() {
        return returnType;
    }

    public String getName() {
        return name;
    }

    public List<Argument> getArgs() {
        return args;
    }

    private boolean checkArguments(Signature signature) {
        Argument[] args1 = args.toArray(new Argument[0]);
        Argument[] args2 = signature.args.toArray(new Argument[0]);
        if(args1.length!=args2.length)
            return false;
        for (int i = 0; i < args1.length; i++)
            if(args1[i].getType() != args2[i].getType())
                return false;

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Signature signature = (Signature) o;

        return Objects.equals(name, signature.name) &&
                checkArguments(signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(returnType, name, args);
    }
}