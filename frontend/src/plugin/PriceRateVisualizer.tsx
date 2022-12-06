/**
 * A visualization plugin that plots scatter charts according to the given course information,
 * including the overall rate and the price.
 */

import React from 'react'
import {
  Chart as ChartJS,
  LinearScale,
  PointElement,
  Tooltip,
  Legend
} from 'chart.js'
import { Scatter } from 'react-chartjs-2'
import { Course, VisPlugin } from '../Analysis'

ChartJS.register(LinearScale, PointElement, Tooltip, Legend)

const options = {
  scales: {
    x: {
      title: {
        display: true,
        text: 'Price'
      }
    },
    y: {
      beginAtZero: true,
      title: {
        display: true,
        text: 'Overall Rate'
      }
    }
  }
}
const colors = [
  'rgba(255, 99, 132, 0.5)', // red
  'rgba(53, 162, 235, 0.5)' // blue
]

interface DataPoint {
  x: number
  y: number
  r: number
}

interface VisualizerState {
  data: DataPoint[][]
  names: string[]
  // filtering options
  name: string
  category: string
  level: string
  instructor: string
  organization: string
  year: string
  size: string
}

class Visualizer extends React.Component<{}, VisualizerState> {
  private initialized: boolean = false

  constructor (props: {}) {
    super(props)
    this.state = {
      data: [],
      names: [],
      name: '',
      category: '',
      level: '',
      instructor: '',
      organization: '',
      year: '',
      size: '20'
    }
    this.handleChange = this.handleChange.bind(this)
    this.handleSubmit = this.handleSubmit.bind(this)
  }

  /**
   * Fetches analyzed data from the backend server.
   */
  fetchData (): void {
    void fetch(this.buildURL())
      .then(async resp => await resp.json())
      .then(json => {
        const [data, names] = this.extractData(json.courses)
        this.setState({
          data,
          names
        })
      })
  }

  /**
   * Extracts data from the Instructor objects for the scatter charts.
   *
   * @param instructors The raw instructor data received from the backend.
   * @returns Data points and labels for the charts.
   */
  extractData (courses: Course[]): [DataPoint[][], string[]] {
    let count = 0
    const map: { [key: string]: number } = {}
    const names: string[] = []
    const data: DataPoint[][] = []
    for (const course of courses) {
      if (!(course.organizationName in map)) {
        map[course.organizationName] = count
        count += 1
        names.push(course.organizationName)
        data.push([])
      }
      data[map[course.organizationName]].push({
        x: course.price,
        y: course.rate,
        r: 1
      })
    }
    return [data, names]
  }

  componentDidMount (): void {
    if (!this.initialized) {
      this.initialized = true
      this.fetchData()
    }
  }

  buildURL (): string {
    let url: string = '/courses?'
    if (this.state.name.length !== 0) {
      url += `name=${this.state.name}&`
    }
    if (this.state.category.length !== 0) {
      url += `category=${this.state.category}&`
    }
    if (this.state.level.length !== 0) {
      url += `level=${this.state.level}&`
    }
    if (this.state.instructor.length !== 0) {
      url += `instructor=${this.state.instructor}&`
    }
    if (this.state.organization.length !== 0) {
      url += `organization=${this.state.organization}&`
    }
    if (this.state.year.length !== 0) {
      url += `year=${this.state.year}&`
    }
    if (this.state.size.length !== 0) {
      url += `size=${this.state.size}&`
    }
    return url
  }

  handleSubmit (evt: any): void {
    this.fetchData()
    evt.preventDefault()
  }

  handleChange (evt: any): void {
    const value = evt.target.value
    this.setState({
      ...this.state,
      [evt.target.name]: value
    })
  }

  render (): JSX.Element {
    const data = {
      datasets: this.state.data.map((data, idx) => {
        return {
          label: `${this.state.names[idx]} dataset`,
          data,
          pointRadius: 10,
          pointHoverRadius: 12,
          backgroundColor: colors[idx]
        }
      })
    }
    return (
      <div>
        <Scatter options={options} data={data} />
        <div>Filter Options:</div>
        <form onSubmit={this.handleSubmit}>
          <p>
            <label>Course Name: </label>
            <input type='text' name='name' value={this.state.name} onChange={this.handleChange} />
          </p>
          <p>
            <label>Category: </label>
            <input type='text' name='category' value={this.state.category} onChange={this.handleChange} />
          </p>
          <p>
            <label>Level: </label>
            <input type='text' name='level' value={this.state.level} onChange={this.handleChange} />
          </p>
          <p>
            <label>Instructor Name: </label>
            <input type='text' name='instructor' value={this.state.instructor} onChange={this.handleChange} />
          </p>
          <p>
            <label>Organization Name: </label>
            <input type='text' name='organization' value={this.state.organization} onChange={this.handleChange} />
          </p>
          <p>
            <label>Year: </label>
            <input type='text' name='year' value={this.state.year} onChange={this.handleChange} />
          </p>
          <p>
            <label>Limit: </label>
            <input type='text' name='size' value={this.state.size} onChange={this.handleChange} />
          </p>
          <input type='submit' value='Submit' />
        </form>
      </div>
    )
  }
}

// define and export the "plugin" object, which implements the "VisPlugin" interface
export const plugin: VisPlugin = {
  name: 'Price-Rate Visualization plugin',
  renderer: () => <Visualizer />
}
