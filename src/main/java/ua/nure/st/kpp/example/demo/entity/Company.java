package ua.nure.st.kpp.example.demo.entity;

import java.util.Objects;

public class Company {
    private String id;
    private String name;
    private String email;
    private String address;

    public Company(String id, String name, String email, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
    }

    Company(Builder<?> builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.address = builder.address;
    }

    public Company() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Company company = (Company) o;

        if (id != company.id) return false;
        if (!Objects.equals(name, company.name)) return false;
        if (!Objects.equals(email, company.email)) return false;
        return Objects.equals(address, company.address);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    public static class Builder<T extends Builder<T>> {
        private String id;
        private String name = "";
        private String email = "";
        private String address = "";

        public T id(String id) {
            this.id = id;
            return self();
        }

        public T name(String name) {
            this.name = name;
            return self();
        }

        public T email(String email) {
            this.email = email;
            return self();
        }

        public T address(String address) {
            this.address = address;
            return self();
        }

        public Company build() {
            return new Company(this);
        }

        protected T self() {
            @SuppressWarnings("unchecked")
            T result = (T) this;
            return result;
        }
    }
}
