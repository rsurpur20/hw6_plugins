package edu.cmu.cs.cs214.analyzer.framework.gui;

import edu.cmu.cs.cs214.analyzer.framework.core.DataPlugin;

public class Plugin {
    private final DataPlugin plugin;

    public Plugin(DataPlugin plugin) {
        this.plugin = plugin;
    }

    public DataPlugin getPlugin() {
        return plugin;
    }

    @Override
    public String toString() {
        return "{ \"name\": \"" + this.plugin.getName() + "\" }";
    }
}
