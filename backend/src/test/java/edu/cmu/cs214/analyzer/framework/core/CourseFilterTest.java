package edu.cmu.cs214.analyzer.framework.core;

import edu.cmu.cs.cs214.analyzer.framework.core.Course;
import edu.cmu.cs.cs214.analyzer.framework.core.CourseFilter;
import edu.cmu.cs.cs214.analyzer.framework.core.CourseReview;

import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class CourseFilterTest {
    @Test
    public void testIsMatchedTrue() {
        Course course = getTestCourse();
        CourseFilter filter = new CourseFilter("Construction", "SCS", "Under",
                         "Le", "CMU", 2022, 1);
        assertTrue(filter.isMatched(course));
    }

    @Test
    public void testIsMatchedTrueIgnoreYear() {
        Course course = getTestCourse();
        CourseFilter filter = new CourseFilter("Construction", "SCS", "Under",
                         "Le", "CMU", 0, 1);
        assertTrue(filter.isMatched(course));
    }

    @Test
    public void testIsMatchedNameNotMatch() {
        Course course = getTestCourse();
        CourseFilter filter = new CourseFilter("Java", "SCS", "Under",
                         "Le", "CMU", 2022, 1);
        assertFalse(filter.isMatched(course));
    }

    @Test
    public void testIsMatchedCategoryNotMatch() {
        Course course = getTestCourse();
        CourseFilter filter = new CourseFilter("Construction", "CIT", "Under",
                         "Le", "CMU", 2022, 1);
        assertFalse(filter.isMatched(course));
    }
    
    @Test
    public void testIsMatchedLevelNotMatch() {
        Course course = getTestCourse();
        CourseFilter filter = new CourseFilter("Construction", "SCS", "Doctor",
                         "Le", "CMU", 2022, 1);
        assertFalse(filter.isMatched(course));
    }

    @Test
    public void testIsMatchedInstructorNotMatch() {
        Course course = getTestCourse();
        CourseFilter filter = new CourseFilter("Construction", "SCS", "Under",
                         "Eric", "CMU", 2022, 1);
        assertFalse(filter.isMatched(course));
    }

    @Test
    public void testIsMatchedOrganizationNotMatch() {
        Course course = getTestCourse();
        CourseFilter filter = new CourseFilter("Construction", "SCS", "Under",
                         "Le", "MIT", 2022, 1);
        assertFalse(filter.isMatched(course));
    }

    @Test
    public void testIsMatchedYearNotMatch() {
        Course course = getTestCourse();
        CourseFilter filter = new CourseFilter("Construction", "SCS", "Under",
                         "Le", "CMU", 2023, 1);
        assertFalse(filter.isMatched(course));
    }

    private static Course getTestCourse() {
        Course course = new Course();
        course.id = 1;
        course.year = 2022;
        course.name = "Principles of Software Construction";
        course.description = "Objects, Design, and Concurrency";
        course.instructorNames = new ArrayList<>();
        course.instructorNames.add("Claire Le Goues");
        course.instructorNames.add("Vincent Hellendoorn");
        course.organizationName = "CMU";
        course.category = "SCS";
        course.level = "Undergraduate";
        course.totalStudents = 100;
        course.totalHours = 3.5;
        course.totalWeeks = 14;
        course.estimatedWorkload = -1;
        course.rate = -1;
        course.price = 8333;
        course.reviews = new ArrayList<>();

        CourseReview review0 = new CourseReview();
        review0.courseRate = 4.5;
        review0.instructorRates = new ArrayList<>();
        review0.instructorRates.add(4.0);
        review0.instructorRates.add(5.0);
        review0.workloadPerWeek = 15.5;
        course.reviews.add(review0);

        CourseReview review1 = new CourseReview();
        review1.courseRate = 4.0;
        review1.instructorRates = new ArrayList<>();
        review1.instructorRates.add(3.5);
        review1.instructorRates.add(4.5);
        review1.workloadPerWeek = 20.5;
        course.reviews.add(review1);

        return course;
    }
}
