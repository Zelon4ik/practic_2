package com.mysuperproject;

import com.mysuperproject.entity.User;
import com.mysuperproject.util.ConnectionPool;
import com.mysuperproject.util.PropertiesUtil;
import com.mysuperproject.view.AdminView;
import com.mysuperproject.view.LoginView;
import com.mysuperproject.view.StudentDashboardView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.flywaydb.core.Flyway;

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void init() throws Exception {
        super.init();
        initDatabase();
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Student Progress Tracking (MVVM)");
        showLoginView();
        primaryStage.show();
    }

    public static void showLoginView() {
        LoginView loginView = new LoginView();
        Scene scene = new Scene(loginView.getView(), 400, 300);
        scene.getStylesheets().add(App.class.getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    public static void showAdminView() {
        AdminView adminView = new AdminView();
        Scene scene = new Scene(adminView.getView(), 800, 600);
        scene.getStylesheets().add(App.class.getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    public static void showStudentDashboard(User user) {
        StudentDashboardView dashboardView = new StudentDashboardView(user);
        Scene scene = new Scene(dashboardView.getView(), 800, 600);
        scene.getStylesheets().add(App.class.getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    @Override
    public void stop() throws Exception {
        ConnectionPool.closePool();
        super.stop();
    }

    private void initDatabase() {
        System.out.println("Ініціалізація бази даних...");
        Flyway flyway =
                Flyway.configure()
                        .dataSource(
                                PropertiesUtil.get("db.url"),
                                PropertiesUtil.get("db.username"),
                                PropertiesUtil.get("db.password"))
                        .load();
        flyway.migrate();
        System.out.println("База даних готова до роботи!\n");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
