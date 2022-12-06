/**
 * An example visualization plugin.
 */

import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js'
import React from 'react'
import { Line } from 'react-chartjs-2'
import { Course, VisPlugin } from '../Analysis'

interface VisualizerState {
  courses: Course[]
}

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
)

class Visualizer extends React.Component<{}, VisualizerState> {
  private initialized: boolean = false

  constructor (props: {}) {
    super(props)
    this.state = {
      courses: []
    }
  }

  componentDidMount (): void {
    if (!this.initialized) {
      this.initialized = true
      void fetch('plugin?i=0')
        .then(async resp => await resp.json())
        .then(json => this.setState({
          courses: json.courses
        }))
    }
  }

  /**
   * Returns a React component, which displays a line chart.
   *
   * @returns The React component.
   */
  render (): JSX.Element {
    const options = {
      responsive: true,
      plugins: {
        legend: {
          position: 'top' as const
        },
        title: {
          display: true,
          text: 'Chart.js Line Chart'
        }
      }
    }

    const labels = ['January', 'February', 'March', 'April', 'May', 'June', 'July']
    if (this.state.courses.length > 0) {
      labels.push(this.state.courses[0].name)
    }

    const data = {
      labels,
      datasets: [
        {
          label: 'Dataset 1',
          data: labels.map(() => 300),
          borderColor: 'rgb(255, 99, 132)',
          backgroundColor: 'rgba(255, 99, 132, 0.5)'
        },
        {
          label: 'Dataset 2',
          data: labels.map(() => 500),
          borderColor: 'rgb(53, 162, 235)',
          backgroundColor: 'rgba(53, 162, 235, 0.5)'
        }
      ]
    }
    return (
      <Line options={options} data={data} />
    )
  }
}

// define and export the "plugin" object, which implements the "VisPlugin" interface
export const plugin: VisPlugin = {
  name: 'Example Visualizer',
  renderer: () => <Visualizer />
}
