package ua.nure.st.kpp.example.demo.dao;

import ua.nure.st.kpp.example.demo.entity.Item;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Stanislav Hlova
 */
public interface AggregationQuery {

    Long findTotalItemsNumber();
    Map<String, Long> findTotalItemsNumberGroupByUnit();

    List<Item> findItemsByOneCompanyWithEmail(String email);

    BigDecimal findTotalPaidPriceForItemByVendor(String vendor);
    BigDecimal findTotalPaidPriceForItems();
}
