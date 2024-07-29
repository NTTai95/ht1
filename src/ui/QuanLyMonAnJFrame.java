/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ui;

import dao.LoaiMonDAO;
import dao.MonAnDAO;
import entity.LoaiMon;
import entity.MonAn;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
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
    int row = 0;
    LoaiMonDAO LMdao = new LoaiMonDAO();
    
   
 
    

    /**
     * Creates new form QuanLyMon_LoaiMon
     */
    public QuanLyMonAnJFrame() {
        
        initComponents();
        fillTable();
        fillComboBoxLoaiMon();
        fillComboBoxLoaiMonTT();
        
        txtMaMon.setText(generateNewMaMon());
    }
    
    
    public void fillTable(){
        DefaultTableModel model = (DefaultTableModel) tblMonAn.getModel();
        model.setRowCount(0);
        try{
            List<MonAn> list = MAdao.selectAll();
            for(MonAn cd : list){
                Object[] row ={
                  cd.getMaMon(), cd.getTenMon(), cd.getDonGia()
                };
                model.addRow(row);
            }
        }catch(Exception e){
            MsgBox.alert(this, "Lỗi truy vẫn dữ liệu");
        }
    }
    void fillComboBoxLoaiMon() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cboLoaiMon.getModel();
        model.removeAllElements();
//        try {
        List<LoaiMon> list = LMdao.selectAll();
        for (LoaiMon cd : list) {
            model.addElement(cd);
        }   
    }
    
    void fillComboBoxLoaiMonTT() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cboLoaiMonTT.getModel();
        model.removeAllElements();
//        try {
        List<LoaiMon> list = LMdao.selectAll();
        for (LoaiMon cd : list) {
            model.addElement(cd);
        }   
    }
    
    public void fillTableByLoai() {
        DefaultTableModel model = (DefaultTableModel) tblMonAn.getModel();
        model.setRowCount(0);
        LoaiMon kh = (LoaiMon) cboLoaiMon.getSelectedItem();
        if (kh != null) {
            System.out.println("filltableMonAn MaLoai: " + kh.getMaLoai());
            List<MonAn> list = MAdao.selectByLoaiMon(kh.getMaLoai());
            System.out.println("list: " + list.size());
            for (int i = 0; i < list.size(); i++) {
                MonAn hv = list.get(i);
                
                Object[] row = {
                    i + 1,  hv.getMaMon(), hv.getTenMon(), hv.getDonGia()
                };
                model.addRow(row);
            }
        }
        fillTable();
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

    
    void setForm(MonAn model){
        txtMaLoai.setText(model.getMaLoai());
        txtMaMon.setText(model.getMaMon());
        txtTenMonAn.setText(model.getTenMon());
        txtDonGia.setText(String.valueOf(model.getDonGia()));   
        if(!model.getAnh().equals("")){
            lblAnh.setIcon(XImage.read(model.getAnh()));
            lblAnh.setToolTipText(model.getAnh());
        }
    }
    
    void setForm1(MonAn model){
        txtMaLoai.setText(model.getMaLoai());
        txtMaMon.setText(generateNewMaMon());
        txtTenMonAn.setText(model.getTenMon());
        txtDonGia.setText(String.valueOf(model.getDonGia()));   
//        if(!model.getAnh().equals("")){
//            lblAnh.setIcon(XImage.read(model.getAnh()));
//            lblAnh.setToolTipText(model.getAnh());
//        }
        
    }
    
    MonAn getForm(){
        MonAn ma = new MonAn();
        
        ma.setMaLoai(txtMaLoai.getText());
        ma.setMaMon(txtMaMon.getText());
        ma.setTenMon(txtTenMonAn.getText());
        ma.setDonGia(Float.parseFloat(txtDonGia.getText()));
        ma.setAnh(lblAnh.getToolTipText());
        return ma;
    }
    
    void chonAnh() throws IOException{
        if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            XImage.save(file);
            ImageIcon icon = XImage.read(file.getName());
            lblAnh.setIcon(icon);
            lblAnh.setToolTipText(file.getName());
        }
    }
    
    void edit(){
        try {
            String maMon = (String)tblMonAn.getValueAt(this.row, 0);
            MonAn ma = MAdao.selectById(maMon);
            if(ma != null){
                setForm(ma);               
            }
        } catch (Exception e) {
            MsgBox.alert(this, "Lỗi truy vấn dữ liệu");
        }
    }
    
    
    void clearForm(){
        this.setForm1(new MonAn());       
        row = -1;
        
    }
    
    void insert(){
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
    
    void update(){
        MonAn cd = getForm();
            try {
                MAdao.update(cd);
                this.fillTable();                
                MsgBox.alert(this, "Cập nhật thành công");
            } catch (Exception e) {
                MsgBox.alert(this, "Cập nhật thất bại");
            }                    
    }
    
    void delete(){
//        if(!Auth.isManager()){
//            MsgBox.alert(this, "Bạn không có quyền xoá nhân viên");
//        }else{
            if(MsgBox.confirm(this, "Bạn thực sự muốn xoá món ăn này?")){
                String maMon = txtMaMon.getText();
                try {
                    MAdao.delete(maMon);
                    this.fillTable();
                    this.clearForm();
                    MsgBox.alert(this, "Xoá thành công");
                } catch (Exception e) {
                    MsgBox.alert(this, "Xoá thất bại");
                }
            }                
        }
//    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)
                        .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addGap(38, 38, 38)
                                    .addComponent(txtDonGia, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addGap(18, 18, 18)
                                    .addComponent(txtTenMonAn, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cboLoaiMonTT, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(23, 23, 23))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(txtMaLoai, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtMaMon, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(136, 136, 136))))))
                .addGap(51, 51, 51))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(cboLoaiMonTT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMaLoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMaMon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtTenMonAn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDonGia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThem)
                    .addComponent(btnSua)
                    .addComponent(btnXoa)
                    .addComponent(btnLamMoi))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Danh sách", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        jScrollPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jScrollPane1MousePressed(evt);
            }
        });

        tblMonAn.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Mã món ăn", "Tên món ăn", "Đơn giá"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblMonAn.setShowGrid(true);
        tblMonAn.addMouseListener(new java.awt.event.MouseAdapter() {
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 832, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
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

        cboDonGia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0 - 50000", "51000 - 100000", "100000 - 200000", "201000 - 300000", "301000 - 400000", "401000 - 500000", " ", " " }));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboDonGia, 0, 217, Short.MAX_VALUE)
                    .addComponent(cboLoaiMon, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTimKiem)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboLoaiMon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cboDonGia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(22, 22, 22))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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
        if(evt.getClickCount() == 2){
            try {
                chonAnh();
            } catch (IOException ex) {
                Logger.getLogger(QuanLyMonAnJFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_lblAnhMousePressed

    private void jScrollPane1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane1MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jScrollPane1MousePressed

    private void tblMonAnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMonAnMousePressed
        if(evt.getClickCount() == 2){
            this.row = tblMonAn.rowAtPoint(evt.getPoint());
            edit();
        }
    }//GEN-LAST:event_tblMonAnMousePressed

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        insert();
    }//GEN-LAST:event_btnThemActionPerformed

    private void cboLoaiMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLoaiMonActionPerformed
        LoaiMon kh = (LoaiMon) cboLoaiMon.getSelectedItem();
        if (kh != null) {
            fillTableByLoai();
        }
    }//GEN-LAST:event_cboLoaiMonActionPerformed

    private void txtMaMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaMonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaMonActionPerformed

    private void cboLoaiMonTTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLoaiMonTTActionPerformed
        updateMaLoai();
    }//GEN-LAST:event_cboLoaiMonTTActionPerformed

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        clearForm();
        
    }//GEN-LAST:event_btnLamMoiActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
        update();
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        delete();
    }//GEN-LAST:event_btnXoaActionPerformed

    private void txtTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTimKiemActionPerformed
//        fillTableByFindName();
    }//GEN-LAST:event_txtTimKiemActionPerformed

    private void txtTimKiemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTimKiemKeyPressed
        fillTableByFindName();
    }//GEN-LAST:event_txtTimKiemKeyPressed

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
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JComboBox<String> cboDonGia;
    private javax.swing.JComboBox<String> cboLoaiMon;
    private javax.swing.JComboBox<String> cboLoaiMonTT;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAnh;
    private javax.swing.JTable tblMonAn;
    private javax.swing.JTextField txtDonGia;
    private javax.swing.JTextField txtMaLoai;
    private javax.swing.JTextField txtMaMon;
    private javax.swing.JTextField txtTenMonAn;
    private javax.swing.JTextField txtTimKiem;
    // End of variables declaration//GEN-END:variables
}
