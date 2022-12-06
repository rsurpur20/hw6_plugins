/**
 * A visualization plugin that plots bar charts according to the given instructor information,
 * including the teaching overall rate and the total students.
 */

import React from 'react'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js'
import { Bar } from 'react-chartjs-2'
import { Instructor, VisPlugin } from '../Analysis'

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
)

type Record = [string, number]
type SortFuncType = ((a: Record, b: Record) => number)
const sortNone: SortFuncType = (r1, r2) => 0
const sortDescending: SortFuncType = (r1, r2) => r2[1] - r1[1]
const sortAscending: SortFuncType = (r1, r2) => r1[1] - r2[1]

const colors = [
  'rgba(255, 99, 132, 0.5)', // red
  'rgba(53, 162, 235, 0.5)' // blue
]

interface DataPoint {
  totalStudents: number
  rate: number
}

interface VisualizerState {
  data: DataPoint[]
  label: string[]
  sorting: SortFuncType
  // filtering options
  name: string
  course: string
  organization: string
  size: string
}

class Visualizer extends React.Component<{}, VisualizerState> {
  private initialized: boolean = false

  constructor (props: {}) {
    super(props)
    this.state = {
      data: [],
      label: [],
      sorting: sortNone,
      name: '',
      course: '',
      organization: '',
      size: '20'
    }
    this.handleChange = this.handleChange.bind(this)
    this.handleSortingChange = this.handleSortingChange.bind(this)
    this.handleSubmit = this.handleSubmit.bind(this)
  }

  /**
   * Fetches analyzed data from the backend server.
   */
  fetchData (): void {
    void fetch(this.buildURL())
      .then(async resp => await resp.json())
      .then(json => {
        const [data, label] = this.extractData(json.instructors)
        this.setState({
          data,
          label
        })
      })
  }

  /**
   * Extracts data from the Instructor objects for the bar charts.
   *
   * @param instructors The raw instructor data received from the backend.
   * @returns Data points and labels for the charts.
   */
  extractData (instructors: Instructor[]): [DataPoint[], string[]] {
    const data: DataPoint[] = []
    const instructorNames: string[] = []
    for (const instructor of instructors) {
      instructorNames.push(instructor.name)
      data.push({
        totalStudents: instructor.totalStudents,
        rate: instructor.rate
      })
    }
    return [data, instructorNames]
  }

  componentDidMount (): void {
    if (!this.initialized) {
      this.initialized = true
      this.fetchData()
    }
  }

  buildURL (): string {
    let url: string = '/instructors?'
    if (this.state.name.length !== 0) {
      url += `name=${this.state.name}&`
    }
    if (this.state.course.length !== 0) {
      url += `course=${this.state.course}&`
    }
    if (this.state.organization.length !== 0) {
      url += `organization=${this.state.organization}&`
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

  handleSortingChange (evt: any): void {
    const value = evt.target.value
    let fn: SortFuncType
    if (value === 'descending') {
      fn = sortDescending
    } else if (value === 'ascending') {
      fn = sortAscending
    } else {
      fn = sortNone
    }
    this.setState({
      sorting: fn
    })
  }

  /**
   * Creates the bar chart with the information of the total students of instructors.
   *
   * @returns A react component for display.
   */
  createChartTotalStudents (): JSX.Element {
    const records: Record[] = []
    for (let idx = 0; idx < this.state.label.length; idx++) {
      records.push([this.state.label[idx], this.state.data[idx].totalStudents])
    }
    records.sort(this.state.sorting)
    const options = {
      responsive: true,
      plugins: {
        legend: {
          position: 'top' as const
        },
        title: {
          display: true,
          text: 'Total Students Bar Chart'
        }
      }
    }
    const data = {
      labels: records.map(record => record[0]),
      datasets: [
        {
          label: 'Total Students',
          data: records.map(record => record[1]),
          backgroundColor: colors[0]
        }
      ]
    }
    return <Bar options={options} data={data} />
  }

  /**
   * Creates the bar chart with the information of the teaching rate of instructors.
   *
   * @returns A react component for display.
   */
  createChartRate (): JSX.Element {
    const records: Record[] = []
    for (let idx = 0; idx < this.state.label.length; idx++) {
      records.push([this.state.label[idx], this.state.data[idx].rate])
    }
    records.sort(this.state.sorting)
    const options = {
      responsive: true,
      plugins: {
        legend: {
          position: 'top' as const
        },
        title: {
          display: true,
          text: 'Overall Rate Bar Chart'
        }
      }
    }
    const data = {
      labels: records.map(record => record[0]),
      datasets: [
        {
          label: 'Overall Rate',
          data: records.map(record => record[1]),
          backgroundColor: colors[1]
        }
      ]
    }
    return <Bar options={options} data={data} />
  }

  render (): JSX.Element {
    return (
      <div>
        {this.createChartTotalStudents()}
        {this.createChartRate()}
        <div>Filter Options:</div>
        <form onSubmit={this.handleSubmit}>
          <p>
            <label>Instructor Name: </label>
            <input type='text' name='name' value={this.state.name} onChange={this.handleChange} />
          </p>
          <p>
            <label>Course Name: </label>
            <input type='text' name='course' value={this.state.course} onChange={this.handleChange} />
          </p>
          <p>
            <label>Organization Name: </label>
            <input type='text' name='organization' value={this.state.organization} onChange={this.handleChange} />
          </p>
          <p>
            <label>Limit: </label>
            <input type='text' name='size' value={this.state.size} onChange={this.handleChange} />
          </p>
          <p>
            <label>Sort Order: </label>
            <select onChange={this.handleSortingChange}>
              <option value='none'>None</option>
              <option value='ascending'>Ascending</option>
              <option value='descending'>Descending</option>
            </select>
          </p>
          <input type='submit' value='Submit' />
        </form>
      </div>
    )
  }
}

// define and export the "plugin" object, which implements the "VisPlugin" interface
export const plugin: VisPlugin = {
  name: 'Instructor visualization plugin',
  renderer: () => <Visualizer />
}
