package models;

public class Message {
	private int id;
	private String text;
	private String token;
	private String color;
	
	public Message(int id, String text, String token, String color) {
		this.setId(id);
		this.setText(text);
		this.setToken(token);
		this.setColor(color);
	}
	
	public Message(String text, String token, String color) {
		this.setText(text);
		this.setToken(token);
		this.setColor(color);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}

}
