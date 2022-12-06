package edu.cmu.cs.cs214.analyzer.framework.core;

import java.util.ArrayList;

import edu.cmu.cs.cs214.analyzer.framework.Util;

public class CourseReview {
    public double courseRate; // [0, 5]
    public ArrayList<Double> instructorRates; // [0, 5]
    public double workloadPerWeek;

    @Override
    public String toString() {
        return "{ " +
            "\"courseRate\": " + this.courseRate + ", " +
            "\"instructorRates\": " + Util.arrayListToString(this.instructorRates) + ", " +
            "\"workloadPerWeek\": " + this.workloadPerWeek +
            "}";
    }
}
