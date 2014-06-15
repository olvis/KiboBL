/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.com.kibo.bl.exceptions;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Olvinho
 */
public class BusinessException extends RuntimeException {

    private List<BusinessExceptionMessage> messages = new ArrayList<>();

    public BusinessException(String message) {
        super(message);
        messages.add(new BusinessExceptionMessage(message));
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        messages.add(new BusinessExceptionMessage(message));
    }

    public List<BusinessExceptionMessage> getMessages() {
        return messages;
    }

    public BusinessException(BusinessExceptionMessage message) {
        super(message.getMessage());
        messages.add(message);
    }
}
