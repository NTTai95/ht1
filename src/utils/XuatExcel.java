package utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import java.awt.Desktop;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableModel;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.Label;

public class XuatExcel {

    public static void printReport(TableModel model, String nameSheet) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu Excel");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xls", "xlsx"));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                File fileToSave = fileChooser.getSelectedFile();
                // Thêm phần mở rộng .xls nếu chưa có
                String filePath = fileToSave.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".xls")) {
                    fileToSave = new File(filePath + ".xls");
                }
                
                WritableWorkbook workbook = Workbook.createWorkbook(fileToSave);
                WritableSheet sheet = workbook.createSheet(nameSheet, 0);
                
                // Tạo tiêu đề cột
                for (int i = 0; i < model.getColumnCount(); i++) {
                    Label column = new Label(i, 0, model.getColumnName(i));
                    sheet.addCell(column);
                }
                
                // Thêm dữ liệu từ JTable vào file Excel
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        try {
                            Label cell = new Label(j, i + 1, getValueTable(model.getValueAt(i, j).toString()));
                            sheet.addCell(cell);
                        } catch (Exception e) {
                            System.out.println("row: " + i);
                            System.out.println("column: " + j + "\n");
                            
                        }
                    }
                }
                
                workbook.write();
                workbook.close();
                
                // Thông báo và mở file nếu người dùng đồng ý
                if (MsgBox.confirm(null, "Đã xuất file, Bạn có muốn mở không!")) {
                    Desktop.getDesktop().browse(fileToSave.toURI());
                }
            } catch (WriteException ex) {
                Logger.getLogger(XuatExcel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static String getValueTable(String str) {
        if (!str.startsWith("<html>")) {
            return str;
        }
        String strNew = str.substring(6, str.length() - 7);
        int start = strNew.indexOf(">");
        int end = strNew.lastIndexOf("<");

        return strNew.substring(start + 1, end);
    }

    // Hàm main để test thử
    public static void main(String[] args) {
        try {
            // Dữ liệu mẫu để thử nghiệm
            String[] columnNames = {"Name", "Age", "City"};
            Object[][] data = {
                {"John", 25, "New York"},
                {"Anna", 30, "London"},
                {"Mike", 22, "San Francisco"}
            };
            
            // Tạo JTable với dữ liệu mẫu
            JTable table = new JTable(new DefaultTableModel(data, columnNames));
            // Tạo đối tượng ExcelExporter và gọi phương thức printReport
            XuatExcel.printReport(table.getModel(), "Test");
            
            // Thông báo hoàn thành
            System.out.println("Export complete.");
        } catch (IOException ex) {
            Logger.getLogger(XuatExcel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
