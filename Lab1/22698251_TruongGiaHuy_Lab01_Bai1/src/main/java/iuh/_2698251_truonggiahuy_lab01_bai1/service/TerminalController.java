package iuh._2698251_truonggiahuy_lab01_bai1.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class TerminalController implements CommandLineRunner {

    private final MessageProducer producer;
    private int count = 0;

    public TerminalController(MessageProducer producer) {
        this.producer = producer;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===============================");
            System.out.println("  RABBITMQ FLOW DEMO - IUH");
            System.out.println("  [1] Push Message");
            System.out.println("  [2] Exit");
            System.out.println("===============================");
            System.out.print("Chọn: ");

            String choice = scanner.nextLine();

            if ("1".equals(choice)) {
                count++;
                String status = (count % 3 == 0) ? "fail" : "success";
                String msg = "Order-" + count + "-" + status;

                producer.send(msg);
            } else if ("2".equals(choice)) {
                System.exit(0);
            }
        }
    }
}
