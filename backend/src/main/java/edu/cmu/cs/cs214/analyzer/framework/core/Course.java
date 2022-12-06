package edu.cmu.cs.cs214.analyzer.framework.core;

import java.util.ArrayList;

import edu.cmu.cs.cs214.analyzer.framework.Util;

public class Course {
    public int id;
    public int year;
    public String name;
    public String description;
    public ArrayList<String> instructorNames;
    public String organizationName;
    public String category;
    public String level;
    public int totalStudents;
    public double totalHours;
    public int totalWeeks;
    public double estimatedWorkload;
    public double rate;
    public double price;
    public ArrayList<CourseReview> reviews;

    /**
     * Compute the course's workload based on its reviews
     */
    public void computeWorkload() {
        this.estimatedWorkload = -1;

        // Return if there is no info of total weeks
        if (this.totalWeeks <= 0)
            return;
        
        // Sum up all valid workloads mentioned in reviews
        double sumWorkloadPerWeek = 0;
        int countValidEntries = 0;
        for (CourseReview review: this.reviews) {
            if (review.workloadPerWeek > 0) {
                sumWorkloadPerWeek += review.workloadPerWeek;
                countValidEntries++;
            }
        }

        // Return if no review records workload per week
        if (countValidEntries == 0)
            return;
        
        this.estimatedWorkload = (sumWorkloadPerWeek / countValidEntries) * this.totalWeeks;
    }

    
    /**
     * Compute the course's rate based on its reviews
     */
    public void computeCourseRate() {
        this.rate = -1;

        // Sum up all valid course rates mentioned in reviews
        double sumRate = 0;
        int countValidRates = 0;
        for (CourseReview review: this.reviews) {
            if (review.courseRate >= 0 && review.courseRate <= 5) {
                sumRate += review.courseRate;
                countValidRates++;
            }
        }

        // Return if no review records course rate
        if (countValidRates == 0)
            return;       
        this.rate = sumRate / countValidRates;
    }

    /**
     * Compute the i-th instructor's rate based on their reviews
     * 
     * @param instructor The {@link Instructor} to compute
     */
    public void computeInstructorRate(Instructor instructor) {
        // Find the instructor in this course by their name
        int instrIdx = -1;
        for(int i = 0; i < this.instructorNames.size(); i++) {
            if(instructorNames.get(i).equals(instructor.getName())) {
                instrIdx = i;
                break;
            }
        }

        if(instrIdx == -1)  // This course doesn't have the given instructor
            return;

        // Sum up all valid instructor rates mentioned in reviews
        double sumRate = 0;
        int countValidRates = 0;
        for (CourseReview review: this.reviews) {
            double rate = review.instructorRates.get(instrIdx);
            if (rate >= 0 && rate <= 5) {
                sumRate += rate;
                countValidRates++;
            }
        }

        // Return if no review records this instructor's rate
        if (countValidRates == 0)
            return;
        
        double avgRate = sumRate / countValidRates;
        instructor.updateRate(avgRate, countValidRates);
    }

    @Override
    public String toString() {
        // Escape strings
        this.name = escapeString(this.name);
        this.description = escapeString(this.description);
        for (int i = 0; i < this.instructorNames.size(); i++)
			this.instructorNames.set(i, escapeString(this.instructorNames.get(i)));
        this.organizationName = escapeString(this.organizationName);
        this.category = escapeString(this.category);
        this.level = escapeString(this.level);

        return "{ " +
            "\"id\": " + this.id + ", " +
            "\"year\": " + this.year + ", " +
            "\"name\": \"" + this.name + "\", " +
            "\"description\": \"" + this.description + "\", " +
            "\"instructorNames\": " + Util.arrayListToString(this.instructorNames, true) + ", " +
            "\"organizationName\": \"" + this.organizationName + "\", " +
            "\"category\": \"" + this.category + "\", " +
            "\"level\": \"" + this.level + "\", " +
            "\"totalStudents\": " + this.totalStudents + ", " +
            "\"totalHours\": " + this.totalHours + ", " +
            "\"totalWeeks\": " + this.totalWeeks + ", " +
            "\"estimatedWorkload\": " + this.estimatedWorkload + ", " +
            "\"rate\": " + this.rate + ", " +
            "\"price\": " + this.price + ", " +
            "\"reviews\": " + Util.arrayListToString(this.reviews) +
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
