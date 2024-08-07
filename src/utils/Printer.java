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
import java.util.logging.Level;
import java.util.logging.Logger;

public class Printer {

    private static final HoaDonDAO hdDAO = new HoaDonDAO();
    private static final KhachHangDAO khDAO = new KhachHangDAO();
    private static final NhanVienDAO nvDAO = new NhanVienDAO();
    private static final HoaDonChiTietDAO hdctDAO = new HoaDonChiTietDAO();
    private static final MonAnDAO maDAO = new MonAnDAO();

    private static final DecimalFormat fmTien = new DecimalFormat("#,#00");
    private static final DateTimeFormatter fmThoiGian = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static String tenNhaHang = "NHÀ HÀNG L'ESCALE CẦN THƠ";
    public static String tieuDe = "HÓA ĐƠN THANH TOÁN";
    public static String diaChi = "Tầng 4, 01 Ngô Quyền Tan An Ward, Ninh Kieu 92000 Việt Nam";
    public static String camOn = "Trân trọng cảm ơn!";

    public static String phieuCheBien = "PHIẾU CHẾ BIẾN";

    private static final int width = 80;
    private static final int sizeHeader = 210;
    private static final int sizeFooter = 140;
    private static final int margin_left_right = 2;
    private static final int margin_top_bottom = 5;
    private static final Font fontTieuDe = new Font("Arial", Font.BOLD, 14);
    private static final Font fontPlain = new Font("Arial", Font.PLAIN, 10);
    private static final Font fontBold = new Font("Arial", Font.BOLD, 10);
    private static final Font fontHeader = new Font("Arial", Font.BOLD, 12);
    private static final Font fontBody = new Font("Arial", Font.PLAIN, 12);

    public static boolean inThongBaoBep(String maHD) {
        PrinterJob job = PrinterJob.getPrinterJob();

        PageFormat pf = job.defaultPage();

        HoaDon hd = hdDAO.selectById(Integer.parseInt(maHD));

        int heightHDCT = getHeightHDCT(hdctDAO.selectHDCT(maHD), fontHeader, (float) (mmToPonit(width) * 0.7));
        System.out.println(heightHDCT);

        Paper pr = new Paper();
        pr.setSize(mmToPonit(width), mmToPonit(120) + heightHDCT);

        pr.setImageableArea(mmToPonit(margin_left_right), mmToPonit(margin_top_bottom), (mmToPonit(width) - mmToPonit(margin_left_right)) * 1.745, mmToPonit(120) + heightHDCT);

        pf.setPaper(pr);

        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }

                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(pf.getImageableX(), pf.getImageableY());

//                g2d.setColor(new Color(0, 0, 0, 55));
//                g2d.fillRect(0, 0, (int) pf.getImageableWidth(), (int) pf.getImageableHeight());

                g2d.setColor(Color.black);

                int y = 14;

                drawCenter(phieuCheBien, fontTieuDe, g2d, pf.getImageableWidth(), y);

                y += 20;

                drawLeft2("Khách Hàng: ", khDAO.selectById(hd.getMaKH()).getTenKH(), fontBold, fontPlain, g2d, pf.getImageableWidth(), y);

                y += 14;

                drawLeft2("Bàn ăn: ", hd.getMaB(), fontBold, fontPlain, g2d, pf.getImageableWidth(), y);

                y += 14;

                drawLeft2("Ngày lập: ", hd.getNgayLap().format(fmThoiGian), fontBold, fontPlain, g2d, pf.getImageableWidth(), y);

                y += 14;

                drawLeft2("Nhân viên: ", nvDAO.selectById(hd.getMaNV()).getTenNV(), fontBold, fontPlain, g2d, pf.getImageableWidth(), y);
                y += 14;
                g2d.drawRect(0, y, (int) pf.getImageableWidth(), 0);

                y += 20;
                String[] header = new String[]{"Tên Món", "Số lượng", "Đơn giá"};
                float[] widthColumn = new float[]{0.6f, 0.15f, 0.25f};
                drawTable3clm(g2d, pf.getImageableWidth(), y, widthColumn, header, hdctDAO.selectHDCT(String.valueOf(hd.getMaHD())));

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

        HoaDon hd = hdDAO.selectById(Integer.parseInt(maHD));

        int heightHDCT = getHeightHDCT(hdctDAO.selectHDCT(maHD), fontHeader, (float) (mmToPonit(width) * 0.7));
        System.out.println(heightHDCT);

        Paper pr = new Paper();
        pr.setSize(mmToPonit(width), sizeHeader + heightHDCT + sizeFooter);

        pr.setImageableArea(mmToPonit(margin_left_right), mmToPonit(margin_top_bottom), (mmToPonit(width) - mmToPonit(margin_left_right)) * 1.745, sizeHeader + heightHDCT + sizeFooter);

        pf.setPaper(pr);

        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }

                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(pf.getImageableX(), pf.getImageableY());
                int y = 14;

//                g2d.setColor(new Color(0, 0, 0, 55));
//                g2d.fillRect(0, 0, (int) pf.getImageableWidth(), (int) pf.getImageableHeight());
                g2d.setColor(Color.black);
                drawCenter(tenNhaHang, fontTieuDe, g2d, pf.getImageableWidth(), y);
                y += 14;
                drawCenter(diaChi, fontPlain, g2d, pf.getImageableWidth(), y);
                y += 45;
                drawCenter(tieuDe, fontTieuDe, g2d, pf.getImageableWidth(), y);
                y += 14;
                drawCenter("Số HD: " + hd.getMaHD(), fontPlain, g2d, pf.getImageableWidth(), y);
                y += 20;
                drawLeft2("Khách hàng: ", khDAO.selectById(hd.getMaKH()).getTenKH(), fontBold, fontPlain, g2d, pf.getImageableWidth(), y);
                y += 20;
                drawLeft2("Ngày Lập: ", hd.getNgayLap().format(fmThoiGian), fontBold, fontPlain, g2d, pf.getImageableWidth(), y);
                y += 20;
                drawLeft2("Bàn ăn: ", hd.getMaB(), fontBold, fontPlain, g2d, pf.getImageableWidth(), y);
                y += 20;
                drawLeft2("Nhân viên: ", nvDAO.selectById(hd.getMaNV()).getTenNV(), fontBold, fontPlain, g2d, pf.getImageableWidth(), y);
                y += 20;
                g2d.drawRect(0, y, (int) pf.getImageableWidth(), 0);
                y += 20;

                System.err.println("Y: " + y);

                String[] header = new String[]{"Tên món", "Số lượng", "T.Tiền"};
                float[] widthColumn = new float[]{0.6f, 0.15f, 0.25f};
                drawTable3clm(g2d, pf.getImageableWidth(), y, widthColumn, header, hdctDAO.selectHDCT(String.valueOf(hd.getMaHD())));

                int thanhToan = 0;
                for (HoaDonChiTiet hdct : hdctDAO.selectHDCT(String.valueOf(hd.getMaHD()))) {
                    thanhToan += (hdct.getSoLuong() * hdct.getDonGia());
                }

                y += getHeightHDCT(hdctDAO.selectHDCT(String.valueOf(hd.getMaHD())), fontHeader, (float) pf.getImageableWidth()) + 15;

                g2d.drawRect(0, y, (int) pf.getImageableWidth(), 0);

                y += 20;

                drawLeft("THANH TOÁN", fontHeader, g2d, pf.getImageableWidth(), y);
                drawRight(fmTien.format(thanhToan) + "đ", fontBody, g2d, pf.getImageableWidth(), y);

                y += 15;

                drawLeft("Tiền khách trả", fontPlain, g2d, pf.getImageableWidth(), y);
                drawRight(fmTien.format(KHTra) + "đ", fontPlain, g2d, pf.getImageableWidth(), y);

                y += 15;

                g2d.drawRect(0, y, (int) pf.getImageableWidth(), 0);

                y += 15;

                drawLeft("Thối lại", fontPlain, g2d, pf.getImageableWidth(), y);
                drawRight(fmTien.format((KHTra - thanhToan)) + "đ", fontPlain, g2d, pf.getImageableWidth(), y);
                y += 30;

                drawCenter("-------*--***--*-------", fontBold, g2d, pf.getImageableWidth(), y);

                y += 20;
                drawCenter(camOn, fontBold, g2d, pf.getImageableWidth(), y);

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

    private static void drawTable3clm(Graphics2D g2d, double width, int y, float[] widthColumn, String[] header, List<HoaDonChiTiet> list) {
        int x = 0;
        int textWidth;
        float widthColumn1 = (float) (width * widthColumn[0]);
        float widthColumn2 = (float) (width * widthColumn[1]);
        float widthColumn3 = (float) (width * widthColumn[2]);
        int fontSize = fontHeader.getSize() + 8;
        g2d.setFont(fontHeader);
        g2d.drawString(header[0], x, y);

        textWidth = g2d.getFontMetrics().stringWidth(header[1]);
        x = (int) (widthColumn1 + ((widthColumn2 - textWidth) / 2));

        g2d.drawString(header[1], x, y);

        drawRight(header[2], fontHeader, g2d, width, y);
        x = 0;
        fontSize = fontBody.getSize() + 8;
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
                y += fontSize;
                g2d.drawString(text.substring(i + 1, text.length()), x, y);
                y -= 8;
            } else {
                g2d.drawString(text, 0, y);
            }

            text = String.valueOf(hdct.getSoLuong());
            textWidth = g2d.getFontMetrics().stringWidth(text);

            x = (int) (widthColumn1 + ((widthColumn2 - textWidth) / 2));
            g2d.drawString(text, x, y);

            x = (int) (widthColumn1 + widthColumn2);

            text = fmTien.format(hdct.getDonGia() * hdct.getSoLuong());
            drawRight(text, fontBody, g2d, width, y);
            if (i > 0) {
                y += fontSize + 10;
            } else {
                y += fontSize;
            }
        }
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

    private static int getHeightHDCT(List<HoaDonChiTiet> list, Font font, float widthColumn) {
        int y = 0;

        int fontSize = font.getSize() + 8;

        for (HoaDonChiTiet hdct : list) {
            int i = -1;
            String text = maDAO.selectById(hdct.getMaMon()).getTenMon();

            int textWidth = getTextWidth(text, font);
            while (true) {
                String textTemp = text.substring(0, i == -1 ? text.length() - 1 : i);
                if (textWidth > widthColumn) {
                    i = textTemp.lastIndexOf(" ");
                    textWidth = getTextWidth(text.substring(0, i), font);
                } else {
                    break;
                }
            }

            if (i > 0) {
                y += fontSize;
                y -= 8;
            }

            if (i > 0) {
                y += fontSize + 10;
            } else {
                y += fontSize;
            }
        }
        return y;
    }

    private static int getTextWidth(String text, Font font) {
        // Tạo một BufferedImage tạm thời để lấy đối tượng Graphics
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int width = fm.stringWidth(text);
        g.dispose(); // Giải phóng tài nguyên đồ họa
        return width;
    }

    public static void main(String[] args) {
        inHoaDon("2", 1000000);
//        inThongBaoBep("1");
    }
}
