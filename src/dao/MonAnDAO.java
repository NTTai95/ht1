/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import utils.XJdbc;
import entity.MonAn;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author admin
 */
public class MonAnDAO extends SysDAO<MonAn, String> {

    String INSERT_SQL = "Insert MonAn(MaMon, TenMon, DonGia, Anh, MaLoai) Values(?,?,?,?,?)";
    String UPDATE_SQL = "UPDATE MonAn SET TenMon = ?, DonGia = ?, Anh = ?, MaLoai = ? WHERE MaMon like ?";
    String DELETE_SQL = "DELETE FROM MonAn WHERE MaMon like ?";
    String SELECT_BY_ID = "Select * from MonAn where MaMon like ?";
    String SELECT_ALL = "SELECT * FROM MonAn";
    String COUNT_ROW = "SELECT COUNT(*) FROM MonAn";
    
    String SELECT_BY_LoaiMon = "select* from MonAn where MaLoai = ?";
    String GET_PRICE = "SELECT * FROM MonAn WHERE DonGia BETWEEN ? AND ?";
    String SELECT_BY_KEYWORD = "select* from MonAn where TenMon like ? AND DonGia BETWEEN ? AND ?";
    String SELECT_IN_LIST = "SELECT * FROM MonAn WHERE TenMon LIKE ?";
    
    
    @Override
    public void insert(MonAn entity) {
        XJdbc.executeUpdate(INSERT_SQL,
                entity.getMaMon(),
                entity.getTenMon(),
                entity.getDonGia(),
                entity.getAnh(),
                entity.getMaLoai());
    }

    @Override
    public void update(MonAn entity) {
        XJdbc.executeUpdate(UPDATE_SQL,
                entity.getTenMon(),
                entity.getDonGia(),
                entity.getAnh(),
                entity.getMaLoai(),
                entity.getMaMon());
    }

    @Override
    public void delete(String id) {
        XJdbc.executeUpdate(DELETE_SQL, id);
    }

    @Override
    public MonAn selectById(String id) {
        List<MonAn> list = this.selectBySQL(SELECT_BY_ID, id);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<MonAn> selectAll() {
        return this.selectBySQL(SELECT_ALL);
    }

    @Override
    protected List<MonAn> selectBySQL(String sql, Object... args) {
        List<MonAn> list = new ArrayList<>();
        try {
            ResultSet rs = XJdbc.executeQuery(sql, args);
            while (rs.next()) {
                MonAn entity = new MonAn();
                entity.setMaMon(rs.getString(1));
                entity.setTenMon(rs.getString(2));
                entity.setDonGia(rs.getFloat(3));
                entity.setAnh(rs.getString(4));
                entity.setMaLoai(rs.getString(5));
                list.add(entity);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<MonAn> selectByLoaiMon(String MaLoai) {
        return selectBySQL(SELECT_BY_LoaiMon, MaLoai);
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
    
    public List<MonAn> selectByKeyWord(String keyword){
        return this.selectBySQL(SELECT_BY_KEYWORD, "%"+keyword+"%");       
    }
    
    public List<MonAn> selectlnList(String maMon, String keywork){
        return this.selectBySQL(SELECT_IN_LIST, "%"+keywork+"%",maMon);
    }
    
    
    
    
    
}
