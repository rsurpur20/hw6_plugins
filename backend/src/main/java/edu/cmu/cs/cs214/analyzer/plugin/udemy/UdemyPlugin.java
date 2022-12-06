package edu.cmu.cs.cs214.analyzer.plugin.udemy;

import edu.cmu.cs.cs214.analyzer.framework.core.Course;
import edu.cmu.cs.cs214.analyzer.framework.core.CourseReview;
import edu.cmu.cs.cs214.analyzer.framework.core.DataPlugin;

import java.io.File;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An example Memory game plug-in.
 */
public class UdemyPlugin implements DataPlugin {
    private String baseURI = "https://www.udemy.com";
    private String basePath = "/api-2.0/courses";
    private int maxRetry = 3;
    
    @Override
    public String getName() {
        return "Udemy";
    }

    @Override
    public ArrayList<Course> getCourses() {
        // List all English courses from Udemy
        // Udemy API requires a search keyword, so the most common letter "e" is used here
        HttpClient httpClient = HttpClient.newHttpClient();
        ArrayList<Course> courses = listCourses(httpClient, "e", "en");

        // Get all courses' details and reviews
        for(Course course: courses) {
            getCourseDetails(httpClient, course);
            getCourseReviews(httpClient, course);
        }

        return courses;
    }

    private ArrayList<Course> listCourses(HttpClient httpClient, String keyword, String lang) {
        ArrayList<Course> courses = new ArrayList<Course>();

        // Construct the URI
        String uri = baseURI + basePath;
        uri += "/?language=" + lang;
        uri += "&page=1&page_size=100";
        uri += "&search=" + keyword;

        // Keep sending GET requests for listing courses until there is no next page
        try {
            int countRetry = 0;
            while(countRetry < maxRetry) {
                // Read access token from file
                File tokenFile = new File("src/main/java/edu/cmu/cs/cs214/analyzer/plugin/udemy/accessToken.txt");
                Scanner scanner = new Scanner(tokenFile);
                String accessToken = scanner.nextLine();
                scanner.close();

                // Create and send the request
                HttpRequest req = HttpRequest.newBuilder(URI.create(uri))
                                             .GET()
                                             .header("cookie", "access_token=" + accessToken)
                                             .header("accept", "application/json, text/plain")
                                             .header("accept-language", "en-US,en;q=0.5")
                                             .timeout(Duration.ofSeconds(10))
                                             .build();
                HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

                // Print error and retry if status code is not 200 OK
                if(resp.statusCode() != 200) {
                    System.out.println("Error: Got " + resp.statusCode() + " when fetching " + uri);
                    countRetry++;
                    continue;
                }
                countRetry = 0;
                
                // Parse all courses in this page
                JSONObject respObj = new JSONObject(resp.body());
                JSONArray courseArr = respObj.getJSONArray("results");
                for(int i = 0; i < courseArr.length(); i++) {
                    JSONObject courseObj = courseArr.getJSONObject(i);
                    Course course = new Course();

                    course.id = courseObj.getInt("id");
                    course.year = 2022;  // cueently available
                    course.name = courseObj.getString("title");
                    course.description = courseObj.getString("url");  // use course URL as description
                    course.organizationName = "Udemy";
                    
                    course.totalWeeks = -1;
                    course.estimatedWorkload = -1;
                    course.rate = -1;
                    course.price = courseObj.getJSONObject("price_detail").getDouble("amount");
                    
                    // Get all instructors' names
                    course.instructorNames = new ArrayList<String>();
                    JSONArray instArr = courseObj.getJSONArray("visible_instructors");
                    for(int j = 0; j < instArr.length(); j++)
                        course.instructorNames.add(instArr.getJSONObject(j).getString("title"));

                    courses.add(course);
                }

                // Get the URI of next page
                if(respObj.isNull("next"))
                    break;
                uri = respObj.getString("next");
                
                // There is a bug in Udemy API - the data is identical for all pages
                // Thus, before the bug is fixed, we only fetch the first page
                break;
            }
        } catch(Exception e) {
            System.out.println("Error: Failed to fetch course list - " + e.toString());
        }

        return courses;
    }

    private void getCourseDetails(HttpClient httpClient, Course course) {
        // Construct the URI
        String params = "/?fields[course]=primary_category,instructional_level,num_subscribers,estimated_content_length";
        String uri = baseURI + basePath + "/" + String.valueOf(course.id) + params;

        // Send a GET request for fetching the course details
        try {
            int countRetry = 0;
            while(countRetry < maxRetry) {
                // Create and send the request
                HttpRequest req = HttpRequest.newBuilder(URI.create(uri))
                                             .GET()
                                             .header("accept", "application/json, text/plain")
                                             .header("accept-language", "en-US,en;q=0.5")
                                             .timeout(Duration.ofSeconds(10))
                                             .build();
                HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

                // Print error and retry if status code is not 200 OK
                if(resp.statusCode() != 200) {
                    System.out.println("Error: Got " + resp.statusCode() + " when fetching " + uri);
                    countRetry++;
                    continue;
                }

                // Get the details of this course
                JSONObject respObj = new JSONObject(resp.body());
                course.category = respObj.getJSONObject("primary_category").getString("title");
                course.level = respObj.getString("instructional_level");
                course.totalStudents = respObj.getInt("num_subscribers");
                course.totalHours = respObj.getInt("estimated_content_length") / 60.0;  // Convert minutes to hours
                break;
            }
        } catch(Exception e) {
            System.out.println("Error: Failed to fetch details of course" + course.name + " - " + e.toString());
        }
    }

    private void getCourseReviews(HttpClient httpClient, Course course) {
        course.reviews = new ArrayList<CourseReview>();

        // Construct the URI
        String uri = baseURI + basePath + "/" + String.valueOf(course.id) + "/reviews/?page=1&page_size=10";

        // Send a GET request for fetching the first 10 course reviews
        // We do not fetch all reviews here since it will take too long
        try {
            int countRetry = 0;
            while(countRetry < maxRetry) {
                // Create and send the request
                HttpRequest req = HttpRequest.newBuilder(URI.create(uri))
                                             .GET()
                                             .header("accept", "application/json, text/plain")
                                             .header("accept-language", "en-US,en;q=0.5")
                                             .timeout(Duration.ofSeconds(10))
                                             .build();
                HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

                // Print error and retry if status code is not 200 OK
                if(resp.statusCode() != 200) {
                    System.out.println("Error: Got " + resp.statusCode() + " when fetching " + uri);
                    countRetry++;
                    continue;
                }

                // Parse all reviews in this page
                JSONObject respObj = new JSONObject(resp.body());
                JSONArray reviewArr = respObj.getJSONArray("results");
                for(int i = 0; i < reviewArr.length(); i++) {
                    JSONObject reviewObj = reviewArr.getJSONObject(i);
                    CourseReview review = new CourseReview();

                    review.courseRate = reviewObj.getDouble("rating");
                    review.instructorRates = new ArrayList<Double>();
                    for(int j = 0; j < course.instructorNames.size(); j++) {
                        // There is no instructor rate for reviews on Udemy, just use course rate
                        review.instructorRates.add(review.courseRate);
                    }

                    review.workloadPerWeek = -1;  // There is no workload per week for online courses
                    course.reviews.add(review);
                }

                break;
            }
        } catch(Exception e) {
            System.out.println("Error: Failed to fetch details of course" + course.name + " - " + e.toString());
        }
    }
}
