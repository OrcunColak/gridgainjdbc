import java.sql.*;

public class User {



    public void createUSer(String name) throws SQLException {

        Sequence sequence = new Sequence();

        //We can have only one transaction per connection. So we need a new connection
        try (Connection connection = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1")) {

            int nextValue = 0;
            String SQL_UPDATE = "INSERT INTO MYUSER(ID,NAME) VALUES(?,?)";
            try (PreparedStatement updateStatement = connection.prepareStatement(SQL_UPDATE)) {

                nextValue = sequence.getNextValue("SEQ1");

                updateStatement.setLong(1, nextValue);
                updateStatement.setString(2, name);

                updateStatement.executeUpdate();
            } catch (Exception exception) {
                System.out.println("ID is : " + nextValue);
                throw exception;
            }


        }

    }
}

