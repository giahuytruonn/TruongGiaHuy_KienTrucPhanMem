const { pool } = require('../config/database');

class CMSCore {
  constructor() {
    this.plugins = new Map();
    this.hookRegistry = new Map();
    console.log('🔧 CMS Core initialized');
  }

  register(plugin) {
    if (!plugin.name) throw new Error('Plugin must have a name');
    if (typeof plugin.activate !== 'function') throw new Error('Plugin must implement activate()');
    if (typeof plugin.deactivate !== 'function') throw new Error('Plugin must implement deactivate()');

    plugin.status = 'registered';
    this.plugins.set(plugin.name, plugin);
    console.log(`📥 Core: plugin registered → ${plugin.name}`);
  }

  getPlugin(name) {
    const p = this.plugins.get(name);
    if (!p) throw new Error(`Plugin "${name}" not registered in core`);
    return p;
  }

  listPlugins() {
    return Array.from(this.plugins.values()).map(p => ({
      name: p.name,
      version: p.version,
      status: p.status,
    }));
  }

  on(eventName, pluginName, fn) {
    if (!this.hookRegistry.has(eventName)) {
      this.hookRegistry.set(eventName, []);
    }
    this.hookRegistry.get(eventName).push({ pluginName, fn });
  }

  async emit(eventName, data) {
    const handlers = this.hookRegistry.get(eventName) || [];
    const results = [];
    for (const { pluginName, fn } of handlers) {
      try {
        const result = await fn(data);
        results.push({ pluginName, result });
        console.log(`  🔔 Core emit [${eventName}] → ${pluginName}`);
      } catch (err) {
        throw err;
      }
    }
    return results;
  }

  offPlugin(pluginName) {
    for (const [event, handlers] of this.hookRegistry.entries()) {
      this.hookRegistry.set(event, handlers.filter(h => h.pluginName !== pluginName));
    }
  }

  getDB() {
    return pool;
  }
}

const core = new CMSCore();
module.exports = core;
