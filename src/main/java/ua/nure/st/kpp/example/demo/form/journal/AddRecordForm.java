package ua.nure.st.kpp.example.demo.form.journal;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class AddRecordForm {
    @NotEmpty(message = "The document number can't be empty")
    @Size(min = 2, max = 45, message = "The min and max size of the name is 2-45")
    private String documentNumber;
    private String itemId;
    private String companyId;
    @NotNull(message = "The price can't be empty")
    @PositiveOrZero(message = "The value must be positive or zero")
    //    @Pattern(regexp = "^\\d+\\.\\d+$", message = "The value must in format \"2.045\"") don't work
    private BigDecimal price;
    @Positive(message = "The value must be positive")
    private int amount;

    public AddRecordForm() {
    }

    public AddRecordForm(String documentNumber, String itemId, String companyId, BigDecimal price, int amount) {
        this.documentNumber = documentNumber;
        this.itemId = itemId;
        this.companyId = companyId;
        this.price = price;
        this.amount = amount;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
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
