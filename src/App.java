import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Select number K: ");
        Scanner scan = new Scanner(System.in);
        int k = scan.nextInt();
        scan.close();
        System.out.println(k);
    }
}
