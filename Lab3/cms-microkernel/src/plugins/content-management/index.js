const core = require('../../core/CMSCore');

const ContentManagementPlugin = {
  name: 'content-management',
  version: '1.0.0',

  activate() {
    const db = core.getDB();
    console.log('✅ ContentManagement plugin: activated');

    core.on('before_save', this.name, async (data) => {
      if (!data.title || data.title.trim() === '') {
        throw new Error('Title cannot be empty');
      }
      return 'content validated';
    });

    this.api = {
      async getContentTypes() {
        const [rows] = await db.execute('SELECT * FROM content_types ORDER BY created_at DESC');
        return rows;
      },

      async registerContentType(name, description) {
        if (!name) throw new Error('name is required');
        const [exists] = await db.execute('SELECT id FROM content_types WHERE name = ?', [name]);
        if (exists.length) throw new Error(`Content type "${name}" already exists`);
        const [r] = await db.execute(
          'INSERT INTO content_types (name, description) VALUES (?, ?)',
          [name, description]
        );
        const result = { id: r.insertId, name, description };
        await core.emit('after_register_content_type', result);
        return result;
      },

      async getContents(typeName) {
        if (typeName) {
          const [rows] = await db.execute(
            'SELECT * FROM contents WHERE type_name = ? ORDER BY created_at DESC',
            [typeName]
          );
          return rows;
        }
        const [rows] = await db.execute('SELECT * FROM contents ORDER BY created_at DESC');
        return rows;
      },

      async getContentById(id) {
        const [rows] = await db.execute('SELECT * FROM contents WHERE id = ?', [id]);
        if (!rows.length) throw new Error(`Content id=${id} not found`);
        return rows[0];
      },

      async createContent(typeName, title, body, status = 'draft') {
        if (!typeName || !title) throw new Error('typeName and title are required');
        await core.emit('before_save', { typeName, title, body, status });
        const [r] = await db.execute(
          'INSERT INTO contents (type_name, title, body, status) VALUES (?, ?, ?, ?)',
          [typeName, title, body, status]
        );
        const result = { id: r.insertId, typeName, title, body, status };
        await core.emit('after_save', result);
        if (status === 'published') await core.emit('after_publish', result);
        return result;
      },

      async updateContent(id, title, body, status) {
        await core.emit('before_save', { id, title, body, status });
        await db.execute(
          'UPDATE contents SET title=?, body=?, status=? WHERE id=?',
          [title, body, status, id]
        );
        const [rows] = await db.execute('SELECT * FROM contents WHERE id=?', [id]);
        await core.emit('after_save', rows[0]);
        return rows[0];
      },

      async deleteContent(id) {
        await db.execute('DELETE FROM contents WHERE id=?', [id]);
        return { deleted: true, id };
      },
    };
  },

  deactivate() {
    core.offPlugin(this.name);
    this.api = null;
    console.log('⏸  ContentManagement plugin: deactivated');
  },
};

module.exports = ContentManagementPlugin;
