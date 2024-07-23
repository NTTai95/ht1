/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import entity.BanAn;
import entity.MonAn;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utils.XJdbc;

/**
 *
 * @author admin
 */
public class BanAnDAO extends SysDAO<BanAn, String>{

    String INSERT_SQL = "Insert BanAn(MaB) Value(?)";
    String DELETE_SQL = "DELETE FROM BanAn WHERE BanAn like ?";
    String SELECT_BY_ID = "Select * from BanAn where BanAn like ?";
    String SELECT_ALL = "SELECT * FROM BanAn";
    
    @Override
    public void insert(BanAn entity) {
        XJdbc.executeUpdate(INSERT_SQL, entity.getMaB());
    }

    @Override
    public void update(BanAn entity) {
        
    }

    @Override
    public void delete(String id) {
        XJdbc.executeUpdate(DELETE_SQL, id);
    }

    @Override
    public BanAn selectById(String id) {
        List<BanAn> list = this.selectBySQL(SELECT_BY_ID, id);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<BanAn> selectAll() {
        return this.selectBySQL(SELECT_ALL);
    }

    @Override
    protected List<BanAn> selectBySQL(String sql, Object... args) {
        List<BanAn> list = new ArrayList<>();
        try {
            ResultSet rs = XJdbc.executeQuery(sql, args);
            while (rs.next()) {
                BanAn entity = new BanAn();
                entity.setMaB(rs.getString(1));
                list.add(entity);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
}
