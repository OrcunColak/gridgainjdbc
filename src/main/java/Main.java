import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        Main main = new Main();
        main.start();

    }

    public void start() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> createUser("thread1"));
        executorService.submit(() -> createUser("thread2"));
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

    }

    private void createUser(String name) {

        for (int index = 0; index < 100; index++) {
            try {
                User user = new User();
                user.createUSer(name);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
