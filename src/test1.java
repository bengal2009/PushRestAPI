/**
 * Created by blin on 2015/11/3.
 */
public class test1 {
    public static void main(String[] args) {
       /* System.out.println("Hello World!");
        System.out.print("111");*/
        /**
         * new 一个小李
         */
        Li li = new Li();

        /**
         * new 一个小王
         */
        MyThread wang = new MyThread(li);

        /**
         * 小王问小李问题
         */
        wang.askQuestion("1 + 1 = ?");
    }

}
