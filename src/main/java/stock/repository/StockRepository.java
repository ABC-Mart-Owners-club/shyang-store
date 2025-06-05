package stock.repository;
import stock.domain.Stock;

import java.time.LocalDate;
import java.util.List;

public interface StockRepository {
    public Stock save(Stock stock);

    public int countStockByProduct(String productCode);

    // 특정 날짜 이전 입고된 재고 정보 조회
    public List<Stock> findStocksBefore(LocalDate localDate);

    // 오래된 재고 정보 부터 조회
    public List<Stock> findStockOrderByReceivedDateDesc(String productCode);
}
