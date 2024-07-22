/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import entity.BanAn;
import java.util.List;

/**
 *
 * @author admin
 */
public class BanAnDAO extends SysDAO<BanAn, String>{

    @Override
    public void insert(BanAn entity) {
        
    }

    @Override
    public void update(BanAn entity) {
        
    }

    @Override
    public void delete(String id) {
    }

    @Override
    public BanAn selectById(String id) {
        return null;
    }

    @Override
    public List<BanAn> selectAll() {
        return null;
    }

    @Override
    protected List<BanAn> selectBySQL(String sql, Object... args) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
