package com.bank.account.transactions.domain.model.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class CustomOffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {
    
    @Override
    public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String timestamp = p.getText();
        
        // Handle excess nanosecond digits by truncating to 9 digits
        if (timestamp.contains(".")) {
            String[] parts = timestamp.split("\\.");
            if (parts.length > 1 && parts[1].length() > 9) {
                timestamp = parts[0] + "." + parts[1].substring(0, 9);
            }
        }
        
        return OffsetDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}