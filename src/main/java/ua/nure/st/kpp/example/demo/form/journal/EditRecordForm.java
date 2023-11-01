package ua.nure.st.kpp.example.demo.form.journal;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class EditRecordForm {
    private int id;
    @NotEmpty(message = "The document number can't be empty")
    @Size(min = 2, max = 45, message = "The min and max size of the name is 2-45")
    private String documentNumber;
    @Positive(message = "The value must be positive integer")
    private int itemId;
    @Positive(message = "The value must be positive integer")
    private int companyId;
    @NotNull(message = "The price can't be empty")
    @PositiveOrZero(message = "The value must be positive or zero")
    //    @Pattern(regexp = "^\\d+\\.\\d+$", message = "The value must in format \"2.045\"") don't work
    private BigDecimal price;
    @Positive(message = "The value must be positive")
    private int amount;

    public EditRecordForm() {
    }

    public EditRecordForm(int id, String documentNumber, int itemId, int companyId, BigDecimal price, int amount) {
        this.id = id;
        this.documentNumber = documentNumber;
        this.itemId = itemId;
        this.companyId = companyId;
        this.price = price;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
