package scanner;

import java_cup.runtime.Symbol;
import parser.sym;

import java.io.DataInputStream;
import java.io.InputStreamReader;

public class Scanner implements java_cup.runtime.Scanner {
    protected Lexer scanner; // The JFlex produced scanner.
    protected Symbol nextToken; // The lookahead token.

    public Scanner(DataInputStream istream) {
        super();

        scanner = new Lexer(new InputStreamReader(istream));

        try {
            nextToken = scanner.scanFunction();
        } catch (Exception e) {
            nextToken = null;
        }
    }

    public Symbol peek() {
        return (nextToken == null) ? new Symbol(sym.EOF) : nextToken;
    }

    /**
     * Tests if the input stream has more tokens.
     *
     * @return <tt>true</tt> if the input stream has more tokens, <tt>false</tt>
     * otherwise.
     */
    public boolean hasNext() {
        return nextToken != null;
    }

    /**
     * Consumes and returns the next token.
     *
     * @return the next Token.
     */
    public Symbol next_token() {
        Symbol old = peek();
        advance();
        return old;
    }

    /**
     * Consumes and returns the next token.
     *
     * @return the next Token.
     */
    public Symbol nextToken() {
        return next_token();
    }

    /**
     * Consumes and returns the next token.
     *
     * @return the next Token.
     */
    public Symbol next() {
        return next_token();
    }

    /**
     * Consumes a token without returning the value.
     */
    public void advance() {
        if (nextToken != null) {
            try {
                nextToken = scanner.scanFunction();
            } catch (Exception e) {
                nextToken = null;
            }
        }
    }
}