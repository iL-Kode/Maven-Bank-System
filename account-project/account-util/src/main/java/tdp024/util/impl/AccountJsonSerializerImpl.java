package tdp024.util.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import tdp024.util.api.AccountLogger;
import tdp024.util.impl.AccountLoggerImpl;
import tdp024.util.api.AccountJsonSerializer;
import tdp024.util.impl.AccountJsonSerializerImpl;


public class AccountJsonSerializerImpl implements AccountJsonSerializer {

    private AccountLogger accountLogger = new AccountLoggerImpl();
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonMappingException e) {
            accountLogger.log(e);
            return null;
        } catch (IOException e) {
            accountLogger.log(e);
            return null;
        }

    }

    @Override
    public String toJson(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            accountLogger.log(e);
            return null;
        }
    }
}
