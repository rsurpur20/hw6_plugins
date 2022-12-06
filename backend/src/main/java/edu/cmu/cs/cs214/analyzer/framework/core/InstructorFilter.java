package edu.cmu.cs.cs214.analyzer.framework.core;

public class InstructorFilter {
    private final String nameKeyword;
    private final String courseNameKeyword;
    private final String organizationNameKeyword;
    private final int size;

    public InstructorFilter(
        String nameKeyword,
        String courseNameKeyword,
        String organizationNameKeyword,
        int size
    ) {
        this.nameKeyword = nameKeyword;
        this.courseNameKeyword = courseNameKeyword;
        this.organizationNameKeyword = organizationNameKeyword;
        this.size = size;
    }

    public boolean isMatched(Instructor instructor) {
        if (!instructor.getName().contains(this.nameKeyword))
            return false;
        
        boolean hasCourse = false;
        for (String courseName : instructor.getCourseNames()) {
            if (courseName.contains(this.courseNameKeyword)) {
                hasCourse = true;
                break;
            }
        }
        if (!hasCourse)
            return false;

        boolean hasOrganization = false;
        for (String organizationName : instructor.getOrganizationNames()) {
            if (organizationName.contains(this.organizationNameKeyword)) {
                hasOrganization = true;
                break;
            }
        }
        if (!hasOrganization)
            return false;

        return true;
    }

    public int getSize() {
        return this.size;
    }
}
