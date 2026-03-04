import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

class Exercise34_01 extends JFrame {
  // Update these values for your database.
  private static final String DB_URL = "jdbc:mysql://localhost/javabook";
  private static final String DB_USER = "scott";
  private static final String DB_PASSWORD = "tiger";

  private Connection connection;

  private final JTextField tfId = new JTextField();
  private final JTextField tfLastName = new JTextField();
  private final JTextField tfFirstName = new JTextField();
  private final JTextField tfMi = new JTextField();
  private final JTextField tfAddress = new JTextField();
  private final JTextField tfCity = new JTextField();
  private final JTextField tfState = new JTextField();
  private final JTextField tfTelephone = new JTextField();
  private final JTextField tfEmail = new JTextField();

  Exercise34_01() {
    setTitle("Exercise34_01 - Staff Table");
    setSize(420, 360);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    initDatabase();
    initUi();
  }

  private void initDatabase() {
    try {
      connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
      createStaffTableIfNeeded();
    } catch (SQLException ex) {
      showError("Database connection failed", ex.getMessage());
    }
  }

  private void initUi() {
    JPanel form = new JPanel(new GridLayout(9, 2, 8, 8));

    form.add(new JLabel("ID"));
    form.add(tfId);
    form.add(new JLabel("Last Name"));
    form.add(tfLastName);
    form.add(new JLabel("First Name"));
    form.add(tfFirstName);
    form.add(new JLabel("MI"));
    form.add(tfMi);
    form.add(new JLabel("Address"));
    form.add(tfAddress);
    form.add(new JLabel("City"));
    form.add(tfCity);
    form.add(new JLabel("State"));
    form.add(tfState);
    form.add(new JLabel("Telephone"));
    form.add(tfTelephone);
    form.add(new JLabel("Email"));
    form.add(tfEmail);

    JButton btView = new JButton("View");
    JButton btInsert = new JButton("Insert");
    JButton btUpdate = new JButton("Update");

    btView.addActionListener(e -> viewStaffById());
    btInsert.addActionListener(e -> insertStaff());
    btUpdate.addActionListener(e -> updateStaff());

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
    buttonPanel.add(btView);
    buttonPanel.add(btInsert);
    buttonPanel.add(btUpdate);

    add(form, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  private void viewStaffById() {
    if (!ensureConnection()) {
      return;
    }

    String id = tfId.getText().trim();
    if (id.isEmpty()) {
      showInfo("View", "Enter an ID first.");
      return;
    }

    String sql = "SELECT id, lastName, firstName, mi, address, city, state, telephone, email "
        + "FROM Staff WHERE id = ?";

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
      ps.setString(1, id);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          tfId.setText(rs.getString("id"));
          tfLastName.setText(rs.getString("lastName"));
          tfFirstName.setText(rs.getString("firstName"));
          tfMi.setText(rs.getString("mi"));
          tfAddress.setText(rs.getString("address"));
          tfCity.setText(rs.getString("city"));
          tfState.setText(rs.getString("state"));
          tfTelephone.setText(rs.getString("telephone"));
          tfEmail.setText(rs.getString("email"));
        } else {
          clearNonIdFields();
          showInfo("View", "No record found for ID " + id);
        }
      }
    } catch (SQLException ex) {
      showError("View failed", ex.getMessage());
    }
  }

  private void insertStaff() {
    if (!ensureConnection()) {
      return;
    }

    String id = tfId.getText().trim();
    if (id.isEmpty()) {
      showInfo("Insert", "ID is required.");
      return;
    }

    String sql = "INSERT INTO Staff (id, lastName, firstName, mi, address, city, state, telephone, email) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
      bindAllFields(ps);
      ps.executeUpdate();
      showInfo("Insert", "Record inserted.");
    } catch (SQLException ex) {
      showError("Insert failed", ex.getMessage());
    }
  }

  private void updateStaff() {
    if (!ensureConnection()) {
      return;
    }

    String id = tfId.getText().trim();
    if (id.isEmpty()) {
      showInfo("Update", "ID is required.");
      return;
    }

    String sql = "UPDATE Staff SET lastName = ?, firstName = ?, mi = ?, address = ?, city = ?, "
        + "state = ?, telephone = ?, email = ? WHERE id = ?";

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
      ps.setString(1, tfLastName.getText().trim());
      ps.setString(2, tfFirstName.getText().trim());
      ps.setString(3, tfMi.getText().trim());
      ps.setString(4, tfAddress.getText().trim());
      ps.setString(5, tfCity.getText().trim());
      ps.setString(6, tfState.getText().trim());
      ps.setString(7, tfTelephone.getText().trim());
      ps.setString(8, tfEmail.getText().trim());
      ps.setString(9, id);

      int rows = ps.executeUpdate();
      if (rows == 0) {
        showInfo("Update", "No record found for ID " + id);
      } else {
        showInfo("Update", "Record updated.");
      }
    } catch (SQLException ex) {
      showError("Update failed", ex.getMessage());
    }
  }

  private void bindAllFields(PreparedStatement ps) throws SQLException {
    ps.setString(1, tfId.getText().trim());
    ps.setString(2, tfLastName.getText().trim());
    ps.setString(3, tfFirstName.getText().trim());
    ps.setString(4, tfMi.getText().trim());
    ps.setString(5, tfAddress.getText().trim());
    ps.setString(6, tfCity.getText().trim());
    ps.setString(7, tfState.getText().trim());
    ps.setString(8, tfTelephone.getText().trim());
    ps.setString(9, tfEmail.getText().trim());
  }

  private boolean ensureConnection() {
    if (connection != null) {
      return true;
    }

    initDatabase();
    return connection != null;
  }

  private void clearNonIdFields() {
    tfLastName.setText("");
    tfFirstName.setText("");
    tfMi.setText("");
    tfAddress.setText("");
    tfCity.setText("");
    tfState.setText("");
    tfTelephone.setText("");
    tfEmail.setText("");
  }

  private void createStaffTableIfNeeded() throws SQLException {
    String sql = "CREATE TABLE IF NOT EXISTS Staff ("
        + "id CHAR(9) NOT NULL, "
        + "lastName VARCHAR(15), "
        + "firstName VARCHAR(15), "
        + "mi CHAR(1), "
        + "address VARCHAR(20), "
        + "city VARCHAR(20), "
        + "state CHAR(2), "
        + "telephone CHAR(10), "
        + "email VARCHAR(40), "
        + "PRIMARY KEY (id)"
        + ")";

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
      ps.execute();
    }
  }

  private void showInfo(String title, String message) {
    JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
  }

  private void showError(String title, String message) {
    JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
  }

  private void closeConnection() {
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException ignored) {
      }
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      Exercise34_01 app = new Exercise34_01();
      app.setVisible(true);

      Runtime.getRuntime().addShutdownHook(new Thread(app::closeConnection));
    });
  }
}
