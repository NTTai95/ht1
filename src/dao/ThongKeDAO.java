/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import utils.XJdbc;

/**
 *
 * @author ADMIN
 */
public class ThongKeDAO {
//    String SUM_SQL = "SELECT SUM(HoaDonChiTiet.SoLuongMon)\n" +
//                        "FROM HoaDonChiTiet\n" +
//                        "JOIN MonAn ON HoaDonChiTiet.MaMon = MonAn.MaMon\n" +
//                        "JOIN LoaiMon ON MonAn.MaLoai = LoaiMon.MaLoai\n" +
//                        "WHERE LoaiMon.MaLoai = ?;";
    
    String SUM_SQL = "SELECT MonAn.MaMon, MonAn.TenMon, SUM(HoaDonChiTiet.SoLuongMon) as SoLuongBan\n" +
                     "FROM HoaDonChiTiet\n" +
                     "JOIN MonAn ON HoaDonChiTiet.MaMon = MonAn.MaMon\n" +
                     "JOIN LoaiMon ON MonAn.MaLoai = LoaiMon.MaLoai\n" +
                     "WHERE LoaiMon.MaLoai = ?\n" +
                     "GROUP BY MonAn.MaMon, MonAn.TenMon;";
    
//    String DoanhThu_SQL = "SELECT SUM(HoaDonChiTiet.SoLuongMon * HoaDonChiTiet.GiaBan) as TotalRevenue\n" +
//                               "FROM HoaDonChiTiet\n" +
//                               "JOIN HoaDon ON HoaDonChiTiet.MaHD = HoaDon.MaHD\n" +
//                               "WHERE HoaDon.NgayBan BETWEEN ? AND ?;";
    
    
    private List<Object[]> getListOfArray(String sql, String[] cols, Object... args) {
        try {
            List<Object[]> list = new ArrayList<>();
            ResultSet rs = XJdbc.executeQuery(sql, args);
            while (rs.next()) {
                Object[] vals = new Object[cols.length];
                for (int i = 0; i < cols.length; i++) {
                    vals[i] = rs.getObject(cols[i]);
                }

                list.add(vals);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
//    public List<Object[]> getSum(int maLoai) {
//        List<Object[]> resultList = new ArrayList<>();
//        
//        try {
////            XJdbc.setInt(1, maLoai);
//            ResultSet rs = XJdbc.executeQuery(SUM_SQL);
//
//            while (rs.next()) {
//                Object[] row = new Object[3];
//                row[0] = rs.getString("MaMon");
//                row[1] = rs.getString("TenMon");
//                row[2] = rs.getDouble("SoLuongBan");
//                resultList.add(row);
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return resultList;
//    }
    
    public List<Object[]> getSum(String maLoai) {
        List<Object[]> resultList = new ArrayList<>();
        
        try {
            ResultSet rs = XJdbc.executeQuery(SUM_SQL, maLoai);

            while (rs.next()) {
                Object[] row = new Object[3];
                row[0] = rs.getString("MaMon");
                row[1] = rs.getString("TenMon");
                row[2] = rs.getDouble("SoLuongBan");
                resultList.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultList;
    }
    
//    public double getDoanhThu(Date startDate, Date endDate) {
//        double totalRevenue = 0;
//        try {
//            ResultSet rs = XJdbc.executeQuery(DoanhThu_SQL, startDate, endDate);
//            if (rs.next()) {
//                totalRevenue = rs.getDouble("TotalRevenue");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return totalRevenue;
//    }
}
