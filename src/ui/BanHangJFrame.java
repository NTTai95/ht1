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
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;
import java.net.URI;
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
        init();
        ImageIcon icon = new ImageIcon("./img/logo.jpg");
        setIconImage(icon.getImage());
    }

    void init() {
        setTitle("Hệ thống quản lý nhà hàng L'ESSALE - Bán Hàng");
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
        } else if (lblTenKhachHang.getText().equals("không tìm thấy!")) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng!");
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
        lblTenKhachHang.setText(khTemp == null ? "không tìm thấy!" : khTemp.getTenKH());
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
            ImageIcon anh = new ImageIcon(hinhAnh);
            Image img = anh.getImage().getScaledInstance(130, 130, Image.SCALE_SMOOTH);

            model.addRow(new Object[]{ma.get(i).getMaMon(), new ImageIcon(img), "<html><h2 style=\"color: red; margin-top: 0px;\">" + tenMon + "</h2><h4 style=\"margin: 0px;\">" + gia + "</h4><i style=\"margin: 0px;\">" + loaiMon + "</i></html>"});
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

        String color = "rgb(0,153,204)";

        row = -1;
        loadHoaDon();

        int rowBA = baDAO.getCountRow();
        System.out.println(rowBA);
        List<BanAn> banAnAn = baDAO.selectByTrangThai(false);

        for (int i = 1; i <= rowBA; i++) {

            boolean boQua = false;

            String maBA = "B" + (i < 10 ? "00" + i : i < 100 ? "0" + i : i);
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

            for (BanAn BATemp : banAnAn) {
                if (BATemp.getMaB().equalsIgnoreCase(maBA)) {
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
            btnChuyenBan.setEnabled(false);
            txtKhachTra.setText("");
            tinhTienThoi();
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
                    btnChuyenBan.setEnabled(true);
                    txtKhachTra.setText("");
                    tinhTienThoi();
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
            btnChuyenBan.setEnabled(false);
            txtKhachTra.setText("");
            tinhTienThoi();
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
            MsgBox.alert(this, "Vui lòng thêm khách hàng trước!");
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

        hdctTemp = hdctDAO.selectHDCT(String.valueOf(maHD));

        loadHoaDonChiTiet(hdctTemp);
        tinhTienThoi();

    }

    public void capNhatHDCT() {
        DefaultTableModel model = (DefaultTableModel) tblHoaDonChiTiet.getModel();
        List<HoaDonChiTiet> hdctTemp = hdctDAO.selectHDCT(lblMaHoaDon.getText());

        int rowS = tblHoaDonChiTiet.getSelectedRow();
        if (rowS == -1) {
            return;
        }
        try {
            if (Integer.parseInt(tblHoaDonChiTiet.getValueAt(rowS, 2).toString()) < 1) {
                MsgBox.alert(this, "Số lượng phải lớn hơn 0!");
                loadHoaDonChiTiet(hdctTemp);
                return;
            }
        } catch (Exception e) {
            MsgBox.alert(this, "Số lượng không hợp lệ!");
            loadHoaDonChiTiet(hdctTemp);
            return;
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            if (hdctTemp.get(i).getSoLuong() != Integer.parseInt(model.getValueAt(i, 2).toString())) {
                hdctTemp.get(i).setSoLuong(Integer.parseInt(model.getValueAt(i, 2).toString()));
                hdctDAO.update(hdctTemp.get(i));
                tblHoaDonChiTiet.setValueAt(hdctTemp.get(i).getDonGia() * hdctTemp.get(i).getSoLuong(), i, 4);
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

        List<BanAn> baAn = baDAO.selectByTrangThai(false);
        for (BanAn ba1 : baAn) {
            if (ba1.getMaB().equalsIgnoreCase(maBA)) {
                MsgBox.alert(this, "Bàn ăn '" + maBA + "' không được sử dụng!");
                return;
            }
        }

        List<BanAn> baAll = baDAO.selectAll();
        boolean check = true;
        for (BanAn ba1 : baAll) {
            if (ba1.getMaB().equalsIgnoreCase(maBA)) {
                check = false;
                break;
            }
        }
        if (check) {
            MsgBox.alert(this, "Không tìm thấy bàn ăn '" + maBA + "'");
            return;
        }

        check = true;
        for (HoaDon hd1 : hd) {
            if (hd1.getMaB().equalsIgnoreCase(maBA)) {
                check = false;
            }
        }

        if (check) {
            MsgBox.alert(this, "Không thể gợp với bàn trống!");
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
        try {
            String strTongTien = lblTongTien.getText().replaceAll("\\.", "");
            int tongTien = Integer.parseInt(strTongTien);
            String tienKhachTra = txtKhachTra.getText().replaceAll("\\.", "");
            tienKhachTra = tienKhachTra.replaceAll(",", "");
            if (Integer.parseInt(tienKhachTra) < tongTien) {
                txtThoiLai.setText("Chưa đủ!");
                return false;
            } else {
                txtThoiLai.setText(fmTien.format(Integer.parseInt(tienKhachTra) - tongTien));
                return true;
            }
        } catch(Exception e) {
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
        tblListKH.getTableHeader().setFont(new Font(tblListKH.getTableHeader().getFont().getFontName(), Font.BOLD, 14));
        tblBanAn.getTableHeader().setFont(new Font(tblBanAn.getTableHeader().getFont().getFontName(), Font.BOLD, 13));
        tblHoaDonChiTiet.getTableHeader().setFont(new Font(tblHoaDonChiTiet.getTableHeader().getFont().getFontName(), Font.BOLD, 12));
        tblListBanAn.getTableHeader().setFont(new Font(tblListBanAn.getTableHeader().getFont().getFontName(), Font.BOLD, 14));
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
        String tenKH = txtTenKH.getText();
        String SDT = txtSDT.getText();
        List<KhachHang> list = khDAO.selectByKeyword(tenKH, SDT);
        for (KhachHang kh : list) {
            Object[] rows = {kh.getMaKH(), kh.getTenKH(), kh.getSDT()};
            model.addRow(rows);
        }

    }

    void setForm(KhachHang kh) {
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
        if (txtTenKH.getText().trim().matches(".*\\d.*")) {
            JOptionPane.showMessageDialog(this, "Tên khách hàng không hợp lệ!");
            return false;
        } else if (txtSDT.getText().trim().matches(".*\\D.*")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không được nhập chữ!");
            return false;
        } else if (txtTenKH.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "Tên khách hàng không được bỏ trống!");
            return false;
        } else if (txtSDT.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không được bỏ trống!");
            return false;
        } else if (txtSDT.getText().trim().length() < 9) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không đủ!");
            return false;
        }
        return true;
    }

    KhachHang getForm() {
        KhachHang kh = new KhachHang();
        kh.setMaKH(txtmaKH.getText());
        if (txtTenKH.getText().matches(".*\\d.*")) {
            MsgBox.alert(this, "Tên không được nhập số!");
            return null;
        }
        kh.setTenKH(txtTenKH.getText());
        if (!txtSDT.getText().matches("\\d{9,}")) {
            MsgBox.alert(this, "Số điện thoại không được nhập chữ");
            return null;
        }
        kh.setSDT(txtSDT.getText());
        return kh;
    }

    KhachHang getKhachHangNew(KhachHang kh) {
        List<KhachHang> list = khDAO.selectAll();
        KhachHang khTemp = new KhachHang();
        khTemp.setTenKH(kh.getTenKH());
        khTemp.setSDT(kh.getSDT());
        int x = list.size() + 1;
        String maKH = "";
        if (x < 10) {
            maKH = "KH0000" + x;
        } else if (x < 100) {
            maKH = "KH000" + x;
        } else if (x < 1000) {
            maKH = "KH00" + x;
        } else if (x < 10000) {
            maKH = "KH0" + x;
        } else {
            maKH = "KH" + x;
        }
        khTemp.setMaKH(maKH);
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).getMaKH().equalsIgnoreCase(maKH)) {
                khTemp.setMaKH(maKH);
                break;
            }
        }
        return khTemp;
    }

    public boolean checkTrungSDT(KhachHang khNew) {
        KhachHang kh = khDAO.selectBySDT(khNew.getSDT());
        System.out.println("kh != null - :" + kh != null);
        if (khNew.getMaKH().equals("KH0032")) {
            System.out.println("ui.BanHangJFrame.checkTrungSDT()");
        }
        if (kh != null) {

            if (kh.getMaKH().equalsIgnoreCase(khNew.getMaKH())) {
                System.out.println("--Chính nó");
                return false;
            }
            return true;
        }
        return false;
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
        clearForm();
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
            khTemp.setSDT(SDT);
            if (SDT.matches(".*\\D.*") || SDT.length() < 9) {
                MsgBox.alert(this, tblListKH.getValueAt(i, 0) + " - Số điện thoại Không hợp lệ '" + tblListKH.getValueAt(i, 2) + "'");
                continue;
            } else if (checkTrungSDT(khTemp)) {
                KhachHang khNew = khDAO.selectBySDT(khTemp.getSDT());
                MsgBox.alert(this, khTemp.getMaKH() + " - " + khNew.getMaKH() + " - Số điện thoại bị trùng!");
                continue;
            }

            khDAO.update(khTemp);
        }
        clearForm();
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
        if (txtMaBanAn.getText().trim().length() > 5) {
            MsgBox.alert(this, "Mã bàn ăn tối thiểu 6 ký tự!");
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
            if (ba1.isTrangThai()) {
                model.addRow(new Object[]{ba1.getMaB(), "Đang Sử dụng", ba1.getViTri(), ba1.getGhiChu()});
            } else {
                model.addRow(new Object[]{ba1.getMaB(),
                    "<html><p style='color: rgb(255,153,153)'>Không sử dụng</p></html>",
                    ba1.getViTri(),
                    ba1.getGhiChu()});
            }

        }
    }

    public void banAnNEW() {
        int rowBanAn = baDAO.getCountRow() + 1;

        txtMaBanAn.setText("B" + (rowBanAn < 10 ? "00" + rowBanAn : rowBanAn < 100 ? "0" + rowBanAn : rowBanAn));
    }

    public BanAn getFromBanAn() {
        BanAn baTemp = new BanAn();
        baTemp.setMaB(txtMaBanAn.getText());
        baTemp.setTrangThai(true);
        baTemp.setViTri(txtViTri.getText());
        baTemp.setGhiChu(txtGhiChu.getText());

        return baTemp;
    }
    
    public void chuyenBanAn(){
       String maBA = JOptionPane.showInputDialog(this, "Nhập mã bàn muốn chuyển :", "Chuyển Bàn Ăn", JOptionPane.QUESTION_MESSAGE);
        maBA = maBA == null ? null : maBA.toUpperCase();

        if (maBA == null) {
            return;
        };
        
        if (!maBA.startsWith("B") || maBA.length() < 4 || !maBA.substring(1).matches("[0-9]{3}")) {
            JOptionPane.showMessageDialog(this, "Mã bàn ăn không hợp lệ!");
            return;
        }

        List<BanAn> baAn = baDAO.selectByTrangThai(false);
        for (BanAn ba1 : baAn) {
            if (ba1.getMaB().equalsIgnoreCase(maBA)) {
                MsgBox.alert(this, "Bàn ăn '" + maBA + "' không được sử dụng!");
                return;
            }
        }

        List<BanAn> baAll = baDAO.selectAll();
        boolean check = true;
        for (BanAn ba1 : baAll) {
            if (ba1.getMaB().equalsIgnoreCase(maBA)) {
                check = false;
                break;
            }
        }
        if (check) {
            MsgBox.alert(this, "Không tìm thấy bàn ăn '" + maBA + "'");
            return;
        }
        
        check = false;
        for (HoaDon hd1 : hd) {
            if (hd1.getMaB().equalsIgnoreCase(maBA)) {
                check = true;
            }
        }

        if (check) {
            MsgBox.alert(this, "Không thể chuyển sang bàn đang có khách!");
            return;
        }
        
        HoaDon hdTemp = hdDAO.selectById(Integer.valueOf(lblMaHoaDon.getText()));
        
        hdTemp.setMaB(maBA);
        
        hdDAO.update(hdTemp);
        
        hd = hdDAO.selectByTrangThai("0");
        
        loadBanAn();
        loadHoaDon();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabMain = new javax.swing.JTabbedPane();
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
        btnChuyenBan = new javax.swing.JButton();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Bán Hàng");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tabMain.setBackground(new java.awt.Color(255, 255, 255));
        tabMain.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tabMain.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        tabMain.setOpaque(true);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jTabbedPane2.setBackground(new java.awt.Color(255, 0, 51));
        jTabbedPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 51)));
        jTabbedPane2.setForeground(new java.awt.Color(255, 255, 255));
        jTabbedPane2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabbedPane2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 0, 51));

        jScrollPane1.setBackground(new java.awt.Color(255, 0, 51));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 1, 1, new java.awt.Color(255, 255, 255)));

        tblBanAn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
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
        tblBanAn.setFillsViewportHeight(true);
        tblBanAn.setGridColor(new java.awt.Color(204, 204, 204));
        tblBanAn.setRowHeight(25);
        tblBanAn.setSelectionBackground(new java.awt.Color(204, 204, 204));
        tblBanAn.setShowGrid(true);
        tblBanAn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBanAnMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblBanAn);
        if (tblBanAn.getColumnModel().getColumnCount() > 0) {
            tblBanAn.getColumnModel().getColumn(0).setMaxWidth(70);
            tblBanAn.getColumnModel().getColumn(2).setPreferredWidth(150);
            tblBanAn.getColumnModel().getColumn(2).setMaxWidth(150);
        }

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
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
                .addGap(379, 379, 379)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboBATrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(cboBATrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Bàn ăn", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 0, 51));

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
        tblThucDon.setSelectionBackground(new java.awt.Color(204, 204, 204));
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

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Loại món ăn:");

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
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
                .addGap(14, 14, 14)
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboLoaiMonAn, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 137, Short.MAX_VALUE)
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTimKiemMonAn, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
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
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Thực đơn", jPanel3);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "HÓA ĐƠN", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 0, 51));
        jLabel1.setText("Ngày lập:");

        lblNgayLap.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblNgayLap.setForeground(new java.awt.Color(255, 0, 51));
        lblNgayLap.setText("0");

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 1, 0, new java.awt.Color(0, 0, 0)), "HÓA ĐƠN CHI TIẾT", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

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
        tblHoaDonChiTiet.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tblHoaDonChiTiet.setGridColor(new java.awt.Color(255, 102, 102));
        tblHoaDonChiTiet.setRowHeight(25);
        tblHoaDonChiTiet.setSelectionBackground(new java.awt.Color(255, 204, 204));
        tblHoaDonChiTiet.setShowGrid(true);
        tblHoaDonChiTiet.addHierarchyListener(new java.awt.event.HierarchyListener() {
            public void hierarchyChanged(java.awt.event.HierarchyEvent evt) {
                tblHoaDonChiTietHierarchyChanged(evt);
            }
        });
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
        if (tblHoaDonChiTiet.getColumnModel().getColumnCount() > 0) {
            tblHoaDonChiTiet.getColumnModel().getColumn(0).setMinWidth(40);
            tblHoaDonChiTiet.getColumnModel().getColumn(0).setPreferredWidth(40);
            tblHoaDonChiTiet.getColumnModel().getColumn(0).setMaxWidth(40);
            tblHoaDonChiTiet.getColumnModel().getColumn(1).setMinWidth(130);
            tblHoaDonChiTiet.getColumnModel().getColumn(1).setPreferredWidth(180);
            tblHoaDonChiTiet.getColumnModel().getColumn(1).setMaxWidth(250);
            tblHoaDonChiTiet.getColumnModel().getColumn(2).setMinWidth(30);
            tblHoaDonChiTiet.getColumnModel().getColumn(2).setPreferredWidth(50);
            tblHoaDonChiTiet.getColumnModel().getColumn(2).setMaxWidth(100);
            tblHoaDonChiTiet.getColumnModel().getColumn(3).setMinWidth(50);
            tblHoaDonChiTiet.getColumnModel().getColumn(3).setPreferredWidth(90);
            tblHoaDonChiTiet.getColumnModel().getColumn(3).setMaxWidth(130);
            tblHoaDonChiTiet.getColumnModel().getColumn(4).setMinWidth(50);
            tblHoaDonChiTiet.getColumnModel().getColumn(4).setPreferredWidth(90);
            tblHoaDonChiTiet.getColumnModel().getColumn(4).setMaxWidth(130);
        }

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 0, 51));
        jLabel7.setText("Tổng Tiền:");

        lblTongTien.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
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
                .addComponent(btnXoaMonAn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(4, 4, 4)
                .addComponent(lblTongTien, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(cuon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(cuon, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTongTien, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnXoaMonAn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(7, 7, 7))
        );

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 0, 51));
        jLabel5.setText("Khách hàng (SĐT):");

        txtTimKiemKhachHang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTimKiemKhachHangKeyPressed(evt);
            }
        });

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
        btnGopBan.setText("Gộp bàn");
        btnGopBan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGopBanActionPerformed(evt);
            }
        });

        lblTenKhachHang.setText("không tìm thấy!");

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

        lblBanAn.setBackground(new java.awt.Color(255, 255, 255));
        lblBanAn.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblBanAn.setForeground(new java.awt.Color(51, 51, 51));
        lblBanAn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBanAn.setText("Bàn 0");
        lblBanAn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51), 3));
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

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Mã hóa đơn:");

        lblMaHoaDon.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblMaHoaDon.setText("0");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
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

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Thối lại:");

        txtThoiLai.setText("0");
        txtThoiLai.setEnabled(false);
        txtThoiLai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtThoiLaiActionPerformed(evt);
            }
        });

        btnChuyenBan.setBackground(new java.awt.Color(0, 153, 255));
        btnChuyenBan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnChuyenBan.setForeground(new java.awt.Color(255, 255, 255));
        btnChuyenBan.setText("Chuyển bàn");
        btnChuyenBan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChuyenBanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblBanAn, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtTimKiemKhachHang, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                                    .addComponent(lblTenKhachHang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtKhachTra, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                                    .addComponent(txtThoiLai)))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblNgayLap, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblMaHoaDon, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(71, 71, 71))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(btnThongBaoBep, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnTaoHD, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(btnGopBan, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(btnChuyenBan)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnInHoaDon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnThanhToan, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(67, 67, 67))))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBanAn, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(lblMaHoaDon))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(lblNgayLap))))
                .addGap(7, 7, 7)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTimKiemKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(5, 5, 5)
                        .addComponent(lblTenKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtKhachTra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtThoiLai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))))
                .addGap(18, 18, 18)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnInHoaDon)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnGopBan)
                        .addComponent(btnChuyenBan)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThongBaoBep)
                    .addComponent(btnTaoHD)
                    .addComponent(btnThanhToan))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 614, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 554, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane2)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        tabMain.addTab("Bán Hàng", jPanel1);

        pnlKhachHang.setBackground(new java.awt.Color(255, 255, 255));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 0, 51), 2), "Thông tin", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(204, 0, 51))); // NOI18N

        txtTenKH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTenKHActionPerformed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setText("Tên khách hàng:");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Số điện thoại:");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setText("Mã khách hàng:");

        txtmaKH.setEnabled(false);
        txtmaKH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtmaKHActionPerformed(evt);
            }
        });

        btnThem.setBackground(new java.awt.Color(0, 255, 0));
        btnThem.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThem.setForeground(new java.awt.Color(255, 255, 255));
        btnThem.setText("Thêm");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        btntimKiem.setBackground(new java.awt.Color(0, 153, 255));
        btntimKiem.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btntimKiem.setForeground(new java.awt.Color(255, 255, 255));
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
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(8, 8, 8)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTenKH, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSDT, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btntimKiem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnThem))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtmaKH))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                .addGap(0, 13, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(2, 0, 2, 0, new java.awt.Color(204, 0, 51)), "Danh Sách", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(204, 0, 51))); // NOI18N

        jScrollPane3.setBorder(new javax.swing.border.MatteBorder(null));
        jScrollPane3.setViewportBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));

        tblListKH.setAutoCreateRowSorter(true);
        tblListKH.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        tblListKH.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
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
        tblListKH.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tblListKH.setFillsViewportHeight(true);
        tblListKH.setGridColor(new java.awt.Color(153, 153, 153));
        tblListKH.setRowHeight(25);
        tblListKH.setSelectionBackground(new java.awt.Color(204, 204, 204));
        tblListKH.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblListKH.setShowGrid(true);
        tblListKH.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblListKHMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblListKH);
        if (tblListKH.getColumnModel().getColumnCount() > 0) {
            tblListKH.getColumnModel().getColumn(0).setMinWidth(100);
            tblListKH.getColumnModel().getColumn(0).setPreferredWidth(150);
            tblListKH.getColumnModel().getColumn(0).setMaxWidth(200);
            tblListKH.getColumnModel().getColumn(2).setMinWidth(250);
            tblListKH.getColumnModel().getColumn(2).setPreferredWidth(250);
            tblListKH.getColumnModel().getColumn(2).setMaxWidth(450);
        }

        btnXoa.setBackground(new java.awt.Color(255, 0, 51));
        btnXoa.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnXoa.setForeground(new java.awt.Color(255, 255, 255));
        btnXoa.setText("Xóa");
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        btnCapNhat.setBackground(new java.awt.Color(0, 153, 255));
        btnCapNhat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCapNhat.setForeground(new java.awt.Color(255, 255, 255));
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(1012, Short.MAX_VALUE)
                .addComponent(btnXoa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCapNhat)
                .addContainerGap())
            .addComponent(jScrollPane3)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnXoa)
                    .addComponent(btnCapNhat))
                .addContainerGap())
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
                        .addGap(0, 598, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlKhachHangLayout.setVerticalGroup(
            pnlKhachHangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlKhachHangLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabMain.addTab("Khách Hàng", pnlKhachHang);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Thông tin", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        txtMaBanAn.setEnabled(false);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Mã bàn ăn:");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel12.setText("Vị trí:");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel13.setText("Ghi chú:");

        btnThemBanAn.setBackground(new java.awt.Color(0, 255, 0));
        btnThemBanAn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnThemBanAn.setForeground(new java.awt.Color(255, 255, 255));
        btnThemBanAn.setText("Thêm");
        btnThemBanAn.setBorder(null);
        btnThemBanAn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(txtGhiChu, javax.swing.GroupLayout.PREFERRED_SIZE, 799, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnThemBanAn, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(txtMaBanAn, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(137, 137, 137)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtViTri, javax.swing.GroupLayout.PREFERRED_SIZE, 545, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMaBanAn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtViTri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(13, 13, 13)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtGhiChu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnThemBanAn, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        jScrollPane2.setBorder(new javax.swing.border.MatteBorder(null));
        jScrollPane2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane2MouseClicked(evt);
            }
        });

        tblListBanAn.setAutoCreateRowSorter(true);
        tblListBanAn.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        tblListBanAn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
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
        tblListBanAn.setFillsViewportHeight(true);
        tblListBanAn.setGridColor(new java.awt.Color(153, 153, 153));
        tblListBanAn.setRowHeight(25);
        tblListBanAn.setShowGrid(true);
        tblListBanAn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblListBanAnMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblListBanAn);
        if (tblListBanAn.getColumnModel().getColumnCount() > 0) {
            tblListBanAn.getColumnModel().getColumn(0).setMinWidth(60);
            tblListBanAn.getColumnModel().getColumn(0).setPreferredWidth(60);
            tblListBanAn.getColumnModel().getColumn(0).setMaxWidth(100);
            tblListBanAn.getColumnModel().getColumn(1).setMinWidth(100);
            tblListBanAn.getColumnModel().getColumn(1).setPreferredWidth(120);
            tblListBanAn.getColumnModel().getColumn(1).setMaxWidth(160);
            tblListBanAn.getColumnModel().getColumn(2).setMinWidth(50);
            tblListBanAn.getColumnModel().getColumn(2).setPreferredWidth(200);
            tblListBanAn.getColumnModel().getColumn(2).setMaxWidth(500);
            tblListBanAn.getColumnModel().getColumn(3).setMinWidth(50);
            tblListBanAn.getColumnModel().getColumn(3).setPreferredWidth(200);
            tblListBanAn.getColumnModel().getColumn(3).setMaxWidth(500);
        }

        btnCapNhatBanAn.setBackground(new java.awt.Color(0, 153, 255));
        btnCapNhatBanAn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCapNhatBanAn.setForeground(new java.awt.Color(255, 255, 255));
        btnCapNhatBanAn.setText("Cập nhật");
        btnCapNhatBanAn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCapNhatBanAnActionPerformed(evt);
            }
        });

        btnDoiTrangThai.setBackground(new java.awt.Color(255, 0, 51));
        btnDoiTrangThai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDoiTrangThai.setForeground(new java.awt.Color(255, 255, 255));
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
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1189, Short.MAX_VALUE)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
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
                .addGap(32, 32, 32)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCapNhatBanAn)
                    .addComponent(btnDoiTrangThai))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        tabMain.addTab("Bàn Ăn", jPanel7);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabMain)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabMain)
        );

        pack();
        setLocationRelativeTo(null);
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
        if (kh == null) {
            return;
        }
        kh = getKhachHangNew(kh);

        if (checkTrungSDT(kh)) {
            MsgBox.alert(this, "Số điện thoại bị trùng!");
            return;
        }

        try {
            khDAO.insert(kh);
            setFormKhachHang();
            txtSDT.setText("");
            txtTenKH.setText("");
            clearForm();
            this.fillTable();
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
        if (Printer.inThongBaoBep(lblMaHoaDon.getText())) {
            JOptionPane.showMessageDialog(this, "Đã in thông báo bếp!");
        } else {
            MsgBox.alert(this, "Thông báo bếp chưa được in!");
        }

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
        tienKhachTra = tienKhachTra.replaceAll(" ", "");
        if (Printer.inHoaDon(lblMaHoaDon.getText(), Integer.parseInt(tienKhachTra))) {
            JOptionPane.showMessageDialog(this, "Đã in hóa đơn!");
        } else {
            MsgBox.alert(this, "Hóa đơn chưa được in!");
        }

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
        lblTenKhachHang.setText(khTemp != null ? khTemp.getTenKH() : "không tìm thấy!");
    }//GEN-LAST:event_txtTimKiemKhachHangKeyPressed

    private void btnTaoHDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTaoHDActionPerformed
        // TODO add your handling code here:
        if (!checkHoaDon()) {
            System.out.println("Hoa Don Khong Duoc Luu");
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng");
            return;
        }
        int rowTemp = row;
        hdDAO.insert(getFrom(0));
        hd = hdDAO.selectByTrangThai("0");
        loadBanAn();
        row = rowTemp;
        tblBanAn.setRowSelectionInterval(row, row);
        loadHoaDon();
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
        
        if (!tinhTienThoi()) {
            JOptionPane.showMessageDialog(this, "Tiền khách trả chưa nhập hoặc chưa đủ!");
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
        fillTable();
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
        banAnNEW();
        MsgBox.alert(this, "Đã thêm bàn ăn!");
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
                MsgBox.alert(this, "Bàn ăn: " + tblListBanAn.getValueAt(i, 0).toString() + " - vị trí không được để trống!");
                continue;
            }
            BanAn baTemp = new BanAn();
            baTemp.setMaB(tblListBanAn.getValueAt(i, 0).toString());
            baTemp.setTrangThai(getValueTable(tblListBanAn.getValueAt(i, 1).toString()).equalsIgnoreCase("Đang Sử dụng"));
            baTemp.setViTri(tblListBanAn.getValueAt(i, 2) == null ? "" : tblListBanAn.getValueAt(i, 2).toString());
            baTemp.setGhiChu(tblListBanAn.getValueAt(i, 3) == null ? "" : tblListBanAn.getValueAt(i, 3).toString());

            baDAO.update(baTemp);
        }
        loadListBanAn();
        MsgBox.alert(this, "Đã cập nhật bàn ăn!");
    }//GEN-LAST:event_btnCapNhatBanAnActionPerformed

    private void btnDoiTrangThaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDoiTrangThaiActionPerformed
        // TODO add your handling code here:
        int rowTemp = tblListBanAn.getSelectedRow();

        BanAn baTemp = baDAO.selectById(getValueTable(tblListBanAn.getValueAt(rowTemp, 0).toString()));

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

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        this.dispose();
        new TrangChuJFrame().setVisible(true);
    }//GEN-LAST:event_formWindowClosing

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:

    }//GEN-LAST:event_formWindowClosed

    private void txtTenKHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTenKHActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTenKHActionPerformed

    private void tblHoaDonChiTietHierarchyChanged(java.awt.event.HierarchyEvent evt) {//GEN-FIRST:event_tblHoaDonChiTietHierarchyChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tblHoaDonChiTietHierarchyChanged

    private void btnChuyenBanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChuyenBanActionPerformed
        // TODO add your handling code here:
        if(!checkHoaDon()){
            return;
        }
        
        chuyenBanAn();
    }//GEN-LAST:event_btnChuyenBanActionPerformed

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
//        bh.init();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                new BanHangJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCapNhat;
    private javax.swing.JButton btnCapNhatBanAn;
    private javax.swing.JButton btnChuyenBan;
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
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel lblBanAn;
    private javax.swing.JLabel lblMaHoaDon;
    private javax.swing.JLabel lblNgayLap;
    private javax.swing.JLabel lblTenKhachHang;
    private javax.swing.JLabel lblTongTien;
    private javax.swing.JPanel pnlKhachHang;
    private javax.swing.JTabbedPane tabMain;
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
