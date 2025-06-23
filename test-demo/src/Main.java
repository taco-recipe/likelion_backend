public class Main {
    int sum(int a, int b) {
        return a + b;
    }

    public static void main(String[] args) {
        System.out.println(new Main().sum(1, 2) == 3);
        System.out.println(new Main().sum(2, 2) == 4);
        System.out.println(new Main().sum(3, 2) == 5);

    }
}