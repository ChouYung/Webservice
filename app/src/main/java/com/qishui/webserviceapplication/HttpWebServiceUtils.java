package com.qishui.webserviceapplication;

/**
 * @author Zhou
 * Created on 2018/5/11 9:07.
 * Email:qishuichixi@163.com
 * Desc: 请求webservice服务器
 */

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


public class HttpWebServiceUtils {

    // 访问的服务器是否由dotNet开发
    public static boolean isDotNet = true;
    // 连接响应标示
    public static final int SUCCESS_FLAG = 0;
    public static final int ERROR_FLAG = 1;

    // WebService  服务器地址
    // nameSpace   命名空间
    //  methodName  WebService的调用方法名
    private String endPoint;
    private String nameSpace;
    private String methodName;
    private static HttpWebServiceUtils httpManager;

    private HttpWebServiceUtils(String endPoint, String nameSpace) {
        this.endPoint = endPoint;
        this.nameSpace = nameSpace;
    }

    /**
     * 单例模式
     *
     * @return HttpManager对象
     */
    public static HttpWebServiceUtils getInstance(String endPoint, String nameSpace) {
        if (httpManager == null) {
            synchronized (HttpWebServiceUtils.class) {
                if (httpManager == null) {
                    httpManager = new HttpWebServiceUtils(endPoint, nameSpace);
                }
            }
        }
        return httpManager;
    }

    /**
     * 向webservice发送请求
     *
     * @param methodName
     * @param mapParams
     * @param reponseCallBack
     */
    public void call(final String methodName, SimpleArrayMap<String, String> mapParams, final CallBack reponseCallBack) {

        StringBuilder sb = new StringBuilder();
        sb.append("使用webservice方法:\n");
        sb.append("请求路径:" + endPoint + "\n");
        sb.append("命名空间:" + nameSpace + "\n");
        sb.append("请求方法:" + methodName + "\n");

        this.methodName = methodName;
        // 1.创建HttpTransportSE对象，传递WebService服务器地址
        final HttpTransportSE transport = new HttpTransportSE(endPoint);
        transport.debug = true;
        // 2.创建SoapObject对象用于传递请求参数
        final SoapObject request = new SoapObject(nameSpace, methodName);

        /*
        1.
         */
        // 2.1.添加参数也可以不传
        if (mapParams != null) {
            for (int index = 0; index < mapParams.size(); index++) {

                String key = mapParams.keyAt(index);
                String value = mapParams.get(key);
                sb.append("请求参数: key " + key + "  value " + value + "\n");
                request.addProperty(key, value);


            }
        }

        Log.e("QiShui request ", sb.toString());
        Log.e("QiShui request ", request.toString());

//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
//        stringBuilder.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n");
//        stringBuilder.append("<soap:Body>\n");
//        stringBuilder.append("<RoomResource_Query xmlns=\"http://tempuri.org/\">\n");
//        stringBuilder.append("<interface>\n");
//        stringBuilder.append("<item>\n");
//        stringBuilder.append("<flag>");
//        stringBuilder.append("roomtype");
//        stringBuilder.append("</flag>\n");
//        stringBuilder.append("</item>\n");
//        stringBuilder.append("</interface>\n");
//        stringBuilder.append("</RoomResource_Query>\n");
//        stringBuilder.append("</soap:Body>\n");
//        stringBuilder.append("</soap:Envelope>\n");
//        Log.i("d", "call: xml:"+stringBuilder.toString());
//
//        Log.e("QiShui", sb.toString());

        /*
        2.
         */
//        Info info1 = new Info();
//        info1.setProperty(0, "周");
//        info1.setProperty(1, "1906100008");
//        info1.setProperty(2, "周荣");
//        info1.setProperty(3, "340826199306244814");
//        info1.setProperty(4, "18550427543");

        SoapObject soapObject = new SoapObject(nameSpace, methodName);
        SoapObject soapObject2 = new SoapObject(nameSpace, methodName);
        PropertyInfo info = new PropertyInfo();
        info.setName("item");
        info.setValue(request);
        soapObject.addProperty(info);
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName("interface");
        propertyInfo.setValue(soapObject);
        soapObject2.addProperty(propertyInfo);
        Log.i("ss", "QiShui request2:" + soapObject2);

        // 3.实例化SoapSerializationEnvelope，传入WebService的SOAP协议的版本号
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = isDotNet; // 设置是否调用的是.Net开发的WebService
        envelope.setOutputSoapObject(soapObject);
        envelope.addMapping(nameSpace,methodName,soapObject.getClass());
        // 4.用于子线程与主线程通信的Handler，网络请求成功时会在子线程发送一个消息，然后在主线程上接收
        @SuppressLint("HandlerLeak") final Handler responseHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                // 根据消息的arg1值判断调用哪个接口
                if (msg.arg1 == SUCCESS_FLAG) {
                    reponseCallBack.onSuccess((SoapObject) msg.obj);
                } else {
                    reponseCallBack.onError((Exception) msg.obj);
                }
            }

        };
        // 5.提交一个子线程到线程池并在此线种内调用WebService
        new Thread() {
            @Override
            public void run() {

                SoapObject result = null;
                try {
                    // 解决EOFException
//                    System.setProperty("http.keepAlive", "false");
                    // 连接服务器
                    transport.call(nameSpace + methodName, envelope);
                    if (envelope.getResponse() != null) {
                        // 获取服务器响应返回的SoapObject
                        result = (SoapObject) envelope.bodyIn;
                    }
                } catch (IOException e) {
                    // 当call方法的第一个参数为null时会有一定的概念抛IO异常
                    // 因此需要需要捕捉此异常后用命名空间加方法名作为参数重新连接
                    e.printStackTrace();

                    try {
                        transport.call(nameSpace + methodName, envelope);
                        if (envelope.getResponse() != null) {
                            // 获取服务器响应返回的SoapObject
                            result = (SoapObject) envelope.bodyIn;
                        }
                    } catch (Exception e1) {
                        responseHandler.sendMessage(responseHandler.obtainMessage(0, ERROR_FLAG, 0, e1));
                    }
                } catch (XmlPullParserException e) {
                    responseHandler.sendMessage(responseHandler.obtainMessage(0, ERROR_FLAG, 0, e));
                } finally {
                    // 将获取的消息利用Handler发送到主线程
                    responseHandler.sendMessage(responseHandler.obtainMessage(0, SUCCESS_FLAG, 0, result));
                }

            }
        }.start();


    }

    public interface CallBack {
        void onSuccess(SoapObject result);

        void onError(Exception e);
    }


}

