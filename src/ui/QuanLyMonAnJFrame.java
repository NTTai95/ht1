/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ui;

import dao.LoaiMonDAO;
import dao.MonAnDAO;
import entity.LoaiMon;
import entity.MonAn;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.table.DefaultTableModel;
import utils.MsgBox;
import utils.XImage;

/**
 *
 * @author admin
 */
public class QuanLyMonAnJFrame extends javax.swing.JFrame {

    JFileChooser fileChooser = new JFileChooser();
    MonAnDAO MAdao = new MonAnDAO();
    int row = -1;
    LoaiMonDAO LMdao = new LoaiMonDAO();
    
    LoaiMonDAO daolm = new LoaiMonDAO();
    int rowlm = 0;

    DefaultTableModel modelTableHA = new DefaultTableModel(new Object[]{"Mã món ăn", "Tên món ăn", "Đơn giá", "Hình ảnh"}, 0) {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 3:
                    return ImageIcon.class; // Cột Photo
                default:
                    return Object.class;
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    /**
     * Creates new form QuanLyMon_LoaiMon
     */
    
    void init(){
        setLocationRelativeTo(null);
        setTitle("Hệ thống nhà hàng L'ESCALE - Quản Lý Món Ăn");
        fillTableLM();
        updateStatusLM();
        updateStatus();
    }
    
    public QuanLyMonAnJFrame() {

        initComponents();

        fillComboBoxLoaiMon();
        fillComboBoxLoaiMonTT();
        fillTable();
        init();

        txtMaMon.setText(generateNewMaMon());
        txtMaLoai1.setEnabled(false);
        txtMaLoai1.setText(generateNewMaLoai());
    }
    
    public void fillTableLM(){
        DefaultTableModel model = (DefaultTableModel) tblLoaiMon.getModel();
        model.setRowCount(0);
        try{
            List<LoaiMon> list = daolm.selectAll();
            for(LoaiMon lm : list){
                Object[] row ={
                  lm.getMaLoai(),
                  lm.getTenLoai()
                };
                model.addRow(row);
              
            }
        }catch(Exception e){
            MsgBox.alert(this, "Lỗi truy vẫn dữ liệu");
        }
    }

    public void fillTable() {
        DefaultTableModel model = modelTableHA;
        model.setRowCount(0);
        try {
            LoaiMon lm = cboLoaiMon.getSelectedIndex() == 0 ? null : (LoaiMon) cboLoaiMon.getSelectedItem();
            String maLM = lm == null ? "All" : lm.getMaLoai();

            List<MonAn> list = MAdao.selectAll();

            System.out.println(maLM);

            for (MonAn cd : list) {
                if (cd.getMaLoai().equalsIgnoreCase(maLM) || maLM.equalsIgnoreCase("All")) {
                    Image hinhAnh = new ImageIcon("./" + cd.getAnh()).getImage().getScaledInstance(tblMonAn.getRowHeight(), tblMonAn.getRowHeight(), Image.SCALE_SMOOTH);
                    Object[] row = {
                        cd.getMaMon(), cd.getTenMon(), cd.getDonGia(), new ImageIcon(hinhAnh)
                    };
                    model.addRow(row);
                }
            }
            tblMonAn.setModel(model);
            System.out.println("ui.QuanLyMonAnJFrame.fillTable()");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void fillTableByPrice() {
        DefaultTableModel model = modelTableHA;
        model.setRowCount(0);
        try {
            MonAn lm = cboLoaiMon.getSelectedIndex() == 0 ? null : (MonAn) cboLoaiMon.getSelectedItem();
            String maLM = lm == null ? "All" : (String.valueOf(lm.getDonGia()));

            
            String priceRange = cboDonGia.getSelectedItem().toString();
            String[] prices = priceRange.split("-");
            double minPrice = Double.parseDouble(prices[0].trim());    
            double maxPrice = Double.parseDouble(prices[1].trim());

            List<MonAn> list = MAdao.selectAll();

            for (MonAn cd : list) {
                if ((cd.getMaLoai().equalsIgnoreCase(maLM) || maLM.equalsIgnoreCase("All")) &&
                    cd.getDonGia() >= minPrice && cd.getDonGia() <= maxPrice) {
                    Image hinhAnh = new ImageIcon("./" + cd.getAnh()).getImage().getScaledInstance(tblMonAn.getRowHeight(), tblMonAn.getRowHeight(), Image.SCALE_SMOOTH);
                    Object[] row = {
                        cd.getMaMon(), cd.getTenMon(), cd.getDonGia(), new ImageIcon(hinhAnh)
                    };
                    model.addRow(row);
                }
            }
            tblMonAn.setModel(model);
            System.out.println("ui.QuanLyMonAnJFrame.fillTable()");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    

    void fillComboBoxLoaiMon() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cboLoaiMon.getModel();
        model.removeAllElements();
        model.addElement("ALL");
        List<LoaiMon> list = LMdao.selectAll();
        for (LoaiMon lm : list) {
            model.addElement(lm);
        }
    }

    void fillComboBoxLoaiMonTT() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cboLoaiMonTT.getModel();
        model.removeAllElements();
        List<LoaiMon> list = LMdao.selectAll();
        for (LoaiMon lm : list) {
            model.addElement(lm);
        }
    }

    public void fillTableByFindName() {
        DefaultTableModel model = (DefaultTableModel) tblMonAn.getModel();
        model.setRowCount(0);

        String keyword = txtTimKiem.getText();
        List<MonAn> list = MAdao.selectByKeyWord(keyword);
        for (MonAn ma : list) { // Duyệt nh rồi đưa lên table như bình thường
            model.addRow(new Object[]{                
                ma.getMaMon(),              
                ma.getTenMon(),
                ma.getDonGia(),                
            });
        }
    }
    
    public void updateMaLoai() {
        LoaiMon selectedLoaiMon = (LoaiMon) cboLoaiMonTT.getSelectedItem();
        if (selectedLoaiMon != null) {
            txtMaLoai.setText(selectedLoaiMon.getMaLoai());
        }
    }

    public String generateNewMaMon() {
        int rowCount = MAdao.getCountRow(); // Lấy số lượng dòng hiện tại
        int newNumber = rowCount + 1; // Tăng lên 1
        return "M0" + newNumber; // Tạo mã món ăn mới
    }
    
    public String generateNewMaLoai() {
        int rowCount = LMdao.getCountRow(); 
        int newNumber = rowCount + 1; 
        return "ML0" + newNumber; 
    }

    void setForm(MonAn model) {
        txtMaLoai.setText(model.getMaLoai());
        txtMaMon.setText(model.getMaMon());
        txtTenMonAn.setText(model.getTenMon());
        txtDonGia.setText(String.valueOf(model.getDonGia()));
        if (!model.getAnh().equals("")) {
            lblAnh.setIcon(new ImageIcon("./" + model.getAnh()));
            lblAnh.setToolTipText(model.getAnh());
        }
    }

    void setForm1(MonAn model) {
        txtMaLoai.setText(model.getMaLoai());
        txtMaMon.setText(generateNewMaMon());
        txtTenMonAn.setText(model.getTenMon());
        txtDonGia.setText(String.valueOf(model.getDonGia()));
//        if(!model.getAnh().equals("")){
//            lblAnh.setIcon(XImage.read(model.getAnh()));
//            lblAnh.setToolTipText(model.getAnh());
//        }

    }

    MonAn getForm() {
        MonAn ma = new MonAn();

        ma.setMaLoai(txtMaLoai.getText());
        ma.setMaMon(txtMaMon.getText());
        ma.setTenMon(txtTenMonAn.getText());
        ma.setDonGia(Float.parseFloat(txtDonGia.getText()));
        ma.setAnh(lblAnh.getToolTipText());
        return ma;
    }

    ImageIcon chonAnh() throws IOException {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Hình ảnh", "jpg", "jpeg", "png", "gif"));
            File file = fileChooser.getSelectedFile();
            XImage.save(file);
            ImageIcon icon = XImage.read(file.getName());
            return icon;
        }
        return null;
    }

    void clearForm() {
        this.setForm1(new MonAn());
        row = -1;
        updateStatus();
    }

    void insert() {
        MonAn cd = getForm();
        try {
            MAdao.insert(cd);
            this.fillTable();
            this.clearForm();
            MsgBox.alert(this, "Thêm mới thành công");
        } catch (Exception e) {
            MsgBox.alert(this, "Thêm mới thất bại");
        }
    }

    void update() {
        MonAn cd = getForm();
        try {
            MAdao.update(cd);
            this.fillTable();
            MsgBox.alert(this, "Cập nhật thành công");
        } catch (Exception e) {
            MsgBox.alert(this, "Cập nhật thất bại");
        }
    }

    void delete() {
//        if(!Auth.isManager()){
//            MsgBox.alert(this, "Bạn không có quyền xoá nhân viên");
//        }else{
        if (MsgBox.confirm(this, "Bạn thực sự muốn xoá món ăn này?")) {
            String maMon = tblMonAn.getValueAt(row, 0).toString();
            System.out.println(tblMonAn.getValueAt(row, 0).toString());
            try {
                MAdao.delete(maMon);
                this.fillTable();
                this.clearForm();
                MsgBox.alert(this, "Xoá thành công");
            } catch (Exception e) {
                MsgBox.alert(this, "Không thể xóa món ăn");
            }
        }
    }
    
    void edit() {
        try {
            String maMon = (String) tblMonAn.getValueAt(this.row, 0);
            MonAn ma = MAdao.selectById(maMon);
            if (ma != null) {
                this.setForm(ma);
                
            }
        } catch (Exception e) {
//            MsgBox.alert(this, "Lỗi truy vấn dữ liệu");
        }
        
    }
    
    void updateStatus(){
        boolean edit = this.row >= 0 ;
        btnThem.setEnabled(!edit);
        btnSua.setEnabled(edit);
        btnXoa.setEnabled(edit);        
    }
    
    public boolean checkMonAn() {
        if (txtTenMonAn.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "Tên món ăn không được bỏ trống!");
            return false;
        } else if (txtDonGia.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "Đơn giá không được bỏ trống!");
            return false;
        } else if (!txtDonGia.getText().trim().matches("-?\\d+(\\.\\d+)?")) {
            JOptionPane.showMessageDialog(this, "Đơn giá không được nhập chữ!");
            return false;
        } else {
        try {
            double donGia = Double.parseDouble(txtDonGia.getText().trim());            
            if (donGia <= 0) {
                JOptionPane.showMessageDialog(this, "Đơn giá phải lớn hơn 0!");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Đơn giá không hợp lệ!");
            return false;
        }
    }
        return true;
    }
    
    void updateStatusLM(){
        boolean edit = this.row >= 0 ;
        txtMaLoai1.setEnabled(!edit);
        btnThem1.setEnabled(!edit);
        btnSua1.setEnabled(edit);
        btnXoa1.setEnabled(edit);
        
    }
    
    void editLM(){
        try {
            String maloai = (String) tblLoaiMon.getValueAt(this.row, 0);
            LoaiMon lm = daolm.selectById(maloai);
            this.setFormLM(lm);
            this.updateStatusLM();
        } catch (Exception e) {
        }
        
    }
    
    void clearFormLM(){
        LoaiMon lm = new LoaiMon();
        this.setFormLM(lm);
        row = -1;
        updateStatusLM();
        txtMaLoai1.setEnabled(false);
        txtMaLoai1.setText(generateNewMaLoai());
    }
    
    void setFormLM(LoaiMon model){
        txtMaLoai1.setText(model.getMaLoai());
        txtTenLoai.setText(model.getTenLoai());
        
    }
    
    LoaiMon getFormLM(){
        LoaiMon model = new LoaiMon();
        model.setMaLoai(txtMaLoai1.getText());
        model.setTenLoai(txtTenLoai.getText());
        
        return model;
    }

    void insertLM(){
        LoaiMon lm = getFormLM();
        try {
            daolm.insert(lm);
            this.fillTableLM();
            this.clearFormLM();
            MsgBox.alert(this, "Thêm mới thành công!");
        } catch (Exception e) {
            MsgBox.alert(this, "Thêm mới thất bại!");
        }
    }
    
    void updateLM(){
        LoaiMon lm = getFormLM();
        try {
            daolm.update(lm);
            this.fillTableLM();
            MsgBox.alert(this, "Cập nhật thành công!");
        } catch (Exception e) {
            MsgBox.alert(this, "Cập nhật thất bại!");
        }
    }
    
    void deleteLM(){
        LoaiMon lm = getFormLM();
        try {
            MsgBox.confirm(this, "Bạn có chắc chắn muốn xóa!");
                String maloai = txtMaLoai1.getText();
                try {
                    daolm.delete(maloai);
                    this.fillTableLM();
                    this.clearFormLM();
                    MsgBox.alert(this, "Xóa thành công loại món!");
                } catch (Exception e) {
                    MsgBox.alert(this, "Xóa thất bại!");
                }
            
        } catch (Exception e) {
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtTenMonAn = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDonGia = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        lblAnh = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtMaLoai = new javax.swing.JTextField();
        btnThem = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnLamMoi = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        cboLoaiMonTT = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        txtMaMon = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMonAn = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        cboLoaiMon = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        txtTimKiem = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cboDonGia = new javax.swing.JComboBox<>();
        jPanel6 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        txtTenLoai = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtMaLoai1 = new javax.swing.JTextField();
        btnThem1 = new javax.swing.JButton();
        btnSua1 = new javax.swing.JButton();
        btnXoa1 = new javax.swing.JButton();
        btnLamMoi1 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblLoaiMon = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Thông tin", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        jLabel1.setText("Tên món ăn:");

        txtTenMonAn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTenMonAnActionPerformed(evt);
            }
        });

        jLabel2.setText("Đơn giá:");

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblAnh.setBackground(new java.awt.Color(204, 204, 204));
        lblAnh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblAnhMousePressed(evt);
            }
        });
        jPanel1.add(lblAnh, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 130, 150));

        jLabel3.setText("Mã món ăn:");

        txtMaLoai.setEnabled(false);
        txtMaLoai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaLoaiActionPerformed(evt);
            }
        });

        btnThem.setText("Thêm");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        btnSua.setText("Cập nhật");
        btnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaActionPerformed(evt);
            }
        });

        btnXoa.setText("Xóa");
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        btnLamMoi.setText("Làm mới");
        btnLamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoiActionPerformed(evt);
            }
        });

        jLabel7.setText("Loại món ăn");

        cboLoaiMonTT.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboLoaiMonTT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLoaiMonTTActionPerformed(evt);
            }
        });

        jLabel8.setText("Mã loại");

        txtMaMon.setEnabled(false);
        txtMaMon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaMonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)
                        .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtMaMon, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtMaLoai, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                            .addComponent(jLabel2)
                                            .addGap(38, 38, 38)
                                            .addComponent(txtDonGia, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                            .addComponent(jLabel1)
                                            .addGap(18, 18, 18)
                                            .addComponent(txtTenMonAn, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cboLoaiMonTT, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboLoaiMonTT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(txtMaLoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMaMon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtTenMonAn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDonGia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThem)
                    .addComponent(btnSua)
                    .addComponent(btnXoa)
                    .addComponent(btnLamMoi))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Danh sách", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        jScrollPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jScrollPane1MousePressed(evt);
            }
        });

        tblMonAn.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Mã món ăn", "Tên món ăn", "Đơn giá", "Hình ảnh"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblMonAn.setRowHeight(40);
        tblMonAn.setShowGrid(true);
        tblMonAn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMonAnMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblMonAnMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(tblMonAn);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 733, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 7, Short.MAX_VALUE))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Tìm kiếm", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        jLabel4.setText("Loại món ăn");

        cboLoaiMon.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboLoaiMon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLoaiMonActionPerformed(evt);
            }
        });

        jLabel5.setText("Tên món ăn");

        txtTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTimKiemActionPerformed(evt);
            }
        });
        txtTimKiem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTimKiemKeyPressed(evt);
            }
        });

        jLabel6.setText("Đơn giá");

        cboDonGia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ALL", "0 - 49000", "50000 - 99000", "100000 - 149000", "150000 - 199000", "200000 - 249000", "250000 - 299000", "300000 - 400000", " ", " " }));
        cboDonGia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboDonGiaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboLoaiMon, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboDonGia, 0, 169, Short.MAX_VALUE)
                    .addComponent(txtTimKiem)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboLoaiMon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cboDonGia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(199, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(176, 176, 176))
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(173, 173, 173)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(296, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(69, 69, 69)))
        );

        jTabbedPane1.addTab("Món ăn", jPanel5);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Thông tin", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        jLabel9.setText("Tên loại:");

        txtTenLoai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTenLoaiActionPerformed(evt);
            }
        });

        jLabel10.setText("Mã loại:");

        txtMaLoai1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaLoai1ActionPerformed(evt);
            }
        });

        btnThem1.setText("Thêm");
        btnThem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThem1ActionPerformed(evt);
            }
        });

        btnSua1.setText("Cập nhật");
        btnSua1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSua1ActionPerformed(evt);
            }
        });

        btnXoa1.setText("Xóa");
        btnXoa1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoa1ActionPerformed(evt);
            }
        });

        btnLamMoi1.setText("Làm mới");
        btnLamMoi1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoi1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTenLoai, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMaLoai1, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 16, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                        .addComponent(btnThem1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSua1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnXoa1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLamMoi1)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtMaLoai1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtTenLoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSua1)
                    .addComponent(btnThem1)
                    .addComponent(btnXoa1)
                    .addComponent(btnLamMoi1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Danh sách", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        tblLoaiMon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Mã loại", "Tên Loại"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblLoaiMon.setShowGrid(true);
        tblLoaiMon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblLoaiMonMousePressed(evt);
            }
        });
        jScrollPane2.setViewportView(tblLoaiMon);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 733, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(101, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(488, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(139, Short.MAX_VALUE)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(321, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Loại món", jPanel6);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 768, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 494, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtTenMonAnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTenMonAnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTenMonAnActionPerformed

    private void txtMaLoaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaLoaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaLoaiActionPerformed

    private void lblAnhMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAnhMousePressed
        if (evt.getClickCount() == 2) {
            try {
                ImageIcon anh = chonAnh();
                lblAnh.setIcon(anh != null ? anh : lblAnh.getIcon());
                lblAnh.setToolTipText("src/img/" + anh.getDescription().substring(anh.getDescription().lastIndexOf("\\") + 1));
            } catch (IOException ex) {
                Logger.getLogger(QuanLyMonAnJFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_lblAnhMousePressed

    private void jScrollPane1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane1MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jScrollPane1MousePressed

    private void tblMonAnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMonAnMousePressed

    }//GEN-LAST:event_tblMonAnMousePressed

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        if(checkMonAn() == true){
            insert();
        }
        
    }//GEN-LAST:event_btnThemActionPerformed

    private void cboLoaiMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLoaiMonActionPerformed

        fillTable();

    }//GEN-LAST:event_cboLoaiMonActionPerformed

    private void txtMaMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaMonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaMonActionPerformed

    private void cboLoaiMonTTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLoaiMonTTActionPerformed
        updateMaLoai();
    }//GEN-LAST:event_cboLoaiMonTTActionPerformed

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        clearForm();
        fillComboBoxLoaiMonTT();

    }//GEN-LAST:event_btnLamMoiActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
        if(checkMonAn() == true){
            update();
        }
        
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        delete();
    }//GEN-LAST:event_btnXoaActionPerformed

    private void txtTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTimKiemActionPerformed
//        fillTableByFindName();
    }//GEN-LAST:event_txtTimKiemActionPerformed

    private void tblMonAnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMonAnMouseClicked
        // TODO add your handling code here:
        this.row = tblMonAn.getSelectedRow();
        if (row < 0) {
            return;
        }
        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
            this.row = tblMonAn.getSelectedRow();
            this.edit();
            btnThem.setEnabled(false);
            btnSua.setEnabled(true);
            btnXoa.setEnabled(true);
        }
    }//GEN-LAST:event_tblMonAnMouseClicked

    private void cboDonGiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboDonGiaActionPerformed
        fillTableByPrice();
    }//GEN-LAST:event_cboDonGiaActionPerformed

    private void txtTimKiemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTimKiemKeyPressed
        fillTableByFindName();
    }//GEN-LAST:event_txtTimKiemKeyPressed

    private void txtTenLoaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTenLoaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTenLoaiActionPerformed

    private void txtMaLoai1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaLoai1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaLoai1ActionPerformed

    private void btnThem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThem1ActionPerformed
        insertLM();
    }//GEN-LAST:event_btnThem1ActionPerformed

    private void btnSua1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSua1ActionPerformed
        updateLM();
    }//GEN-LAST:event_btnSua1ActionPerformed

    private void btnXoa1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoa1ActionPerformed
        deleteLM();
    }//GEN-LAST:event_btnXoa1ActionPerformed

    private void btnLamMoi1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoi1ActionPerformed
        clearFormLM();
    }//GEN-LAST:event_btnLamMoi1ActionPerformed

    private void tblLoaiMonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLoaiMonMousePressed
        if (evt.getClickCount() == 2){
            this.row = tblLoaiMon.getSelectedRow();
            this.editLM();
        }
    }//GEN-LAST:event_tblLoaiMonMousePressed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        new TrangChuJFrame().setVisible(true);
        dispose();
    }//GEN-LAST:event_formWindowClosing


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
            java.util.logging.Logger.getLogger(QuanLyMonAnJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(QuanLyMonAnJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(QuanLyMonAnJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(QuanLyMonAnJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new QuanLyMonAnJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnLamMoi1;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnSua1;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnThem1;
    private javax.swing.JButton btnXoa;
    private javax.swing.JButton btnXoa1;
    private javax.swing.JComboBox<String> cboDonGia;
    private javax.swing.JComboBox<String> cboLoaiMon;
    private javax.swing.JComboBox<String> cboLoaiMonTT;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblAnh;
    private javax.swing.JTable tblLoaiMon;
    private javax.swing.JTable tblMonAn;
    private javax.swing.JTextField txtDonGia;
    private javax.swing.JTextField txtMaLoai;
    private javax.swing.JTextField txtMaLoai1;
    private javax.swing.JTextField txtMaMon;
    private javax.swing.JTextField txtTenLoai;
    private javax.swing.JTextField txtTenMonAn;
    private javax.swing.JTextField txtTimKiem;
    // End of variables declaration//GEN-END:variables
}
