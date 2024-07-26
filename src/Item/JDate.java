package Item;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JDate extends JPanel {
    private final JDateChooser dateChooser;

    public JDate() {
        // Thiết lập giao diện của JPanel
        dateChooser = new JDateChooser();
        dateChooser.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        dateChooser.setDateFormatString("dd/MM/yyyy");
    }

    public String getSelectedDate() {
        Date selectedDate = dateChooser.getDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return selectedDate != null ? dateFormat.format(selectedDate) : null;
    }

    public void setDate(Date date) {
        dateChooser.setDate(date);
    }
}
