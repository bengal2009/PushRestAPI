/**
 * Created by blin on 2015/11/5.
 */
public class Asynccall {
    static  AsyncTest T1,T2;
    public static void main(String[] args) {
       T1=new AsyncTest("Done");
        T2=new AsyncTest("2-Done");
        T1.TestStart("T1:");
        T2.TestStart("T2:");

    }

}
