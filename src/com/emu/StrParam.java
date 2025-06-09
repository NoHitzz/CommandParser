//
// StrParam.java
// CommandParser
//
// Noah Hitz 2025
//

package com.emu;

public final class StrParam extends Parameter {
    private String value;

    public StrParam(String name, String shortName, String defaultValue, String metaVar, String description) {
        super(name, shortName, metaVar, description);
        value = defaultValue;
    }

    public StrParam(String name, String shortName, String defaultValue, String metaVar) {
        super(name, shortName, metaVar);
        value = defaultValue;
    }

    public StrParam(String name, String shortName, String defaultValue) {
        super(name, shortName);
        value = defaultValue;
    }

    public StrParam(String name, String defaultValue) {
        super(name);
        value = defaultValue;
    }

    @Override
    public String getUsage() {
        StringBuilder usage = new StringBuilder();

        if(!this.getShort().isEmpty())
            usage.append("-" + this.getShort());
        else
            usage.append("--" + this.getName());

        usage.append(" ");
        usage.append(getMetaVar());

        return usage.toString();
    }

    @Override
    public String getHelp() {
        StringBuilder help = new StringBuilder();

        if(!this.getShort().isEmpty())
            help.append("-" + this.getShort() + ", ");
        help.append("--" + this.getName());

        help.append(" ");
        help.append(getMetaVar());

        return help.toString();
    }

    @Override
    public void parse(ArgsReader argsReader) {
        value = tokenize(argsReader);
    }

    public String getValue() { return value; }

}
