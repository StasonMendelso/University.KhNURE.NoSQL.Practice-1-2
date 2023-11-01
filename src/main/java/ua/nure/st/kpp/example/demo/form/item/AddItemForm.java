package ua.nure.st.kpp.example.demo.form.item;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class AddItemForm {
    @NotEmpty(message = "The vendor can't be empty")
    @Size(min = 2, max = 20, message = "The min and max size of the vendor is 2-20")
    private String vendor;
    @NotEmpty(message = "The name can't be empty")
    @Size(max = 100, message = "The max size of the vendor is 100")
    private String name;
    @NotEmpty(message = "The unit of measurement can't be empty")
    @Size(max = 45, message = "The max size of the vendor is 45")
    private String unit;
    @NotNull(message = "The field can't be empty")
    @Positive(message = "The weight can't be negative or zero")
//    @Pattern(regexp = "^\\d+\\.\\d+$", message = "The value must in format \"2.045\"")
    private BigDecimal weight;
    @Positive(message = "The reserve rate can't be negative or zero")
    private int reserveRate;

    public AddItemForm() {
    }

    public AddItemForm(String vendor, String name, String unit, BigDecimal weight, int reserveRate) {
        this.vendor = vendor;
        this.name = name;
        this.unit = unit;
        this.weight = weight;
        this.reserveRate = reserveRate;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public int getReserveRate() {
        return reserveRate;
    }

    public void setReserveRate(int reserveRate) {
        this.reserveRate = reserveRate;
    }

    @Override
    public String toString() {
        return "AddItemForm{" +
                "vendor='" + vendor + '\'' +
                ", name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", weight=" + weight +
                ", reserveRate=" + reserveRate +
                '}';
    }
}
