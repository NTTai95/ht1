/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import entity.KhachHang;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utils.XJdbc;

/**
 *
 * @author admin
 */
public class KhachHangDAO extends SysDAO<KhachHang, String>{
    String INSERT_SQL = "Insert KhachHang(MaKH, TenKH, SDT) Values (?,?,?)";
    String UPDATE_SQL = "UPDATE KhachHang SET TenKH = ?, SDT = ? WHERE MaKH like ?";
    String DELETE_SQL = "DELETE FROM KhachHang WHERE MaKh like ?";
    String SELECT_BY_ID = "Select * from KhachHang where MaKH like ?";
    String SELECT_ALL = "SELECT * FROM KhachHang";
    String COUNT_ROW = "SELECT COUNT(*) FROM KhachHang";

    @Override
    public void insert(KhachHang entity) {
        XJdbc.executeUpdate(INSERT_SQL, 
                entity.getMaKH(),
                entity.getTenKH(),
                entity.getSDT());
    }

    @Override
    public void update(KhachHang entity) {
         XJdbc.executeUpdate(UPDATE_SQL,
                entity.getMaKH(),
                entity.getTenKH(),
                entity.getSDT());
    }

    @Override
    public void delete(String id) {
        XJdbc.executeUpdate(DELETE_SQL, id);
    }

    @Override
    public KhachHang selectById(String id) {
        List<KhachHang> list = this.selectBySQL(SELECT_BY_ID, id);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<KhachHang> selectAll() {
        return this.selectBySQL(SELECT_ALL);
    }

    @Override
    protected List<KhachHang> selectBySQL(String sql, Object... args) {
        List<KhachHang> list = new ArrayList<>();
        try {
            ResultSet rs = XJdbc.executeQuery(sql, args);
            while (rs.next()) {
                KhachHang entity = new KhachHang();
                entity.setMaKH(rs.getString(1));
                entity.setTenKH(rs.getString(2));
                entity.setSDT(rs.getString(3));
                list.add(entity);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public int getCountRow() {
        try {
            ResultSet rs = XJdbc.executeQuery(COUNT_ROW);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
