/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author admin
 */
public class BanAn {
    String MaB;
    boolean trangThai;

    public String getMaB() {
        return MaB;
    }

    public void setMaB(String MaB) {
        this.MaB = MaB;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
    String viTri;
    String ghiChu;
    public BanAn() {
    }

    public BanAn(String MaB) {
        this.MaB = MaB;
    }
    
}
