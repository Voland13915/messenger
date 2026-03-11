package messenger.builder;

public class ConcreteMessageBuilder implements MessageBuilder {
    private final ComplexMessage message = new ComplexMessage();

    @Override public MessageBuilder setText(String text)      { message.setText(text);      return this; }
    @Override public MessageBuilder setSender(String sender)  { message.setSender(sender);  return this; }
    @Override public MessageBuilder setAttachment(String url) { message.setAttachment(url); return this; }
    @Override public MessageBuilder setQuote(String q)        { message.setQuote(q);        return this; }
    @Override public MessageBuilder setLocation(String loc)   { message.setLocation(loc);   return this; }
    @Override public ComplexMessage getResult()               { return message; }
}