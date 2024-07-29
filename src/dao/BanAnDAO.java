/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import entity.BanAn;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utils.XJdbc;

/**
 *
 * @author admin
 */
public class BanAnDAO extends SysDAO<BanAn, String> {

    String INSERT_SQL = "Insert BanAn(MaB, TrangThai, ViTri, GhiChu) Values(?,?,?,?)";
    String DELETE_SQL = "DELETE FROM BanAn WHERE BanAn like ?";
    String UPDATA_SQL = "UPDATE BanAn SET TrangThai = ?, ViTri = ?, GhiChu = ? WHERE MaB like ?";
    String SELECT_BY_ID = "Select * from BanAn where MaB like ?";
    String SELECT_ALL = "SELECT * FROM BanAn";
    String COUNT_ROW = "SELECT COUNT(*) FROM BanAn";
    String SELECT_TRANGTHAI = "SELECT * from BanAn WHERE TrangThai like ?";

    @Override
    public void insert(BanAn entity) {
        XJdbc.executeUpdate(INSERT_SQL,
                entity.getMaB(),
                entity.isTrangThai(),
                entity.getViTri(),
                entity.getGhiChu());
    }

    @Override
    public void update(BanAn entity) {
        XJdbc.executeUpdate(UPDATA_SQL,
                entity.isTrangThai(),
                entity.getViTri(),
                entity.getGhiChu(),
                entity.getMaB());
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
                entity.setTrangThai(rs.getBoolean(2));
                entity.setViTri(rs.getString(3));
                entity.setGhiChu(rs.getString(4));
                list.add(entity);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<BanAn> selectByTrangThai(boolean id) {
        return this.selectBySQL(SELECT_TRANGTHAI, id);
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
