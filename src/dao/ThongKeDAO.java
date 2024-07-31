/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utils.XJdbc;

/**
 *
 * @author ADMIN
 */
public class ThongKeDAO {
    String SUM_SQL = "SELECT SUM(HoaDonChiTiet.SoLuongMon)\n" +
                        "FROM HoaDonChiTiet\n" +
                        "JOIN MonAn ON HoaDonChiTiet.MaMon = MonAn.MaMon\n" +
                        "JOIN LoaiMon ON MonAn.MaLoai = LoaiMon.MaLoai\n" +
                        "WHERE LoaiMon.MaLoai = ?;";
    
    
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
    
//    public int getSum(String MaLoai) {
//        try {
//            ResultSet rs = XJdbc.executeQuery(SUM_SQL);
//            if (rs.next()) {
//                return rs.getInt(1);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return 0;
//    }
    
    public List<Object[]> getSum(int maLoai) {
        List<Object[]> resultList = new ArrayList<>();
        
        try {
//            XJdbc.setInt(1, maLoai);
            ResultSet rs = XJdbc.executeQuery(SUM_SQL);

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
}
