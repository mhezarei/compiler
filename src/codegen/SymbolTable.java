package codegen;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple symbol table implementation.
 */
class SymbolTable implements Symbol {
    private ArrayList<HashMap<String, Symbol>> scopes = new ArrayList<>();
    private HashMap<String, Symbol> currentScope = new HashMap<>();

    void enterScopeType(String id) {
        if (currentScope != null) {
            scopes.add(currentScope);
            SymbolTable newScope = new SymbolTable();
            currentScope.put(id, newScope);
            currentScope = newScope.currentScope;
        } else
            currentScope = new HashMap<>();
    }

    void leaveScopeType(String id) {
        scopes.remove(currentScope);

        currentScope = scopes.get(scopes.size()-1);

        scopes.remove(currentScope);

        currentScope.remove(id);
    }

    void put(String id, SymbolInfo si) throws Exception {
        if (currentScope.containsKey(id)) {
            throw new Exception("current scope already contains an entry for " + id);
        }

        currentScope.put(id, si);
    }

    Symbol get(String id) {
        for (HashMap<String, Symbol> scope : scopes) {
            if(scope.get(id)!=null)
                return scope.get(id);
        }
        return currentScope.get(id);
    }

    boolean getSI(String id) {
        for (HashMap<String, Symbol> scope : scopes) {
            if(scope.get(id)!=null)
                return ((SymbolInfo) scope.get(id)).isConst();
        }
        return ((SymbolInfo) currentScope.get(id)).isConst();
    }

    boolean contains(String id) {
        for (HashMap<String, Symbol> scope : scopes)
            if (scope.containsKey(id))
                return true;
        return false;
    }
}