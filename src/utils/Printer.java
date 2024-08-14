package utils;

import dao.HoaDonChiTietDAO;
import dao.HoaDonDAO;
import dao.KhachHangDAO;
import dao.MonAnDAO;
import dao.NhanVienDAO;
import entity.HoaDon;
import entity.HoaDonChiTiet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Printer {

    private static final HoaDonDAO hdDAO = new HoaDonDAO();
    private static final KhachHangDAO khDAO = new KhachHangDAO();
    private static final NhanVienDAO nvDAO = new NhanVienDAO();
    private static final HoaDonChiTietDAO hdctDAO = new HoaDonChiTietDAO();
    private static final MonAnDAO maDAO = new MonAnDAO();

    private static final DecimalFormat fmTien = new DecimalFormat("#,#00");
    private static final DateTimeFormatter fmThoiGian = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static int heightHDCT = 0;

    public static String tenNhaHang = "NHÀ HÀNG L'ESCALE CẦN THƠ";
    public static String tieuDe = "HÓA ĐƠN THANH TOÁN";
    public static String diaChi1 = "Tầng 4, 01 Ngô Quyền Tan An Ward,";
    public static String diaChi2 = " Ninh Kieu, 92000 Việt Nam";
    public static String camOn = "Trân trọng cảm ơn!";

    public static String phieuCheBien = "PHIẾU CHẾ BIẾN";

    private static final int width = 58; // Thay đổi width thành 58mm
    private static final int sizeHeader = 180; // Giảm sizeHeader để phù hợp
    private static final int sizeFooter = 100; // Giảm sizeFooter để phù hợp
    private static final int margin_left_right = 1;
    private static final int margin_top_bottom = 5;
    private static final Font fontTieuDe = new Font("Arial", Font.BOLD, 9); 
    private static final Font fontPlain = new Font("Arial", Font.PLAIN, 7); 
    private static final Font fontDiaChi = new Font("Arial", Font.PLAIN, 6);
    private static final Font fontBold = new Font("Arial", Font.BOLD, 7); 
    private static final Font fontHeader = new Font("Arial", Font.BOLD, 8); 
    private static final Font fontBody = new Font("Arial", Font.PLAIN, 7);

    public static boolean inThongBaoBep(String maHD) {
        PrinterJob job = PrinterJob.getPrinterJob();

        PageFormat pf = job.defaultPage();

        Paper pr = pf.getPaper();
        HoaDon hd = hdDAO.selectById(Integer.valueOf(maHD));
        if (pf.getImageableWidth() <= 0 || pf.getImageableHeight() <= 0) {
            double width = mmToPonit(Printer.width); 
            double height = pr.getHeight();  
            pr.setSize(width, height);
            pr.setImageableArea(mmToPonit(margin_left_right), mmToPonit(margin_top_bottom), mmToPonit(Printer.width) - (mmToPonit(margin_left_right) * 2), sizeHeader + heightHDCT + sizeFooter);
            pf.setPaper(pr);
        } else {
            int heightHDCT = getHeightHDCT((double) pf.getImageableWidth(), new float[]{0.7f, 0.3f, 0f}, hdctDAO.selectHDCT(String.valueOf(hd.getMaHD())));

            pr.setSize(mmToPonit(width), sizeHeader + heightHDCT + sizeFooter);

            pr.setImageableArea(mmToPonit(margin_left_right), mmToPonit(margin_top_bottom), mmToPonit(width) - (mmToPonit(margin_left_right) * 2), sizeHeader + heightHDCT + sizeFooter);
        }
        System.out.println("Tổng Height: " + (sizeHeader + heightHDCT + sizeFooter));

        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }

                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(pf.getImageableX(), pf.getImageableY());

                g2d.setColor(Color.black);

                int y = 14;

                drawCenter(phieuCheBien, fontTieuDe, g2d, pf.getImageableWidth(), y);

                y += 15; // Giảm khoảng cách

                drawLeft2("Khách Hàng: ", khDAO.selectById(hd.getMaKH()).getTenKH(), fontBold, fontPlain, g2d, pf.getImageableWidth(), y);

                y += 12; // Giảm khoảng cách

                drawLeft2("Bàn ăn: ", hd.getMaB(), fontBold, fontPlain, g2d, pf.getImageableWidth(), y);

                y += 12; // Giảm khoảng cách

                drawLeft2("Ngày lập: ", hd.getNgayLap().format(fmThoiGian), fontBold, fontPlain, g2d, pf.getImageableWidth(), y);

                y += 12; // Giảm khoảng cách

                drawLeft2("Nhân viên: ", nvDAO.selectById(hd.getMaNV()).getTenNV(), fontBold, fontPlain, g2d, pf.getImageableWidth(), y);
                y += 12; // Giảm khoảng cách
                g2d.drawRect(0, y, (int) pf.getImageableWidth(), 0);

                y += 15; // Giảm khoảng cách
                String[] header = new String[]{"Tên Món", "SL"}; // Rút gọn "Số lượng" thành "SL"
                float[] widthColumn = new float[]{0.7f, 0.3f, 0f}; // Điều chỉnh tỷ lệ cột cho phù hợp
                drawTable2clm(g2d, pf.getImageableWidth(), y, widthColumn, header, hdctDAO.selectHDCT(String.valueOf(hd.getMaHD())));

                return PAGE_EXISTS;
            }

        }, pf);

        if (job.printDialog()) {
            try {
                job.print();
                return true;
            } catch (PrinterException ex) {
                return false;
            }
        }
        return false;
    }

    public static boolean inHoaDon(String maHD, float KHTra) {
        PrinterJob job = PrinterJob.getPrinterJob();

        PageFormat pf = job.defaultPage();

        Paper pr = pf.getPaper();
        HoaDon hd = hdDAO.selectById(Integer.valueOf(maHD));
        if (pf.getImageableWidth() <= 0 || pf.getImageableHeight() <= 0) {
            double width = mmToPonit(Printer.width);  
            double height = pr.getHeight();
            pr.setSize(width, height);
            pr.setImageableArea(mmToPonit(margin_left_right), mmToPonit(margin_top_bottom), mmToPonit(Printer.width) - (mmToPonit(margin_left_right) * 2), sizeHeader + heightHDCT + sizeFooter);
        } else {

            int heightHDCT = getHeightHDCT((double) pf.getImageableWidth(), new float[]{0.6f, 0.1f, 0.3f}, hdctDAO.selectHDCT(String.valueOf(hd.getMaHD())));

            pr.setSize(mmToPonit(width), sizeHeader + heightHDCT + sizeFooter);

            pr.setImageableArea(mmToPonit(margin_left_right), mmToPonit(margin_top_bottom), mmToPonit(width) - (mmToPonit(margin_left_right) * 2), sizeHeader + heightHDCT + sizeFooter);
        }
        System.out.println("Tổng Height: " + (sizeHeader + heightHDCT + sizeFooter));

        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics g, PageFormat pfo, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }

                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(pfo.getImageableX(), pfo.getImageableY());
                int y = 14;

                g2d.setColor(Color.black);
                drawCenter(tenNhaHang, fontTieuDe, g2d, pfo.getImageableWidth(), y);
                y += 12; 
                drawCenter(diaChi1, fontDiaChi, g2d, pfo.getImageableWidth(), y);
                y +=10;
                drawCenter(diaChi2, fontDiaChi, g2d, pfo.getImageableWidth(), y);
                y += 35; 
                drawCenter(tieuDe, fontTieuDe, g2d, pfo.getImageableWidth(), y);
                y += 12; 
                drawCenter("Số HD: " + hd.getMaHD(), fontPlain, g2d, pfo.getImageableWidth(), y);
                y += 18; 
                drawLeft2("Khách hàng: ", khDAO.selectById(hd.getMaKH()).getTenKH(), fontBold, fontPlain, g2d, pfo.getImageableWidth(), y);
                y += 16; 
                drawLeft2("Ngày Lập: ", hd.getNgayLap().format(fmThoiGian), fontBold, fontPlain, g2d, pfo.getImageableWidth(), y);
                y += 16; 
                drawLeft2("Bàn ăn: ", hd.getMaB(), fontBold, fontPlain, g2d, pfo.getImageableWidth(), y);
                y += 16; 
                drawLeft2("Nhân viên: ", nvDAO.selectById(hd.getMaNV()).getTenNV(), fontBold, fontPlain, g2d, pfo.getImageableWidth(), y);
                y += 18; 
                g2d.drawRect(0, y, (int) pfo.getImageableWidth(), 0);
                y += 18; 

                String[] header = new String[]{"Tên món", "SL", "T.Tiền"}; 
                float[] widthColumn = new float[]{0.6f, 0.1f, 0.3f}; 
                drawTable3clm(g2d, pfo.getImageableWidth(), y, widthColumn, header, hdctDAO.selectHDCT(String.valueOf(hd.getMaHD())));

                int thanhToan = 0;
                for (HoaDonChiTiet hdct : hdctDAO.selectHDCT(String.valueOf(hd.getMaHD()))) {
                    thanhToan += (hdct.getSoLuong() * hdct.getDonGia());
                }

                y += getHeightHDCT((double) pfo.getImageableWidth(), widthColumn, hdctDAO.selectHDCT(String.valueOf(hd.getMaHD())));

                g2d.drawRect(0, y, (int) pfo.getImageableWidth(), 0);

                y += 20; 

                drawLeft("THANH TOÁN", fontHeader, g2d, pfo.getImageableWidth(), y);
                drawRight(fmTien.format(thanhToan) + "đ", fontBody, g2d, pfo.getImageableWidth(), y);

                y += 13; 

                drawLeft("Tiền khách trả", fontPlain, g2d, pfo.getImageableWidth(), y);
                drawRight(fmTien.format(KHTra) + "đ", fontPlain, g2d, pfo.getImageableWidth(), y);

                y += 13; 

                g2d.drawRect(0, y, (int) pfo.getImageableWidth(), 0);

                y += 13; 

                drawLeft("Thối lại", fontPlain, g2d, pfo.getImageableWidth(), y);
                drawRight(fmTien.format((KHTra - thanhToan)) + "đ", fontPlain, g2d, pfo.getImageableWidth(), y);
                y += 25; 

                drawCenter("-------*--***--*-------", fontBold, g2d, pfo.getImageableWidth(), y);

                y += 18; 
                drawCenter(camOn, fontBold, g2d, pfo.getImageableWidth(), y);

                return PAGE_EXISTS;
            }

        }, pf);

        if (job.printDialog()) {
            try {
                job.print();
                return true;
            } catch (PrinterException ex) {
                return false;
            }
        }
        return false;
    }

    private static void drawTable2clm(Graphics2D g2d, double width, int y, float[] widthColumn, String[] header, List<HoaDonChiTiet> list) {
        int x = 0;
        int textWidth;
        float widthColumn1 = (float) (width * widthColumn[0]);
        float widthColumn2 = (float) (width * widthColumn[1]);
        int fontSize = fontHeader.getSize() + 6; 
        g2d.setFont(fontHeader);
        g2d.drawString(header[0], x, y);

        textWidth = g2d.getFontMetrics().stringWidth(header[1]);
        x = (int) (widthColumn1 + ((widthColumn2 - textWidth) / 2)+mmToPonit(2));

        g2d.drawString(header[1], x, y);

        x = 0;
        fontSize = fontBody.getSize() + 6;
        y += fontSize; // y = 15 + fontSize (fontBody.getSize() + 8)
        g2d.setFont(fontBody);

        for (HoaDonChiTiet hdct : list) {
            int i = -1;
            String text = maDAO.selectById(hdct.getMaMon()).getTenMon();

            textWidth = g2d.getFontMetrics().stringWidth(text);
            while (true) {
                String textTemp = text.substring(0, i == -1 ? text.length() - 1 : i);
                if (textWidth > widthColumn1) {
                    i = textTemp.lastIndexOf(" ");
                    textWidth = g2d.getFontMetrics().stringWidth(text.substring(0, i));
                } else {
                    break;
                }
            }

            x = 0;

            if (i > 0) {
                g2d.drawString(text.substring(0, i), x, y);
                y += fontSize - 3; // y = 15 + fontSize for(if()(+ fontSize))
                g2d.drawString(text.substring(i + 1, text.length()), x, y);
                y -= 6;
            } else {
                g2d.drawString(text, 0, y);
            }

            text = String.valueOf(hdct.getSoLuong());
            textWidth = g2d.getFontMetrics().stringWidth(text);

            x = (int) (widthColumn1 + ((widthColumn2 - textWidth) / 2)+mmToPonit(2));
            g2d.drawString(text, x, y);

            if (i > 0) {
                y += fontSize + 7;
            } else {
                y += fontSize;
            }
        }
    }

    private static void drawTable3clm(Graphics2D g2d, double width, int y, float[] widthColumn, String[] header, List<HoaDonChiTiet> list) {
        int x = 0;
        int textWidth;
        float widthColumn1 = (float) (width * widthColumn[0]);
        float widthColumn2 = (float) (width * widthColumn[1]);
        float widthColumn3 = (float) (width * widthColumn[2]);
        int fontSize = fontHeader.getSize() + 6; 
        g2d.setFont(fontHeader);
        g2d.drawString(header[0], x, y);

        textWidth = g2d.getFontMetrics().stringWidth(header[1]);
        x = (int) (widthColumn1 + ((widthColumn2 - textWidth) / 2)+mmToPonit(2));

        g2d.drawString(header[1], x, y);

        drawRight(header[2], fontHeader, g2d, width, y);
        x = 0;
        fontSize = fontBody.getSize() + 6; 
        y += fontSize; 
        g2d.setFont(fontBody);

        for (HoaDonChiTiet hdct : list) {
            int i = -1;
            String text = maDAO.selectById(hdct.getMaMon()).getTenMon();

            textWidth = g2d.getFontMetrics().stringWidth(text);
            while (true) {
                String textTemp = text.substring(0, i == -1 ? text.length() - 1 : i);
                if (textWidth > widthColumn1) {
                    i = textTemp.lastIndexOf(" ");
                    textWidth = g2d.getFontMetrics().stringWidth(text.substring(0, i));
                } else {
                    break;
                }
            }

            x = 0;

            if (i > 0) {
                g2d.drawString(text.substring(0, i), x, y);
                y += fontSize - 3; 
                g2d.drawString(text.substring(i + 1, text.length()), x, y);
                y -= 6; 
            } else {
                g2d.drawString(text, 0, y);
            }

            text = String.valueOf(hdct.getSoLuong());
            textWidth = g2d.getFontMetrics().stringWidth(text);

            x = (int) (widthColumn1 + ((widthColumn2 - textWidth) / 2)+mmToPonit(2));
            g2d.drawString(text, x, y);

            text = fmTien.format(hdct.getDonGia() * hdct.getSoLuong());
            drawRight(text, fontBody, g2d, width, y);
            if (i > 0) {
                y += fontSize + 7; 
            } else {
                y += fontSize; 
            }
        }
        System.out.println("Height HDCT full: " + y);
        System.out.println("Height HDCT: " + (y - sizeHeader));
    }

    private static void drawLeft2(String text1, String text2, Font font1, Font font2, Graphics2D g2d, double width, float y) {
        g2d.setFont(font1);
        g2d.drawString(text1, 0, y);

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text1);

        g2d.setFont(font2);
        g2d.drawString(text2, textWidth, y);
    }

    private static void drawRight(String text, Font font, Graphics2D g2d, double width, float y) {
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int RightX = (int) (width - textWidth);
        g2d.drawString(text, RightX, y);
    }

    private static void drawLeft(String text, Font font, Graphics2D g2d, double width, float y) {
        g2d.setFont(font);
        g2d.drawString(text, 0, y);
    }

    private static void drawCenter(String text, Font font, Graphics2D g2d, double width, float y) {
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int centerX = (int) (width - textWidth) / 2;
        g2d.drawString(text, centerX, y);
    }

    private static float mmToPonit(int mm) {
        return (float) (mm * (72 / 25.4));
    }

    private static int getHeightHDCT(double width, float[] widthColumn, List<HoaDonChiTiet> list) {
        int y = 0;
        int textWidth;
        int fontSize = fontHeader.getSize() + 6; 
        float widthColumn1 = (float) (width * widthColumn[0]);

        fontSize = fontBody.getSize() + 6; 
        y += fontSize; 
        for (HoaDonChiTiet hdct : list) {
            int i = -1;
            String text = maDAO.selectById(hdct.getMaMon()).getTenMon();

            textWidth = getStringWidth(fontBody, text);
            while (true) {
                String textTemp = text.substring(0, i == -1 ? text.length() - 1 : i);
                if (textWidth > widthColumn1) {
                    i = textTemp.lastIndexOf(" ");
                    textWidth = getStringWidth(fontBody, text.substring(0, i));
                } else {
                    break;
                }
            }

            if (i > 0) {
                y += fontSize - 3; 
                y -= 6; 
            }
            if (i > 0) {
                y += fontSize + 7;
            } else {
                y += fontSize;
            }
        }
        System.out.println("Y1: " + y);
        return y;
    }

    public static int getStringWidth(Font font, String text) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        try {
            g2d.setFont(font);

            FontMetrics fm = g2d.getFontMetrics();

            return fm.stringWidth(text);
        } finally {
            g2d.dispose();
        }
    }

    public static void main(String[] args) {
        inHoaDon("7", 4000000);
        inThongBaoBep("7");
    }
}