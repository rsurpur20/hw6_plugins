package edu.cmu.cs.cs214.analyzer.plugin;

import edu.cmu.cs.cs214.analyzer.framework.core.Course;
import edu.cmu.cs.cs214.analyzer.framework.core.CourseReview;
import edu.cmu.cs.cs214.analyzer.framework.core.DataPlugin;

import java.io.StringReader;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.opencsv.CSVReader;

/**
 * The FCE plug-in implementation.
 * 
 * FCE data fields:
 * 0:  Year
 * 1:  Sem
 * 2:  College
 * 3:  Dept
 * 4:  Num
 * 5:  Section
 * 6:  Instructor
 * 7:  Course Name
 * 8:  Course Level
 * 9:  Total # Students
 * 10: # Responses
 * 11: Response Rate
 * 12: Hrs Per Week
 * 13: Interest in student learning
 * 14: Clearly explain course requirements
 * 15: Clear learning objectives & goals
 * 16: Instructor provides feedback to students to improve
 * 17: Demonstrate importance of subject matter
 * 18: Explains subject matter of course
 * 19: Show respect for all students
 * 20: Overall teaching rate
 * 21: Overall course rate
 */

public class FCEPlugin implements DataPlugin {
    private final String baseURI = "https://gist.githubusercontent.com";
    private final String basePath = "/shihhunw/2c260c9490cb4fcf7ec87a83c9068dac/raw/b4580df22360a616e86ca17396e8945f0041b01e";
    private final int maxRetry = 3;
    private final int timeoutSeconds = 10;
    private final String organizationName = "CMU";
    private final String miniCourseSectionRegex = "^[A-Z][1-4]$";
    private final int miniCourseTotalWeeks = 7;
    private final int regularCourseTotalWeeks = 14;
    private final double undergradTuitionPerYer = 59864;
    private final double masterTuitionPerYer = 52100;
    private final double summerTuitionPerUnit = 480;
    private final double miniCourseUnits = 6;
    private final double regularCourseUnits = 12;
    private final double totalUnitsPerYear = 72;
    private final int startYear = 2018;
    private final int endYear = 2022;
    private final int numOfFields = 22;
    
    @Override
    public String getName() {
        return "FCE";
    }

    @Override
    public ArrayList<Course> getCourses() {
        ArrayList<Course> courses = new ArrayList<>();
        for (int i = startYear; i < endYear + 1; i++) {
            courses.addAll(getCoursesByYear(i));
        }
        return courses;
    }

    private ArrayList<Course> getCoursesByYear(int year) {
        String uri = baseURI + basePath + "/" + String.valueOf(year) + ".csv";
        String csvString = httpGet(uri);
        if (csvString == null) {
            return null;
        }
        return parseCourses(csvString);
    }

    private ArrayList<Course> parseCourses(String csvString) {
        Map<Integer, Course> map = new HashMap<Integer, Course>();
        try {
            CSVReader csvReader = new CSVReader(new StringReader(csvString));
            String[] record;

            // read the csv header first
            record = csvReader.readNext();
      
            // read data line by line
            while ((record = csvReader.readNext()) != null) {
                if (!isValid(record)) {
                    // skip invalid records
                    continue;
                }
                Integer courseID = getCourseID(record);
                if (!map.containsKey(courseID)) {
                    // create a new course
                    map.put(courseID, createCourse(record));
                }
                // add a new review to this course
                Course course = map.get(courseID);
                updateCourse(course, record);
                course.reviews.add(createCourseReview(record));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<Course> courses = new ArrayList<>(map.values());
        updateInstructorRates(courses);
        return courses;
    }

    private Course createCourse(String[] record) {
        Course course = new Course();
        for (int i = 0; i < numOfFields; i++) {
            course.id = getCourseID(record);
            course.year = Integer.parseInt(record[0]);
            course.name = record[7];
            course.description = "N/A";
            course.instructorNames = new ArrayList<>();
            course.organizationName = organizationName;
            course.category = record[2].strip();
            course.level = record[8];
            course.totalStudents = 0;
            course.totalHours = -1; // unable to calculate
            course.totalWeeks = getTotalWeeks(record);
            course.estimatedWorkload = -1;
            course.rate = -1;
            course.price = getPrice(record);
            course.reviews = new ArrayList<>();
        }
        return course;
    }

    private void updateCourse(Course course, String[] record) {
        course.instructorNames.add(record[6]);
        course.totalStudents += Integer.parseInt(record[9]);
    }

    private CourseReview createCourseReview(String[] record) {
        CourseReview review = new CourseReview();
        review.courseRate = Double.parseDouble(record[21]);
        review.instructorRates = new ArrayList<>();
        review.instructorRates.add(Double.parseDouble(record[20]));
        review.workloadPerWeek = Double.parseDouble(record[12]);
        return review;
    }

    private void updateInstructorRates(ArrayList<Course> courses) {
        for (Course course : courses) {
            ArrayList<Double> rates = new ArrayList<>();
            for (CourseReview review : course.reviews) {
                rates.add(review.instructorRates.get(0));
            }
            for (CourseReview review : course.reviews) {
                review.instructorRates = rates;
            }
        }
    }

    private boolean isValid(String[] record) {
        if (record.length != numOfFields) {
            return false;
        }
        for (String s : record) {
            if (s.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isMiniCourse(String[] record) {
        return record[5].matches(miniCourseSectionRegex);
    }

    private int getCourseID(String[] record) {
        String key = record[0] + record[1] + record[4] + record[5];
        return key.hashCode();
    }

    private int getTotalWeeks(String[] record) {
        return isMiniCourse(record) ? miniCourseTotalWeeks : regularCourseTotalWeeks;
    }

    private double getPrice(String[] record) {
        boolean isMini = isMiniCourse(record);
        if (record[1].equals("Summer")) {
            return summerTuitionPerUnit * (isMini ? miniCourseUnits : regularCourseUnits);
        }
        double tuition = record[8].equals("Undergraduate") ?
            undergradTuitionPerYer : masterTuitionPerYer;
        return tuition * (isMini ? miniCourseUnits : regularCourseUnits) / totalUnitsPerYear;
    }

    private String httpGet(String uri) {
        // Create a http client and construct the URI
        HttpClient httpClient = HttpClient.newHttpClient();

        // Send a GET request for fetching the course details
        try {
            int countRetry = 0;
            while (countRetry < maxRetry) {
                // Create and send the request
                HttpRequest req = HttpRequest.newBuilder(URI.create(uri))
                                             .GET()
                                             .header("accept", "application/json, text/plain")
                                             .header("accept-language", "en-US,en;q=0.5")
                                             .timeout(Duration.ofSeconds(timeoutSeconds))
                                             .build();
                HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

                // Print error and retry if status code is not 200 OK
                if (resp.statusCode() != 200) {
                    System.out.println("Error: Got " + resp.statusCode() + " when fetching " + uri);
                    countRetry++;
                    continue;
                }
                return resp.body();
            }
        } catch (Exception e) {
            System.out.println("Error: Failed " + e.toString());
        }
        return null;
    }
}
