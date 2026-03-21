require('dotenv').config();
const express = require('express');
const path = require('path');
const { initDB } = require('./config/database');
const routes = require('./layers/presentation/presentationLayer');

const app = express();
app.use(express.json());

app.use((req, res, next) => {
  res.header('Access-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Methods', 'GET,POST,PUT,DELETE,OPTIONS');
  res.header('Access-Control-Allow-Headers', 'Content-Type');
  if (req.method === 'OPTIONS') return res.sendStatus(200);
  next();
});

app.use(express.static(path.join(__dirname, '../../')));

app.use('/api', routes);

app.get('/', (req, res) => {
  res.json({
    project: 'CMS - Layer Architecture',
    architecture: 'Layer (4 tầng)',
    layers: [
      'Tầng 1: Presentation (Express Routes)',
      'Tầng 2: Business Logic (Validate + Hook)',
      'Tầng 3: Data Access (MySQL queries)',
      'Tầng 4: Database (MySQL)',
    ],
    features: ['Content Management', 'Hook System', 'Plugin Lifecycle'],
    endpoints: {
      contentTypes: 'GET/POST /api/content-types',
      contents: 'GET/POST/PUT/DELETE /api/contents',
      plugins: 'GET /api/plugins | POST /api/plugins/install | POST /api/plugins/:name/activate',
      hooks: 'GET/POST/DELETE /api/hooks',
    },
  });
});

const PORT = process.env.PORT || 3000;

initDB()
  .then(() => {
    app.listen(PORT, () => {
      console.log(`\n🚀 CMS Layer Architecture running on http://localhost:${PORT}`);
      console.log(`📖 API docs: http://localhost:${PORT}/\n`);
    });
  })
  .catch(err => {
    console.error('❌ Failed to init DB:', err.message);
    process.exit(1);
  });
