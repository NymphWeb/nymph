package test;


public class Person {
	
	private String name;
	
	private int age;
	
	private Object test;
	
	public Object getTest() {
		return test;
	}

	public void setTest(Object test) {
		this.test = test;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	public void setPerson(Person p) {
	}
	
	
	
	/**
	 * 
	 */
	public Person() {
		// TODO Auto-generated constructor stub
	}

	public Person(String name, int age) {
		super();
		this.name = name;
		this.age = age;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\"name\":\"");
		builder.append(name);
		builder.append("\", \"age\":\"");
		builder.append(age);
		builder.append("\"}");
		return builder.toString();
	}
}
