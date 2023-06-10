package Entities;

import Anotations.Column;
import Anotations.Entity;
import Anotations.Id;

import java.lang.annotation.Target;
import java.time.LocalDate;

@Entity(name = "testUsers")
public class User {
    @Id
    @Column(name = "id")
    private long id;
    @Column(name = "user_name")
    private String username;
    @Column(name = "age")
    private int age;
    @Column(name = "registration_date")
    private LocalDate registration;

    public User(String username, int age, LocalDate registration) {
        this.username = username;
        this.age = age;
        this.registration = registration;
    }
 public User() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public LocalDate getRegistration() {
        return registration;
    }

    public void setRegistration(LocalDate registration) {
        this.registration = registration;
    }
}
