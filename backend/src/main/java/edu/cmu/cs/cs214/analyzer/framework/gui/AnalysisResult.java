package edu.cmu.cs.cs214.analyzer.framework.gui;

import java.util.ArrayList;

import edu.cmu.cs.cs214.analyzer.framework.Util;
import edu.cmu.cs.cs214.analyzer.framework.core.AppFrameworkImpl;
import edu.cmu.cs.cs214.analyzer.framework.core.Course;
import edu.cmu.cs.cs214.analyzer.framework.core.DataPlugin;
import edu.cmu.cs.cs214.analyzer.framework.core.Instructor;

public final class AnalysisResult {
    private final String name;
    private final String footer;
    private final ArrayList<Plugin> plugins;
    private final ArrayList<Course> courses;
    private final ArrayList<Instructor> instructors;

    private AnalysisResult(
        String name,
        String footer,
        ArrayList<Plugin> pluginNames,
        ArrayList<Course> courses,
        ArrayList<Instructor> instructors
    ) {
        this.name = name;
        this.footer = footer;
        this.plugins = pluginNames;
        this.courses = courses;
        this.instructors = instructors;
    }

    public static AnalysisResult getConfigurations(AppFrameworkImpl analyzer) {
        String name = analyzer.getAnalysisName();
        String footer = analyzer.getFooter();
        ArrayList<Plugin> pluginNames = getPlugins(analyzer);
        ArrayList<Course> courses = new ArrayList<Course>();
        ArrayList<Instructor> instructors = new ArrayList<Instructor>();
        return new AnalysisResult(name, footer, pluginNames, courses, instructors);
    }

    public static AnalysisResult getAllResult(AppFrameworkImpl analyzer) {
        String name = analyzer.getAnalysisName();
        String footer = analyzer.getFooter();
        ArrayList<Plugin> pluginNames = getPlugins(analyzer);
        ArrayList<Course> courses = getAnalyzedCourses(analyzer);
        ArrayList<Instructor> instructors = getAnalyzedInstructors(analyzer);
        return new AnalysisResult(name, footer, pluginNames, courses, instructors);
    }

    public static AnalysisResult getCoursesResult(AppFrameworkImpl analyzer) {
        String name = analyzer.getAnalysisName();
        String footer = analyzer.getFooter();
        ArrayList<Plugin> pluginNames = getPlugins(analyzer);
        ArrayList<Course> courses = getFilteredCourses(analyzer);
        ArrayList<Instructor> instructors = new ArrayList<Instructor>();
        return new AnalysisResult(name, footer, pluginNames, courses, instructors);
    }

    public static AnalysisResult getInstructorsResult(AppFrameworkImpl analyzer) {
        String name = analyzer.getAnalysisName();
        String footer = analyzer.getFooter();
        ArrayList<Plugin> pluginNames = getPlugins(analyzer);
        ArrayList<Course> courses = new ArrayList<Course>();
        ArrayList<Instructor> instructors = getFilteredInstructors(analyzer);
        return new AnalysisResult(name, footer, pluginNames, courses, instructors);
    }

    private static ArrayList<Plugin> getPlugins(AppFrameworkImpl analyzer) {
        ArrayList<DataPlugin> plugins = analyzer.getRegisteredPlugins();
        ArrayList<Plugin> result = new ArrayList<Plugin>();
        for (DataPlugin p : plugins) {
            result.add(new Plugin(p));
        }
        return result;
    }

    private static ArrayList<Course> getAnalyzedCourses(AppFrameworkImpl analyzer) {
        return analyzer.getAnalyzedCourses();
    }

    private static ArrayList<Course> getFilteredCourses(AppFrameworkImpl analyzer) {
        return analyzer.getFilteredCourses();
    }

    private static ArrayList<Instructor> getAnalyzedInstructors(AppFrameworkImpl analyzer) {
        return analyzer.getAnalyzedInstructors();
    }

    private static ArrayList<Instructor> getFilteredInstructors(AppFrameworkImpl analyzer) {
        return analyzer.getFilteredInstructors();
    }

    public String getName() {
        return name;
    }

    public String getFooter() {
        return footer;
    }

    @Override
    public String toString() {
        return ("{ \"name\": \"" + this.name + "\"," +
                " \"footer\": \"" + this.footer + "\"," +
                " \"plugins\": " + Util.arrayListToString(this.plugins) + "," +
                " \"courses\": " + Util.arrayListToString(this.courses) + "," +
                " \"instructors\": " + Util.arrayListToString(this.instructors) +
                " }"
        );
    }
}
