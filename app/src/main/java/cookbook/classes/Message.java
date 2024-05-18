package cookbook.classes;

import java.time.LocalDateTime;

public class Message {
    private int messageId;
    private int senderId;
    private int recipientId;
    private int recipeId;
    private String messageText;
    private LocalDateTime sentTime;

    // Constructor
    public Message(int messageId, int senderId, int recipientId, int recipeId, String messageText, LocalDateTime sentTime) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.recipeId = recipeId;
        this.messageText = messageText;
        this.sentTime = sentTime;
    }

    // Getters and setters
    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public LocalDateTime getSentTime() {
        return sentTime;
    }

    public void setSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
    }

    // Additional methods can be added as needed

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", senderId=" + senderId +
                ", recipientId=" + recipientId +
                ", recipeId=" + recipeId +
                ", messageText='" + messageText + '\'' +
                ", sentTime=" + sentTime +
                '}';
    }
}
