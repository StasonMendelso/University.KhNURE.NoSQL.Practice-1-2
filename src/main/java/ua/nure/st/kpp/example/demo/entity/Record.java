package ua.nure.st.kpp.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Record {
    private String id;
    private String documentNumber;
    private Item item;
    private Company company;
    private LocalDateTime date;
    private BigDecimal price;
    private int amount;

    public Record(String id, String documentNumber, Item item, Company company, LocalDateTime date, BigDecimal price, int amount) {
        this.id = id;
        this.documentNumber = documentNumber;
        this.item = item;
        this.company = company;
        this.date = date;
        this.price = price;
        this.amount = amount;
    }

    Record(Builder<?> builder) {
        this.id = builder.id;
        this.documentNumber = builder.documentNumber;
        this.item = builder.item;
        this.company = builder.company;
        this.date = builder.date;
        this.price = builder.price;
        this.amount = builder.amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;

        if (id != record.id) return false;
        if (amount != record.amount) return false;
        if (!Objects.equals(documentNumber, record.documentNumber))
            return false;
        if (!Objects.equals(item, record.item)) return false;
        if (!Objects.equals(company, record.company)) return false;
        if (!Objects.equals(date, record.date)) return false;
        return Objects.equals(price, record.price);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (documentNumber != null ? documentNumber.hashCode() : 0);
        result = 31 * result + (item != null ? item.hashCode() : 0);
        result = 31 * result + (company != null ? company.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + amount;
        return result;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", documentNumber='" + documentNumber + '\'' +
                ", item=" + item +
                ", company=" + company +
                ", date=" + date +
                ", price=" + price +
                ", amount=" + amount +
                '}';
    }

    public static class Builder<T extends Builder<T>> {
        private String id;
        private String documentNumber = "";
        private Item item;
        private Company company;
        private LocalDateTime date = LocalDateTime.MIN;
        private BigDecimal price = BigDecimal.ZERO;
        private int amount;

        public T id(String id) {
            this.id = id;
            return self();
        }

        public T item(Item item) {
            this.item = item;
            return self();
        }

        public T company(Company company) {
            this.company = company;
            return self();
        }

        public T documentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
            return self();
        }

        public T date(LocalDateTime localDateTime) {
            this.date = localDateTime;
            return self();
        }

        public T price(BigDecimal price) {
            this.price = price;
            return self();
        }

        public T amount(int amount) {
            this.amount = amount;
            return self();
        }

        public Record build() {
            return new Record(this);
        }

        protected T self() {
            @SuppressWarnings("unchecked")
            T result = (T) this;
            return result;
        }
    }
}
