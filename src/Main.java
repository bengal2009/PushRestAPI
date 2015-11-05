public class Main {
    public static final String APP_KEY ="mzaavB1EGEG8qGqeE5FhrLFy";
    public static final String SECRIT_KEY = "97177c7744320dd55571457fc122a418";
    private static  BaiduPush mBaiduPushServer=null;

    public static void main(String[] args) {
    String result;
        if (mBaiduPushServer == null)
            mBaiduPushServer = new BaiduPush(BaiduPush.HTTP_METHOD_POST,
                   SECRIT_KEY, APP_KEY);
        String msg = String
                .format("{\"title\":\"%s\",\"description\":\"%s\"}",
                        "title", mBaiduPushServer.jsonencode("测试111!"));
        System.out.println("MSG:"+msg);
        result = mBaiduPushServer.PushtoAll(msg);


        System.out.println("result");
    }
}
