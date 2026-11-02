package com.techie.microservices.accountservice.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class AccountConcurrencyTest {

    public static void main(String[] args) throws Exception {

        int numberOfRequests = 50;

        String customerId = "858d18ad-0392-4c10-90d4-9f26b927454a";

        HttpClient client = HttpClient.newHttpClient();

        ExecutorService executor =
                Executors.newFixedThreadPool(numberOfRequests);

        CountDownLatch ready = new CountDownLatch(numberOfRequests);
        CountDownLatch start = new CountDownLatch(1);

        List<Future<Integer>> results = new ArrayList<>();

        for (int i = 0; i < numberOfRequests; i++) {

            Future<Integer> result = executor.submit(() -> {

                ready.countDown();

                // Wait until all threads are ready
                start.await();

                String requestBody = """
                    {
                      "customerId": "%s",
                      "accountType": "SAVINGS",
                      "currency": "INR"
                    }
                    """.formatted(customerId);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(
                                "http://localhost:8081/api/v1/accounts"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                HttpResponse<String> response = client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

                System.out.println(
                        "HTTP " + response.statusCode()
                                + " | " + response.body()
                );

                return response.statusCode();
            });

            results.add(result);
        }

        // Wait until all 50 tasks are ready
        ready.await();

        // Release all tasks together
        start.countDown();

        int successCount = 0;
        int failureCount = 0;

        for (Future<Integer> result : results) {
            int status = result.get();

            if (status == 201) {
                successCount++;
            } else {
                failureCount++;
            }
        }

        executor.shutdown();

        System.out.println("Successful requests: " + successCount);
        System.out.println("Failed requests: " + failureCount);
    }
}
