package edu.cmu.cs214.analyzer.framework.core;

import edu.cmu.cs.cs214.analyzer.framework.core.Instructor;
import edu.cmu.cs.cs214.analyzer.framework.core.InstructorFilter;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class InstructorFilterTest {
    @Test
    public void testIsMatchedTrue() {
        Instructor instructor = getTestInstructor();
        InstructorFilter filter = new InstructorFilter("Vincent", "Construction",
                                                       "CMU", 1);
        assertTrue(filter.isMatched(instructor));
    }

    @Test
    public void testIsMatchedNameNotMatch() {
        Instructor instructor = getTestInstructor();
        InstructorFilter filter = new InstructorFilter("Claire", "Construction",
                                                       "CMU", 1);
        assertFalse(filter.isMatched(instructor));
    }

    @Test
    public void testIsMatchedCourseNotMatch() {
        Instructor instructor = getTestInstructor();
        InstructorFilter filter = new InstructorFilter("Vincent", "AI",
                                                       "CMU", 1);
        assertFalse(filter.isMatched(instructor));
    }

    @Test
    public void testIsMatchedOrganizationNotMatch() {
        Instructor instructor = getTestInstructor();
        InstructorFilter filter = new InstructorFilter("Vincent", "Construction",
                                                       "MIT", 1);
        assertFalse(filter.isMatched(instructor));
    }

    private static Instructor getTestInstructor() {
        Instructor instructor = new Instructor("Vincent");
        instructor.addCourse("Principles of Software Construction");
        instructor.addCourse("Applied Deep Learning");
        instructor.addOrganization("CMU");
        return instructor;
    }
}
