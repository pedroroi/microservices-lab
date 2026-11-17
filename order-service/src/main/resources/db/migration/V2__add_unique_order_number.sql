ALTER TABLE `t_orders`
    ADD CONSTRAINT `uk_t_orders_order_number`
        UNIQUE (`order_number`);