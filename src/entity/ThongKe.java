/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.util.Date;

/**
 *
 * @author admin
 */
public class ThongKe {
    public static class DoanhThuMonAn{
        String tenMon;
        int soLuong;

        public DoanhThuMonAn() {
        }

        public DoanhThuMonAn(String tenMon, int soLuong) {
            this.tenMon = tenMon;
            this.soLuong = soLuong;
        }

        public String getTenMon() {
            return tenMon;
        }

        public void setTenMon(String tenMon) {
            this.tenMon = tenMon;
        }

        public int getSoLuong() {
            return soLuong;
        }

        public void setSoLuong(int soLuong) {
            this.soLuong = soLuong;
        }
    }
    
    public static class DoanhThu{
        int tongTien;
        Date ngayLap;

        public DoanhThu() {
        }
        
        public DoanhThu(int tongTien, Date ngayLap) {
            this.tongTien = tongTien;
            this.ngayLap = ngayLap;
        }

        public double getTongTien() {
            return tongTien;
        }

        public void setTongTien(int tongTien) {
            this.tongTien = tongTien;
        }

        public Date getNgayLap() {
            return ngayLap;
        }

        public void setNgayLap(Date ngayLap) {
            this.ngayLap = ngayLap;
        }
    }
    
    public static class NgayLap{
        Date min;
        Date max;

        public NgayLap() {
        }

        public NgayLap(Date min, Date max) {
            this.min = min;
            this.max = max;
        }

        public Date getMin() {
            return min;
        }

        public void setMin(Date min) {
            this.min = min;
        }

        public Date getMax() {
            return max;
        }

        public void setMax(Date max) {
            this.max = max;
        }
    }
}
