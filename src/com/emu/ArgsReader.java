//
// ArgsReader.java
// CommandParser
//
// Noah Hitz 2025
//

package com.emu;

/**
 * Internal helper class used to parse arguments.
 * @see CommandParser
 */
class ArgsReader {
    private final String[] args;
    private int index = 0;
    private String currentToken = null;
    private String lastOption = null;

    public ArgsReader(String[] args) {
        this.args = args;
    }

    public boolean hasNext() { return index < args.length; }
    public String current() { return currentToken; }
    public String peek() { return hasNext() ? args[index] : null; }
    public String next() { return currentToken = hasNext() ? args[index++] : null; }
    public int getIndex() { return index; }
    public void setIndex(int idx) { index = idx; }
    public void reset() { index = -1; }

    public void setLastOption(String option) { lastOption = option; }
    public String getLastOption() { return lastOption; }

    public static String extractOption(String token) {
        if(token.contains("="))
            return token.substring(0, token.indexOf("="));

        return token;
    }
}

