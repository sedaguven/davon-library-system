const express = require('express');
const puppeteer = require('puppeteer');
const path = require('path');
const fs = require('fs');

const app = express();
const PORT = process.env.PORT || 3002;

// Serve static files
app.use(express.static(path.join(__dirname, 'public')));
app.use(express.json());

// Create screenshots directory if it doesn't exist
const screenshotsDir = path.join(__dirname, 'public', 'screenshots');
if (!fs.existsSync(screenshotsDir)) {
  fs.mkdirSync(screenshotsDir, { recursive: true });
}

// Route to take screenshots
app.post('/screenshot', async (req, res) => {
  const { url } = req.body;
  
  if (!url) {
    return res.status(400).json({ error: 'URL is required' });
  }
  
  try {
    const browser = await puppeteer.launch();
    const page = await browser.newPage();
    await page.goto(url);
    
    const timestamp = Date.now();
    const filename = `screenshot-${timestamp}.png`;
    const filepath = path.join(screenshotsDir, filename);
    
    await page.screenshot({ path: filepath, fullPage: true });
    await browser.close();
    
    res.json({ 
      success: true, 
      filename: `/screenshots/${filename}`,
      timestamp
    });
  } catch (error) {
    console.error('Screenshot error:', error);
    res.status(500).json({ error: 'Failed to capture screenshot' });
  }
});

// Home route
app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

app.listen(PORT, () => {
  console.log(`Server running on http://localhost:${PORT}`);
}); 