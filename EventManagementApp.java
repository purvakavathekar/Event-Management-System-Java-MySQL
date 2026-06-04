package project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;

public class EventManagementApp {

    // ===================== THEME COLORS =====================
    static final Color TEAL       = new Color(0x2A, 0x9D, 0x8F);
    static final Color SKY_BLUE   = new Color(0x48, 0xCA, 0xE4);
    static final Color LAVENDER   = new Color(0xB3, 0x9D, 0xDB);
    static final Color LIGHT_PINK = new Color(0xF4, 0xB8, 0xD1);
    static final Color WHITE      = Color.WHITE;
    static final Color DARK_TEXT  = new Color(0x2C, 0x2C, 0x2C);

    /** Apply gradient background to any JPanel */
    static JPanel gradientPanel() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                    0, 0, TEAL,
                    getWidth(), getHeight(), LIGHT_PINK);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }

    /** Style a button with rounded corners and theme color */
    static void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
    }

    /** Style a JTable with the theme */
    static void styleTable(JTable table) {
        table.setRowHeight(26);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setSelectionBackground(LAVENDER);
        table.setSelectionForeground(WHITE);
        table.setGridColor(new Color(0xE0, 0xE0, 0xE0));
        table.setBackground(WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(TEAL);
        header.setForeground(WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 13));
    }

    /** Style a text field */
    static void styleField(JTextField f) {
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SKY_BLUE, 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    }

    static class DBConnection {
        private static final String URL  = "jdbc:mysql://localhost:3306/mock";
        private static final String USER = "root";
        private static final String PASS = "purva@123";
        static {
            try { Class.forName("com.mysql.cj.jdbc.Driver"); }
            catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found");
            }
        }
        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASS);
        }
    }

    // ===================== LOGIN FRAME =====================
    static class LoginFrame extends JFrame {
        private JTextField     usernameField = new JTextField();
        private JPasswordField passwordField = new JPasswordField();

        LoginFrame() {
            setTitle("Event Management Login");
            setSize(420, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            JPanel bg = gradientPanel();
            bg.setLayout(new GridBagLayout());
            setContentPane(bg);

            JPanel card = new JPanel(new GridBagLayout());
            card.setBackground(WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LAVENDER, 2, true),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));
            card.setOpaque(true);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill   = GridBagConstraints.HORIZONTAL;

            JLabel title = new JLabel("Admin Login", SwingConstants.CENTER);
            title.setFont(new Font("Arial", Font.BOLD, 24));
            title.setForeground(TEAL);
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            card.add(title, gbc);

            gbc.gridwidth = 1;
            JLabel uLbl = new JLabel("Username:");
            uLbl.setFont(new Font("Arial", Font.PLAIN, 13));
            gbc.gridx = 0; gbc.gridy = 1; card.add(uLbl, gbc);
            styleField(usernameField);
            gbc.gridx = 1; card.add(usernameField, gbc);

            JLabel pLbl = new JLabel("Password:");
            pLbl.setFont(new Font("Arial", Font.PLAIN, 13));
            gbc.gridx = 0; gbc.gridy = 2; card.add(pLbl, gbc);
            styleField(passwordField);
            gbc.gridx = 1; card.add(passwordField, gbc);

            JButton loginBtn = new JButton("Login");
            styleButton(loginBtn, TEAL);
            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
            card.add(loginBtn, gbc);

            bg.add(card);
            loginBtn.addActionListener(e -> login());
        }

        private void login() {
            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword());
            if (user.equals("admin") && pass.equals("admin123")) {
                dispose();
                new MainFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password");
            }
        }
    }

    // ===================== DASHBOARD PANEL =====================
    static class DashboardPanel extends JPanel {
        private JLabel totalEvents  = new JLabel("0", SwingConstants.CENTER);
        private JLabel totalClients = new JLabel("0", SwingConstants.CENTER);
        private JLabel totalVenues  = new JLabel("0", SwingConstants.CENTER);
        private JLabel totalVendors = new JLabel("0", SwingConstants.CENTER);
        private JLabel totalBudget  = new JLabel("0", SwingConstants.CENTER);
        private DefaultTableModel upcomingModel = new DefaultTableModel(
                new String[]{"Event ID", "Title", "Date", "Budget", "Status"}, 0);
        private JTable upcomingTable = new JTable(upcomingModel);

        DashboardPanel() {
            setLayout(new BorderLayout(10, 10));
            setBackground(new Color(0xF8, 0xF9, 0xFA));

            Color[] cardColors = {TEAL, SKY_BLUE, LAVENDER, LIGHT_PINK,
                                  new Color(0x80, 0xCB, 0xC4)};
            String[] titles = {"Total Events","Total Clients","Total Venues",
                               "Total Vendors","Total Budget"};
            JLabel[] labels = {totalEvents, totalClients, totalVenues,
                               totalVendors, totalBudget};

            JPanel cards = new JPanel(new GridLayout(1, 5, 12, 12));
            cards.setOpaque(false);
            cards.setBorder(BorderFactory.createEmptyBorder(14, 14, 6, 14));
            for (int i = 0; i < titles.length; i++)
                cards.add(statCard(titles[i], labels[i], cardColors[i]));

            JButton refresh = new JButton("⟳  Refresh");
            styleButton(refresh, TEAL);
            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnRow.setOpaque(false);
            btnRow.add(refresh);

            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.add(cards, BorderLayout.CENTER);
            top.add(btnRow, BorderLayout.SOUTH);

            styleTable(upcomingTable);
            JScrollPane sp = new JScrollPane(upcomingTable);
            sp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 14, 14, 14),
                BorderFactory.createLineBorder(LAVENDER, 1, true)));

            add(top, BorderLayout.NORTH);
            add(sp,  BorderLayout.CENTER);
            refresh.addActionListener(e -> loadDashboard());
            loadDashboard();
        }

        private JPanel statCard(String title, JLabel value, Color color) {
            JPanel p = new JPanel(new BorderLayout(4, 4)) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(color);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                }
            };
            p.setOpaque(false);
            p.setBorder(BorderFactory.createEmptyBorder(14, 10, 14, 10));
            JLabel lbl = new JLabel(title, SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            lbl.setForeground(WHITE);
            value.setFont(new Font("Arial", Font.BOLD, 26));
            value.setForeground(WHITE);
            p.add(lbl,   BorderLayout.NORTH);
            p.add(value, BorderLayout.CENTER);
            return p;
        }

        private int countRows(Connection con, String table) throws SQLException {
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM " + table);
                 ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }

        private void loadDashboard() {
            upcomingModel.setRowCount(0);
            try (Connection con = DBConnection.getConnection()) {
                totalEvents.setText(String.valueOf(countRows(con, "event")));
                totalClients.setText(String.valueOf(countRows(con, "client")));
                totalVenues.setText(String.valueOf(countRows(con, "venue")));
                totalVendors.setText(String.valueOf(countRows(con, "vendor")));
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT COALESCE(SUM(Budget),0) FROM event");
                     ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) totalBudget.setText(String.valueOf(rs.getDouble(1)));
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT EventID,Title,EventDate,Budget,Status " +
                        "FROM event ORDER BY EventDate ASC LIMIT 10");
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next())
                        upcomingModel.addRow(new Object[]{
                            rs.getInt(1), rs.getString(2),
                            rs.getDate(3), rs.getDouble(4), rs.getString(5)});
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Dashboard error: " + e.getMessage());
            }
        }
    }

    // ===================== COMMON CRUD PANEL =====================
    static abstract class CrudPanel extends JPanel {
        protected DefaultTableModel model;
        protected JTable            table;
        protected JTextField        searchField = new JTextField();

        CrudPanel(String[] columns) {
            setLayout(new BorderLayout(8, 8));
            setBackground(new Color(0xF8, 0xF9, 0xFA));
            model = new DefaultTableModel(columns, 0);
            table = new JTable(model);
            styleTable(table);
            JScrollPane sp = new JScrollPane(table);
            sp.setBorder(BorderFactory.createLineBorder(LAVENDER, 1, true));
            add(sp, BorderLayout.CENTER);
        }

        protected JPanel searchPanel() {
            JPanel p = new JPanel(new BorderLayout(8, 8));
            p.setOpaque(false);
            p.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
            styleField(searchField);
            JButton searchBtn  = new JButton("Search");
            JButton refreshBtn = new JButton("Refresh");
            styleButton(searchBtn,  SKY_BLUE);
            styleButton(refreshBtn, LAVENDER);
            JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            btns.setOpaque(false);
            btns.add(searchBtn); btns.add(refreshBtn);
            JLabel lbl = new JLabel("Search: ");
            lbl.setFont(new Font("Arial", Font.BOLD, 13));
            p.add(lbl, BorderLayout.WEST);
            p.add(searchField, BorderLayout.CENTER);
            p.add(btns, BorderLayout.EAST);
            searchBtn.addActionListener(e  -> load(searchField.getText().trim()));
            refreshBtn.addActionListener(e -> { searchField.setText(""); load(""); });
            return p;
        }

        /** Builds a consistent themed form + button row */
        protected JPanel buildFormButtons(JPanel form,
                                          Runnable onAdd, Runnable onUpdate,
                                          Runnable onDelete, Runnable onClear) {
            JPanel main = new JPanel(new BorderLayout(6, 6));
            main.setOpaque(false);
            main.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

            form.setOpaque(false);
            main.add(form, BorderLayout.CENTER);

            JButton addBtn    = new JButton("Add");
            JButton updateBtn = new JButton("Update");
            JButton deleteBtn = new JButton("Delete");
            JButton clearBtn  = new JButton("Clear");
            styleButton(addBtn,    TEAL);
            styleButton(updateBtn, SKY_BLUE);
            styleButton(deleteBtn, new Color(0xE57373));
            styleButton(clearBtn,  LAVENDER);

            JPanel btn = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
            btn.setOpaque(false);
            btn.add(addBtn); btn.add(updateBtn); btn.add(deleteBtn); btn.add(clearBtn);
            main.add(btn, BorderLayout.SOUTH);

            addBtn.addActionListener(e    -> onAdd.run());
            updateBtn.addActionListener(e -> onUpdate.run());
            deleteBtn.addActionListener(e -> onDelete.run());
            clearBtn.addActionListener(e  -> onClear.run());
            return main;
        }

        abstract void load(String keyword);
    }

    // ===================== CLIENT PANEL =====================
    static class ClientPanel extends CrudPanel {
        JTextField idField      = new JTextField();
        JTextField nameField    = new JTextField();
        JTextField contactField = new JTextField();
        JTextField emailField   = new JTextField();
        JTextField companyField = new JTextField();

        ClientPanel() {
            super(new String[]{"ClientID","Name","ContactNumber","Email","CompanyName"});
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setOpaque(false);
            wrapper.add(searchPanel(), BorderLayout.NORTH);
            wrapper.add(formPanel(),   BorderLayout.CENTER);
            add(wrapper, BorderLayout.NORTH);
            load("");
        }

        private JPanel formPanel() {
            JTextField[] fs = {idField, nameField, contactField, emailField, companyField};
            String[] labels = {"Client ID","Name","Contact","Email","Company"};
            JPanel form = new JPanel(new GridLayout(5, 2, 6, 6));
            for (int i = 0; i < labels.length; i++) {
                JLabel l = new JLabel(labels[i] + ":"); l.setFont(new Font("Arial",Font.PLAIN,13));
                styleField(fs[i]);
                form.add(l); form.add(fs[i]);
            }
            table.getSelectionModel().addListSelectionListener(e -> fillFields());
            return buildFormButtons(form,
                this::insertRecord, this::updateRecord,
                this::deleteRecord, this::clearFields);
        }

        private void fillFields() {
            int r = table.getSelectedRow(); if (r == -1) return;
            idField.setText(model.getValueAt(r,0).toString());
            nameField.setText(model.getValueAt(r,1).toString());
            contactField.setText(model.getValueAt(r,2).toString());
            emailField.setText(model.getValueAt(r,3).toString());
            companyField.setText(model.getValueAt(r,4).toString());
        }

        private void clearFields() {
            idField.setText(""); nameField.setText(""); contactField.setText("");
            emailField.setText(""); companyField.setText("");
        }

        public void load(String keyword) {
            model.setRowCount(0);
            String sql = "SELECT ClientID,Name,ContactNumber,Email,CompanyName " +
                         "FROM client WHERE Name LIKE ? OR Email LIKE ? OR CompanyName LIKE ?";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                String key = "%" + keyword + "%";
                ps.setString(1,key); ps.setString(2,key); ps.setString(3,key);
                ResultSet rs = ps.executeQuery();
                while (rs.next())
                    model.addRow(new Object[]{rs.getInt(1),rs.getString(2),
                        rs.getString(3),rs.getString(4),rs.getString(5)});
            } catch (SQLException e) { JOptionPane.showMessageDialog(this,e.getMessage()); }
        }

        private void insertRecord() {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO client(ClientID,Name,ContactNumber,Email,CompanyName) VALUES(?,?,?,?,?)")) {
                ps.setInt(1,Integer.parseInt(idField.getText().trim()));
                ps.setString(2,nameField.getText().trim());
                ps.setString(3,contactField.getText().trim());
                ps.setString(4,emailField.getText().trim());
                ps.setString(5,companyField.getText().trim());
                ps.executeUpdate(); clearFields(); load("");
            } catch (Exception e) { JOptionPane.showMessageDialog(this,e.getMessage()); }
        }

        private void updateRecord() {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                    "UPDATE client SET Name=?,ContactNumber=?,Email=?,CompanyName=? WHERE ClientID=?")) {
                ps.setString(1,nameField.getText().trim());
                ps.setString(2,contactField.getText().trim());
                ps.setString(3,emailField.getText().trim());
                ps.setString(4,companyField.getText().trim());
                ps.setInt(5,Integer.parseInt(idField.getText().trim()));
                ps.executeUpdate(); clearFields(); load("");
            } catch (Exception e) { JOptionPane.showMessageDialog(this,e.getMessage()); }
        }

        private void deleteRecord() {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM client WHERE ClientID=?")) {
                ps.setInt(1,Integer.parseInt(idField.getText().trim()));
                ps.executeUpdate(); clearFields(); load("");
            } catch (Exception e) { JOptionPane.showMessageDialog(this,e.getMessage()); }
        }
    }

    // ===================== VENUE PANEL =====================
    static class VenuePanel extends CrudPanel {
        JTextField idField       = new JTextField();
        JTextField nameField     = new JTextField();
        JTextField addressField  = new JTextField();
        JTextField capacityField = new JTextField();
        JTextField feeField      = new JTextField();

        VenuePanel() {
            super(new String[]{"VenueID","Name","Address","Capacity","RentalFee"});
            JPanel wrapper = new JPanel(new BorderLayout()); wrapper.setOpaque(false);
            wrapper.add(searchPanel(), BorderLayout.NORTH);
            wrapper.add(formPanel(),   BorderLayout.CENTER);
            add(wrapper, BorderLayout.NORTH);
            load("");
        }

        private JPanel formPanel() {
            JTextField[] fs = {idField,nameField,addressField,capacityField,feeField};
            String[] labels = {"Venue ID","Name","Address","Capacity","Rental Fee"};
            JPanel form = new JPanel(new GridLayout(5,2,6,6));
            for (int i = 0; i < labels.length; i++) {
                JLabel l = new JLabel(labels[i]+":"); l.setFont(new Font("Arial",Font.PLAIN,13));
                styleField(fs[i]); form.add(l); form.add(fs[i]);
            }
            table.getSelectionModel().addListSelectionListener(e -> fillFields());
            return buildFormButtons(form,
                this::insertRecord,this::updateRecord,this::deleteRecord,this::clearFields);
        }

        private void fillFields() {
            int r = table.getSelectedRow(); if(r==-1) return;
            idField.setText(model.getValueAt(r,0).toString());
            nameField.setText(model.getValueAt(r,1).toString());
            addressField.setText(model.getValueAt(r,2).toString());
            capacityField.setText(model.getValueAt(r,3).toString());
            feeField.setText(model.getValueAt(r,4).toString());
        }

        private void clearFields(){idField.setText("");nameField.setText("");addressField.setText("");capacityField.setText("");feeField.setText("");}

        public void load(String keyword) {
            model.setRowCount(0);
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                    "SELECT VenueID,Name,Address,Capacity,RentalFee FROM venue WHERE Name LIKE ? OR Address LIKE ?")) {
                String key = "%"+keyword+"%";
                ps.setString(1,key); ps.setString(2,key);
                ResultSet rs = ps.executeQuery();
                while(rs.next()) model.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getDouble(5)});
            } catch(SQLException e){JOptionPane.showMessageDialog(this,e.getMessage());}
        }

        private void insertRecord(){
            try(Connection con=DBConnection.getConnection();
                PreparedStatement ps=con.prepareStatement("INSERT INTO venue(VenueID,Name,Address,Capacity,RentalFee) VALUES(?,?,?,?,?)")){
                ps.setInt(1,Integer.parseInt(idField.getText().trim()));
                ps.setString(2,nameField.getText().trim());
                ps.setString(3,addressField.getText().trim());
                ps.setInt(4,Integer.parseInt(capacityField.getText().trim()));
                ps.setDouble(5,Double.parseDouble(feeField.getText().trim()));
                ps.executeUpdate();clearFields();load("");
            }catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage());}
        }

        private void updateRecord(){
            try(Connection con=DBConnection.getConnection();
                PreparedStatement ps=con.prepareStatement("UPDATE venue SET Name=?,Address=?,Capacity=?,RentalFee=? WHERE VenueID=?")){
                ps.setString(1,nameField.getText().trim());
                ps.setString(2,addressField.getText().trim());
                ps.setInt(3,Integer.parseInt(capacityField.getText().trim()));
                ps.setDouble(4,Double.parseDouble(feeField.getText().trim()));
                ps.setInt(5,Integer.parseInt(idField.getText().trim()));
                ps.executeUpdate();clearFields();load("");
            }catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage());}
        }

        private void deleteRecord(){
            try(Connection con=DBConnection.getConnection();
                PreparedStatement ps=con.prepareStatement("DELETE FROM venue WHERE VenueID=?")){
                ps.setInt(1,Integer.parseInt(idField.getText().trim()));
                ps.executeUpdate();clearFields();load("");
            }catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage());}
        }
    }

    // ===================== EVENT PANEL =====================
    static class EventPanel extends CrudPanel {
        JTextField idField=new JTextField(),titleField=new JTextField(),
            typeField=new JTextField(),dateField=new JTextField(),
            budgetField=new JTextField(),statusField=new JTextField(),
            clientField=new JTextField(),venueField=new JTextField(),orgField=new JTextField();

        EventPanel(){
            super(new String[]{"EventID","Title","Type","EventDate","Budget","Status","ClientID","VenueID","PrimaryOrganizerID"});
            JPanel wrapper=new JPanel(new BorderLayout());wrapper.setOpaque(false);
            wrapper.add(searchPanel(),BorderLayout.NORTH);
            wrapper.add(formPanel(),BorderLayout.CENTER);
            add(wrapper,BorderLayout.NORTH);load("");
        }

        private JPanel formPanel(){
            JTextField[] fs={idField,titleField,typeField,dateField,budgetField,statusField,clientField,venueField,orgField};
            String[] labels={"Event ID","Title","Type","Date (yyyy-mm-dd)","Budget","Status","Client ID","Venue ID","Organizer Staff ID"};
            JPanel form=new JPanel(new GridLayout(9,2,6,6));
            for(int i=0;i<labels.length;i++){
                JLabel l=new JLabel(labels[i]+":");l.setFont(new Font("Arial",Font.PLAIN,13));
                styleField(fs[i]);form.add(l);form.add(fs[i]);
            }
            table.getSelectionModel().addListSelectionListener(e->fillFields());
            return buildFormButtons(form,this::insertRecord,this::updateRecord,this::deleteRecord,this::clearFields);
        }

        private void fillFields(){
            int r=table.getSelectedRow();if(r==-1)return;
            idField.setText(model.getValueAt(r,0).toString());
            titleField.setText(model.getValueAt(r,1).toString());
            typeField.setText(model.getValueAt(r,2).toString());
            dateField.setText(model.getValueAt(r,3).toString());
            budgetField.setText(model.getValueAt(r,4).toString());
            statusField.setText(model.getValueAt(r,5).toString());
            clientField.setText(model.getValueAt(r,6).toString());
            venueField.setText(model.getValueAt(r,7).toString());
            orgField.setText(model.getValueAt(r,8).toString());
        }

        private void clearFields(){
            idField.setText("");titleField.setText("");typeField.setText("");
            dateField.setText("");budgetField.setText("");statusField.setText("");
            clientField.setText("");venueField.setText("");orgField.setText("");
        }

        public void load(String keyword){
            model.setRowCount(0);
            try(Connection con=DBConnection.getConnection();
                PreparedStatement ps=con.prepareStatement(
                "SELECT EventID,Title,Type,EventDate,Budget,Status,ClientID,VenueID,PrimaryOrganizerID FROM event WHERE Title LIKE ? OR Type LIKE ? OR Status LIKE ?")){
                String key="%"+keyword+"%";
                ps.setString(1,key);ps.setString(2,key);ps.setString(3,key);
                ResultSet rs=ps.executeQuery();
                while(rs.next()) model.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getDate(4),rs.getDouble(5),rs.getString(6),rs.getInt(7),rs.getInt(8),rs.getInt(9)});
            }catch(SQLException e){JOptionPane.showMessageDialog(this,e.getMessage());}
        }

        private void insertRecord(){
            try(Connection con=DBConnection.getConnection();
                PreparedStatement ps=con.prepareStatement(
                "INSERT INTO event(EventID,Title,Type,EventDate,Budget,Status,ClientID,VenueID,PrimaryOrganizerID) VALUES(?,?,?,?,?,?,?,?,?)")){
                ps.setInt(1,Integer.parseInt(idField.getText().trim()));
                ps.setString(2,titleField.getText().trim());
                ps.setString(3,typeField.getText().trim());
                ps.setDate(4,Date.valueOf(dateField.getText().trim()));
                ps.setDouble(5,Double.parseDouble(budgetField.getText().trim()));
                ps.setString(6,statusField.getText().trim());
                ps.setInt(7,Integer.parseInt(clientField.getText().trim()));
                ps.setInt(8,Integer.parseInt(venueField.getText().trim()));
                ps.setInt(9,Integer.parseInt(orgField.getText().trim()));
                ps.executeUpdate();clearFields();load("");
            }catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage());}
        }

        private void updateRecord(){
            try(Connection con=DBConnection.getConnection();
                PreparedStatement ps=con.prepareStatement(
                "UPDATE event SET Title=?,Type=?,EventDate=?,Budget=?,Status=?,ClientID=?,VenueID=?,PrimaryOrganizerID=? WHERE EventID=?")){
                ps.setString(1,titleField.getText().trim());
                ps.setString(2,typeField.getText().trim());
                ps.setDate(3,Date.valueOf(dateField.getText().trim()));
                ps.setDouble(4,Double.parseDouble(budgetField.getText().trim()));
                ps.setString(5,statusField.getText().trim());
                ps.setInt(6,Integer.parseInt(clientField.getText().trim()));
                ps.setInt(7,Integer.parseInt(venueField.getText().trim()));
                ps.setInt(8,Integer.parseInt(orgField.getText().trim()));
                ps.setInt(9,Integer.parseInt(idField.getText().trim()));
                ps.executeUpdate();clearFields();load("");
            }catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage());}
        }

        private void deleteRecord(){
            try(Connection con=DBConnection.getConnection();
                PreparedStatement ps=con.prepareStatement("DELETE FROM event WHERE EventID=?")){
                ps.setInt(1,Integer.parseInt(idField.getText().trim()));
                ps.executeUpdate();clearFields();load("");
            }catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage());}
        }
    }

    // ===================== GENERIC SIMPLE PANELS =====================
    static class StaffPanel       extends SimpleEntityPanel { StaffPanel()       { super("staff",        new String[]{"StaffID","Name","Role","ContactNumber","Salary"},            "StaffID");  } }
    static class VendorPanel      extends SimpleEntityPanel { VendorPanel()      { super("vendor",       new String[]{"VendorID","Name","ContactPerson","ContactNumber","ServiceType"},"VendorID"); } }
    static class ServicePanel     extends SimpleEntityPanel { ServicePanel()     { super("service",      new String[]{"ServiceID","Name","Description","UnitPrice","VendorID"},      "ServiceID"); } }
    static class EventServicePanel extends SimpleEntityPanel { EventServicePanel(){ super("event_service",new String[]{"EventID","ServiceID","Quantity","TotalCost"},                "EventID");  } }
    static class EventStaffPanel  extends SimpleEntityPanel { EventStaffPanel()  { super("event_staff",  new String[]{"EventID","StaffID","HoursWorked"},                            "EventID");  } }

    static class SimpleEntityPanel extends CrudPanel {
        private final String tableName;
        private final String[] columns;
        private final String idColumn;
        private final JTextField[] fields;

        SimpleEntityPanel(String tableName, String[] columns, String idColumn) {
            super(columns);
            this.tableName = tableName;
            this.columns   = columns;
            this.idColumn  = idColumn;
            this.fields    = new JTextField[columns.length];

            JPanel wrapper = new JPanel(new BorderLayout()); wrapper.setOpaque(false);
            wrapper.add(searchPanel(), BorderLayout.NORTH);
            wrapper.add(formPanel(),   BorderLayout.CENTER);
            add(wrapper, BorderLayout.NORTH);
            load("");
        }

        private JPanel formPanel() {
            JPanel form = new JPanel(new GridLayout(columns.length, 2, 6, 6));
            for (int i = 0; i < columns.length; i++) {
                fields[i] = new JTextField();
                JLabel l = new JLabel(columns[i] + ":"); l.setFont(new Font("Arial",Font.PLAIN,13));
                styleField(fields[i]);
                form.add(l); form.add(fields[i]);
            }
            table.getSelectionModel().addListSelectionListener(e -> fillFields());
            return buildFormButtons(form,
                this::insertRecord, this::updateRecord,
                this::deleteRecord, this::clearFields);
        }

        private void fillFields() {
            int r = table.getSelectedRow(); if(r==-1) return;
            for (int i = 0; i < columns.length; i++)
                fields[i].setText(String.valueOf(model.getValueAt(r,i)));
        }

        private void clearFields() { for (JTextField f : fields) f.setText(""); }

        public void load(String keyword) {
            model.setRowCount(0);
            StringBuilder sql = new StringBuilder("SELECT ")
                .append(String.join(",", columns)).append(" FROM ").append(tableName);
            if (!keyword.isEmpty()) {
                sql.append(" WHERE ");
                for (int i = 0; i < columns.length; i++) {
                    if (i > 0) sql.append(" OR ");
                    sql.append("CAST(").append(columns[i]).append(" AS CHAR) LIKE ?");
                }
            }
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql.toString())) {
                if (!keyword.isEmpty()) {
                    String key = "%" + keyword + "%";
                    for (int i = 1; i <= columns.length; i++) ps.setString(i, key);
                }
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Object[] row = new Object[columns.length];
                    for (int i = 0; i < columns.length; i++) row[i] = rs.getObject(i+1);
                    model.addRow(row);
                }
            } catch (SQLException e) { JOptionPane.showMessageDialog(this,e.getMessage()); }
        }

        private void insertRecord() {
            StringBuilder sql = new StringBuilder("INSERT INTO ")
                .append(tableName).append("(").append(String.join(",",columns)).append(") VALUES(");
            for (int i = 0; i < columns.length; i++) sql.append(i==0?"?": ",?");
            sql.append(")");
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql.toString())) {
                for (int i = 0; i < columns.length; i++) ps.setString(i+1,fields[i].getText().trim());
                ps.executeUpdate(); clearFields(); load("");
            } catch (Exception e) { JOptionPane.showMessageDialog(this,e.getMessage()); }
        }

        private void updateRecord() {
            StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
            for (int i = 1; i < columns.length; i++)
                sql.append(i==1?"":", ").append(columns[i]).append("=?");
            sql.append(" WHERE ").append(idColumn).append("=?");
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql.toString())) {
                for (int i = 1; i < columns.length; i++) ps.setString(i,fields[i].getText().trim());
                ps.setString(columns.length, fields[0].getText().trim());
                ps.executeUpdate(); clearFields(); load("");
            } catch (Exception e) { JOptionPane.showMessageDialog(this,e.getMessage()); }
        }

        private void deleteRecord() {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM "+tableName+" WHERE "+idColumn+"=?")) {
                ps.setString(1, fields[0].getText().trim());
                ps.executeUpdate(); clearFields(); load("");
            } catch (Exception e) { JOptionPane.showMessageDialog(this,e.getMessage()); }
        }
    }

    // ===================== MAIN FRAME =====================
    static class MainFrame extends JFrame {
        MainFrame() {
            setTitle("Event Management System");
            setSize(1100, 750);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            JTabbedPane tabs = new JTabbedPane();
            // Tab header colors via UIManager
            UIManager.put("TabbedPane.selected",            LAVENDER);
            UIManager.put("TabbedPane.background",          new Color(0xF0,0xF4,0xF8));
            UIManager.put("TabbedPane.foreground",          DARK_TEXT);
            UIManager.put("TabbedPane.selectedForeground",  WHITE);
            tabs.setFont(new Font("Arial", Font.BOLD, 13));
            tabs.setBackground(TEAL);

            tabs.addTab("Dashboard",      new DashboardPanel());
            tabs.addTab("Clients",        new ClientPanel());
            tabs.addTab("Venues",         new VenuePanel());
            tabs.addTab("Staff",          new StaffPanel());
            tabs.addTab("Vendors",        new VendorPanel());
            tabs.addTab("Services",       new ServicePanel());
            tabs.addTab("Events",         new EventPanel());
            tabs.addTab("Event Services", new EventServicePanel());
            tabs.addTab("Event Staff",    new EventStaffPanel());

            // Colored tab backgrounds
            Color[] tabColors = {TEAL, SKY_BLUE, LAVENDER, LIGHT_PINK,
                                 TEAL, SKY_BLUE, LAVENDER, LIGHT_PINK, TEAL};
            for (int i = 0; i < tabColors.length; i++)
                tabs.setBackgroundAt(i, tabColors[i]);

            getContentPane().setBackground(TEAL);
            add(tabs);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Global font & UI defaults
            UIManager.put("OptionPane.background",  Color.WHITE);
            UIManager.put("Panel.background",        new Color(0xF8,0xF9,0xFA));
            UIManager.put("Button.font",             new Font("Arial", Font.BOLD, 13));
            UIManager.put("Label.font",              new Font("Arial", Font.PLAIN, 13));
            UIManager.put("TextField.font",          new Font("Arial", Font.PLAIN, 13));
            new LoginFrame().setVisible(true);
        });
    }
}