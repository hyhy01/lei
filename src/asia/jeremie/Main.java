package asia.jeremie;


import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // 随机书生成器测试
//        int m = 1;
//        int[] a = new int[m];
//        for (int i = 0; i != 500; ++i) {
//            ++a[Core.random(m - 1)];
//        }
//        for (int i = 0; i < m; i++) {
//            System.out.print(" " + a[i]);
//        }

        Core core = new Core();
        System.out.println(core.initial(9, 9, 60, true));
        int x;
        int y;
        Scanner sc = new Scanner(System.in);
        x = sc.nextInt();
        while (x != -1) {
            y = sc.nextInt();

            Flag o = core.Hit(y, x);
            if (o == Flag.Boom) {
                System.out.println("Boom!!!!!!!");
                showMarix(core, true);
                break;
            }
            System.out.println(o);
            core.autoFlag();
            showMarix(core, false);
            if (core.wasSolubed()) {
                System.out.println("Solubed");
                break;
            }
            x = sc.nextInt();
        }

    }

    public static void showMarix(Core core, boolean showS) {
        for (int i = 0; i < core.data[0].length; i++) {
            System.out.print("\t" + i);
        }
        System.out.println();
        for (int i = 0; i < core.data.length; i++) {
            System.out.print(i);
            for (int j = 0; j < core.data[i].length; j++) {
                if (showS) {
                    System.out.print("\t" + core.data[i][j].toInt());
                } else {
                    System.out.print("\t" +
                            (
                                    core.mask[i][j] ?
                                            (
                                                    core.data[i][j].toInt() == 0 ?
                                                            " " : core.data[i][j].toInt()
                                            )
                                            :
                                            (
                                                    core.flag[i][j] ? "▛" : "█"
                                            )
                            )
                    );
                }
            }
            System.out.println();
        }
    }

}
