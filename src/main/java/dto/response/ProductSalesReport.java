package dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductSalesReport {
    private String productCode;
    private String productName;
    private int totalSalesAmount;

    public void addToTotalSales(int amount) {
        this.totalSalesAmount += amount;
    }
}
