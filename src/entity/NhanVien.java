/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.util.Random;

/**
 *
 * @author admin
 */
public class NhanVien {

    String maNV;
    String tenNV;
    String matKhau;
    int chucVu;
    String email;

    public NhanVien() {
    }

    public NhanVien(String maNV, String tenNV, String matKhau, int chucVu, String email) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.matKhau = maHoa(matKhau);
        this.chucVu = chucVu;
        this.email = email;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getTenNV() {
        return tenNV;
    }

    public void setTenNV(String tenNV) {
        this.tenNV = tenNV;
    }

    public String getMatKhau() {
        return giaiMa(matKhau);
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = maHoa(matKhau);
    }

    public int getChucVu() {
        return chucVu;
    }

    public void setChucVu(int chucVu) {
        this.chucVu = chucVu;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    protected String maHoa(String str) {
        char[] reStr = reverse(str).toCharArray();
        String enStr = "";
        for (int i = 0; i < reStr.length; i++) {
            enStr += "" + reStr[i] + randomCharacter();
        }
        return enStr;
    }

    protected String giaiMa(String str) {
        char[] enStr = str.toCharArray();
        String prStr = "";
        for (int i = 0; i < enStr.length; i += 2) {
            prStr += enStr[i];
        }
        return prStr;
    }

    protected String reverse(String str) {
        char[] chars = str.toCharArray();
        String reversed = "";
        for (int i = chars.length - 1; i >= 0; i--) {
            reversed += chars[i];
        }
        return reversed;
    }

    protected static char randomCharacter() {
        Random random = new Random();
        int randomNumber = random.nextInt(62); // 26 chữ cái hoa + 26 chữ cái thường + 10 số

        char randomChar;
        if (randomNumber < 26) {
            // Chữ cái hoa (A-Z)
            randomChar = (char) ('A' + randomNumber);
        } else if (randomNumber < 52) {
            // Chữ cái thường (a-z)
            randomChar = (char) ('a' + randomNumber - 26);
        } else {
            // Số (0-9)
            randomChar = (char) ('0' + randomNumber - 52);
        }
        return randomChar;
    }
}
