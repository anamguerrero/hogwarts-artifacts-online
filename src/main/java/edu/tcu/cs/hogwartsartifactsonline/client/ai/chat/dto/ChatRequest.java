package edu.tcu.cs.hogwartsartifactsonline.client.ai.chat.dto;

import java.util.*;

public record ChatRequest(String model,
                          List<Message> messages) {
}