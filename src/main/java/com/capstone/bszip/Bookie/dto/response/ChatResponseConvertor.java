package com.capstone.bszip.Bookie.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ChatResponseConvertor implements AttributeConverter<ChatResponse, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ChatResponse chatResponse) {
        try{
            return mapper.writeValueAsString(chatResponse);
        }catch(JsonProcessingException e){
            throw new IllegalArgumentException("Failed to convert ChatResponse to JSON", e);
        }
    }

    @Override
    public ChatResponse convertToEntityAttribute(String chatResponse) {
        try {
            return mapper.readValue(chatResponse, ChatResponse.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to deserialize ChatResponse", e);
        }
    }
}
