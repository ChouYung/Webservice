package com.qishui.webserviceapplication;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

public class Info implements KvmSerializable {
    public String name;
    public String resno;
    public String fullname;
    public String ident;
    public String mobile;


    @Override
    public Object getProperty(int i) {
        switch (i) {
            case 0:
                return name;
            case 1:
                return resno;
            case 2:
                return fullname;
            case 3:
                return ident;
            case 4:
                return mobile;
            default:
                break;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 5;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch (i) {
            case 0:
                name = o.toString();
                break;
            case 1:
                resno = o.toString();
                break;
            case 2:
                fullname =  o.toString();
                break;
            case 3:
                ident = o.toString();
                break;
            case 4:
                mobile =  o.toString();
                break;
            default:
                break;
        }

    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch (i) {
            case 0:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "name";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "resno";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "fullname";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "ident";
                break;
            case 4:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "mobile";
                break;
            default:
                break;

        }
    }

    @Override
    public String toString() {
        return "Info{" +
                "name='" + name + '\'' +
                ", resno='" + resno + '\'' +
                ", fullname='" + fullname + '\'' +
                ", ident='" + ident + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
    }
}
