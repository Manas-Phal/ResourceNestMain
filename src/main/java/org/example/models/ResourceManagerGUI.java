package org.example.models;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.net.URI;
import java.util.ArrayList;
import java.awt.Desktop;

import static org.example.models.DBConnection.connect;

public class ResourceManagerGUI extends JFrame {

    JTextField titleField, subjectField, topicField, linkField, searchField;
    JComboBox<String> typeBox, difficultyBox;
    JTable table;
    DefaultTableModel model;
    JPanel graphPanel;
    JPanel graphCanvas;
    JPanel form;

    private boolean isEditMode = false;

    public ResourceManagerGUI() {

        setTitle("ResourceNest - Study Manager");
        setSize(1350, 780);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        // ===== HEADER =====
        JPanel header = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(33, 150, 243),
                        getWidth(), 0, new Color(103, 58, 183)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(100, 65));
        header.setLayout(new BorderLayout());

        ImageIcon logoIcon = new ImageIcon(
                getClass().getResource("/images/logo.png")
        );

        Image scaled = logoIcon.getImage().getScaledInstance(
                60, 60, Image.SCALE_SMOOTH
        );

        JLabel title = new JLabel(
                "  ResourceNest",
                new ImageIcon(scaled),
                JLabel.CENTER
        );

        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));

// logo on LEFT of text
        title.setHorizontalTextPosition(JLabel.RIGHT);
        title.setIconTextGap(10);

        JButton showFormBtn = new JButton("➕ Add");

        header.add(title, BorderLayout.CENTER);
        header.add(showFormBtn, BorderLayout.EAST);

        // ===== FORM =====
        form = new JPanel(new GridLayout(2, 6, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Manage Resource"));

        titleField = createInput("Title");
        subjectField = createInput("Subject");
        topicField = createInput("Topic");
        linkField = createInput("Link");

        typeBox = new JComboBox<>(new String[]{"PDF", "Video", "Website", "Notes"});
        difficultyBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});

        JButton doneBtn = new JButton("✔ Done");
        JButton deleteBtn = new JButton("Delete");
        JButton newBtn = new JButton("Cancel");

        form.add(titleField);
        form.add(subjectField);
        form.add(topicField);
        form.add(linkField);
        form.add(typeBox);
        form.add(difficultyBox);

        form.add(doneBtn);
        form.add(deleteBtn);
        form.add(newBtn);
        form.add(new JLabel());
        form.add(new JLabel());
        form.add(new JLabel());

        form.setVisible(false);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(header, BorderLayout.NORTH);
        topContainer.add(form, BorderLayout.CENTER);

        add(topContainer, BorderLayout.NORTH);

        // ===== SIDEBAR =====
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton all = createSideButton("📚 All");
        JButton bm = createSideButton("⭐ Bookmarked");
        JButton easy = createSideButton("🟢 Easy");
        JButton med = createSideButton("🟡 Medium");
        JButton hard = createSideButton("🔴 Hard");

        easy.setBackground(new Color(76, 175, 80));
        med.setBackground(new Color(255, 152, 0));
        hard.setBackground(new Color(244, 67, 54));

        side.add(all);
        side.add(Box.createVerticalStrut(15));
        side.add(bm);
        side.add(Box.createVerticalStrut(20));
        side.add(easy);
        side.add(Box.createVerticalStrut(10));
        side.add(med);
        side.add(Box.createVerticalStrut(10));
        side.add(hard);
        side.add(Box.createVerticalStrut(25));

        // SEARCH
        searchField = new JTextField("🔍 Search...");
        searchField.setMaximumSize(new Dimension(220, 35));
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().contains("Search")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("🔍 Search...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
                table.setRowSorter(sorter);
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchField.getText()));
            }
        });

        side.add(searchField);
        add(side, BorderLayout.WEST);

        // ===== TABLE =====
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "ID", "Title", "Subject", "Topic", "Link", "Type", "Difficulty", "Bookmark"
        });

        table = new JTable(model);
        table.setRowHeight(28);

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = new JLabel("<html><u>" + v + "</u></html>");
                l.setForeground(Color.BLUE);
                return l;
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                int row = table.getSelectedRow();

                if (e.isControlDown() && row != -1) {
                    openURL(model.getValueAt(row, 4).toString());
                    return;
                }

                if (e.getClickCount() == 2 && row != -1) {
                    openURL(model.getValueAt(row, 4).toString());
                }

                if (row != -1) {
                    titleField.setText(model.getValueAt(row, 1).toString());
                    subjectField.setText(model.getValueAt(row, 2).toString());
                    topicField.setText(model.getValueAt(row, 3).toString());
                    linkField.setText(model.getValueAt(row, 4).toString());

                    typeBox.setSelectedItem(model.getValueAt(row, 5));
                    difficultyBox.setSelectedItem(model.getValueAt(row, 6));

                    isEditMode = true;
                    form.setVisible(true);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);

        // ===== GRAPH (FIXED - ONLY ONCE) =====
        graphPanel = new JPanel(new BorderLayout());
        graphPanel.setPreferredSize(new Dimension(800, 260));

        JPanel graphButtons = new JPanel();

        JButton subGraph = new JButton("Subjects");
        JButton diffGraph = new JButton("Difficulty");
        JButton insightBtn = new JButton("Insights");

        graphButtons.add(subGraph);
        graphButtons.add(diffGraph);
        graphButtons.add(insightBtn);

        graphCanvas = new JPanel(new BorderLayout());

        graphPanel.add(graphButtons, BorderLayout.NORTH);
        graphPanel.add(graphCanvas, BorderLayout.CENTER);

        // ===== CENTER =====
        JPanel center = new JPanel(new BorderLayout());
        center.add(scroll, BorderLayout.CENTER);
        center.add(graphPanel, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);

        // ===== ACTIONS =====
        showFormBtn.addActionListener(e -> {
            clearForm();
            isEditMode = false;
            form.setVisible(true);
        });

        newBtn.addActionListener(e -> {
            clearForm();
            form.setVisible(false);
        });

        doneBtn.addActionListener(e -> {
            if (isEditMode) updateResource();
            else addResource();
            clearForm();
            form.setVisible(false);
        });

        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this resource?");
            if (confirm == JOptionPane.YES_OPTION) deleteResource();
        });

        subGraph.addActionListener(e ->
                drawGraph("SELECT subject, COUNT(*) c FROM resources GROUP BY subject", "Subjects"));

        diffGraph.addActionListener(e ->
                drawGraph("SELECT difficulty, COUNT(*) c FROM resources GROUP BY difficulty", "Difficulty"));

        insightBtn.addActionListener(e -> showInsights());

        all.addActionListener(e -> loadResources("SELECT * FROM resources"));
        bm.addActionListener(e -> loadResources("SELECT * FROM resources WHERE bookmark=1"));
        easy.addActionListener(e -> loadResources("SELECT * FROM resources WHERE difficulty='Easy'"));
        med.addActionListener(e -> loadResources("SELECT * FROM resources WHERE difficulty='Medium'"));
        hard.addActionListener(e -> loadResources("SELECT * FROM resources WHERE difficulty='Hard'"));

        loadResources("SELECT * FROM resources");

        setVisible(true);
    }

    private JTextField createInput(String title) {
        JTextField field = new JTextField();
        field.setBorder(BorderFactory.createTitledBorder(title));
        return field;
    }

    private JButton createSideButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 40));
        return btn;
    }

    private void deleteResource() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select row first");
            return;
        }
        try (Connection c = connect()) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM resources WHERE id=?");
            ps.setInt(1, Integer.parseInt(model.getValueAt(row, 0).toString()));
            ps.executeUpdate();
            loadResources("SELECT * FROM resources");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openURL(String url) {
        try {
            if (!url.startsWith("http")) url = "https://" + url;
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid Link");
        }
    }

    private void clearForm() {
        titleField.setText("");
        subjectField.setText("");
        topicField.setText("");
        linkField.setText("");
        isEditMode = false;
    }

    private void loadResources(String q) {
        model.setRowCount(0);
        try (Connection c = connect();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(q)) {
            while (r.next()) {
                model.addRow(new Object[]{
                        r.getInt("id"),
                        r.getString("title"),
                        r.getString("subject"),
                        r.getString("topic"),
                        r.getString("link"),
                        r.getString("type"),
                        r.getString("difficulty"),
                        r.getInt("bookmark")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addResource() {
        try (Connection c = connect()) {
            PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO resources(title,subject,topic,link,type,difficulty) VALUES(?,?,?,?,?,?)"
            );
            ps.setString(1, titleField.getText());
            ps.setString(2, subjectField.getText());
            ps.setString(3, topicField.getText());
            ps.setString(4, linkField.getText());
            ps.setString(5, typeBox.getSelectedItem().toString());
            ps.setString(6, difficultyBox.getSelectedItem().toString());
            ps.executeUpdate();
            loadResources("SELECT * FROM resources");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateResource() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        try (Connection c = connect()) {
            PreparedStatement ps = c.prepareStatement(
                    "UPDATE resources SET title=?,subject=?,topic=?,link=?,type=?,difficulty=? WHERE id=?"
            );
            ps.setInt(7, Integer.parseInt(model.getValueAt(row, 0).toString()));
            ps.setString(1, titleField.getText());
            ps.setString(2, subjectField.getText());
            ps.setString(3, topicField.getText());
            ps.setString(4, linkField.getText());
            ps.setString(5, typeBox.getSelectedItem().toString());
            ps.setString(6, difficultyBox.getSelectedItem().toString());
            ps.executeUpdate();
            loadResources("SELECT * FROM resources");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawGraph(String query, String title) {

        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                try (Connection conn = connect();
                     Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery(query)) {

                    ArrayList<String> labels = new ArrayList<>();
                    ArrayList<Integer> values = new ArrayList<>();

                    int max = 1;

                    while (rs.next()) {
                        String label = rs.getString(1);
                        int val = rs.getInt("c");

                        if (label == null) label = "Unknown";

                        labels.add(label);
                        values.add(val);

                        if (val > max) max = val;
                    }

                    if (values.size() == 0) {
                        g.drawString("No Data Available", getWidth()/2 - 60, getHeight()/2);
                        return;
                    }

                    Graphics2D g2 = (Graphics2D) g;

                    int width = getWidth();
                    int height = getHeight();

                    int padding = 60;
                    int base = height - 60;

                    int count = values.size();
                    int gap = 30;
                    int barWidth = Math.max(50, (width - 2*padding - (count-1)*gap)/count);

                    int x = padding;

                    g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    g2.drawString(title + " Graph", width/2 - 70, 25);

                    for (int i = 0; i < count; i++) {

                        int val = values.get(i);
                        int barHeight = (int)((val * 1.0 / max) * (height - 140));

                        String label = labels.get(i).toLowerCase();

                        GradientPaint gp;

                        // 🎯 COLOR LOGIC
                        if (title.toLowerCase().contains("difficulty")) {

                            if (label.contains("easy")) {
                                gp = new GradientPaint(x, base - barHeight,
                                        new Color(56,142,60),
                                        x, base,
                                        new Color(129,199,132));
                            }
                            else if (label.contains("medium")) {
                                gp = new GradientPaint(x, base - barHeight,
                                        new Color(255,143,0),
                                        x, base,
                                        new Color(255,204,128));
                            }
                            else {
                                gp = new GradientPaint(x, base - barHeight,
                                        new Color(211,47,47),
                                        x, base,
                                        new Color(239,154,154));
                            }

                        } else {
                            gp = new GradientPaint(x, base - barHeight,
                                    new Color(33,150,243),
                                    x, base,
                                    new Color(144,202,249));
                        }

                        // DRAW BAR
                        g2.setPaint(gp);
                        g2.fillRoundRect(x, base - barHeight, barWidth, barHeight, 15, 15);

                        // VALUE ON TOP
                        g2.setColor(Color.BLACK);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                        g2.drawString(String.valueOf(val), x + barWidth/3, base - barHeight - 5);

                        // LABEL BELOW
                        String shortLabel = labels.get(i);
                        if (shortLabel.length() > 10)
                            shortLabel = shortLabel.substring(0,10) + "..";

                        int textWidth = g.getFontMetrics().stringWidth(shortLabel);
                        int labelX = x + (barWidth - textWidth)/2;

                        g2.drawString(shortLabel, labelX, base + 18);

                        x += barWidth + gap;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        graphCanvas.removeAll();
        graphCanvas.add(panel, BorderLayout.CENTER);
        graphCanvas.revalidate();
        graphCanvas.repaint();
    }

    private void showInsights(){

        try(Connection c = connect();
            Statement s = c.createStatement()){

            ResultSet r = s.executeQuery(
                    "SELECT " +
                            "COUNT(*) total, " +
                            "COALESCE(SUM(bookmark),0) bm, " +
                            "SUM(CASE WHEN LOWER(difficulty)='easy' THEN 1 ELSE 0 END) easy, " +
                            "SUM(CASE WHEN LOWER(difficulty)='medium' THEN 1 ELSE 0 END) med, " +
                            "SUM(CASE WHEN LOWER(difficulty)='hard' THEN 1 ELSE 0 END) hard " +
                            "FROM resources"
            );

            if(r.next()){

                int total = r.getInt("total");
                int bm = r.getInt("bm");
                int easy = r.getInt("easy");
                int med = r.getInt("med");
                int hard = r.getInt("hard");

                double percent = total == 0 ? 0 : (bm * 100.0 / total);

                // SMART ANALYSIS
                String weak;
                if(hard > med && hard > easy) weak = "Too many Hard topics";
                else if(easy > med && easy > hard) weak = "Too many Easy topics";
                else weak = "Balanced";

                String rec;
                if(total == 0) rec = "Start adding resources";
                else if(hard > med) rec = "Revise basics (too many hard)";
                else if(easy > med) rec = "Increase Medium difficulty";
                else rec = "Good balance — keep going";

                JOptionPane.showMessageDialog(this,
                        "📊 RESOURCE INSIGHTS\n\n" +
                                "Total Resources: " + total +
                                "\nBookmarks: " + bm + " (" + String.format("%.1f", percent) + "%)" +
                                "\n\nEasy: " + easy +
                                "\nMedium: " + med +
                                "\nHard: " + hard +
                                "\n\n⚠ Weak Area: " + weak +
                                "\n💡 Suggestion: " + rec
                );
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ResourceManagerGUI());
    }
}