package ua.nure.st.kpp.example.demo.entity;


import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents an entity Item from the warehouse.
 *
 * @author Hlova Stanislav
 */
public class Item {
    private String id;
    private String vendor;
    private String name;
    private String unit;
    private BigDecimal weight;
    private int amount;
    private int reserveRate;

    public Item() {
        super();
    }

    Item(Builder<?> builder) {
        this.id = builder.id;
        this.vendor = builder.vendor;
        this.name = builder.name;
        this.unit = builder.unit;
        this.weight = builder.weight;
        this.amount = builder.amount;
        this.reserveRate = builder.reserveRate;
    }

    public Item(String id, String vendor, String name, String unit, BigDecimal weight, int amount, int reserveRate) {
        this.id = id;
        this.vendor = vendor;
        this.name = name;
        this.unit = unit;
        this.weight = weight;
        this.amount = amount;
        this.reserveRate = reserveRate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getReserveRate() {
        return reserveRate;
    }

    public void setReserveRate(int reserveRate) {
        this.reserveRate = reserveRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (amount != item.amount) return false;
        if (reserveRate != item.reserveRate) return false;
        if (!Objects.equals(vendor, item.vendor)) return false;
        if (!Objects.equals(name, item.name)) return false;
        if (!Objects.equals(unit, item.unit)) return false;
        return Objects.equals(weight, item.weight);
    }

    @Override
    public int hashCode() {
        int result = vendor != null ? vendor.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        result = 31 * result + (weight != null ? weight.hashCode() : 0);
        result = 31 * result + amount;
        result = 31 * result + reserveRate;
        return result;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", vendor='" + vendor + '\'' +
                ", name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", weight=" + weight +
                ", amount=" + amount +
                ", reserveRate=" + reserveRate +
                '}';
    }

    public static class Builder<T extends Builder<T>> {
        private String id;
        private String vendor;
        private String name = "";
        private String unit = "";
        private BigDecimal weight = BigDecimal.ZERO;
        private int amount;
        private int reserveRate;

        public T id(String id) {
            this.id = id;
            return self();
        }

        public T vendor(String vendor) {
            this.vendor = vendor;
            return self();
        }

        public T name(String name) {
            this.name = name;
            return self();
        }

        public T unit(String unit) {
            this.unit = unit;
            return self();
        }

        public T weight(BigDecimal weight) {
            this.weight = weight;
            return self();
        }

        public T amount(int amount) {
            this.amount = amount;
            return self();
        }

        public T reserveRate(int reserveRate) {
            this.reserveRate = reserveRate;
            return self();
        }

        public Item build() {
            return new Item(this);
        }

        protected T self() {
            @SuppressWarnings("unchecked")
            T result = (T) this;
            return result;
        }
    }
}
