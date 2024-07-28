/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import entity.HoaDon;
import entity.HoaDonChiTiet;
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
public class HoaDonDAO extends SysDAO<HoaDon, Integer> {

    String INSERT_SQL = "Insert HoaDon(NgayLap, MaKH, MaNV, MaB, TrangThai,GhiChu) Values (?,?,?,?,?,?)";
    String UPDATA_SQL = "UPDATE HoaDon SET TrangThai = ?, GhiChu = ? WHERE MaHD like ?";
    String DELETE_SQL = "DELETE FROM HoaDon WHERE MaHD like ?";
    String SELECT_BY_ID = "Select * from HoaDon where MaHD like ?";
    String SELECT_ALL = "SELECT * FROM HoaDon";
    String COUNT_ROW = "SELECT COUNT(*) FROM HoaDon";
    String SELECT_TRANGTHAI = "Select * from HoaDon Where TrangThai like ?";
    String SELECT_KHACHHANG = "Select * from HoaDon Where MaKH like ?";

    @Override
    public void insert(HoaDon entity) {
        XJdbc.executeUpdate(INSERT_SQL,
                entity.getNgayLap(),
                entity.getMaKH(),
                entity.getMaNV(),
                entity.getMaB(),
                entity.getTrangThai(),
                entity.getGhiChu()
        );
    }

    @Override
    public void update(HoaDon entity) {
        XJdbc.executeUpdate(UPDATA_SQL,
                entity.getTrangThai(),
                entity.getGhiChu(),
                entity.getMaHD()
        );
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
                entity.setMaHD(rs.getInt(1));
                entity.setNgayLap(rs.getTimestamp(2).toLocalDateTime());
                entity.setMaKH(rs.getString(3));
                entity.setMaNV(rs.getString(4));
                entity.setMaB(rs.getString(5));
                entity.setTrangThai(rs.getInt(6));
                entity.setGhiChu(rs.getString(7));
                list.add(entity);
                
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<HoaDon> selectByTrangThai(String trangThai){
        return this.selectBySQL(SELECT_TRANGTHAI, trangThai);
    }
    
    public HoaDon selectByMaKH(String maKH){
        List<HoaDon> list = this.selectBySQL(SELECT_KHACHHANG, maKH);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
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
