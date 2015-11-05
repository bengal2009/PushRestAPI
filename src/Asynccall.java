/**
 * Created by blin on 2015/11/5.
 */
public class Asynccall implements CallBackTest{
    public static void main(String[] args) {
        Asynccall T1=new Asynccall();
        T1.test1();
    }
    public void test1() {
        AsyncTest T2=new AsyncTest("test1:");
        AsyncTest T3=new AsyncTest("test2:");
        T2.setQuestionListener(this);
        T3.setQuestionListener(this);
        T2.TestStart("Start Test:");
        T3.TestStart("Start Test2:");
    }
        @Override
     public void solve(String result) {
         System.out.println("小李告诉小王的答案是--->" + result);
     }

}
