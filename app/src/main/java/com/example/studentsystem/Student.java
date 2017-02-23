package com.example.studentsystem;

/**
 * Created by ljh on 2016/11/18.
 */

//学生类
public class Student {
    private String id;  //学号
    private String name;    //姓名
    private String cls;     //专业班级
    private String addr;    //寝室
    private String phone;   //联系方式

    //构造方法
    public Student(String name, String id) {
        this.name = name;
        this.id = id;
    }
    public Student(String name, String id, String cls, String addr, String phone) {
        this.name = name;
        this.id = id;
        this.cls = cls;
        this.addr = addr;
        this.phone = phone;
    }
    public String getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getCls() {
        return this.cls;
    }
    public String getAddr() {
        return this.addr;
    }
    public String getPhone() {
        return this.phone;
    }
}
