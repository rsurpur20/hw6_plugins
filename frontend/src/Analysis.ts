/**
 * The interface of the course analysis result received from the backend.
 */
interface AnalysisResult {
  status: string
  dataPlugins: DataPlugin[]
  visPlugins: VisPlugin[]
  loadedDataPlugins: string[]
  chosenVis: number
  analyzedCourses: Course[]
  currentVisPlugin: VisPlugin
}

/**
 * The interface of the course object.
 */
interface Course {
  id: number
  year: number
  name: string
  description: string
  instructorNames: string[]
  organizationName: string
  category: string
  level: string
  totalStudents: number
  totalHours: number
  totalWeeks: number
  estimatedWorkload: number
  rate: number
  price: number
  reviews: CourseReview
}

/**
 * The interface of the course review object.
 */
interface CourseReview {
  courseRate: number
  instructorRates: number[]
  workloadPerWeek: number
}

/**
 * The interface of the instructor object.
 */
interface Instructor {
  name: string
  courseNum: number
  courseNames: string[]
  organizationNum: number
  organizationNames: string[]
  totalStudents: number
  rate: number
}

/**
 * The interface of the data plugin object.
 */
interface DataPlugin {
  name: string
}

/**
 * The interface of the visualization plugin object. A visualization plugin must implement an
 * object named "plugin", which complies with this interface so that the front can load and use
 * the plugin properly.
 *
 * @param name The name of the plugin
 * @param renderer A function that takes no arguments and returns a JSX element/React component,
 *                 which is then embedded into the web page and shown to the users on the front
 *                 end side.
 */
interface VisPlugin {
  name: string
  renderer: () => JSX.Element
}

export type { AnalysisResult, Course, CourseReview, Instructor, DataPlugin, VisPlugin }
