package codegen;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple symbol table implementation.
 */
class SymbolTable {
    private ArrayList<HashMap<String, SymbolInfo>> scopes = new ArrayList<>();
    private HashMap<String, SymbolInfo> currentScope = new HashMap<>();

    void enterScope() {
        currentScope = new HashMap<>();
    }

    void leaveScope() {
        scopes.add(currentScope);
    }

    void put(String id, SymbolInfo si) throws Exception {
        if (currentScope.containsKey(id)) {
            throw new Exception("current scope already contains an entry for " + id);
        }

        currentScope.put(id, si);
    }

    SymbolInfo get(String id) {
        SymbolInfo si = currentScope.get(id);

        if (si != null) {
            return si;
        }

        for (int i = scopes.size() - 1; i >= 0; --i) {
            HashMap<String, SymbolInfo> scope = scopes.get(i);
            si = scope.get(id);
            if (si != null) {
                return si;
            }
        }

        return null;
    }

    boolean contains(String id) {
        for (HashMap<String, SymbolInfo> scope : scopes)
            if (scope.containsKey(id))
                return true;
        return false;
    }
}