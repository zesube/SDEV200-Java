import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

class Exercise35_01 extends JFrame {
  private static final int RECORD_COUNT = 1000;

  private Connection connection;

  private final JTextArea outputArea = new JTextArea(12, 52);
  private final JButton btConnect = new JButton("Connect to Database");
  private final JButton btNoBatch = new JButton("Insert Without Batch");
  private final JButton btWithBatch = new JButton("Insert With Batch");

  Exercise35_01() {
    setTitle("Exercise35_01 - Batch Update Performance");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    outputArea.setEditable(false);
    outputArea.setLineWrap(true);
    outputArea.setWrapStyleWord(true);

    btNoBatch.setEnabled(false);
    btWithBatch.setEnabled(false);

    btConnect.addActionListener(e -> openConnectionDialog());
    btNoBatch.addActionListener(e -> runWithoutBatch());
    btWithBatch.addActionListener(e -> runWithBatch());

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
    buttonPanel.add(btConnect);
    buttonPanel.add(btNoBatch);
    buttonPanel.add(btWithBatch);

    add(buttonPanel, BorderLayout.NORTH);
    add(new JScrollPane(outputArea), BorderLayout.CENTER);

    pack();
    setLocationRelativeTo(null);
  }

  private void openConnectionDialog() {
    DBConnectionDialog dialog = new DBConnectionDialog(this, this::connectToDatabase);
    dialog.setVisible(true);
  }

  private void connectToDatabase(String url, String user, String password) {
    try {
      closeConnection();
      connection = DriverManager.getConnection(url.trim(), user.trim(), password);
      createTempTableIfNeeded();

      btNoBatch.setEnabled(true);
      btWithBatch.setEnabled(true);
      log("Connected successfully to: " + url.trim());
    } catch (SQLException ex) {
      connection = null;
      btNoBatch.setEnabled(false);
      btWithBatch.setEnabled(false);
      showError("Connection failed", ex.getMessage());
    }
  }

  private void runWithoutBatch() {
    if (!ensureConnected()) {
      return;
    }

    long elapsedMs = insertWithoutBatch();
    if (elapsedMs >= 0) {
      log("Inserted " + RECORD_COUNT + " records without batch in " + elapsedMs + " ms.");
    }
  }

  private void runWithBatch() {
    if (!ensureConnected()) {
      return;
    }

    long elapsedMs = insertWithBatch();
    if (elapsedMs >= 0) {
      log("Inserted " + RECORD_COUNT + " records with batch in " + elapsedMs + " ms.");
    }
  }

  private long insertWithoutBatch() {
    final String insertSql = "INSERT INTO Temp(num1, num2, num3) VALUES (?, ?, ?)";

    try {
      clearTempTable();
      long start = System.nanoTime();

      try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
        for (int i = 0; i < RECORD_COUNT; i++) {
          ps.setDouble(1, Math.random());
          ps.setDouble(2, Math.random());
          ps.setDouble(3, Math.random());
          ps.executeUpdate();
        }
      }

      long end = System.nanoTime();
      return (end - start) / 1_000_000;
    } catch (SQLException ex) {
      showError("Insert without batch failed", ex.getMessage());
      return -1;
    }
  }

  private long insertWithBatch() {
    final String insertSql = "INSERT INTO Temp(num1, num2, num3) VALUES (?, ?, ?)";

    try {
      clearTempTable();
      boolean oldAutoCommit = connection.getAutoCommit();
      connection.setAutoCommit(false);
      long start = System.nanoTime();

      try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
        for (int i = 0; i < RECORD_COUNT; i++) {
          ps.setDouble(1, Math.random());
          ps.setDouble(2, Math.random());
          ps.setDouble(3, Math.random());
          ps.addBatch();
        }

        ps.executeBatch();
      }

      connection.commit();
      long end = System.nanoTime();
      connection.setAutoCommit(oldAutoCommit);
      return (end - start) / 1_000_000;
    } catch (SQLException ex) {
      try {
        if (connection != null) {
          connection.rollback();
          connection.setAutoCommit(true);
        }
      } catch (SQLException ignored) {
      }

      showError("Insert with batch failed", ex.getMessage());
      return -1;
    }
  }

  private void createTempTableIfNeeded() throws SQLException {
    String sql = "CREATE TABLE IF NOT EXISTS Temp(" +
        "num1 DOUBLE, " +
        "num2 DOUBLE, " +
        "num3 DOUBLE" +
        ")";

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
      ps.execute();
    }
  }

  private void clearTempTable() throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement("DELETE FROM Temp")) {
      ps.executeUpdate();
    }
  }

  private boolean ensureConnected() {
    if (connection == null) {
      showInfo("Not connected", "Click 'Connect to Database' first.");
      return false;
    }

    return true;
  }

  private void log(String message) {
    outputArea.append(message + "\n");
    outputArea.setCaretPosition(outputArea.getDocument().getLength());
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
      connection = null;
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      Exercise35_01 app = new Exercise35_01();
      app.setVisible(true);

      Runtime.getRuntime().addShutdownHook(new Thread(app::closeConnection));
    });
  }
}

interface ConnectionHandler {
  void onConnect(String url, String user, String password);
}

class DBConnectionPanel extends JPanel {
  private final JTextField tfUrl = new JTextField("jdbc:mysql://localhost/javabook");
  private final JTextField tfUser = new JTextField("scott");
  private final JPasswordField pfPassword = new JPasswordField("tiger");

  DBConnectionPanel() {
    setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    setLayout(new GridLayout(3, 2, 6, 6));

    add(new JLabel("JDBC URL"));
    add(tfUrl);
    add(new JLabel("Username"));
    add(tfUser);
    add(new JLabel("Password"));
    add(pfPassword);
  }

  String getUrl() {
    return tfUrl.getText();
  }

  String getUser() {
    return tfUser.getText();
  }

  String getPassword() {
    return new String(pfPassword.getPassword());
  }
}

class DBConnectionDialog extends JDialog {
  DBConnectionDialog(Frame owner, ConnectionHandler handler) {
    super(owner, "Connect to Database", true);

    DBConnectionPanel panel = new DBConnectionPanel();
    JButton btConnect = new JButton("Connect");
    JButton btCancel = new JButton("Cancel");

    btConnect.addActionListener(e -> {
      handler.onConnect(panel.getUrl(), panel.getUser(), panel.getPassword());
      dispose();
    });

    btCancel.addActionListener(e -> dispose());

    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttons.add(btConnect);
    buttons.add(btCancel);

    JPanel root = new JPanel();
    root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
    root.add(panel);
    root.add(buttons);

    add(root);
    pack();
    setLocationRelativeTo(owner);
  }
}
