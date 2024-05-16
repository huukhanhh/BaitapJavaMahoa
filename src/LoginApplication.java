import javax.swing.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class LoginApplication extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Connection conn;

    public LoginApplication() {
        initializeUI();
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/your_database", "root", "root");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        setTitle("Login Form");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(registerButton);

        add(panel);
        setVisible(true);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM btmahoa.users WHERE username=? AND password=?");
            statement.setString(1, username);
            statement.setString(2, hashPassword(password));
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                JOptionPane.showMessageDialog(this, "Welcome " + username);
                // Open new window or do whatever after successful login
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password");
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO btmahoa.users (username, password) VALUES (?, ?)");
            statement.setString(1, username);
            statement.setString(2, hashPassword(password));
            statement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registered successfully!");

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginApplication();
            }
        });
    }
}
