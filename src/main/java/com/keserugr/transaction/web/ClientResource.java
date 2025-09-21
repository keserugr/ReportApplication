package com.keserugr.transaction.web;

import com.keserugr.transaction.dto.client.GetClientResponse;
import com.keserugr.transaction.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Validated
public class ClientResource {

    private final ClientService clientService;

    @GetMapping("/{transactionId}")
    public ResponseEntity<GetClientResponse> getClient(
            @PathVariable @Length(min = 5, max = 64) String transactionId) {
        return ResponseEntity.ok(clientService.getClientByTransactionId(transactionId));
    }

    @GetMapping("/{transactionId}/email")
    public ResponseEntity<String> getClientEmail(@PathVariable String transactionId) {
        GetClientResponse res = clientService.getClientByTransactionId(transactionId);
        String email = clientService.safeClientEmail(res);
        return ResponseEntity.ok(email);
    }
}
