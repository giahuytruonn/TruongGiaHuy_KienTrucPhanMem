const { pool } = require('../../config/database');

async function getAllContentTypes() {
  const [rows] = await pool.execute('SELECT * FROM content_types ORDER BY created_at DESC');
  return rows;
}

async function createContentType(name, description) {
  const [result] = await pool.execute(
    'INSERT INTO content_types (name, description) VALUES (?, ?)',
    [name, description]
  );
  return { id: result.insertId, name, description };
}

async function getAllContents(typeName = null) {
  if (typeName) {
    const [rows] = await pool.execute(
      'SELECT * FROM contents WHERE type_name = ? ORDER BY created_at DESC',
      [typeName]
    );
    return rows;
  }
  const [rows] = await pool.execute('SELECT * FROM contents ORDER BY created_at DESC');
  return rows;
}

async function getContentById(id) {
  const [rows] = await pool.execute('SELECT * FROM contents WHERE id = ?', [id]);
  return rows[0] || null;
}

async function createContent(typeName, title, body, status = 'draft') {
  const [result] = await pool.execute(
    'INSERT INTO contents (type_name, title, body, status) VALUES (?, ?, ?, ?)',
    [typeName, title, body, status]
  );
  return { id: result.insertId, typeName, title, body, status };
}

async function updateContent(id, title, body, status) {
  await pool.execute(
    'UPDATE contents SET title = ?, body = ?, status = ? WHERE id = ?',
    [title, body, status, id]
  );
  return getContentById(id);
}

async function deleteContent(id) {
  await pool.execute('DELETE FROM contents WHERE id = ?', [id]);
  return { deleted: true, id };
}

async function getAllPlugins() {
  const [rows] = await pool.execute('SELECT * FROM plugins ORDER BY created_at DESC');
  return rows;
}

async function getPluginByName(name) {
  const [rows] = await pool.execute('SELECT * FROM plugins WHERE name = ?', [name]);
  return rows[0] || null;
}

async function installPlugin(name, version = '1.0.0') {
  const [result] = await pool.execute(
    'INSERT INTO plugins (name, version, status) VALUES (?, ?, "installed")',
    [name, version]
  );
  return { id: result.insertId, name, version, status: 'installed' };
}

async function updatePluginStatus(name, status) {
  await pool.execute('UPDATE plugins SET status = ? WHERE name = ?', [status, name]);
  return getPluginByName(name);
}

async function uninstallPlugin(name) {
  await pool.execute('DELETE FROM plugins WHERE name = ?', [name]);
  await pool.execute('DELETE FROM hooks WHERE plugin_name = ?', [name]);
  return { uninstalled: true, name };
}

async function registerHook(eventName, pluginName, callbackInfo) {
  const [result] = await pool.execute(
    'INSERT INTO hooks (event_name, plugin_name, callback_info) VALUES (?, ?, ?)',
    [eventName, pluginName, callbackInfo]
  );
  return { id: result.insertId, eventName, pluginName, callbackInfo };
}

async function getHooksByEvent(eventName) {
  const [rows] = await pool.execute(
    'SELECT * FROM hooks WHERE event_name = ? ORDER BY created_at ASC',
    [eventName]
  );
  return rows;
}

async function getAllHooks() {
  const [rows] = await pool.execute('SELECT * FROM hooks ORDER BY event_name');
  return rows;
}

async function removeHook(eventName, pluginName) {
  await pool.execute(
    'DELETE FROM hooks WHERE event_name = ? AND plugin_name = ?',
    [eventName, pluginName]
  );
  return { removed: true, eventName, pluginName };
}

module.exports = {
  getAllContentTypes, createContentType,
  getAllContents, getContentById, createContent, updateContent, deleteContent,
  getAllPlugins, getPluginByName, installPlugin, updatePluginStatus, uninstallPlugin,
  registerHook, getHooksByEvent, getAllHooks, removeHook,
};
