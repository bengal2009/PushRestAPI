import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Map;


public class BaiduPush {
    public final static String mUrl = "http://api.tuisong.baidu.com/rest/3.0/";

    public final static String HTTP_METHOD_POST = "POST";
    public final static String HTTP_METHOD_GET = "GET";

    public static final String SEND_MSG_ERROR = "send_msg_error";

    private final static int HTTP_CONNECT_TIMEOUT = 20000;// ËøûÊé•Ë∂ÖÊó∂?∂Èó¥Ôº?0s
    private final static int HTTP_READ_TIMEOUT = 20000;// ËØªÊ??ØË??∂Êó∂?¥Ô?10s

    public String mHttpMethod;// ËØ∑Ê??πÂ?ÔºåPost or Get
    public String mSecretKey;// ÂÆâÂÖ®key

    /**
     * ?ÑÈ??ΩÊï∞
     *
     * @param http_mehtod
     *            ËØ∑Ê??πÂ?
     * @param secret_key
     *            ÂÆâÂÖ®key
     * @param api_key
     *            Â∫îÁî®key
     */
    public BaiduPush(String http_mehtod, String secret_key, String api_key) {
        mHttpMethod = http_mehtod;
        mSecretKey = secret_key;
        RestApi.mApiKey = api_key;
    }

    /**
     * urlÁºñÁ??πÂ?
     *
     * @param str
     *            ?áÂ?ÁºñÁ??πÂ?ÔºåÊú™?áÂ?ÈªòËÆ§‰∏∫utf-8
     * @return
     * @throws UnsupportedEncodingException
     */
    private String urlencode(String str) throws UnsupportedEncodingException {
        String rc = URLEncoder.encode(str, "utf-8");
        return rc.replace("*", "%2A");
    }

    /**
     * Â∞ÜÂ?Á¨¶‰∏≤ËΩ¨Êç¢Áß∞json?ºÂ?
     *
     * @param str
     * @return
     */
    public String jsonencode(String str) {
        String rc = str.replace("\\", "\\\\");
        rc = rc.replace("\"", "\\\"");
        rc = rc.replace("\'", "\\\'");
        return rc;
    }

    /**
     * ?ßË?PostËØ∑Ê??çÊï∞?ÆÂ??ÜÔ??†Â?‰πãÁ±ª
     *
     * @param data
     *            ËØ∑Ê??ÑÊï∞??	 * @return
     */
    public String PostHttpRequest(RestApi data) {

        StringBuilder sb = new StringBuilder();

        String channel = data.remove(RestApi._CHANNEL_ID);
        if (channel == null)
            channel =data._CHANNEL;
        try {
            data.put(RestApi._SECRETKEY,mSecretKey);
            data.put(RestApi._TIMESTAMP,
                    Long.toString(System.currentTimeMillis() / 1000));
            data.remove(RestApi._SIGN);
            data.put(RestApi._DEVICE_TYPE, RestApi.DEVICE_TYPE_ANDROID);
            sb.append(mHttpMethod);
            sb.append(mUrl);
            sb.append(channel);
            for (Map.Entry<String, String> i : data.entrySet()) {
                sb.append(i.getKey());
                sb.append('=');
                sb.append(i.getValue());
            }
            sb.append(mSecretKey);

            // System.out.println( "PRE: " + sb.toString() );
            // System.out.println( "UEC: " + URLEncoder.encode(sb.toString(),
            // "utf-8") );

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            // md.update( URLEncoder.encode(sb.toString(), "utf-8").getBytes()
            // );
            md.update(urlencode(sb.toString()).getBytes());
            byte[] md5 = md.digest();

            sb.setLength(0);
            for (byte b : md5)
                sb.append(String.format("%02x", b & 0xff));
            data.put(RestApi._SIGN, sb.toString());

            // System.out.println( "MD5: " + sb.toString());

            sb.setLength(0);
            for (Map.Entry<String, String> i : data.entrySet()) {
                sb.append(i.getKey());
                sb.append('=');
                // sb.append(i.getValue());
                // sb.append(URLEncoder.encode(i.getValue(), "utf-8"));
                sb.append(urlencode(i.getValue()));
                sb.append('&');
            }
            sb.setLength(sb.length() - 1);

            // System.out.println( "PST: " + sb.toString() );
            // System.out.println( mUrl + "?" + sb.toString() );

        } catch (Exception e) {
            e.printStackTrace();
            return SEND_MSG_ERROR;//Ê∂àÊÅØ?ëÈ?Â§±Ë¥•ÔºåË??ûÈ?ËØØÔ??ßË??çÂ?
        }

        StringBuilder response = new StringBuilder();
        HttpRequest(mUrl + channel, sb.toString(), response);
        System.out.println("URL:" + mUrl + channel);
        System.out.println("Parameters:" + sb.toString());
        System.out.println("Response String:" + response.toString());
        return response.toString();
    }

    /**
     * ?ßË?PostËØ∑Ê?
     *
     * @param url
     *            ?∫Á?url
     * @param query
     *            ?ê‰∫§?ÑÊï∞??	 * @param out
     *            ?çÂä°?®Â?Â§çÁ?Â≠óÁ¨¶‰∏?	 * @return
     */
    private int HttpRequest(String url, String query, StringBuilder out) {

        URL urlobj;
        HttpURLConnection connection = null;

        try {
            urlobj = new URL(url);

            connection = (HttpURLConnection) urlobj.openConnection();
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=utf-8");
            connection.setRequestProperty("User-Agent",
                    "BCCS_SDK/3.0");
            connection
                    .setRequestProperty("Content-Length", "" + query.length());

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(HTTP_CONNECT_TIMEOUT);
            connection.setReadTimeout(HTTP_READ_TIMEOUT);

            // Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(query.toString());
            wr.flush();
            wr.close();
            int responseCode = connection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {

                // Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;

                while ((line = rd.readLine()) != null) {
                    out.append(line);
                    out.append('\r');
                }
                rd.close();
                System.out.println("OUT:"+out);
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.append("HttpRequest Exception:" + e.getMessage());
            out.append(SEND_MSG_ERROR);//Ê∂àÊÅØ?ëÈ?Â§±Ë¥•ÔºåË??ûÈ?ËØØÔ??ßË??çÂ?
        }

        if (connection != null)
            connection.disconnect();

        return 0;
    }

    //
    // REST APIs
    //
    /**
     * ?•ËØ¢ËÆæÂ?‰ø°ÊÅØ?ÅÂ??®„??®Êà∑‰∏éÁôæÂ∫¶Channel?ÑÁ?ÂÆöÂÖ≥Á≥ª„?
     *
     * @param userid
     *            ?®Êà∑id
     * @param channelid
     * @return jsonÂΩ¢Â??ÑÊ??°Âô®?¢Â?
     */
    public String QueryBindlist(String userid, String channelid) {
        RestApi ra = new RestApi(RestApi.METHOD_QUERY_BIND_LIST);
        ra.put(RestApi._USER_ID, userid);
        // ra.put(RestApi._DEVICE_TYPE, RestApi.DEVICE_TYPE_ANDROID);
        ra.put(RestApi._CHANNEL_ID, channelid);
        // ra.put(RestApi._START, "0");
        // ra.put(RestApi._LIMIT, "10");
        return PostHttpRequest(ra);
    }

    /**
     * ?§Êñ≠ËÆæÂ??ÅÂ??®„??®Êà∑‰∏éChannel?ÑÁ?ÂÆöÂÖ≥Á≥ªÊòØ?¶Â??®„?
     *
     * @param userid
     *            ?®Êà∑id
     * @param channelid
     * @return
     */
    public String VerifyBind(String userid, String channelid) {
        RestApi ra = new RestApi(RestApi.METHOD_VERIFY_BIND);
        ra.put(RestApi._USER_ID, userid);
        // ra.put(RestApi._DEVICE_TYPE, RestApi.DEVICE_TYPE_ANDROID);
        ra.put(RestApi._CHANNEL_ID, channelid);
        return PostHttpRequest(ra);
    }

    /**
     * ÁªôÊ?ÂÆöÁî®?∑ËÆæÁΩÆÊ?Á≠?	 *
     * @param tag
     * @param userid
     * @return
     */
    public String SetTag(String tag, String userid) {
        RestApi ra = new RestApi(RestApi.METHOD_SET_TAG);
        ra.put(RestApi._USER_ID, userid);
        ra.put(RestApi._TAG, tag);
        return PostHttpRequest(ra);
    }

    /**
     * ?•ËØ¢Â∫îÁî®?ÑÊ??âÊ?Á≠?	 *
     * @return
     */
    public String FetchTag() {
        RestApi ra = new RestApi(RestApi.METHOD_FETCH_TAG);
        // ra.put(RestApi._NAME, "0");
        // ra.put(RestApi._START, "0");
        // ra.put(RestApi._LIMIT, "10");
        return PostHttpRequest(ra);
    }

    /**
     * ?†Èô§?áÂ??®Êà∑?ÑÊ?ÂÆöÊ?Á≠?	 *
     * @param tag
     * @param userid
     * @return
     */
    public String DeleteTag(String tag, String userid) {
        RestApi ra = new RestApi(RestApi.METHOD_DELETE_TAG);
        ra.put(RestApi._USER_ID, userid);
        ra.put(RestApi._TAG, tag);
        return PostHttpRequest(ra);
    }


    public String QueryUserTag() {
        RestApi ra = new RestApi(RestApi.METHOD_QUERY_USER_TAG);
        ra.put(RestApi._CHANNEL, "app/query_tags");
//		ra.put(RestApi._USER_ID, userid);
//        ra.put(RestApi.METHOD_QUERY_USER_TAG, "query_tags");
        return PostHttpRequest(ra);
    }
    /*
    Benny Test
     */
    public String PushtoAll(String message) {
        RestApi ra = new RestApi(RestApi.METHOD_QUERY_USER_TAG);
        ra.put(RestApi._CHANNEL, "push/all");
        ra.put(RestApi._MESSAGE_TYPE, RestApi.MESSAGE_TYPE_NOTIFY);
        ra.put(RestApi._MESSAGES, message);
        return PostHttpRequest(ra);
    }
    /**
     * ?πÊçÆchannel_id?•ËØ¢ËÆæÂ?Á±ªÂ?Ôº?1ÔºöÊ?ËßàÂô®ËÆæÂ?Ôº?2ÔºöpcËÆæÂ?Ôº?3ÔºöAndriodËÆæÂ?Ôº?4ÔºöiOSËÆæÂ?Ôº?5ÔºöwpËÆæÂ?Ôº?	 *
     * @param channelid
     * @return
     */
    public String QueryDeviceType(String channelid) {
        RestApi ra = new RestApi(RestApi.METHOD_QUERY_DEVICE_TYPE);
        ra.put(RestApi._CHANNEL_ID, channelid);
        return PostHttpRequest(ra);
    }

    // Message Push

    private final static String MSGKEY = "msgkey";

    /**
     * ÁªôÊ?ÂÆöÁî®?∑Êé®?ÅÊ???	 *
     * @param message
     * @param userid
     * @return
     */
    public String PushMessage(String message, String userid) {
        RestApi ra = new RestApi(RestApi.METHOD_PUSH_MESSAGE);
        ra.put(RestApi._MESSAGE_TYPE, RestApi.MESSAGE_TYPE_MESSAGE);

        ra.put(RestApi._MESSAGES, message);
        ra.put(RestApi._MESSAGE_KEYS, MSGKEY);
        // ra.put(RestApi._MESSAGE_EXPIRES, "86400");
        // ra.put(RestApi._CHANNEL_ID, "");
        ra.put(RestApi._PUSH_TYPE, RestApi.PUSH_TYPE_USER);
        ra.put(RestApi._DEVICE_TYPE, RestApi.DEVICE_TYPE_ANDROID);
        ra.put(RestApi._USER_ID, userid);
        return PostHttpRequest(ra);
    }

    /**
     * ÁªôÊ?ÂÆöÊ?Á≠æÁî®?∑Êé®?ÅÊ???	 *
     * @param message
     * @param tag
     * @return
     */
    public String PushTagMessage(String message, String tag) {
        RestApi ra = new RestApi(RestApi.METHOD_PUSH_MESSAGE);
        ra.put(RestApi._MESSAGE_TYPE, RestApi.MESSAGE_TYPE_MESSAGE);
        ra.put(RestApi._MESSAGES, message);
        ra.put(RestApi._MESSAGE_KEYS, MSGKEY);
        // ra.put(RestApi._MESSAGE_EXPIRES, "86400");
        ra.put(RestApi._PUSH_TYPE, RestApi.PUSH_TYPE_TAG);
        // ra.put(RestApi._DEVICE_TYPE, RestApi.DEVICE_TYPE_ANDROID);
        ra.put(RestApi._TAG, tag);
        return PostHttpRequest(ra);
    }

    /**
     * ÁªôÊ??âÁî®?∑Êé®?ÅÊ???	 *
     * @param message
     * @return
     */
    public String PushMessage(String message) {
        RestApi ra = new RestApi(RestApi.METHOD_PUSH_MESSAGE);
        ra.put(RestApi._MESSAGE_TYPE, RestApi.MESSAGE_TYPE_MESSAGE);
        ra.put(RestApi._MESSAGES, message);
        ra.put(RestApi._MESSAGE_KEYS, MSGKEY);
        // ra.put(RestApi._MESSAGE_EXPIRES, "86400");
        ra.put(RestApi._PUSH_TYPE, RestApi.PUSH_TYPE_ALL);
        // ra.put(RestApi._DEVICE_TYPE, RestApi.DEVICE_TYPE_ANDROID);
        return PostHttpRequest(ra);
    }

    /**
     * ÁªôÊ?ÂÆöÁî®?∑Êé®?ÅÈ???	 *
     * @param title
     * @param message
     * @param userid
     * @return
     */
    public String PushNotify(String title, String message, String userid) {
        RestApi ra = new RestApi(RestApi.METHOD_PUSH_MESSAGE);
        ra.put(RestApi._MESSAGE_TYPE, RestApi.MESSAGE_TYPE_NOTIFY);

        // notification_builder_id : default 0

        // String msg =
        // String.format("{'title':'%s','description':'%s','notification_basic_style':7}",
        // title, jsonencode(message));
        // String msg =
        // String.format("{'title':'%s','description':'%s','notification_builder_id':0,'notification_basic_style':5,'open_type':2}",
        // title, jsonencode(message));
        // String msg =
        // String.format("{'title':'%s','description':'%s','notification_builder_id':2,'notification_basic_style':7}",
        // title, jsonencode(message));

        String msg = String
                .format("{'title':'%s','description':'%s','notification_builder_id':0,'notification_basic_style':7,'open_type':2,'custom_content':{'test':'test'}}",
                        title, jsonencode(message));

        // String msg =
        // String.format("{\"title\":\"%s\",\"description\":\"%s\",\"notification_basic_style\":\"7\"}",
        // title, jsonencode(message));
        // String msg =
        // String.format("{\"title\":\"%s\",\"description\":\"%s\",\"notification_builder_id\":0,\"notification_basic_style\":1,\"open_type\":2}",
        // title, jsonencode(message));

        System.out.println(msg);

        ra.put(RestApi._MESSAGES, msg);

        ra.put(RestApi._MESSAGE_KEYS, MSGKEY);
        ra.put(RestApi._PUSH_TYPE, RestApi.PUSH_TYPE_USER);
        // ra.put(RestApi._PUSH_TYPE, RestApi.PUSH_TYPE_ALL);
        ra.put(RestApi._USER_ID, userid);
        return PostHttpRequest(ra);
    }

    /**
     * ÁªôÊ??âÁî®?∑Êé®?ÅÈ???	 *
     * @param title
     * @param message
     * @return
     */
    public String PushNotifyAll(String title, String message) {
        RestApi ra = new RestApi(RestApi.METHOD_PUSH_MESSAGE);
        ra.put(RestApi._MESSAGE_TYPE, RestApi.MESSAGE_TYPE_NOTIFY);

        String msg = String
                .format("{'title':'%s','description':'%s','notification_builder_id':0,'notification_basic_style':7,'open_type':2,'custom_content':{'test':'test'}}",
                        title, jsonencode(message));

        System.out.println(msg);

        ra.put(RestApi._MESSAGES, msg);

        ra.put(RestApi._MESSAGE_KEYS, MSGKEY);
        ra.put(RestApi._PUSH_TYPE, RestApi.PUSH_TYPE_ALL);
        return PostHttpRequest(ra);
    }

    /**
     * ?•ËØ¢?áÂ??®Êà∑Á¶ªÁ∫øÊ∂àÊÅØ??	 *
     * @param userid
     * @return
     */
    public String FetchMessage(String userid) {
        RestApi ra = new RestApi(RestApi.METHOD_FETCH_MESSAGE);
        ra.put(RestApi._USER_ID, userid);
        // ra.put(RestApi._START, "0");
        // ra.put(RestApi._LIMIT, "10");
        return PostHttpRequest(ra);
    }

    /**
     * ?•ËØ¢?áÂ??®Êà∑?ÑÁ¶ªÁ∫øÊ??ØÊï∞
     *
     * @param userid
     * @return
     */
    public String FetchMessageCount(String userid) {
        RestApi ra = new RestApi(RestApi.METHOD_FETCH_MSG_COUNT);
        ra.put(RestApi._USER_ID, userid);
        return PostHttpRequest(ra);
    }

    /**
     * ?†Èô§Á¶ªÁ∫øÊ∂àÊÅØ
     *
     * @param userid
     * @param msgids
     * @return
     */
    public String DeleteMessage(String userid, String msgids) {
        RestApi ra = new RestApi(RestApi.METHOD_DELETE_MESSAGE);
        ra.put(RestApi._USER_ID, userid);
        ra.put(RestApi._MESSAGE_IDS, msgids);
        return PostHttpRequest(ra);
    }

}