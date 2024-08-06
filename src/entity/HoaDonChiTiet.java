/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import dao.MonAnDAO;

/**
 *
 * @author admin
 */
public class HoaDonChiTiet {
    int maHD;
    String maMon;
    float donGia;
    int soLuong;

    String tenMon;
    String tenNhanVien;
    String tenKhachHang;

    public HoaDonChiTiet(int maHD, String maMon, float donGia, int soLuong, String tenMon, String tenNhanVien, String tenKhachHang) {
        this.maHD = maHD;
        this.maMon = maMon;
        this.donGia = donGia;
        this.soLuong = soLuong;
        this.tenMon = tenMon;
        this.tenNhanVien = tenNhanVien;
        this.tenKhachHang = tenKhachHang;
    }

//  
//
//    public HoaDonChiTiet(int maHD, String maMon, float donGia, int soLuong, String tenMon, String tenNhanVien) {
//        this.maHD = maHD;
//        this.maMon = maMon;
//        this.donGia = donGia;
//        this.soLuong = soLuong;
//        this.tenMon = tenMon;
//        this.tenNhanVien = tenNhanVien;
//    }
//    
    
    public HoaDonChiTiet() {
    }

    
    public HoaDonChiTiet(int maHD, String maMon, float donGia, int soLuong) {
        this.maHD = maHD;
        this.maMon = maMon;
        this.donGia = donGia;
        this.soLuong = soLuong;
    }

    public int getMaHD() {
        return maHD;
    }

    public void setMaHD(int maHD) {
        this.maHD = maHD;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public float getDonGia() {
        return donGia;
    }

    public void setDonGia(float donGia) {
        this.donGia = donGia;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }
    public String getTenMon() {
        return new MonAnDAO().selectById(maMon).getTenMon();
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }
    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public double selectSum(Integer maHD) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
    
}
