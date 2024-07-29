package entity;

import java.time.LocalDateTime;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author admin
 */
public class HoaDon {
    int maHD;
    LocalDateTime ngayLap;
    String maKH;
    String maNV;
    String maB;
    int trangThai; //-1. Hủy, 0. Chưa thanh toán, 1. Đã thanh toán
    String ghiChu;

    public HoaDon() {
    }
    
    public HoaDon(int maHD, LocalDateTime ngayLap, String maKH, String maNV, String maB, int trangThai, String ghiChu) {
        this.maHD = maHD;
        this.ngayLap = ngayLap;
        this.maKH = maKH;
        this.maNV = maNV;
        this.maB = maB;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }
    
    public int getMaHD() {
        return maHD;
    }

    public void setMaHD(int maHD) {
        this.maHD = maHD;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getMaB() {
        return maB;
    }

    public void setMaB(String maB) {
        this.maB = maB;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
    
}
