package cl.latam.contactabilitytest.features.contactability;

import java.io.IOException;

import javax.net.ssl.SSLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;

import cl.latam.proto.contactability.v1.ContactabilityServiceGrpc;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

@Configuration
public class ContactabilityConfig {
  @Value("${contactability.service.host}")
  private String CONTACTABILITY_SERVICE_HOST;

  @Value("${contactability.service.port}")
  private int CONTACTABILITY_SERVICE_PORT;

  @Value("${contactability.service.audience}")
  private String CONTACTABILITY_SERVICE_AUDIENCE;

  @Bean
  ContactabilityServiceGrpc.ContactabilityServiceBlockingStub stub() throws SSLException {
    var channel = NettyChannelBuilder.forAddress(CONTACTABILITY_SERVICE_HOST, CONTACTABILITY_SERVICE_PORT)
        .sslContext(GrpcSslContexts.forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build())
        .build();

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        channel.shutdown();

        try {
          channel.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
          System.err.println("Channel shutdown interrupted: " + e.getMessage());
          Thread.currentThread().interrupt();
        }

        channel.shutdownNow();
      }
    });

    return ContactabilityServiceGrpc.newBlockingStub(channel)
        .withInterceptors(globalAuthorizationInterceptor());
  }

  private ClientInterceptor globalAuthorizationInterceptor() {
    return new ClientInterceptor() {
      private IdTokenCredentials idTokenCredentials;

      private String getIdentityToken() throws IOException {
        if (idTokenCredentials != null) {
          this.idTokenCredentials.refreshIfExpired();
          return idTokenCredentials.getAccessToken().getTokenValue();
        }

        if (!(GoogleCredentials.getApplicationDefault() instanceof IdTokenProvider credentials))
          throw new IllegalStateException("GoogleCredentials is not an instance of GoogleCredentials");

        this.idTokenCredentials = IdTokenCredentials.newBuilder()
            .setIdTokenProvider(credentials)
            .setTargetAudience(CONTACTABILITY_SERVICE_AUDIENCE)
            .build();

        this.idTokenCredentials.refreshIfExpired();
        return this.idTokenCredentials.getAccessToken().getTokenValue();
      }

      @Override
      public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
          MethodDescriptor<ReqT, RespT> method,
          CallOptions callOptions,
          Channel next) {

        return new ClientCall<ReqT, RespT>() {
          private final ClientCall<ReqT, RespT> delegate = next.newCall(method, callOptions);

          @Override
          public void start(Listener<RespT> responseListener, Metadata headers) {
            try {
              Metadata.Key<String> AUTHORIZATION_HEADER = Metadata.Key.of("authorization",
                  Metadata.ASCII_STRING_MARSHALLER);
              headers.put(AUTHORIZATION_HEADER, String.format("Bearer %s", getIdentityToken()));
            } catch (IOException e) {
              System.err.println("Failed to get identity token: " + e.getMessage());
              throw new RuntimeException("Failed to get identity token", e);
            }

            delegate.start(responseListener, headers);
          }

          @Override
          public void request(int numMessages) {
            delegate.request(numMessages);
          }

          @Override
          public void cancel(String message, Throwable cause) {
            delegate.cancel(message, cause);
          }

          @Override
          public void halfClose() {
            delegate.halfClose();
          }

          @Override
          public void sendMessage(ReqT message) {
            delegate.sendMessage(message);
          }
        };
      }
    };
  }
}
