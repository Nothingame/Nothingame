import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class Main {
    static final String db_url = "jdbc:mysql://localhost:3306/todolist";
    static final String user = "root";
    static final String pass = "Italy2016-!";
    static final String insert = "INSERT INTO `todolist`.`tasks` (`id`, `name`, `status`) VALUES ";
    static final String select = "SELECT * FROM tasks";
    static final String select_opt = "SELECT * FROM tasks WHERE `id` = ";
    static final String delete = "DELETE FROM `todolist`.`tasks` WHERE id = ";
    static final String update = "UPDATE `todolist`.`tasks` SET `id` = %1$s, `name` = '%2$s', `status` = '%3$s' " +
            "WHERE `id` = %1$s";

    enum Commands {
        status0,
        status1,
        error1,
        error2,
        error3,
        functions_desc,
        id,
        name,
        status,
        request1,
        request2,
        request4
    }

    static String comm_desc(Commands command) {
        return switch (command) {
            case status0 -> "Не выполнено";
            case status1 -> "Выполнено";
            case error1 -> "Неверный id";
            case error2 -> "Непредвиденная ошибка";
            case error3 -> "Неправильный код команды";
            case functions_desc -> """
                    Добавить - 1
                    Удалить - 2
                    Посмотреть полный список - 3
                    Изменить статус - 4
                    Остановить - 5""";
            case request1 -> "Введите задание";
            case request2 -> "Введите id задания, которое вы хотите удалить";
            case request4 -> "Введите id задания, статус которого вы хотите изменить";
            default -> "Нет такой команды";
        };
    }

    static Task create_task(String name, String status) {
        Task task = new Task();
        task.setName(name);
        task.setStatus(status);
        return task;
    }

    static void tasks_display(ResultSet resultSet) {
        System.out.println("id - name - status");

        try {
            while (resultSet.next()) {
                String id = resultSet.getString(String.valueOf(Commands.id));
                String name = resultSet.getString(String.valueOf(Commands.name));
                String status = resultSet.getString(String.valueOf(Commands.status));
                System.out.printf("%1$s - %2$s - %3$s%n", id, name, status);
            }
        } catch (Exception e) {
            System.out.println(comm_desc(Commands.error2));
        }
    }

    static Task change_status(ResultSet resultSet) {
        String name = "", status = "";
        try {
            if (resultSet.next()) {
                name = resultSet.getString(String.valueOf(Commands.name));
                status = resultSet.getString(String.valueOf(Commands.status));
            }
        } catch (Exception e) {
            System.out.println(comm_desc(Commands.error2));
        }
        Task task = create_task(name, status);
        task.change_status();
        return task;
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        boolean condition = true;

        while (condition) {

            System.out.println(comm_desc(Commands.functions_desc));

            try {

                Connection conn = DriverManager.getConnection(db_url, user, pass);
                Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                ResultSet res_set = stmt.executeQuery(select);


                switch (Integer.parseInt(sc.nextLine())) {

                    case 1:
                        try {
                            System.out.println(comm_desc(Commands.request1));
                            Task task = create_task(sc.nextLine(), comm_desc(Commands.status0));
                            res_set.last();
                            int next_id = res_set.getInt(String.valueOf(Commands.id)) + 1;
                            stmt.executeUpdate(insert + String.format("(%1$s, '%2$s', '%3$s')", next_id,
                                    task.getName(), task.getStatus()));
                        } catch (Exception e) {
                            System.out.println(comm_desc(Commands.error2));
                        }
                        break;

                    case 2:
                        // deletes the task by number
                        System.out.println(comm_desc(Commands.request2));
                        try {
                            stmt.executeUpdate(delete + Integer.parseInt(sc.nextLine()));
                        } catch (Exception e) {
                            System.out.println(comm_desc(Commands.error1));
                        }
                        break;

                    case 3:
                        // takes data from db and print it
                        tasks_display(res_set);
                        break;

                    case 4:
                        try {
                            System.out.println(comm_desc(Commands.request4));
                            int num = Integer.parseInt(sc.nextLine());
                            ResultSet rs_opt = stmt.executeQuery(select_opt + num);
                            Task task = change_status(rs_opt);
                            stmt.executeUpdate(String.format(update, num, task.getName(), task.getStatus()));
                        } catch (Exception e) {
                            System.out.println(comm_desc(Commands.error1));
                        }

                        break;

                    case 5:
                        condition = false;
                        break;

                    default:
                        System.out.println(comm_desc(Commands.error3));
                }
            } catch (Exception e) {
                System.out.println(comm_desc(Commands.error3));
            }
        }
    }
}
