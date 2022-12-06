package edu.cmu.cs.cs214.analyzer.plugin.csv;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVFormat.Builder;

import edu.cmu.cs.cs214.analyzer.framework.core.Course;
import edu.cmu.cs.cs214.analyzer.framework.core.CourseReview;
import edu.cmu.cs.cs214.analyzer.framework.core.DataPlugin;

public class CSVPlugin implements DataPlugin {

    private static final String name = "CSV Plugin";
    private static final String BASE = "src/main/java/edu/cmu/cs/cs214/analyzer/plugin/csv/";
    private static final String[] COURSE_HEADERS = { "ID", "Year", "Name", "Description", 
                                                     "Instructor 1", "Instructor 2", "Instructor 3", 
                                                     "Organization", "Category", 
                                                     "Level", "Students", "Hours", "Weeks", 
                                                     "Workload", "Rate", "Price" };
    private static final String[] REVIEW_HEADERS = { "Name", "Course Rating", 
                                                     "Instructor Rating 1", "Instructor Rating 2", 
                                                     "Instructor Rating 3", "Workload" };

    /**
     * Returns the name of the plugin.
     *
     * @return The name of the plugin.
     */
    public String getName()    {
        return name;
    }

    /**
     * Retrieves the information of courses from the plugin. Data plugins have the full
     * responsibility for deciding which courses to return.
     *
     * @return A list of courses.
     */
    public ArrayList<Course> getCourses()  {

        // initialize result
        ArrayList<Course> courses = new ArrayList<Course>();
        HashMap<String, Course> courseMap = new HashMap<String, Course>();
        // parse the course csv
        parseCourses(courses, courseMap);
        // parse the course csv
        parseReviews(courses, courseMap);
        
        return courses;
    }

    private void parseCourses(ArrayList<Course> courses, HashMap<String, Course> courseMap)  {

        boolean firstLine = true;
        // parse the course csv
        CSVParser courseparser = parse("courses.csv", COURSE_HEADERS);
        for (CSVRecord csvRecord : courseparser) {
            // skip the first line because it is just the header names
            if (!firstLine) {
                Course course = getCourse(csvRecord);
                // add to map to be used later
                courseMap.put(course.name, course);
                courses.add(course);
            }
            firstLine = false;
        }

    }

    private void parseReviews(ArrayList<Course> courses, HashMap<String, Course> courseMap)  {

        boolean firstLine = true;
        // parse the ratings csv
        CSVParser reviewparser = parse("ratings.csv", REVIEW_HEADERS);
        for (CSVRecord csvRecord : reviewparser) {
            // skip the first line because it is just the header names
            if (!firstLine) {
                // get the course name of the review
                String courseName = getCourseName(csvRecord);
                // get the corresponding course
                Course course = courseMap.get(courseName);
                // process the reviews for that course
                course.reviews.add(getCourseReview(csvRecord, course));
                // if we added reviews, we want to recompute workload / rating
                course.computeCourseRate();
                course.computeWorkload();
            }
            firstLine = false;
        }
        
    }

    private Course getCourse(CSVRecord record)   {

        // create the new course
        Course course = new Course();
        // now parse the record into course attributes
        course.id = Integer.parseInt(record.get("ID"));
        course.year = Integer.parseInt(record.get("Year"));
        course.name = record.get("Name");
        course.description = record.get("Description");
        course.instructorNames = getInstructorNames(record);
        course.category = record.get("Category");
        course.organizationName = record.get("Organization");
        course.level = record.get("Level");
        course.totalStudents = Integer.parseInt(record.get("Students"));
        course.totalHours = Double.parseDouble(record.get("Hours"));
        course.totalWeeks = Integer.parseInt(record.get("Weeks"));
        course.estimatedWorkload = Double.parseDouble(record.get("Workload"));
        course.rate = Double.parseDouble(record.get("Rate"));
        course.price = Double.parseDouble(record.get("Price"));
        course.reviews = new ArrayList<CourseReview>();

        return course;

    }

    private CourseReview getCourseReview(CSVRecord record, Course course)    {

        CourseReview review = new CourseReview();

        review.courseRate = Double.parseDouble(record.get("Course Rating"));
        review.workloadPerWeek = Double.parseDouble(record.get("Workload"));

        ArrayList<Double> instructorRatings = new ArrayList<Double>();

        String rating1 = record.get("Instructor Rating 1");
        String rating2 = record.get("Instructor Rating 2");
        String rating3 = record.get("Instructor Rating 3");

        // only add the rating if it is not empty
        if (!rating1.equals("")) instructorRatings.add(Double.parseDouble(rating1));
        if (!rating2.equals("")) instructorRatings.add(Double.parseDouble(rating2));
        if (!rating3.equals("")) instructorRatings.add(Double.parseDouble(rating3));

        review.instructorRates = instructorRatings;

        return review;

    }

    private String getCourseName(CSVRecord record) {

        return record.get("Name");

    }

    private ArrayList<String> getInstructorNames(CSVRecord record)    {
        
        ArrayList<String> instructorNames = new ArrayList<String>();

        String instructor1 = record.get("Instructor 1");
        String instructor2 = record.get("Instructor 2");
        String instructor3 = record.get("Instructor 3");

        // only add an instructor if it is not empty
        if (!instructor1.equals("")) instructorNames.add(instructor1);
        if (!instructor2.equals("")) instructorNames.add(instructor2);
        if (!instructor3.equals("")) instructorNames.add(instructor3);

        return instructorNames;
    }

    private CSVParser parse(String fileName, String[] headers) {
        // init return
        CSVParser parser = null;
        try {
            // read the csv file
            File file = new File(BASE + fileName);
            Reader csvData = new FileReader(file.getAbsolutePath());
            // create the csv format builder
            Builder builder = CSVFormat.Builder.create();
            CSVFormat format = builder.setHeader(headers).build();
            // parse with the specified format
            parser = CSVParser.parse(csvData, format);

        } catch (IOException e) {
            System.out.println("Error: Failed " + e.toString());
            e.printStackTrace();
        }
        return parser;
    }
    
    
}
