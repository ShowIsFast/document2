package com.mymall.cas.service.impl;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
@Document(indexName = "stu")
public class Stu {
    @Id
    private Long stuId;

    private String name;
    @Field(store = true)
    private Integer age;

    public Long getStuId() {
        return stuId;
    }

    public void setStuId(Long stuId) {
        this.stuId = stuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Stu{" +
                "stuId=" + stuId +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
