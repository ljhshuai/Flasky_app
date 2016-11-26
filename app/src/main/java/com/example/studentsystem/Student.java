package com.example.studentsystem;

/**
 * Created by ljh on 2016/11/18.
 */

public class Student {
    private String id;
    private String name;
    private String sex;
    private int age;
    private float score;

    public Student(String name, String id) {
        this.name = name;
        this.id = id;
    }
    public Student(String name, String id, String sex, int age, float score) {
        this.name = name;
        this.id = id;
        this.sex = sex;
        this.age = age;
        this.score = score;
    }
    public String getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getSex() {
        return this.sex;
    }
    public int getAge() {
        return this.age;
    }
    public float getScore() {
        return this.score;
    }
}
