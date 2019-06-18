package com.qishui.webserviceapplication;

import android.support.v4.util.SimpleArrayMap;
import android.util.Log;

import org.ksoap2.serialization.SoapObject;

/**
 * @author Zhou
 * Created on 2018/5/11 9:28.
 * Email:qishuichixi@163.com
 * Desc: 使用webService方法
 */

public class HttpWebServiceDemo {

    public static void getData() {

        SimpleArrayMap<String, String> mapParams = new SimpleArrayMap<>();
//        mapParams.put("name", "周");
//        mapParams.put("resno", "1906110005");
//        mapParams.put("fullname", "周荣");
        mapParams.put("ident", "340826199306244814");
//        mapParams.put("mobile", "18550427543");



        HttpWebServiceUtils.getInstance("http://60.190.224.119:5039/XRHotelSelf-Gzl-1.4/?wsdl",
                "http://tempuri.org/")
                .call("GetArrivingReservation",
                        mapParams,
                        new HttpWebServiceUtils.CallBack() {
                            @Override
                            public void onSuccess(SoapObject result) {
                                Log.e("QiShui2", "" + result);
                            }

                            @Override
                            public void onError(Exception e) {
                                e.printStackTrace();

                                Log.e("QiShui", "" + e.getMessage());
                            }
                        });

    }

}
