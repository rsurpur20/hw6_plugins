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

 /*

 IDEA: pie chart
 basically just have a pie chart where the total is the total number of
 workload hours, and each slice of the pie is a class
 allows you to visualize different course workload in relation to another

 const config = {
    type: 'pie',
    data: data,
    options: {
      responsive: true,
      plugins: {
        legend: {
          position: 'top',
        },
        title: {
          display: true,
          text: 'Chart.js Pie Chart'
        }
      }
    },
  };
  */