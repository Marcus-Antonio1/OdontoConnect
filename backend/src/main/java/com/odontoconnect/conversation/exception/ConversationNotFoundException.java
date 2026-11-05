package com.odontoconnect.conversation.exception;

import com.odontoconnect.conversation.exception.ConversationNotFoundException;

public class ConversationNotFoundException extends RuntimeException {

    public ConversationNotFoundException(String message) {
        super(message);
    }
}