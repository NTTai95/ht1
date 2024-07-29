/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ui;

import dao.BanAnDAO;
import dao.HoaDonChiTietDAO;
import dao.HoaDonDAO;
import dao.KhachHangDAO;
import dao.LoaiMonDAO;
import dao.MonAnDAO;
import entity.BanAn;
import entity.HoaDon;
import entity.HoaDonChiTiet;
import entity.KhachHang;
import entity.LoaiMon;
import entity.MonAn;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import utils.Auth;
import utils.MsgBox;
import utils.Printer;

/**
 *
 * @author admin
 */
public class BanHangJFrame extends javax.swing.JFrame {

    /**
     * Creates new form NhanVienJDialog
     */
    int row = -1;
    HoaDonDAO hdDAO = new HoaDonDAO();
    KhachHangDAO khDAO = new KhachHangDAO();
    HoaDonChiTietDAO hdctDAO = new HoaDonChiTietDAO();
    MonAnDAO maDAO = new MonAnDAO();
    LoaiMonDAO lmDAO = new LoaiMonDAO();
    List<HoaDon> hd = hdDAO.selectByTrangThai(String.valueOf(0));
    BanAnDAO baDAO = new BanAnDAO();
    List<LoaiMon> lm = lmDAO.selectAll();

    int rowBA = baDAO.getCountRow();
    DecimalFormat fmTien = new DecimalFormat("#,#00");
    DateTimeFormatter fmThoiGian = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    DefaultTableModel modelTableHA = new DefaultTableModel(new Object[]{"MaB", "Hình ảnh", "Thông tin"}, 0) {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 1:
                    return ImageIcon.class; // Cột Photo
                default:
                    return Object.class;
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Không cho phép chỉnh sửa bất kỳ ô nào
        }
    };

    public BanHangJFrame() {
        initComponents();
        this.loadThucDon();
        this.loadBanAn();
        this.loadComboxLoaiMonAn();
        this.initKhachHang();
    }

    public void loadComboxLoaiMonAn() {
        DefaultComboBoxModel cbomodel = (DefaultComboBoxModel) cboLoaiMonAn.getModel();
        cbomodel.removeAllElements();

        cbomodel.addElement("All");
        for (LoaiMon lm : lmDAO.selectAll()) {
            cbomodel.addElement(lm);
        }

        cboLoaiMonAn.setModel(cbomodel);
    }

    public boolean checkHoaDon() {
        if (lblMaHoaDon.getText().equals("0")) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn ăn!");
            System.err.print("Mã hóa đơn: " + lblMaHoaDon.getText());
            return false;
        } else if (lblNgayLap.getText().equals("0")) {
            System.err.print("Ngày lập: " + lblNgayLap.getText());
            return false;
        } else if (txtTimKiemKhachHang.getText().equals("")) {
            System.err.print("Tìm kiếm khách hàng: " + txtTimKiemKhachHang.getText());
            return false;
        } else if (lblTenKhachHang.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng!");
            System.err.print("Tìm kiếm khách hàng: " + lblTenKhachHang.getText());
            return false;
        } else if (lblTongTien.getText().equals("0")) {
            System.err.print("Tổng tiền: " + lblTongTien.getText());
            return false;
        }
        return true;
    }

    public HoaDon getFrom(int trangThai) {
        HoaDon hdTemp = new HoaDon();
        hdTemp.setMaHD(Integer.parseInt(lblMaHoaDon.getText()));
        hdTemp.setMaB(lblBanAn.getText());
        hdTemp.setMaKH(timKhachHangbySDT(txtTimKiemKhachHang.getText()).getMaKH());
        hdTemp.setMaNV(Auth.user.getMaNV());
        hdTemp.setNgayLap(LocalDateTime.parse(lblNgayLap.getText(), fmThoiGian));
        hdTemp.setTrangThai(trangThai);
        hdTemp.setGhiChu("");

        return hdTemp;
    }

    public void setFrom(HoaDon hd) {
        lblBanAn.setText(hd.getMaB());
        lblMaHoaDon.setText(String.valueOf(hd.getMaHD()));
        lblNgayLap.setText(hd.getNgayLap().format(fmThoiGian));
        KhachHang khTemp = khDAO.selectById(hd.getMaKH());
//        System.out.println("Hóa đơn: " + hd.getMaKH());
        txtTimKiemKhachHang.setText(khTemp == null ? "" : khTemp.getSDT());
        lblTenKhachHang.setText(khTemp == null ? "" : khTemp.getTenKH());
        List<HoaDonChiTiet> hdct = hdctDAO.selectHDCT(String.valueOf(hd.getMaHD()));
        loadHoaDonChiTiet(hdct);
        lblTongTien.setText(String.valueOf(fmTien.format(tongTien())));
    }

    public void loadThucDon() {

        DefaultTableModel model = modelTableHA;
        model.setRowCount(0);

        List<MonAn> ma = maDAO.selectAll();

        String maLoaiMon = "All";
        if (cboLoaiMonAn.getSelectedItem() != null) {
            if (!cboLoaiMonAn.getSelectedItem().toString().equalsIgnoreCase("All")) {
                LoaiMon lmTemp = (LoaiMon) cboLoaiMonAn.getSelectedItem();
                maLoaiMon = lmTemp.getMaLoai();
            }
        }
        for (int i = 0; i < ma.size(); i++) {
            String tenMon = ma.get(i).getTenMon();

            if (!maLoaiMon.equals(ma.get(i).getMaLoai())
                    && !maLoaiMon.equalsIgnoreCase("All")) {
                continue;
            } else if (!tenMon.toUpperCase().contains(txtTimKiemMonAn.getText().toUpperCase())) {
                continue;
            }

            String gia = fmTien.format(ma.get(i).getDonGia());
            String loaiMon = lmDAO.selectById(ma.get(i).getMaLoai()).getTenLoai();
            String hinhAnh = ma.get(i).getAnh();

            model.addRow(new Object[]{ma.get(i).getMaMon(), new ImageIcon("./" + hinhAnh), "<html><h2 style=\"color: red; margin-top: 0px;\">" + tenMon + "</h2><h4 style=\"margin: 0px;\">" + gia + "</h4><i style=\"margin: 0px;\">" + loaiMon + "</i></html>"});
        }

        tblThucDon.setModel(model);

        tblThucDon.getTableHeader().setVisible(false);

        TableColumn clm = tblThucDon.getColumnModel().getColumn(1);
        clm.setMinWidth(130);
        clm.setPreferredWidth(130);
        clm.setMaxWidth(130);

        TableColumn clm2 = tblThucDon.getColumnModel().getColumn(0);
        clm2.setMinWidth(0);
        clm2.setPreferredWidth(0);
        clm2.setMaxWidth(0);
    }

    public void loadBanAn() {
        DefaultTableModel model = (DefaultTableModel) tblBanAn.getModel();
        model.setRowCount(0);

        String color = "blue";

        row = -1;
        loadHoaDon();

        List<BanAn> banAnAn = baDAO.selectByTrangThai(false);
        
        for (int i = 1; i <= rowBA; i++) {
            
            boolean boQua = false;
            
            String maBA = "B" + (i < 10 ? "0" + i : i);
            String tenKH = "Trống";
            String trangThai = "Chờ sử dụng";
            boQua = cboBATrangThai.getSelectedIndex() == 1;

            for (int j = 0; j < hd.size(); j++) {
                if (maBA.equalsIgnoreCase(hd.get(j).getMaB())) {
                    maBA = "<html><p style='color:" + color + ";'>" + maBA + "</p></html>";
                    tenKH = "<html><p style='color:" + color + ";'>" + khDAO.selectById(hd.get(j).getMaKH()).getTenKH() + "</p></html>";
                    trangThai = "<html><p style='color:" + color + ";'>Chưa Thanh Toán</p></html>";
                    boQua = cboBATrangThai.getSelectedIndex() == 2;
                    break;
                }
            }
            
             for(BanAn BATemp : banAnAn){
                if(BATemp.getMaB().equalsIgnoreCase(maBA)){
                    boQua = true;
                    break;
                }
            }
            if (boQua) {
                continue;
            }

            model.addRow(new Object[]{maBA, tenKH, trangThai});
        }
    }

    public String getValueTable(String str) {
        if (!str.startsWith("<html>")) {
            return str;
        }
        String strNew = str.substring(6, str.length() - 7);
        int start = strNew.indexOf(">");
        int end = strNew.lastIndexOf("<");

        return strNew.substring(start + 1, end);
    }

    public void loadHoaDon() {

        if (row == -1) {
            HoaDon hdNew = new HoaDon();
            hdNew.setMaHD(0);
            hdNew.setMaB("B00");
            hdNew.setMaKH("");
            hdNew.setMaNV(Auth.user.getMaNV());
            hdNew.setNgayLap(LocalDateTime.now());
            hdNew.setTrangThai(0);
            hdNew.setGhiChu("");
            setFrom(hdNew);
            btnTaoHD.setEnabled(false);
            txtTimKiemKhachHang.setEnabled(false);
            txtKhachTra.setEnabled(false);
            btnGopBan.setEnabled(false);
            btnInHoaDon.setEnabled(false);
            btnXoaMonAn.setEnabled(false);
            btnThongBaoBep.setEnabled(false);
            btnThanhToan.setEnabled(false);
            return;
        }

        if (getValueTable(tblBanAn.getValueAt(row, 2).toString()).equalsIgnoreCase("Chưa Thanh Toán")) {
            for (int i = 0; i < hd.size(); i++) {
                if (getValueTable(tblBanAn.getValueAt(row, 0).toString()).equalsIgnoreCase(hd.get(i).getMaB())) {
                    setFrom(hd.get(i));
                    btnTaoHD.setEnabled(false);
                    txtTimKiemKhachHang.setEnabled(false);
                    txtKhachTra.setEnabled(true);
                    btnGopBan.setEnabled(true);
                    btnInHoaDon.setEnabled(true);
                    btnXoaMonAn.setEnabled(false);
                    btnThongBaoBep.setEnabled(true);
                    btnThanhToan.setEnabled(true);
                    return;
                }
            }
        } else {
            HoaDon hdNew = new HoaDon();
            hdNew.setMaHD(hdDAO.getCountRow() + 1);
            hdNew.setMaB(getValueTable(tblBanAn.getValueAt(row, 0).toString()));
            hdNew.setMaKH("");
            hdNew.setMaNV(Auth.user.getMaNV());
            hdNew.setNgayLap(LocalDateTime.now());
            hdNew.setTrangThai(0);
            hdNew.setGhiChu("");
            setFrom(hdNew);
            btnTaoHD.setEnabled(true);
            txtTimKiemKhachHang.setEnabled(true);
            txtKhachTra.setEnabled(false);
            btnGopBan.setEnabled(false);
            btnInHoaDon.setEnabled(false);
            btnXoaMonAn.setEnabled(false);
            btnThongBaoBep.setEnabled(false);
            btnThanhToan.setEnabled(false);
        }
    }

    public float tongTien() {
//        float tongTien = 0;
//        for (int i = 0; i < tblHoaDonChiTiet.getRowCount(); i++) {
//            tongTien += (float) tblHoaDonChiTiet.getValueAt(i, 4);
//        }
//        return tongTien;

        float tongTien = 0;
        List<HoaDonChiTiet> hdctTemp = hdctDAO.selectHDCT(lblMaHoaDon.getText());
        for (HoaDonChiTiet hdct1 : hdctTemp) {
            tongTien += hdct1.getDonGia() * hdct1.getSoLuong();
        }
        return tongTien;
    }

    public void loadHoaDonChiTiet(List<HoaDonChiTiet> hdct) {
        DefaultTableModel model = (DefaultTableModel) tblHoaDonChiTiet.getModel();
        model.setRowCount(0);
        for (int i = 0; i < hdct.size(); i++) {
            String tenMon = maDAO.selectById(hdct.get(i).getMaMon()).getTenMon();
            int soLuong = hdct.get(i).getSoLuong();
            float donGia = hdct.get(i).getDonGia();
            model.addRow(new Object[]{i + 1, tenMon, soLuong, fmTien.format(donGia), soLuong * donGia});
        }

        tblHoaDonChiTiet.setModel(model);
        lblTongTien.setText(fmTien.format(tongTien()));
    }

    public KhachHang timKhachHangbySDT(String SDT) {
        KhachHang kh = khDAO.selectBySDT(SDT);
        return kh;
    }

    public void themMonAn() {
        if (!checkHoaDon()) {
            System.out.println("Khong The Them Mon An");
            return;
        }
        DefaultTableModel model = (DefaultTableModel) tblHoaDonChiTiet.getModel();
        HoaDonChiTiet hdctNew = new HoaDonChiTiet();

        MonAn maTemp = maDAO.selectById(tblThucDon.getValueAt(tblThucDon.getSelectedRow(), 0).toString());
        int maHD = Integer.parseInt(lblMaHoaDon.getText());
        List<HoaDonChiTiet> hdctTemp = hdctDAO.selectHDCT(String.valueOf(maHD));

        for (int i = 0; i < hdctTemp.size(); i++) {
            if (hdctTemp.get(i).getMaMon().equalsIgnoreCase(maTemp.getMaMon())) {
                return;
            }
        }

        hdctNew.setMaHD(maHD);
        hdctNew.setMaMon(maTemp.getMaMon());
        hdctNew.setSoLuong(1);
        hdctNew.setDonGia(maTemp.getDonGia());
        hdctDAO.insert(hdctNew);

        hdctTemp.add(hdctNew);

        loadHoaDonChiTiet(hdctTemp);
        tinhTienThoi();

    }

    public void capNhatHDCT() {
        DefaultTableModel model = (DefaultTableModel) tblHoaDonChiTiet.getModel();
        List<HoaDonChiTiet> hdctTemp = hdctDAO.selectHDCT(lblMaHoaDon.getText());

        for (int i = 0; i < model.getRowCount(); i++) {
            if (hdctTemp.get(i).getSoLuong() != Integer.parseInt(model.getValueAt(i, 2).toString())) {
                hdctTemp.get(i).setSoLuong(Integer.parseInt(model.getValueAt(i, 2).toString()));
                hdctDAO.update(hdctTemp.get(i));
            }
        }

        lblTongTien.setText(fmTien.format(tongTien()));
        tinhTienThoi();

    }

    public void gopBanAn() {
        if (!checkHoaDon()) {
            return;
        }
        //System.out.println("iu.NhanVienJDialog.gopBanAn()");
        String maBA = JOptionPane.showInputDialog(this, "Nhập mã bàn muốn gợp :", "Gợp Bàn Ăn", JOptionPane.QUESTION_MESSAGE);
        maBA = maBA == null ? null : maBA.toUpperCase();

        if (maBA == null) {
            return;
        }

        if (!maBA.startsWith("B")) {
            JOptionPane.showMessageDialog(this, "Mã bàn ăn không hợp lệ!");
            return;
        }
        String maHD = null;
        for (HoaDon hd1 : hd) {
            if (hd1.getMaB().toUpperCase().equalsIgnoreCase(maBA)) {
                maHD = String.valueOf(hd1.getMaHD());
                break;
            }
        }

        if (maHD == null) {
            JOptionPane.showMessageDialog(this, "Không Tìm thấy bàn ăn " + maBA + " !");
            return;
        }

        List<HoaDonChiTiet> hdctOld = hdctDAO.selectHDCT(lblMaHoaDon.getText());
        List<HoaDonChiTiet> hdctNew = hdctDAO.selectHDCT(maHD);

        for (HoaDonChiTiet hdctOld1 : hdctOld) {
            for (HoaDonChiTiet hdctNew1 : hdctNew) {
                if (hdctOld1.getMaMon().equalsIgnoreCase(hdctNew1.getMaMon())) {
                    hdctOld1.setSoLuong(hdctOld1.getSoLuong() + hdctNew1.getSoLuong());
                    hdctDAO.update(hdctOld1);
                    hdctDAO.delete(String.valueOf(hdctNew1.getMaHD()), hdctNew1.getMaMon());
                }
            }
        }

        hdctDAO.gopBanAn(maHD, lblMaHoaDon.getText());

        HoaDon hdOld = hdDAO.selectById(Integer.valueOf(maHD));

        hdOld.setTrangThai(-1);
        hdOld.setGhiChu("Hóa đơn đã gớp với hóa đơn có mã '" + lblMaHoaDon.getText() + "'");

        hdDAO.update(hdOld);

        hd = hdDAO.selectByTrangThai("0");

        loadBanAn();
        loadHoaDon();

    }

    public boolean tinhTienThoi() {
        if (txtKhachTra.getText().equals("")) {
            txtThoiLai.setText("Nhập tiền trả!");
            return false;
        }
        if (txtKhachTra.getText().matches("^[^a-zA-Z\s]+$")) {
            String strTongTien = lblTongTien.getText().replaceAll("\\.", "");
            int tongTien = Integer.parseInt(strTongTien);
            String tienKhachTra = txtKhachTra.getText().replaceAll("\\.", "");
            tienKhachTra = tienKhachTra.replaceAll(",", "");
            if (Integer.parseInt(tienKhachTra) < tongTien) {
                txtThoiLai.setText("Chưa đủ!");
            } else {
                txtThoiLai.setText(fmTien.format(Integer.parseInt(tienKhachTra) - tongTien));
                return true;
            }
        } else {
            txtThoiLai.setText("không hợp lệ!");
        }
        return false;
    }

//-------------------------------------From KHÁCH HÀNG
    public void initKhachHang() {
        this.setFormKhachHang();
        this.fillTable();
        loadListBanAn();
        banAnNEW();
        btnDoiTrangThai.setEnabled(false);
    }

    int rowkh = -1;
    private Object tabs;

    void edit() {
        String maKH = (String) tblListKH.getValueAt(this.rowkh, 0);
        KhachHang kh = khDAO.selectById(maKH);
        this.setForm(kh);
        tblListKH.setRowSelectionInterval(rowkh, rowkh);
    }

    void clearForm() {
        KhachHang kh = new KhachHang();
        this.setForm(kh);
        this.rowkh = -1;

    }

    void fillTable() {
        DefaultTableModel model = (DefaultTableModel) tblListKH.getModel();
        model.setRowCount(0);
        List<KhachHang> list = khDAO.selectAll();
        for (KhachHang kh : list) {
            Object[] rows = {kh.getMaKH(), kh.getTenKH(), kh.getSDT()};
            model.addRow(rows);
        }

    }

    void setForm(KhachHang kh) {
        txtmaKH.setText((String) kh.getMaKH());
        txtTenKH.setText(kh.getTenKH());
        txtSDT.setText(kh.getSDT());

    }

    void setFormKhachHang() {
        KhachHang kh = new KhachHang("0", "", "");
        kh = getKhachHangNew(kh);
        txtmaKH.setText(kh.getMaKH());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    public boolean checkKhachHang() {
        if (txtTenKH.getText().matches(".*\\d.*")) {
            JOptionPane.showMessageDialog(this, "Tên khách hàng không hợp lệ!");
            return false;
        } else if (txtSDT.getText().matches(".*\\D.*")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không được nhập chữ!");
            return false;
        } else if (txtTenKH.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Tên khách hàng không được bỏ trống!");
            return false;
        } else if (txtSDT.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không được bỏ trống!");
            return false;
        } else if (txtSDT.getText().length() < 9) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không đủ!");
            return false;
        }
        return true;
    }

    KhachHang getForm() {
        KhachHang kh = new KhachHang();
        kh.setMaKH(txtmaKH.getText());
        kh.setTenKH(txtTenKH.getText());
        kh.setSDT(txtSDT.getText());
        return kh;
    }

    KhachHang getKhachHangNew(KhachHang kh) {
        List<KhachHang> list = khDAO.selectAll();
        KhachHang khTemp = new KhachHang();
        khTemp.setTenKH(kh.getTenKH());
        khTemp.setSDT(kh.getSDT());
        int x = list.size() + 1;
        khTemp.setMaKH("KH" + (x < 10 ? "000" + x : x < 100 ? "00" + x : x < 1000 ? "0" + x : x));
        for (int i = 0; i < list.size(); i++) {
            String maKH = "KH" + ((i + 1) < 10 ? "000" + (i + 1) : (i + 1) < 100 ? "00" + (i + 1) : (i + 1) < 1000 ? "0" + (i + 1) : (i + 1));
            if (!list.get(i).getMaKH().equalsIgnoreCase(maKH)) {
                khTemp.setMaKH(maKH);
                break;
            }
        }
        return khTemp;
    }

    public boolean checkTrungSDT(String sdt) {
        KhachHang kh = khDAO.selectBySDT(sdt);
        return kh == null;
    }

    public void xoaKhachHang() {
        for (int i : tblListKH.getSelectedRows()) {
            if (hdDAO.selectByMaKH(tblListKH.getValueAt(i, 0).toString()) != null) {
                MsgBox.alert(this, "Không thể xóa khách hàng '" + tblListKH.getValueAt(i, 1) + "'");
            } else {
                khDAO.delete(tblListKH.getValueAt(i, 0).toString());
                MsgBox.alert(this, "Đã xóa khách hàng '" + tblListKH.getValueAt(i, 1) + " (" + tblListKH.getValueAt(i, 2) + ")'");
            }
        }
        fillTable();
    }

    public void capNhatKhachHang() {

        for (int i = 0; i < tblListKH.getRowCount(); i++) {
            KhachHang khTemp = new KhachHang();
            khTemp.setMaKH(tblListKH.getValueAt(i, 0).toString());
            String tenKhachHang = tblListKH.getValueAt(i, 1).toString();
            if (tenKhachHang.matches(".*\\d.*")) {
                MsgBox.alert(this, tblListKH.getValueAt(i, 0) + " - Tên Không hợp lệ '" + tblListKH.getValueAt(i, 1) + "'");
                continue;
            }
            khTemp.setTenKH(tenKhachHang);

            String SDT = tblListKH.getValueAt(i, 2).toString();
            if (SDT.matches(".*\\D.*") && SDT.length() < 9) {
                MsgBox.alert(this, tblListKH.getValueAt(i, 0) + " - Số điện thoại Không hợp lệ '" + tblListKH.getValueAt(i, 2) + "'");
                continue;
            } else if (!checkTrungSDT(SDT)) {
                MsgBox.alert(this, tblListKH.getValueAt(i, 0) + " - Số điện thoại bị trùng!");
                continue;
            }
            khTemp.setSDT(SDT);

            khDAO.update(khTemp);
        }
        fillTable();
        MsgBox.alert(this, "Cập nhật khách hàng thành công!");
    }
//    void edit() {
//        String Makhachhang = (String) tblListKH.getValueAt(this.rowkh, 0);
//        KhachHang khachHang = khDAO.selectById(Makhachhang);
//        this.setForm(khachHang);
//        tblListKH.setRowSelectionInterval(this.rowkh, this.rowkh);
//        this.updateStatus();
//    }

    int rowBanAn = -1;

    public boolean checkBanAn() {
        if (txtMaBanAn.getText().length() > 5) {
            MsgBox.alert(this, "Mã bàn ăn phải ngắn hơn 6 ký tự!");
            return false;
        } else if (txtMaBanAn.getText().trim().equals("")) {
            MsgBox.alert(this, "Mã bàn ăn không được bỏ trống!");
            return false;
        } else if (txtViTri.getText().trim().equals("")) {
            MsgBox.alert(this, "Vị trí không được bỏ trống!");
            return false;
        }
        return true;
    }

    public void loadListBanAn() {
        DefaultTableModel model = (DefaultTableModel) tblListBanAn.getModel();
        model.setRowCount(0);

        List<BanAn> ba = baDAO.selectAll();

        for (BanAn ba1 : ba) {
            model.addRow(new Object[]{ba1.getMaB(), ba1.isTrangThai() ? "Đang Sử dụng" : "Không sử dụng", ba1.getViTri(), ba1.getGhiChu()});
        }
    }

    public void banAnNEW() {
        int rowBanAn = baDAO.getCountRow() + 1;

        txtMaBanAn.setText("B" + (rowBanAn < 10 ? "0" + rowBanAn : rowBanAn));
    }

    public BanAn getFromBanAn() {
        BanAn baTemp = new BanAn();
        baTemp.setMaB(txtMaBanAn.getText());
        baTemp.setTrangThai(true);
        baTemp.setViTri(txtViTri.getText());
        baTemp.setGhiChu(txtGhiChu.getText());

        return baTemp;
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBanAn = new javax.swing.JTable();
        jLabel22 = new javax.swing.JLabel();
        cboBATrangThai = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblThucDon = new javax.swing.JTable();
        cboLoaiMonAn = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txtTimKiemMonAn = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblNgayLap = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        cuon = new javax.swing.JScrollPane();
        tblHoaDonChiTiet = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        lblTongTien = new javax.swing.JLabel();
        btnXoaMonAn = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtTimKiemKhachHang = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btnThanhToan = new javax.swing.JButton();
        btnGopBan = new javax.swing.JButton();
        lblTenKhachHang = new javax.swing.JLabel();
        btnThongBaoBep = new javax.swing.JButton();
        btnInHoaDon = new javax.swing.JButton();
        lblBanAn = new javax.swing.JLabel();
        btnTaoHD = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        lblMaHoaDon = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtKhachTra = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtThoiLai = new javax.swing.JTextField();
        pnlKhachHang = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        txtTenKH = new javax.swing.JTextField();
        txtSDT = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtmaKH = new javax.swing.JTextField();
        btnThem = new javax.swing.JButton();
        btntimKiem = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblListKH = new javax.swing.JTable();
        btnXoa = new javax.swing.JButton();
        btnCapNhat = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        txtMaBanAn = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtViTri = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtGhiChu = new javax.swing.JTextField();
        btnThemBanAn = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblListBanAn = new javax.swing.JTable();
        btnCapNhatBanAn = new javax.swing.JButton();
        btnDoiTrangThai = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Bán Hàng");

        jTabbedPane1.setBackground(new java.awt.Color(255, 153, 153));

        jTabbedPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 51)));

        tblBanAn.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"B01", "Nguyễn Tấn Tài", "Chưa Thanh Toán"},
                {"B02", "Trống", "Chưa Sử Dụng"},
                {"B03", "Trống", "Chưa Sử Dụng"},
                {"B04", "Nguyễn Thị Ngọc Nghi", "Chưa Sử Dụng"},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Mã bàn", "Tên Khách hàng", "Trạng thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblBanAn.setSelectionBackground(new java.awt.Color(204, 204, 204));
        tblBanAn.setShowGrid(true);
        tblBanAn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBanAnMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblBanAn);

        jLabel22.setText("Trạng thái:");

        cboBATrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Chưa thanh toán", "Chờ sử dụng" }));
        cboBATrangThai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboBATrangThaiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(391, 391, 391)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboBATrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(cboBATrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 591, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Bàn ăn", jPanel2);

        tblThucDon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"", null}
            },
            new String [] {
                "Hình ảnh", "Thông tin"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblThucDon.setRowHeight(130);
        tblThucDon.setSelectionBackground(new java.awt.Color(102, 255, 51));
        tblThucDon.setShowGrid(true);
        tblThucDon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblThucDonMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblThucDon);

        cboLoaiMonAn.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        cboLoaiMonAn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLoaiMonAnActionPerformed(evt);
            }
        });

        jLabel23.setText("Loại món ăn:");

        jLabel24.setText("Tên món ăn:");

        txtTimKiemMonAn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTimKiemMonAnKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4)
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboLoaiMonAn, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 104, Short.MAX_VALUE)
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTimKiemMonAn, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboLoaiMonAn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24)
                    .addComponent(txtTimKiemMonAn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Thực đơn", jPanel3);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 51)), "HÓA ĐƠN", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(255, 0, 51))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 51, 51));
        jLabel1.setText("Ngày lập:");

        lblNgayLap.setForeground(new java.awt.Color(255, 51, 51));
        lblNgayLap.setText("0");

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 51)), "HÓA ĐƠN CHI TIẾT", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(255, 0, 51))); // NOI18N

        tblHoaDonChiTiet.setAutoCreateRowSorter(true);
        tblHoaDonChiTiet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Tên món", "Số lượng", "Đơn giá", "Thành Tiền"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblHoaDonChiTiet.setGridColor(new java.awt.Color(255, 102, 102));
        tblHoaDonChiTiet.setSelectionBackground(new java.awt.Color(255, 204, 204));
        tblHoaDonChiTiet.setShowGrid(true);
        tblHoaDonChiTiet.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                tblHoaDonChiTietAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        tblHoaDonChiTiet.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblHoaDonChiTietFocusGained(evt);
            }
        });
        tblHoaDonChiTiet.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblHoaDonChiTietMouseClicked(evt);
            }
        });
        tblHoaDonChiTiet.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                tblHoaDonChiTietInputMethodTextChanged(evt);
            }
        });
        tblHoaDonChiTiet.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblHoaDonChiTietKeyPressed(evt);
            }
        });
        cuon.setViewportView(tblHoaDonChiTiet);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 0, 51));
        jLabel7.setText("Tổng Tiền:");

        lblTongTien.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblTongTien.setForeground(new java.awt.Color(255, 0, 51));
        lblTongTien.setText("0");

        btnXoaMonAn.setBackground(new java.awt.Color(255, 0, 51));
        btnXoaMonAn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXoaMonAn.setForeground(new java.awt.Color(255, 255, 255));
        btnXoaMonAn.setText("Xóa món ăn");
        btnXoaMonAn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaMonAnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cuon, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(btnXoaMonAn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addGap(4, 4, 4)
                        .addComponent(lblTongTien, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cuon, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(lblTongTien)
                    .addComponent(btnXoaMonAn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 0, 51));
        jLabel5.setText("Khách hàng (SĐT):");

        txtTimKiemKhachHang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTimKiemKhachHangKeyPressed(evt);
            }
        });

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("02");

        btnThanhToan.setBackground(new java.awt.Color(0, 204, 0));
        btnThanhToan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThanhToan.setForeground(new java.awt.Color(255, 255, 255));
        btnThanhToan.setText("Thanh toán");
        btnThanhToan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThanhToanActionPerformed(evt);
            }
        });

        btnGopBan.setBackground(new java.awt.Color(0, 153, 255));
        btnGopBan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnGopBan.setForeground(new java.awt.Color(255, 255, 255));
        btnGopBan.setText("Gộp bàn / Chuyển bàn");
        btnGopBan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGopBanActionPerformed(evt);
            }
        });

        btnThongBaoBep.setBackground(new java.awt.Color(0, 153, 255));
        btnThongBaoBep.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThongBaoBep.setForeground(new java.awt.Color(255, 255, 255));
        btnThongBaoBep.setText("Thông báo bếp");
        btnThongBaoBep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThongBaoBepActionPerformed(evt);
            }
        });

        btnInHoaDon.setBackground(new java.awt.Color(0, 153, 255));
        btnInHoaDon.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnInHoaDon.setForeground(new java.awt.Color(255, 255, 255));
        btnInHoaDon.setText("In hóa đơn");
        btnInHoaDon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInHoaDonActionPerformed(evt);
            }
        });

        lblBanAn.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblBanAn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBanAn.setText("Bàn 0");
        lblBanAn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 3));
        lblBanAn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        btnTaoHD.setBackground(new java.awt.Color(0, 153, 255));
        btnTaoHD.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTaoHD.setForeground(new java.awt.Color(255, 255, 255));
        btnTaoHD.setText("Tạo");
        btnTaoHD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTaoHDActionPerformed(evt);
            }
        });

        jLabel2.setText("Mã hóa đơn:");

        lblMaHoaDon.setText("0");

        jLabel3.setText("khách trả:");

        txtKhachTra.setText("0");
        txtKhachTra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKhachTraActionPerformed(evt);
            }
        });
        txtKhachTra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtKhachTraKeyPressed(evt);
            }
        });

        jLabel8.setText("Thối lại:");

        txtThoiLai.setText("0");
        txtThoiLai.setEnabled(false);
        txtThoiLai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtThoiLaiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(btnThongBaoBep, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnTaoHD, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnGopBan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 209, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnThanhToan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnInHoaDon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblBanAn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(71, 71, 71)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(lblMaHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lblTenKhachHang, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtTimKiemKhachHang, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(29, 29, 29)
                                .addComponent(lblNgayLap, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtKhachTra)
                            .addComponent(txtThoiLai, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblBanAn, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(lblMaHoaDon))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(lblNgayLap))))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtTimKiemKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtKhachTra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTenKhachHang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(txtThoiLai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGopBan)
                    .addComponent(btnInHoaDon))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThanhToan)
                    .addComponent(btnThongBaoBep)
                    .addComponent(btnTaoHD))
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 581, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane2))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Bán Hàng", jPanel1);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Thông tin", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        jLabel9.setText("Tên khách hàng:");

        jLabel10.setText("Số điện thoại:");

        jLabel11.setText("Mã khách hàng:");

        txtmaKH.setEnabled(false);
        txtmaKH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtmaKHActionPerformed(evt);
            }
        });

        btnThem.setText("Thêm");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        btntimKiem.setText("Tìm kiếm");
        btntimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btntimKiemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTenKH, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSDT)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtmaKH, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(btntimKiem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnThem)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtTenKH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtmaKH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnThem)
                    .addComponent(btntimKiem))
                .addGap(18, 18, 18))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Danh Sách", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        tblListKH.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Mã khách hàng", "Tên khách hàng", "Số điện thoại"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblListKH.setShowGrid(true);
        tblListKH.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblListKHMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblListKH);

        btnXoa.setText("Xóa");
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        btnCapNhat.setText("Cập nhật");
        btnCapNhat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCapNhatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnXoa)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCapNhat)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnXoa)
                    .addComponent(btnCapNhat))
                .addGap(21, 21, 21))
        );

        javax.swing.GroupLayout pnlKhachHangLayout = new javax.swing.GroupLayout(pnlKhachHang);
        pnlKhachHang.setLayout(pnlKhachHangLayout);
        pnlKhachHangLayout.setHorizontalGroup(
            pnlKhachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlKhachHangLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlKhachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlKhachHangLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 500, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlKhachHangLayout.setVerticalGroup(
            pnlKhachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlKhachHangLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Khách Hàng", pnlKhachHang);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 51)), "Thông tin", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(255, 0, 51))); // NOI18N

        txtMaBanAn.setEditable(false);

        jLabel4.setText("Mã bàn ăn:");

        jLabel12.setText("Vị trí:");

        jLabel13.setText("Ghi chú:");

        btnThemBanAn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnThemBanAn.setText("Thêm");
        btnThemBanAn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemBanAnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(txtMaBanAn, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(137, 137, 137)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtViTri, javax.swing.GroupLayout.PREFERRED_SIZE, 548, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtGhiChu, javax.swing.GroupLayout.PREFERRED_SIZE, 883, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(btnThemBanAn)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnThemBanAn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMaBanAn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(txtViTri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(txtGhiChu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 1, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jScrollPane2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane2MouseClicked(evt);
            }
        });

        tblListBanAn.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Mã bàn ăn", "Trạng thái", "Vị trí", "Ghi chú"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblListBanAn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblListBanAnMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblListBanAn);

        btnCapNhatBanAn.setText("Cập nhật");
        btnCapNhatBanAn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCapNhatBanAnActionPerformed(evt);
            }
        });

        btnDoiTrangThai.setText("Đổi trạng thái");
        btnDoiTrangThai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDoiTrangThaiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDoiTrangThai)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCapNhatBanAn)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCapNhatBanAn)
                    .addComponent(btnDoiTrangThai))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Bàn Ăn", jPanel7);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCapNhatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCapNhatActionPerformed
        // TODO add your handling code here:
        capNhatKhachHang();
    }//GEN-LAST:event_btnCapNhatActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        // TODO add your handling code here:
        if (rowkh >= 0) {
            if (MsgBox.confirm(this, "Bạn có muốn xóa khách hàng?")) {
                xoaKhachHang();
            }
        } else {
            MsgBox.alert(this, "Vui lòng chọn khách hàng cần xóa!");
        }
    }//GEN-LAST:event_btnXoaActionPerformed

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        // TODO add your handling code here:
        if (!checkKhachHang()) {
            return;
        }
        KhachHang kh = getForm();
        kh = getKhachHangNew(kh);

        if (!checkTrungSDT(kh.getSDT())) {
            MsgBox.alert(this, "Số điện thoại bị trùng!");
            return;
        }

        try {
            khDAO.insert(kh);
            this.fillTable();
            setFormKhachHang();
            MsgBox.alert(this, "Thêm thành công!");
        } catch (Exception e) {
            System.out.println(e);
        }
    }//GEN-LAST:event_btnThemActionPerformed

    private void txtmaKHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtmaKHActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtmaKHActionPerformed

    private void btnThongBaoBepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThongBaoBepActionPerformed
        // TODO add your handling code here:
        if (!checkHoaDon()) {
            return;
        }
        Printer.printThongBaoBep(lblMaHoaDon.getText());
        JOptionPane.showMessageDialog(this, "Đã in thông báo bếp!");
    }//GEN-LAST:event_btnThongBaoBepActionPerformed

    private void cboBATrangThaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboBATrangThaiActionPerformed
        // TODO add your handling code here:
        loadBanAn();
    }//GEN-LAST:event_cboBATrangThaiActionPerformed

    private void tblBanAnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBanAnMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            this.row = tblBanAn.getSelectedRow();
            if (row >= 0) {
                loadHoaDon();
            }
        }
    }//GEN-LAST:event_tblBanAnMouseClicked

    private void cboLoaiMonAnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLoaiMonAnActionPerformed
        // TODO add your handling code here:
        loadThucDon();
    }//GEN-LAST:event_cboLoaiMonAnActionPerformed

    private void txtTimKiemMonAnKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTimKiemMonAnKeyPressed
        // TODO add your handling code here:
        loadThucDon();
    }//GEN-LAST:event_txtTimKiemMonAnKeyPressed

    private void btnInHoaDonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInHoaDonActionPerformed
        // TODO add your handling code here:
        if (!checkHoaDon()) {
            return;
        }
        if (!tinhTienThoi()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tiền khách trả!");
            return;
        }

        String tienKhachTra = txtKhachTra.getText().replaceAll("\\.", "");
        tienKhachTra = tienKhachTra.replaceAll(",", "");
        Printer.printHoaDon(lblMaHoaDon.getText(), Integer.parseInt(tienKhachTra));
        JOptionPane.showMessageDialog(this, "Đã in hóa đơn!");
    }//GEN-LAST:event_btnInHoaDonActionPerformed

    private void tblThucDonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblThucDonMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            if (tblThucDon.getSelectedRow() >= 0) {
                themMonAn();
            }
        }
    }//GEN-LAST:event_tblThucDonMouseClicked

    private void tblHoaDonChiTietMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblHoaDonChiTietMouseClicked
        // TODO add your handling code here:
        btnXoaMonAn.setEnabled(true);

    }//GEN-LAST:event_tblHoaDonChiTietMouseClicked

    private void tblHoaDonChiTietAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_tblHoaDonChiTietAncestorAdded
        // TODO add your handling code here:

    }//GEN-LAST:event_tblHoaDonChiTietAncestorAdded

    private void tblHoaDonChiTietInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_tblHoaDonChiTietInputMethodTextChanged
        // TODO add your handling code here:

    }//GEN-LAST:event_tblHoaDonChiTietInputMethodTextChanged

    private void tblHoaDonChiTietKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblHoaDonChiTietKeyPressed
        // TODO add your handling code here:

    }//GEN-LAST:event_tblHoaDonChiTietKeyPressed

    private void txtTimKiemKhachHangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTimKiemKhachHangKeyPressed
        // TODO add your handling code here:
        KhachHang khTemp = timKhachHangbySDT(txtTimKiemKhachHang.getText());
        lblTenKhachHang.setText(khTemp != null ? khTemp.getTenKH() : "");
    }//GEN-LAST:event_txtTimKiemKhachHangKeyPressed

    private void btnTaoHDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTaoHDActionPerformed
        // TODO add your handling code here:
        if (!checkHoaDon()) {
            System.out.println("Hoa Don Khong Duoc Luu");
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng");
            return;
        }
        hdDAO.insert(getFrom(0));
        hd = hdDAO.selectByTrangThai("0");
        rowBA = baDAO.getCountRow();
        loadBanAn();
    }//GEN-LAST:event_btnTaoHDActionPerformed

    private void tblHoaDonChiTietFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblHoaDonChiTietFocusGained
        // TODO add your handling code here:
        capNhatHDCT();
    }//GEN-LAST:event_tblHoaDonChiTietFocusGained

    private void btnGopBanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGopBanActionPerformed
        // TODO add your handling code here:
        if (!checkHoaDon()) {
            return;
        }

        gopBanAn();
    }//GEN-LAST:event_btnGopBanActionPerformed

    private void btnThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThanhToanActionPerformed
        // TODO add your handling code here:
        if (!checkHoaDon()) {
            System.out.println("Hóa đơn không hợp lệ");
            return;
        }
        HoaDon hdTemp = hdDAO.selectById(Integer.valueOf(lblMaHoaDon.getText()));
        hdTemp.setTrangThai(1);

        hdDAO.update(hdTemp);
        hd = hdDAO.selectByTrangThai("0");
        loadBanAn();
        loadHoaDon();
    }//GEN-LAST:event_btnThanhToanActionPerformed

    private void btnXoaMonAnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaMonAnActionPerformed
        // TODO add your handling code here:
        List<HoaDonChiTiet> hdctTemp = hdctDAO.selectHDCT(lblMaHoaDon.getText());
        for (int x : tblHoaDonChiTiet.getSelectedRows()) {
            hdctDAO.delete(lblMaHoaDon.getText(), hdctTemp.get(x - 1).getMaMon());
            hdctTemp.remove(x - 1);
        }

        loadHoaDonChiTiet(hdctTemp);
        tinhTienThoi();
    }//GEN-LAST:event_btnXoaMonAnActionPerformed

    private void btntimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btntimKiemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btntimKiemActionPerformed

    private void tblListKHMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblListKHMouseClicked
        // TODO add your handling code here:
        this.rowkh = tblListKH.getSelectedRow();
        if (evt.getClickCount() == 2) {
            if (this.rowkh >= 0) {
                this.edit();
            }
        }
    }//GEN-LAST:event_tblListKHMouseClicked

    private void txtKhachTraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKhachTraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKhachTraActionPerformed

    private void txtThoiLaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtThoiLaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtThoiLaiActionPerformed

    private void txtKhachTraKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKhachTraKeyPressed
        // TODO add your handling code here:
        if (!checkHoaDon()) {
            return;
        }
        tinhTienThoi();
    }//GEN-LAST:event_txtKhachTraKeyPressed

    private void btnThemBanAnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemBanAnActionPerformed
        // TODO add your handling code here:
        if (!checkBanAn()) {
            return;
        }
        BanAn baTemp = getFromBanAn();

        baDAO.insert(baTemp);
        loadListBanAn();
        loadBanAn();
    }//GEN-LAST:event_btnThemBanAnActionPerformed

    private void jScrollPane2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jScrollPane2MouseClicked

    private void tblListBanAnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblListBanAnMouseClicked
        // TODO add your handling code here:
        rowBanAn = tblListBanAn.getSelectedRow();
        if (rowBanAn < 0) {
            btnDoiTrangThai.setEnabled(false);
            btnCapNhatBanAn.setEnabled(false);
        } else {
            btnDoiTrangThai.setEnabled(true);
            btnCapNhatBanAn.setEnabled(true);
        }
    }//GEN-LAST:event_tblListBanAnMouseClicked

    private void btnCapNhatBanAnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCapNhatBanAnActionPerformed
        // TODO add your handling code here:
        for (int i = 0; i < tblListBanAn.getRowCount(); i++) {
            if (tblListBanAn.getValueAt(i, 2).toString().trim().equals("")) {
                MsgBox.alert(this, "Bàn ăn: " + tblListBanAn.getValueAt(i, 0) + " - vị trí không được để trống!");
                continue;
            }
            BanAn baTemp = new BanAn();
            baTemp.setMaB(tblListBanAn.getValueAt(i, 0).toString());
            baTemp.setTrangThai(tblListBanAn.getValueAt(i, 1).toString().equalsIgnoreCase("Đang Sử dụng"));
            baTemp.setViTri(tblListBanAn.getValueAt(i, 2) == null?"":tblListBanAn.getValueAt(i, 2).toString());
            baTemp.setGhiChu(tblListBanAn.getValueAt(i, 3) == null?"":tblListBanAn.getValueAt(i, 3).toString());

            baDAO.update(baTemp);
        }
        loadListBanAn();
        MsgBox.alert(this, "Đã cập nhật bàn ăn!");
    }//GEN-LAST:event_btnCapNhatBanAnActionPerformed

    private void btnDoiTrangThaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDoiTrangThaiActionPerformed
        // TODO add your handling code here:
        int rowTemp = tblListBanAn.getSelectedRow();

        BanAn baTemp = baDAO.selectById(tblListBanAn.getValueAt(rowTemp, 0).toString());

        List<HoaDon> listHD = hdDAO.selectByTrangThai("0");

        for (HoaDon hd1 : listHD) {
            if (baTemp.getMaB().equalsIgnoreCase(hd1.getMaB())) {
                MsgBox.alert(this, "Bàn ăn: " + baTemp.getMaB() + " Đang có khách không thể đổi trạng thái!");
                return;
            }
        }

        baTemp.setTrangThai(!baTemp.isTrangThai());

        baDAO.update(baTemp);

        loadListBanAn();

        tblListBanAn.setRowSelectionInterval(rowTemp, rowTemp);
        
        loadBanAn();

    }//GEN-LAST:event_btnDoiTrangThaiActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BanHangJFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BanHangJFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BanHangJFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BanHangJFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BanHangJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCapNhat;
    private javax.swing.JButton btnCapNhatBanAn;
    private javax.swing.JButton btnDoiTrangThai;
    private javax.swing.JButton btnGopBan;
    private javax.swing.JButton btnInHoaDon;
    private javax.swing.JButton btnTaoHD;
    private javax.swing.JButton btnThanhToan;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnThemBanAn;
    private javax.swing.JButton btnThongBaoBep;
    private javax.swing.JButton btnXoa;
    private javax.swing.JButton btnXoaMonAn;
    private javax.swing.JButton btntimKiem;
    private javax.swing.JComboBox<String> cboBATrangThai;
    private javax.swing.JComboBox<String> cboLoaiMonAn;
    private javax.swing.JScrollPane cuon;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel lblBanAn;
    private javax.swing.JLabel lblMaHoaDon;
    private javax.swing.JLabel lblNgayLap;
    private javax.swing.JLabel lblTenKhachHang;
    private javax.swing.JLabel lblTongTien;
    private javax.swing.JPanel pnlKhachHang;
    private javax.swing.JTable tblBanAn;
    private javax.swing.JTable tblHoaDonChiTiet;
    private javax.swing.JTable tblListBanAn;
    private javax.swing.JTable tblListKH;
    private javax.swing.JTable tblThucDon;
    private javax.swing.JTextField txtGhiChu;
    private javax.swing.JTextField txtKhachTra;
    private javax.swing.JTextField txtMaBanAn;
    private javax.swing.JTextField txtSDT;
    private javax.swing.JTextField txtTenKH;
    private javax.swing.JTextField txtThoiLai;
    private javax.swing.JTextField txtTimKiemKhachHang;
    private javax.swing.JTextField txtTimKiemMonAn;
    private javax.swing.JTextField txtViTri;
    private javax.swing.JTextField txtmaKH;
    // End of variables declaration//GEN-END:variables
}
