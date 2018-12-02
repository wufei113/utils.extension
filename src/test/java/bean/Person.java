package bean;

import java.io.Serializable;

/**
 * @author WuFei
 */
public class Person implements Serializable {

    private String name;
    private int age;
    private String sex;
    private Address address;

    public Person() {
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Person(String name, int age, String sex, Address address) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "name" + name + "\n\rage" + age + "\n\rsex" + sex;
    }

    public void eat() {
        System.out.println("人总是要吃饭的");
    }

    public void sleep() {
        System.out.println("睡觉");
    }
}
