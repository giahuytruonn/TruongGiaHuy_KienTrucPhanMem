const dal = require('../dataAccess/dataAccessLayer');

const hookRegistry = {};

function registerRuntimeHook(eventName, pluginName, callbackFn) {
  if (!hookRegistry[eventName]) hookRegistry[eventName] = [];
  hookRegistry[eventName].push({ pluginName, callbackFn });
}

async function fireHook(eventName, data) {
  const results = [];
  const callbacks = hookRegistry[eventName] || [];
  for (const { pluginName, callbackFn } of callbacks) {
    try {
      const result = await callbackFn(data);
      results.push({ pluginName, result });
      console.log(`  🔔 Hook [${eventName}] fired → plugin: ${pluginName}`);
    } catch (err) {
      results.push({ pluginName, error: err.message });
    }
  }
  return results;
}

async function getContentTypes() {
  return dal.getAllContentTypes();
}

async function registerContentType(name, description) {
  if (!name) throw new Error('Content type name is required');
  const existing = await dal.getAllContentTypes();
  if (existing.find(t => t.name === name)) {
    throw new Error(`Content type "${name}" already exists`);
  }
  const result = await dal.createContentType(name, description);
  await fireHook('after_register_content_type', { name, description });
  return result;
}

async function getContents(typeName) {
  return dal.getAllContents(typeName);
}

async function getContent(id) {
  const content = await dal.getContentById(id);
  if (!content) throw new Error(`Content id=${id} not found`);
  return content;
}

async function createContent(typeName, title, body, status = 'draft') {
  if (!typeName || !title) throw new Error('typeName and title are required');

  await fireHook('before_save', { typeName, title, body, status });

  const result = await dal.createContent(typeName, title, body, status);

  await fireHook('after_save', result);

  if (status === 'published') {
    await fireHook('after_publish', result);
  }

  return result;
}

async function updateContent(id, title, body, status) {
  await getContent(id);
  await fireHook('before_save', { id, title, body, status });
  const result = await dal.updateContent(id, title, body, status);
  await fireHook('after_save', result);
  return result;
}

async function deleteContent(id) {
  await getContent(id);
  return dal.deleteContent(id);
}

async function getPlugins() {
  return dal.getAllPlugins();
}

async function installPlugin(name, version = '1.0.0') {
  if (!name) throw new Error('Plugin name is required');
  const existing = await dal.getPluginByName(name);
  if (existing) throw new Error(`Plugin "${name}" already installed`);
  const result = await dal.installPlugin(name, version);
  console.log(`📦 Plugin installed: ${name}`);
  return result;
}

async function activatePlugin(name) {
  const plugin = await dal.getPluginByName(name);
  if (!plugin) throw new Error(`Plugin "${name}" not found`);
  if (plugin.status === 'active') throw new Error(`Plugin "${name}" already active`);

  const result = await dal.updatePluginStatus(name, 'active');

  registerRuntimeHook('after_save', name, async (data) => {
    return `[${name}] processed after_save for: ${data.title || data.id}`;
  });

  await dal.registerHook('after_save', name, 'auto-registered on activate');
  console.log(`✅ Plugin activated: ${name}`);
  return result;
}

async function deactivatePlugin(name) {
  const plugin = await dal.getPluginByName(name);
  if (!plugin) throw new Error(`Plugin "${name}" not found`);
  if (plugin.status !== 'active') throw new Error(`Plugin "${name}" is not active`);

  // Xoá khỏi runtime hook registry
  for (const event in hookRegistry) {
    hookRegistry[event] = hookRegistry[event].filter(h => h.pluginName !== name);
  }

  const result = await dal.updatePluginStatus(name, 'inactive');
  console.log(`⏸  Plugin deactivated: ${name}`);
  return result;
}

async function uninstallPlugin(name) {
  const plugin = await dal.getPluginByName(name);
  if (!plugin) throw new Error(`Plugin "${name}" not found`);
  if (plugin.status === 'active') throw new Error(`Deactivate "${name}" before uninstalling`);

  const result = await dal.uninstallPlugin(name);
  console.log(`🗑  Plugin uninstalled: ${name}`);
  return result;
}

async function registerHook(eventName, pluginName, callbackInfo) {
  if (!eventName || !pluginName) throw new Error('eventName and pluginName are required');
  return dal.registerHook(eventName, pluginName, callbackInfo);
}

async function getHooks(eventName) {
  if (eventName) return dal.getHooksByEvent(eventName);
  return dal.getAllHooks();
}

async function removeHook(eventName, pluginName) {
  if (hookRegistry[eventName]) {
    hookRegistry[eventName] = hookRegistry[eventName].filter(h => h.pluginName !== pluginName);
  }
  return dal.removeHook(eventName, pluginName);
}

module.exports = {
  registerRuntimeHook, fireHook,
  getContentTypes, registerContentType,
  getContents, getContent, createContent, updateContent, deleteContent,
  getPlugins, installPlugin, activatePlugin, deactivatePlugin, uninstallPlugin,
  registerHook, getHooks, removeHook,
};
