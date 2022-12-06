package edu.cmu.cs214.analyzer.framework.core;

import edu.cmu.cs.cs214.analyzer.framework.core.Course;
import edu.cmu.cs.cs214.analyzer.framework.core.CourseReview;
import edu.cmu.cs.cs214.analyzer.framework.core.Instructor;

import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class CourseTest {
    @Test
    public void testComputeWorkload() {
        Course course = getTestCourse();
        course.computeWorkload();
        assertEquals(252, course.estimatedWorkload, 0.01);
    }

    @Test
    public void testComputeWorkloadNoReview() {
        Course course = getTestCourseWithoutReivews();
        course.computeWorkload();
        assertEquals(-1, course.estimatedWorkload, 0.01);
    }

    @Test
    public void testComputeWorkloadNoTotalWeeks() {
        Course course = getTestCourseWithoutTotalWeeks();
        course.computeWorkload();
        assertEquals(-1, course.estimatedWorkload, 0.01);
    }

    @Test
    public void testComputeCourseRate() {
        Course course = getTestCourse();
        course.computeCourseRate();
        assertEquals(4.25, course.rate, 0.01);
    }

    @Test
    public void testComputeCourseRateNoReview() {
        Course course = getTestCourseWithoutReivews();
        course.computeCourseRate();
        assertEquals(-1, course.rate, 0.01);
    }

    @Test
    public void testComputeInstructorRate() {
        Course course = getTestCourse();
        Instructor instructor = new Instructor("Vincent Hellendoorn");
        course.computeInstructorRate(instructor);
        assertEquals(4.75, instructor.getRate(), 0.01);
    }

    @Test
    public void testComputeInstructorRateNoReview() {
        Course course = getTestCourseWithoutReivews();
        Instructor instructor = new Instructor("Vincent Hellendoorn");
        course.computeInstructorRate(instructor);
        assertEquals(0, instructor.getRate(), 0.01);
    }

    @Test
    public void testComputeInstructorRateInstructorNotFound() {
        Course course = getTestCourse();
        Instructor instructor = new Instructor("Eric Wang");
        course.computeInstructorRate(instructor);
        assertEquals(0, instructor.getRate(), 0.01);
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

    private static Course getTestCourseWithoutReivews() {
        Course course = getTestCourse();
        course.reviews = new ArrayList<>();
        return course;
    }

    private static Course getTestCourseWithoutTotalWeeks() {
        Course course = getTestCourse();
        course.totalWeeks = 0;
        return course;
    }
}
