package cl.latam.contactabilitytest.features.contactability;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import cl.latam.contactabilitytest.features.contactability.ordersgenerator.OrdersGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactabilityConsoleRunner implements CommandLineRunner {
  private final ContactabilityService contactabilityService;
  private final OrdersGenerator ordersGenerator;

  @Value("${contactability.test.max-api-calls}")
  private int MAX_API_CALLS;

  @Override
  public void run(String... args) throws Exception {
    var orderIds = ordersGenerator.generateOrderIds(MAX_API_CALLS);

    log.info("There are {} orders to process", orderIds.size());

    var random = new Random();
    var atomicCounter = new AtomicInteger();

    // INFO: Simulate processing time similar to the use case
    orderIds.parallelStream().forEach(orderId -> {
      try {
        Thread.sleep(random.nextInt(5_000));
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        log.error("Thread interrupted while processing order ID: {}", orderId, e);
      }

      log.info("({}/{}) Testing contactability for order {}",
          atomicCounter.incrementAndGet(),
          orderIds.size(),
          orderId);

      contactabilityService.callContactabilityApi(orderId);
    });
  }
}
