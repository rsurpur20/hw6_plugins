package edu.cmu.cs.cs214.analyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import edu.cmu.cs.cs214.analyzer.framework.core.AppFrameworkImpl;
import edu.cmu.cs.cs214.analyzer.framework.core.CourseFilter;
import edu.cmu.cs.cs214.analyzer.framework.core.DataPlugin;
import edu.cmu.cs.cs214.analyzer.framework.core.InstructorFilter;
import edu.cmu.cs.cs214.analyzer.framework.gui.AnalysisResult;
import fi.iki.elonen.NanoHTTPD;

public class App extends NanoHTTPD {
    private static final int PORT_NUMBER = 8080;

    public static void main(String[] args) {
        try {
            new App();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }

    private AppFrameworkImpl analyzer;
    private List<DataPlugin> plugins;

    /**
     * Start the server at :8080 port.
     * @throws IOException
     */
    public App() throws IOException {
        super(PORT_NUMBER);

        this.analyzer = new AppFrameworkImpl();
        plugins = loadPlugins();
        for (DataPlugin p: plugins) {
            analyzer.registerPlugin(p);
        }


        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:8080/ \n");
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Map<String, String> params = session.getParms();

        // Extract the view-specific data from the analyzer
        AnalysisResult result = null;
        final int maxSize = 2147483647;
        if (uri.equals("/plugin")) {  // e.g., /plugin?i=0
            analyzer.startNewAnalysis(plugins.get(Integer.parseInt(params.get("i"))));
            result = AnalysisResult.getAllResult(this.analyzer);
        } else if (uri.equals("/courses")) {   // e.g., /courses?name=Java&instructor=Claire&size=100
            // Parse course filter parameters
            String nameKeyword = params.containsKey("name") ? params.get("name") : "";
            String categoryKeyword = params.containsKey("category") ? params.get("category") : "";
            String levelKeyword = params.containsKey("level") ? params.get("level") : "";
            String instructorNameKeyword = params.containsKey("instructor") ? params.get("instructor") : "";
            String organizationNameKeyword = params.containsKey("organization") ? params.get("organization") : "";
            int year = params.containsKey("year") ? Integer.parseInt(params.get("year")) : -1;
            int size = params.containsKey("size") ? Integer.parseInt(params.get("size")) : maxSize;

            // Filter the courses and get the result
            CourseFilter filter = new CourseFilter(nameKeyword, categoryKeyword, levelKeyword,
                                                   instructorNameKeyword, organizationNameKeyword, year, size);
            analyzer.filterCourses(filter);
            result = AnalysisResult.getCoursesResult(this.analyzer);
        } else if (uri.equals("/instructors")) {   // e.g., /instructors?name=Vincent&organization=CMU&size=100
            // Parse parameters to the instructor filter
            String nameKeyword = params.containsKey("name") ? params.get("name") : "";
            String courseKeyword = params.containsKey("course") ? params.get("course") : "";
            String organizationNameKeyword = params.containsKey("organization") ? params.get("organization") : "";
            int size = params.containsKey("size") ? Integer.parseInt(params.get("size")) : maxSize;

            // Filter the instructors and get the result
            InstructorFilter filter = new InstructorFilter(nameKeyword, courseKeyword, organizationNameKeyword, size);
            analyzer.filterInstructors(filter);
            result = AnalysisResult.getInstructorsResult(this.analyzer);
        } else {
            result = AnalysisResult.getConfigurations(this.analyzer);
        }

        return newFixedLengthResponse(result.toString());
    }


    /**
     * Load plugins listed in META-INF/services/...
     *
     * @return List of instantiated plugins
     */
    private static List<DataPlugin> loadPlugins() {
        ServiceLoader<DataPlugin> plugins = ServiceLoader.load(DataPlugin.class);
        List<DataPlugin> result = new ArrayList<>();
        for (DataPlugin plugin : plugins) {
            System.out.println("Loaded plugin " + plugin.getName());
            result.add(plugin);
        }
        return result;
    }

    public static class Test {
        public String getText() {
            return "Hello World!";
        }
    }
}

