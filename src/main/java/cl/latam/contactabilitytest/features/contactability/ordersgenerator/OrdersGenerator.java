package cl.latam.contactabilitytest.features.contactability.ordersgenerator;

import java.util.List;

public interface OrdersGenerator {
  /**
   * Generates a list of order IDs maxed on the specified maximum amount.
   *
   * @param maxAmount the maximum number of order IDs to generate (exclusive)
   * @return a list of formatted order IDs
   */
  List<String> generateOrderIds(int maxAmount);
}
