package cl.latam.contactabilitytest.features.contactability.ordersgenerator;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("intg")
public final class FormattedOrdersGenerator implements OrdersGenerator {
  /**
   * Format for order IDs:
   * `LA0450000SYNT` -> `LA0459999SYNT`
   */
  private static final String ORDER_ID_FORMAT = "LA045%04dSYNT";

  @Override
  public List<String> generateOrderIds(int maxAmount) {
    var random = new Random();

    return IntStream.range(1, random.nextInt(maxAmount) + 1)
        .map(i -> random.nextInt(1_0001) + 1)
        .boxed()
        .map(n -> String.format(ORDER_ID_FORMAT, n))
        .collect(Collectors.toList());
  }
}
