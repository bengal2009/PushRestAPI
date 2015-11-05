/**
 * Created by blin on 2015/11/5.
 */
interface CallBackTest {
    /**
     * 这个是小李知道答案时要调用的函数告诉小王，也就是回调函数
     * @param result 是答案
     */
    public void solve(String result);
}

public class AsyncTest  {
    String Msg;
    CallBackTest call1;
    public AsyncTest(String s1){
        this.Msg=s1;
    }
    public void TestStart(final String MSG1) {

        try {
            //这里用一个线程就是异步，
            new Thread(new Runnable() {
                @Override
                public void run() {
                    /**
                     * 小王调用小李中的方法，在这里注册回调接口
                     * 这就相当于A类调用B的方法C
                     */
                    for (Integer i=0;i<10;i++)
                    {
                        System.out.println(MSG1+i);
                    }
                    call1.solve(Msg);
                }
            }).start();

        }catch (Exception E)
        {
            E.printStackTrace();
        }
        //小网问完问题挂掉电话就去干其他的事情了，诳街去了
      System.out.print("Testing Start");
    }
    public void setQuestionListener(CallBackTest questionListener) {
        this.call1= questionListener;
    }
}