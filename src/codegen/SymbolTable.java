package codegen;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple symbol table implementation.
 */
class SymbolTable {
    private ArrayList<HashMap<String, SymbolInfo>> scopes = new ArrayList<>();
    private HashMap<String, SymbolInfo> currentScope = new HashMap<>();

    void enterScopeType() {
        currentScope = new HashMap<>();
    }

    void leaveScopeType() {
        scopes.add(currentScope);
    }

    void put(String id, SymbolInfo si) throws Exception {
        if (currentScope.containsKey(id)) {
            throw new Exception("current scope already contains an entry for " + id);
        }

        currentScope.put(id, si);
    }

    SymbolInfo get(String id) {
        return currentScope.get(id);
    }

    boolean contains(String id) {
        for (HashMap<String, SymbolInfo> scope : scopes)
            if (scope.containsKey(id))
                return true;
        return false;
    }
}