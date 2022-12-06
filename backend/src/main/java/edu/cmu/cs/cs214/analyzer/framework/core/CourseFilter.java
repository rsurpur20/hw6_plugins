package edu.cmu.cs.cs214.analyzer.framework.core;

public class CourseFilter {
    private final String nameKeyword;
    private final String categoryKeyword;
    private final String levelKeyword;
    private final String instructorNameKeyword;
    private final String organizationNameKeyword;
    private final int year;
    private final int size;

    public CourseFilter(
        String nameKeyword,
        String categoryKeyword,
        String levelKeyword,
        String instructorNameKeyword,
        String organizationNameKeyword,
        int year,
        int size
    ) {
        this.nameKeyword = nameKeyword;
        this.categoryKeyword = categoryKeyword;
        this.levelKeyword = levelKeyword;
        this.instructorNameKeyword = instructorNameKeyword;
        this.organizationNameKeyword = organizationNameKeyword;
        this.year = year;
        this.size = size;
    }

    public boolean isMatched(Course course) {
        if (!course.name.contains(this.nameKeyword))
            return false;
        if (!course.category.contains(this.categoryKeyword))
            return false;
        if (!course.level.contains(this.levelKeyword))
            return false;
        
        boolean hasInstructor = false;
        for (String instructorName : course.instructorNames) {
            if (instructorName.contains(this.instructorNameKeyword)) {
                hasInstructor = true;
                break;
            }
        }
        if (!hasInstructor)
            return false;
        
        if (!course.organizationName.contains(this.organizationNameKeyword))
            return false;
        if (this.year > 0 && course.year != this.year)
            return false;

        return true;
    }

    public int getSize() {
        return this.size;
    }
}
