const express = require('express');
const router = express.Router();
const core = require('./core/CMSCore');

function ok(res, data, message = 'Success') {
  res.json({ success: true, message, data });
}
function fail(res, err, status = 400) {
  res.status(status).json({ success: false, message: err.message });
}

function getAPI(pluginName) {
  const p = core.getPlugin(pluginName);
  if (!p || !p.api) throw new Error(`Plugin "${pluginName}" chưa được activate hoặc không tồn tại`);
  return p.api;
}

router.get('/content-types', async (req, res) => {
  try { ok(res, await getAPI('content-management').getContentTypes()); }
  catch (e) { fail(res, e); }
});

router.post('/content-types', async (req, res) => {
  try {
    const { name, description } = req.body;
    ok(res, await getAPI('content-management').registerContentType(name, description), 'Registered');
  } catch (e) { fail(res, e); }
});

router.get('/contents', async (req, res) => {
  try { ok(res, await getAPI('content-management').getContents(req.query.type)); }
  catch (e) { fail(res, e); }
});

router.get('/contents/:id', async (req, res) => {
  try { ok(res, await getAPI('content-management').getContentById(req.params.id)); }
  catch (e) { fail(res, e, 404); }
});

router.post('/contents', async (req, res) => {
  try {
    const { typeName, title, body, status } = req.body;
    ok(res, await getAPI('content-management').createContent(typeName, title, body, status), 'Created');
  } catch (e) { fail(res, e); }
});

router.put('/contents/:id', async (req, res) => {
  try {
    const { title, body, status } = req.body;
    ok(res, await getAPI('content-management').updateContent(req.params.id, title, body, status), 'Updated');
  } catch (e) { fail(res, e); }
});

router.delete('/contents/:id', async (req, res) => {
  try { ok(res, await getAPI('content-management').deleteContent(req.params.id), 'Deleted'); }
  catch (e) { fail(res, e); }
});

router.get('/plugins', async (req, res) => {
  try { ok(res, await getAPI('lifecycle').getPlugins()); }
  catch (e) { fail(res, e); }
});

router.post('/plugins/install', async (req, res) => {
  try {
    const { name, version } = req.body;
    ok(res, await getAPI('lifecycle').installPlugin(name, version), 'Installed');
  } catch (e) { fail(res, e); }
});

router.post('/plugins/:name/activate', async (req, res) => {
  try { ok(res, await getAPI('lifecycle').activatePlugin(req.params.name), 'Activated'); }
  catch (e) { fail(res, e); }
});

router.post('/plugins/:name/deactivate', async (req, res) => {
  try { ok(res, await getAPI('lifecycle').deactivatePlugin(req.params.name), 'Deactivated'); }
  catch (e) { fail(res, e); }
});

router.delete('/plugins/:name', async (req, res) => {
  try { ok(res, await getAPI('lifecycle').uninstallPlugin(req.params.name), 'Uninstalled'); }
  catch (e) { fail(res, e); }
});

router.get('/hooks', async (req, res) => {
  try { ok(res, await getAPI('hook-system').getHooks(req.query.event)); }
  catch (e) { fail(res, e); }
});

router.post('/hooks/register', async (req, res) => {
  try {
    const { eventName, pluginName, callbackInfo } = req.body;
    ok(res, await getAPI('hook-system').registerHook(eventName, pluginName, callbackInfo), 'Registered');
  } catch (e) { fail(res, e); }
});

router.delete('/hooks', async (req, res) => {
  try {
    const { eventName, pluginName } = req.body;
    ok(res, await getAPI('hook-system').removeHook(eventName, pluginName), 'Removed');
  } catch (e) { fail(res, e); }
});

router.post('/hooks/fire', async (req, res) => {
  try {
    const { eventName, data } = req.body;
    ok(res, await getAPI('hook-system').fireEvent(eventName, data), 'Event fired');
  } catch (e) { fail(res, e); }
});

module.exports = router;
