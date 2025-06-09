//
// StrArrayParameter.java
// CommandParser
//
// Noah Hitz 2025
//

package com.emu;

import java.util.ArrayList;

public final class StrArrayParameter extends Parameter {
    private static final int MAX_DISPLAYED_META_VARS = 3;
    private ArrayList<String> value;
    private int arity;

    public StrArrayParameter(String name, String shortName, int arity, String metaVar, String description) {
        super(name, shortName, metaVar, description);
        this.value = new ArrayList<>(arity);
        this.arity = arity;
    }

    public StrArrayParameter(String name, String shortName, int arity, String metaVar) {
        super(name, shortName, metaVar);
        this.value = new ArrayList<>(arity);
        this.arity = arity;
    }

    public StrArrayParameter(String name, String shortName, String metaVar) {
        super(name, shortName, metaVar);
        this.value = new ArrayList<>();
        this.arity = Parameter.VAR_ARITY;
    }

    public StrArrayParameter(String name, String shortName, int arity) {
        super(name, shortName);
        value = new ArrayList<>();
        this.arity = arity;
    }

    public StrArrayParameter(String name, int arity) {
        super(name);
        value = new ArrayList<>();
        this.arity = arity;
    }

    public StrArrayParameter(String name) {
        super(name);
        value = new ArrayList<>();
        this.arity = Parameter.VAR_ARITY;
    }

    @Override
    public String getUsage() {
        StringBuilder usage = new StringBuilder();

        if(!this.getShort().isEmpty())
            usage.append("-" + this.getShort());
        else
            usage.append("--" + this.getName());

        usage.append(" ");
        if(arity != Parameter.VAR_ARITY && arity <= MAX_DISPLAYED_META_VARS) {
            for(int i = 0; i < arity; i++)
                usage.append(getMetaVar() + (i != arity-1 ? " " : ""));
        } else {
            usage.append(getMetaVar() + " [" + getMetaVar() + "..." + "]");
        }

        return usage.toString();
    }

    @Override
    public String getHelp() {
        StringBuilder help = new StringBuilder();

        if(!this.getShort().isEmpty())
            help.append("-" + this.getShort() + ", ");
        help.append("--" + this.getName());

        help.append(" ");
        if(arity != Parameter.VAR_ARITY && arity > 0 && arity <= MAX_DISPLAYED_META_VARS)
            help.append((getMetaVar() + " ").repeat(arity-1) + getMetaVar());
        else
            help.append(getMetaVar() + " [" + getMetaVar() + "..." + "]");

        return help.toString();
    }

    @Override
    public void parse(ArgsReader argsReader) {
        value = tokenizeArray(argsReader, arity);   // arity == Integer.Max_Value means tokenizeArray gets greedy
    }

    public ArrayList<String> getValue() { return value; }

}
