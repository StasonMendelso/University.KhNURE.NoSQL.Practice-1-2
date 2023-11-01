package ua.nure.st.kpp.example.demo.form.company;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class AddCompanyForm {
    @NotEmpty(message = "The name can't be empty")
    @Size(min = 2, max = 200, message = "The min and max size of the name is 2-200")
    private String name;
    @NotEmpty(message = "The address can't be empty")
    @Size(min = 2, max = 100, message = "The min and max size of the address is 2-100")
    private String address;
    @NotEmpty(message = "The e-mail can't be empty")
    @Size(min = 2, max = 100, message = "The min and max size of the e-mail is 2-100")
    @Email(message = "Invalid e-mail")
    private String email;

    public AddCompanyForm() {
    }

    public AddCompanyForm(String name, String address, String email) {
        this.name = name;
        this.address = address;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
