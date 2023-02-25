package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {
    private HashMap<String, Order> orderHashMap = new HashMap<>(); // orderId -> order
    private HashMap<String, DeliveryPartner> deliveryPartnerHashMap = new HashMap<>(); // partnerId -> partner
    private HashMap<String, List<String>> partnerOrderHashMap = new HashMap<>(); // partnerId -> orderid list

    public void addOrder(Order order) {
        orderHashMap.put(order.getId(), order);
    }

    public void addPartner(String partnerId) {
        deliveryPartnerHashMap.put(partnerId, new DeliveryPartner(partnerId));
    }

    public void addOrderPartnerPair(String orderId, String partnerId){
        List<String> orders = partnerOrderHashMap.getOrDefault(partnerId, new ArrayList<>());
        orders.add(orderId);
        partnerOrderHashMap.put(partnerId, orders);
        // setting numbers of order assigned
        deliveryPartnerHashMap.get(partnerId).setNumberOfOrders(orders.size());
    }

    public Order getOrderById(String orderId){
        return orderHashMap.getOrDefault(orderId, null);
    }

    public DeliveryPartner getPartnerById(String partnerId){
        return deliveryPartnerHashMap.getOrDefault(partnerId, null);
    }

    public int getOrderCountByPartnerId(String partnerId){
        if(!deliveryPartnerHashMap.containsKey(partnerId)) return 0;
        return deliveryPartnerHashMap.get(partnerId).getNumberOfOrders();
    }

    public List<String> getOrdersByPartnerId(String partnerId){
        return partnerOrderHashMap.getOrDefault(partnerId, new ArrayList<>());
    }

    public List<String> getAllOrders(){
        return new ArrayList<>(orderHashMap.keySet());
    }

    public int getCountOfUnassignedOrders(){
        int totalOrders = orderHashMap.size();
        int assignedOrders = 0;
        for(List<String> orders: partnerOrderHashMap.values()){
            assignedOrders += orders.size();
        }

        return totalOrders-assignedOrders;
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId){
        int ordersLeft = 0;

        if(!partnerOrderHashMap.containsKey(partnerId)) return 0;

        int hr = Integer.parseInt(time.substring(0, 2));
        int min = Integer.parseInt(time.substring(3));
        int currTime = hr*60 + min;

        List<String> orders = partnerOrderHashMap.get(partnerId);
        for(String order: orders){
            int orderTime = orderHashMap.get(order).getDeliveryTime();
            ordersLeft += (orderTime>currTime)?1:0;
        }

        return ordersLeft;
    }

    public int getLastDeliveryTimeByPartnerId(String partnerId){
        int maxTime = 0;
        List<String> orders = partnerOrderHashMap.getOrDefault(partnerId, new ArrayList<>());
        for(String order: orders){
            int time = orderHashMap.get(order).getDeliveryTime();
            maxTime = Math.max(time, maxTime);
        }

        return maxTime;
    }

    public void deletePartnerById(String partnerId){
        deliveryPartnerHashMap.remove(partnerId);
        partnerOrderHashMap.remove(partnerId);
    }

    public void deleteOrderById(String orderId){
        for(String partnerId: partnerOrderHashMap.keySet()) {
            List<String> orders = partnerOrderHashMap.get(partnerId);
            if (orders.contains(orderId)) {
                orders.remove(orderId);
                partnerOrderHashMap.put(partnerId, orders);
                break;
            }
        }
        orderHashMap.remove(orderId);
    }


}
