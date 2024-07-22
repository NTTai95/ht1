/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;

/**
 *
 * @author admin
 */
abstract public class SysDAO<Entity, Key> {
    
    abstract public void insert(Entity entity);

    abstract public void update(Entity entity);

    abstract public void delete(Key id);

    abstract public Entity selectById(Key id);

    abstract public List<Entity> selectAll();
    
    abstract protected List<Entity> selectBySQL(String sql, Object... args);
}
