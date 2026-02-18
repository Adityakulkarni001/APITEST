package com.example.hiring_task;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookService {

    private final RestTemplate restTemplate;

    public WebhookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void executeTask() {

        // STEP 1: Call generateWebhook API
        String generateUrl =
                "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "John Doe");
        requestBody.put("regNo", "REG12347");
        requestBody.put("email", "john@example.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request =
                new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(generateUrl, request, Map.class);

        // STEP 2: Read response
        String webhookUrl = (String) response.getBody().get("webhook");
        String accessToken = (String) response.getBody().get("accessToken");

        // STEP 3: Decide odd/even
        String regNo = "REG12347";
        int lastTwoDigits =
                Integer.parseInt(regNo.substring(regNo.length() - 2));

        boolean isOdd = lastTwoDigits % 2 != 0;

        // STEP 4: SQL Query (replace with actual question query)
        String finalQuery;

        if (isOdd) {
            finalQuery =
            		"SELECT department, emp_id, emp_name, salary FROM employee_payments ep WHERE salary = ( SELECT MAX(salary) FROM employee_payments WHERE department = ep.department AND EXTRACT(DAY FROM payment_date) <> 1 ) AND EXTRACT(DAY FROM payment_date) <> 1;";
        } else {
            finalQuery =
            		"SELECT department, AVG(age) AS avg_age, STRING_AGG(emp_name, ', ' ORDER BY emp_name) AS employee_names FROM ( SELECT department, emp_name, age, ROW_NUMBER() OVER (PARTITION BY department ORDER BY emp_name) AS rn FROM employees WHERE salary > 70000 ) t WHERE rn <= 10 GROUP BY department;";
        }

        // STEP 5: Submit SQL query to webhook
        HttpHeaders submitHeaders = new HttpHeaders();
        submitHeaders.setContentType(MediaType.APPLICATION_JSON);

        // JWT TOKEN IS JUST PASSED LIKE THIS
        submitHeaders.set("Authorization", accessToken);

        Map<String, String> submitBody = new HashMap<>();
        submitBody.put("finalQuery", finalQuery);

        HttpEntity<Map<String, String>> submitRequest =
                new HttpEntity<>(submitBody, submitHeaders);

        restTemplate.postForEntity(webhookUrl, submitRequest, String.class);

        System.out.println("Task completed successfully");
    }
}
