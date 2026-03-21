const express = require('express');
const router = express.Router();
const bll = require('../business/businessLayer');

function ok(res, data, message = 'Success') {
  res.json({ success: true, message, data });
}
function fail(res, err, status = 400) {
  res.status(status).json({ success: false, message: err.message });
}

router.get('/content-types', async (req, res) => {
  try { ok(res, await bll.getContentTypes()); }
  catch (e) { fail(res, e); }
});

router.post('/content-types', async (req, res) => {
  try {
    const { name, description } = req.body;
    ok(res, await bll.registerContentType(name, description), 'Content type registered');
  } catch (e) { fail(res, e); }
});

router.get('/contents', async (req, res) => {
  try { ok(res, await bll.getContents(req.query.type)); }
  catch (e) { fail(res, e); }
});

router.get('/contents/:id', async (req, res) => {
  try { ok(res, await bll.getContent(req.params.id)); }
  catch (e) { fail(res, e, 404); }
});

router.post('/contents', async (req, res) => {
  try {
    const { typeName, title, body, status } = req.body;
    ok(res, await bll.createContent(typeName, title, body, status), 'Content created');
  } catch (e) { fail(res, e); }
});

router.put('/contents/:id', async (req, res) => {
  try {
    const { title, body, status } = req.body;
    ok(res, await bll.updateContent(req.params.id, title, body, status), 'Content updated');
  } catch (e) { fail(res, e); }
});

router.delete('/contents/:id', async (req, res) => {
  try { ok(res, await bll.deleteContent(req.params.id), 'Content deleted'); }
  catch (e) { fail(res, e); }
});

router.get('/plugins', async (req, res) => {
  try { ok(res, await bll.getPlugins()); }
  catch (e) { fail(res, e); }
});

router.post('/plugins/install', async (req, res) => {
  try {
    const { name, version } = req.body;
    ok(res, await bll.installPlugin(name, version), 'Plugin installed');
  } catch (e) { fail(res, e); }
});

router.post('/plugins/:name/activate', async (req, res) => {
  try { ok(res, await bll.activatePlugin(req.params.name), 'Plugin activated'); }
  catch (e) { fail(res, e); }
});

router.post('/plugins/:name/deactivate', async (req, res) => {
  try { ok(res, await bll.deactivatePlugin(req.params.name), 'Plugin deactivated'); }
  catch (e) { fail(res, e); }
});

router.delete('/plugins/:name', async (req, res) => {
  try { ok(res, await bll.uninstallPlugin(req.params.name), 'Plugin uninstalled'); }
  catch (e) { fail(res, e); }
});

router.get('/hooks', async (req, res) => {
  try { ok(res, await bll.getHooks(req.query.event)); }
  catch (e) { fail(res, e); }
});

router.post('/hooks/register', async (req, res) => {
  try {
    const { eventName, pluginName, callbackInfo } = req.body;
    ok(res, await bll.registerHook(eventName, pluginName, callbackInfo), 'Hook registered');
  } catch (e) { fail(res, e); }
});

router.delete('/hooks', async (req, res) => {
  try {
    const { eventName, pluginName } = req.body;
    ok(res, await bll.removeHook(eventName, pluginName), 'Hook removed');
  } catch (e) { fail(res, e); }
});

module.exports = router;
