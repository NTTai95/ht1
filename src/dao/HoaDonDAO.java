/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import entity.HoaDon;
import entity.MonAn;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.XJdbc;

/**
 *
 * @author admin
 */
public class HoaDonDAO extends SysDAO<HoaDon, Integer> {

    String INSERT_SQL = "Insert HoaDon(MaHD, NgayLap, MaKH, MaNV, MaB, TrangThai, NgayNhan,GhiChu) Values (?,?,?,?,?,?,?,?)";
    String DELETE_SQL = "DELETE FROM HoaDon WHERE MaHD like ?";
    String SELECT_BY_ID = "Select * from HoaDon where MaHD like ?";
    String SELECT_ALL = "SELECT * FROM HoaDon";
    String COUNT_ROW = "SELECT COUNT(*) FROM HoaDon";

    @Override
    public void insert(HoaDon entity) {
        XJdbc.executeUpdate(INSERT_SQL,
                getCountRow(),
                entity.getNgayLap(),
                entity.getMaKH(),
                entity.getMaNV(),
                entity.getMaB(),
                entity.getTrangThai(),
                entity.getNgayNhan(),
                entity.getGhiChu()
        );
    }

    @Override
    public void update(HoaDon entity) {
    }

    @Override
    public void delete(Integer id) {
        XJdbc.executeUpdate(DELETE_SQL, id);
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public HoaDon selectById(Integer id) {
        List<HoaDon> list = this.selectBySQL(SELECT_BY_ID, id);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<HoaDon> selectAll() {
        return selectBySQL(SELECT_ALL);
    }

    @Override
    protected List<HoaDon> selectBySQL(String sql, Object... args) {
        List<HoaDon> list = new ArrayList<>();
        try {
            ResultSet rs = XJdbc.executeQuery(sql, args);
            while (rs.next()) {
                HoaDon entity = new HoaDon();
                list.add(entity);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected int getCountRow() {
        try {
            ResultSet rs = XJdbc.executeQuery(COUNT_ROW);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(HoaDonDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
}
