package semantic;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple symbol table implementation.
 */
class SymbolTable {
    private ArrayList<HashMap<String, SymbolInfo>> scopes = new ArrayList<>();
    private HashMap<String, SymbolInfo> currentScope = new HashMap<>();
    
    void enterScope() {
        scopes.add(currentScope);
        currentScope = new HashMap<>();
    }
    
    void leaveScope() {
        int index = scopes.size() - 1;
        currentScope = scopes.remove(index);
    }
    
    void put(String id, SymbolInfo si) throws SemanticException {
        if (currentScope.containsKey(id)) {
            throw new SemanticException("current scope already contains an entry for " + id);
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
}
