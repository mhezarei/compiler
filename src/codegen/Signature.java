package codegen;

import ast.TypeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Signature {
    static class Argument{
        private TypeNode type;
        private String name;

        public Argument(TypeNode type, String name) {
            this.type = type;
            this.name = name;
        }

        public TypeNode getType() {
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

    private TypeNode returnType;
    private String name;
    private List<Argument> args=new ArrayList<>();

    Signature(TypeNode returnType, String name) {
        this.returnType = returnType;
        this.name = name;
    }

    public void addArgs(List<Argument> arguments) {
        args.addAll(arguments);
    }

    public TypeNode getReturnType() {
        return returnType;
    }

    public String getName() {
        return name;
    }

    public List<Argument> getArgs() {
        return args;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Signature signature = (Signature) o;
        boolean argTypeCheck=true;
        Argument[] args1=args.toArray(new Argument[0]);
        Argument[] args2=signature.args.toArray(new Argument[0]);
        for (int i = 0; i < args1.length; i++)
            argTypeCheck = args1[i] == args2[i];
        return returnType == signature.returnType &&
                Objects.equals(name, signature.name) &&
                argTypeCheck;
    }

    @Override
    public int hashCode() {
        return Objects.hash(returnType, name, args);
    }
}