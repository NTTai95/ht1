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
import entity.ThongKe.*;

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

    String SUM_SQL = "SELECT MonAn.TenMon, SUM(HoaDonChiTiet.SoLuongMon) as SoLuongBan\n"
            + "FROM HoaDonChiTiet\n"
            + "JOIN MonAn ON HoaDonChiTiet.MaMon = MonAn.MaMon\n"
            + "JOIN LoaiMon ON MonAn.MaLoai = LoaiMon.MaLoai\n"
            + "JOIN HoaDon ON HoaDonChiTiet.MaHD = HoaDon.MaHD\n"
            + "WHERE LoaiMon.MaLoai like ? AND HoaDon.TrangThai like '1'\n"
            + "GROUP BY MonAn.TenMon;";

    String DoanhThu_SQL = "SELECT CONVERT(date, hd.NgayLap, 103), SUM(hdct.SoLuongMon * hdct.DonGia)\n"
            + "From HoaDonChiTiet hdct Join HoaDon hd on hdct.MaHD = hd.MaHD\n"
            + "Where CONVERT(date, hd.NgayLap, 103) BETWEEN CONVERT(date, ?, 103) AND CONVERT(date, ?, 103) AND hd.TrangThai like '1'\n"
            + "GROUP BY CONVERT(date, hd.NgayLap, 103)\n"
            + "ORDER BY CONVERT(date, hd.NgayLap, 103) asc";

    String Ngaylap_SQL = "SELECT MIN(CONVERT(date, hd.NgayLap, 103)), MAX(CONVERT(date, hd.NgayLap, 103)) FROM HoaDon hd WHERE hd.TrangThai like '1'";

    public List<DoanhThuMonAn> getSum(String maLoai) {
        List<DoanhThuMonAn> list = new ArrayList<>();
        try {
            ResultSet rs = XJdbc.executeQuery(SUM_SQL, maLoai);

            while (rs.next()) {
                DoanhThuMonAn dtma = new DoanhThuMonAn();
                dtma.setTenMon(rs.getString(1));
                dtma.setSoLuong(rs.getInt(2));
                list.add(dtma);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<DoanhThu> getDoanhThu(Date tuNgay, Date denNgay) {
        List<DoanhThu> list = new ArrayList<>();
        try {
            ResultSet rs = XJdbc.executeQuery(DoanhThu_SQL, tuNgay, denNgay);

            while (rs.next()) {
                DoanhThu dt = new DoanhThu();
                dt.setNgayLap(rs.getDate(1));
                dt.setTongTien(rs.getInt(2));
                list.add(dt);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public NgayLap getNgayLap(){
        try {
            ResultSet rs = XJdbc.executeQuery(Ngaylap_SQL);

            if(rs.next()) {
                NgayLap nl = new NgayLap();
                nl.setMin(rs.getDate(1));
                nl.setMax(rs.getDate(2));
                rs.getStatement().getConnection().close();
            return nl;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
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
}
