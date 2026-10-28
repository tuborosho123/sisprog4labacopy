import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileCopy {

    // копирует файл source в target (если target уже есть - перезаписывает)
    static void copyFile(String source, String target) {
        Path from = Paths.get(source);
        Path to = Paths.get(target);
        try {
            if (to.getParent() != null) {
                Files.createDirectories(to.getParent()); // создаем папку, если ее нет
            }
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Скопирован " + source + " -> " + target);
        } catch (IOException e) {
            System.out.println("Ошибка копирования " + source + ": " + e.getMessage());
        }
    }

    // создает текстовый файл для теста, чтобы было что копировать
    static void createTestFile(String name, int lines) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= lines; i++) {
            sb.append("Строка номер ").append(i).append("\n");
        }
        Files.writeString(Paths.get(name), sb.toString());
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String file1 = "file1.txt";
        String file2 = "file2.txt";

        createTestFile(file1, 2_000_000);
        createTestFile(file2, 2_000_000);

        // 1. Последовательное копирование
        System.out.println("=== Последовательное копирование ===");
        long start = System.currentTimeMillis();

        copyFile(file1, "sequential/file1_copy.txt");
        copyFile(file2, "sequential/file2_copy.txt");

        long sequentialTime = System.currentTimeMillis() - start;
        System.out.println("Время: " + sequentialTime + " мс\n");

        // 2. Параллельное копирование
        System.out.println("=== Параллельное копирование ===");
        Thread t1 = new Thread(() -> copyFile(file1, "parallel/file1_copy.txt"));
        Thread t2 = new Thread(() -> copyFile(file2, "parallel/file2_copy.txt"));

        start = System.currentTimeMillis();

        t1.start();
        t2.start();
        t1.join(); // ждем, пока оба потока закончат копирование
        t2.join();

        long parallelTime = System.currentTimeMillis() - start;
        System.out.println("Время: " + parallelTime + " мс\n");

        System.out.println("Итог: последовательно " + sequentialTime
                + " мс, параллельно " + parallelTime + " мс");
    }
}