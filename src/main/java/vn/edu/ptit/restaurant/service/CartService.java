package vn.edu.ptit.restaurant.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import vn.edu.ptit.restaurant.dto.CartItem;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@SessionScope
public class CartService {
    private Map<Long, CartItem> map = new HashMap<>();

    public void add(CartItem item) {
        CartItem existedItem = map.get(item.getMenuItemId());
        if (existedItem != null) {
            existedItem.setQuantity(item.getQuantity() + existedItem.getQuantity());
        } else {
            map.put(item.getMenuItemId(), item);
        }
    }

    public void remove(Long menuItemId) {
        map.remove(menuItemId);
    }

    public void update(Long menuItemId, int quantity) {
        CartItem item = map.get(menuItemId);
        if (item != null) {
            item.setQuantity(quantity);
            if (item.getQuantity() <= 0) {
                map.remove(menuItemId);
            }
        }
    }

    public void update(Long menuItemId, int quantity, String note) {
        CartItem item = map.get(menuItemId);
        if (item != null) {
            item.setQuantity(quantity);
            item.setNote(note);
            if (item.getQuantity() <= 0) {
                map.remove(menuItemId);
            }
        }
    }

    public void clear() {
        map.clear();
    }

    public Collection<CartItem> getItems() {
        return map.values();
    }

    public int getCount() {
        return map.values().stream().mapToInt(CartItem::getQuantity).sum();
    }

    public BigDecimal getAmount() {
        return map.values().stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
