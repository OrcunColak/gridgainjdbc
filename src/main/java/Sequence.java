import java.sql.*;

public class Sequence {

    private int readNextValue(Connection connection, String sequenceName) throws SQLException {

        String SQL_QUERY = "SELECT SEQ_NAME, SEQ_VALUE FROM MYSEQUENCE WHERE SEQ_NAME = ? FOR UPDATE ";
        try (PreparedStatement selectStatement = connection.prepareStatement(SQL_QUERY)) {

            int nextValue = 0;

            selectStatement.setString(1, sequenceName);
            try (ResultSet resultSet = selectStatement.executeQuery()) {

                if (resultSet.next()) { //Get next value from sequence
                    nextValue = 1 + resultSet.getInt(2);
                } else {
                    throw new SQLException(String.format("Sequence name %s does not exist", sequenceName));
                }
            }
            return nextValue;
        }
    }

    private void updateNextValue(Connection connection, String sequenceName, int nextValue) throws SQLException {

        String SQL_UPDATE = "UPDATE MYSEQUENCE T SET t.SEQ_VALUE = ? WHERE t.SEQ_NAME = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(SQL_UPDATE)) {
            updateStatement.setLong(1, nextValue);
            updateStatement.setString(2, sequenceName);
            updateStatement.executeUpdate();
            connection.commit();
        }
    }

    public int getNextValue(String sequenceName) throws SQLException {

        int RETRY = 3;
        for (int index = 0; index < RETRY; index++) {
            //We can have only one transaction per connection. So we need a new connection
            try (Connection connection = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1")) {

                int nextValue = 1;

                connection.setAutoCommit(false);

                try {

                    nextValue = readNextValue(connection, sequenceName);

                    updateNextValue(connection, sequenceName, nextValue);

                    connection.setAutoCommit(true);

                    return nextValue;

                } catch (Exception exception) {

                }
            }
        }
        throw new SQLException(String.format("Could not read from %s sequence", sequenceName));
    }
}


