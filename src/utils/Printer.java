/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import dao.HoaDonChiTietDAO;
import dao.HoaDonDAO;
import dao.KhachHangDAO;
import dao.MonAnDAO;
import dao.NhanVienDAO;
import entity.HoaDon;
import entity.HoaDonChiTiet;
import entity.MonAn;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.util.Elements;

/**
 *
 * @author admin
 */
public class Printer {

    public static HoaDonDAO hdDAO = new HoaDonDAO();
    public static NhanVienDAO nvDAO = new NhanVienDAO();
    public static KhachHangDAO khDAO = new KhachHangDAO();
    public static MonAnDAO maDAO = new MonAnDAO();
    public static HoaDonChiTietDAO hdctDAO = new HoaDonChiTietDAO();
    public static String pathFont = "c:/windows/fonts/Arial.ttf";
    public static DecimalFormat fmTien = new DecimalFormat("#,#00");
    public static DateTimeFormatter fmThoiGian = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static void printHoaDon(String maHD, float tienKhachDua) {
        try {

            HoaDon hd = hdDAO.selectById(Integer.valueOf(maHD));
            BaseFont bf = BaseFont.createFont(pathFont, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font fontTenNhaHang = new Font(bf, 20, Font.BOLD);
            Font fontDiaChi = new Font(bf, 12);
            Font fontTieuDe = new Font(bf, 16, Font.BOLD);
            Font fontSoHD = new Font(bf, 12, Font.BOLD);

            Font fontKhachHang = new Font(bf, 12);
            Font fontHeader = new Font(bf, 12, Font.BOLD);
            Font fontData = new Font(bf, 12);
            Font fontCamOn = new Font(bf, 16, Font.BOLD);

            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream("./src/hoadon/hoa_don.pdf"));

            document.open();

            Paragraph tenNhaHang = new Paragraph("Nhà hàng L'ESCALE", fontTenNhaHang);
            tenNhaHang.setAlignment(Element.ALIGN_CENTER);
            document.add(tenNhaHang);

            Paragraph diaChi = new Paragraph("Tầng 4, 01 Ngô Quyền Tan An Ward,\n Ninh Kieu 92000 Việt Nam", fontDiaChi);
            diaChi.setAlignment(Element.ALIGN_CENTER);
            document.add(diaChi);

            document.add(new Paragraph(" "));

            Paragraph tieuDe = new Paragraph("HÓA ĐƠN THANH TOÁN", fontTieuDe);
            tieuDe.setAlignment(Element.ALIGN_CENTER);
            document.add(tieuDe);

            Paragraph soHD = new Paragraph("Số HD: " + hd.getMaHD(), fontSoHD);
            soHD.setAlignment(Element.ALIGN_CENTER);
            document.add(soHD);

            document.add(new Paragraph(" "));

            //-----Thông tin hóa đơn
            Paragraph tenKhachHang = getChiMuc("Khách Hàng: ", khDAO.selectById(hd.getMaKH()).getTenKH());
            tenKhachHang.setAlignment(Element.ALIGN_LEFT);
            document.add(tenKhachHang);

            Paragraph ngayLap = getChiMuc("Ngày lập: ", hd.getNgayLap().format(fmThoiGian));
            ngayLap.setAlignment(Element.ALIGN_LEFT);
            document.add(ngayLap);

            Paragraph ban = getChiMuc("Bàn: ", hd.getMaB());
            ban.setAlignment(Element.ALIGN_LEFT);
            document.add(ban);

            Paragraph nhanVien = getChiMuc("Nhân Viên: ", nvDAO.selectById(hd.getMaNV()).getTenNV());
            nhanVien.setAlignment(Element.ALIGN_LEFT);
            document.add(nhanVien);

            //------Craete Table
            //Thêm bảng chi tiết sản phẩm
            PdfPTable table = new PdfPTable(5); // 4 cột: STT, Tên sản phẩm, Số lượng,, Thành tiền
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.setWidths(new int[]{1, 4, 1, 2, 2});

            //Thêm header cho bảng
            addCell(table, "STT", fontHeader);
            addCell(table, "Tên sản phẩm", fontHeader);
            addCell(table, "Số lượng", fontHeader);
            addCell(table, "Đơn giá", fontHeader);
            addCell(table, "Thành tiền", fontHeader);

            List<HoaDonChiTiet> hdct = hdctDAO.selectHDCT(String.valueOf(hd.getMaHD()));
            float tongTien = 0;
            for (int i = 0; i < hdct.size(); i++) {
                addCell(table, "" + (i + 1), fontData);
                addCell(table, maDAO.selectById(hdct.get(i).getMaMon()).getTenMon(), fontData);
                addCell(table, "" + hdct.get(i).getSoLuong(), fontData);
                addCell(table, fmTien.format(hdct.get(i).getDonGia()), fontData);
                addCell(table, fmTien.format(hdct.get(i).getSoLuong() * hdct.get(i).getDonGia()), fontData);
                tongTien += (hdct.get(i).getSoLuong() * hdct.get(i).getDonGia());
            }

            document.add(table);

            document.add(new Paragraph(" "));
            
            PdfPTable table2 = new PdfPTable(2);
            table2.setWidthPercentage(100);

            getChiMuc2(table2, "Thành tiền: ", fmTien.format(tongTien));

            getChiMuc2(table2, "Tiền khách đưa: ", fmTien.format(tienKhachDua));

            document.add(table2);
            table2.deleteBodyRows();
            
            document.add(new Paragraph(" "));

            LineSeparator line = new LineSeparator();
            document.add(line);

            document.add(new Paragraph(" "));
            
            getChiMuc2(table2, "Tiền trả lại: ", fmTien.format(tienKhachDua - tongTien));
            
            document.add(table2);
            
            document.add(new Paragraph(" "));
            
            Paragraph camOn = new Paragraph("Trân trọng cảm ơn!", fontCamOn);
            camOn.setAlignment(Element.ALIGN_CENTER);
            document.add(camOn);
            
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void getChiMuc2(PdfPTable table, String textBlod, String textItalic) {
        try {
            BaseFont bf = BaseFont.createFont(pathFont, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font fontItalic = new Font(bf, 12, Font.ITALIC);
            Font fontBold = new Font(bf, 12, Font.BOLD);
            
            PdfPCell cellLabel = new PdfPCell(new Phrase(textBlod, fontBold));
            cellLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellLabel.setBorder(0);
            
            PdfPCell cellValue = new PdfPCell(new Phrase(textItalic, fontItalic));
            cellValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellValue.setBorder(0);
            
            table.addCell(cellLabel);
            table.addCell(cellValue);
        } catch (DocumentException ex) {
            Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Paragraph getChiMuc(String textBlod, String textItalic) {
        try {
            BaseFont bf = BaseFont.createFont(pathFont, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font fontItalic = new Font(bf, 12, Font.ITALIC);
            Font fontBold = new Font(bf, 12, Font.BOLD);
            Phrase ph = new Phrase();

            ph.add(new Chunk(textBlod, fontBold));
            ph.add(new Chunk(textItalic, fontItalic));

            return new Paragraph(ph);
        } catch (DocumentException ex) {
            Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void main(String[] args) throws IOException {

        printHoaDon("12", 1700000);
    }
    //        try {

    //            BaseFont bf = BaseFont.createFont("c:/windows/fonts/Arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
    //            Font fontTieuDe = new Font(bf, 18, Font.BOLD);
    //            Font fontCuaHang = new Font(bf, 12);
    //            Font fontKhachHang = new Font(bf, 12);
    //            Font fontHeader = new Font(bf, 12, Font.BOLD);
    //            Font fontData = new Font(bf, 12);
    //
    //            // Tạo đối tượng Document
    //            Document document = new Document();
    //
    //            // Lấy đối tượng PdfWriter để ghi vào file PDF
    //            PdfWriter.getInstance(document, new FileOutputStream("./src/hoadon/hoa_don.pdf"));
    //
    //            // Mở document để bắt đầu ghi
    //            document.open();
    //
    //            // =========== THÊM NỘI DUNG HÓA ĐƠN ============= //
    //            // Thêm tiêu đề
    //            Paragraph tieuDe = new Paragraph("HÓA ĐƠN BÁN HÀNG", fontTieuDe);
    //            tieuDe.setAlignment(Element.ALIGN_CENTER);
    //            document.add(tieuDe);
    //
    //            // Thêm thông tin cửa hàng
    //            Paragraph cuaHang = new Paragraph("CỬA HÀNG ABC\nĐịa chỉ: 123 Đường XYZ, Quận 1, TP.HCM", fontCuaHang);
    //            cuaHang.setAlignment(Element.ALIGN_CENTER);
    //            document.add(cuaHang);
    //
    //            // Thêm khoảng trống
    //            document.add(new Paragraph(" "));
    //
    //            // Thêm thông tin khách hàng
    //            Paragraph thongTinKH = new Paragraph("Khách hàng: Nguyễn Văn A\nSố điện thoại: 0123456789", fontKhachHang);
    //            document.add(thongTinKH);
    //
    //            // Thêm bảng chi tiết sản phẩm
    //            PdfPTable table = new PdfPTable(4); // 4 cột: STT, Tên sản phẩm, Số lượng, Thành tiền
    //            table.setWidthPercentage(100);
    //            table.setSpacingBefore(10f);
    //            table.setSpacingAfter(10f);
    //
    //            // Thêm header cho bảng
    //            addCell(table, "STT", fontHeader);
    //            addCell(table, "Tên sản phẩm", fontHeader);
    //            addCell(table, "Số lượng", fontHeader);
    //            addCell(table, "Thành tiền", fontHeader);
    //
    //            // Thêm dữ liệu sản phẩm (ví dụ)
    //            addCell(table, "1", fontData);
    //            addCell(table, "Áo thun", fontData);
    //            addCell(table, "2", fontData);
    //            addCell(table, "200.000", fontData);
    //
    //            addCell(table, "2", fontData);
    //            addCell(table, "Quần jeans", fontData);
    //            addCell(table, "1", fontData);
    //            addCell(table, "350.000", fontData);
    //
    //            // Thêm bảng vào document
    //            document.add(table);
    //
    //            // Thêm tổng tiền
    //            Paragraph tongTien = new Paragraph("Tổng cộng: 550.000 VND", fontKhachHang);
    //            tongTien.setAlignment(Element.ALIGN_RIGHT);
    //            document.add(tongTien);
    //
    //            // =========== KẾT THÚC THÊM NỘI DUNG ============= //
    //            // Đóng document
    //            document.close();
    //
    //            System.out.println("Đã tạo file PDF thành công!");
    //        } catch (DocumentException | FileNotFoundException e) {
    //            e.printStackTrace();
    //        }
    //    }
    //
    //    // Hàm thêm cell vào bảng
    private static void addCell(PdfPTable table, String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPaddingLeft(5f);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

}
