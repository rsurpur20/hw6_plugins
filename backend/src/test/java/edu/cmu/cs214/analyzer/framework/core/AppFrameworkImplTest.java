package edu.cmu.cs214.analyzer.framework.core;

import edu.cmu.cs.cs214.analyzer.framework.core.AppFrameworkImpl;
import edu.cmu.cs.cs214.analyzer.framework.core.Course;
import edu.cmu.cs.cs214.analyzer.framework.core.CourseFilter;
import edu.cmu.cs.cs214.analyzer.framework.core.CourseReview;
import edu.cmu.cs.cs214.analyzer.framework.core.DataPlugin;
import edu.cmu.cs.cs214.analyzer.framework.core.Instructor;
import edu.cmu.cs.cs214.analyzer.framework.core.InstructorFilter;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AppFrameworkImplTest {
    private static AppFrameworkImpl analyzer;
    private static DataPlugin stubPlugin;

    @Before
    public void initialize() {
        // Generate the stub data plugin
        stubPlugin = mock(DataPlugin.class);
        when(stubPlugin.getName()).thenReturn("Stub");
        ArrayList<Course> courses = getTestCourses();
        when(stubPlugin.getCourses()).thenReturn(courses);

        // Register the stub data plugin
        analyzer = new AppFrameworkImpl();
        analyzer.registerPlugin(stubPlugin);

        // Verify the registration
        ArrayList<DataPlugin> registeredPlugins = analyzer.getRegisteredPlugins();
        assertEquals(1, registeredPlugins.size());
        assertTrue(registeredPlugins.get(0).getName().equals("Stub"));

        // Start analyzing with the stub data plugin
        analyzer.startNewAnalysis(stubPlugin);
    }

    @Test
    public void testAnalyzedCourses() {
        // Get the analyzed courses
        ArrayList<Course> analyzedCourses = analyzer.getAnalyzedCourses();
        assertEquals(2, analyzedCourses.size());

        Course course0 = analyzedCourses.get(0);
        assertTrue(course0.name.equals("Principles of Software Construction"));
        assertEquals(2, course0.instructorNames.size());
        assertTrue(course0.instructorNames.get(0).equals("Claire Le Goues"));
        assertTrue(course0.instructorNames.get(1).equals("Vincent Hellendoorn"));
        assertEquals(100, course0.totalStudents);
        assertEquals(252, course0.estimatedWorkload, 0.01);
        assertEquals(4.25, course0.rate, 0.01);
        assertEquals(8333, course0.price, 0.01);
    }

    @Test
    public void testAnalyzedInstructors() {
        // Get the analyzed instructors
        ArrayList<Instructor> analyzedInstructors = analyzer.getAnalyzedInstructors();
        assertEquals(2, analyzedInstructors.size());

        Instructor instructor0 = analyzedInstructors.get(0);
        assertTrue(instructor0.getName().equals("Claire Le Goues"));
        assertEquals(1, instructor0.getCourseNames().size());
        assertTrue(instructor0.getCourseNames().get(0).equals("Principles of Software Construction"));
        assertEquals(100, instructor0.getTotalStudents());
        assertEquals(3.75, instructor0.getRate(), 0.01);
    }

    @Test
    public void testFilteredCourses() {
        // Create a filter to filter the courses
        String nameKeyword = "Deep";
        String instructorNameKeyword = "Hellendoorn";
        CourseFilter filter = new CourseFilter(nameKeyword, "", "",
                                               instructorNameKeyword, "", 2022, 10);
        analyzer.filterCourses(filter);
        
        // Get the filtered courses
        ArrayList<Course> filteredCourses = analyzer.getFilteredCourses();
        assertEquals(1, filteredCourses.size());

        Course course = filteredCourses.get(0);
        assertTrue(course.name.equals("Applied Deep Learning"));
        assertEquals(1, course.instructorNames.size());
        assertTrue(course.instructorNames.get(0).equals("Vincent Hellendoorn"));
        assertEquals(50, course.totalStudents);
        assertEquals(175, course.estimatedWorkload, 0.01);
        assertEquals(4.0, course.rate, 0.01);
        assertEquals(4167, course.price, 0.01);
    }

    @Test
    public void testFilteredInstructors() {
        // Create a filter to filter the instructors
        String nameKeyword = "Vincent";
        String courseNameKeyword = "Software";
        InstructorFilter filter = new InstructorFilter(nameKeyword, courseNameKeyword, "", 10);
        analyzer.filterInstructors(filter);
        
        // Get the filtered instructors
        ArrayList<Instructor> filteredInstructors = analyzer.getFilteredInstructors();
        assertEquals(1, filteredInstructors.size());

        Instructor instructor = filteredInstructors.get(0);
        assertTrue(instructor.getName().equals("Vincent Hellendoorn"));
        assertEquals(2, instructor.getCourseNames().size());
        assertTrue(instructor.getCourseNames().get(0).equals("Principles of Software Construction"));
        assertTrue(instructor.getCourseNames().get(1).equals("Applied Deep Learning"));
        assertEquals(150, instructor.getTotalStudents());
        assertEquals(4.5, instructor.getRate(), 0.01);
    }

    private static ArrayList<Course> getTestCourses() {
        ArrayList<Course> courses = new ArrayList<Course>();

        Course course0 = new Course();
        course0.id = 1;
        course0.year = 2022;
        course0.name = "Principles of Software Construction";
        course0.description = "Objects, Design, and Concurrency";
        course0.instructorNames = new ArrayList<>();
        course0.instructorNames.add("Claire Le Goues");
        course0.instructorNames.add("Vincent Hellendoorn");
        course0.organizationName = "CMU";
        course0.category = "SCS";
        course0.level = "Undergraduate";
        course0.totalStudents = 100;
        course0.totalHours = 3.5;
        course0.totalWeeks = 14;
        course0.estimatedWorkload = -1;
        course0.rate = -1;
        course0.price = 8333;
        course0.reviews = new ArrayList<>();

        CourseReview review0 = new CourseReview();
        review0.courseRate = 4.5;
        review0.instructorRates = new ArrayList<>();
        review0.instructorRates.add(4.0);
        review0.instructorRates.add(5.0);
        review0.workloadPerWeek = 15.5;
        course0.reviews.add(review0);

        CourseReview review1 = new CourseReview();
        review1.courseRate = 4.0;
        review1.instructorRates = new ArrayList<>();
        review1.instructorRates.add(3.5);
        review1.instructorRates.add(4.5);
        review1.workloadPerWeek = 20.5;
        course0.reviews.add(review1);

        courses.add(course0);

        Course course1 = new Course();
        course1.id = 2;
        course1.year = 2022;
        course1.name = "Applied Deep Learning";
        course1.description = "Deep neural networks have made in-roads in virtually every industry";
        course1.instructorNames = new ArrayList<>();
        course1.instructorNames.add("Vincent Hellendoorn");
        course1.organizationName = "CMU";
        course1.category = "SCS";
        course1.level = "Graduate";
        course1.totalStudents = 50;
        course1.totalHours = 1.5;
        course1.totalWeeks = 14;
        course1.estimatedWorkload = -1;
        course1.rate = -1;
        course1.price = 4167;
        course1.reviews = new ArrayList<>();

        CourseReview review2 = new CourseReview();
        review2.courseRate = 4.0;
        review2.instructorRates = new ArrayList<>();
        review2.instructorRates.add(4.0);
        review2.workloadPerWeek = 12.5;
        course1.reviews.add(review2);

        courses.add(course1);
        return courses;
    }
}
