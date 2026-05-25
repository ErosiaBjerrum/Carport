package app.entities;

public class User {
    private int userId;
    private String role;
    private String name;
    private String email;
    private String phone;
    private String address;
    private int zipCode;
    private String city;

    public User(int userId, String role, String name, String email, String phone, String address, int zipCode, String city) {
        this.userId = userId;
        this.role = role;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.zipCode = zipCode;
        this.city = city;
    }

    public int getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public int getZipCode() {
        return zipCode;
    }

    public String getCity() {
        return city;
    }
}