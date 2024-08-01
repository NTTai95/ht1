/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import entity.LoaiMon;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utils.XJdbc;

/**
 *
 * @author admin
 */
public class LoaiMonDAO extends SysDAO<LoaiMon, String> {

    String INSERT_SQL = "Insert LoaiMon(MaLoai,TenLoai) Values(?,?)";
    String UPDATE_SQL = "UPDATE LoaiMon SET TenLoai = ? WHERE MaLoai like ?";
    String DELETE_SQL = "DELETE FROM LoaiMon WHERE MaLoai like ?";
    String SELECT_BY_ID = "Select * from LoaiMon where MaLoai like ?";
    String SELECT_ALL = "SELECT * FROM LoaiMon";
    String COUNT_ROW = "SELECT COUNT(*) FROM LoaiMon";

    @Override
    public void insert(LoaiMon entity) {
        XJdbc.executeUpdate(INSERT_SQL,
                entity.getMaLoai(),
                entity.getTenLoai());
    }

    @Override
    public void update(LoaiMon entity) {
        XJdbc.executeUpdate(UPDATE_SQL,
                entity.getTenLoai(),
                entity.getMaLoai());
    }

    @Override
    public void delete(String id) {
        XJdbc.executeUpdate(DELETE_SQL, id);
    }

    @Override
    public LoaiMon selectById(String id) {
        List<LoaiMon> list = this.selectBySQL(SELECT_BY_ID, id);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<LoaiMon> selectAll() {
        return this.selectBySQL(SELECT_ALL);
    }

    @Override
    protected List<LoaiMon> selectBySQL(String sql, Object... args) {
        List<LoaiMon> list = new ArrayList<>();
        try {
            ResultSet rs = XJdbc.executeQuery(sql, args);
            while (rs.next()) {
                LoaiMon entity = new LoaiMon();
                entity.setMaLoai(rs.getString(1));
                entity.setTenLoai(rs.getString(2));
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
