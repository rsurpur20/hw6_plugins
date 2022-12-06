package edu.cmu.cs.cs214.analyzer.plugin;

import java.util.ArrayList;
import java.util.Random;

import edu.cmu.cs.cs214.analyzer.framework.core.Course;
import edu.cmu.cs.cs214.analyzer.framework.core.CourseReview;
import edu.cmu.cs.cs214.analyzer.framework.core.DataPlugin;

public class ExamplePlugin implements DataPlugin {
    @Override
    public String getName() {
        return "Example";
    }

    @Override
    public ArrayList<Course> getCourses() {
        Random rand = new Random();
        ArrayList<Course> result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String name = rand.nextInt(2) == 0 ? "CMU" : "Udemy";
            int totalStudents = rand.nextInt(100);
            double rate = round(rand.nextDouble() * 5, 2);
            double price = rand.nextInt(10000);
            result.add(generateCourse(name, totalStudents, rate, price));
        }
        return result;
    }

    private Course generateCourse(
        String organizationName,
        int totalStudents,
        double rate,
        double price
    ) {
        Course c = new Course();
        c.id = 123;
        c.year = 2022;
        c.name = "An example course";
        c.description = "An example description";
        c.instructorNames = new ArrayList<>();
        c.instructorNames.add("Instructor 1");
        c.instructorNames.add("Instructor 2");
        c.organizationName = organizationName;
        c.category = "Example category";
        c.level = "Example level";
        c.totalStudents = totalStudents;
        c.totalHours = 256.5;
        c.totalWeeks = 14;
        c.estimatedWorkload = 9;
        c.rate = rate;
        c.price = price;
        c.reviews = new ArrayList<>();

        CourseReview r = new CourseReview();
        r.courseRate = 4.9;
        r.instructorRates = new ArrayList<>();
        r.instructorRates.add(1.0);
        r.instructorRates.add(5.0);
        r.workloadPerWeek = 19;
        c.reviews.add(r);
        return c;
    }

    private double round(double d, int n) {
        return Math.floor(d * Math.pow(10, n)) / Math.pow(10, n);
    }
}
