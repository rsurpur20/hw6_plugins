import React from 'react'

import './App.css'
import { AnalysisResult } from './Analysis'
import raw from './plugins.txt'

// the directory containing the visualization plugins
const visPluginDirectory: string = './plugin/'

/**
 * Using generics to specify the type of props and state.
 * props and state is a special field in a React component.
 * React will keep track of the value of props and state.
 * Any time there's a change to their values, React will
 * automatically update (not fully re-render) the HTML needed.
 *
 * props and state are similar in the sense that they manage
 * the data of this component. A change to their values will
 * cause the view (HTML) to change accordingly.
 *
 * Usually, props is passed and changed by the parent component;
 * state is the internal value of the component and managed by
 * the component itself.
 */
class App extends React.Component<{}, AnalysisResult> {
  private initialized: boolean = false

  /**
   * @param props has type Props
   */
  constructor (props: {}) {
    super(props)
    /**
     * state has type AnalysisResult as specified in the class inheritance.
     */
    this.state = {
      status: 'Please select a data plugin',
      dataPlugins: [],
      chosenVis: -1,
      loadedDataPlugins: [],
      analyzedCourses: [],
      currentVisPlugin: {
        name: '',
        renderer: () => <div />
      },
      visPlugins: []
    }
  }

  /**
   * Imports a visualization plugin from the specified path.
   *
   * @param path The path to the plugin
   */
  async importPlugin (path: string): Promise<void> {
    // NB: this is magic. The "" is necessary to make the import succeed
    const plugin = await import('' + path)
    this.state.visPlugins.push(plugin.plugin)
  }

  /**
   * Starts and initializes the frontend. Should be called only once.
   */
  async start (): Promise<void> {
    // import visualization plugins dynamically according to the config file
    const paths = await fetch(raw)
      .then(async r => await r.text())
      .then(text => text.split('\n'))
    for (const path of paths) {
      await this.importPlugin(visPluginDirectory + path)
    }
    // initialize the react state
    const response = await fetch('start')
    const json = await response.json()
    // NB: setState should be the last step to make sure the HTML page is refreshed
    this.setState({
      dataPlugins: json.plugins
    })
  }

  /**
   * Creates an instruction block on the frontend.
   *
   * @returns The instruction block element.
   */
  createInstructions (): JSX.Element {
    let textLeft = ''
    textLeft += 'Instructions:\n'
    textLeft += '1. Select a data plugin from the list on the bottom left\n'
    textLeft += '2. Wait for the plugin to finish loading\n'
    textLeft += '3. Select a visualization plugin from the list on the bottom right\n'
    let textRight = ''
    textRight += 'Notes:\n'
    textRight += '- You may switch between visualization plugins without loading the data plugin '
    textRight += 'again\n'
    textRight += '- You may select multiple data plugins at the same time by clicking on other '
    textRight += 'data plugins\n'
    textRight += '- The Udemy data plugin requires about 1 minute to load\n'
    return (
      <div id='instructions' className='box'>
        <div>{textLeft}</div>
        <div>{textRight}</div>
      </div>
    )
  }

  /**
   * Creates an status block on the frontend.
   *
   * @returns The status block element.
   */
  createStatus (): JSX.Element {
    let loadedPlugins = this.state.loadedDataPlugins.join(', ')
    if (loadedPlugins === '') {
      loadedPlugins = 'None'
    }
    let text = ''
    text += `Loaded data plugins: ${loadedPlugins}\n`
    text += this.state.status
    return (
      <div id='status' className='box'>
        {text}
      </div>
    )
  }

  /**
   * Creates data plugin bottons on the frontend.
   *
   * @returns Data plugin botton elements.
   */
  createDataPluginBottons (): JSX.Element {
    if (this.state.dataPlugins.length === 0) {
      return (
        <span>No plugin loaded</span>
      )
    } else {
      return (
        <div>
          {
            this.state.dataPlugins.map((plugin, index) =>
              this.createDataPlugin(plugin.name, index))
          }
        </div>
      )
    }
  }

  /**
   * Creates the data plugin internal elements.
   *
   * @param name The name of the plugin.
   * @param index The index of the plugin.
   * @returns The data plugin element.
   */
  createDataPlugin (name: string, index: number): JSX.Element {
    return (
      <div key={index}>
        <button
          disabled={this.state.loadedDataPlugins.includes(name)}
          onClick={this.chooseDataPlugin(index)}
        >
          {name}
        </button>
      </div>
    )
  }

  /**
   * Handles the click event of data plugins.
   *
   * @param i The index of the chosen plugin.
   * @returns A react MouseEventHandler.
   */
  chooseDataPlugin (i: number): React.MouseEventHandler {
    // eslint-disable-next-line @typescript-eslint/no-misused-promises
    return async (e) => {
      e.preventDefault()
      const loadPluginPromise = fetch(`/plugin?i=${i}`)
      this.setState({
        status: 'Loading... Please wait'
      })
      await loadPluginPromise
        .then(async resp => await resp.json())
        .then(json => {
          const loaded = this.state.loadedDataPlugins
          loaded.push(this.state.dataPlugins[i].name)
          this.setState({
            dataPlugins: json.plugins,
            loadedDataPlugins: loaded,
            status: 'Done loading plugin!',
            analyzedCourses: json.analyzedCourses
          })
        })
    }
  }

  /**
   * Creates visualization plugin bottons on the frontend.
   *
   * @returns Visualization plugin botton elements.
   */
  createVisPluginBottons (): JSX.Element {
    if (this.state.visPlugins.length === 0) {
      return (
        <span>No plugin loaded</span>
      )
    } else {
      return (
        <div>
          {this.state.visPlugins.map((plugin, index) => this.createVisPlugin(plugin.name, index))}
        </div>
      )
    }
  }

  /**
   * Creates the visualization plugin internal elements.
   *
   * @param name The name of the plugin.
   * @param index The index of the plugin.
   * @returns The visualization plugin element.
   */
  createVisPlugin (name: string, index: number): JSX.Element {
    return (
      <div key={index}>
        <button
          disabled={this.state.chosenVis === index}
          onClick={this.chooseVisPlugin(index)}
        >
          {name}
        </button>
      </div>
    )
  }

  /**
   * Handles the click event of visualization plugins.
   *
   * @param i The index of the chosen plugin.
   * @returns A react MouseEventHandler.
   */
  chooseVisPlugin (i: number): React.MouseEventHandler {
    // eslint-disable-next-line @typescript-eslint/no-misused-promises
    return async (e) => {
      e.preventDefault()
      this.setState({
        status: `Current visualization plugin: ${this.state.visPlugins[i].name}`,
        chosenVis: i,
        currentVisPlugin: this.state.visPlugins[i]
      })
    }
  }

  /**
   * This function will call after the HTML is rendered.
   * We update the initial state by creating a new analysis.
   * @see https://reactjs.org/docs/react-component.html#componentdidmount
   */
  componentDidMount (): void {
    /**
     * setState in DidMount() will cause it to render twice which may cause
     * this function to be invoked twice. Use initialized to avoid that.
     */
    if (!this.initialized) {
      void this.start()
      this.initialized = true
    }
  }

  /**
   * The only method you must define in a React.Component subclass.
   * @returns the React element via JSX.
   * @see https://reactjs.org/docs/react-component.html
   */
  render (): React.ReactNode {
    /**
     * We use JSX to define the template. An advantage of JSX is that you
     * can treat HTML elements as code.
     * @see https://reactjs.org/docs/introducing-jsx.html
     */
    return (
      <div>
        <div id='kanban' className='box'>Course Analysis Framework</div>
        {this.createInstructions()}
        {this.createStatus()}
        <div id='board'>
          {this.state.chosenVis !== -1 ? this.state.currentVisPlugin.renderer() : ''}
        </div>
        <div className='row'>
          <div className='column'>
            <div>Data Plugins</div>
            <div>
              {this.createDataPluginBottons()}
            </div>
          </div>
          <div className='column'>
            <div>Visualization Plugins</div>
            <div>
              {this.createVisPluginBottons()}
            </div>
          </div>
        </div>
      </div>
    )
  }
}

export default App
