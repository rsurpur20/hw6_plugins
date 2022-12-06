package edu.cmu.cs.cs214.analyzer.framework.core;

import java.util.ArrayList;

/**
 * The framework core implementation.
 */
public class AppFrameworkImpl implements AppFramework {
    private final String defaultName = "A course analysis framework";
    private final String defaultFooter = "Default footer";
    private String footer;
    private DataPlugin currentPlugin;
    private ArrayList<DataPlugin> registeredPlugins;
    private ArrayList<DataPlugin> analyzedPlugins;
    private ArrayList<Course> analyzedCourses;
    private ArrayList<Course> filteredCourses;
    private ArrayList<Instructor> analyzedInstructors;
    private ArrayList<Instructor> filteredInstructors;

    public AppFrameworkImpl() {
        footer = defaultFooter;
        registeredPlugins = new ArrayList<DataPlugin>();
        analyzedPlugins = new ArrayList<DataPlugin>();
        analyzedCourses = new ArrayList<Course>();
        analyzedInstructors = new ArrayList<Instructor>();
    }

    /**
     * Registers a new {@link DataPlugin} with the analysis framework
     * 
     * @param plugin The {@link DataPlugin} to register
     */
    public void registerPlugin(DataPlugin plugin) {
        registeredPlugins.add(plugin);
    }

    /**
     * Starts a new analysis for the provided {@link DataPlugin}
     * 
     * @param plugin The {@link DataPlugin} to analyze
     */
    public void startNewAnalysis(DataPlugin plugin) {
        // Check if we have analyzed courses from this plugin
        for(DataPlugin analyzedPlugin : this.analyzedPlugins)
            if(analyzedPlugin.getName().equals(plugin.getName()))
                return;
        this.analyzedPlugins.add(plugin);

        if (currentPlugin != plugin)  // Switching to a new plugin's analysis
            currentPlugin = plugin;

        // Get courses from plugin and analyze them
        ArrayList<Course> courses = currentPlugin.getCourses();
        for (Course course : courses) {
            // Compute estimatedWorkload and rate if they are not specified
            if (course.estimatedWorkload < 0)
                course.computeWorkload();
            if (course.rate < 0)
                course.computeCourseRate();

            this.analyzedCourses.add(course);

            // Update info of all instructors in this course
            for (String instructorName : course.instructorNames) {
                boolean isNewInstructor = true;
                for (Instructor instructor : this.analyzedInstructors) {
                    if (instructor.getName().equals(instructorName)) {  // An old instructor
                        isNewInstructor = false;
                        instructor.addCourse(course.name);
                        instructor.addOrganization(course.organizationName);
                        instructor.addStudents(course.totalStudents);
                        course.computeInstructorRate(instructor);
                        break;
                    }
                }

                if (isNewInstructor) {  // A new instructor
                    Instructor instructor = new Instructor(instructorName);
                    instructor.addCourse(course.name);
                    instructor.addOrganization(course.organizationName);
                    instructor.addStudents(course.totalStudents);
                    course.computeInstructorRate(instructor);
                    this.analyzedInstructors.add(instructor);
                }
            }
        }
    }

    /**
     * Filter the analyzed courses based on the provided {@link CourseFilter}
     * 
     * @param filter The {@link CourseFilter} to apply
     */
    public void filterCourses(CourseFilter filter) {
        this.filteredCourses = new ArrayList<Course>();
        int currSize = 0;
        for (Course course : this.analyzedCourses) {
            if (currSize >= filter.getSize())
                return;
            
            if (filter.isMatched(course)) {
                this.filteredCourses.add(course);
                currSize++;
            }
        }
    }

    /**
     * Filter the analyzed instructors based on the provided {@link InstructorFilter}
     * 
     * @param filter The {@link InstructorFilter} to apply
     */
    public void filterInstructors(InstructorFilter filter) {
        this.filteredInstructors = new ArrayList<Instructor>();
        int currSize = 0;
        for (Instructor instructor : this.analyzedInstructors) {
            if (currSize >= filter.getSize())
                return;
            
            if (filter.isMatched(instructor)) {
                this.filteredInstructors.add(instructor);
                currSize++;
            }
        }
    }

    /* AppFramework methods. */
    @Override
    public void setFooterText(String text) {
        footer = text;
    }

    /* getter for Gui purposes*/
    public String getAnalysisName() {
        if (currentPlugin == null) {
            return defaultName;
        } else {
            return currentPlugin.getName();
        }
    }

    public String getFooter() {
        return footer;
    }

    public ArrayList<DataPlugin> getRegisteredPlugins() {
        return registeredPlugins;
    }

    public boolean hasStarted() {
        return currentPlugin != null;
    }

    public ArrayList<Course> getAnalyzedCourses() {
        return this.analyzedCourses;
    }

    public ArrayList<Course> getFilteredCourses() {
        return this.filteredCourses;
    }

    public ArrayList<Instructor> getAnalyzedInstructors() {
        return this.analyzedInstructors;
    }

    public ArrayList<Instructor> getFilteredInstructors() {
        return this.filteredInstructors;
    }
}
