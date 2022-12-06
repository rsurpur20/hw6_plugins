package edu.cmu.cs.cs214.analyzer.framework.core;

import java.util.ArrayList;

/**
 * The data plugin interface used for plugins to send their courses to the analyzer.
 */
public interface DataPlugin {
    /**
     * Returns the name of the plugin.
     *
     * @return The name of the plugin.
     */
    String getName();

    /**
     * Retrieves the information of courses from the plugin. Data plugins have the full
     * responsibility for deciding which courses to return.
     *
     * @return A list of courses.
     */
    ArrayList<Course> getCourses();
}
