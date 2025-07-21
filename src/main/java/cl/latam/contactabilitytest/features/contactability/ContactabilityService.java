package cl.latam.contactabilitytest.features.contactability;

import org.springframework.stereotype.Service;

import cl.latam.proto.contactability.v1.ContactabilityServiceGrpc;
import cl.latam.proto.contactability.v1.GetByOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
class ContactabilityService {
  private final ContactabilityServiceGrpc.ContactabilityServiceBlockingStub contactabilityService;

  public void callContactabilityApi(final String orderId) {
    try {
      contactabilityService.getContactabilityByOrder(
          GetByOrderRequest.newBuilder()
              .setOrderId(orderId)
              .build());
    } catch (Exception e) {
      log.error("Error while testing contactability for order {}: {}", orderId, e.getMessage());
      log.error("Stack trace: ", e);
    }
  }
}
