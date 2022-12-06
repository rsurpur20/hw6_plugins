package edu.cmu.cs214.analyzer.framework.core;

import edu.cmu.cs.cs214.analyzer.framework.core.Instructor;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class InstructorTest {
    @Test
    public void testAddCourse() {
        Instructor instructor = new Instructor("Vincent");
        instructor.addCourse("Principles of Software Construction");
        instructor.addCourse("Applied Deep Learning");

        assertEquals(2, instructor.getCourseNames().size());
        assertTrue(instructor.getCourseNames().get(0).equals("Principles of Software Construction"));
        assertTrue(instructor.getCourseNames().get(1).equals("Applied Deep Learning"));
    }

    @Test
    public void testAddOrganization() {
        Instructor instructor = new Instructor("Vincent");
        instructor.addOrganization("CMU");
        instructor.addOrganization("MIT");

        assertEquals(2, instructor.getOrganizationNames().size());
        assertTrue(instructor.getOrganizationNames().get(0).equals("CMU"));
        assertTrue(instructor.getOrganizationNames().get(1).equals("MIT"));
    }

    @Test
    public void testAddStudents() {
        Instructor instructor = new Instructor("Vincent");
        instructor.addStudents(123);
        instructor.addStudents(456);

        assertEquals(579, instructor.getTotalStudents());
    }

    @Test
    public void testUpdateRate() {
        Instructor instructor = new Instructor("Vincent");
        instructor.updateRate(4.0, 4);
        instructor.updateRate(5.0, 6);

        assertEquals(4.6, instructor.getRate(), 0.01);
    }
}
