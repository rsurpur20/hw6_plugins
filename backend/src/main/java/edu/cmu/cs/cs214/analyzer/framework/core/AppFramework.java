package edu.cmu.cs.cs214.analyzer.framework.core;

/**
 * The interface by which {@link DataPlugin} instances can directly interact
 * with the analysis framework.
 */
public interface AppFramework {
    /**
     * Sets the text to display at the bottom of the framework's display.
     *
     * @param text The text to display.
     */
    void setFooterText(String text);
}
