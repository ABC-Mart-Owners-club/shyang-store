package service;

import dto.request.OrderPaymentInfo;
import dto.request.OrderProductInfo;
import dto.request.OrderRequestDto;
import order.domain.Order;
import order.domain.OrderItem;
import order.domain.OrderStatus;
import order.repository.OrderRepository;
import pay.domain.Payment;
import pay.domain.PaymentMethod;
import pay.repository.PayRepository;
import product.domain.Product;
import product.repository.ProductRepository;
import user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SalesService {

    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private UserRepository userRepository;
    private PayRepository payRepository;


    // 1. 주문
    // 트랜잭션 적용됐다고 가정
    public void order(String userName, OrderRequestDto orderRequestDto) {

        List<OrderProductInfo> orderProductInfos = orderRequestDto.getOrderProductInfoList();
        List<OrderPaymentInfo> orderPaymentInfos = orderRequestDto.getOrderPaymentInfoList();

        List<OrderItem> orderItems = new ArrayList<>();

        // 주문 내역 생성
        for (OrderProductInfo orderProductInfo : orderProductInfos) {

            String productCode = orderProductInfo.getProductCode();
            int quantity = orderProductInfo.getQuantity();
            int buyPrice = orderProductInfo.getBuyPrice();

            Product product = productRepository.findByCode(productCode); // 예외 구현체에서 처리했다고 가정
            product.deductStock(quantity); // 재고 차감
            productRepository.save(product); // 재고 업데이트

            OrderItem orderItem = new OrderItem(product, quantity, buyPrice);
            orderItems.add(orderItem);
        }

        // 주문 객체 생성
        String orderCode = UUID.randomUUID().toString(); // 임의로 생성한 주문 코드
        Order order = new Order(orderCode, orderItems, userName, OrderStatus.PAID, new ArrayList<>());

        // 결제 정보 추가
        for (OrderPaymentInfo orderPaymentInfo : orderPaymentInfos) {

            PaymentMethod.Type type = orderPaymentInfo.getType();
            PaymentMethod.CardType cardType = orderPaymentInfo.getCardType();
            int payAmount = orderPaymentInfo.getPayAmount();

            PaymentMethod paymentMethod = new PaymentMethod(type, cardType);// 결제 수단 생성
            Payment payment = new Payment(paymentMethod, order, payAmount);// 결제 객체 생성

            order.addPayment(payment); // 주문에 결제 정보 추가
        }

        orderRepository.save(order); // 주문 저장
        payRepository.saveAll(order.getPayments()); // 결제 정보 저장
    }


    // 2. 전체 취소 : 유저의 모든 주문 취소 (주문 상태가 PAID인 것만) - 이미 배송중인 상품은 취소 못함
    // 트랜잭션 적용됐다고 가정
    public void cancelOrders(String userName) {

        List<Order> ordersByUser = orderRepository.findByUserNameWhereOrderStatus(userName, OrderStatus.PAID); // 예외처리는 구현체에서 처리했다고 가정
        for (Order order : ordersByUser) {

            order.cancelOrder(); // 주문 취소

            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                Product product = orderItem.getProduct();
                product.refundStock(orderItem.getQuantity()); // 재고 복구
                productRepository.save(product); // 재고 업데이트
            }
        }

        orderRepository.saveAll(ordersByUser); // 주문 정보 업데이트
    }


    // 3. 부분 취소 : 특정 주문 취소 - 이미 배송중인 상품은 취소 못함
    // 트랜잭션 적용됐다고 가정
    public void cancelOrder(String orderCode) {

        Order order = orderRepository.findByOrderCode(orderCode); // 예외처리는 구현체에서 처리했다고 가정

        order.cancelOrder(); // 주문 취소

        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            product.refundStock(orderItem.getQuantity()); // 재고 복구
            productRepository.save(product); // 재고 업데이트
        }

        orderRepository.save(order); // 주문 정보 업데이트
    }
}
