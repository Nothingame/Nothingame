import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import java.util.*;

public class Main {

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

    static void add_task(String name, String status) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Task task = new Task();
        task.setName(name);
        task.setStatus(status);

        entityManager.persist(task);

        entityManager.getTransaction().commit();
        entityManager.close();
        entityManagerFactory.close();
    }

    static void delete_task(int id) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Task task = entityManager.find(Task.class, id);

        entityManager.remove(task);

        entityManager.getTransaction().commit();
        entityManager.close();
        entityManagerFactory.close();
    }


    static void all_tasks() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        Query query = entityManager.createQuery("Select e from Task e");

        List<Task> resultList = (List<Task>)query.getResultList();

        for(Task task: resultList){
            System.out.printf("%1$s - %2$s - %3$s%n", task.getId(), task.getName(), task.getStatus());
        }
    }

    static void update_task(int id) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Task task = entityManager.find(Task.class, id);

        if (task.getStatus().equals(comm_desc(Commands.status0))){
            task.setStatus(comm_desc(Commands.status1));
        } else {
            task.setStatus(comm_desc(Commands.status0));
        }

        entityManager.getTransaction().commit();
        entityManager.close();
        entityManagerFactory.close();
    }


    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        boolean condition = true;

        while (condition){

            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            System.out.println(comm_desc(Commands.functions_desc));

            try {
                switch (Integer.parseInt(sc.nextLine())) {

                    case 1 -> {
                        System.out.println(comm_desc(Commands.request1));
                        add_task(sc.nextLine(), comm_desc(Commands.status0));
                    }

                    case 2 -> {
                        System.out.println(comm_desc(Commands.request2));
                        delete_task(Integer.parseInt(sc.nextLine()));
                    }

                    case 3 -> all_tasks();

                    case 4 -> {
                        System.out.println(comm_desc(Commands.request4));
                        update_task(Integer.parseInt(sc.nextLine()));
                    }

                    case 5 -> condition = false;

                    default -> System.out.println(comm_desc(Commands.error3));
                }
            } catch (Exception e) {
                System.out.println(comm_desc(Commands.error2));
            }

        }
    }
}
