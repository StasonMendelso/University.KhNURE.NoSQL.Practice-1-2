package ua.nure.st.kpp.example.demo.entity;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Journal {
    private List<Record> records;

    public Journal(List<Record> records) {
        this.records = records;
    }
    Journal(Builder<?> builder) {
        this.records = builder.records;
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public void addRecord(Record record) {
        records.add(record);
    }
    public void removeRecord(Record record){
        records.remove(record);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Journal journal = (Journal) o;

        return Objects.equals(records, journal.records);
    }

    @Override
    public int hashCode() {
        return records != null ? records.hashCode() : 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Journal{\n");
        for(Record record :records){
            builder.append("\t")
                    .append(record)
                    .append("\n\t")
                    .append(record.getItem())
                    .append("\n\t")
                    .append(record.getCompany())
                    .append("\n\t")
                    .append("------------------------------------------------------------------------------------------------------------------------------------------------------------------")
                    .append("\n");
        }
        builder.append("}");
        return builder.toString();
    }

    public static class Builder<T extends Builder<T>> {

        private List<Record> records = new LinkedList<>();

        public T records(List<Record> records) {
            this.records = records;
            return self();
        }
        public T addRecord(Record record) {
            this.records.add(record);
            return self();
        }

        public Journal build() {
            return new Journal(this);
        }

        protected T self() {
            @SuppressWarnings("unchecked")
            T result = (T) this;
            return result;
        }
    }

}
