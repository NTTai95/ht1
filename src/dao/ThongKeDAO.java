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
import entity.ThongKe.DoanhThuTheoNam;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    String SUM_SQL = """
                     SELECT MonAn.TenMon, SUM(HoaDonChiTiet.SoLuongMon) as SoLuongBan
                     FROM HoaDonChiTiet
                     JOIN MonAn ON HoaDonChiTiet.MaMon = MonAn.MaMon
                     JOIN LoaiMon ON MonAn.MaLoai = LoaiMon.MaLoai
                     JOIN HoaDon ON HoaDonChiTiet.MaHD = HoaDon.MaHD
                     WHERE LoaiMon.MaLoai like ? AND HoaDon.TrangThai like '1'
                     GROUP BY MonAn.TenMon;""";

    String DoanhThu_SQL = """
                          SELECT CONVERT(date, hd.NgayLap, 103), SUM(hdct.SoLuongMon * hdct.DonGia)
                          From HoaDonChiTiet hdct Join HoaDon hd on hdct.MaHD = hd.MaHD
                          Where CONVERT(date, hd.NgayLap, 103) BETWEEN CONVERT(date, ?, 103) AND CONVERT(date, ?, 103) AND hd.TrangThai like '1'
                          GROUP BY CONVERT(date, hd.NgayLap, 103)
                          ORDER BY CONVERT(date, hd.NgayLap, 103) asc""";
    
    String DoanhThuCT_SQL = """
                          SELECT hd.MaHD, CONVERT(date, hd.NgayLap, 103) as NgayLap, hd.MaKH, SUM(hdct.SoLuongMon * hdct.DonGia) as TongTien
                          From HoaDonChiTiet hdct Join HoaDon hd on hdct.MaHD = hd.MaHD
                          Where CONVERT(date, hd.NgayLap, 103) BETWEEN CONVERT(date, ?, 103) AND CONVERT(date, ?, 103) AND hd.TrangThai like '1'
                          GROUP BY CONVERT(date, hd.NgayLap, 103), hd.MaHD, hd.MaKH
                          ORDER BY CONVERT(date, hd.NgayLap, 103) asc;""";


    String Ngaylap_SQL = "SELECT MIN(CONVERT(date, hd.NgayLap, 103)), MAX(CONVERT(date, hd.NgayLap, 103)) FROM HoaDon hd WHERE hd.TrangThai like '1'";

    String DoanhThuHomNay_SQL = """
                                Select Sum(hdct.SoLuongMon * hdct.DonGia) from HoaDon hd Join
                                HoaDonChiTiet hdct on hd.MaHD = hdct.MaHD
                                Where convert(Date,hd.NgayLap) = Convert(Date, GETDATE())""";

    String DoanhThuThangNay_SQL = """
                                Select Sum(hdct.SoLuongMon * hdct.DonGia) from HoaDon hd Join
                                HoaDonChiTiet hdct on hd.MaHD = hdct.MaHD
                                Where Month(hd.NgayLap) = Month(GETDATE()) AND Year(hd.NgayLap) = Year(GETDATE())""";
    
    String SoLuongHomNay_SQL = """
                                Select Sum(hdct.SoLuongMon) 
                                from HoaDon hd Join HoaDonChiTiet hdct on hd.MaHD = hdct.MaHD
                                Where convert(Date,hd.NgayLap) = Convert(Date, GETDATE())""";
    
    String doanhThuTheoNam_SQL = """
                                 SELECT MONTH(hd.NgayLap),  SUM(hdct.DonGia * hdct.SoLuongMon)
                                 FROM HoaDon hd join HoaDonChiTiet hdct on hd.MaHD = hdct.MaHD
                                 WHERE hd.TrangThai like '1' AND Year(hd.NgayLap) = ?
                                 Group By MONTH(hd.NgayLap)
                                 """;
    
    
    
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
    
    public List<DoanhThuCT> getDoanhThuCT(Date tuNgayCT, Date denNgayCT) {
        List<DoanhThuCT> list = new ArrayList<>();
        try {
            ResultSet rs = XJdbc.executeQuery(DoanhThuCT_SQL, tuNgayCT, denNgayCT);

            while (rs.next()) {
                DoanhThuCT dt = new DoanhThuCT();
                dt.setMaHD(rs.getInt(1));
                dt.setNgayLap(rs.getDate(2));
                dt.setMaKH(rs.getString(3));
                dt.setTongTien(rs.getInt(4));
                list.add(dt);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public NgayLap getNgayLap() {
        try {
            ResultSet rs = XJdbc.executeQuery(Ngaylap_SQL);

            if (rs.next()) {
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

    public int getDoanhThuHomNay() {
        try {
            ResultSet rs = XJdbc.executeQuery(DoanhThuHomNay_SQL);
            
            if(rs.next()){        
                int doanhThuHomNay = rs.getInt(1);
                rs.getStatement().getConnection().close();
                return  doanhThuHomNay;    
            }
        } catch (SQLException ex) {
            Logger.getLogger(ThongKeDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public int getDoanhThuThang(){
        try {
            ResultSet rs = XJdbc.executeQuery(DoanhThuThangNay_SQL);
            
            if(rs.next()){
                int doanhThuThang = rs.getInt(1);
                rs.getStatement().getConnection().close();
                return  doanhThuThang;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ThongKeDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public int getSoLuongBanHomNay(){
        try {
            ResultSet rs = XJdbc.executeQuery(SoLuongHomNay_SQL);
            
            if(rs.next()){
                int soLuongHomNay = rs.getInt(1);
                rs.getStatement().getConnection().close();
                return soLuongHomNay;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ThongKeDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public List<DoanhThuTheoNam> getDoanhThuTheoNam(int year){
        try {
            ResultSet rs = XJdbc.executeQuery(doanhThuTheoNam_SQL, year);
            List<DoanhThuTheoNam> list = new ArrayList<>();
            
            while(rs.next()){
                DoanhThuTheoNam dttn = new DoanhThuTheoNam();
                dttn.setMonth(rs.getInt(1));
                dttn.setTongTien(rs.getLong(2));
                list.add(dttn);
            }
            return list;
        } catch (SQLException ex) {
            Logger.getLogger(ThongKeDAO.class.getName()).log(Level.SEVERE, null, ex);
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
