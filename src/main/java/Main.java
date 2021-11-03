import java.sql.*;

public class Main {

    public static void main(String[] args) {
        // Register JDBC driver.
        try {
            Class.forName("org.apache.ignite.IgniteJdbcThinDriver");

            // Open the JDBC connection.
            try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1")) {

                DatabaseMetaData dbmd = conn.getMetaData();
                int defaultTransactionIsolation = dbmd.getDefaultTransactionIsolation();
                System.out.println("defaultTransactionIsolation = " + defaultTransactionIsolation);
                if (dbmd.supportsTransactionIsolationLevel(Connection.TRANSACTION_REPEATABLE_READ)) {
                    System.out.println("Supports TRANSACTION_REPEATABLE_READ");
                }
                if (dbmd.supportsTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE)) {
                    System.out.println("Supports TRANSACTION_SERIALIZABLE");
                }

                conn.setAutoCommit(false);

                String SQL_QUERY = "SELECT SEQ_NAME, SEQ_VALUE FROM MYSEQUENCE WHERE SEQ_NAME = ? FOR UPDATE ";
                try (PreparedStatement selectStatement = conn.prepareStatement(SQL_QUERY,ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE)) {

                    selectStatement.setString(1, "SEQ1");
                    try (ResultSet resultSet = selectStatement.executeQuery()) {
                        int nextValue = 1;
                        if (resultSet.next()) { //Get next value from sequence
                            nextValue = 1 + resultSet.getInt(2);

                            String SQL_UPDATE = "UPDATE MYSEQUENCE T SET t.SEQ_VALUE = ? WHERE t.SEQ_NAME = ?";
                            try (PreparedStatement updateStatement = conn.prepareStatement(SQL_UPDATE)) {
                                updateStatement.setLong(1, nextValue);
                                updateStatement.setString(2, "SEQ1");
                                int rows = updateStatement.executeUpdate();
                                conn.commit();
                            }

                        }
                        System.out.println("Next Value is : " + nextValue);
                    }

                } finally {
                    conn.setAutoCommit(true);//auto commit to false
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
