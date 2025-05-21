# Puppeteer Landing Page

A demo landing page that showcases Puppeteer capabilities through a screenshot generation tool.

## Features

- Modern, responsive landing page built with Bootstrap 5
- Interactive demo that takes screenshots of any URL using Puppeteer
- Node.js backend powered by Express
- Real-time screenshot generation and display

## Prerequisites

- Node.js (v14 or higher)
- npm or yarn

## Installation

1. Clone this repository
2. Navigate to the project directory:
   ```
   cd puppeteer-landing-page
   ```
3. Install dependencies:
   ```
   npm install
   ```

## Usage

1. Start the server:
   ```
   npm start
   ```
2. Open your browser and navigate to http://localhost:3000
3. Enter a URL in the form and click "Capture Screenshot"
4. View the generated screenshot directly on the page

## How It Works

The application uses Puppeteer to launch a headless Chrome browser, navigate to the provided URL, and take a screenshot. The screenshot is saved in the `public/screenshots` directory and then displayed on the webpage.

Key technologies used:
- **Puppeteer**: For browser automation and screenshot capture
- **Express**: For serving the web application and handling API requests
- **Bootstrap 5**: For responsive design and UI components
- **Fetch API**: For AJAX requests

## License

MIT 