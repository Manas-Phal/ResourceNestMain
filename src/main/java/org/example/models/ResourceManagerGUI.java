package org.example.models;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;
import java.net.URI;

public class ResourceManagerGUI extends JFrame {

    JTextField titleField, subjectField, topicField, linkField, searchField;
    JComboBox<String> typeBox;
    JTable table;
    DefaultTableModel model;
    JPanel sideBar;

    public ResourceManagerGUI() {

        setTitle("ResourceNest - Study Manager");
        setSize(1100,650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ================= TOP =================
        JPanel topPanel = new JPanel(new BorderLayout());

        JButton menuBtn = new JButton("☰");
        topPanel.add(menuBtn, BorderLayout.WEST);

        JLabel header = new JLabel("📚 ResourceNest", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topPanel.add(header, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(2,1,10,10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

        JPanel row1 = new JPanel(new GridLayout(1,5,10,10));
        row1.add(new JLabel("Title"));
        row1.add(new JLabel("Subject"));
        row1.add(new JLabel("Topic"));
        row1.add(new JLabel("Link"));
        row1.add(new JLabel("Type"));

        JPanel row2 = new JPanel(new GridLayout(1,5,10,10));
        titleField = new JTextField();
        subjectField = new JTextField();
        topicField = new JTextField();
        linkField = new JTextField();
        typeBox = new JComboBox<>(new String[]{"PDF","Video","Website","Notes"});

        row2.add(titleField);
        row2.add(subjectField);
        row2.add(topicField);
        row2.add(linkField);
        row2.add(typeBox);

        formPanel.add(row1);
        formPanel.add(row2);

        topPanel.add(formPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // ================= CENTER =================
        JPanel centerPanel = new JPanel(new BorderLayout());

        sideBar = new JPanel(new GridLayout(6,1,10,10));
        sideBar.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JButton allBtn = new JButton("📚 All");
        JButton bookmarkBtn = new JButton("⭐ Bookmarked");
        JButton importantBtn = new JButton("🔥 Important");

        sideBar.add(allBtn);
        sideBar.add(bookmarkBtn);
        sideBar.add(importantBtn);

        centerPanel.add(sideBar, BorderLayout.WEST);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "ID","Title","Subject","Topic","Link","Type","⭐"
        });

        table = new JTable(model);
        table.setRowHeight(25);

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){
                JLabel l = new JLabel("<html><u>"+v+"</u></html>");
                l.setForeground(Color.BLUE);
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        centerPanel.add(scroll, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // ================= BOTTOM =================
        JPanel bottom = new JPanel(new FlowLayout());

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update"); // 🔥 added
        JButton deleteBtn = new JButton("Delete");
        JButton openBtn = new JButton("Open Link");
        JButton toggleBtn = new JButton("⭐ Toggle");

        JButton statsBtn = new JButton("📊 Subjects Graph");
        JButton topicGraphBtn = new JButton("🔥 Topics Graph");
        JButton progressGraphBtn = new JButton("📈 Progress Graph");
        JButton insightBtn = new JButton("🧠 Insights");

        // SEARCH PLACEHOLDER
        searchField = new JTextField("Search...",15);
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new java.awt.event.FocusAdapter(){
            public void focusGained(java.awt.event.FocusEvent e){
                if(searchField.getText().equals("Search...")){
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent e){
                if(searchField.getText().isEmpty()){
                    searchField.setText("Search...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        bottom.add(addBtn);
        bottom.add(updateBtn); // 🔥 added
        bottom.add(deleteBtn);
        bottom.add(openBtn);
        bottom.add(toggleBtn);
        bottom.add(searchField);
        bottom.add(statsBtn);
        bottom.add(topicGraphBtn);
        bottom.add(progressGraphBtn);
        bottom.add(insightBtn);

        add(bottom, BorderLayout.SOUTH);

        // ================= ACTIONS =================

        menuBtn.addActionListener(e -> sideBar.setVisible(!sideBar.isVisible()));
        allBtn.addActionListener(e -> load("SELECT * FROM resources"));
        bookmarkBtn.addActionListener(e -> load("SELECT * FROM resources WHERE bookmark=1"));
        importantBtn.addActionListener(e -> loadImportant());

        addBtn.addActionListener(e -> addResource());
        updateBtn.addActionListener(e -> updateResource()); // 🔥 added
        deleteBtn.addActionListener(e -> deleteResource());
        openBtn.addActionListener(e -> openLink());
        toggleBtn.addActionListener(e -> toggleBookmark());

        statsBtn.addActionListener(e -> showSubjectGraph());
        topicGraphBtn.addActionListener(e -> showTopicGraph());
        progressGraphBtn.addActionListener(e -> showProgressGraph());
        insightBtn.addActionListener(e -> showInsights());

        searchField.addKeyListener(new java.awt.event.KeyAdapter(){
            public void keyReleased(java.awt.event.KeyEvent e){
                if(!searchField.getText().equals("Search..."))
                    search(searchField.getText());
            }
        });

        // 🔥 AUTO FILL FIELDS WHEN ROW SELECTED
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if(row != -1){
                titleField.setText(model.getValueAt(row,1).toString());
                subjectField.setText(model.getValueAt(row,2).toString());
                topicField.setText(model.getValueAt(row,3).toString());
                linkField.setText(model.getValueAt(row,4).toString());
                typeBox.setSelectedItem(model.getValueAt(row,5).toString());
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseClicked(java.awt.event.MouseEvent evt){

                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());

                if(col == 4){
                    if(evt.isControlDown() || evt.getClickCount()==2){
                        openLinkFromRow(row);
                    }
                }
            }
        });

        setVisible(true);
        load("SELECT * FROM resources");
    }

    // ================= DB =================

    private void addResource(){
        try(Connection conn = DBConnection.connect()){
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO resources(title,subject,topic,link,type,created_at) VALUES(?,?,?,?,?,datetime('now'))"
            );

            ps.setString(1,titleField.getText());
            ps.setString(2,subjectField.getText());
            ps.setString(3,topicField.getText());
            ps.setString(4,linkField.getText());
            ps.setString(5,typeBox.getSelectedItem().toString());

            ps.executeUpdate();

            titleField.setText("");
            subjectField.setText("");
            topicField.setText("");
            linkField.setText("");

            load("SELECT * FROM resources");

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void updateResource(){
        int row = table.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(this,"Select row to update");
            return;
        }

        int id = (int) model.getValueAt(row,0);

        try(Connection conn = DBConnection.connect()){
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE resources SET title=?, subject=?, topic=?, link=?, type=? WHERE id=?"
            );

            ps.setString(1,titleField.getText());
            ps.setString(2,subjectField.getText());
            ps.setString(3,topicField.getText());
            ps.setString(4,linkField.getText());
            ps.setString(5,typeBox.getSelectedItem().toString());
            ps.setInt(6,id);

            ps.executeUpdate();
            load("SELECT * FROM resources");

        }catch(Exception e){ e.printStackTrace(); }
    }

    private void load(String sql){
        model.setRowCount(0);
        try(Connection conn = DBConnection.connect();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)){

            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("subject"),
                        rs.getString("topic"),
                        rs.getString("link"),
                        rs.getString("type"),
                        rs.getInt("bookmark")
                });
            }
        }catch(Exception e){ e.printStackTrace(); }
    }

    private void search(String key){
        load("SELECT * FROM resources WHERE title LIKE '%"+key+"%' OR subject LIKE '%"+key+"%' OR topic LIKE '%"+key+"%'");
    }

    private void loadImportant(){
        load("SELECT * FROM resources WHERE bookmark=1 ORDER BY subject");
    }

    private void deleteResource(){
        int row = table.getSelectedRow();
        if(row==-1) return;

        int id = (int) model.getValueAt(row,0);

        try(Connection conn = DBConnection.connect()){
            PreparedStatement ps = conn.prepareStatement("DELETE FROM resources WHERE id=?");
            ps.setInt(1,id);
            ps.executeUpdate();
            load("SELECT * FROM resources");
        }catch(Exception e){ e.printStackTrace(); }
    }

    private void toggleBookmark(){
        int row = table.getSelectedRow();
        if(row==-1) return;

        int id = (int) model.getValueAt(row,0);
        int val = (int) model.getValueAt(row,6);

        try(Connection conn = DBConnection.connect()){
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE resources SET bookmark=? WHERE id=?");

            ps.setInt(1, val==1?0:1);
            ps.setInt(2, id);
            ps.executeUpdate();

            load("SELECT * FROM resources");

        }catch(Exception e){ e.printStackTrace(); }
    }

    private void openLink(){
        int row = table.getSelectedRow();
        if(row==-1) return;
        openLinkFromRow(row);
    }

    private void openLinkFromRow(int row){
        try{
            String link = model.getValueAt(row,4).toString();
            Desktop.getDesktop().browse(new URI(link));
        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"Invalid Link");
        }
    }

    // ================= GRAPHS =================
    private void drawBarGraph(String query, String title){

        JFrame frame = new JFrame(title);
        frame.setSize(750,550);

        JPanel panel = new JPanel(){

            protected void paintComponent(Graphics g){
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));

                try(Connection conn = DBConnection.connect();
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(query)){

                    java.util.List<String> labels = new java.util.ArrayList<>();
                    java.util.List<Integer> values = new java.util.ArrayList<>();

                    int max = 0;

                    while(rs.next()){
                        labels.add(rs.getString(1));
                        int val = rs.getInt("c");
                        values.add(val);
                        if(val > max) max = val;
                    }

                    if(labels.size()==0){
                        g.drawString("No Data Available", 300,250);
                        return;
                    }

                    int width = getWidth();
                    int height = getHeight();

                    int padding = 80;
                    int graphWidth = width - 2*padding;
                    int graphHeight = height - 2*padding;

                    int barWidth = graphWidth / labels.size();

                    // AXES
                    g2.drawLine(padding, height-padding, width-padding, height-padding); // X
                    g2.drawLine(padding, height-padding, padding, padding); // Y

                    // TITLE
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    g2.drawString(title, width/2 - 80, 30);

                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));

                    // Y AXIS SCALE
                    for(int i=0; i<=max; i++){
                        int y = height - padding - (i * graphHeight / max);
                        g2.drawString(String.valueOf(i), padding-40, y);
                        g2.drawLine(padding-5, y, padding, y); // tick
                    }

                    // BARS
                    for(int i=0; i<labels.size(); i++){

                        int value = values.get(i);
                        int barHeight = value * graphHeight / max;

                        int x = padding + i*barWidth + 15;
                        int y = height - padding - barHeight;

                        g2.fillRect(x, y, barWidth-30, barHeight);

                        // VALUE ON TOP
                        g2.drawString(String.valueOf(value), x, y-5);

                        // X LABEL
                        g2.drawString(labels.get(i), x, height-padding+20);
                    }

                    // AXIS LABELS
                    g2.drawString("Count", padding-60, padding-20);
                    g2.drawString("Categories", width/2 - 40, height-10);

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };

        frame.add(panel);
        frame.setVisible(true);
    }

    private void showSubjectGraph(){
        drawBarGraph("SELECT subject, COUNT(*) as c FROM resources GROUP BY subject","Resources per Subject");
    }

    private void showTopicGraph(){
        drawBarGraph("SELECT topic, COUNT(*) as c FROM resources WHERE bookmark=1 GROUP BY topic","Important Topics");
    }

    private void showProgressGraph(){
        drawBarGraph("SELECT DATE(created_at), COUNT(*) as c FROM resources GROUP BY DATE(created_at)","Daily Progress");
    }


    private void showInsights(){
        try(Connection conn = DBConnection.connect();
            Statement st = conn.createStatement()){

            ResultSet t = st.executeQuery("SELECT COUNT(*) FROM resources");
            t.next();

            ResultSet b = st.executeQuery("SELECT COUNT(*) FROM resources WHERE bookmark=1");
            b.next();

            JOptionPane.showMessageDialog(this,
                    "Total: "+t.getInt(1)+"\nBookmarked: "+b.getInt(1));

        }catch(Exception e){ e.printStackTrace(); }
    }

    public static void main(String[] args) {
        new ResourceManagerGUI();
    }
}