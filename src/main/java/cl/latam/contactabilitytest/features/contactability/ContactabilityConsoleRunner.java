package cl.latam.contactabilitytest.features.contactability;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ContactabilityConsoleRunner implements CommandLineRunner {
  private final ContactabilityService contactabilityService;

  @Value("${contactability.test.max-api-calls}")
  private int MAX_API_CALLS;

  @Override
  public void run(String... args) throws Exception {
    var random = new Random();

    var orderIds = IntStream.range(1, random.nextInt(MAX_API_CALLS))
        .map(i -> random.nextInt(1_001) + 1)
        .boxed()
        .map(n -> String.format("LA%010d", n))
        .collect(Collectors.toList());

    log.info("There are {} orders to process", orderIds.size());

    var atomicCounter = new AtomicInteger();

    // INFO: Simulate processing time similar to the use case
    orderIds.parallelStream().forEach(orderId -> {
      try {
        Thread.sleep(random.nextInt(30_000));
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
