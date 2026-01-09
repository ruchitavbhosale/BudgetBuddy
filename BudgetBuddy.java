package com.mycompany.budgetbuddy;


import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class BudgetBuddy extends JFrame {

    private JTextField incomeField, expenseField;
    private JTextArea summaryArea;

    private double totalIncome = 0;
    private double totalExpense = 0;

    public BudgetBuddy() {

        // Window settings
        
        setUndecorated(true);
        setOpacity(0f);
        setSize(1050, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ============== TOP GRADIENT BAR ==============
        
        JPanel top = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(33, 150, 243),
                        getWidth(), 0, new Color(25, 118, 210)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        top.setPreferredSize(new Dimension(1000, 75));
        top.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));

        JLabel title = new JLabel("BUDGET BUDDY");
        title.setForeground(Color.white);
        title.setFont(new Font("SansSerif", Font.BOLD, 34));
        top.add(title);

        add(top, BorderLayout.NORTH);

        // ================= LEFT SIDEBAR ==================
        JPanel menu = new JPanel();
        menu.setBackground(Color.WHITE);
        menu.setLayout(new GridLayout(6, 1, 18, 18));
        menu.setBorder(BorderFactory.createEmptyBorder(40, 25, 40, 25));
        menu.setPreferredSize(new Dimension(260, 600));

        JButton addIncomeBtn = createButton("➕   Add Income");
        JButton addExpenseBtn = createButton("💸   Add Expense");
        JButton summaryBtn = createButton("📊   View Summary");
        JButton exportBtn = createButton("📁   Export Report");
        JButton resetBtn = createButton("🔄   Reset Data");
        JButton exitBtn = createButton("❌   Exit");

        menu.add(addIncomeBtn);
        menu.add(addExpenseBtn);
        menu.add(summaryBtn);
        menu.add(exportBtn);
        menu.add(resetBtn);
        menu.add(exitBtn);

        add(menu, BorderLayout.WEST);

        
        // ================== RIGHT MAIN AREA ==================
        
        JPanel right = new JPanel();
        right.setLayout(new GridLayout(3, 1, 20, 20));
        right.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        right.setBackground(new Color(245, 245, 245));

        incomeField = styledTextField();
        expenseField = styledTextField();

        summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("SansSerif", Font.PLAIN, 17));
        summaryArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        summaryArea.setBackground(Color.WHITE);

        right.add(createCard("💰 Enter Income Amount", incomeField));
        right.add(createCard("💳 Enter Expense Amount", expenseField));
        right.add(createCard("📘 Summary", new JScrollPane(summaryArea)));

        add(right, BorderLayout.CENTER);

        // ================= BUTTON ACTIONS =================
        addIncomeBtn.addActionListener(e -> addIncome());
        addExpenseBtn.addActionListener(e -> addExpense());
        summaryBtn.addActionListener(e -> showSummary());
        exportBtn.addActionListener(e -> exportFile());
        resetBtn.addActionListener(e -> resetData());
        exitBtn.addActionListener(e -> System.exit(0));

        loadFromFile();
    }

    // ============== MODERN BLUE BUTTON ==============
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 17));
        btn.setBackground(new Color(33, 150, 243));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        return btn;
    }

    // ============== ROUNDED INPUT FIELD ==============
    private JTextField styledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("SansSerif", Font.PLAIN, 18));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 2, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        return tf;
    }

    // ============== GLASS CARD PANEL ============
    private JPanel createCard(String titleText, Component comp) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel label = new JLabel(titleText);
        label.setFont(new Font("SansSerif", Font.BOLD, 22));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        card.add(label, BorderLayout.NORTH);
        card.add(comp, BorderLayout.CENTER);

        return card;
    }

    // DATE TIME FORMATTER
    private String getDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy  hh:mm a"));
    }

    // ================= ADD INCOME =================
    private void addIncome() {
        try {
            double value = Double.parseDouble(incomeField.getText().trim());
            if (value <= 0) {
                JOptionPane.showMessageDialog(this, "Enter a valid positive amount!");
                return;
            }

            totalIncome += value;

            saveToFile("Income: " + value + " | Date: " + getDateTime());

            JOptionPane.showMessageDialog(this, "Income Added Successfully!");
            incomeField.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid Amount!");
        }
    }

    // ================= ADD EXPENSE =================
    private void addExpense() {
        try {
            double value = Double.parseDouble(expenseField.getText().trim());
            if (value <= 0) {
                JOptionPane.showMessageDialog(this, "Enter a valid positive amount!");
                return;
            }

            String reason = JOptionPane.showInputDialog(this,
                    "Enter reason for this expense:", "Expense Reason", JOptionPane.PLAIN_MESSAGE);

            if (reason == null || reason.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid reason!");
                return;
            }

            totalExpense += value;

            saveToFile("Expense: " + value + " | Reason: " + reason + " | Date: " + getDateTime());

            JOptionPane.showMessageDialog(this, "Expense Added Successfully!");
            expenseField.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid Amount!");
        }
    }

    // ================= SUMMARY =================
    private void showSummary() {
    double balance = totalIncome - totalExpense;
    double expenseRatio = (totalIncome == 0) ? 0 : (totalExpense / totalIncome) * 100;

    summaryArea.setText(
            "============== BUDGET SUMMARY ==============\n\n" +
            "Total Income: ₹" + totalIncome + "\n" +
            "Total Expenses: ₹" + totalExpense + "\n" +
            "---------------------------------------------\n" +
            "Remaining Balance: ₹" + balance + "\n" +
            "Expense Ratio: " + String.format("%.2f", expenseRatio) + "%\n" +
            "---------------------------------------------\n" +
            (balance >= 0
                    ? "💡 Status: Great! You are within budget."
                    : "⚠ Status: Warning! You overspent your budget.") +
            "\n\n==============================================="
    );

    showDetailedPopup(balance, expenseRatio);
}

    
    private void showDetailedPopup(double balance, double ratio) {

    JDialog popup = new JDialog(this, "Detailed Financial Report", true);
    popup.setSize(520, 420);
    popup.setLocationRelativeTo(this);

    JPanel panel = new JPanel();
    panel.setBackground(Color.WHITE);
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

    JLabel heading = new JLabel("📊 Detailed Budget Analysis");
    heading.setFont(new Font("SansSerif", Font.BOLD, 24));
    heading.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel incomeLabel = new JLabel("Total Income:  ₹" + totalIncome);
    styleDetailText(incomeLabel);

    JLabel expenseLabel = new JLabel("Total Expenses:  ₹" + totalExpense);
    styleDetailText(expenseLabel);

    JLabel balanceLabel = new JLabel("Remaining Balance:  ₹" + balance);
    styleDetailText(balanceLabel);

    JLabel ratioLabel = new JLabel("Expense Ratio:  " + String.format("%.2f", ratio) + "%");
    styleDetailText(ratioLabel);

    JLabel status = new JLabel(balance >= 0
            ? "🟢 You are financially stable! Keep tracking your savings."
            : "🔴 You overspent this period! Reduce extra expenses.");
    status.setFont(new Font("SansSerif", Font.BOLD, 16));
    status.setForeground(balance >= 0 ? new Color(0, 128, 0) : Color.RED);
    status.setAlignmentX(Component.CENTER_ALIGNMENT);

    panel.add(heading);
    panel.add(Box.createVerticalStrut(20));
    panel.add(incomeLabel);
    panel.add(expenseLabel);
    panel.add(balanceLabel);
    panel.add(ratioLabel);
    panel.add(Box.createVerticalStrut(20));
    panel.add(status);

    popup.add(panel);
    popup.setVisible(true);
}

private void styleDetailText(JLabel lbl) {
    lbl.setFont(new Font("SansSerif", Font.PLAIN, 18));
    lbl.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
    lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
}

    // ================= EXPORT =================
private void exportFile() {
    try {
        // Create a default filename with timestamp
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String defaultFileName = "Budget_Report_" + timeStamp + ".txt";

        // Open a save dialog
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Budget Report");
        fileChooser.setSelectedFile(new File(defaultFileName));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Write the summary to the selected file
            try (FileWriter fw = new FileWriter(fileToSave)) {
                fw.write(summaryArea.getText());
            }

            JOptionPane.showMessageDialog(this,
                    "Report Exported Successfully!\nSaved at: " + fileToSave.getAbsolutePath(),
                    "Export Success", JOptionPane.INFORMATION_MESSAGE);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "Error Exporting File!\n" + e.getMessage(),
                "Export Error", JOptionPane.ERROR_MESSAGE);
    }
}

    // ================= RESET =================
    private void resetData() {
        totalIncome = 0;
        totalExpense = 0;
        summaryArea.setText("");

        new File("budget_data.txt").delete();

        JOptionPane.showMessageDialog(this, "All Data Reset Successfully!");
    }

    // ================= SAVE TO FILE =================
    private void saveToFile(String text) {
        try (FileWriter fw = new FileWriter("budget_data.txt", true)) {
            fw.write(text + "\n");
        } catch (Exception ignored) {}
    }

    // ================= LOAD FILE =================
    private void loadFromFile() {
        try {
            File file = new File("budget_data.txt");
            if (!file.exists()) return;

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("Income:"))
                    totalIncome += Double.parseDouble(line.split(": ")[1].split(" ")[0]);
                if (line.startsWith("Expense:"))
                    totalExpense += Double.parseDouble(line.split(": ")[1].split(" ")[0]);
            }

        } catch (Exception ignored) {}
    }

    // ================= FADE IN ANIMATION =================
    private void fadeIn() {
        for (float i = 0; i <= 1; i += 0.03f) {
            setOpacity(i);
            try {
                Thread.sleep(18);
            } catch (Exception ignored) {}
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BudgetBuddy app = new BudgetBuddy();
            app.setVisible(true);
            app.fadeIn();
        });
    }

}                                          
