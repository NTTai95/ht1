/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import entity.HoaDonChiTiet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utils.XJdbc;


/**
 *
 * @author admin
 */
public class HoaDonChiTietDAO extends SysDAO<HoaDonChiTiet, String>{

    String INSERT_SQL = "Insert HoaDonChiTiet(MaHD, MaMon, DonGia, SoLuongMon) Values (?,?,?,?)";
    String UPDATA_SQl = "UPDATE HoaDonChiTiet SET SoLuongMon = ? WHERE MaHD like ? And MaMon like ?";
    String DELETE_SQL = "DELETE FROM HoaDonChiTiet WHERE MaHD like ? And MaMon like ?";
    String SELECT_HDCT = "Select * from HoaDonChiTiet Where MaHD = ?";
    String UPDATA_HDCT = "UPDATE HoaDonChiTiet SET MaHD = ? WHERE MaHD like ?";
       
    @Override
    public void insert(HoaDonChiTiet entity) {
        XJdbc.executeUpdate(INSERT_SQL,
                            entity.getMaHD(),
                            entity.getMaMon(),
                            entity.getDonGia(),
                            entity.getSoLuong());
    }

    @Override
    public void update(HoaDonChiTiet entity) {
        XJdbc.executeUpdate(UPDATA_SQl,     
                            entity.getSoLuong(),
                            entity.getMaHD(),
                            entity.getMaMon());
    }
    
    public void gopBanAn(String maHDOld, String maHDNew){
        XJdbc.executeUpdate(UPDATA_HDCT, maHDNew, maHDOld);
    }

    @Override
    public void delete(String id) {
    }
    
    public void delete(String maHD, String maMon) {
       XJdbc.executeUpdate(DELETE_SQL, maHD, maMon);
    }

    @Override
    public HoaDonChiTiet selectById(String id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


    
    @Override
    public List<HoaDonChiTiet> selectAll() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected List<HoaDonChiTiet> selectBySQL(String sql, Object... args) {
        List<HoaDonChiTiet> list = new ArrayList<>();
        try {
            ResultSet rs = XJdbc.executeQuery(sql, args);
            while (rs.next()) {
                HoaDonChiTiet entity = new HoaDonChiTiet();
                entity.setMaHD(rs.getInt(1));
                entity.setMaMon(rs.getString(2));
                entity.setDonGia(rs.getFloat(3));
                entity.setSoLuong(rs.getInt(4));
                list.add(entity);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public List<HoaDonChiTiet> selectHDCT(String maHD){
        return this.selectBySQL(SELECT_HDCT, maHD);
    }
    
   public double selectSum(int maHD) throws SQLException {
    String sql = "SELECT SUM(DonGia * SoLuongMon) AS TongTien FROM HoaDonChiTiet WHERE MaHD = ?";
    double tongTien = 0;

    try {
        ResultSet rs = XJdbc.executeQuery(sql, maHD);
        if (rs.next()) {
            tongTien = rs.getDouble(1);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return tongTien;
}

    
}
