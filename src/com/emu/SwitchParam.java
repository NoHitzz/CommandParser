//
// SwitchParam.java
// CommandParser
//
// Noah Hitz 2025
//

package com.emu;

public final class SwitchParam extends Parameter {
    private boolean value = false;

    public SwitchParam(String name, String shortName, boolean defaultValue, String description) {
        super(name, shortName, description);
        value = defaultValue;
    }

    public SwitchParam(String name, String shortName, boolean defaultValue) {
        super(name, shortName, "");
        value = defaultValue;
    }

    public SwitchParam(String name, boolean defaultValue) {
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

        return usage.toString();
    }

    @Override
    public String getHelp() {
        StringBuilder help = new StringBuilder();

        if(!this.getShort().isEmpty())
            help.append("-" + this.getShort() + ", ");
        help.append("--" + this.getName());

        return help.toString();
    }

    @Override
    public void parse(ArgsReader argsReader) {
        value = !value;
    }

    public boolean getValue() { return value; }

}


