package edu.cmu.cs.cs214.analyzer.framework.core;

import java.util.ArrayList;

import edu.cmu.cs.cs214.analyzer.framework.Util;

public class Instructor {
    private String name;
    private int courseNum;
    private ArrayList<String> courseNames;
    private int organizationNum;
    private ArrayList<String> organizationNames;
    private int totalStudents;
    private double rate;
    private int rateEntries;

    public Instructor(String name) {
        this.name = name;
        this.courseNum = 0;
        this.courseNames = new ArrayList<String>();
        this.organizationNum = 0;
        this.organizationNames = new ArrayList<String>();
        this.totalStudents = 0;
        this.rate = 0;
        this.rateEntries = 0;
    }

    /**
     * Add a course to this instructor
     * 
     * @param courseName The name of the course to add
     */
    public void addCourse(String courseName) {
        (this.courseNum)++;
        this.courseNames.add(courseName);
    }

    /**
     * Add an organization to this instructor
     * 
     * @param organizationName The name of the organization to add
     */
    public void addOrganization(String organizationName) {
        // Do nothing if the organization has been added
        for (String name: this.organizationNames)
            if (name.equals(organizationName))
                return;

        (this.organizationNum)++;
        this.organizationNames.add(organizationName);
    }

    /**
     * Add the number of total students to this instructor
     * 
     * @param numStudents The number of new students
     */
    public void addStudents(int numStudents) {
        if (numStudents > 0)
            this.totalStudents += numStudents;
    }

    /**
     * Update the rate of this instructor
     * 
     * @param rate The new rate of this instructor
     * @param rateEntries The number of entries of the new rate
     */
    public void updateRate(double rate, int rateEntries) {
        if (rateEntries > 0) {
            final double rateSum = this.rate * this.rateEntries + rate * rateEntries;
            this.rateEntries += rateEntries;
            this.rate = rateSum / this.rateEntries;
        }
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<String> getCourseNames() {
        return this.courseNames;
    }

    public ArrayList<String> getOrganizationNames() {
        return this.organizationNames;
    }

    public int getTotalStudents() {
        return this.totalStudents;
    }
    
    public double getRate() {
        return this.rate;
    }

    @Override
    public String toString() {
        // Escape strings
        this.name = escapeString(this.name);
        for (int i = 0; i < this.courseNames.size(); i++)
			this.courseNames.set(i, escapeString(this.courseNames.get(i)));
        for (int i = 0; i < this.organizationNames.size(); i++)
			this.organizationNames.set(i, escapeString(this.organizationNames.get(i)));

        return "{ " +
            "\"name\": \"" + this.name + "\", " +
            "\"courseNum\": " + this.courseNum + ", " +
            "\"courseNames\": " + Util.arrayListToString(this.courseNames, true) + ", " +
            "\"organizationNum\": " + this.organizationNum + ", " +
            "\"organizationNames\": " + Util.arrayListToString(this.organizationNames, true) + ", " +
            "\"totalStudents\": " + this.totalStudents + ", " +
            "\"rate\": " + this.rate +
            " }";
    }

    public static String escapeString(String s) {
        return s.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\f", "\\f")
                .replace("\"", "\\\"");
    }
}
